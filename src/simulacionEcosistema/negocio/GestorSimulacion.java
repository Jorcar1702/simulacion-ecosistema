package simulacionEcosistema.negocio;

import simulacionEcosistema.modelo.*;

public class GestorSimulacion {
    private Simulacion simulacionActual;
    private Estudiante estudianteJugando;
    private int regeneracionVegetal;

    public GestorSimulacion(Simulacion simulacion, Estudiante estudiante) {
        this.simulacionActual = simulacion;
        this.estudianteJugando = estudiante;
    }

    public void registrarPoblacion(Poblacion p) {
        if (p != null) {
            simulacionActual.getPoblaciones().add(p);
        }
    }

    public void registrarInteraccion(Interaccion i) {
        if (i != null) {
            simulacionActual.getInteracciones().add(i);
        }
    }

    public void iniciar() throws Exception {
        if (simulacionActual.getPoblaciones().isEmpty()) {
            throw new Exception("No se puede iniciar: El ecosistema no tiene poblaciones.");
        }
        simulacionActual.setActiva(true);
        simulacionActual.setTurnoActual(0);
        simulacionActual.setEstadoInicial(simulacionActual.toString());

        System.out.println(simulacionActual.getDuracionTurnoDescripcion());
        if (estudianteJugando != null) {
            System.out.println("[Gestor] Simulación en marcha. Piloto: " + estudianteJugando.getNombreCompleto());
        } else {
            System.out.println("[Gestor] Simulación en marcha. (Modo profesor)");
        }
    }

    public void ejecutarTurno() throws Exception {
        if (!simulacionActual.isActiva()) {
            throw new Exception("La simulación ha terminado o no ha sido inicializada.");
        }

        int turno = simulacionActual.getTurnoActual() + 1;
        simulacionActual.setTurnoActual(turno);

        System.out.println("\n=============================================");
        System.out.println("EJECUTANDO TURNO: " + turno + " / " + simulacionActual.getTiempoTotal());
        System.out.println("=============================================");

        for (Poblacion p : simulacionActual.getPoblaciones()) {
            p.actualizarPoblacion(); // Lógica interna de natalidad/mortalidad nativa
        }

        for (Interaccion inter : simulacionActual.getInteracciones()) {
            Poblacion depr = buscarPoblacionEnSimulacion(inter.getNombreDepredador());
            Poblacion pres = buscarPoblacionEnSimulacion(inter.getNombrePresa());

            if (depr != null && pres != null) {
                if (pres.getCantidad() <= 0) {
                    // No hay presas: el depredador empieza a morir de hambre en este y los próximos turnos.
                    inter.aplicarHambrunaPorFaltaDePresa(depr);
                } else {
                    int cazadas = inter.simularCaza(pres, depr);
                    inter.transferirEnergia(depr, cazadas);
                }
            }
        }

        Entorno entorno = simulacionActual.getEntorno();
        if (entorno != null) {
            entorno.actualizarRecursos(simulacionActual.getPoblaciones());
            entorno.mostrarInformacion();
        }

        simulacionActual.registrarEventoTurno(construirResumenTurno(turno));

        if (turno >= simulacionActual.getTiempoTotal()) {
            evaluarFinalizacionPartida();
        }
    }

    /**
     * Ejecuta todos los turnos restantes de la simulación de una sola vez,
     * uno por uno, hasta que termine (por tiempo agotado o extinción total).
     */
    public void ejecutarTodosLosTurnos() throws Exception {
        if (!simulacionActual.isActiva()) {
            throw new Exception("La simulación ha terminado o no ha sido inicializada.");
        }
        while (simulacionActual.isActiva()) {
            ejecutarTurno();
        }
        System.out.println("\n[Gestor] Se ejecutaron todos los turnos restantes de la simulación.");
    }

    private String construirResumenTurno(int turno) {
        String resultado = "Turno " + turno + "/" + simulacionActual.getTiempoTotal() +
                " (" + java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "): ";
        for (Poblacion p : simulacionActual.getPoblaciones()) {
            resultado += p.getEspecie().getNombre() + "=" + p.getCantidad() + "  ";
        }
        if (simulacionActual.getEntorno() != null) {
            resultado += "| Alimento disponible=" + simulacionActual.getEntorno().getAlimentoDisponible();
        }
        return resultado;
    }

    // Evalúa si el ecosistema sobrevivió para otorgar la recompensa
    private void evaluarFinalizacionPartida() {
        simulacionActual.setActiva(false);
        boolean ecosistemaEstable = false;

        // Si al menos una población sobrevivió con individuos, se considera éxito
        for (Poblacion p : simulacionActual.getPoblaciones()) {
            if (p.getCantidad() > 0) {
                ecosistemaEstable = true;
                break;
            }
        }

        simulacionActual.setEstadoFinal(simulacionActual.toString());

        System.out.println("\n=============================================");
        if (ecosistemaEstable) {
            System.out.println("¡VICTORIA! Lograste mantener el equilibrio del bioma.");
            simulacionActual.setResultado("VICTORIA");

            if (estudianteJugando != null) {
                estudianteJugando.registrarSimulacionExitosa();
                System.out.println("Logro guardado en tu perfil institucional.");
            }
        } else {
            System.out.println("COLAPSO ECOLÓGICO: Todas las especies se han extinguido.");
            simulacionActual.setResultado("COLAPSO");
        }
        System.out.println("=============================================");

        // Se guarda automáticamente en el historial del estudiante, sin necesidad
        // de que use la opción manual de "Guardar simulación".
        if (estudianteJugando != null) {
            estudianteJugando.registrarSimulacion(simulacionActual);
            // Evaluar posibles logros tras guardar la simulación
            GestorLogros.evaluarLogros(estudianteJugando, simulacionActual);
        }
    }

    private Poblacion buscarPoblacionEnSimulacion(String nombreEspecie) {
        if (nombreEspecie == null) return null;
        for (Poblacion p : simulacionActual.getPoblaciones()) {
            if (p.getEspecie().getNombre().equalsIgnoreCase(nombreEspecie.trim())) {
                return p;
            }
        }
        return null;
    }

    public void terminarManualmente() {
        simulacionActual.setActiva(false);
        System.out.println("Simulación finalizada por el usuario.");
    }

    public Simulacion getSimulacion() {
        return simulacionActual; //
    }

    public boolean haySimulacionActiva() {
        return simulacionActual != null && simulacionActual.isActiva();
    }

    /**
     * Construye un reporte completo en texto: estado inicial, lo ocurrido en cada
     * turno (bitácora) y el estado final. Sirve tanto para mostrarlo en consola
     * como para exportarlo a un archivo.
     */
    public String generarReporteTexto() {
        String reporte = "";
        Simulacion s = simulacionActual;

        reporte += "==================== REPORTE DE SIMULACIÓN ====================\n";
        reporte += "Fecha de la simulación: " + s.getFechaFormateada() + "\n";
        reporte += s.getDuracionTurnoDescripcion() + "\n";
        reporte += "Turnos ejecutados: " + s.getTurnoActual() + " / " + s.getTiempoTotal() + "\n";
        reporte += "Estado de la simulación: " + (s.isActiva() ? "En curso" : "Finalizada") + "\n";
        reporte += "Resultado: " + s.getResultado() + "\n";
        if (estudianteJugando != null) {
            reporte += "Estudiante: " + estudianteJugando.getNombreCompleto() +
                    " (Paralelo " + estudianteJugando.getParalelo() + ")\n";
        }

        reporte += "\n--- ESTADO INICIAL ---\n";
        reporte += (s.getEstadoInicial() != null ? s.getEstadoInicial() : "No disponible (aún no se ha iniciado).");
        reporte += "\n";

        reporte += "\n--- EVENTOS POR TURNO ---\n";
        if (s.getBitacoraTurnos().isEmpty()) {
            reporte += "Todavía no se ha ejecutado ningún turno.\n";
        } else {
            for (String evento : s.getBitacoraTurnos()) {
                reporte += evento + "\n";
            }
        }

        reporte += "\n--- ESTADO FINAL ---\n";
        reporte += (s.getEstadoFinal() != null ? s.getEstadoFinal() : s.toString());
        reporte += "\n=================================================================\n";

        return reporte;
    }
    public void setRegeneracionVegetal(int tasa) throws Exception {
        if (simulacionActual != null && simulacionActual.getEntorno() != null) {
            simulacionActual.getEntorno().setRegeneracionVegetal(tasa);
        }
    }
}

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

    public void iniciar() {
        if (simulacionActual.getPoblaciones().isEmpty()) {
            System.out.println("No se puede iniciar: El ecosistema no tiene poblaciones.");
            return;
        }
        simulacionActual.setActiva(true);
        simulacionActual.setTurnoActual(0);

        if (estudianteJugando != null) {
            System.out.println("[Gestor] Simulación en marcha. Piloto: " + estudianteJugando.getNombreCompleto());
        } else {
            System.out.println("[Gestor] Simulación en marcha. (Modo profesor)");
        }
    }

    public void ejecutarTurno() {
        if (!simulacionActual.isActiva()) {
            System.out.println("La simulación ha terminado o no ha sido inicializada.");
            return;
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
                int cazadas = inter.simularCaza(pres, depr);
                inter.transferirEnergia(depr, cazadas);
            }
        }

        Entorno entorno = simulacionActual.getEntorno();
        if (entorno != null) {
            entorno.actualizarRecursos(simulacionActual.getPoblaciones());
            entorno.mostrarInformacion();
        }

        if (turno >= simulacionActual.getTiempoTotal()) {
            evaluarFinalizacionPartida();
        }
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

        System.out.println("\n=============================================");
        if (ecosistemaEstable) {
            System.out.println("¡VICTORIA! Lograste mantener el equilibrio del bioma.");

            estudianteJugando.registrarSimulacionExitosa();
            System.out.println("Logro guardado en tu perfil institucional.");
        } else {
            System.out.println("COLAPSO ECOLÓGICO: Todas las especies se han extinguido.");
        }
        System.out.println("=============================================");
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
    public void setRegeneracionVegetal(int tasa) {
        if (simulacionActual != null && simulacionActual.getEntorno() != null) {
            simulacionActual.getEntorno().setRegeneracionVegetal(tasa);
        }
    }
}

package simulacionEcosistema.negocio;

import simulacionEcosistema.modelo.*;
import java.util.List;

public class GestorLogros {

    public static void evaluarLogros(Estudiante estudiante, Simulacion simulacion) {
        if (estudiante == null || simulacion == null) return;

        // Ejemplo de reglas de desbloqueo
        // 1) Primera simulación exitosa
        if (estudiante.getSimulacionesExitosas() == 1) {
            if (!tieneLogroTipo(estudiante.getLogros(), "PRIMERA_SIMULACION")) {
                Logro l = new Logro("Primera Victoria",
                        "Completaste tu primera simulación exitosa sin colapso ecológico",
                        "PRIMERA_SIMULACION");
                otorgarLogro(estudiante, l);
            }
        }

        // 2) Cinco simulaciones exitosas
        if (estudiante.getSimulacionesExitosas() == 5) {
            if (!tieneLogroTipo(estudiante.getLogros(), "CINCO_EXITOSAS")) {
                Logro l = new Logro("Experto del Ecosistema",
                        "Has completado 5 simulaciones exitosas",
                        "CINCO_EXITOSAS");
                otorgarLogro(estudiante, l);
            }
        }

        // 3) Mantener 3 o más especies vivas al final
        if (mantiene3OmasPoblacionesVivas(simulacion)) {
            if (!tieneLogroTipo(estudiante.getLogros(), "TRES_ESPECIES_VIVAS")) {
                Logro l = new Logro("Biodiversidad Protegida",
                        "Mantuviste 3 o más especies vivas durante la simulación",
                        "TRES_ESPECIES_VIVAS");
                otorgarLogro(estudiante, l);
            }
        }

                // 6) Completar una simulación sin extinciones (todas las poblaciones > 0 al final)
                if (mantenerTodasPoblacionesVivas(simulacion)) {
                    if (!tieneLogroTipo(estudiante.getLogros(), "TODAS_VIVAS")) {
                        Logro l = new Logro("Simulación Sin Extinciones",
                                "Completaste una simulación sin que ninguna población llegara a 0",
                                "TODAS_VIVAS");
                        otorgarLogro(estudiante, l);
                    }
                }

                // 7) Mantener todas las poblaciones vivas (sin extinción parcial)
                // (Esta regla es similar a la anterior; se mantiene por claridad de nombres)
                if (mantenerTodasPoblacionesVivas(simulacion)) {
                    if (!tieneLogroTipo(estudiante.getLogros(), "MANTENER_TODAS_VIVAS")) {
                        Logro l = new Logro("Guardían de la Vida",
                                "Mantener todas las poblaciones vivas al finalizar la simulación",
                                "MANTENER_TODAS_VIVAS");
                        otorgarLogro(estudiante, l);
                    }
                }

                // 8) Alta biodiversidad: 5 o más poblaciones vivas al final
                if (contarPoblacionesVivas(simulacion) >= 5) {
                    if (!tieneLogroTipo(estudiante.getLogros(), "ALTA_BIODIVERSIDAD")) {
                        Logro l = new Logro("Alta Biodiversidad",
                                "Completaste una simulación con alta biodiversidad (>=5 poblaciones vivas)",
                                "ALTA_BIODIVERSIDAD");
                        otorgarLogro(estudiante, l);
                    }
                }

                // 9) Completar 20 simulaciones (historial)
                if (estudiante.getHistorialSimulaciones().size() == 20) {
                    if (!tieneLogroTipo(estudiante.getLogros(), "VEINTE_SIMULACIONES")) {
                        Logro l = new Logro("Veterano de Campo",
                                "Has ejecutado 20 simulaciones",
                                "VEINTE_SIMULACIONES");
                        otorgarLogro(estudiante, l);
                    }
                }

        // 4) Diez simulaciones ejecutadas (historial)
        if (estudiante.getHistorialSimulaciones().size() == 10) {
            if (!tieneLogroTipo(estudiante.getLogros(), "DIEZ_SIMULACIONES")) {
                Logro l = new Logro("Investigador Persistente",
                        "Has ejecutado 10 simulaciones (exitosas y fallidas)",
                        "DIEZ_SIMULACIONES");
                otorgarLogro(estudiante, l);
            }
        }

        // 5) Rango máximo alcanzado
        if ("Maestro de la Biósfera".equals(estudiante.obtenerRecompensa())) {
            if (!tieneLogroTipo(estudiante.getLogros(), "MAESTRO_BIOSFERA")) {
                Logro l = new Logro("Maestro de la Biósfera",
                        "Alcanzaste el rango máximo del sistema de recompensas",
                        "MAESTRO_BIOSFERA");
                otorgarLogro(estudiante, l);
            }
        }
    }

    private static boolean mantiene3OmasPoblacionesVivas(Simulacion simulacion) {
        int vivas = 0;
        for (Poblacion p : simulacion.getPoblaciones()) {
            if (p.getCantidad() > 0) vivas++;
        }
        return vivas >= 3;
    }

    private static boolean mantenerTodasPoblacionesVivas(Simulacion simulacion) {
        for (Poblacion p : simulacion.getPoblaciones()) {
            if (p.getCantidad() <= 0) return false;
        }
        return true;
    }

    private static int contarPoblacionesVivas(Simulacion simulacion) {
        int contador = 0;
        for (Poblacion p : simulacion.getPoblaciones()) {
            if (p.getCantidad() > 0) contador++;
        }
        return contador;
    }

    private static boolean tieneLogroTipo(List<Logro> lista, String tipo) {
        if (lista == null) return false;
        for (Logro l : lista) {
            if (l.getTipo().equals(tipo)) return true;
        }
        return false;
    }

    private static void otorgarLogro(Estudiante estudiante, Logro logro) {
        estudiante.agregarLogro(logro);
        System.out.println("\n🏆 ¡LOGRO DESBLOQUEADO! " + logro.getNombre());
        System.out.println("   " + logro.getDescripcion());
    }

    public static String obtenerLogrosPorEstudiante(Estudiante estudiante) {
        if (estudiante == null) return "Estudiante no válido.";
        String resultado = "\n=== LOGROS DE " + estudiante.getNombreCompleto().toUpperCase() + " ===\n";
        resultado += "Total de logros desbloqueados: " + estudiante.getLogros().size() + "\n";
        resultado += "Rango actual: " + estudiante.obtenerRecompensa() + "\n";
        resultado += "Simulaciones exitosas: " + estudiante.getSimulacionesExitosas() + "\n\n";

        if (estudiante.getLogros().isEmpty()) {
            resultado += "Aún no has desbloqueado logros. ¡Completa simulaciones exitosas!\n";
        } else {
            resultado += "--- LOGROS DESBLOQUEADOS ---\n";
            for (Logro l : estudiante.getLogros()) {
                resultado += l.toString() + "\n";
            }
        }
        return resultado;
    }
}




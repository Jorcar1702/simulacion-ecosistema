package simulacionEcosistema.modelo;

import java.util.ArrayList;
import java.util.List;

public class Estudiante extends Usuario {
    private String paralelo; // A, B o C
    private List<Simulacion> historialSimulaciones;
    private int simulacionesExitosas; //

    public Estudiante(String nombreCompleto, String cedula, String correo,
                      String nombreUsuario, String contrasena, String paralelo) {
        super(nombreCompleto, cedula, correo, nombreUsuario, contrasena);
        setParalelo(paralelo);
        this.historialSimulaciones = new ArrayList<>();
        this.simulacionesExitosas = 0;
    }

    public List<Simulacion> getHistorialSimulaciones() {
        return historialSimulaciones;
    }

    public void setParalelo(String paralelo) {
        if (paralelo != null && (paralelo.equalsIgnoreCase("A") ||
                paralelo.equalsIgnoreCase("B") ||
                paralelo.equalsIgnoreCase("C"))) {
            this.paralelo = paralelo.toUpperCase().trim();
        } else {
            this.paralelo = "A";
        }
    }

    public void registrarSimulacionExitosa() {
        this.simulacionesExitosas++;
        System.out.println("\n ¡Felicidades! Has estabilizado el ecosistema. +1 Simulación Exitosa.");
    }

    public int getSimulacionesExitosas() {
        return simulacionesExitosas;
    }

    public String obtenerRecompensa() {
        if (this.simulacionesExitosas == 0) {
            return "Investigador Novato";
        } else if (this.simulacionesExitosas <= 2) {
            return "Protector del Entorno";
        } else if (this.simulacionesExitosas <= 5) {
            return "Guardián del Ecosistema";
        } else {
            return "Maestro de la Biósfera";
        }
    }
    public void registrarSimulacion(Simulacion simulacion) {
        if (simulacion != null) {
            historialSimulaciones.add(simulacion);
        }
    }

    public String getParalelo() { return paralelo; }

    @Override
    public String toString() {
        return "=== DATOS DEL ESTUDIANTE ===\n" +
                super.toString() + "\n" +
                "Paralelo Asignado: " + paralelo + "\n" +
                "Simulaciones Exitosas: " + simulacionesExitosas + "\n" +
                "Recompensa / Rango: " + obtenerRecompensa() + "\n";

    }
}

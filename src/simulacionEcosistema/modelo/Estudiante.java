package simulacionEcosistema.modelo;

import java.util.ArrayList;
import java.util.List;
import simulacionEcosistema.modelo.Logro;

public class Estudiante extends Usuario {
    private String paralelo; // A, B o C
    private List<Simulacion> historialSimulaciones;
    private int simulacionesExitosas; //
    private List<Logro> logros;
    private Simulacion simulacionEnCurso;

    public Estudiante(String nombreCompleto, String cedula, String correo,
                      String nombreUsuario, String contrasena, String paralelo)throws Exception {
        super(nombreCompleto, cedula, correo, nombreUsuario, contrasena);
        setParalelo(paralelo);
        this.historialSimulaciones = new ArrayList<>();
        this.simulacionesExitosas = 0;
        this.logros = new ArrayList<>();
    }

    public List<Simulacion> getHistorialSimulaciones() {
        return historialSimulaciones;
    }

    public void setParalelo(String paralelo) throws Exception {
        if (paralelo == null || paralelo.isBlank()) {
            throw new Exception("Paralelo no puede ser nulo.");
        }

        paralelo = paralelo.toUpperCase().trim();

        if (!paralelo.equals("A") &&
                !paralelo.equals("B") &&
                !paralelo.equals("C")) {
            throw new Exception("Paralelo inválido. Solo A, B o C.");
        }

        this.paralelo = paralelo;
    }

    public void registrarSimulacionExitosa() {
        this.simulacionesExitosas++;
        System.out.println("\n ¡Felicidades! Has estabilizado el ecosistema. +1 Simulación Exitosa.");
    }

    public int getSimulacionesExitosas() {
        return simulacionesExitosas;
    }

    public List<Logro> getLogros() { return logros; }

    public void agregarLogro(Logro logro) {
        if (logro != null && !logros.contains(logro)) {
            logros.add(logro);
        }
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
        if (simulacion != null && !historialSimulaciones.contains(simulacion)) {
            historialSimulaciones.add(simulacion);
        }
    }


    public Simulacion getSimulacionEnCurso() {
        return simulacionEnCurso;
    }

    public void setSimulacionEnCurso(Simulacion simulacionEnCurso) {
        this.simulacionEnCurso = simulacionEnCurso;
    }


    public int simulacionesParaSiguienteRango() {
        if (simulacionesExitosas == 0) {
            return 1 - simulacionesExitosas;
        } else if (simulacionesExitosas <= 2) {
            return 3 - simulacionesExitosas;
        } else if (simulacionesExitosas <= 5) {
            return 6 - simulacionesExitosas;
        } else {
            return 0;
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

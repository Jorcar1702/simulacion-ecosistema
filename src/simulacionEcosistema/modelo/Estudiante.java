package simulacionEcosistema.modelo;

public class Estudiante extends Usuario {
    private String paralelo; // A, B o C
    private int simulacionesExitosas;

    public Estudiante(String nombreCompleto, String cedula, String correo,
                      String nombreUsuario, String contrasena, String paralelo) {
        super(nombreCompleto, cedula, correo, nombreUsuario, contrasena);
        setParalelo(paralelo);
        this.simulacionesExitosas = 0;
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

    // Recompensa

    public void registrarSimulacionExitosa() {
        this.simulacionesExitosas++;
        System.out.println("\n ¡Felicidades! Has estabilizado el ecosistema. +1 Simulación Exitosa.");
    }

    public String obtenerRecompensa() {
        if (this.simulacionesExitosas == 0) {
            return " Investigador Novato";
        } else if (this.simulacionesExitosas >= 1 && this.simulacionesExitosas <= 2) {
            return "Protector del Entorno";
        } else if (this.simulacionesExitosas >= 3 && this.simulacionesExitosas <= 5) {
            return "Guardián del Ecosistema";
        } else {
            return " Maestro de la Biósfera";
        }
    }

    public String getParalelo() { return paralelo; }
    public int getSimulacionesExitosas() { return simulacionesExitosas; }


    @Override
    public String toString() {
        // Usamos super.toString() para traer los datos validados de Usuario
        return "=== DATOS DEL ESTUDIANTE ===\n" +
                super.toString() + "\n" +
                "Paralelo Asignado: " + paralelo + "\n" +
                "Simulaciones Exitosas: " + simulacionesExitosas + "\n" +
                "Recompensa / Rango: " + obtenerRecompensa() + "\n" +
                "============================";
    }
}
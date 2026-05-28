package simulacionEcosistema.modelo;


public class Administrador extends Usuario {


        public Administrador(String nombreCompleto, String cedula, String correo,
                             String nombreUsuario, String contrasena, String facultad) {
            // Envia los datos a la clase padre Usuario para que pasen por los filtros de validación
            super(nombreCompleto, cedula, correo, nombreUsuario, contrasena);

        }

        @Override
        public String toString() {
            // Reutilizamos el toString() de la clase padre Usuario y le añadimos los datos del Admin
            return "=== DATOS DEL ADMINISTRADOR / DOCENTE ===\n" +
                    super.toString() + "\n" +super.toString();

        }
    }


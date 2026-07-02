package simulacionEcosistema.modelo;

public class Administrador extends Usuario {

    public Administrador(String nombreCompleto, String cedula, String correo,
                         String nombreUsuario, String contrasena) throws Exception {

        super(nombreCompleto, cedula, correo, nombreUsuario, contrasena);
    }

    @Override
    public String toString() {
        return "=== DATOS DEL ADMINISTRADOR ===\n" +
                super.toString();
    }
}
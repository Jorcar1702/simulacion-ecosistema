package simulacionEcosistema.modelo;

public abstract class Usuario {
    private String nombreCompleto;
    private String cedula;
    private String correo;
    private String nombreUsuario;
    private String contrasena;
    private String estado;

    public Usuario(String nombreCompleto, String cedula, String correo,
                   String nombreUsuario, String contrasena) throws Exception {
        if (!esNombreValido(nombreCompleto)) {
            throw new Exception("Nombre inválido: solo letras y espacios.");
        }
        if (!esCedulaValida(cedula)) {
            throw new Exception("Cédula inválida.");
        }
        if (!esNombreUsuarioValido(nombreUsuario)) {
            throw new Exception("Nombre de usuario inválido: no puede estar vacío ni tener espacios.");
        }
        this.nombreCompleto = nombreCompleto;
        this.cedula = cedula;
        setCorreo(correo);
        this.nombreUsuario = nombreUsuario;
        setContrasena(contrasena);
        this.estado = "ACTIVO";
    }

    public static boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.isBlank()) return false;
        for (int i = 0; i < nombre.length(); i++) {
            char c = nombre.charAt(i);
            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ' ||
                    "áéíóúÁÉÍÓÚñÑ".indexOf(c) >= 0)) {
                return false;
            }
        }
        return true;
    }

    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || cedula.length() != 10 || !cedula.chars().allMatch(Character::isDigit)) {
            return false;
        }
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercerDigito = cedula.charAt(2) - '0';
        if (provincia < 1 || provincia > 24 || tercerDigito >= 6) return false;

        int[] coef = {2,1,2,1,2,1,2,1,2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int val = (cedula.charAt(i) - '0') * coef[i];
            if (val > 9) val -= 9;
            suma += val;
        }
        int verificador = (10 - (suma % 10)) % 10;
        return verificador == (cedula.charAt(9) - '0');
    }

    public static boolean esCorreoValido(String correo) {
        if (correo == null || correo.isBlank()) return false;
        int posArroba = correo.indexOf('@');
        int posPunto = correo.lastIndexOf('.');
        return posArroba > 0 && posPunto > posArroba + 1 && posPunto < correo.length() - 1;
    }

    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && !contrasena.isBlank() && contrasena.length() >= 4;
    }

    public static boolean esNombreUsuarioValido(String nombreUsuario) {
        return nombreUsuario != null && !nombreUsuario.isBlank() && !nombreUsuario.contains(" ");
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getCedula() { return cedula; }
    public String getCorreo() { return correo; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getContrasena() { return contrasena; }
    public String getEstado() { return estado; }

    public void setCorreo(String correo) throws Exception {
        if (!esCorreoValido(correo)) {
            throw new Exception("Correo inválido: formato esperado usuario@dominio.com");
        }
        this.correo = correo;
    }

    public void setContrasena(String contrasena) throws Exception {
        if (!esContrasenaValida(contrasena)) {
            throw new Exception("Contraseña inválida: debe tener al menos 4 caracteres.");
        }
        this.contrasena = contrasena;
    }

    public void desactivar() { this.estado = "INACTIVO"; }
    public void activar() { this.estado = "ACTIVO"; }
    public boolean estaActivo() { return "ACTIVO".equalsIgnoreCase(estado); }

    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            return false;
        }
        return this.nombreUsuario.equals(nombreUsuario) && this.contrasena.equals(contrasena);
    }

    @Override
    public String toString() {
        return "Cédula: " + cedula + "\n" +
                "Nombre Completo: " + nombreCompleto + "\n" +
                "Correo Electrónico: " + correo + "\n" +
                "Nombre de Usuario: " + nombreUsuario + "\n" +
                "Estado: " + estado;
    }
}
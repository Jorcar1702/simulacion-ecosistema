package simulacionEcosistema.modelo;

public class Usuario {
    private String nombreCompleto;
    private String cedula;
    private String correo;
    private String nombreUsuario;
    private String contrasena;
    private String estado;

    public Usuario(String nombreCompleto, String cedula, String correo, String nombreUsuario, String contrasena){
        setNombreCompleto(nombreCompleto);
        setCedula(cedula);
        setCorreo(correo);
        setNombreUsuario(nombreUsuario);
        setContrasena(contrasena);
        this.estado = "ACTIVO";
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        boolean valido = true;
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            valido = false;
        } else {
            for (int i = 0; i < nombreCompleto.length(); i++) {
                char c = nombreCompleto.charAt(i);
                if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ' ||
                        c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' ||
                        c == 'Á' || c == 'É' || c == 'Í' || c == 'Ó' || c == 'Ú' ||
                        c == 'ñ' || c == 'Ñ')) {
                    valido = false;
                    break;
                }
            }
        }
        if (valido) {
            this.nombreCompleto = nombreCompleto;
        } else {
            this.nombreCompleto = "Nombre inválido";
        }
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        boolean valido = true;
        if (cedula == null || cedula.length() != 10) {
            valido = false;
        } else {
            for (int i = 0; i < cedula.length(); i++) {
                char c = cedula.charAt(i);
                if (!(c >= '0' && c <= '9')) {
                    valido = false;
                    break;
                }
            }
        }
        if (valido) {
            this.cedula = cedula;
        } else {
            this.cedula = "0000000000";
        }

    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo != null && !correo.isBlank() && correo.contains("@") && correo.contains(".")) {
            this.correo = correo;
        } else {
            this.correo = "pendiente@udla.edu.ec";
        }
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario != null && !nombreUsuario.isBlank() && !nombreUsuario.contains(" ")) {
            this.nombreUsuario = nombreUsuario;
        } else {
            this.nombreUsuario = "user_" + this.cedula;
        }
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        if (contrasena != null && contrasena.length() >= 4) {
            this.contrasena = contrasena;
        } else {
            this.contrasena = "1234";
        }
    }

    public String getEstado() {
        return estado;
    }
    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            return false;
        }
        return this.nombreUsuario.equals(nombreUsuario) && this.contrasena.equals(contrasena);
    }
    public boolean estaActivo() {
        return this.estado != null && this.estado.equalsIgnoreCase("ACTIVO");
    }
    @Override
    public String toString() {
        return "Cédula: " + cedula + "\n" +
                "Nombre Completo: " + nombreCompleto + "\n" +
                "Correo Electrónico: " + correo + "\n" +
                "Nombre de Usuario: " + nombreUsuario + "\n" +
                "Estado de Cuenta: " + estado;
    }
}

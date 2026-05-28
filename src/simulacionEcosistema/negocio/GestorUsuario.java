package simulacionEcosistema.negocio;

import simulacionEcosistema.modelo.Usuario;
import simulacionEcosistema.modelo.Estudiante;
import java.util.ArrayList;
import java.util.List;

public class GestorUsuario{
    private List<Usuario> listaUsuarios;

    public GestorUsuario() {
        this.listaUsuarios = new ArrayList<>();
    }

    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }


    // Validación de ciberseguridad para evitar credenciales duplicadas
    private boolean existeCedula(String cedula) {
        if (cedula == null) return false;
        for (Usuario u : listaUsuarios) {
            if (u.getCedula().equals(cedula.trim())) return true;
        }
        return false;
    }

    private boolean existeNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null) return false;
        for (Usuario u : listaUsuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario.trim())) return true;
        }
        return false;
    }

    // Registro seguro de usuarios
    public boolean registrarUsuario(Usuario nuevoUsuario) {
        if (nuevoUsuario == null) return false;

        if (existeCedula(nuevoUsuario.getCedula())) {
            System.out.println("Error: La cédula ya se encuentra registrada.");
            return false;
        }
        if (existeNombreUsuario(nuevoUsuario.getNombreUsuario())) {
            System.out.println("Error: El nombre de usuario ya está en uso.");
            return false;
        }

        listaUsuarios.add(nuevoUsuario);
        return true;
    }

    // Autenticación que recorre la lista polimórfica
    public Usuario buscarUsuarioParaLogin(String username, String password) {
        if (username == null || password == null) return null;

        for (Usuario u : listaUsuarios) {
            if (u.estaActivo() && u.iniciarSesion(username.trim(), password)) {
                return u; // Retorna el objeto (puede ser Estudiante o Administrador)
            }
        }
        return null;
    }

    // Reporte exclusivo para el Profesor usando StringBuilder
    public String obtenerAlumnosPorParalelo(String paraleloBuscado) {
        if (paraleloBuscado == null || paraleloBuscado.isBlank()) {
            return "Error: Paralelo no válido.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n LISTA DE ESTUDIANTES - PARALELO ").append(paraleloBuscado.toUpperCase()).append("\n");
        sb.append("----------------------------------------------------------------\n");

        boolean encontrado = false;
        for (Usuario u : listaUsuarios) {
            if (u instanceof Estudiante) {
                Estudiante est = (Estudiante) u; // Downcasting seguro
                if (est.getParalelo().equalsIgnoreCase(paraleloBuscado.trim())) {
                    sb.append("- ").append(est.getNombreCompleto())
                            .append(" | Logros/Rango: ").append(est.obtenerRecompensa()).append("\n");
                    encontrado = true;
                }
            }
        }

        if (!encontrado) {
            sb.append("No hay estudiantes registrados en este paralelo.\n");
        }
        return sb.toString();
    }
}
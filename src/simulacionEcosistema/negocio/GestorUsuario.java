package simulacionEcosistema.negocio;

import simulacionEcosistema.modelo.Usuario;
import simulacionEcosistema.modelo.Estudiante;
import simulacionEcosistema.modelo.Simulacion;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorUsuario {
    private List<Usuario> listaUsuarios;

    public GestorUsuario() {
        this.listaUsuarios = new ArrayList<>();
    }

    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    // Validación de ciberseguridad
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

    // Registro seguro
    public boolean registrarUsuario(Usuario nuevoUsuario) throws Exception {
        if (nuevoUsuario == null) return false;

        if (existeCedula(nuevoUsuario.getCedula())) {
            throw new Exception("Error: La cédula ya se encuentra registrada.");
        }
        if (existeNombreUsuario(nuevoUsuario.getNombreUsuario())) {
            throw new Exception("Error: El nombre de usuario ya está en uso.");
        }

        listaUsuarios.add(nuevoUsuario);
        return true;
    }

    // Login
    public Usuario buscarUsuarioParaLogin(String username, String password) {
        if (username == null || password == null) return null;

        for (Usuario u : listaUsuarios) {
            if (u.estaActivo() && u.iniciarSesion(username.trim(), password)) {
                return u;
            }
        }
        return null;
    }

    // Reporte por paralelo
    public String obtenerAlumnosPorParalelo(String paraleloBuscado) {
        if (paraleloBuscado == null || paraleloBuscado.isBlank()) {
            return "Error: Paralelo no válido.";
        }

        String resultado = "\n LISTA DE ESTUDIANTES - PARALELO " + paraleloBuscado.toUpperCase() + "\n";
        resultado += "----------------------------------------------------------------\n";

        boolean encontrado = false;
        for (Usuario u : listaUsuarios) {
            if (u instanceof Estudiante est) {
                if (est.getParalelo().equalsIgnoreCase(paraleloBuscado.trim())) {
                    resultado += "- " + est.getNombreCompleto() +
                            " | Logros/Rango: " + est.obtenerRecompensa() + "\n";
                    encontrado = true;
                }
            }
        }

        if (!encontrado) {
            resultado += "No hay estudiantes registrados en este paralelo.\n";
        }
        return resultado;
    }

    // 🔹 Métodos nuevos
    public Usuario buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) return null;
        for (Usuario u : listaUsuarios) {
            if (u.getCedula().equalsIgnoreCase(cedula.trim())) {
                return u;
            }
        }
        return null;
    }

    public String darDeBaja(String cedula) {
        Usuario u = buscarPorCedula(cedula);
        if (u != null) {
            listaUsuarios.remove(u);
            return "Usuario con cédula " + cedula + " dado de baja correctamente.";
        }
        return "No se encontró usuario con esa cédula.";
    }
    public void registrarSimulacionEnHistorial(Estudiante estudiante, Simulacion simulacion) {
        if (estudiante != null && simulacion != null) {
            estudiante.registrarSimulacion(simulacion);
            System.out.println("Simulación registrada en el historial de " + estudiante.getNombreCompleto());
        }
    }

    // 🔹 Ver simulaciones hechas por estudiantes filtrando por fecha
    public String obtenerSimulacionesPorFecha(LocalDate fecha) {
        if (fecha == null) return "Fecha inválida.";

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        String resultado = "\n=== SIMULACIONES REALIZADAS EL " +
                fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ===\n";

        boolean encontrado = false;
        for (Usuario u : listaUsuarios) {
            if (u instanceof Estudiante est) {
                for (Simulacion sim : est.getHistorialSimulaciones()) {
                    if (sim.getFechaCreacion() != null && sim.getFechaCreacion().toLocalDate().equals(fecha)) {
                        resultado += "- " + est.getNombreCompleto() +
                                " (Paralelo " + est.getParalelo() + ")" +
                                " | Hora: " + sim.getFechaCreacion().toLocalTime().format(formatoHora) +
                                " | Turnos: " + sim.getTurnoActual() + "/" + sim.getTiempoTotal() +
                                " | Resultado: " + sim.getResultado() +
                                "\n";
                        encontrado = true;
                    }
                }
            }
        }

        if (!encontrado) {
            resultado += "No se encontraron simulaciones registradas en esa fecha.\n";
        }
        return resultado;
    }

    // 🔹 Ver estudiantes agrupados por rango/logro alcanzado
    public String obtenerEstudiantesPorRango(String rango) {
        if (rango == null || rango.isBlank()) return "Rango no válido.";

        String resultado = "\n=== ESTUDIANTES CON RANGO: " + rango + " ===\n";
        boolean encontrado = false;
        for (Usuario u : listaUsuarios) {
            if (u instanceof Estudiante est && est.obtenerRecompensa().equalsIgnoreCase(rango.trim())) {
                resultado += "- " + est.getNombreCompleto() +
                        " | Paralelo: " + est.getParalelo() +
                        " | Simulaciones exitosas: " + est.getSimulacionesExitosas() +
                        "\n";
                encontrado = true;
            }
        }
        if (!encontrado) {
            resultado += "No hay estudiantes con ese rango todavía.\n";
        }
        return resultado;
    }

    // 🔹 Búsqueda de usuarios por nombre (coincidencia parcial)
    public String buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return "Nombre inválido.";

        String resultado = "\n=== RESULTADOS PARA \"" + nombre + "\" ===\n";
        boolean encontrado = false;
        for (Usuario u : listaUsuarios) {
            if (u.getNombreCompleto().toLowerCase().contains(nombre.trim().toLowerCase())) {
                resultado += "- " + u.getNombreCompleto() +
                        " | Cédula: " + u.getCedula() +
                        " | Tipo: " + (u instanceof Estudiante ? "Estudiante" : "Administrador") +
                        "\n";
                encontrado = true;
            }
        }
        if (!encontrado) {
            resultado += "No se encontraron usuarios con ese nombre.\n";
        }
        return resultado;
    }

    // 🔹 Estadísticas generales del sistema (para el panel del administrador)
    public String obtenerEstadisticasGenerales() {
        int totalEstudiantes = 0;
        int totalSimulacionesExitosas = 0;
        int totalSimulacionesRegistradas = 0;

        for (Usuario u : listaUsuarios) {
            if (u instanceof Estudiante est) {
                totalEstudiantes++;
                totalSimulacionesExitosas += est.getSimulacionesExitosas();
                totalSimulacionesRegistradas += est.getHistorialSimulaciones().size();
            }
        }

        double promedio = totalEstudiantes > 0
                ? (double) totalSimulacionesExitosas / totalEstudiantes
                : 0.0;

        String resultado = "\n=== ESTADÍSTICAS GENERALES ===\n";
        resultado += "Total de estudiantes registrados: " + totalEstudiantes + "\n";
        resultado += "Total de simulaciones registradas en historiales: " + totalSimulacionesRegistradas + "\n";
        resultado += "Total de simulaciones exitosas: " + totalSimulacionesExitosas + "\n";
        resultado += String.format("Promedio de simulaciones exitosas por estudiante: %.2f\n", promedio);
        return resultado;
    }

}

package simulacionEcosistema.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Simulacion {

    // Definición del período de tiempo que representa un turno del ecosistema.
    public static final int DIAS_POR_TURNO = 7; // 1 turno = 1 semana simulada
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int tiempoTotal;
    private int turnoActual;
    private boolean activa;
    private List<Especie> especies;
    private List<Poblacion> poblaciones;
    private List<Poblacion> poblacionesInicial;  // Copia de poblaciones al inicio
    private Map<String, Integer> estadoInicial;  // Snapshot: nombre especie -> cantidad inicial
    private List<Interaccion> interacciones;
    private Entorno entorno;

    private final LocalDateTime fechaCreacion;      // Cuándo se creó/inició la simulación
    private final List<String> bitacoraTurnos;      // Qué ocurrió en cada turno ejecutado
    private String estadoFinal;                     // Foto del ecosistema al terminar
    private String resultado;                        // "EN CURSO", "VICTORIA" o "COLAPSO"

    // Constructor que blinda el tiempo total contra valores negativos o cero
    public Simulacion(int tiempoTotal, Entorno entorno) {
        this.tiempoTotal = (tiempoTotal > 0) ? tiempoTotal : 10; // Por defecto 10 turnos
        this.turnoActual = 0;
        this.activa = false;
        this.especies = new ArrayList<>();
        this.poblaciones = new ArrayList<>();
        this.poblacionesInicial = new ArrayList<>();
        this.estadoInicial = new LinkedHashMap<>();
        this.interacciones = new ArrayList<>();
        this.entorno = entorno;
        this.fechaCreacion = LocalDateTime.now();
        this.bitacoraTurnos = new ArrayList<>();
        this.resultado = "EN CURSO";
    }

    // ======= GETTERS Y SETTERS (Encapsulamiento puro sin lógica de negocio) =======

    public int getTiempoTotal() {
        return tiempoTotal;
    }

    public void setTiempoTotal(int tiempoTotal) {
        if (tiempoTotal > 0) {
            this.tiempoTotal = tiempoTotal;
        }
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(int turnoActual) {
        this.turnoActual = turnoActual;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<Especie> getEspecies() {
        return especies;
    }

    public void setEspecies(List<Especie> especies) {
        this.especies = especies;
    }

    public List<Poblacion> getPoblaciones() {
        return poblaciones;
    }

    public void setPoblaciones(List<Poblacion> poblaciones) {
        this.poblaciones = poblaciones;
    }

    public List<Interaccion> getInteracciones() {
        return interacciones;
    }

    public void setInteracciones(List<Interaccion> interacciones) {
        this.interacciones = interacciones;
    }
    public List<Poblacion> getListaPoblaciones() {
        return poblaciones;
    }

    public List<Poblacion> getPoblacionesInicial() {
        return poblacionesInicial;
    }

    public void setPoblacionesInicial(List<Poblacion> poblaciones) {
        if (poblaciones != null) {
            this.poblacionesInicial = new ArrayList<>(poblaciones);
        }
    }

    // Estado inicial: snapshot de nombre especie -> cantidad inicial
    public Map<String, Integer> getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(Map<String, Integer> estadoInicial) {
        if (estadoInicial != null) {
            this.estadoInicial = new LinkedHashMap<>(estadoInicial);
        }
    }

    public Entorno getEntorno() {
        return entorno;
    }

    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
    }


    // ======= FECHA Y DURACIÓN DEL TURNO =======

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFechaFormateada() {
        return fechaCreacion.format(FORMATO_FECHA);
    }

    public String getDuracionTurnoDescripcion() {
        return "1 turno equivale a " + DIAS_POR_TURNO + " días (1 semana) del ecosistema simulado.";
    }

    // ======= BITÁCORA DE TURNOS (para historial y reportes) =======

    public void registrarEventoTurno(String evento) {
        if (evento != null && !evento.isBlank()) {
            bitacoraTurnos.add(evento);
        }
    }

    public List<String> getBitacoraTurnos() {
        return bitacoraTurnos;
    }

    // ======= ESTADO FINAL / RESULTADO =======

    public String getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(String estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        if (resultado != null && !resultado.isBlank()) {
            this.resultado = resultado;
        }
    }

    public void reiniciarSimulacion() {
        this.turnoActual = 0;
        this.activa = false;
        this.poblaciones.clear();
        this.poblacionesInicial.clear();
        this.estadoInicial.clear();
        this.interacciones.clear();
        this.especies.clear();
        this.bitacoraTurnos.clear();
        this.estadoFinal = null;
        this.resultado = "EN CURSO";
        if (this.entorno != null) {
            this.entorno.restablecerRecursos();
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== ESTADO DEL ECOSISTEMA ===\n");
        sb.append("Fecha de la simulación: ").append(getFechaFormateada()).append("\n");
        sb.append("Turno actual: ").append(turnoActual).append(" / ").append(tiempoTotal).append("\n");
        sb.append("Simulación activa: ").append(activa ? "Sí" : "No").append("\n");

        sb.append("\n--- Poblaciones ---\n");
        for (Poblacion p : poblaciones) {
            sb.append(p.getEspecie().getNombre())
                    .append(" -> ").append(p.getCantidad()).append(" individuos\n");
        }

        sb.append("\n--- Interacciones ---\n");
        for (Interaccion i : interacciones) {
            sb.append(i.getNombreDepredador())
                    .append(" caza a ").append(i.getNombrePresa())
                    .append(" (probabilidad: ").append(i.getFactorAfectacion()).append(")\n");
        }

        if (entorno != null) {
            sb.append("\n--- Entorno ---\n");
            sb.append(entorno.toString()).append("\n");
        }

        return sb.toString();
    }
}
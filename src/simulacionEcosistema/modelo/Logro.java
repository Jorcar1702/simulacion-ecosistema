package simulacionEcosistema.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logro {
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaObtenido;
    private String tipo; // "PRIMERA_SIMULACION", "TRES_ESPECIES_VIVAS", etc.

    public Logro(String nombre, String descripcion, String tipo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaObtenido = LocalDateTime.now();
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaObtenido() { return fechaObtenido; }
    public String getTipo() { return tipo; }

    public String getFechaFormateada() {
        return fechaObtenido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + nombre + " - " + descripcion + " | Obtenido: " + getFechaFormateada();
    }
}


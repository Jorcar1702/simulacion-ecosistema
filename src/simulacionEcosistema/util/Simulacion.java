package simulacionEcosistema.util;

import simulacionEcosistema.negocio.*;
import java.util.ArrayList;
import java.util.List;

public class Simulacion {
    private int tiempoTotal;
    private int turnoActual;
    private boolean activa;
    private List<Especie> especies;
    private List<Poblacion> poblaciones;
    private Entorno entorno;
    private List<Interaccion> interacciones;

    // 🔹 Constructor
    public Simulacion(int tiempoTotal, Entorno entorno) {
        this.tiempoTotal = tiempoTotal;
        this.turnoActual = 0;
        this.activa = false;
        this.especies = new ArrayList<>();
        this.poblaciones = new ArrayList<>();
        this.interacciones = new ArrayList<>();
        this.entorno = entorno;
    }

    public List<Especie> getEspecies() { return especies; }
    public List<Poblacion> getPoblaciones() { return poblaciones; }
    public List<Interaccion> getInteracciones() { return interacciones; }
    public Entorno getEntorno() { return entorno; }

    public void agregarEspecie(Especie e) {
        especies.add(e);
    }

    public void agregarPoblacion(Poblacion p) {
        poblaciones.add(p);
    }

    public void agregarInteraccion(Interaccion i) {
        interacciones.add(i);
    }

    public Poblacion buscarPoblacion(String nombreEspecie) {
        for (Poblacion p : poblaciones) {
            if (p.getEspecie().getNombre().equalsIgnoreCase(nombreEspecie)) {
                return p;
            }
        }
        return null;
    }
    public void iniciarSimulacion() {
        turnoActual = 0;
        activa = true;
        System.out.println("Simulación iniciada.");
    }

    public void avanzarTurno() {
        if (!activa) {
            System.out.println("La simulación no está activa.");
            return;
        }
        if (turnoActual >= tiempoTotal) {
            System.out.println("Tiempo máximo alcanzado.");
            detenerSimulacion();
            return;
        }

        turnoActual++;
        System.out.println("\n--- Turno " + turnoActual + " ---");

        for (Poblacion p : poblaciones) {
            int antes = p.getCantidad();
            p.actualizarPoblacion();
            int despues = p.getCantidad();
            System.out.println(p.getEspecie().getNombre() + ": " + antes + " → " + despues);
        }

        for (Interaccion i : interacciones) {
        }
        entorno.actualizarRecursos(poblaciones);

        System.out.println("Turno " + turnoActual + " completado.");
    }

    public void detenerSimulacion() {
        activa = false;
        System.out.println("Simulación detenida.");
    }
}

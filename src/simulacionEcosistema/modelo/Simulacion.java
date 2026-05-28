package simulacionEcosistema.modelo;

import java.util.ArrayList;
import java.util.List;

public class Simulacion {
    private int tiempoTotal;
    private int turnoActual;
    private boolean activa;
    private List<Especie> especies;
    private List<Poblacion> poblaciones;
    private List<Interaccion> interacciones;
    private Entorno entorno;

    // Constructor que blinda el tiempo total contra valores negativos o cero
    public Simulacion(int tiempoTotal, Entorno entorno) {
        this.tiempoTotal = (tiempoTotal > 0) ? tiempoTotal : 10; // Por defecto 10 turnos
        this.turnoActual = 0;
        this.activa = false;
        this.especies = new ArrayList<>();
        this.poblaciones = new ArrayList<>();
        this.interacciones = new ArrayList<>();
        this.entorno = entorno;
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

    public Entorno getEntorno() {
        return entorno;
    }

    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
    }


    public void reiniciarSimulacion() {
        this.turnoActual = 0;
        this.activa = false;
        this.poblaciones.clear();
        this.interacciones.clear();
        this.especies.clear();
        if (this.entorno != null) {
            this.entorno.restablecerRecursos();
        }
    }
}
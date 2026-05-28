package simulacionEcosistema.modelo;

import java.util.ArrayList;
import java.util.List;

public class Entorno {
    private int alimentoDisponible;
    private int tasaConsumo;
    private int regeneracionVegetal;
    private List<String> alertas;

    public Entorno(int alimentoDisponible, int tasaConsumo, int regeneracionVegetal) {
        this.alimentoDisponible = alimentoDisponible;
        this.tasaConsumo = tasaConsumo;
        this.regeneracionVegetal = regeneracionVegetal;
        this.alertas = new ArrayList<>();
    }


    public int getAlimentoDisponible() { return alimentoDisponible; }

    public int getTasaConsumo() { return tasaConsumo; }
    public int getRegeneracionVegetal() { return regeneracionVegetal; }
    public List<String> getAlertas() { return alertas; }


    public void calcularConsumo(List<Poblacion> poblaciones) {
        int consumoTotal = 0;
        for (Poblacion p : poblaciones) {
            consumoTotal += p.getCantidad() * tasaConsumo;
        }
        alimentoDisponible -= consumoTotal;
        if (alimentoDisponible < 0) alimentoDisponible = 0;
    }

    public void generarAlerta(String mensaje) {
        alertas.add(mensaje);
        System.out.println("ALERTA: " + mensaje);
    }

    public void actualizarRecursos(List<Poblacion> poblaciones) {
        calcularConsumo(poblaciones);
        alimentoDisponible += regeneracionVegetal;
        if (alimentoDisponible <= 0) {
            aplicarCrisis();
        }
    }
    public void restablecerRecursos() {
        this.alimentoDisponible = 100;
        System.out.println(" [Entorno] Recursos del bioma restablecidos a su estado inicial.");
    }

    public void aplicarCrisis() {
        generarAlerta("Crisis ambiental: recursos agotados.");
    }

    public void mostrarInformacion() {
        System.out.println("Entorno -> Alimento disponible: " + alimentoDisponible);
        System.out.println("Regeneración vegetal: " + regeneracionVegetal);
        if (!alertas.isEmpty()) {
            System.out.println("Alertas: " + alertas);
        }
    }
    public void setAlimentoDisponible(int alimentoDisponible) {
        if (alimentoDisponible >= 0) {
            this.alimentoDisponible = alimentoDisponible;
            System.out.println("[Entorno] Nuevo alimento disponible: " + alimentoDisponible);
        } else {
            System.out.println("Error: el alimento disponible debe ser positivo.");
        }
    }

    public void setTasaConsumo(int tasaConsumo) {
        if (tasaConsumo >= 0) {
            this.tasaConsumo = tasaConsumo;
            System.out.println("[Entorno] Nueva tasa de consumo: " + tasaConsumo);
        } else {
            System.out.println("Error: la tasa de consumo debe ser positiva.");
        }
    }

    public void setRegeneracionVegetal(int regeneracionVegetal) {
        if (regeneracionVegetal >= 0) {
            this.regeneracionVegetal = regeneracionVegetal;
            System.out.println("[Entorno] Nueva tasa de regeneración vegetal: " + regeneracionVegetal);
        } else {
            System.out.println("Error: la regeneración vegetal debe ser positiva.");
        }
    }
    @Override
    public String toString() {
        return "Alimento disponible: " + alimentoDisponible +
                " | Tasa de consumo: " + tasaConsumo +
                " | Regeneración vegetal: " + regeneracionVegetal;
    }
}

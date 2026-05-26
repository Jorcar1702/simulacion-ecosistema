package simulacionEcosistema.negocio;

import java.util.ArrayList;
import java.util.List;

public class Entorno {
    private int alimentoDisponible;
    private int tasaConsumo;
    private int regeneracionVegetal;
    private List<String> alertas;

    // 🔹 Constructor
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
}

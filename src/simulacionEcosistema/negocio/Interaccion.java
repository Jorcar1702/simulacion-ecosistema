package simulacionEcosistema.negocio;

public class Interaccion {
    private double factorCaza;              // proporción de presas cazadas por depredador
    private double eficienciaConversion;    // eficiencia de convertir presas en nuevos depredadores

    public Interaccion(double factorCaza, double eficienciaConversion) {
        this.factorCaza = factorCaza;
        this.eficienciaConversion = eficienciaConversion;
    }

    public double getFactorCaza() {
        return factorCaza;
    }

    public double getEficienciaConversion() {
        return eficienciaConversion;
    }

    public int simularCaza(Poblacion presas, Poblacion depredadores) {
        int presasCazadas = (int)(depredadores.getCantidad() * factorCaza);
        if (presasCazadas > presas.getCantidad()) {
            presasCazadas = presas.getCantidad();
        }
        presas.setCantidad(presas.getCantidad() - presasCazadas);
        return presasCazadas;
    }


    public void transferirEnergia(Poblacion depredadores, int presasCazadas) {
        int nuevosDepredadores = (int)(presasCazadas * eficienciaConversion);
        depredadores.setCantidad(depredadores.getCantidad() + nuevosDepredadores);
    }

    public void mostrarInformacion() {
        System.out.println("Interacción: factor de caza = " + factorCaza +
                ", eficiencia de conversión = " + eficienciaConversion);
    }

    @Override
    public String toString() {
        return "Interaccion{" +
                "factorCaza=" + factorCaza +
                ", eficienciaConversion=" + eficienciaConversion +
                '}';
    }
}

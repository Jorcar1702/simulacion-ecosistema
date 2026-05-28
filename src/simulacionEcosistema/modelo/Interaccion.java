package simulacionEcosistema.modelo;

public class Interaccion {
    private String nombreDepredador;   // Nombre de la especie que caza
    private String nombrePresa;        // Nombre de la especie cazada
    private double tasaExito;          // Probabilidad de éxito de la caza (0.0 a 1.0)
    private double factorAfectacion;   // Cuánto golpea el hambre o la sobrepoblación

    // Constructor que soluciona el error de instanciación del Main
    public Interaccion(String nombreDepredador, String nombrePresa, double tasaExito, double factorAfectacion) {
        this.nombreDepredador = nombreDepredador;
        this.nombrePresa = nombrePresa;
        this.tasaExito = tasaExito;
        this.factorAfectacion = factorAfectacion;
    }

    // ======= LÓGICA DE NEGOCIO BIOLÓGICA =======

    /**
     * Calcula cuántos individuos de la población presa mueren debido a la caza.
     */
    public int simularCaza(Poblacion presa, Poblacion depredador) {
        if (presa == null || depredador == null || presa.getCantidad() <= 0 || depredador.getCantidad() <= 0) {
            return 0;
        }

        // Lógica matemática base: los depredadores intentan cazar según la tasa de éxito
        int intentosCaza = (int) (depredador.getCantidad() * 1.5);
        int muertesPresa = (int) (intentosCaza * this.tasaExito * this.factorAfectacion);

        // Controlamos que no se cacen más de los que existen en la población real
        if (muertesPresa > presa.getCantidad()) {
            muertesPresa = presa.getCantidad();
        }

        // Restamos las bajas directamente al modelo de la presa
        presa.setCantidad(presa.getCantidad() - muertesPresa);

        System.out.println("⚔️  [Interacción] " + nombreDepredador + " cazó " + muertesPresa + " individuos de " + nombrePresa + ".");
        return muertesPresa;
    }

    /**
     * Transfiere la energía o alimento obtenido de las presas para beneficiar al depredador.
     */
    public void transferirEnergia(Poblacion depredador, int presasCazadas) {
        if (depredador == null || presasCazadas <= 0) return;

        // Por cada presa comida, el depredador genera un estímulo de natalidad o supervivencia
        int nuevosNacimientos = (int) (presasCazadas * 0.25); // Factor de conversión biológica

        int nuevaCantidad = depredador.getCantidad() + nuevosNacimientos;

        // Respetamos el tope máximo que soporte el bioma para esa especie
        if (nuevaCantidad > depredador.getLimiteMaximo()) {
            nuevaCantidad = depredador.getLimiteMaximo();
        }

        depredador.setCantidad(nuevaCantidad);
        if (nuevosNacimientos > 0) {
            System.out.println(" [Interacción] " + nombreDepredador + " aumentó +" + nuevosNacimientos + " individuos gracias al alimento.");
        }
    }

    // ======= GETTERS Y SETTERS COMPATIBLES CON EL GESTOR =======

    public String getNombreDepredador() {
        return nombreDepredador;
    }

    public void setNombreDepredador(String nombreDepredador) {
        this.nombreDepredador = nombreDepredador;
    }

    public String getNombrePresa() {
        return nombrePresa;
    }

    public void setNombrePresa(String nombrePresa) {
        this.nombrePresa = nombrePresa;
    }

    public double getTasaExito() {
        return tasaExito;
    }

    public void setTasaExito(double tasaExito) {
        this.tasaExito = tasaExito;
    }

    public double getFactorAfectacion() {
        return factorAfectacion;
    }

    public void setFactorAfectacion(double factorAfectacion) {
        this.factorAfectacion = factorAfectacion;
    }
}
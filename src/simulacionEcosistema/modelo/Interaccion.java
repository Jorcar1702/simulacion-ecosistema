package simulacionEcosistema.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Interaccion {

    // Porcentaje de la población depredadora que muere por turno cuando no hay presas disponibles.
    private static final double TASA_HAMBRUNA = 0.20;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String nombreDepredador;   // Nombre de la especie que caza
    private String nombrePresa;        // Nombre de la especie cazada
    private double tasaExito;          // Probabilidad de éxito de la caza (0.0 a 1.0)
    private double factorAfectacion;   // Cuánto golpea el hambre o la sobrepoblación
    private LocalDateTime ultimaEjecucion; // Fecha/hora en que se ejecutó por última vez esta interacción

    public Interaccion(String nombreDepredador, String nombrePresa, double tasaExito, double factorAfectacion) {
        this.nombreDepredador = nombreDepredador;
        this.nombrePresa = nombrePresa;
        this.tasaExito = tasaExito;
        this.factorAfectacion = factorAfectacion;
    }


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
        this.ultimaEjecucion = LocalDateTime.now();

        System.out.println(" [Interacción " + this.ultimaEjecucion.format(FORMATO_FECHA) + "] "
                + nombreDepredador + " cazó " + muertesPresa + " individuos de " + nombrePresa + ".");
        return muertesPresa;
    }

    /**
     * Cuando la presa se extinguió (cantidad = 0), el depredador empieza a morir de hambre
     * turno a turno hasta que aparezca comida de nuevo o se extinga también.
     */
    public int aplicarHambrunaPorFaltaDePresa(Poblacion depredador) {
        if (depredador == null || depredador.getCantidad() <= 0) return 0;

        int muertesPorHambre = (int) Math.ceil(depredador.getCantidad() * TASA_HAMBRUNA);
        if (muertesPorHambre > depredador.getCantidad()) {
            muertesPorHambre = depredador.getCantidad();
        }

        depredador.setCantidad(depredador.getCantidad() - muertesPorHambre);
        this.ultimaEjecucion = LocalDateTime.now();

        System.out.println(" [Hambruna " + this.ultimaEjecucion.format(FORMATO_FECHA) + "] "
                + nombreDepredador + " perdió " + muertesPorHambre
                + " individuos por falta de " + nombrePresa + " (población de presa en 0).");
        return muertesPorHambre;
    }

    public LocalDateTime getUltimaEjecucion() {
        return ultimaEjecucion;
    }

    public String getUltimaEjecucionFormateada() {
        return ultimaEjecucion != null ? ultimaEjecucion.format(FORMATO_FECHA) : "Sin ejecutar todavía";
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
            System.out.println("[Interacción] " + nombreDepredador + " aumentó +" + nuevosNacimientos + " individuos gracias al alimento.");
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
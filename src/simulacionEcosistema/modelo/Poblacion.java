package simulacionEcosistema.modelo;

public class Poblacion {
    private int cantidad;
    private int limiteMaximo;
    private Especie especie;

    public Poblacion(int cantidad, int limiteMaximo, Especie especie) {
        this.cantidad = cantidad;
        this.limiteMaximo = limiteMaximo;
        this.especie = especie;
    }

    public void actualizarPoblacion() {
        if (this.cantidad <= 0 || this.cantidad >= this.limiteMaximo) {
            return;
        }
        int nacimientos = (int) (this.cantidad * especie.getTasaNatalidad());
        if (nacimientos == 0 && this.cantidad > 1) {
            nacimientos = 1;
        }
        int nuevaCantidad = this.cantidad + nacimientos;
        if (nuevaCantidad > this.limiteMaximo) {
            nuevaCantidad = this.limiteMaximo;
        }

        this.cantidad = nuevaCantidad;
        System.out.println(" [Población] " + especie.getNombre() + " creció de forma natural a " + this.cantidad + " individuos (+" + nacimientos + ").");
    }



    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 0) {
            this.cantidad = 0;
        } else if (cantidad > this.limiteMaximo) {
            this.cantidad = this.limiteMaximo;
        } else {
            this.cantidad = cantidad;
        }
    }

    public int getLimiteMaximo() {
        return limiteMaximo;
    }

    public void setLimiteMaximo(int limiteMaximo) {
        this.limiteMaximo = limiteMaximo;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
}
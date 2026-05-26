package simulacionEcosistema.negocio;

public class Poblacion {
    private int cantidad;
    private int limiteEspacio;
    private Especie especie;

    public Poblacion(int cantidad,int limiteEspacio, Especie especie){
        this.cantidad= cantidad;
        this.limiteEspacio= limiteEspacio;
        this.especie= especie;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getLimiteEspacio() {
        return limiteEspacio;
    }

    public void setLimiteEspacio(int limiteEspacio) {
        this.limiteEspacio = limiteEspacio;
    }

    public Especie getEspecie() {
        return especie;
    }

    public int calcularNacimientos(){
        if(cantidad >= limiteEspacio){
            return 0;
        }
        int nacimientos = (int) (this.cantidad * especie.getTasaReproduccion());
        if (this.cantidad + nacimientos > limiteEspacio) {
            nacimientos = limiteEspacio - this.cantidad;
        }

        return nacimientos;
    }
    public int aplicarMortalidad() {
        return (int)(cantidad * especie.getTasaMortalidad());
    }


    public void actualizarPoblacion() {
        int nacimientos = calcularNacimientos();
        int muertes = aplicarMortalidad();
        cantidad = cantidad + nacimientos - muertes;

        if (cantidad > limiteEspacio) {
            cantidad = limiteEspacio;
        }
        if (cantidad < 0) {
            cantidad = 0;
        }
    }
    public void mostrarInformacion(){
        System.out.println("Población de " + especie.getNombre() + ": " + cantidad + " individuos.");
    }

}

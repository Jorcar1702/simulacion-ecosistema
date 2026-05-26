package simulacionEcosistema.negocio;

public class Especie {
    private String nombre;
    private String tipo; // Herbívoro / Carnívoro / Planta
    private double tasaReproduccion;
    private double tasaMortalidad;

    public Especie(String nombre, String tipo, double tasaReproduccion, double tasaMortalidad) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.tasaReproduccion = tasaReproduccion;
        this.tasaMortalidad = tasaMortalidad;
    }

    public String getNombre() {
        return nombre;
    }
    public String getTipo() {
        return tipo;
    }
    public double getTasaReproduccion() {
        return tasaReproduccion;
    }
    public void setTasaReproduccion(double tasaReproduccion) {
        this.tasaReproduccion = tasaReproduccion;
    }
    public double getTasaMortalidad() {
        return tasaMortalidad;
    }
    public void setTasaMortalidad(double tasaMortalidad) {
        this.tasaMortalidad = tasaMortalidad;
    }

    public boolean validarViabilidad() {
        return tasaReproduccion > tasaMortalidad && tasaReproduccion > 0;
    }

    public void registrarEspecie() {
        if (validarViabilidad()) {
            System.out.println("Especie registrada: " + nombre + " | Tipo: " + tipo);
        } else {
            System.out.println("No se pudo registrar la especie: " + nombre + " (No viable)");
        }
    }

    public void mostrarInformacion() {
        System.out.println(this.toString());
        System.out.println("Viabilidad: " + (validarViabilidad() ? "Viable" : "No viable"));
    }

    @Override
    public String toString() {
        return "Especie{" +
                "nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", tasaReproduccion=" + tasaReproduccion +
                ", tasaMortalidad=" + tasaMortalidad +
                '}';
    }
}

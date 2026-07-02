package simulacionEcosistema.modelo;

public class Especie {
    private String nombre;
    private String tipo;
    private double tasaNatalidad;
    private double tasaMortalidad;


    public Especie(String nombre, String tipo, double tasaNatalidad, double tasaMortalidad) {
        this.nombre = nombre;
        this.tipo = normalizeTipo(tipo);
        this.tasaNatalidad = (tasaNatalidad >= 0 && tasaNatalidad <= 1) ? tasaNatalidad : 0.12;
        this.tasaMortalidad = (tasaMortalidad >= 0 && tasaMortalidad <= 1) ? tasaMortalidad : 0.05;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = normalizeTipo(tipo);
    }


    public String normalizeTipo(String tipo) {
        if (tipo == null) return "HERBIVORO";
        String t = tipo.replaceAll("[áÁéÉíÍóÓúÚ]", "").toUpperCase().trim();
        if (t.contains("CARN")) return "CARNIVORO";
        if (t.contains("HERB") || t.equals("ANIMAL")) return "HERBIVORO";
        if (t.contains("PLANT")) return "PLANTA";
        return "HERBIVORO";
    }

    public double getTasaNatalidad() {
        return tasaNatalidad;
    }

    public void setTasaNatalidad(double tasaNatalidad) {
        if (tasaNatalidad >= 0 && tasaNatalidad <= 1) {
            this.tasaNatalidad = tasaNatalidad;
        }
    }

    public double getTasaMortalidad() {
        return tasaMortalidad;
    }

    public void setTasaMortalidad(double tasaMortalidad) {
        if (tasaMortalidad >= 0 && tasaMortalidad <= 1) {
            this.tasaMortalidad = tasaMortalidad;
        }
    }
}
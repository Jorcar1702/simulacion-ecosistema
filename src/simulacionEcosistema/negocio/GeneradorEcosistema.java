package simulacionEcosistema.negocio;

import simulacionEcosistema.modelo.*;
import java.util.ArrayList;
import java.util.List;

public class GeneradorEcosistema {
    private GestorUsuario gestorUsuario;

    // Valores por defecto (constantes)
    private static final int DEFAULT_ALIMENTO = 1000;
    private static final int DEFAULT_TASA_CONSUMO = 2;
    private static final int DEFAULT_REGENERACION = 50;

    // Parámetros por paralelo (A/B/C) — inicializados con valores por defecto
    // Alimento disponible
    private static int alimentoA = DEFAULT_ALIMENTO;
    private static int alimentoB = DEFAULT_ALIMENTO;
    private static int alimentoC = DEFAULT_ALIMENTO;

    // Tasa de consumo
    private static int consumoA = DEFAULT_TASA_CONSUMO;
    private static int consumoB = DEFAULT_TASA_CONSUMO;
    private static int consumoC = DEFAULT_TASA_CONSUMO;

    // Regeneración vegetal
    private static int regeneracionA = DEFAULT_REGENERACION;
    private static int regeneracionB = DEFAULT_REGENERACION;
    private static int regeneracionC = DEFAULT_REGENERACION;

    public GeneradorEcosistema() {
        this.gestorUsuario = new GestorUsuario();
        cargarUsuariosPorDefecto();
    }

    public GestorUsuario getGestorUsuario() {
        return gestorUsuario;
    }
    public void registrarUsuario(Usuario u) throws Exception {
        if (u != null) {
            gestorUsuario.registrarUsuario(u);
        }
    }

    public static GestorSimulacion crearEcosistemaBase(Estudiante estudiante, int tiempoMax) {
        // Usar valores por defecto, pero modificar si el estudiante tiene un paralelo configurado
        int alimento = DEFAULT_ALIMENTO;
        int consumo = DEFAULT_TASA_CONSUMO;
        int regen = DEFAULT_REGENERACION;

        if (estudiante != null) {
            String p = estudiante.getParalelo();
            if ("A".equalsIgnoreCase(p)) {
                alimento = alimentoA;
                consumo = consumoA;
                regen = regeneracionA;
            } else if ("B".equalsIgnoreCase(p)) {
                alimento = alimentoB;
                consumo = consumoB;
                regen = regeneracionB;
            } else if ("C".equalsIgnoreCase(p)) {
                alimento = alimentoC;
                consumo = consumoC;
                regen = regeneracionC;
            }
        }
        Entorno entorno = new Entorno(alimento, consumo, regen);
         Simulacion simulacion = new Simulacion(tiempoMax, entorno);
         GestorSimulacion gestor = new GestorSimulacion(simulacion, estudiante);

         Especie hierba = new Especie("Hierba", "PLANTA", 0.40, 0.10);
         Especie conejo = new Especie("Conejo Silvestre", "HERBIVORO", 0.25, 0.15);

         gestor.registrarPoblacion(new Poblacion(100, 400, hierba));
         gestor.registrarPoblacion(new Poblacion(20, 80, conejo));

         gestor.registrarInteraccion(new Interaccion("Conejo Silvestre", "Hierba", 0.30, 1.0));

         return gestor;
    }
    public static Poblacion crearEspecieConPoblacion(String nombre, String tipo, int cantidad, int capacidad) {
        Especie nueva = new Especie(nombre, tipo, 0.2, 0.1);
        Poblacion poblacion = new Poblacion(cantidad, capacidad, nueva);

        System.out.println("Especie " + nombre + " creada.");
        System.out.println("Población inicial de " + cantidad + " " + nombre + " registrada en el ecosistema.");

        return poblacion;
    }

    private void cargarUsuariosPorDefecto() {
        try {
            registrarUsuario(new Administrador("Dr Joe Garcia", "1725423899",
                    "edison.loza@udla.edu.ec", "jgarcia123", "jgarcia123"));

            registrarUsuario(new Estudiante("Marco Rodriguez", "1727359794",
                    "marco.rodriguez@udla.ec", "marro123", "marro123", "A"));
            registrarUsuario(new Estudiante("Jennifer Navarrete", "1708621915",
                    "jennifer.navarrete@udla.ec", "jennifer123", "jennifer123", "B"));
            registrarUsuario(new Estudiante("Juan Vallejo", "1702895630",
                    "juan.vallejo@udla.ec", "juan123", "juan123", "C"));
        } catch (Exception e) {
            System.out.println("Error al cargar usuarios por defecto: " + e.getMessage());
        }
    }

    // getters / setters para los 3 parámetros por paralelo
    // Paralelo A
    public static int getAlimentoA(){ return alimentoA; }
    public static int getConsumoA(){ return consumoA; }
    public static int getRegeneracionA(){ return regeneracionA; }
    public static void setAlimentoA(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); alimentoA = v; }
    public static void setConsumoA(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); consumoA = v; }
    public static void setRegeneracionA(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); regeneracionA = v; }

    // Paralelo B
    public static int getAlimentoB(){ return alimentoB; }
    public static int getConsumoB(){ return consumoB; }
    public static int getRegeneracionB(){ return regeneracionB; }
    public static void setAlimentoB(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); alimentoB = v; }
    public static void setConsumoB(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); consumoB = v; }
    public static void setRegeneracionB(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); regeneracionB = v; }

    // Paralelo C
    public static int getAlimentoC(){ return alimentoC; }
    public static int getConsumoC(){ return consumoC; }
    public static int getRegeneracionC(){ return regeneracionC; }
    public static void setAlimentoC(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); alimentoC = v; }
    public static void setConsumoC(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); consumoC = v; }
    public static void setRegeneracionC(int v) throws Exception{ if(v<0) throw new Exception("Valor inválido"); regeneracionC = v; }

    // Restaurar valores por defecto para todos los paralelos
    public static void restaurarValoresPorDefecto() {
        alimentoA = DEFAULT_ALIMENTO;
        alimentoB = DEFAULT_ALIMENTO;
        alimentoC = DEFAULT_ALIMENTO;
        consumoA = DEFAULT_TASA_CONSUMO;
        consumoB = DEFAULT_TASA_CONSUMO;
        consumoC = DEFAULT_TASA_CONSUMO;
        regeneracionA = DEFAULT_REGENERACION;
        regeneracionB = DEFAULT_REGENERACION;
        regeneracionC = DEFAULT_REGENERACION;
    }

    // Obtener configuración de un paralelo como Map
    public static java.util.Map<String, Integer> obtenerConfiguracion(String paralelo) {
        java.util.Map<String, Integer> config = new java.util.LinkedHashMap<>();

        if (paralelo != null && !paralelo.isEmpty()) {
            if ("A".equalsIgnoreCase(paralelo)) {
                config.put("Alimento Disponible", alimentoA);
                config.put("Tasa de Consumo", consumoA);
                config.put("Regeneración Vegetal", regeneracionA);
            } else if ("B".equalsIgnoreCase(paralelo)) {
                config.put("Alimento Disponible", alimentoB);
                config.put("Tasa de Consumo", consumoB);
                config.put("Regeneración Vegetal", regeneracionB);
            } else if ("C".equalsIgnoreCase(paralelo)) {
                config.put("Alimento Disponible", alimentoC);
                config.put("Tasa de Consumo", consumoC);
                config.put("Regeneración Vegetal", regeneracionC);
            }
        }

        return config;
    }
}

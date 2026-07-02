package simulacionEcosistema.negocio;

import simulacionEcosistema.modelo.*;
import java.util.ArrayList;
import java.util.List;

public class GeneradorEcosistema {
    private GestorUsuario gestorUsuario;

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
        Entorno entorno = new Entorno(1000, 2, 50);
        Simulacion simulacion = new Simulacion(tiempoMax, entorno);
        GestorSimulacion gestor = new GestorSimulacion(simulacion, estudiante);

        Especie hierba = new Especie("Hierba", "PLANTA", 0.40, 0.10);
        Especie conejo = new Especie("Conejo Silvestre", "ANIMAL", 0.25, 0.15);

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
}

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
    public void registrarUsuario(Usuario u) {
        if (u != null) {
            gestorUsuario.registrarUsuario(u);
        }
    }

    public static GestorSimulacion crearEcosistemaBase(Estudiante estudiante, int tiempoMax) {
        Entorno entorno = new Entorno(1000, 2, 50);

        Simulacion simulacion =
                new Simulacion(tiempoMax, entorno);

        GestorSimulacion gestor = new GestorSimulacion(
                        simulacion,
                        estudiante
                );

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
        registrarUsuario(new Administrador("Dr. Joe Garcia", "1711111111",
                "edison.loza@udla.edu.ec", "jgarcia123", "jgarcia123", "FICA"));

        registrarUsuario(new Estudiante("Marco Rodriguez", "1722222222",
                "marco.rodriguez@udla.ec", "marro123", "marro123", "A"));
        registrarUsuario(new Estudiante("Jennifer Navarrete", "1733333333",
                "jennifer.navarrete@udla.ec", "jennifer123", "jennifer123", "B"));
        registrarUsuario(new Estudiante("Juan Vallejo", "1744444444",
                "juan.vallejo@udla.ec", "juan123", "juan123", "C"));
    }
}

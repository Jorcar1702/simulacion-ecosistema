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
        Simulacion simulacion = new Simulacion(tiempoMax, entorno);
        GestorSimulacion gestor = new GestorSimulacion(simulacion, estudiante);

        Especie hierba = new Especie("Hierba", "PLANTA", 0.40, 0.10);
        Especie conejo = new Especie("Conejo Silvestre", "ANIMAL", 0.25, 0.15);

        gestor.registrarPoblacion(new Poblacion(100, 400, hierba));
        gestor.registrarPoblacion(new Poblacion(20, 80, conejo));

        gestor.registrarInteraccion(new Interaccion("Conejo Silvestre", "Hierba", 0.30, 1.0));

        return gestor;
    }

    // Usuarios de prueba
    private void cargarUsuariosPorDefecto() {
        registrarUsuario(new Administrador("Dr. Edison Loza", "1711111111",
                "edison.loza@udla.edu.ec", "0999999999", "Campus Granados", "admin123"));

        registrarUsuario(new Estudiante("Marco Rodriguez", "1722222222",
                "marco.rodriguez@udla.ec", "0984444444", "Quito Norte", "A"));
        registrarUsuario(new Estudiante("Jennifer Navarrete", "1733333333",
                "jennifer.navarrete@udla.ec", "0985555555", "Quito Centro", "A"));
        registrarUsuario(new Estudiante("Juan Vallejo", "1744444444",
                "juan.vallejo@udla.ec", "0986666666", "Quito Sur", "A"));
    }
}

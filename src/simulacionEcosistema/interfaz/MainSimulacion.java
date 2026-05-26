package simulacionEcosistema.interfaz;

import simulacionEcosistema.negocio.*;
import simulacionEcosistema.util.Simulacion;
import java.util.*;

public class MainSimulacion {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);


        Entorno entorno = new Entorno(1000, 1, 50);
        Simulacion simulacion = new Simulacion(10, entorno);

        int op;
        do {
            menu();
            op = Integer.parseInt(sc.nextLine());
            switch (op) {
                case 1: {
                    System.out.print("Nombre de la especie: ");
                    String nombre = sc.nextLine();
                    System.out.print("Tipo (Herbívoro/Carnívoro/Planta): ");
                    String tipo = sc.nextLine();
                    System.out.print("Tasa de reproducción: ");
                    double tasaR = Double.parseDouble(sc.nextLine());
                    System.out.print("Tasa de mortalidad: ");
                    double tasaM = Double.parseDouble(sc.nextLine());

                    Especie e = new Especie(nombre, tipo, tasaR, tasaM);
                    simulacion.agregarEspecie(e);
                    System.out.println("Especie agregada: " + e.getNombre());
                } break;

                case 2: {
                    if (simulacion.getEspecies().isEmpty()) {
                        System.out.println("Primero debe agregar especies (opción 1).");
                        break;
                    }
                    System.out.println("Especies disponibles:");
                    for (int i = 0; i < simulacion.getEspecies().size(); i++) {
                        System.out.println((i+1) + ". " + simulacion.getEspecies().get(i).getNombre());
                    }
                    System.out.print("Seleccione especie: ");
                    int idx = Integer.parseInt(sc.nextLine()) - 1;
                    Especie especie = simulacion.getEspecies().get(idx);

                    System.out.print("Cantidad inicial: ");
                    int cantidad = Integer.parseInt(sc.nextLine());
                    System.out.print("Capacidad máxima: ");
                    int capacidad = Integer.parseInt(sc.nextLine());

                    Poblacion p = new Poblacion(cantidad, capacidad, especie);
                    simulacion.agregarPoblacion(p);
                    System.out.println("Población agregada: " + especie.getNombre() + " con " + cantidad + " individuos.");
                } break;

                case 3: {
                    for (Poblacion p : simulacion.getPoblaciones()) {
                        System.out.println(p.getEspecie().getNombre() + ": " + p.getCantidad());
                    }
                } break;
                case 4: {
                    simulacion.iniciarSimulacion();
                } break;
                case 5: {
                    simulacion.avanzarTurno();
                } break;

                case 6: { // Simular varios turnos
                    System.out.print("Número de turnos: ");
                    int n = Integer.parseInt(sc.nextLine());
                    for (int i = 0; i < n; i++) {
                        simulacion.avanzarTurno();
                    }
                } break;

                case 7: { // Agregar interacción
                    System.out.print("Factor de caza: ");
                    double factorCaza = Double.parseDouble(sc.nextLine());
                    System.out.print("Eficiencia de conversión: ");
                    double eficiencia = Double.parseDouble(sc.nextLine());

                    Interaccion interaccion = new Interaccion(factorCaza, eficiencia);
                    simulacion.agregarInteraccion(interaccion);
                    System.out.println("Interacción agregada.");
                } break;

                case 8: { // Mostrar entorno
                    entorno.mostrarInformacion();
                } break;

                case 9: { // Buscar especie
                    System.out.print("Ingrese nombre de especie: ");
                    String nombre = sc.nextLine();
                    Poblacion p = simulacion.buscarPoblacion(nombre);
                    if (p != null) {
                        System.out.println("Población de " + p.getEspecie().getNombre() +
                                ": " + p.getCantidad() + " individuos.");
                    } else {
                        System.out.println("No existe esa especie.");
                    }
                } break;

                case 10: {
                    if (entorno.getAlertas().isEmpty()) {
                        System.out.println("No hay alertas registradas.");
                    } else {
                        System.out.println("Alertas del entorno:");
                        for (String alerta : entorno.getAlertas()) {
                            System.out.println("- " + alerta);
                        }
                    }
                } break;

                case 11: {
                    simulacion.detenerSimulacion();
                } break;



                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 0);

        sc.close();
    }

    public static void menu() {
        System.out.println("\n*********** Simulación Ecosistema **********");
        System.out.println("1. Agregar especie");
        System.out.println("2. Agregar población");
        System.out.println("3. Mostrar poblaciones");
        System.out.println("4. Iniciar Simulacion");
        System.out.println("4. Ejecutar un turno");
        System.out.println("5. Simular varios turnos");
        System.out.println("6. Agregar interacción");
        System.out.println("7. Mostrar entorno");
        System.out.println("8. Buscar especie");
        System.out.println("9. Mostrar alertas");
        System.out.println("10. Detener simulación");
        System.out.println("0. Salir");
        System.out.print("Ingrese una opción: ");
    }
}

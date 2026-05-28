package simulacionEcosistema.interfaz;

import simulacionEcosistema.negocio.*;
import simulacionEcosistema.modelo.*;
import java.util.Scanner;

public class MainSimulacion {
    static Scanner sc = new Scanner(System.in);
    static GeneradorEcosistema generador = new GeneradorEcosistema();
    static GestorUsuario gestorUsuarios = generador.getGestorUsuario();
    static GestorSimulacion motorSimulacion = null;
    static Estudiante estudianteActivo = null;

    public static void main(String[] args) {
        int opc;
        do {
            menuPrincipal();
            opc = leerEntero();
            switch(opc) {
                case 1 -> login();
                case 2 -> registrarEstudiante();
                case 3 -> System.out.println("Gracias por usar el simulador de ecosistemas.");
                default -> System.out.println("Opción inválida.");
            }
        } while(opc != 3);
    }

    // ===== MENÚ PRINCIPAL =====
    static void menuPrincipal() {
        System.out.println("\n=============================");
        System.out.println(" SIMULADOR DE ECOSISTEMAS UDLA");
        System.out.println("=============================");
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ===== LOGIN =====
    static void login() {
        System.out.print("Usuario: ");
        String usr = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        Usuario u = gestorUsuarios.buscarUsuarioParaLogin(usr, pass);
        if (u == null) {
            System.out.println("Credenciales inválidas.");
            return;
        }

        if (u instanceof Administrador) {
            menuProfesor();
        } else if (u instanceof Estudiante) {
            estudianteActivo = (Estudiante) u;
            menuEstudiante();
        }
    }

    // ===== REGISTRO ESTUDIANTE =====
    static void registrarEstudiante() {
        System.out.println("\n--- REGISTRO DE ESTUDIANTE ---");
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        System.out.print("Cédula: ");
        String cedula = sc.nextLine();
        System.out.print("Correo: ");
        String correo = sc.nextLine();
        System.out.print("Teléfono: ");
        String telf = sc.nextLine();
        System.out.print("Dirección: ");
        String dir = sc.nextLine();
        System.out.print("Paralelo (A/B/C): ");
        String paralelo = sc.nextLine();
        System.out.print("Usuario: ");
        String usr = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        Estudiante nuevo = new Estudiante(nombre, cedula, correo, usr, pass, paralelo);
        if (gestorUsuarios.registrarUsuario(nuevo)) {
            System.out.println("Registro exitoso. Ya puedes iniciar sesión.");
        }
    }

    // ===== MENÚ ESTUDIANTE =====
    static void menuEstudiante() {
        int opc;
        do {
            System.out.println("\n--- MENÚ ESTUDIANTE ---");
            System.out.println("1. Configurar Ecosistema");
            System.out.println("2. Ejecutar Turno");
            System.out.println("3. Terminar Simulación");
            System.out.println("4. Cerrar Sesión");
            System.out.print("Seleccione: ");
            opc = leerEntero();

            switch(opc) {
                case 1 -> configurarEscenario();
                case 2 -> {
                    if (motorSimulacion != null) motorSimulacion.ejecutarTurno();
                    else System.out.println("Primero configura el ecosistema.");
                }
                case 3 -> {
                    if (motorSimulacion != null) motorSimulacion.terminarManualmente();
                    motorSimulacion = null;
                }
                case 4 -> {
                    estudianteActivo = null;
                    motorSimulacion = null;
                    System.out.println("Sesión cerrada.");
                }
                default -> System.out.println("Opción inválida.");
            }
        } while(opc != 4);
    }

    static void configurarEscenario() {
        System.out.print("Número de turnos: ");
        int tiempoMax = leerEntero();
        motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(estudianteActivo, tiempoMax);
        motorSimulacion.iniciar();
        System.out.println("Ecosistema inicializado.");
    }

    // ===== MENÚ PROFESOR =====
    static void menuProfesor() {
        int opc;
        do {
            System.out.println("\n--- MENÚ PROFESOR ---");
            System.out.println("1. Listar Estudiantes Paralelo A");
            System.out.println("2. Listar Estudiantes Paralelo B");
            System.out.println("3. Listar Estudiantes Paralelo C");
            System.out.println("4. Cerrar Sesión");
            System.out.print("Seleccione: ");
            opc = leerEntero();

            switch(opc) {
                case 1 -> System.out.println(gestorUsuarios.obtenerAlumnosPorParalelo("A"));
                case 2 -> System.out.println(gestorUsuarios.obtenerAlumnosPorParalelo("B"));
                case 3 -> System.out.println(gestorUsuarios.obtenerAlumnosPorParalelo("C"));
                case 4 -> System.out.println("Sesión cerrada.");
                default -> System.out.println("Opción inválida.");
            }
        } while(opc != 4);
    }

    // ===== VALIDACIÓN =====
    static int leerEntero() {
        while (!sc.hasNextInt()) {
            System.out.println("Error: ingrese un número válido.");
            sc.next();
        }
        int num = sc.nextInt();
        sc.nextLine();
        return num;
    }
}

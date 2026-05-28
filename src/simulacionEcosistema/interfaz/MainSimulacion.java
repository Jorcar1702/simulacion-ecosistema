package simulacionEcosistema.interfaz;

import simulacionEcosistema.negocio.*;
import simulacionEcosistema.modelo.*;
import java.util.Scanner;

public class MainSimulacion {
    static Scanner sc = new Scanner(System.in);
    static GestorUsuario gestorUsuarios = new GestorUsuario();
    static GestorSimulacion motorSimulacion = null;
    static Estudiante estudianteActivo = null;

    public static void main(String[] args) {
        GeneradorEcosistema generador = new GeneradorEcosistema();
        gestorUsuarios = generador.getGestorUsuario();
        int opc;
        do {
            menuLogin();
            opc = leerEntero();
            switch(opc){
                case 1:{
                    iniciarSesion();
                }break;
                case 2: {
                    registrarEstudiante();
                }break;
                case 3:{
                    registrarAdministrador();
                }break;
                case 4:{
                    System.out.println("Gracias por usar el simulador de ecosistemas.");
                }break;
                default: System.out.println("Opción inválida.");
            }
        } while(opc != 4);
    }

    // ===== MENÚS =====
    public static void menuLogin(){
        System.out.println("\n=== SIMULADOR DE ECOSISTEMAS ===");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse como Estudiante");
        System.out.println("3. Registrarse como Administrador");
        System.out.println("4. Salir");
        System.out.print("Seleccione: ");
    }

    public static void menuEstudiante(){
        System.out.println("\n========== MENÚ ESTUDIANTE ==========");
        System.out.println("1. Configurar Ecosistema");
        System.out.println("2. Agregar Especie");
        System.out.println("3. Definir Interacción");
        System.out.println("4. Iniciar Simulación");
        System.out.println("5. Ejecutar Turno");
        System.out.println("6. Ver Estado del Ecosistema");
        System.out.println("7. Ver Historial");
        System.out.println("8. Guardar Simulación");
        System.out.println("9. Exportar Reporte");
        System.out.println("10. Terminar Simulación");
        System.out.println("11. Cerrar Sesión");
        System.out.print("Seleccione: ");
    }

    static void menuProfesor(){
        System.out.println("\n--- MENÚ PROFESOR ---");
        System.out.println("1. Listar estudiantes por paralelo");
        System.out.println("2. Buscar estudiante por cédula");
        System.out.println("3. Ver logros de un estudiante");
        System.out.println("4. Dar de baja estudiante");
        System.out.println("5. Generar reporte global de simulaciones");
        System.out.println("6. Configurar parámetros globales del ecosistema");
        System.out.println("7. Exportar listado de estudiantes");
        System.out.println("8. Ver ranking de estudiantes");
        System.out.println("9. Cerrar Sesión");
        System.out.print("Seleccione: ");
    }

    // ===== LOGIN =====
    public static void iniciarSesion(){
            System.out.print("Usuario: ");
            String usr = sc.nextLine();
            System.out.print("Contraseña: ");
            String pass = sc.nextLine();
            Usuario u = gestorUsuarios.buscarUsuarioParaLogin(usr, pass);
            if(u == null){
                System.out.println("Credenciales inválidas.");
                return;
            }
            if(u instanceof Administrador){
                System.out.println("Bienvenido Administrador.");
                menuProfesorOpciones();
            }else if(u instanceof Estudiante){
                estudianteActivo = (Estudiante) u;
                System.out.println(
                        "Sesión iniciada como Estudiante: " + estudianteActivo.getNombreCompleto());
                menuEstudianteOpciones();
            }
        }

    // ===== REGISTRO ESTUDIANTE =====
    public static void registrarEstudiante(){
        System.out.println("\n--- REGISTRO ESTUDIANTE ---");
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        System.out.print("Cédula: ");
        String cedula = sc.nextLine();
        System.out.print("Correo: ");
        String correo = sc.nextLine();;
        System.out.print("Usuario: ");
        String usr = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();
        System.out.print("Paralelo: ");
        String paralelo = sc.nextLine();

        Estudiante nuevo = new Estudiante(nombre, cedula, correo,  usr, pass, paralelo);
        gestorUsuarios.registrarUsuario(nuevo);
        System.out.println("Registro de estudiante exitoso.");
    }

    // ===== REGISTRO ADMINISTRADOR =====
    public static void registrarAdministrador(){
        System.out.println("\n--- REGISTRO ADMINISTRADOR ---");
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
        System.out.print("Usuario: ");
        String usr = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        Administrador nuevo = new Administrador(nombre, cedula, correo, telf, usr, pass);
        gestorUsuarios.registrarUsuario(nuevo);
        System.out.println("Registro de administrador exitoso.");
    }

    // ===== OPCIONES ESTUDIANTE =====
    public static void menuEstudianteOpciones(){
        int opc;
        do {
            menuEstudiante();
            opc = leerEntero();
            switch(opc){
                case 1:{
                    configurarEscenario();
                }break;

                case 2:{
                    if(motorSimulacion == null){
                        System.out.println("Primero configure el ecosistema.");
                        break;
                    }
                    System.out.print("Nombre especie: ");
                    String nombre = sc.nextLine();
                    System.out.print("Tipo: ");
                    String tipo = sc.nextLine();
                    System.out.print("Cantidad inicial: ");
                    int cantidad = leerEntero();
                    System.out.print("Capacidad máxima: ");
                    int capacidad = leerEntero();
                    Poblacion p = GeneradorEcosistema.crearEspecieConPoblacion(nombre, tipo, cantidad, capacidad);
                    motorSimulacion.registrarPoblacion(p);
                }break;
                case 3:{
                    if(motorSimulacion == null){
                        System.out.println("Primero configure el ecosistema.");
                        break;
                    }
                    System.out.print("Depredador: ");
                    String depredador = sc.nextLine();
                    System.out.print("Presa: ");
                    String presa = sc.nextLine();
                    System.out.print("Factor de caza: ");
                    double factor = sc.nextDouble();
                    System.out.print("Eficiencia: ");
                    double eficiencia = sc.nextDouble();
                    sc.nextLine();
                    Interaccion inter = new Interaccion(depredador, presa, factor, eficiencia);

                    motorSimulacion.registrarInteraccion(inter);

                    System.out.println("Interacción registrada.");

                }break;

                case 4:{
                    if(motorSimulacion != null){
                        motorSimulacion.iniciar();
                        System.out.println("Simulación iniciada.");
                    }else{
                        System.out.println("Configure el ecosistema primero.");
                    }
                }break;
                case 5:{
                    if(motorSimulacion != null){
                        motorSimulacion.ejecutarTurno();
                    }else{
                        System.out.println("No existe simulación activa.");
                    }
                }break;
                case 6:{
                    if(motorSimulacion != null){
                        System.out.println(motorSimulacion.getSimulacion());
                    }else{
                        System.out.println("No existe simulación activa.");
                    }
                }break;
                case 7:{
                    System.out.println("\n========== HISTORIAL ==========");
                    if(estudianteActivo.getHistorialSimulaciones().isEmpty()){
                        System.out.println("No existen simulaciones guardadas.");
                    }else{
                        int contador = 1;
                        for(Simulacion s :
                                estudianteActivo
                                        .getHistorialSimulaciones()){
                            System.out.println("\nSimulación #" + contador);
                            System.out.println("Turnos máximos: " + s.getTiempoTotal());
                            System.out.println("Turno actual: " + s.getTurnoActual());
                            System.out.println("Estado: " + (s.isActiva() ? "Activa" : "Finalizada"));

                            System.out.println("Cantidad de poblaciones: " + s.getListaPoblaciones().size());
                            contador++;
                        }
                    }
                }break;
                case 8:{
                    if(motorSimulacion != null){
                        estudianteActivo.registrarSimulacion(motorSimulacion.getSimulacion());
                        System.out.println(
                                "Simulación guardada."
                        );
                    }
                }break;
                case 9:{
                    if(estudianteActivo.getHistorialSimulaciones().isEmpty()){
                        System.out.println("No existen simulaciones en el historial.");
                        break;
                    }
                    System.out.println("\n========== REPORTE DE SIMULACIONES ==========");
                    for(Simulacion s : estudianteActivo.getHistorialSimulaciones()){

                        System.out.println("\nTurnos máximos: " + s.getTiempoTotal());
                        System.out.println("Estado: " + (s.isActiva() ? "Activa" : "Finalizada"));
                        System.out.println("Turno actual: " + s.getTurnoActual());
                        System.out.println("\n----- POBLACIONES -----");

                        for(Poblacion p : s.getListaPoblaciones()){

                            System.out.println("Especie: " + p.getEspecie().getNombre());
                            System.out.println("Tipo: " + p.getEspecie().getTipo());

                            System.out.println("Cantidad: " + p.getCantidad());

                            System.out.println("----------------------");
                        }
                    }

                }break;

                case 10:{
                    if(motorSimulacion != null){
                        motorSimulacion.terminarManualmente();
                        motorSimulacion = null;
                        System.out.println("Simulación terminada.");
                    }
                }break;

                case 11:{
                    estudianteActivo = null;
                    motorSimulacion = null;
                    System.out.println("Sesión cerrada.");

                }break;

                default:{
                    System.out.println(
                            "Opción inválida."
                    );
                }
            }

        }while(opc != 11);
    }

    public static void configurarEscenario(){
        if(estudianteActivo == null){
            System.out.println("Error: Debes iniciar sesión como estudiante antes de configurar el ecosistema.");
            return;
        }

        System.out.print("Número de turnos: ");
        int tiempoMax = leerEntero();
        motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(estudianteActivo, tiempoMax);
        motorSimulacion.iniciar();
        System.out.println("Ecosistema inicializado.");
    }

    // ===== OPCIONES PROFESOR =====
    public static void menuProfesorOpciones(){
        int opc;
        do {
            menuProfesor();
            opc = leerEntero();
            switch(opc){
                case 1: {
                    System.out.print("Paralelo: ");
                    String paralelo = sc.nextLine();
                    System.out.println(gestorUsuarios.obtenerAlumnosPorParalelo(paralelo));
                }break;
                case 2: {
                    System.out.print("Cédula: ");
                    String cedula = sc.nextLine();
                    Usuario u = gestorUsuarios.buscarPorCedula(cedula);
                    System.out.println(u!=null ? u : "No encontrado.");
                }break;
                case 3: {
                    System.out.print("Cédula: ");
                    String cedula = sc.nextLine();
                    Usuario u = gestorUsuarios.buscarPorCedula(cedula);
                    if(u instanceof Estudiante est) System.out.println("Logros: " + est.obtenerRecompensa());
                }break;
                case 4: {
                    System.out.print("Cédula: ");
                    String cedula = sc.nextLine();
                    System.out.println(gestorUsuarios.darDeBaja(cedula));
                }break;
                case 5 : {
                    System.out.println("=== REPORTE GLOBAL DE SIMULACIONES ===");
                    for (Usuario u : gestorUsuarios.getListaUsuarios()) {
                        if (u instanceof Estudiante est) {
                            System.out.println(est.getNombreCompleto() +
                                    " | Paralelo: " + est.getParalelo() +
                                    " | Simulaciones exitosas: " + est.getSimulacionesExitosas() +
                                    " | Rango: " + est.obtenerRecompensa());
                        }
                    }

                }break;
                case 6 :{
                    if (motorSimulacion == null) {
                        System.out.print("Número de turnos para la simulación global: ");
                        int tiempoMax = leerEntero();
                        motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(null, tiempoMax);
                        motorSimulacion.iniciar();
                    }
                    System.out.print("Nueva tasa de regeneración vegetal: ");
                    int tasa = leerEntero();
                    motorSimulacion.setRegeneracionVegetal(tasa);
                    System.out.println("Parámetros globales configurados.");
                }break;
                case 7: {
                    System.out.println("=== LISTADO DE ESTUDIANTES ===");
                    for (Usuario u : gestorUsuarios.getListaUsuarios()) {
                        if (u instanceof Estudiante est) {
                            System.out.println(est.getNombreCompleto() + " | Paralelo: " + est.getParalelo());
                        }
                    }
                    break;
                }
                case 8: {
                    System.out.println("=== RANKING DE ESTUDIANTES ===");
                    gestorUsuarios.getListaUsuarios().stream()
                            .filter(u -> u instanceof Estudiante)
                            .map(u -> (Estudiante) u)
                            .sorted((a,b) -> Integer.compare(b.getSimulacionesExitosas(), a.getSimulacionesExitosas()))
                            .forEach(est -> System.out.println(est.getNombreCompleto() +
                                    " | Simulaciones exitosas: " + est.getSimulacionesExitosas() +
                                    " | Rango: " + est.obtenerRecompensa()));
                    break;
                }
                case 9 :{
                    System.out.println("Sesión cerrada.");
                }break;
                default :
                    System.out.println("Opción inválida.");
            }
        } while(opc!=9);
    }

    // ===== VALIDACIONES =====
    public static int leerEntero(){
        while(!sc.hasNextInt()){
            System.out.println("Error: ingrese un número válido.");
            sc.next();
        }
        int num = sc.nextInt();
        sc.nextLine();
        return num;
    }
}
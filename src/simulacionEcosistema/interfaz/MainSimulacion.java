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
        System.out.println("\n--- MENÚ ESTUDIANTE ---");
        System.out.println("1. Agregar Especie");
        System.out.println("2. Definir Interaccion");
        System.out.println("3. Iniciar Simulación");
        System.out.println("4. Configurar Ecosistema");
        System.out.println("5. Ejecutar Turno");
        System.out.println("6. Ejecutar todos los turnos restantes");
        System.out.println("7. Ver estado actual del ecosistema");
        System.out.println("8. Guardar Simulacion actual");
        System.out.println("9. Ver Historial de Simulaciones");
        System.out.println("10. Cargar Simulación");
        System.out.println("11. Exportar reporte");
        System.out.println("12. Terminar Simulación");
        System.out.println("13. Cerrar Sesión");
        System.out.println("14. Ver mis logros");
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
        System.out.println("9. Ver estudiantes por rango/logro");
        System.out.println("10. Ver simulaciones realizadas por fecha");
        System.out.println("11. Buscar estudiante por nombre");
        System.out.println("12. Ver estadísticas generales");
        System.out.println("13. Cerrar Sesión");
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
            menuProfesorOpciones();
        } else if(u instanceof Estudiante){
            estudianteActivo = (Estudiante) u;

            // 🔹 Inicializar motorSimulacion aquí
            System.out.print("Número de turnos para la simulación: ");
            int tiempoMax = leerEntero();
            motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(estudianteActivo, tiempoMax);
            try {
                motorSimulacion.iniciar();
            } catch (Exception e) {
                System.out.println("Error al iniciar simulación: " + e.getMessage());
                return;
            }

            System.out.println(" Sesión iniciada como Estudiante: " + estudianteActivo.getNombreCompleto());
            System.out.println("Ecosistema inicializado con especies base.");
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
        System.out.print("Paralelo: ");
        String paralelo = sc.nextLine();
        System.out.print("Usuario: ");
        String usr = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        try {
            Estudiante nuevo = new Estudiante(nombre, cedula, correo, usr, pass, paralelo);
            if (gestorUsuarios.registrarUsuario(nuevo)) {
                System.out.println("Registro de estudiante exitoso.");
            }
        } catch (Exception e) {
            System.out.println("Error al registrar estudiante: " + e.getMessage());
        }
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
        System.out.print("Usuario: ");
        String usr = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        try {
            Administrador nuevo = new Administrador(nombre, cedula, correo, usr, pass);
            if (gestorUsuarios.registrarUsuario(nuevo)) {
                System.out.println("Registro de administrador exitoso.");
            }
        } catch (Exception e) {
            System.out.println("Error al registrar administrador: " + e.getMessage());
        }
    }

    // ===== OPCIONES ESTUDIANTE =====
    public static void menuEstudianteOpciones(){
        int opc;
        do {
            menuEstudiante();
            opc = leerEntero();
            switch(opc){
                case 1:{
                    System.out.print("Nombre de la especie: ");
                    String nombre = sc.nextLine();

                    System.out.print("Tipo (Herbívoro/Carnívoro/Planta): ");
                    String tipo = sc.nextLine();

                    System.out.print("Cantidad inicial: ");
                    int cantidad = leerEntero();

                    System.out.print("Capacidad máxima: ");
                    int capacidad = leerEntero();

                    Poblacion poblacion =
                            GeneradorEcosistema.crearEspecieConPoblacion(
                                    nombre,
                                    tipo,
                                    cantidad,
                                    capacidad
                            );

                    motorSimulacion.registrarPoblacion(poblacion);

                    System.out.println("Especie agregada.");
                }break;

                case 2:{
                    if(motorSimulacion == null){
                        System.out.println("No hay simulación activa. Configure el ecosistema primero.");
                        break;
                    }

                    System.out.print("Nombre del depredador: ");
                    String depredador = sc.nextLine();

                    System.out.print("Nombre de la presa: ");
                    String presa = sc.nextLine();

                    if(depredador.trim().equalsIgnoreCase(presa.trim())){
                        System.out.println("Una especie no puede ser depredadora de sí misma.");
                        break;
                    }

                    // Nota: se puede registrar más de una interacción para la misma especie,
                    // ya sea como depredador o como presa, para modelar cadenas alimenticias
                    // con varios niveles (ej.: Zorro -> Conejo -> Hierba, o dos depredadores
                    // distintos cazando la misma presa).
                    boolean existeDepredador = false, existePresa = false;
                    for (Poblacion p : motorSimulacion.getSimulacion().getPoblaciones()) {
                        if (p.getEspecie().getNombre().equalsIgnoreCase(depredador.trim())) existeDepredador = true;
                        if (p.getEspecie().getNombre().equalsIgnoreCase(presa.trim())) existePresa = true;
                    }
                    if(!existeDepredador || !existePresa){
                        System.out.println("Aviso: alguna de las especies indicadas todavía no tiene población registrada en este ecosistema.");
                    }

                    System.out.print("Factor de caza: ");
                    double factorCaza = sc.nextDouble();

                    System.out.print("Eficiencia de conversión: ");
                    double eficiencia = sc.nextDouble();
                    sc.nextLine();

                    Interaccion inter =
                            new Interaccion(
                                    depredador,
                                    presa,
                                    factorCaza,
                                    eficiencia
                            );

                    motorSimulacion.registrarInteraccion(inter);

                    System.out.println("Interacción registrada.");
                }break;

                case 3:{
                    if(estudianteActivo == null){
                        System.out.println("Debe iniciar sesión.");
                        break;
                    }
                    if(motorSimulacion != null && motorSimulacion.haySimulacionActiva()){
                        System.out.println("Ya tienes una simulación en curso. Debes terminarla (opción 12) antes de iniciar otra.");
                        break;
                    }

                    System.out.print("Número de turnos: ");
                    int tiempoMax = leerEntero();

                    motorSimulacion =
                            GeneradorEcosistema.crearEcosistemaBase(
                                    estudianteActivo,
                                    tiempoMax
                            );

                    try {
                        motorSimulacion.iniciar();
                        System.out.println("Simulación iniciada.");
                    } catch (Exception e) {
                        System.out.println("Error al iniciar la simulación: " + e.getMessage());
                    }
                }break;

                case 4:{
                    configurarEscenario();
                }break;

                case 5:{
                    if(motorSimulacion != null){
                        try {
                            motorSimulacion.ejecutarTurno();
                        } catch (Exception e) {
                            System.out.println("Error al ejecutar turno: " + e.getMessage());
                        }
                    }else{
                        System.out.println("No hay simulación activa.");
                    }
                }break;

                case 6:{
                    if(motorSimulacion != null){
                        try {
                            motorSimulacion.ejecutarTodosLosTurnos();
                        } catch (Exception e) {
                            System.out.println("Error al ejecutar todos los turnos: " + e.getMessage());
                        }
                    }else{
                        System.out.println("No hay simulación activa.");
                    }
                }break;

                case 7:{
                    if(motorSimulacion != null){
                        System.out.println(
                                motorSimulacion.getSimulacion()
                        );
                    }else{
                        System.out.println("No hay simulación activa.");
                    }
                }break;

                case 8:{
                    if(motorSimulacion != null){

                        estudianteActivo.registrarSimulacion(
                                motorSimulacion.getSimulacion()
                        );

                        System.out.println("Simulación guardada.");

                    }else{
                        System.out.println("No hay simulación activa.");
                    }
                }break;

                case 9:{
                    System.out.println("\n=== HISTORIAL DE SIMULACIONES ===");

                    if(estudianteActivo
                            .getHistorialSimulaciones()
                            .isEmpty()){

                        System.out.println(
                                "No existen simulaciones registradas todavía."
                        );

                    }else{

                        int i = 1;
                        for(Simulacion s :
                                estudianteActivo.getHistorialSimulaciones()){

                            System.out.println(
                                    "\n[" + i + "] Fecha: " + s.getFechaFormateada()
                                            + "\n    Turnos: " + s.getTurnoActual() + " / " + s.getTiempoTotal()
                                            + "\n    Estado: " + (s.isActiva() ? "Activa" : "Finalizada")
                                            + "\n    Resultado: " + s.getResultado()
                                            + "\n    " + s.getDuracionTurnoDescripcion()
                            );
                            i++;
                        }

                        System.out.print("\n¿Desea ver el detalle completo de alguna? (0 = No / N° de la lista): ");
                        int seleccion = leerEntero();
                        if(seleccion >= 1 && seleccion <= estudianteActivo.getHistorialSimulaciones().size()){
                            Simulacion elegida = estudianteActivo.getHistorialSimulaciones().get(seleccion - 1);
                            System.out.println("\n--- BITÁCORA DE TURNOS ---");
                            if(elegida.getBitacoraTurnos().isEmpty()){
                                System.out.println("No se registraron turnos ejecutados.");
                            } else {
                                for(String evento : elegida.getBitacoraTurnos()){
                                    System.out.println(evento);
                                }
                            }
                        }
                    }
                }break;

                case 10:{
                    System.out.println(
                            "Simulación cargada."
                    );
                }break;

                case 11:{
                    if(motorSimulacion == null){
                        System.out.println("No hay ninguna simulación para reportar. Debe iniciar una primero.");
                        break;
                    }

                    System.out.println("\n¿Qué tipo de reporte desea?");
                    System.out.println("1. Verlo en consola");
                    System.out.println("2. Exportarlo a un archivo de texto (.txt)");
                    System.out.print("Seleccione: ");
                    int tipoReporte = leerEntero();

                    String reporte = motorSimulacion.generarReporteTexto();

                    if(tipoReporte == 2){
                        System.out.print("Nombre del archivo (sin extensión): ");
                        String nombreArchivo = sc.nextLine();
                        if(nombreArchivo == null || nombreArchivo.isBlank()){
                            nombreArchivo = "reporte_simulacion";
                        }
                        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(nombreArchivo + ".txt"))) {
                            pw.print(reporte);
                            System.out.println("Reporte exportado exitosamente a " + nombreArchivo + ".txt");
                        } catch (java.io.IOException e) {
                            System.out.println("Error al exportar el reporte: " + e.getMessage());
                        }
                    } else {
                        System.out.println(reporte);
                    }
                }break;

                case 12:{
                    if(motorSimulacion != null){

                        motorSimulacion.terminarManualmente();
                        motorSimulacion = null;

                        System.out.println(
                                "Simulación terminada."
                        );

                    }else{
                        System.out.println(
                                "No hay simulación activa."
                        );
                    }
                }break;

                case 13:{
                    estudianteActivo = null;
                    motorSimulacion = null;

                    System.out.println(
                            "Sesión cerrada."
                    );
                }break;

                case 14: {
                    if (estudianteActivo != null) {
                        System.out.println(GestorLogros.obtenerLogrosPorEstudiante(estudianteActivo));
                    } else {
                        System.out.println("Debe iniciar sesión como estudiante para ver sus logros.");
                    }
                }break;

                default:{
                    System.out.println(
                            "Opción inválida."
                    );
                }
            }

        }while(opc != 13);
    }

    public static void configurarEscenario(){
        if(estudianteActivo == null){
            System.out.println("Error: Debes iniciar sesión como estudiante antes de configurar el ecosistema.");
            return;
        }
        if(motorSimulacion != null && motorSimulacion.haySimulacionActiva()){
            System.out.println("Ya tienes una simulación en curso. Debes terminarla antes de configurar una nueva.");
            return;
        }

        System.out.print("Número de turnos: ");
        int tiempoMax = leerEntero();
        motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(estudianteActivo, tiempoMax);
        try {
            motorSimulacion.iniciar();
            System.out.println("Ecosistema inicializado.");
        } catch (Exception e) {
            System.out.println("Error al iniciar la simulación: " + e.getMessage());
        }
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
                    if(u instanceof Estudiante est) {
                        System.out.println("\n=== LOGROS DE " + est.getNombreCompleto().toUpperCase() + " ===");
                        System.out.println("Paralelo: " + est.getParalelo());
                        System.out.println("Simulaciones exitosas: " + est.getSimulacionesExitosas());
                        System.out.println("Rango actual: " + est.obtenerRecompensa());
                        System.out.println("Simulaciones registradas en historial: " + est.getHistorialSimulaciones().size());
                        int faltan = est.simulacionesParaSiguienteRango();
                        if (faltan > 0) {
                            System.out.println("Le faltan " + faltan + " simulación(es) exitosa(s) para el siguiente rango.");
                        } else {
                            System.out.println("¡Ha alcanzado el rango máximo: Maestro de la Biósfera!");
                        }
                    } else {
                        System.out.println("No se encontró un estudiante con esa cédula.");
                    }
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
                        try {
                            motorSimulacion.iniciar();
                        } catch (Exception e) {
                            System.out.println("Error al iniciar la simulación global: " + e.getMessage());
                        }
                    }
                    System.out.print("Nueva tasa de regeneración vegetal: ");
                    int tasa = leerEntero();
                    try {
                        motorSimulacion.setRegeneracionVegetal(tasa);
                        System.out.println("Parámetros globales configurados.");
                    } catch (Exception e) {
                        System.out.println("Error al configurar parámetros globales: " + e.getMessage());
                    }
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
                case 9: {
                    System.out.print("Rango a buscar (Investigador Novato / Protector del Entorno / Guardián del Ecosistema / Maestro de la Biósfera): ");
                    String rango = sc.nextLine();
                    System.out.println(gestorUsuarios.obtenerEstudiantesPorRango(rango));
                }break;
                case 10: {
                    System.out.print("Fecha a consultar (dd/MM/yyyy): ");
                    String fechaTexto = sc.nextLine();
                    try {
                        java.time.LocalDate fecha = java.time.LocalDate.parse(
                                fechaTexto.trim(),
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        );
                        System.out.println(gestorUsuarios.obtenerSimulacionesPorFecha(fecha));
                    } catch (Exception e) {
                        System.out.println("Formato de fecha inválido. Use dd/MM/yyyy.");
                    }
                }break;
                case 11: {
                    System.out.print("Nombre o parte del nombre a buscar: ");
                    String nombreBuscado = sc.nextLine();
                    System.out.println(gestorUsuarios.buscarPorNombre(nombreBuscado));
                }break;
                case 12: {
                    System.out.println(gestorUsuarios.obtenerEstadisticasGenerales());
                }break;
                case 13 :{
                    System.out.println("Sesión cerrada.");
                }
                default :
                    System.out.println("Opción inválida.");
            }
        } while(opc!=13);
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
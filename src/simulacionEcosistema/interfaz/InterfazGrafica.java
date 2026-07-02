package simulacionEcosistema.interfaz;

import simulacionEcosistema.modelo.*;
import simulacionEcosistema.negocio.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interfaz gráfica principal que reemplaza la interacción por consola.
 * Contiene todas las pantallas necesarias dentro de un único JFrame usando CardLayout.
 * Implementa las reglas indicadas: sin input dialogs, uso de JTable/JList, JOptionPane
 * solo para errores e información, reuse de gestores y lógica de negocio.
 */
public class InterfazGrafica {
    // Estado compartido (no estático): gestor de usuarios, simulación y usuario en sesión
    private GestorUsuario gestorUsuarios;
    private GestorSimulacion motorSimulacion;
    private Estudiante estudianteActivo;
    private Administrador administradorActivo;

    // Referencias a los paneles principales del área de estudiante para poder
    // refrescarlos en el momento en que se muestran (evita leer datos de sesiones previas)
    private ViewParamsPanel viewParamsPanel;
    private AddSpeciesPanel addSpeciesPanel;
    private InteractionsPanel interactionsPanel;
    private SimExecutePanel simExecutePanel;
    private SavePanel savePanel;
    private ExportPanel exportPanel;
    private AchievementsPanel achievementsPanel;

    private JFrame frame;
    private CardLayout mainLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    // Area internals para el espacio del estudiante (se declaran a nivel de la clase
    // para que los métodos auxiliares puedan referenciarlos al mostrar tarjetas)
    private CardLayout areaLayout;
    private JPanel areaPanel;

    // Panels names
    private static final String P_LOGIN = "LOGIN";
    private static final String P_REG_ST = "REG_ST";
    private static final String P_REG_ADM = "REG_ADM";
    private static final String P_STUDENT = "STUDENT";
    private static final String P_ADMIN = "ADMIN";

    public InterfazGrafica() {
        // Inicializar gestores (usar GeneradorEcosistema para precargar usuarios por defecto)
        try {
            GeneradorEcosistema gen = new GeneradorEcosistema();
            gestorUsuarios = gen.getGestorUsuario();
        } catch (Exception e) {
            // si falla, crear uno vacío
            gestorUsuarios = new GestorUsuario();
            System.out.println("Advertencia: no se pudieron cargar usuarios por defecto: " + e.getMessage());
        }

        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        frame = new JFrame("Simulador de Ecosistemas - GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);

        mainLayout = new CardLayout();
        mainPanel = new JPanel(mainLayout);

        // Crear pantallas
        loginPanel = new LoginPanel();
        mainPanel.add(loginPanel, P_LOGIN);
        mainPanel.add(new RegistroPanel(true), P_REG_ST);
        mainPanel.add(new RegistroPanel(false), P_REG_ADM);
        mainPanel.add(new StudentMainPanel(), P_STUDENT);
        mainPanel.add(new AdminMainPanel(), P_ADMIN);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

        showPanel(P_LOGIN);
    }

    private void showPanel(String name) {
        mainLayout.show(mainPanel, name);
    }

    // Refresca todos los paneles del área de estudiante que dependen del estudiante activo
    private void refreshStudentPanels() {
        if (viewParamsPanel != null) viewParamsPanel.refresh();
        if (addSpeciesPanel != null) addSpeciesPanel.refreshTable();
        if (interactionsPanel != null) interactionsPanel.refresh();
        if (simExecutePanel != null) simExecutePanel.refresh();
        if (savePanel != null) savePanel.refresh();
        // NO refrescar exportPanel automáticamente; solo al usuario presionar el botón
        // if (exportPanel != null) exportPanel.refresh();
        if (achievementsPanel != null) achievementsPanel.refresh();
    }

    // Muestra una tarjeta del área de estudiante, refrescando datos dependientes de sesión antes
    private void showStudentArea(String key) {
        // refrescar solo la tarjeta solicitada para ahorrar trabajo
        switch (key) {
            case "VIEW_PARAMS": if (viewParamsPanel!=null) viewParamsPanel.refresh(); break;
            case "ADD_SPECIES": if (addSpeciesPanel!=null) addSpeciesPanel.refreshTable(); break;
            case "INTERACTIONS": if (interactionsPanel!=null) interactionsPanel.refresh(); break;
            case "SIM_CONTROL": if (simExecutePanel!=null) simExecutePanel.refresh(); break;
            case "SAVE": if (savePanel!=null) savePanel.refresh(); break;
            case "EXPORT": if (exportPanel!=null) exportPanel.refresh(); break; // Ahora refresh() no muestra JOptionPane
            case "ACHIEVEMENTS": if (achievementsPanel!=null) achievementsPanel.refresh(); break;
            default: break;
        }
        areaLayout.show(areaPanel, key);
    }

    // ----------------------- Panel Login -----------------------
    private class LoginPanel extends JPanel {
        private JTextField tfUser;
        private JPasswordField pfPass;

        public LoginPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(230, 245, 233)); // light green

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(new Color(27, 94, 32));
            header.setBorder(new EmptyBorder(10, 20, 10, 20));
            JLabel title = new JLabel("\uD83D\uDC3F  Simulador de Ecosistemas", SwingConstants.LEFT);
            title.setForeground(Color.WHITE);
            title.setFont(new Font("SansSerif", Font.BOLD, 24));
            header.add(title, BorderLayout.WEST);

            frame.add(header, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setBackground(getBackground());
            center.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8, 8, 8, 8);
            c.fill = GridBagConstraints.HORIZONTAL;

            JLabel lUser = new JLabel("Usuario:");
            lUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
            c.gridx = 0; c.gridy = 0; center.add(lUser, c);
            tfUser = new JTextField(20); c.gridx = 1; center.add(tfUser, c);

            JLabel lPass = new JLabel("Contraseña:");
            lPass.setFont(new Font("SansSerif", Font.PLAIN, 14));
            c.gridy = 1; c.gridx = 0; center.add(lPass, c);
            pfPass = new JPasswordField(20); c.gridx = 1; center.add(pfPass, c);

            JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            botones.setBackground(getBackground());
            JButton btnLogin = createStyledButton("Iniciar sesión");
            btnLogin.addActionListener(e -> doLogin());
            JButton btnRegEst = createStyledButton("Registrarse como Estudiante");
            btnRegEst.addActionListener(e -> showPanel(P_REG_ST));
            JButton btnRegAdm = createStyledButton("Registrarse como Administrador");
            btnRegAdm.addActionListener(e -> showPanel(P_REG_ADM));
            JButton btnSalir = createStyledButton("Salir");
            btnSalir.setBackground(new Color(198, 40, 40));
            btnSalir.addActionListener(e -> System.exit(0));

            botones.add(btnLogin); botones.add(btnRegEst); botones.add(btnRegAdm); botones.add(btnSalir);

            c.gridx = 0; c.gridy = 2; c.gridwidth = 2; center.add(botones, c);

            add(center, BorderLayout.CENTER);
        }

        public void clearLoginFields() {
            tfUser.setText("");
            pfPass.setText("");
        }

        private void doLogin() {
            String usr = tfUser.getText().trim();
            String pass = new String(pfPass.getPassword());
            if (usr.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Usuario y contraseña requeridos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Usuario u = gestorUsuarios.buscarUsuarioParaLogin(usr, pass);
            if (u == null) {
                JOptionPane.showMessageDialog(frame, "Credenciales inválidas.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (u instanceof Administrador) {
                administradorActivo = (Administrador) u;
                estudianteActivo = null;
                showPanel(P_ADMIN);
            } else if (u instanceof Estudiante) {
                estudianteActivo = (Estudiante) u;
                administradorActivo = null;

                // Si el estudiante tiene una simulación en curso y está activa, la retomamos
                Simulacion s = estudianteActivo.getSimulacionEnCurso();
                if (s != null && s.isActiva()) {
                    motorSimulacion = new GestorSimulacion(s, estudianteActivo);
                } else if (s != null && !s.isActiva()) {
                    // Hay una simulación inactiva; envolver en gestor pero no activar
                    motorSimulacion = new GestorSimulacion(s, estudianteActivo);
                } else {
                    // No hay simulación previa; crear una nueva lista para agregar especies
                    // Usar parámetros del paralelo del estudiante y 10 turnos por defecto
                    int alimento = 1000;
                    int consumo = 2;
                    int regen = 50;

                    String paralelo = estudianteActivo.getParalelo();
                    java.util.Map<String, Integer> paramsDelProfesor = GeneradorEcosistema.obtenerConfiguracion(paralelo);
                    if (paramsDelProfesor != null && !paramsDelProfesor.isEmpty()) {
                        // Los parámetros están configurados por el profesor
                        Integer a = paramsDelProfesor.get("Alimento Disponible");
                        Integer c = paramsDelProfesor.get("Tasa de Consumo");
                        Integer r = paramsDelProfesor.get("Regeneración Vegetal");
                        if (a != null) alimento = a;
                        if (c != null) consumo = c;
                        if (r != null) regen = r;
                    }

                    Entorno entorno = new Entorno(alimento, consumo, regen);
                     Simulacion nuevaSim = new Simulacion(10, entorno);

                     // Pre-llenar con especies por defecto (Hierba, Conejo Silvestre y León)
                     Especie hierba = new Especie("Hierba", "Planta", 0.40, 0.10);
                     Especie conejo = new Especie("Conejo Silvestre", "Herbívoro", 0.25, 0.15);
                     Especie leon = new Especie("León", "Carnívoro", 0.20, 0.20);

                     nuevaSim.getEspecies().add(hierba);
                     nuevaSim.getEspecies().add(conejo);
                     nuevaSim.getEspecies().add(leon);

                     nuevaSim.getPoblaciones().add(new Poblacion(100, 400, hierba));
                     nuevaSim.getPoblaciones().add(new Poblacion(20, 80, conejo));
                     nuevaSim.getPoblaciones().add(new Poblacion(5, 20, leon));

                     // Pre-llenar con interacciones por defecto
                     nuevaSim.getInteracciones().add(new Interaccion("Conejo Silvestre", "Hierba", 0.30, 1.0));
                     nuevaSim.getInteracciones().add(new Interaccion("León", "Conejo Silvestre", 0.25, 1.0));

                    estudianteActivo.setSimulacionEnCurso(nuevaSim);
                    motorSimulacion = new GestorSimulacion(nuevaSim, estudianteActivo);

                    // Refrescar vistas del estudiante antes de mostrarlas
                    refreshStudentPanels();
                    showPanel(P_STUDENT);
                }

                // Refrescar vistas del estudiante antes de mostrarlas
                refreshStudentPanels();
                showPanel(P_STUDENT);
            }
        }
    }

    // ----------------------- Panel Registro (student/admin) -----------------------
    private class RegistroPanel extends JPanel {
        private boolean esEstudiante;
        private JTextField tfNombre, tfCedula, tfCorreo, tfParalelo, tfUsuario;
        private JPasswordField pfPassword;
        private JButton btnEnviar, btnVolver;

        public RegistroPanel(boolean esEstudiante) {
            this.esEstudiante = esEstudiante;
            setLayout(new BorderLayout());
            setBackground(new Color(239, 235, 233));
            JLabel titulo = new JLabel(esEstudiante ? "Registro Estudiante" : "Registro Administrador");
            titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
            titulo.setBorder(new EmptyBorder(10, 10, 10, 10));
            add(titulo, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(new EmptyBorder(20, 20, 20, 20));
            form.setBackground(getBackground());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6,6,6,6);
            c.fill = GridBagConstraints.HORIZONTAL;

            int row = 0;
            form.add(new JLabel("Nombre completo:"), grid(c,0,row)); tfNombre = new JTextField(25); form.add(tfNombre, grid(c,1,row++));
            form.add(new JLabel("Cédula:"), grid(c,0,row)); tfCedula = new JTextField(25); form.add(tfCedula, grid(c,1,row++));
            form.add(new JLabel("Correo:"), grid(c,0,row)); tfCorreo = new JTextField(25); form.add(tfCorreo, grid(c,1,row++));
            if (esEstudiante) { form.add(new JLabel("Paralelo:"), grid(c,0,row)); tfParalelo = new JTextField(25); form.add(tfParalelo, grid(c,1,row++)); }
            form.add(new JLabel("Usuario:"), grid(c,0,row)); tfUsuario = new JTextField(25); form.add(tfUsuario, grid(c,1,row++));
            form.add(new JLabel("Contraseña:"), grid(c,0,row)); pfPassword = new JPasswordField(25); form.add(pfPassword, grid(c,1,row++));

            add(form, BorderLayout.CENTER);

            JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER)); botones.setBackground(getBackground());
            btnEnviar = createStyledButton("Registrar");
            btnVolver = createStyledButton("Volver"); btnVolver.setBackground(new Color(158, 158, 158));
            btnVolver.addActionListener(e -> showPanel(P_LOGIN));
            btnEnviar.addActionListener(e -> enviarRegistro());
            botones.add(btnEnviar); botones.add(btnVolver);
            add(botones, BorderLayout.SOUTH);
        }

        private GridBagConstraints grid(GridBagConstraints c, int x, int y) {
            GridBagConstraints nc = (GridBagConstraints) c.clone(); nc.gridx = x; nc.gridy = y; return nc;
        }

        private void enviarRegistro() {
            String nombre = tfNombre.getText().trim();
            String cedula = tfCedula.getText().trim();
            String correo = tfCorreo.getText().trim();
            String paralelo = esEstudiante ? tfParalelo.getText().trim() : null;
            String usuario = tfUsuario.getText().trim();
            String pass = new String(pfPassword.getPassword());

            if (nombre.isEmpty() || cedula.isEmpty() || correo.isEmpty() || usuario.isEmpty() || pass.isEmpty() || (esEstudiante && paralelo.isEmpty())) {
                JOptionPane.showMessageDialog(frame, "Todos los campos son requeridos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (esEstudiante) {
                    Estudiante nuevo = new Estudiante(nombre, cedula, correo, usuario, pass, paralelo);
                    boolean ok = gestorUsuarios.registrarUsuario(nuevo);
                    if (!ok) {
                        JOptionPane.showMessageDialog(frame, "La cédula o el nombre de usuario ya están registrados.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // keep data
                    }
                    // detect replaced fields
                    String replaced = detectReplacementsForStudent(nuevo, nombre, correo, usuario);
                    JOptionPane.showMessageDialog(frame, "Registro exitoso. Usuario: " + nuevo.getNombreUsuario() + (replaced.isEmpty() ? "" : "\nCampos reemplazados: " + replaced), "Registro", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    Administrador nuevo = new Administrador(nombre, cedula, correo, usuario, pass);
                    boolean ok = gestorUsuarios.registrarUsuario(nuevo);
                    if (!ok) {
                        JOptionPane.showMessageDialog(frame, "La cédula o el nombre de usuario ya están registrados.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String replaced = detectReplacementsForAdmin(nuevo, nombre, correo, usuario);
                    JOptionPane.showMessageDialog(frame, "Registro exitoso. Usuario: " + nuevo.getNombreUsuario() + (replaced.isEmpty() ? "" : "\nCampos reemplazados: " + replaced), "Registro", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private String detectReplacementsForStudent(Estudiante nuevo, String originalNombre, String originalCorreo, String originalUsuario) {
            StringBuilder sb = new StringBuilder();
            if (!nuevo.getNombreCompleto().equals(originalNombre)) sb.append("nombre ");
            if (!nuevo.getCorreo().equals(originalCorreo)) sb.append("correo ");
            if (!nuevo.getNombreUsuario().equals(originalUsuario)) sb.append("usuario ");
            return sb.toString().trim();
        }
        private String detectReplacementsForAdmin(Administrador nuevo, String originalNombre, String originalCorreo, String originalUsuario) {
            StringBuilder sb = new StringBuilder();
            if (!nuevo.getNombreCompleto().equals(originalNombre)) sb.append("nombre ");
            if (!nuevo.getCorreo().equals(originalCorreo)) sb.append("correo ");
            if (!nuevo.getNombreUsuario().equals(originalUsuario)) sb.append("usuario ");
            return sb.toString().trim();
        }

        private void clearFields() {
            tfNombre.setText(""); tfCedula.setText(""); tfCorreo.setText(""); if (esEstudiante) tfParalelo.setText(""); tfUsuario.setText(""); pfPassword.setText("");
        }
    }

    // ----------------------- Student Main Panel with side navigation -----------------------
    private class StudentMainPanel extends JPanel {

        public StudentMainPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(232, 245, 233));

            JPanel top = new JPanel(new BorderLayout()); top.setBackground(new Color(56,142,60)); top.setBorder(new EmptyBorder(8,12,8,12));
            JLabel lbl = new JLabel("\uD83C\uDF32 Estudiante: "); lbl.setForeground(Color.WHITE); lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
            top.add(lbl, BorderLayout.WEST);
            JButton btnLogout = createStyledButton("Cerrar Sesión"); btnLogout.setBackground(new Color(200,0,0)); btnLogout.addActionListener(e -> {
                estudianteActivo = null; motorSimulacion = null; loginPanel.clearLoginFields();
                // refrescar vistas dependientes de sesión y volver a login
                refreshStudentPanels();
                showPanel(P_LOGIN);
            });
            top.add(btnLogout, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JPanel left = new JPanel(); left.setLayout(new GridLayout(0,1,6,6)); left.setBackground(new Color(200,230,201)); left.setBorder(new EmptyBorder(10,10,10,10));
            // Ahora "Guardar Simulación" incluye el historial abajo, por eso removemos la entrada separada de Historial
            String[] opts = new String[]{"Ver parámetros de mi paralelo 📋","Agregar Especie 🐇","Definir Interacción 🐺","Iniciar/Ejecutar Simulación ⚙️","Guardar Simulación 💾","Exportar Reporte 📄","Ver mis Logros 🏆"};
            for (String o : opts) {
                JButton b = createNavButton(o); left.add(b);
            }
            add(left, BorderLayout.WEST);

            areaLayout = new CardLayout(); areaPanel = new JPanel(areaLayout);
            // Guardar referencias a instancias para forzar refresco al mostrarlas
            areaPanel.add(viewParamsPanel = new ViewParamsPanel(), "VIEW_PARAMS");
            areaPanel.add(addSpeciesPanel = new AddSpeciesPanel(), "ADD_SPECIES");
            areaPanel.add(interactionsPanel = new InteractionsPanel(), "INTERACTIONS");
            areaPanel.add(simExecutePanel = new SimExecutePanel(), "SIM_CONTROL");
            areaPanel.add(savePanel = new SavePanel(), "SAVE");
            areaPanel.add(exportPanel = new ExportPanel(), "EXPORT");
            areaPanel.add(achievementsPanel = new AchievementsPanel(), "ACHIEVEMENTS");

            add(areaPanel, BorderLayout.CENTER);

            // wire nav buttons: antes de mostrar cada tarjeta, refrescamos su contenido
            Component[] buttons = left.getComponents();
            String[] keys = new String[]{"VIEW_PARAMS","ADD_SPECIES","INTERACTIONS","SIM_CONTROL","SAVE","EXPORT","ACHIEVEMENTS"};
            for (int i=0;i<buttons.length;i++) {
                int idx = i;
                ((JButton)buttons[i]).addActionListener(e -> showStudentArea(keys[idx]));
            }
        }
    }

    // ---- ViewParamsPanel: Ver parámetros de mi paralelo ----
    private class ViewParamsPanel extends JPanel {
        public ViewParamsPanel() {
            setLayout(new BorderLayout()); setBackground(new Color(237, 245, 233));
            // construimos contenido desde refresh() para que pueda re-ejecutarse al mostrarse
            refresh();
        }

        public void refresh() {
            removeAll();
            JPanel panel = new JPanel(new GridBagLayout()); panel.setBorder(new EmptyBorder(20,20,20,20)); panel.setBackground(getBackground());
            GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(10,10,10,10); c.fill=GridBagConstraints.HORIZONTAL; c.anchor=GridBagConstraints.WEST;

            if (estudianteActivo != null) {
                String paralelo = estudianteActivo.getParalelo();
                panel.add(new JLabel("<html><b>Parámetros del Ecosistema - Paralelo: " + paralelo + "</b></html>"), grid(c,0,0));

                java.util.Map<String, Integer> params = GeneradorEcosistema.obtenerConfiguracion(paralelo);
                int row = 1;
                for (java.util.Map.Entry<String, Integer> e : params.entrySet()) {
                    panel.add(new JLabel(e.getKey() + ":"), grid(c,0,row));
                    JLabel valLabel = new JLabel(String.valueOf(e.getValue()));
                    valLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    panel.add(valLabel, grid(c,1,row));
                    row++;
                }
                panel.add(new JLabel("<html><i>Estos parámetros fueron configurados por tu profesor.</i></html>"), grid(c,0,row));
            } else {
                panel.add(new JLabel("Por favor, inicia sesión como estudiante."), grid(c,0,0));
            }

            add(new JScrollPane(panel), BorderLayout.CENTER);
            revalidate(); repaint();
        }
        private GridBagConstraints grid(GridBagConstraints c, int x, int y) {
            GridBagConstraints nc=(GridBagConstraints)c.clone(); nc.gridx=x; nc.gridy=y; return nc;
        }
    }

    // ---- AddSpeciesPanel: CRUD table and form con validaciones ----
    private class AddSpeciesPanel extends JPanel {
        private JTextField tfNombre; private JComboBox<String> cbTipo; private JSpinner spCantidad, spCapacidad;
        private JButton btnAgregar, btnEliminar, btnGuardar;
        private JTable table; private PoblacionTableModel tableModel;

        public AddSpeciesPanel() {
            setLayout(new BorderLayout()); setBackground(new Color(237, 245, 233));
            JPanel form = new JPanel(new GridBagLayout()); form.setBorder(new EmptyBorder(12,12,12,12)); form.setBackground(getBackground());
            GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6); c.fill = GridBagConstraints.HORIZONTAL;

            c.gridx=0; c.gridy=0; form.add(new JLabel("Nombre de la especie:"), c);
            tfNombre = new JTextField(12);
            tfNombre.setDocument(new javax.swing.text.PlainDocument() {
                public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]*")) super.insertString(offs, str, a);
                }
            });
            c.gridx=1; form.add(tfNombre,c);

            c.gridx=0; c.gridy=1; form.add(new JLabel("Tipo:"), c); cbTipo = new JComboBox<>(new String[]{"HERBIVORO","CARNIVORO","PLANTA"}); c.gridx=1; form.add(cbTipo,c);

            c.gridx=0; c.gridy=2; form.add(new JLabel("Cantidad inicial:"), c); spCantidad = new JSpinner(new SpinnerNumberModel(1,1,100000,1)); c.gridx=1; form.add(spCantidad,c);
            c.gridx=0; c.gridy=3; form.add(new JLabel("Capacidad máxima:"), c); spCapacidad = new JSpinner(new SpinnerNumberModel(10,1,100000,1)); c.gridx=1; form.add(spCapacidad,c);

            btnAgregar = createStyledButton("Agregar"); btnGuardar = createStyledButton("Guardar cambios"); btnEliminar = createStyledButton("Eliminar");
            btnGuardar.setEnabled(false); btnEliminar.setEnabled(false);
            JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT)); bp.setBackground(getBackground()); bp.add(btnAgregar); bp.add(btnGuardar); bp.add(btnEliminar);
            c.gridx=0; c.gridy=4; c.gridwidth=2; form.add(bp,c);

            add(form, BorderLayout.NORTH);

            tableModel = new PoblacionTableModel(); table = new JTable(tableModel); JScrollPane sp = new JScrollPane(table); add(sp, BorderLayout.CENTER);

            // selection
            table.getSelectionModel().addListSelectionListener(e -> onSelectRow());

            btnAgregar.addActionListener(e -> doAgregar());
            btnEliminar.addActionListener(e -> doEliminar());
            btnGuardar.addActionListener(e -> doGuardar());

            refreshTable();
            updateFormEnabled();
        }

        private void updateFormEnabled() {
            Simulacion sim = (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
            boolean activa = sim != null && sim.isActiva();
            boolean editable = !activa;
            tfNombre.setEnabled(editable); cbTipo.setEnabled(editable); spCantidad.setEnabled(editable); spCapacidad.setEnabled(editable);
            btnAgregar.setEnabled(editable);
            if (!editable) {
                btnGuardar.setEnabled(false); btnEliminar.setEnabled(false);
            }
        }

        private void onSelectRow() {
            int r = table.getSelectedRow();
            if (r==-1) { btnGuardar.setEnabled(false); btnEliminar.setEnabled(false); return; }
            Poblacion p = tableModel.getPoblacionAt(r);
            tfNombre.setText(p.getEspecie().getNombre()); cbTipo.setSelectedItem(p.getEspecie().getTipo()); spCantidad.setValue(p.getCantidad()); spCapacidad.setValue(p.getLimiteMaximo());
            Simulacion sim = (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
            boolean activa = sim != null && sim.isActiva();
            btnGuardar.setEnabled(!activa); btnEliminar.setEnabled(!activa);
        }

        private void doAgregar() {
            Simulacion sim = (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
            if (sim==null) { JOptionPane.showMessageDialog(frame, "No hay simulación. Inicia o configura el ecosistema primero.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            if (sim.isActiva()) { JOptionPane.showMessageDialog(frame, "No puede agregar especies mientras la simulación está activa.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }

            String nombre = tfNombre.getText().trim();
            if (nombre.isEmpty()) { JOptionPane.showMessageDialog(frame, "El nombre de la especie solo puede contener letras.", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) { JOptionPane.showMessageDialog(frame, "El nombre de la especie solo puede contener letras.", "Error", JOptionPane.ERROR_MESSAGE); return; }

            // Validar duplicados
            if (sim.getPoblaciones().stream().anyMatch(p -> p.getEspecie().getNombre().equalsIgnoreCase(nombre))) {
                JOptionPane.showMessageDialog(frame, "Ya existe una especie registrada con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE); return;
            }

            String tipo = (String)cbTipo.getSelectedItem();
            int cantidad = (Integer)spCantidad.getValue();
            int capacidad = (Integer)spCapacidad.getValue();

            if (cantidad <= 0 || capacidad <= 0) { JOptionPane.showMessageDialog(frame, "La cantidad y la capacidad máxima deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE); return; }

            Poblacion p = GeneradorEcosistema.crearEspecieConPoblacion(nombre,tipo,cantidad,capacidad);
            // registrar en la simulación (a través del gestor si existe)
            if (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) motorSimulacion.registrarPoblacion(p);
            else if (estudianteActivo!=null && estudianteActivo.getSimulacionEnCurso()!=null) estudianteActivo.getSimulacionEnCurso().getPoblaciones().add(p);
            refreshTable(); clearForm();
        }

        private void doEliminar() {
            int r = table.getSelectedRow(); if (r==-1) return;
            Poblacion p = tableModel.getPoblacionAt(r);
            Simulacion sim = (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
            if (sim==null) return;
            if (sim.isActiva()) { JOptionPane.showMessageDialog(frame, "No puede eliminar mientras simulación activa.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            sim.getPoblaciones().removeIf(pp->pp.getEspecie().getNombre().equalsIgnoreCase(p.getEspecie().getNombre()));
            refreshTable(); clearForm();
        }

        private void doGuardar() {
            int r = table.getSelectedRow(); if (r==-1) return;
            Poblacion p = tableModel.getPoblacionAt(r);
            p.getEspecie().setNombre(tfNombre.getText().trim()); p.getEspecie().setTipo((String)cbTipo.getSelectedItem());
            p.setCantidad((Integer)spCantidad.getValue()); p.setLimiteMaximo((Integer)spCapacidad.getValue());
            refreshTable();
        }

        private void clearForm() { tfNombre.setText(""); spCantidad.setValue(1); spCapacidad.setValue(10); }
        public void refreshTable() { 
            Simulacion sim = (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
            if (sim==null) tableModel.setPoblaciones(List.of()); else tableModel.setPoblaciones(sim.getPoblaciones()); updateFormEnabled(); }

        private class PoblacionTableModel extends AbstractTableModel {
            private List<Poblacion> datos = List.of();
            private final String[] cols = new String[]{"Especie","Tipo","Cantidad","Capacidad"};
            public void setPoblaciones(List<Poblacion> list) { this.datos = list==null?List.of():list; fireTableDataChanged(); }
            public Poblacion getPoblacionAt(int r){ return datos.get(r); }
            public int getRowCount(){ return datos.size(); }
            public int getColumnCount(){ return cols.length; }
            public String getColumnName(int c){ return cols[c]; }
            public Object getValueAt(int r,int c){ Poblacion p = datos.get(r); switch(c){ case 0: return p.getEspecie().getNombre(); case 1: return p.getEspecie().getTipo(); case 2: return p.getCantidad(); case 3: return p.getLimiteMaximo(); } return null; }
        }
    }

    // ---- InteractionsPanel: form con JComboBox + tabla ----
    private class InteractionsPanel extends JPanel {
        private JComboBox<String> cbDep, cbPresa; private JSpinner spFactor; private JSpinner spEfic;
        private JTable table; private InterTableModel model;
        private JButton btnBorrar, btnEditar;

        public InteractionsPanel() {
            setLayout(new BorderLayout()); setBackground(new Color(232, 245, 233));
            JPanel form = new JPanel(new GridBagLayout()); form.setBorder(new EmptyBorder(12,12,12,12)); form.setBackground(getBackground());
            GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL;

            c.gridx=0;c.gridy=0; form.add(new JLabel("Depredador:"),c); cbDep=new JComboBox<>(); c.gridx=1; form.add(cbDep,c);
            c.gridx=0;c.gridy=1; form.add(new JLabel("Presa:"),c); cbPresa=new JComboBox<>(); c.gridx=1; form.add(cbPresa,c);
            c.gridx=0;c.gridy=2; form.add(new JLabel("Factor de caza:"),c); spFactor=new JSpinner(new SpinnerNumberModel(0.1,0.0,10.0,0.1)); c.gridx=1; form.add(spFactor,c);
            c.gridx=0;c.gridy=3; form.add(new JLabel("Eficiencia:"),c); spEfic=new JSpinner(new SpinnerNumberModel(0.1,0.0,1.0,0.1)); c.gridx=1; form.add(spEfic,c);

            JButton btnReg = createStyledButton("Registrar Interacción"); btnReg.addActionListener(e->doRegistrar());
            btnBorrar = createStyledButton("Borrar seleccionada"); btnBorrar.addActionListener(e->doBorrar()); btnBorrar.setEnabled(false);
            btnEditar = createStyledButton("Editar seleccionada"); btnEditar.addActionListener(e->doEditar()); btnEditar.setEnabled(false);

            JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT)); bp.setBackground(getBackground());
            bp.add(btnReg); bp.add(btnBorrar); bp.add(btnEditar);
            c.gridx=0; c.gridy=4; c.gridwidth=2; form.add(bp, c);

            add(form, BorderLayout.NORTH);

            model = new InterTableModel(); table = new JTable(model);
            table.getSelectionModel().addListSelectionListener(e -> actualizarBotonesSeleccion());
            add(new JScrollPane(table), BorderLayout.CENTER);
            refresh();
        }

        private void actualizarBotonesSeleccion() {
            int row = table.getSelectedRow();
            btnBorrar.setEnabled(row >= 0);
            btnEditar.setEnabled(row >= 0);
        }

        private GridBagConstraints grid(GridBagConstraints c,int x,int y){ GridBagConstraints nc=(GridBagConstraints)c.clone(); nc.gridx=x; nc.gridy=y; return nc; }

        private void doBorrar() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Selecciona una interacción para borrar.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Simulacion sim = motorSimulacion!=null && motorSimulacion.getSimulacion()!=null ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
            if (sim != null && sim.isActiva()) {
                JOptionPane.showMessageDialog(frame, "No puedes borrar interacciones mientras la simulación está activa.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Interaccion inter = model.getInteraccionAt(row);
            if (sim != null) {
                sim.getInteracciones().remove(inter);
            }
            refresh();
            JOptionPane.showMessageDialog(frame, "Interacción borrada correctamente.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        private void doEditar() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Selecciona una interacción para editar.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Simulacion sim = motorSimulacion!=null && motorSimulacion.getSimulacion()!=null ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
            if (sim != null && sim.isActiva()) {
                JOptionPane.showMessageDialog(frame, "No puedes editar interacciones mientras la simulación está activa.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Interaccion inter = model.getInteraccionAt(row);
            cbDep.setSelectedItem(inter.getNombreDepredador());
            cbPresa.setSelectedItem(inter.getNombrePresa());
            spFactor.setValue(inter.getTasaExito());
            spEfic.setValue(inter.getFactorAfectacion());

            // Borrar la interacción antigua y permitir al usuario registrar la nueva
            if (sim != null) {
                sim.getInteracciones().remove(inter);
            }
            refresh();
            JOptionPane.showMessageDialog(frame, "Modifica los valores y haz clic en 'Registrar Interacción' para guardar los cambios.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

         private void doRegistrar(){
              if (motorSimulacion==null) { JOptionPane.showMessageDialog(frame,"No hay simulación activa.","Info",JOptionPane.INFORMATION_MESSAGE); return;}

              Object depObj = cbDep.getSelectedItem();
              Object presaObj = cbPresa.getSelectedItem();
              if (depObj==null || presaObj==null || depObj.toString().isEmpty() || presaObj.toString().isEmpty()) {
                  JOptionPane.showMessageDialog(frame,"Debe seleccionar depredador y presa.","Error",JOptionPane.ERROR_MESSAGE); return;
              }

              String dep = depObj.toString().trim();
              String presa = presaObj.toString().trim();

              if(dep.equalsIgnoreCase(presa)){
                  JOptionPane.showMessageDialog(frame,"El depredador y la presa deben ser especies distintas.","Error",JOptionPane.ERROR_MESSAGE); return;
              }

              // Validar cadena alimenticia: CARNIVORO -> HERBIVORO, HERBIVORO -> PLANTA
              Simulacion sim = motorSimulacion!=null && motorSimulacion.getSimulacion()!=null ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
              if (sim != null) {
                  Poblacion depPob = null, presaPob = null;
                  
                  // Búsqueda robusta case-insensitive
                  for (Poblacion p : sim.getPoblaciones()) {
                      if (p.getEspecie().getNombre().trim().equalsIgnoreCase(dep)) depPob = p;
                      if (p.getEspecie().getNombre().trim().equalsIgnoreCase(presa)) presaPob = p;
                  }

                  // CRÍTICO: Validar que encontró ambas especies
                  if (depPob == null) {
                      JOptionPane.showMessageDialog(frame, "Error: No se encontró la especie depredador '" + dep + "'. Verifica que esté en la lista.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                  }
                  if (presaPob == null) {
                      JOptionPane.showMessageDialog(frame, "Error: No se encontró la especie presa '" + presa + "'. Verifica que esté en la lista.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                  }

                  String tipoDep = normalizarTipo(depPob.getEspecie().getTipo());
                  String tipoPresa = normalizarTipo(presaPob.getEspecie().getTipo());

                  // Validar relaciones válidas
                  if (tipoDep.equals("PLANTA")) {
                      JOptionPane.showMessageDialog(frame, "Una planta no puede ser depredador.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                  }

                  if (tipoDep.equals("CARNIVORO")) {
                      if (!tipoPresa.equals("HERBIVORO")) {
                          JOptionPane.showMessageDialog(frame, "Un carnívoro solo puede cazar herbívoros.\n(" + presa + " es " + tipoPresa.toLowerCase() + ")", "Error", JOptionPane.ERROR_MESSAGE);
                          return;
                      }
                  } else if (tipoDep.equals("HERBIVORO")) {
                      if (!tipoPresa.equals("PLANTA")) {
                          JOptionPane.showMessageDialog(frame, "Un herbívoro solo puede alimentarse de plantas.\n(" + presa + " es " + tipoPresa.toLowerCase() + ")", "Error", JOptionPane.ERROR_MESSAGE);
                          return;
                      }
                  } else {
                      JOptionPane.showMessageDialog(frame, "Esa combinación de especies no es una relación depredador-presa válida.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                  }
              }

              double factor = ((Number)spFactor.getValue()).doubleValue();
              double efic = ((Number)spEfic.getValue()).doubleValue();
              Interaccion inter = new Interaccion(dep,presa,factor,efic);
              if (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) {
                  motorSimulacion.registrarInteraccion(inter);
              } else if (estudianteActivo!=null && estudianteActivo.getSimulacionEnCurso()!=null) {
                  estudianteActivo.getSimulacionEnCurso().getInteracciones().add(inter);
              }
              refresh();
          }

         // Normalizar tipos: convierte valores antiguos (Herbívoro, Planta, etc) a valores estándar
         private String normalizarTipo(String tipo) {
             if (tipo == null) return "PLANTA";
             String normalized = tipo.replaceAll("[áéíóú]", "").toUpperCase().trim();
             if (normalized.contains("CARNIVORO")) return "CARNIVORO";
             if (normalized.contains("HERBIVORO") || normalized.equals("ANIMAL")) return "HERBIVORO";
             if (normalized.contains("PLANTA")) return "PLANTA";
             return "PLANTA"; // por defecto
         }

        public void refresh(){
             Simulacion sim = (motorSimulacion!=null && motorSimulacion.getSimulacion()!=null) ? motorSimulacion.getSimulacion() : (estudianteActivo!=null?estudianteActivo.getSimulacionEnCurso():null);
             if (sim == null) {
                 model.setList(List.of());
                 model.setSimulacion(null);
                 cbDep.removeAllItems();
                 cbPresa.removeAllItems();
             } else {
                 model.setSimulacion(sim);
                 model.setList(sim.getInteracciones());
                 List<String> nombres = sim.getPoblaciones().stream().map(p->p.getEspecie().getNombre()).toList();

                 cbDep.removeAllItems();
                 cbPresa.removeAllItems();
                 for (String n : nombres) {
                     cbDep.addItem(n);
                     cbPresa.addItem(n);
                 }
                 if (nombres.size() < 2) {
                     JOptionPane.showMessageDialog(frame, "Primero debes agregar al menos dos especies antes de definir una interacción.", "Info", JOptionPane.INFORMATION_MESSAGE);
                 }
             }
         }

        private class InterTableModel extends AbstractTableModel{
             private List<Interaccion> datos=List.of();
             private String[] cols={"Depredador","Presa","Factor","Eficiencia"};
             private Simulacion simulacion;

             public void setList(List<Interaccion> l){ this.datos=l==null?List.of():l; fireTableDataChanged(); }
             public void setSimulacion(Simulacion sim) { this.simulacion = sim; }
             public Interaccion getInteraccionAt(int r) { return datos.get(r); }
             public int getRowCount(){ return datos.size(); }
             public int getColumnCount(){ return cols.length;}
             public String getColumnName(int c){ return cols[c]; }

             private String obtenerTipo(String nombreEspecie) {
                 if (simulacion != null) {
                     for (Poblacion p : simulacion.getPoblaciones()) {
                         if (p.getEspecie().getNombre().equalsIgnoreCase(nombreEspecie)) {
                             return p.getEspecie().getTipo();
                         }
                     }
                 }
                 return "";
             }

             private String normalizarTipo(String tipo) {
                 if (tipo == null) return "PLANTA";
                 String normalized = tipo.replaceAll("[áéíóú]", "").toUpperCase().trim();
                 if (normalized.contains("CARNIVORO")) return "CARNIVORO";
                 if (normalized.contains("HERBIVORO") || normalized.equals("ANIMAL")) return "HERBIVORO";
                 if (normalized.contains("PLANTA")) return "PLANTA";
                 return "PLANTA";
             }

             public Object getValueAt(int r,int c){
                 Interaccion i = datos.get(r);
                 switch(c){
                     case 0:
                         String tipoD = normalizarTipo(obtenerTipo(i.getNombreDepredador()));
                         return i.getNombreDepredador() + " (" + tipoD + ")";
                     case 1:
                         String tipoP = normalizarTipo(obtenerTipo(i.getNombrePresa()));
                         return i.getNombrePresa() + " (" + tipoP + ")";
                     case 2: return String.format("%.2f", i.getTasaExito());
                     case 3: return String.format("%.2f", i.getFactorAfectacion());
                 }
                 return null;
             }
         }
    }

    // ---- SimExecutePanel: Fusión de Iniciar/Ejecutar/Estado ----
    private class SimExecutePanel extends JPanel {
        private JSpinner spTurnos; private JButton btnCrear, btnTurno, btnTodos;
        private JTable tableState; private StateTableModel modelState;
        private JTextArea areaInfo;

        public SimExecutePanel() {
            setLayout(new BorderLayout()); setBackground(new Color(237, 245, 233));

            // Panel de control
            JPanel controlPanel = new JPanel(new GridBagLayout());
            controlPanel.setBorder(new EmptyBorder(12,12,12,12));
            controlPanel.setBackground(getBackground());
            GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL;

            c.gridx=0; c.gridy=0; controlPanel.add(new JLabel("<html><b>CONFIGURAR SIMULACIÓN</b></html>"), c);
            c.gridx=0; c.gridy=1; controlPanel.add(new JLabel("Número de turnos:"), c);
            spTurnos=new JSpinner(new SpinnerNumberModel(10,1,10000,1));
            c.gridx=1; controlPanel.add(spTurnos, c);

            btnCrear=createStyledButton("Crear/Configurar");
            btnCrear.addActionListener(e->doCrear());
            c.gridx=1; c.gridy=2; controlPanel.add(btnCrear, c);

            c.gridx=0; c.gridy=4; c.gridwidth=2; controlPanel.add(new JSeparator(), c); c.gridwidth=1;

            c.gridx=0; c.gridy=5; controlPanel.add(new JLabel("<html><b>EJECUTAR TURNOS</b></html>"), c);
            btnTurno=createStyledButton("Ejecutar 1 Turno");
            btnTurno.addActionListener(e->doTurno());
            btnTodos=createStyledButton("Ejecutar Todos");
            btnTodos.addActionListener(e->doTodos());
            JPanel execPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            execPanel.setBackground(getBackground());
            execPanel.add(btnTurno); execPanel.add(btnTodos);
            c.gridx=0; c.gridy=6; c.gridwidth=2; controlPanel.add(execPanel, c); c.gridwidth=1;

            c.gridx=0; c.gridy=7; c.gridwidth=2; controlPanel.add(new JSeparator(), c); c.gridwidth=1;

            c.gridx=0; c.gridy=8; c.gridwidth=2;
            areaInfo = new JTextArea(8, 40);
            areaInfo.setEditable(false);
            areaInfo.setLineWrap(true);
            areaInfo.setWrapStyleWord(true);
            areaInfo.setText("INFORMACIÓN DE LA SIMULACIÓN:\n\n" +
                "1 Turno = 1 semana del ecosistema\n\n" +
                "En cada turno ocurre lo siguiente:\n" +
                "1) Cada población se reproduce o muere según su tasa natural\n" +
                "2) Los depredadores cazan a sus presas según las interacciones definidas\n" +
                "3) Se consume y regenera el alimento del entorno\n\n" +
                "El estado actual se muestra abajo.");
            areaInfo.setFont(new Font("Arial", Font.PLAIN, 11));
            controlPanel.add(new JScrollPane(areaInfo), c);

            add(controlPanel, BorderLayout.NORTH);

            // Panel de estado
            JPanel statePanel = new JPanel(new BorderLayout());
            statePanel.setBorder(new TitledBorder("Estado Actual del Ecosistema"));
            statePanel.setBackground(getBackground());

            modelState = new StateTableModel();
            tableState = new JTable(modelState);
            statePanel.add(new JScrollPane(tableState), BorderLayout.CENTER);

            JButton btnRefresh = createStyledButton("Refrescar Estado");
            btnRefresh.addActionListener(e -> modelState.refresh());
            statePanel.add(btnRefresh, BorderLayout.SOUTH);

            add(statePanel, BorderLayout.CENTER);
        }

        private void doCrear(){
             if (estudianteActivo==null){
                 JOptionPane.showMessageDialog(frame,"Debes iniciar sesión como estudiante.","Error",JOptionPane.ERROR_MESSAGE); return;
             }

             // Validar que no haya una simulación activa
             if (motorSimulacion != null && motorSimulacion.haySimulacionActiva()) {
                 JOptionPane.showMessageDialog(frame, "Ya tienes una simulación activa. Debes terminarla antes de crear una nueva.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             int t = (Integer)spTurnos.getValue();
             if (t <= 0) {
                 JOptionPane.showMessageDialog(frame, "El número de turnos debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             // Usar la simulación existente del estudiante (que ya tiene especies e interacciones),
             // solo actualizar el número de turnos y activarla
             Simulacion sim = estudianteActivo.getSimulacionEnCurso();
             if (sim == null) {
                 JOptionPane.showMessageDialog(frame, "No hay simulación configurada. Agrega especies primero.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             // Verificar que hay al menos una población
             if (sim.getPoblaciones().isEmpty()) {
                 JOptionPane.showMessageDialog(frame, "Debes agregar al menos una especie antes de iniciar.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             sim.setTiempoTotal(t);
             try{
                 motorSimulacion.iniciar();
                 JOptionPane.showMessageDialog(frame,"Simulación inicializada con " + t + " turnos.","Info",JOptionPane.INFORMATION_MESSAGE);
                 modelState.refresh();
             } catch(Exception ex){
                 JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
             }
         }

        // Refresh público para uso por la UI principal
        public void refresh() {
            if (modelState != null) modelState.refresh();
        }

        private void doTurno(){
            if(motorSimulacion==null){
                JOptionPane.showMessageDialog(frame,"No hay simulación activa.","Error",JOptionPane.ERROR_MESSAGE); return;
            }
            try{
                motorSimulacion.ejecutarTurno();
                JOptionPane.showMessageDialog(frame,"Turno ejecutado.","Info",JOptionPane.INFORMATION_MESSAGE);
                modelState.refresh();
            } catch(Exception ex){
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }

        private void doTodos(){
            if(motorSimulacion==null){
                JOptionPane.showMessageDialog(frame,"No hay simulación activa.","Error",JOptionPane.ERROR_MESSAGE); return;
            }
            try{
                motorSimulacion.ejecutarTodosLosTurnos();
                JOptionPane.showMessageDialog(frame,"Todos los turnos ejecutados.","Info",JOptionPane.INFORMATION_MESSAGE);
                modelState.refresh();
            } catch(Exception ex){
                JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }

        private class StateTableModel extends AbstractTableModel{
            private List<Poblacion> datos=List.of();
            private String[] cols={"Especie","Tipo","Cantidad"};
            public void refresh(){
                if(motorSimulacion==null||motorSimulacion.getSimulacion()==null) datos=List.of();
                else datos=motorSimulacion.getSimulacion().getPoblaciones();
                fireTableDataChanged();
            }
            public int getRowCount(){ return datos.size(); }
            public int getColumnCount(){ return cols.length;}
            public String getColumnName(int c){ return cols[c]; }
            public Object getValueAt(int r,int c){
                Poblacion p=datos.get(r);
                switch(c){
                    case 0: return p.getEspecie().getNombre();
                    case 1: return p.getEspecie().getTipo();
                    case 2: return p.getCantidad();
                }
                return null;
            }
        }
    }

    // ---- SavePanel (ahora incluye historial embebido) ----
    private class SavePanel extends JPanel {
        private HistoryPanel historyInside;

        public SavePanel(){
            setLayout(new BorderLayout());
            setBackground(new Color(237, 245, 233));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground());
            JButton btnSave=createStyledButton("Guardar Simulación");
            btnSave.addActionListener(e-> doSave());
            top.add(btnSave);
            add(top, BorderLayout.NORTH);

            // Reutilizamos el HistoryPanel como vista embebida
            historyInside = new HistoryPanel();
            add(historyInside, BorderLayout.CENTER);
        }

        private void doSave(){
             if(estudianteActivo==null||motorSimulacion==null||motorSimulacion.getSimulacion()==null){
                 JOptionPane.showMessageDialog(frame,"No hay simulación para guardar.","Info",JOptionPane.INFORMATION_MESSAGE);
                 return;
             }

              Simulacion sim = motorSimulacion.getSimulacion();
              estudianteActivo.registrarSimulacion(sim);
              GestorLogro.evaluarLogros(estudianteActivo,sim);

             // Construir resumen de lo que se guardó
             StringBuilder resumen = new StringBuilder();
             resumen.append("SIMULACIÓN GUARDADA EXITOSAMENTE\n\n");
             resumen.append("=== DATOS DE LA SIMULACIÓN ===\n");
             resumen.append("Fecha: ").append(sim.getFechaFormateada()).append("\n");
             resumen.append("Turnos completados: ").append(sim.getTurnoActual()).append(" / ").append(sim.getTiempoTotal()).append("\n");
             resumen.append("Resultado: ").append(sim.getResultado()).append("\n\n");

             resumen.append("=== ESPECIES REGISTRADAS ===\n");
             for (Poblacion p : sim.getPoblaciones()) {
                 resumen.append("- ").append(p.getEspecie().getNombre()).append(" (").append(p.getEspecie().getTipo()).append("): ");
                 resumen.append(p.getCantidad()).append(" individuos\n");
             }

             resumen.append("\n=== INTERACCIONES DEFINIDAS ===\n");
             if (sim.getInteracciones().isEmpty()) {
                 resumen.append("(Sin interacciones definidas)\n");
             } else {
                 for (Interaccion inter : sim.getInteracciones()) {
                     resumen.append("- ").append(inter.getNombreDepredador()).append(" caza a ").append(inter.getNombrePresa()).append("\n");
                 }
             }

             resumen.append("\n=== EVENTOS REGISTRADOS ===\n");
             List<String> bitacora = sim.getBitacoraTurnos();
             if (bitacora.isEmpty()) {
                 resumen.append("(Sin eventos registrados)\n");
             } else {
                 resumen.append("Total de turnos con eventos: ").append(bitacora.size()).append("\n");
                 if (bitacora.size() <= 5) {
                     for (String evento : bitacora) {
                         resumen.append(evento).append("\n");
                     }
                 } else {
                     for (int i = 0; i < 3; i++) {
                         resumen.append(bitacora.get(i)).append("\n");
                     }
                     resumen.append("... (").append(bitacora.size() - 6).append(" eventos más) ...\n");
                     for (int i = bitacora.size() - 3; i < bitacora.size(); i++) {
                         resumen.append(bitacora.get(i)).append("\n");
                     }
                 }
             }

             resumen.append("\nTu profesor podrá revisar el detalle completo desde su panel de administración.");

             JOptionPane.showMessageDialog(frame, resumen.toString(), "Simulación Guardada", JOptionPane.INFORMATION_MESSAGE);

             // refrescar historial embebido inmediatamente
             if (historyInside != null) historyInside.refresh();
         }

        public void refresh() {
            if (historyInside != null) historyInside.refresh();
        }
    }

    // ---- HistoryPanel ----
    private class HistoryPanel extends JPanel {
        private JList<String> list;
        private DefaultListModel<String> model;
        private JTable tableEvents;
        private EventTableModel modelEvents;

        public HistoryPanel(){
            setLayout(new BorderLayout());
            setBackground(new Color(237, 245, 233));

            model=new DefaultListModel<>();
            list=new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            add(new JScrollPane(list), BorderLayout.WEST);

            modelEvents = new EventTableModel();
            tableEvents = new JTable(modelEvents);
            add(new JScrollPane(tableEvents), BorderLayout.CENTER);

            list.addListSelectionListener(e-> showDetail());

            JButton btnRef=createStyledButton("Refrescar");
            btnRef.addActionListener(e->refresh());
            add(btnRef, BorderLayout.SOUTH);

            refresh();
        }

        public void refresh(){
            model.clear();
            int i=1;

            // Primero mostrar la simulación actual (si existe)
            if (motorSimulacion != null && motorSimulacion.getSimulacion() != null) {
                model.addElement("[ACTUAL] " + motorSimulacion.getSimulacion().getFechaFormateada());
                i++;
            }

            // Luego mostrar las simulaciones guardadas del historial
            if(estudianteActivo!=null){
                for(Simulacion s: estudianteActivo.getHistorialSimulaciones()){
                    model.addElement("["+i+"] "+s.getFechaFormateada());
                    i++;
                }
            }
        }

        private void showDetail(){
            int idx=list.getSelectedIndex();
            if(idx==-1) { modelEvents.setEvents(java.util.List.of()); return; }

            // Verificar si es la simulación actual o una guardada
            Simulacion s = null;
            if (idx == 0 && motorSimulacion != null && motorSimulacion.getSimulacion() != null) {
                // Primera entrada es la simulación actual
                s = motorSimulacion.getSimulacion();
            } else {
                // Ajustar el índice si hay simulación actual
                int historyIdx = idx;
                if (motorSimulacion != null && motorSimulacion.getSimulacion() != null) {
                    historyIdx = idx - 1;
                }
                if (historyIdx >= 0 && estudianteActivo != null && historyIdx < estudianteActivo.getHistorialSimulaciones().size()) {
                    s = estudianteActivo.getHistorialSimulaciones().get(historyIdx);
                }
            }

            if (s == null) {
                modelEvents.setEvents(java.util.List.of());
                return;
            }

            java.util.List<String> eventos = s.getBitacoraTurnos();

            if (eventos == null || eventos.isEmpty()) {
                modelEvents.setEvents(java.util.List.of("(Sin eventos registrados para esta simulación)"));
            } else {
                java.util.List<String> processedEvents = new java.util.ArrayList<>();
                int turno = 1;
                for (String evento : eventos) {
                    processedEvents.add("Turno " + turno + ": " + evento);
                    turno++;
                }
                modelEvents.setEvents(processedEvents);
            }
        }

        private class EventTableModel extends AbstractTableModel {
            private java.util.List<String> eventos = java.util.List.of();
            private String[] cols = {"Turno / Evento"};

            public void setEvents(java.util.List<String> events) {
                this.eventos = events == null ? java.util.List.of() : events;
                fireTableDataChanged();
            }

            public int getRowCount() { return eventos.size(); }
            public int getColumnCount() { return 1; }
            public String getColumnName(int c) { return cols[0]; }
            public Object getValueAt(int r, int c) { return eventos.get(r); }
        }
    }

    // ---- ExportPanel ----
    private class ExportPanel extends JPanel {
        private JTabbedPane tabs;
        private JTable tableInitial, tableFinal, tableEvents;

        public ExportPanel(){
            setLayout(new BorderLayout());
            setBackground(new Color(237, 245, 233));

            tabs = new JTabbedPane();

            tableInitial = new JTable(new PoblacionSimpleTableModel());
            tabs.addTab("Estado Inicial", new JScrollPane(tableInitial));

            tableEvents = new JTable(new EventSimpleTableModel());
            tabs.addTab("Eventos", new JScrollPane(tableEvents));

            tableFinal = new JTable(new PoblacionSimpleTableModel());
            tabs.addTab("Estado Final", new JScrollPane(tableFinal));

            add(tabs, BorderLayout.CENTER);

            JButton btnExport=createStyledButton("Cargar Reporte");
            btnExport.addActionListener(e->loadReportWithValidation());
            add(btnExport, BorderLayout.NORTH);
        }

        // Llamado al presionar el botón: muestra mensaje si no hay simulación
        private void loadReportWithValidation(){
            Simulacion sim = getCurrentSimulation();
            if (sim == null) {
                JOptionPane.showMessageDialog(frame,"No hay simulación para exportar.","Info",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            loadReportData(sim);
        }

        // Llamado desde showStudentArea() al cambiar de tarjeta: no muestra mensajes
        private void loadReportQuietly(){
            Simulacion sim = getCurrentSimulation();
            if (sim != null) {
                loadReportData(sim);
            }
        }

        // Obtiene la simulación actual (desde motorSimulacion o estudianteActivo)
        private Simulacion getCurrentSimulation(){
            if (motorSimulacion != null && motorSimulacion.getSimulacion() != null) {
                return motorSimulacion.getSimulacion();
            } else if (estudianteActivo != null && estudianteActivo.getSimulacionEnCurso() != null) {
                return estudianteActivo.getSimulacionEnCurso();
            }
            return null;
        }

        // Carga los datos de la simulación en las tablas (sin mostrar mensajes)
        private void loadReportData(Simulacion sim){
            // Estado Inicial: crear una lista temporal basada en el snapshot estadoInicial
            java.util.List<Poblacion> poblacionesInitiales = new java.util.ArrayList<>();
            java.util.Map<String, Integer> estadoIni = sim.getEstadoInicial();
            if (estadoIni != null && !estadoIni.isEmpty()) {
                for (java.util.Map.Entry<String, Integer> entry : estadoIni.entrySet()) {
                    // Buscar la especie original para obtener tipo
                    Especie especie = null;
                    for (Poblacion p : sim.getPoblaciones()) {
                        if (p.getEspecie().getNombre().equals(entry.getKey())) {
                            especie = p.getEspecie();
                            break;
                        }
                    }
                    if (especie != null) {
                        // Crear una Poblacion temporal con la cantidad inicial
                        Poblacion pobTemp = new Poblacion(entry.getValue(), entry.getValue(), especie);
                        poblacionesInitiales.add(pobTemp);
                    }
                }
            }
            ((PoblacionSimpleTableModel)tableInitial.getModel()).setPoblaciones(poblacionesInitiales);
            ((PoblacionSimpleTableModel)tableFinal.getModel()).setPoblaciones(sim.getPoblaciones());

            java.util.List<String> eventos = sim.getBitacoraTurnos();
            java.util.List<String> processedEvents = new java.util.ArrayList<>();
            if (eventos != null && !eventos.isEmpty()) {
                int turno = 1;
                for (String evento : eventos) {
                    processedEvents.add("Turno " + turno + ": " + evento);
                    turno++;
                }
            }
            ((EventSimpleTableModel)tableEvents.getModel()).setEvents(processedEvents);
        }

        // Permite recargar desde la UI externa (sin mostrar JOptionPane)
        public void refresh() {
            loadReportQuietly();
        }

        private class PoblacionSimpleTableModel extends AbstractTableModel {
            private java.util.List<Poblacion> datos = java.util.List.of();
            private String[] cols = {"Especie","Tipo","Cantidad"};

            public void setPoblaciones(java.util.List<Poblacion> list) {
                this.datos = list == null ? java.util.List.of() : list;
                fireTableDataChanged();
            }

            public int getRowCount() { return datos.size(); }
            public int getColumnCount() { return cols.length; }
            public String getColumnName(int c) { return cols[c]; }
            public Object getValueAt(int r, int c) {
                Poblacion p = datos.get(r);
                switch(c) {
                    case 0: return p.getEspecie().getNombre();
                    case 1: return p.getEspecie().getTipo();
                    case 2: return p.getCantidad();
                }
                return null;
            }
        }

        private class EventSimpleTableModel extends AbstractTableModel {
            private java.util.List<String> eventos = java.util.List.of();
            private String[] cols = {"Evento"};

            public void setEvents(java.util.List<String> events) {
                this.eventos = events == null ? java.util.List.of() : events;
                fireTableDataChanged();
            }

            public int getRowCount() { return eventos.size(); }
            public int getColumnCount() { return 1; }
            public String getColumnName(int c) { return cols[0]; }
            public Object getValueAt(int r, int c) { return eventos.get(r); }
        }
    }

    // ---- AchievementsPanel mejorado (tarjetas con iconos) ----
    private class AchievementsPanel extends JPanel {
        private JPanel cardsContainer;

        public AchievementsPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(237, 245, 233));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground());
            JButton b = createStyledButton("Refrescar"); b.addActionListener(e -> refresh()); top.add(b);
            add(top, BorderLayout.NORTH);

            cardsContainer = new JPanel();
            cardsContainer.setBackground(getBackground());
            cardsContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12)); // usar WrapLayout para tarjetas (clase auxiliar incluida abajo)

            JScrollPane sc = new JScrollPane(cardsContainer);
            sc.setBorder(null);
            add(sc, BorderLayout.CENTER);

            refresh();
        }

        public void refresh() {
            cardsContainer.removeAll();
            if (estudianteActivo == null) {
                cardsContainer.add(emptyStatePanel("Por favor, inicia sesión como estudiante."));
            } else {
                var logros = estudianteActivo.getLogros();
                if (logros == null || logros.isEmpty()) {
                    cardsContainer.add(emptyStatePanel("Aún no tienes logros. ¡Completa tu primera simulación exitosa!"));
                } else {
                    for (Logro l : logros) {
                        cardsContainer.add(createCardForLogro(l));
                    }
                }
            }
            revalidate(); repaint();
        }

        private JPanel emptyStatePanel(String msg) {
            JPanel p = new JPanel(new BorderLayout()); p.setBackground(getBackground());
            JLabel icon = new JLabel("\uD83D\uDCAA", SwingConstants.CENTER); icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
            JLabel text = new JLabel("<html><div style='text-align:center; padding:10px; font-weight:600; color:#444;'>" + msg + "</div></html>");
            p.add(icon, BorderLayout.CENTER); p.add(text, BorderLayout.SOUTH);
            p.setPreferredSize(new Dimension(320, 160));
            return p;
        }

        private JPanel createCardForLogro(Logro l) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), new EmptyBorder(8,8,8,8)));
            card.setPreferredSize(new Dimension(300, 120));

            JLabel icon = new JLabel(iconForTipo(l.getTipo())); icon.setFont(new Font("SansSerif", Font.PLAIN, 36));
            JPanel left = new JPanel(new BorderLayout()); left.setBackground(Color.WHITE); left.add(icon, BorderLayout.CENTER);

            JPanel info = new JPanel(); info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS)); info.setBackground(Color.WHITE);
            JLabel title = new JLabel(l.getNombre()); title.setFont(new Font("SansSerif", Font.BOLD, 14));
            JLabel desc = new JLabel("<html><div style='width:180px;'>" + l.getDescripcion() + "</div></html>"); desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JLabel date = new JLabel(l.getFechaFormateada()); date.setFont(new Font("SansSerif", Font.ITALIC, 11)); date.setForeground(Color.GRAY);

            info.add(title); info.add(Box.createVerticalStrut(6)); info.add(desc); info.add(Box.createVerticalStrut(8)); info.add(date);

            card.add(left, BorderLayout.WEST);
            card.add(info, BorderLayout.CENTER);
            return card;
        }

        private String iconForTipo(String tipo) {
            if (tipo == null) return "\uD83C\uDF89"; // default (🎉)
            switch (tipo) {
                case "PRIMERA_SIMULACION": return "\uD83C\uDF89"; // 🎉
                case "SIMULACION_EXITOSA": return "\uD83C\uDFC6"; // 🏆
                case "TRES_ESPECIES_VIVAS": return "\uD83D\uDC1F"; // 🐟
                case "CONSERVACION": return "\uD83C\uDF32"; // 🌲
                default: return "\uD83D\uDC51"; // 👑
            }
        }
    }

    /**
     * WrapLayout: simple layout manager que permite que los componentes se acomoden en filas y "wrap".
     * Implementación reducida tomada para uso en este panel (funcional y ligera).
     */
    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap*2);

                int x = 0, y = insets.top + vgap;
                int rowHeight = 0;

                for (Component comp : target.getComponents()) {
                    if (!comp.isVisible()) continue;
                    Dimension d = preferred ? comp.getPreferredSize() : comp.getMinimumSize();
                    if (x == 0 || x + d.width <= maxWidth) {
                        if (x > 0) x += hgap;
                        x += d.width; rowHeight = Math.max(rowHeight, d.height);
                    } else {
                        x = d.width; y += vgap + rowHeight; rowHeight = d.height;
                    }
                }
                y += rowHeight + insets.bottom;
                return new Dimension(targetWidth, y);
            }
        }
    }

    // ----------------------- Admin Main Panel -----------------------
        private class AdminMainPanel extends JPanel {
        private CardLayout adminArea; private JPanel adminAreaPanel;
        public AdminMainPanel(){ setLayout(new BorderLayout()); setBackground(new Color(232, 234, 246)); JPanel top=new JPanel(new BorderLayout()); top.setBackground(new Color(30, 136, 229)); top.setBorder(new EmptyBorder(8,12,8,12)); JLabel lbl=new JLabel("Administrador"); lbl.setForeground(Color.WHITE); lbl.setFont(new Font("SansSerif", Font.BOLD, 16)); top.add(lbl, BorderLayout.WEST); JButton btnLogout=createStyledButton("Cerrar Sesión"); btnLogout.setBackground(new Color(200,0,0)); btnLogout.addActionListener(e->{ administradorActivo=null; estudianteActivo=null; motorSimulacion=null; loginPanel.clearLoginFields(); refreshStudentPanels(); showPanel(P_LOGIN); }); top.add(btnLogout, BorderLayout.EAST); add(top, BorderLayout.NORTH);
            JPanel left=new JPanel(new GridLayout(0,1,6,6)); left.setBackground(new Color(197, 202, 233)); left.setBorder(new EmptyBorder(10,10,10,10)); String[] opts={"Listar por Paralelo","Buscar por Cédula","Ver Logros (Agrupado)","Ver Logros por Curso","Dar de Baja","Reporte Global","Configurar Parámetros","Exportar Listado","Ver Ranking"}; for(String s:opts) left.add(createNavButton(s)); add(left, BorderLayout.WEST);
            adminArea=new CardLayout(); adminAreaPanel=new JPanel(adminArea); adminAreaPanel.add(new AdminListByParaleloPanel(),"PARAL" ); adminAreaPanel.add(new AdminSearchPanel(),"SEARCH"); adminAreaPanel.add(new AdminVerLogrosPanel(),"LOGROS"); adminAreaPanel.add(new AdminVerLogrosPorCursoPanel(),"LOGROS_CUR" ); adminAreaPanel.add(new AdminDarBajaPanel(),"BAJA"); adminAreaPanel.add(new AdminReporteGlobalPanel(),"REPORTE"); adminAreaPanel.add(new AdminConfigPanel(),"CONFIG"); adminAreaPanel.add(new AdminExportPanel(),"EXPORT"); adminAreaPanel.add(new AdminRankingPanel(),"RANK"); add(adminAreaPanel, BorderLayout.CENTER);
            Component[] bs = left.getComponents(); String[] keys = new String[]{"PARAL","SEARCH","LOGROS","LOGROS_CUR","BAJA","REPORTE","CONFIG","EXPORT","RANK"}; for(int i=0;i<bs.length;i++){ int idx=i; ((JButton)bs[i]).addActionListener(e->adminArea.show(adminAreaPanel, keys[idx])); }
        }
    }

    private class AdminListByParaleloPanel extends JPanel{
        private JTable table;
        private JComboBox<String> cbParalelo;
        public AdminListByParaleloPanel(){
            setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233));
            JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground()); top.add(new JLabel("Paralelo:"));
            cbParalelo=new JComboBox<>(new String[]{"Todos","A","B","C"}); top.add(cbParalelo);
            JButton b=createStyledButton("Filtrar"); b.addActionListener(e->refresh()); top.add(b);
            add(top, BorderLayout.NORTH);
            table=new JTable(new AdminUserTableModel()); add(new JScrollPane(table), BorderLayout.CENTER);
        }
        private void refresh(){ final String p = cbParalelo.getSelectedItem() == null ? "" : (String)cbParalelo.getSelectedItem(); List<Estudiante> list = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).filter(est->p.equals("Todos")||p.isEmpty()||est.getParalelo().equalsIgnoreCase(p)).collect(Collectors.toList()); if(!p.equals("Todos") && (list==null || list.isEmpty())){ JOptionPane.showMessageDialog(frame,"No hay estudiantes registrados en este paralelo.","Info",JOptionPane.INFORMATION_MESSAGE); } ((AdminUserTableModel)table.getModel()).setList(list); }
    }
    private class AdminSearchPanel extends JPanel{
        private JTextField tfCed; private JTextArea area;
        public AdminSearchPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground()); top.add(new JLabel("Cédula:")); tfCed=new JTextField(12); ((AbstractDocument)tfCed.getDocument()).setDocumentFilter(new NumericFilter()); top.add(tfCed); JButton b=createStyledButton("Buscar"); b.addActionListener(e->doBuscar()); top.add(b); add(top, BorderLayout.NORTH); area=new JTextArea(); area.setEditable(false); add(new JScrollPane(area), BorderLayout.CENTER); }
        private void doBuscar(){ String c=tfCed.getText().trim(); if(c.isEmpty()){ JOptionPane.showMessageDialog(frame,"Ingrese cédula.","Error",JOptionPane.ERROR_MESSAGE); return;} if(c.length()!=10){ JOptionPane.showMessageDialog(frame,"Debe ingresar una cédula válida.","Error",JOptionPane.ERROR_MESSAGE); return;} Usuario u = gestorUsuarios.buscarPorCedula(c); if(u==null) { JOptionPane.showMessageDialog(frame,"No se encontró ningún estudiante con esa cédula.","Info",JOptionPane.INFORMATION_MESSAGE); area.setText(""); } else { if(u instanceof Estudiante) area.setText(GestorLogro.obtenerLogrosPorEstudiante((Estudiante)u)); else { JOptionPane.showMessageDialog(frame,"No se encontró ningún estudiante con esa cédula.","Info",JOptionPane.INFORMATION_MESSAGE); area.setText(""); } } }
    }
    private class AdminVerLogrosPanel extends JPanel{ private JTextArea area; public AdminVerLogrosPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground()); JButton b=createStyledButton("Refrescar"); b.addActionListener(e->refresh()); top.add(b); add(top, BorderLayout.NORTH); area=new JTextArea(); area.setEditable(false); add(new JScrollPane(area), BorderLayout.CENTER); refresh(); } private void refresh(){ StringBuilder sb=new StringBuilder(); List<Estudiante> todos = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).collect(Collectors.toList()); for(String paralelo: new String[]{"A","B","C"}){ sb.append("=== Paralelo ").append(paralelo).append(" ===\n"); List<Estudiante> por = todos.stream().filter(s->paralelo.equalsIgnoreCase(s.getParalelo())).collect(Collectors.toList()); if(por.isEmpty()){ sb.append("(Sin estudiantes en este paralelo)\n\n"); continue; } for(Estudiante e: por){ sb.append("Nombre: ").append(e.getNombreCompleto()).append(" | Cant. logros: ").append(e.getLogros().size()).append("\n"); if(!e.getLogros().isEmpty()){ for(Logro l: e.getLogros()){ sb.append("   - ").append(l.getNombre()).append(" | ").append(l.getTipo()).append(" | ").append(l.getFechaObtenido()).append("\n"); } } sb.append("\n"); } } area.setText(sb.toString()); } }
    private class AdminVerLogrosPorCursoPanel extends JPanel{ private JComboBox<String> cbPar; private JTextArea area; public AdminVerLogrosPorCursoPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground()); top.add(new JLabel("Paralelo:")); cbPar=new JComboBox<>(new String[]{"A","B","C"}); top.add(cbPar); JButton b=createStyledButton("Ver"); b.addActionListener(e->refresh()); top.add(b); add(top, BorderLayout.NORTH); area=new JTextArea(); area.setEditable(false); add(new JScrollPane(area), BorderLayout.CENTER); } private void refresh(){ String p=(String)cbPar.getSelectedItem(); if(p==null||(!p.equalsIgnoreCase("A")&&!p.equalsIgnoreCase("B")&&!p.equalsIgnoreCase("C"))){ JOptionPane.showMessageDialog(frame,"No existe ese paralelo.","Error",JOptionPane.ERROR_MESSAGE); return; } List<Estudiante> lista = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).filter(e->e.getParalelo().equalsIgnoreCase(p)).collect(Collectors.toList()); if(lista.isEmpty()){ JOptionPane.showMessageDialog(frame,"No hay estudiantes registrados en este paralelo.","Info",JOptionPane.INFORMATION_MESSAGE); area.setText(""); return; } StringBuilder sb=new StringBuilder(); for(Estudiante e: lista){ sb.append("Nombre: ").append(e.getNombreCompleto()).append(" | Cant. logros: ").append(e.getLogros().size()).append("\n"); if(!e.getLogros().isEmpty()){ for(Logro l: e.getLogros()){ sb.append("   - ").append(l.getNombre()).append(" | ").append(l.getTipo()).append(" | ").append(l.getFechaObtenido()).append("\n"); } } sb.append("\n"); } area.setText(sb.toString()); } }
    private class AdminDarBajaPanel extends JPanel{ private JTable table; private DarBajaTableModel model; public AdminDarBajaPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); model=new DarBajaTableModel(); table=new JTable(model); add(new JScrollPane(table), BorderLayout.CENTER); table.addMouseListener(new MouseAdapter(){ public void mouseClicked(MouseEvent e){ int row = table.rowAtPoint(e.getPoint()); int col = table.columnAtPoint(e.getPoint()); if(row<0||col!=4) return; Estudiante est = model.getAt(row); if(est.estaActivo()){ est.desactivar(); JOptionPane.showMessageDialog(frame, "Estudiante desactivado.", "Info", JOptionPane.INFORMATION_MESSAGE); } else { try{ java.lang.reflect.Method m = est.getClass().getMethod("activar"); m.invoke(est); JOptionPane.showMessageDialog(frame, "Estudiante activado.", "Info", JOptionPane.INFORMATION_MESSAGE); } catch(Exception ex){ try{ java.lang.reflect.Field f = Usuario.class.getDeclaredField("estado"); f.setAccessible(true); f.set(est, "ACTIVO"); JOptionPane.showMessageDialog(frame, "Estudiante activado.", "Info", JOptionPane.INFORMATION_MESSAGE); } catch(Exception ex2){ JOptionPane.showMessageDialog(frame, "No se pudo cambiar el estado: " + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } } } model.refresh(); } }); JButton b=new JButton("Refrescar"); b.addActionListener(e->model.refresh()); add(b, BorderLayout.SOUTH); } private class DarBajaTableModel extends AbstractTableModel{ private List<Estudiante> datos=List.of(); private String[] cols={"Nombre completo","Paralelo","Cant. logros","Estado","Acción"}; public DarBajaTableModel(){ refresh(); } public void refresh(){ datos = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).collect(Collectors.toList()); fireTableDataChanged(); } public int getRowCount(){ return datos.size(); } public int getColumnCount(){ return cols.length; } public String getColumnName(int c){ return cols[c]; } public Object getValueAt(int r,int c){ Estudiante e=datos.get(r); switch(c){ case 0: return e.getNombreCompleto(); case 1: return e.getParalelo(); case 2: return e.getLogros().size(); case 3: return e.estaActivo()?"ACTIVO":"INACTIVO"; case 4: return e.estaActivo()?"Dar de baja":"Activar"; } return null; } public Estudiante getAt(int r){ return datos.get(r); } } }
    private class AdminReporteGlobalPanel extends JPanel{ private JTable table; public AdminReporteGlobalPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); table=new JTable(new AdminReporteTableModel()); add(new JScrollPane(table), BorderLayout.CENTER); JButton b=createStyledButton("Refrescar"); b.addActionListener(e-> ((AdminReporteTableModel)table.getModel()).refresh()); add(b, BorderLayout.SOUTH);} }
    private class AdminConfigPanel extends JPanel {
        private JSpinner spAlimentoA, spConsumoA, spRegenA;
        private JSpinner spAlimentoB, spConsumoB, spRegenB;
        private JSpinner spAlimentoC, spConsumoC, spRegenC;

        public AdminConfigPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(197, 202, 233));

            // Panel de tabla de configuración
            JPanel tablePanel = new JPanel(new GridBagLayout());
            tablePanel.setBackground(getBackground());
            tablePanel.setBorder(new EmptyBorder(12, 12, 12, 12));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 6, 6, 6);
            c.fill = GridBagConstraints.HORIZONTAL;

            // Encabezados
            c.gridx = 0; c.gridy = 0; tablePanel.add(new JLabel("Paralelo"), c);
            c.gridx = 1; tablePanel.add(new JLabel("Alimento disponible"), c);
            c.gridx = 2; tablePanel.add(new JLabel("Tasa de consumo"), c);
            c.gridx = 3; tablePanel.add(new JLabel("Regeneración vegetal"), c);

            // Paralelo A
            c.gridx = 0; c.gridy = 1; tablePanel.add(new JLabel("A"), c);
            spAlimentoA = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getAlimentoA(), 0, 100000, 100));
            spConsumoA = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getConsumoA(), 0, 1000, 1));
            spRegenA = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getRegeneracionA(), 0, 10000, 1));
            c.gridx = 1; tablePanel.add(spAlimentoA, c);
            c.gridx = 2; tablePanel.add(spConsumoA, c);
            c.gridx = 3; tablePanel.add(spRegenA, c);

            // Paralelo B
            c.gridx = 0; c.gridy = 2; tablePanel.add(new JLabel("B"), c);
            spAlimentoB = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getAlimentoB(), 0, 100000, 100));
            spConsumoB = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getConsumoB(), 0, 1000, 1));
            spRegenB = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getRegeneracionB(), 0, 10000, 1));
            c.gridx = 1; tablePanel.add(spAlimentoB, c);
            c.gridx = 2; tablePanel.add(spConsumoB, c);
            c.gridx = 3; tablePanel.add(spRegenB, c);

            // Paralelo C
            c.gridx = 0; c.gridy = 3; tablePanel.add(new JLabel("C"), c);
            spAlimentoC = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getAlimentoC(), 0, 100000, 100));
            spConsumoC = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getConsumoC(), 0, 1000, 1));
            spRegenC = new JSpinner(new SpinnerNumberModel(GeneradorEcosistema.getRegeneracionC(), 0, 10000, 1));
            c.gridx = 1; tablePanel.add(spAlimentoC, c);
            c.gridx = 2; tablePanel.add(spConsumoC, c);
            c.gridx = 3; tablePanel.add(spRegenC, c);

            add(tablePanel, BorderLayout.NORTH);

            // Panel de botones
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            botones.setBackground(getBackground());
            JButton btnAplicar = createStyledButton("Aplicar cambios");
            JButton btnRestaurar = createStyledButton("Restaurar valores por defecto");
            btnRestaurar.setBackground(new Color(255, 193, 7)); // Amarillo para destacar

            btnAplicar.addActionListener(e -> doAplicar());
            btnRestaurar.addActionListener(e -> doRestaurar());

            botones.add(btnAplicar);
            botones.add(btnRestaurar);
            add(botones, BorderLayout.CENTER);
        }

        private void doAplicar() {
            try {
                int alimentoA = (Integer) spAlimentoA.getValue();
                int consumoA = (Integer) spConsumoA.getValue();
                int regenA = (Integer) spRegenA.getValue();

                int alimentoB = (Integer) spAlimentoB.getValue();
                int consumoB = (Integer) spConsumoB.getValue();
                int regenB = (Integer) spRegenB.getValue();

                int alimentoC = (Integer) spAlimentoC.getValue();
                int consumoC = (Integer) spConsumoC.getValue();
                int regenC = (Integer) spRegenC.getValue();

                // Validación de coherencia (advertencias informativas)
                StringBuilder advertencias = new StringBuilder();

                // Paralelo A
                if (consumoA > 0 && regenA > 0 && (double) consumoA / regenA > 5) {
                    advertencias.append("Paralelo A: Tasa de consumo muy alta vs regeneración. El ecosistema probablemente colapse rápido.\n");
                } else if (regenA > 0 && consumoA > 0 && (double) regenA / consumoA > 50) {
                    advertencias.append("Paralelo A: Regeneración muy alta vs consumo. La simulación será casi imposible de perder.\n");
                }

                // Paralelo B
                if (consumoB > 0 && regenB > 0 && (double) consumoB / regenB > 5) {
                    advertencias.append("Paralelo B: Tasa de consumo muy alta vs regeneración. El ecosistema probablemente colapse rápido.\n");
                } else if (regenB > 0 && consumoB > 0 && (double) regenB / consumoB > 50) {
                    advertencias.append("Paralelo B: Regeneración muy alta vs consumo. La simulación será casi imposible de perder.\n");
                }

                // Paralelo C
                if (consumoC > 0 && regenC > 0 && (double) consumoC / regenC > 5) {
                    advertencias.append("Paralelo C: Tasa de consumo muy alta vs regeneración. El ecosistema probablemente colapse rápido.\n");
                } else if (regenC > 0 && consumoC > 0 && (double) regenC / consumoC > 50) {
                    advertencias.append("Paralelo C: Regeneración muy alta vs consumo. La simulación será casi imposible de perder.\n");
                }

                if (advertencias.length() > 0) {
                    int resp = JOptionPane.showConfirmDialog(frame,
                            advertencias.toString() + "\n¿Deseas guardar de todas formas?",
                            "Advertencia de coherencia",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (resp != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // Guardar los valores
                GeneradorEcosistema.setAlimentoA(alimentoA);
                GeneradorEcosistema.setConsumoA(consumoA);
                GeneradorEcosistema.setRegeneracionA(regenA);

                GeneradorEcosistema.setAlimentoB(alimentoB);
                GeneradorEcosistema.setConsumoB(consumoB);
                GeneradorEcosistema.setRegeneracionB(regenB);

                GeneradorEcosistema.setAlimentoC(alimentoC);
                GeneradorEcosistema.setConsumoC(consumoC);
                GeneradorEcosistema.setRegeneracionC(regenC);

                JOptionPane.showMessageDialog(frame, "Parámetros por paralelo configurados exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al guardar parámetros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void doRestaurar() {
            int resp = JOptionPane.showConfirmDialog(frame,
                    "¿Estás seguro de que deseas restaurar todos los parámetros a sus valores por defecto?",
                    "Confirmar restauración",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (resp == JOptionPane.YES_OPTION) {
                GeneradorEcosistema.restaurarValoresPorDefecto();

                // Actualizar spinners con los nuevos valores
                spAlimentoA.setValue(GeneradorEcosistema.getAlimentoA());
                spConsumoA.setValue(GeneradorEcosistema.getConsumoA());
                spRegenA.setValue(GeneradorEcosistema.getRegeneracionA());

                spAlimentoB.setValue(GeneradorEcosistema.getAlimentoB());
                spConsumoB.setValue(GeneradorEcosistema.getConsumoB());
                spRegenB.setValue(GeneradorEcosistema.getRegeneracionB());

                spAlimentoC.setValue(GeneradorEcosistema.getAlimentoC());
                spConsumoC.setValue(GeneradorEcosistema.getConsumoC());
                spRegenC.setValue(GeneradorEcosistema.getRegeneracionC());

                JOptionPane.showMessageDialog(frame, "Valores restaurados a sus configuraciones por defecto.", "Restauración completada", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    private class AdminExportPanel extends JPanel{ private JTable table; public AdminExportPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); table = new JTable(new AdminExportTableModel()); add(new JScrollPane(table), BorderLayout.CENTER); JButton b=createStyledButton("Refrescar"); b.addActionListener(e-> ((AdminExportTableModel)table.getModel()).refresh()); add(b, BorderLayout.SOUTH); } private class AdminExportTableModel extends AbstractTableModel{ private List<Estudiante> datos=List.of(); private String[] cols={"Nombre completo","Cédula","Correo","Paralelo","Estado","Sim exitosas","Rango"}; public void refresh(){ datos = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).collect(Collectors.toList()); fireTableDataChanged(); } public int getRowCount(){ return datos.size(); } public int getColumnCount(){ return cols.length; } public String getColumnName(int c){ return cols[c]; } public Object getValueAt(int r,int c){ Estudiante e = datos.get(r); switch(c){ case 0: return e.getNombreCompleto(); case 1: return e.getCedula(); case 2: return e.getCorreo(); case 3: return e.getParalelo(); case 4: return e.estaActivo()?"ACTIVO":"INACTIVO"; case 5: return e.getSimulacionesExitosas(); case 6: return e.obtenerRecompensa(); } return null; } } }
    private class AdminRankingPanel extends JPanel{ private JTable table; public AdminRankingPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); table=new JTable(new RankingTableModel()); add(new JScrollPane(table), BorderLayout.CENTER); JButton b=createStyledButton("Refrescar"); b.addActionListener(e-> ((RankingTableModel)table.getModel()).refresh()); add(b, BorderLayout.SOUTH);} }

    // ---- TableModels for admin ----
    private class AdminUserTableModel extends AbstractTableModel{
        private List<Estudiante> datos=List.of();
        private String[] cols={"Nombre","Cédula","Paralelo","Usuario"};
        public void setList(List<Estudiante> l){ datos = l==null?List.of():l; fireTableDataChanged(); }
        public int getRowCount(){ return datos.size(); }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int c){ return cols[c]; }
        public Object getValueAt(int r,int c){ Estudiante e=datos.get(r); switch(c){ case 0: return e.getNombreCompleto(); case 1: return e.getCedula(); case 2: return e.getParalelo(); case 3: return e.getNombreUsuario(); } return null; }
    }
    private class AdminReporteTableModel extends AbstractTableModel{
        private List<Estudiante> datos=List.of();
        private String[] cols={"Nombre completo","Paralelo","Simulaciones exitosas"};
        public void refresh(){ datos = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).collect(Collectors.toList()); fireTableDataChanged(); }
        public int getRowCount(){ return datos.size(); }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int c){ return cols[c]; }
        public Object getValueAt(int r,int c){ Estudiante e = datos.get(r); switch(c){ case 0: return e.getNombreCompleto(); case 1: return e.getParalelo(); case 2: return e.getSimulacionesExitosas(); } return null; }
    }
    private class RankingTableModel extends AbstractTableModel{
        private List<Estudiante> datos=List.of();
        private String[] cols={"Posición","Nombre completo","Paralelo","Sim exitosas","Rango"};
        public void refresh(){ datos = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).sorted((a,b)->Integer.compare(b.getSimulacionesExitosas(),a.getSimulacionesExitosas())).collect(Collectors.toList()); fireTableDataChanged(); }
        public int getRowCount(){ return datos.size(); }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int c){ return cols[c]; }
        public Object getValueAt(int r,int c){ Estudiante e=datos.get(r); switch(c){ case 0: return r+1; case 1: return e.getNombreCompleto(); case 2: return e.getParalelo(); case 3: return e.getSimulacionesExitosas(); case 4: return e.obtenerRecompensa(); } return null; }
    }

    // ----------------------- Utility UI methods -----------------------
    // DocumentFilter que permite solo dígitos
    private class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            StringBuilder sb = new StringBuilder();
            for (char c : string.toCharArray()) if (Character.isDigit(c)) sb.append(c);
            super.insertString(fb, offset, sb.toString(), attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) if (Character.isDigit(c)) sb.append(c);
            super.replace(fb, offset, length, sb.toString(), attrs);
        }
    }
    private JButton createStyledButton(String text){ JButton b=new JButton(text); b.setBackground(new Color(102,187,106)); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setBorder(BorderFactory.createLineBorder(new Color(76,175,80))); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.addMouseListener(new MouseAdapter(){ public void mouseEntered(MouseEvent e){ b.setBackground(new Color(76,175,80)); } public void mouseExited(MouseEvent e){ b.setBackground(new Color(102,187,106)); } }); return b; }
    private JButton createNavButton(String text){ JButton b=createStyledButton(text); b.setBackground(new Color(67,160,71)); b.addMouseListener(new MouseAdapter(){ public void mouseEntered(MouseEvent e){ b.setBackground(new Color(46,125,50)); } public void mouseExited(MouseEvent e){ b.setBackground(new Color(67,160,71)); } }); return b; }

    private GridBagConstraints grid(GridBagConstraints c,int x,int y){ GridBagConstraints nc=(GridBagConstraints)c.clone(); nc.gridx=x; nc.gridy=y; return nc; }

    // ----------------------- main -----------------------
    public static void main(String[] args){ new InterfazGrafica(); }
}








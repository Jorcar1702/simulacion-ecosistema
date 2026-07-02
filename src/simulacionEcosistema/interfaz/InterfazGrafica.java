package simulacionEcosistema.interfaz;

import simulacionEcosistema.modelo.*;
import simulacionEcosistema.negocio.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    // Estado compartido (simula variables estáticas de MainSimulacion)
    private static GestorUsuario gestorUsuarios;
    private static GestorSimulacion motorSimulacion;
    private static Estudiante estudianteActivo;
    private static Administrador administradorActivo;

    private JFrame frame;
    private CardLayout mainLayout;
    private JPanel mainPanel;

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
        mainPanel.add(new LoginPanel(), P_LOGIN);
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
                // al iniciar sesión, crear motorSimulacion base si no existe
                motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(estudianteActivo, 10);
                try { motorSimulacion.iniciar(); motorSimulacion.terminarManualmente(); } catch (Exception ex) { /* no-op */ }
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
        private CardLayout areaLayout;
        private JPanel areaPanel;

        public StudentMainPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(232, 245, 233));

            JPanel top = new JPanel(new BorderLayout()); top.setBackground(new Color(56,142,60)); top.setBorder(new EmptyBorder(8,12,8,12));
            JLabel lbl = new JLabel("\uD83C\uDF32 Estudiante: "); lbl.setForeground(Color.WHITE); lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
            top.add(lbl, BorderLayout.WEST);
            JButton btnLogout = createStyledButton("Cerrar Sesión"); btnLogout.setBackground(new Color(200,0,0)); btnLogout.addActionListener(e -> { estudianteActivo=null; motorSimulacion=null; showPanel(P_LOGIN); });
            top.add(btnLogout, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JPanel left = new JPanel(); left.setLayout(new GridLayout(0,1,6,6)); left.setBackground(new Color(200,230,201)); left.setBorder(new EmptyBorder(10,10,10,10));
            String[] opts = new String[]{"Agregar Especie 🐇","Definir Interacción 🐺","Iniciar/Configurar Simulación ⚙️","Ejecutar Turnos ▶️","Estado Actual 🌿","Guardar Simulación 💾","Historial 📜","Exportar Reporte 📄","Ver mis Logros 🏆"};
            for (String o : opts) {
                JButton b = createNavButton(o); left.add(b);
            }
            add(left, BorderLayout.WEST);

            areaLayout = new CardLayout(); areaPanel = new JPanel(areaLayout);
            areaPanel.add(new AddSpeciesPanel(), "ADD_SPECIES");
            areaPanel.add(new InteractionsPanel(), "INTERACTIONS");
            areaPanel.add(new SimControlPanel(), "SIM_CONTROL");
            areaPanel.add(new ExecuteTurnPanel(), "EXECUTE");
            areaPanel.add(new StatePanel(), "STATE");
            areaPanel.add(new SavePanel(), "SAVE");
            areaPanel.add(new HistoryPanel(), "HISTORY");
            areaPanel.add(new ExportPanel(), "EXPORT");
            areaPanel.add(new AchievementsPanel(), "ACHIEVEMENTS");

            add(areaPanel, BorderLayout.CENTER);

            // wire nav buttons
            Component[] buttons = left.getComponents();
            String[] keys = new String[]{"ADD_SPECIES","INTERACTIONS","SIM_CONTROL","EXECUTE","STATE","SAVE","HISTORY","EXPORT","ACHIEVEMENTS"};
            for (int i=0;i<buttons.length;i++) {
                int idx = i; ((JButton)buttons[i]).addActionListener(e -> areaLayout.show(areaPanel, keys[idx]));
            }
        }
    }

    // ---- AddSpeciesPanel: CRUD table and form ----
    private class AddSpeciesPanel extends JPanel {
        private JTextField tfNombre; private JComboBox<String> cbTipo; private JSpinner spCantidad, spCapacidad;
        private JButton btnAgregar, btnEliminar, btnGuardar;
        private JTable table; private PoblacionTableModel tableModel;

        public AddSpeciesPanel() {
            setLayout(new BorderLayout()); setBackground(new Color(237, 245, 233));
            JPanel form = new JPanel(new GridBagLayout()); form.setBorder(new EmptyBorder(12,12,12,12)); form.setBackground(getBackground());
            GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6); c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx=0; c.gridy=0; form.add(new JLabel("Nombre de la especie:"), c); tfNombre = new JTextField(12); c.gridx=1; form.add(tfNombre,c);
            c.gridx=0; c.gridy=1; form.add(new JLabel("Tipo:"), c); cbTipo = new JComboBox<>(new String[]{"Herbívoro","Carnívoro","Planta"}); c.gridx=1; form.add(cbTipo,c);
            c.gridx=0; c.gridy=2; form.add(new JLabel("Cantidad inicial:"), c); spCantidad = new JSpinner(new SpinnerNumberModel(1,0,100000,1)); c.gridx=1; form.add(spCantidad,c);
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
            boolean activa = motorSimulacion!=null && motorSimulacion.getSimulacion()!=null && motorSimulacion.getSimulacion().isActiva();
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
            boolean activa = motorSimulacion!=null && motorSimulacion.getSimulacion()!=null && motorSimulacion.getSimulacion().isActiva();
            btnGuardar.setEnabled(!activa); btnEliminar.setEnabled(!activa);
        }

        private void doAgregar() {
            if (motorSimulacion==null) { JOptionPane.showMessageDialog(frame, "No hay simulación. Inicia o configura el ecosistema primero.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            if (motorSimulacion.getSimulacion()!=null && motorSimulacion.getSimulacion().isActiva()) { JOptionPane.showMessageDialog(frame, "No puede agregar especies mientras la simulación está activa.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            String nombre = tfNombre.getText().trim(); String tipo = (String)cbTipo.getSelectedItem(); int cantidad = (Integer)spCantidad.getValue(); int capacidad = (Integer)spCapacidad.getValue();
            if (nombre.isEmpty()) { JOptionPane.showMessageDialog(frame, "Nombre requerido.", "Error", JOptionPane.ERROR_MESSAGE); return; }
            Poblacion p = GeneradorEcosistema.crearEspecieConPoblacion(nombre,tipo,cantidad,capacidad);
            motorSimulacion.registrarPoblacion(p);
            refreshTable(); clearForm();
        }

        private void doEliminar() {
            int r = table.getSelectedRow(); if (r==-1) return;
            Poblacion p = tableModel.getPoblacionAt(r);
            if (motorSimulacion.getSimulacion()!=null && motorSimulacion.getSimulacion().isActiva()) { JOptionPane.showMessageDialog(frame, "No puede eliminar mientras simulación activa.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            // eliminar directamente del modelo
            motorSimulacion.getSimulacion().getPoblaciones().removeIf(pp->pp.getEspecie().getNombre().equalsIgnoreCase(p.getEspecie().getNombre()));
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
        private void refreshTable() { if (motorSimulacion==null || motorSimulacion.getSimulacion()==null) tableModel.setPoblaciones(List.of()); else tableModel.setPoblaciones(motorSimulacion.getSimulacion().getPoblaciones()); updateFormEnabled(); }

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

    // ---- InteractionsPanel: simple form + table listing interactions ----
    private class InteractionsPanel extends JPanel {
        private JTextField tfDep, tfPresa; private JSpinner spFactor; private JSpinner spEfic;
        private JTable table; private InterTableModel model;
        public InteractionsPanel() {
            setLayout(new BorderLayout()); setBackground(new Color(232, 245, 233));
            JPanel form = new JPanel(new GridBagLayout()); form.setBorder(new EmptyBorder(12,12,12,12)); form.setBackground(getBackground());
            GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL;
            c.gridx=0;c.gridy=0; form.add(new JLabel("Depredador:"),c); tfDep=new JTextField(12); c.gridx=1; form.add(tfDep,c);
            c.gridx=0;c.gridy=1; form.add(new JLabel("Presa:"),c); tfPresa=new JTextField(12); c.gridx=1; form.add(tfPresa,c);
            c.gridx=0;c.gridy=2; form.add(new JLabel("Factor de caza:"),c); spFactor=new JSpinner(new SpinnerNumberModel(0.1,0.0,10.0,0.1)); c.gridx=1; form.add(spFactor,c);
            c.gridx=0;c.gridy=3; form.add(new JLabel("Eficiencia:"),c); spEfic=new JSpinner(new SpinnerNumberModel(0.1,0.0,1.0,0.1)); c.gridx=1; form.add(spEfic,c);
            JButton btnReg = createStyledButton("Registrar Interacción"); btnReg.addActionListener(e->doRegistrar()); form.add(btnReg, grid(c,1,4));
            add(form, BorderLayout.NORTH);
            model = new InterTableModel(); table = new JTable(model); add(new JScrollPane(table), BorderLayout.CENTER);
            refresh();
        }
        private GridBagConstraints grid(GridBagConstraints c,int x,int y){ GridBagConstraints nc=(GridBagConstraints)c.clone(); nc.gridx=x; nc.gridy=y; return nc; }
        private void doRegistrar(){ if (motorSimulacion==null) { JOptionPane.showMessageDialog(frame,"No hay simulación activa.","Info",JOptionPane.INFORMATION_MESSAGE); return;} String dep=tfDep.getText().trim(), presa=tfPresa.getText().trim(); if(dep.isEmpty()||presa.isEmpty()){ JOptionPane.showMessageDialog(frame,"Campos requeridos.","Error",JOptionPane.ERROR_MESSAGE); return;} if(dep.equalsIgnoreCase(presa)){ JOptionPane.showMessageDialog(frame,"Una especie no puede ser depredadora de sí misma.","Error",JOptionPane.ERROR_MESSAGE); return;} double factor = ((Number)spFactor.getValue()).doubleValue(); double efic = ((Number)spEfic.getValue()).doubleValue(); Interaccion inter = new Interaccion(dep,presa,factor,efic); motorSimulacion.registrarInteraccion(inter); refresh(); }
        private void refresh(){ if (motorSimulacion==null||motorSimulacion.getSimulacion()==null) model.setList(List.of()); else model.setList(motorSimulacion.getSimulacion().getInteracciones()); }
        private class InterTableModel extends AbstractTableModel{ private List<Interaccion> datos=List.of(); private String[] cols={"Depredador","Presa","Factor","Eficiencia"}; public void setList(List<Interaccion> l){ this.datos=l==null?List.of():l; fireTableDataChanged(); } public int getRowCount(){ return datos.size(); } public int getColumnCount(){ return cols.length;} public String getColumnName(int c){ return cols[c]; } public Object getValueAt(int r,int c){ Interaccion i = datos.get(r); switch(c){ case 0: return i.getNombreDepredador(); case 1: return i.getNombrePresa(); case 2: return i.getTasaExito(); case 3: return i.getFactorAfectacion(); } return null; } }
    }

    // ---- SimControlPanel: iniciar/configurar ----
    private class SimControlPanel extends JPanel {
        private JSpinner spTurnos; private JButton btnCrear;
        public SimControlPanel(){ setLayout(new FlowLayout(FlowLayout.LEFT)); setBackground(new Color(237, 245, 233)); add(new JLabel("Número de turnos:")); spTurnos=new JSpinner(new SpinnerNumberModel(10,1,10000,1)); add(spTurnos); btnCrear=createStyledButton("Crear/Configurar"); add(btnCrear); btnCrear.addActionListener(e->doCrear()); }
        private void doCrear(){ if (estudianteActivo==null){ JOptionPane.showMessageDialog(frame,"Debes iniciar sesión como estudiante.","Error",JOptionPane.ERROR_MESSAGE); return;} int t = (Integer)spTurnos.getValue(); motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(estudianteActivo,t); try{ motorSimulacion.iniciar(); JOptionPane.showMessageDialog(frame,"Simulación inicializada.","Info",JOptionPane.INFORMATION_MESSAGE); } catch(Exception ex){ JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} }
    }

    // ---- ExecuteTurnPanel ----
    private class ExecuteTurnPanel extends JPanel { private JButton btnTurno, btnTodos; public ExecuteTurnPanel(){ setBackground(new Color(237, 245, 233)); btnTurno=createStyledButton("Ejecutar 1 Turno"); btnTodos=createStyledButton("Ejecutar Todos"); add(btnTurno); add(btnTodos); btnTurno.addActionListener(e->doTurno()); btnTodos.addActionListener(e->doTodos()); }
        private void doTurno(){ if(motorSimulacion==null){ JOptionPane.showMessageDialog(frame,"No hay simulación activa.","Error",JOptionPane.ERROR_MESSAGE); return;} try{ motorSimulacion.ejecutarTurno(); JOptionPane.showMessageDialog(frame,"Turno ejecutado.","Info",JOptionPane.INFORMATION_MESSAGE); } catch(Exception ex){ JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} }
        private void doTodos(){ if(motorSimulacion==null){ JOptionPane.showMessageDialog(frame,"No hay simulación activa.","Error",JOptionPane.ERROR_MESSAGE); return;} try{ motorSimulacion.ejecutarTodosLosTurnos(); JOptionPane.showMessageDialog(frame,"Todos los turnos ejecutados.","Info",JOptionPane.INFORMATION_MESSAGE); } catch(Exception ex){ JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} }
    }

    // ---- StatePanel ----
    private class StatePanel extends JPanel { private JTable table; public StatePanel(){ setLayout(new BorderLayout()); setBackground(new Color(237, 245, 233)); table = new JTable(new StateTableModel()); add(new JScrollPane(table), BorderLayout.CENTER); JButton btnRefresh = createStyledButton("Refrescar"); btnRefresh.addActionListener(e-> ((StateTableModel)table.getModel()).refresh()); add(btnRefresh, BorderLayout.SOUTH); } private class StateTableModel extends AbstractTableModel{ private List<Poblacion> datos=List.of(); private String[] cols={"Especie","Tipo","Cantidad"}; public void refresh(){ if(motorSimulacion==null||motorSimulacion.getSimulacion()==null) datos=List.of(); else datos=motorSimulacion.getSimulacion().getPoblaciones(); fireTableDataChanged(); } public int getRowCount(){ return datos.size(); } public int getColumnCount(){ return cols.length;} public String getColumnName(int c){ return cols[c]; } public Object getValueAt(int r,int c){ Poblacion p=datos.get(r); switch(c){ case 0: return p.getEspecie().getNombre(); case 1: return p.getEspecie().getTipo(); case 2: return p.getCantidad(); } return null;} }
    }

    // ---- SavePanel ----
    private class SavePanel extends JPanel { public SavePanel(){ setLayout(new FlowLayout(FlowLayout.LEFT)); setBackground(new Color(237, 245, 233)); JButton btnSave=createStyledButton("Guardar Simulación"); btnSave.addActionListener(e-> doSave()); add(btnSave);} private void doSave(){ if(estudianteActivo==null||motorSimulacion==null||motorSimulacion.getSimulacion()==null){ JOptionPane.showMessageDialog(frame,"No hay simulación para guardar.","Info",JOptionPane.INFORMATION_MESSAGE); return;} estudianteActivo.registrarSimulacion(motorSimulacion.getSimulacion()); GestorLogros.evaluarLogros(estudianteActivo,motorSimulacion.getSimulacion()); JOptionPane.showMessageDialog(frame,"Simulación guardada.","Info",JOptionPane.INFORMATION_MESSAGE);} }

    // ---- HistoryPanel ----
    private class HistoryPanel extends JPanel { private JList<String> list; private DefaultListModel<String> model; private JTextArea area; public HistoryPanel(){ setLayout(new BorderLayout()); setBackground(new Color(237, 245, 233)); model=new DefaultListModel<>(); list=new JList<>(model); list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); add(new JScrollPane(list), BorderLayout.WEST); area=new JTextArea(); area.setEditable(false); add(new JScrollPane(area), BorderLayout.CENTER); list.addListSelectionListener(e-> showDetail()); JButton btnRef=createStyledButton("Refrescar"); btnRef.addActionListener(e->refresh()); add(btnRef, BorderLayout.SOUTH); refresh(); } private void refresh(){ model.clear(); if(estudianteActivo!=null){ int i=1; for(Simulacion s: estudianteActivo.getHistorialSimulaciones()){ model.addElement("["+i+"] "+s.getFechaFormateada()+" - "+s.getResultado()); i++; } } } private void showDetail(){ int idx=list.getSelectedIndex(); if(idx==-1) return; Simulacion s = estudianteActivo.getHistorialSimulaciones().get(idx); StringBuilder sb=new StringBuilder(); sb.append("Fecha: ").append(s.getFechaFormateada()).append("\n"); sb.append("Resultado: ").append(s.getResultado()).append("\n\n"); for(String t: s.getBitacoraTurnos()) sb.append(t).append("\n"); area.setText(sb.toString()); } }

    // ---- ExportPanel ----
    private class ExportPanel extends JPanel { public ExportPanel(){ setLayout(new FlowLayout(FlowLayout.LEFT)); setBackground(new Color(237, 245, 233)); JButton b=createStyledButton("Exportar Reporte a TXT"); b.addActionListener(e->exportReport()); add(b);} private void exportReport(){ if(motorSimulacion==null){ JOptionPane.showMessageDialog(frame,"No hay simulación para exportar.","Info",JOptionPane.INFORMATION_MESSAGE); return;} String reporte = motorSimulacion.generarReporteTexto(); // write to file - simplified: show success
            try { java.nio.file.Files.writeString(java.nio.file.Path.of("reporte_simulacion.txt"), reporte); JOptionPane.showMessageDialog(frame,"Reporte exportado a reporte_simulacion.txt","Info",JOptionPane.INFORMATION_MESSAGE); } catch(Exception ex){ JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} } }

    // ---- AchievementsPanel ----
    private class AchievementsPanel extends JPanel { private DefaultListModel<String> model; private JList<String> list; public AchievementsPanel(){ setLayout(new BorderLayout()); setBackground(new Color(237, 245, 233)); model=new DefaultListModel<>(); list=new JList<>(model); add(new JScrollPane(list), BorderLayout.CENTER); JButton b=createStyledButton("Refrescar"); b.addActionListener(e->refresh()); add(b, BorderLayout.SOUTH); refresh(); } private void refresh(){ model.clear(); if(estudianteActivo!=null){ for(Logro l: estudianteActivo.getLogros()){ model.addElement(l.getNombre()+" - "+l.getDescripcion()+" ("+l.getFechaObtenido()+")"); } } } }

    // ----------------------- Admin Main Panel -----------------------
        private class AdminMainPanel extends JPanel {
        private CardLayout adminArea; private JPanel adminAreaPanel;
        public AdminMainPanel(){ setLayout(new BorderLayout()); setBackground(new Color(232, 234, 246)); JPanel top=new JPanel(new BorderLayout()); top.setBackground(new Color(30, 136, 229)); top.setBorder(new EmptyBorder(8,12,8,12)); JLabel lbl=new JLabel("Administrador"); lbl.setForeground(Color.WHITE); lbl.setFont(new Font("SansSerif", Font.BOLD, 16)); top.add(lbl, BorderLayout.WEST); JButton btnLogout=createStyledButton("Cerrar Sesión"); btnLogout.setBackground(new Color(200,0,0)); btnLogout.addActionListener(e->{ administradorActivo=null; showPanel(P_LOGIN); }); top.add(btnLogout, BorderLayout.EAST); add(top, BorderLayout.NORTH);
            JPanel left=new JPanel(new GridLayout(0,1,6,6)); left.setBackground(new Color(197, 202, 233)); left.setBorder(new EmptyBorder(10,10,10,10)); String[] opts={"Listar por Paralelo","Buscar por Cédula","Ver Logros","Dar de Baja","Reporte Global","Configurar Parámetros","Exportar Listado","Ver Ranking"}; for(String s:opts) left.add(createNavButton(s)); add(left, BorderLayout.WEST);
            adminArea=new CardLayout(); adminAreaPanel=new JPanel(adminArea); adminAreaPanel.add(new AdminListByParaleloPanel(),"PARAL" ); adminAreaPanel.add(new AdminSearchPanel(),"SEARCH"); adminAreaPanel.add(new AdminVerLogrosPanel(),"LOGROS"); adminAreaPanel.add(new AdminDarBajaPanel(),"BAJA"); adminAreaPanel.add(new AdminReporteGlobalPanel(),"REPORTE"); adminAreaPanel.add(new AdminConfigPanel(),"CONFIG"); adminAreaPanel.add(new AdminExportPanel(),"EXPORT"); adminAreaPanel.add(new AdminRankingPanel(),"RANK"); add(adminAreaPanel, BorderLayout.CENTER);
            Component[] bs = left.getComponents(); String[] keys = new String[]{"PARAL","SEARCH","LOGROS","BAJA","REPORTE","CONFIG","EXPORT","RANK"}; for(int i=0;i<bs.length;i++){ int idx=i; ((JButton)bs[i]).addActionListener(e->adminArea.show(adminAreaPanel, keys[idx])); }
        }
    }

    private class AdminListByParaleloPanel extends JPanel{ private JTable table; private JTextField tfFiltro; public AdminListByParaleloPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground()); top.add(new JLabel("Paralelo:")); tfFiltro=new JTextField(10); top.add(tfFiltro); JButton b=createStyledButton("Filtrar"); b.addActionListener(e->refresh()); top.add(b); add(top, BorderLayout.NORTH); table=new JTable(new AdminUserTableModel()); add(new JScrollPane(table), BorderLayout.CENTER); } private void refresh(){ String p=tfFiltro.getText().trim(); List<Estudiante> list = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).filter(est->p.isEmpty()||est.getParalelo().equalsIgnoreCase(p)).collect(Collectors.toList()); ((AdminUserTableModel)table.getModel()).setList(list);} }
    private class AdminSearchPanel extends JPanel{ private JTextField tfCed; private JTextArea area; public AdminSearchPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground()); top.add(new JLabel("Cédula:")); tfCed=new JTextField(12); top.add(tfCed); JButton b=createStyledButton("Buscar"); b.addActionListener(e->doBuscar()); top.add(b); add(top, BorderLayout.NORTH); area=new JTextArea(); area.setEditable(false); add(new JScrollPane(area), BorderLayout.CENTER); } private void doBuscar(){ String c=tfCed.getText().trim(); if(c.isEmpty()){ JOptionPane.showMessageDialog(frame,"Ingrese cédula.","Error",JOptionPane.ERROR_MESSAGE); return;} Usuario u = gestorUsuarios.buscarPorCedula(c); if(u==null) { area.setText("No encontrado."); } else area.setText(u.toString()); } }
    private class AdminVerLogrosPanel extends JPanel{ private JTextField tfCed; private JTextArea area; public AdminVerLogrosPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(getBackground()); top.add(new JLabel("Cédula:")); tfCed=new JTextField(12); top.add(tfCed); JButton b=createStyledButton("Ver Logros"); b.addActionListener(e->doVer()); top.add(b); add(top, BorderLayout.NORTH); area=new JTextArea(); area.setEditable(false); add(new JScrollPane(area), BorderLayout.CENTER); } private void doVer(){ String c=tfCed.getText().trim(); if(c.isEmpty()){ JOptionPane.showMessageDialog(frame,"Ingrese cédula.","Error",JOptionPane.ERROR_MESSAGE); return;} Usuario u = gestorUsuarios.buscarPorCedula(c); if(u instanceof Estudiante est){ area.setText(GestorLogros.obtenerLogrosPorEstudiante(est)); } else { area.setText("No se encontró un estudiante con esa cédula."); } } }
    private class AdminDarBajaPanel extends JPanel{ private JTextField tfCed; public AdminDarBajaPanel(){ setLayout(new FlowLayout(FlowLayout.LEFT)); setBackground(new Color(197, 202, 233)); add(new JLabel("Cédula:")); tfCed=new JTextField(12); add(tfCed); JButton b=createStyledButton("Dar de baja"); b.addActionListener(e-> doBaja()); add(b);} private void doBaja(){ String c=tfCed.getText().trim(); if(c.isEmpty()){ JOptionPane.showMessageDialog(frame,"Ingrese cédula.","Error",JOptionPane.ERROR_MESSAGE); return;} String r=gestorUsuarios.darDeBaja(c); JOptionPane.showMessageDialog(frame,r,"Resultado",JOptionPane.INFORMATION_MESSAGE); } }
    private class AdminReporteGlobalPanel extends JPanel{ private JTable table; public AdminReporteGlobalPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); table=new JTable(new AdminReporteTableModel()); add(new JScrollPane(table), BorderLayout.CENTER); JButton b=createStyledButton("Refrescar"); b.addActionListener(e-> ((AdminReporteTableModel)table.getModel()).refresh()); add(b, BorderLayout.SOUTH);} }
    private class AdminConfigPanel extends JPanel{ private JSpinner spTasa; public AdminConfigPanel(){ setLayout(new FlowLayout(FlowLayout.LEFT)); setBackground(new Color(197, 202, 233)); add(new JLabel("Nueva tasa de regeneración vegetal:")); spTasa=new JSpinner(new SpinnerNumberModel(1,0,1000,1)); add(spTasa); JButton b=createStyledButton("Aplicar"); b.addActionListener(e-> doApply()); add(b);} private void doApply(){ int tasa=(Integer)spTasa.getValue(); try{ if(motorSimulacion==null) motorSimulacion = GeneradorEcosistema.crearEcosistemaBase(null,10); motorSimulacion.setRegeneracionVegetal(tasa); JOptionPane.showMessageDialog(frame,"Parámetros globales configurados.","Info",JOptionPane.INFORMATION_MESSAGE);} catch(Exception ex){ JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} } }
    private class AdminExportPanel extends JPanel{ public AdminExportPanel(){ setLayout(new FlowLayout(FlowLayout.LEFT)); setBackground(new Color(197, 202, 233)); JButton b=createStyledButton("Exportar listado de estudiantes"); b.addActionListener(e-> doExport()); add(b);} private void doExport(){ try{ StringBuilder sb=new StringBuilder(); for(Usuario u:gestorUsuarios.getListaUsuarios()) sb.append(u.toString()).append("\n"); java.nio.file.Files.writeString(java.nio.file.Path.of("listado_estudiantes.txt"), sb.toString()); JOptionPane.showMessageDialog(frame,"Listado exportado a listado_estudiantes.txt","Info",JOptionPane.INFORMATION_MESSAGE);} catch(Exception ex){ JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} } }
    private class AdminRankingPanel extends JPanel{ private JTable table; public AdminRankingPanel(){ setLayout(new BorderLayout()); setBackground(new Color(197, 202, 233)); table=new JTable(new RankingTableModel()); add(new JScrollPane(table), BorderLayout.CENTER); JButton b=createStyledButton("Refrescar"); b.addActionListener(e-> ((RankingTableModel)table.getModel()).refresh()); add(b, BorderLayout.SOUTH);} }

    // ---- TableModels for admin ----
    private class AdminUserTableModel extends AbstractTableModel{ private List<Estudiante> datos=List.of(); private String[] cols={"Nombre","Cédula","Paralelo","Usuario"}; public void setList(List<Estudiante> l){ datos = l==null?List.of():l; fireTableDataChanged(); } public int getRowCount(){ return datos.size(); } public int getColumnCount(){ return cols.length; } public String getColumnName(int c){ return cols[c]; } public Object getValueAt(int r,int c){ Estudiante e=datos.get(r); switch(c){ case 0: return e.getNombreCompleto(); case 1: return e.getCedula(); case 2: return e.getParalelo(); case 3: return e.getNombreUsuario(); } return null; } }
    private class AdminReporteTableModel extends AbstractTableModel{ private List<Usuario> datos=List.of(); private String[] cols={"Nombre","Rol","Paralelo","Sim exitosas"}; public void refresh(){ datos = gestorUsuarios.getListaUsuarios(); fireTableDataChanged(); } public int getRowCount(){ return datos.size(); } public int getColumnCount(){ return cols.length; } public String getColumnName(int c){ return cols[c]; } public Object getValueAt(int r,int c){ Usuario u = datos.get(r); switch(c){ case 0: return u.getNombreCompleto(); case 1: return u instanceof Estudiante?"Estudiante":"Administrador"; case 2: return u instanceof Estudiante?((Estudiante)u).getParalelo():"-"; case 3: return u instanceof Estudiante?((Estudiante)u).getSimulacionesExitosas():"-"; } return null;} }
    private class RankingTableModel extends AbstractTableModel{ private List<Estudiante> datos=List.of(); private String[] cols={"Nombre","Sim exitosas","Rango"}; public void refresh(){ datos = gestorUsuarios.getListaUsuarios().stream().filter(u->u instanceof Estudiante).map(u->(Estudiante)u).sorted((a,b)->Integer.compare(b.getSimulacionesExitosas(),a.getSimulacionesExitosas())).collect(Collectors.toList()); fireTableDataChanged(); } public int getRowCount(){ return datos.size(); } public int getColumnCount(){ return cols.length; } public String getColumnName(int c){ return cols[c]; } public Object getValueAt(int r,int c){ Estudiante e=datos.get(r); switch(c){ case 0: return e.getNombreCompleto(); case 1: return e.getSimulacionesExitosas(); case 2: return e.obtenerRecompensa(); } return null;} }

    // ----------------------- Utility UI methods -----------------------
    private JButton createStyledButton(String text){ JButton b=new JButton(text); b.setBackground(new Color(102,187,106)); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setBorder(BorderFactory.createLineBorder(new Color(76,175,80))); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.addMouseListener(new MouseAdapter(){ public void mouseEntered(MouseEvent e){ b.setBackground(new Color(76,175,80)); } public void mouseExited(MouseEvent e){ b.setBackground(new Color(102,187,106)); } }); return b; }
    private JButton createNavButton(String text){ JButton b=createStyledButton(text); b.setBackground(new Color(67,160,71)); b.addMouseListener(new MouseAdapter(){ public void mouseEntered(MouseEvent e){ b.setBackground(new Color(46,125,50)); } public void mouseExited(MouseEvent e){ b.setBackground(new Color(67,160,71)); } }); return b; }

    private GridBagConstraints grid(GridBagConstraints c,int x,int y){ GridBagConstraints nc=(GridBagConstraints)c.clone(); nc.gridx=x; nc.gridy=y; return nc; }

    // ----------------------- main -----------------------
    public static void main(String[] args){ new InterfazGrafica(); }
}







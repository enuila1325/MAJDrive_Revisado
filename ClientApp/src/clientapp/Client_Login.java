/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clientapp;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ramon
 */
public class Client_Login extends javax.swing.JFrame {
    
    private JPanel globalPanel;

    /**
     * Creates new form Client_Login
     */
    public Client_Login() {
        
        initComponents();
        this.setLocationRelativeTo(null);
        addFileToContainer();
        jt_files.setRowHeight(37);
        //loadDirectory();

    }

    //función para determinar la imagen de archivo a usar
    public String getFileImage(String fileName) {
        String extension = getFileExtension(fileName);
        String imageFile = "";
        switch (extension.toUpperCase()) {
            case "PNG" ->
                imageFile = "png.png";
            case "XLSX" ->
                imageFile = "excel.png";
            case "TXT" ->
                imageFile = "txt.png";
            case "DOCX" ->
                imageFile = "docx.png";
            case "PDF" ->
                imageFile = "pdf.png";
            case "MP4" ->
                imageFile = "mp4.png";
            case "CSV" ->
                imageFile = "csv.png";
            case "JAR" ->
                imageFile = "jar.png";
            case "MP3" ->
                imageFile = "mp3.png";
            case "EXE" ->
                imageFile = "exe.png";
            case "PPT" ->
                imageFile = "ppt.png";
            case "ZIP" ->
                imageFile = "zip.png";
            
            case "RAR" ->
                imageFile = "rar.png";
            case "PY" ->
                imageFile = "python.png";
            
            default ->
                //añadir imagen de file cualquiera para los que no reconozca. 
                imageFile = "blankPage.png";
        }
        return imageFile;
    }

    //función para extraer las extensiones de los archivos
    public String getFileExtension(String fileName) {
        String extension = "";
        StringTokenizer splitString = new StringTokenizer(fileName, ".");
        splitString.nextToken();
        extension = splitString.nextToken();
        return extension;
    }

    //función para hacer el drag and drop
    public void addFileToContainer() {
        //jp_filesContainer = new JPanel(new GridLayout(0,5,10,10));
        //globalPanel = new JPanel(new GridLayout(0,5,10,10));

        //jp_filesContainer.setLayout(new GridLayout(0,5,10,10));
        // OBTENER EL MODELO DE LA TABLA
        DefaultTableModel model = (DefaultTableModel) jt_files.getModel();
        TransferHandler th = new TransferHandler() {
            @Override
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                return true;
            }
            
            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    //generalFilesSent = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    //generalFilesSent.addAll(files);
                    List<File> localFiles = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    
                    long fileSize;
                    double sizeKB;
                    String sizeColumn = ""; //string para concatenar el size del archivo con bytes
                    String imageSelect = "";
                    for (File file : localFiles) {
                        generalFilesSent.add(file);
                        // OBTENER EL SIZE DEL ARCHIVO
                        fileSize = file.length();
                        sizeKB = (double) fileSize / 1024;
                        
                        sizeColumn += (sizeKB + " KB");
                        imageSelect = getFileImage(file.getName()); // variable para determinar que icono se usará para representar el archivo

                        Object[] newRow = {imageSelect, file.getName(), sizeColumn};
                        sizeColumn = "";
                        
                        model.addRow(newRow); // añadir la nueva row en el modelo de la tabla
                    }
                    
                    jt_files.setModel(model);
                    jt_files.getColumnModel().getColumn(0).setCellRenderer(new ImageRender());
                    //jt_files.setRowHeight(40);

                    //para obtener el/los archivo(s)
                    /*if (files.size()==1) {
                        File file = files.get(0);
                        jl_file.setText(file.getName());
                    }*/
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            }
        };
        //jl_file.setTransferHandler(th);
        jp_filesContainer.setTransferHandler(th);
        
    }

    //función para renderizar la imagen
    private class ImageRender extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            String fileName = value.toString();
            ImageIcon imageIcon = new ImageIcon(
                    new ImageIcon("src/images/" + fileName).getImage());
            return new JLabel(imageIcon);
            
        }
        
    }
    
    private static void deleteCachedFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteCachedFiles(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    // función para cargar archivos de un directorio a la tabla
    public void loadDirectory() throws IOException {
        // variables para las propiedades de los archivos
        generalFilesSent.clear(); //limpiar el cache de los archivos cargados
        ArrayList<String> names = new ArrayList<>();
        long fileSize;
        double sizeKB;
        String sizeColumn = ""; //string para concatenar el size del archivo con bytes
        String imageSelect = "";
        DefaultTableModel firstModel = (DefaultTableModel) jt_files.getModel();
        while (firstModel.getRowCount() > 0) {
            firstModel.removeRow(0);
        }
        // Initialize socket
        Socket sc = new Socket("192.168.1.11", 5000);
        InputStream in = sc.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);
        DataOutputStream out = new DataOutputStream(sc.getOutputStream());
        // Send request to CoordinationServer and the user of who did it
        out.writeUTF("loadFiles");
        out.writeUTF(activeUser);

        //Count the amount of dfiles the user has on its account
        int totalFiles = dataIn.readInt();
        System.out.println("Reading " + totalFiles + " files from user");
        for (int i = 0; i < totalFiles; i++) {
            String name = dataIn.readUTF();
            names.add(name);
            fileSize = dataIn.readLong();
            sizeKB = (double) fileSize / 1024;
            
            sizeColumn += (sizeKB + " KB");
            
            imageSelect = getFileImage(name); // variable para determinar que icono se usará para representar el archivo

            Object[] newRow = {imageSelect, name, sizeColumn};
            sizeColumn = "";
            
            firstModel.addRow(newRow); // añadir la nueva row en el modelo de la tabla
        }
        
        for (File dirFile : generalFilesSent) {
            if (dirFile.isFile()) {
                fileSize = dirFile.length();
                sizeKB = (double) fileSize / 1024;
                
                sizeColumn += (sizeKB + " KB");
                
                imageSelect = getFileImage(dirFile.getName()); // variable para determinar que icono se usará para representar el archivo

                Object[] newRow = {imageSelect, dirFile.getName(), sizeColumn};
                sizeColumn = "";
                
                firstModel.addRow(newRow); // añadir la nueva row en el modelo de la tabla
            }
        }
        jt_files.setModel(firstModel);
        jt_files.getColumnModel().getColumn(0).setCellRenderer(new ImageRender());
        
    }

    //función para abrir el file chooser
    public void openFileChooser() {
        DefaultTableModel model = (DefaultTableModel) jt_files.getModel();
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        File currentDirectory = new File("../");
        fc.setCurrentDirectory(currentDirectory);
        int selected = fc.showOpenDialog(jhome_screen);
        
        if (selected == JFileChooser.APPROVE_OPTION) {
            File[] filesSelected = fc.getSelectedFiles();
            List<File> listFiles = Arrays.asList(filesSelected);
            long fileSize;
            double sizeKB;
            String sizeColumn = ""; //string para concatenar el size del archivo con bytes
            String imageSelect = "";
            for (File file : listFiles) {
                generalFilesSent.add(file);
                //añadir row a la tabla
                fileSize = file.length();
                sizeKB = (double) fileSize / 1024;
                
                sizeColumn += (sizeKB + " KB");
                imageSelect = getFileImage(file.getName()); // variable para determinar que icono se usará para representar el archivo

                Object[] newRow = {imageSelect, file.getName(), sizeColumn};
                sizeColumn = "";
                
                model.addRow(newRow); // añadir la nueva row en el modelo de la tabla
            }
            jt_files.setModel(model);
            jt_files.getColumnModel().getColumn(0).setCellRenderer(new ImageRender());
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jhome_screen = new javax.swing.JDialog();
        jp_filesContainer = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jlarr = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jt_files = new javax.swing.JTable();
        jb_subir = new javax.swing.JButton();
        jb_descargar = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jd_registro = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jb_register = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tf_usernameRegistro = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        pf_passwordRegistro1 = new javax.swing.JPasswordField();
        jLabel10 = new javax.swing.JLabel();
        pf_passwordRegistroConfirmacion = new javax.swing.JPasswordField();
        jb_registrarseRegistro = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        cb_region = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tf_username = new javax.swing.JTextField();
        pf_password = new javax.swing.JPasswordField();
        jb_registrarse = new javax.swing.JButton();

        jhome_screen.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jhome_screen.setTitle("MIS ARCHIVOS");
        jhome_screen.setMinimumSize(new java.awt.Dimension(600, 300));

        jp_filesContainer.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus90.png"))); // NOI18N

        jlarr.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jlarr.setText("Arrastre y suelte archivos");

        javax.swing.GroupLayout jp_filesContainerLayout = new javax.swing.GroupLayout(jp_filesContainer);
        jp_filesContainer.setLayout(jp_filesContainerLayout);
        jp_filesContainerLayout.setHorizontalGroup(
            jp_filesContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_filesContainerLayout.createSequentialGroup()
                .addGap(313, 313, 313)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_filesContainerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlarr)
                .addGap(269, 269, 269))
        );
        jp_filesContainerLayout.setVerticalGroup(
            jp_filesContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_filesContainerLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlarr)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jt_files.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Nombre", "Tamaño"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jt_files);

        jb_subir.setText("Subir");
        jb_subir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jb_subirMouseClicked(evt);
            }
        });

        jb_descargar.setText("Descargar");
        jb_descargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_descargarActionPerformed(evt);
            }
        });

        jButton3.setText("Ver mis Archivo");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jMenu1.setText("Archivo");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add-file.png"))); // NOI18N
        jMenuItem1.setText("Añadir Archivo");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jhome_screen.setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout jhome_screenLayout = new javax.swing.GroupLayout(jhome_screen.getContentPane());
        jhome_screen.getContentPane().setLayout(jhome_screenLayout);
        jhome_screenLayout.setHorizontalGroup(
            jhome_screenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jp_filesContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jhome_screenLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 693, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jhome_screenLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jb_descargar)
                .addGap(18, 18, 18)
                .addComponent(jb_subir)
                .addGap(95, 95, 95))
        );
        jhome_screenLayout.setVerticalGroup(
            jhome_screenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jhome_screenLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jp_filesContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jhome_screenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jb_subir)
                    .addComponent(jb_descargar)
                    .addComponent(jButton3))
                .addGap(18, 18, 18))
        );

        jd_registro.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jd_registro.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(13, 59, 102));

        jb_register.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jb_register.setForeground(new java.awt.Color(255, 255, 255));
        jb_register.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jb_register.setText("Registrarse");

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Usuario");

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Contraseña");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Confirmar contraseña");

        jb_registrarseRegistro.setText("Registrarse");
        jb_registrarseRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jb_registrarseRegistroMouseClicked(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Ubicación");

        cb_region.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "América", "Asia", "Europa" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jb_register, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(209, 209, 209)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cb_region, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jb_registrarseRegistro)
                        .addGap(43, 43, 43))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pf_passwordRegistro1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel10)
                                .addComponent(pf_passwordRegistroConfirmacion, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tf_usernameRegistro)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(227, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jb_register, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tf_usernameRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pf_passwordRegistro1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pf_passwordRegistroConfirmacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jb_registrarseRegistro)
                    .addComponent(cb_region, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jd_registro.getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 670, 370));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LOGIN");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cloud.jpg"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(13, 59, 102));
        jLabel2.setText("MAJ DRIVE");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(105, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(104, 104, 104))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel2.setBackground(new java.awt.Color(13, 59, 102));

        jButton1.setText("Iniciar Sesión");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Serif", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Login");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Usuario");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Contraseña");

        jb_registrarse.setText("Registrarse");
        jb_registrarse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jb_registrarseMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6)
                            .addComponent(tf_username)
                            .addComponent(pf_password, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jButton1)
                        .addGap(46, 46, 46)
                        .addComponent(jb_registrarse)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pf_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jb_registrarse))
                .addGap(45, 45, 45))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(354, 0, -1, 373));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        try {
            String user, pass = "";
            user = tf_username.getText();
            pass = new String(pf_password.getPassword());
            if (ValidationUser(user, pass)) {
                activeUser = user;
                System.out.println(user + "-" + pass);
                this.dispose();
                jhome_screen.setLocationRelativeTo(this);
                jhome_screen.pack();
                jhome_screen.setModal(true);
                jhome_screen.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Usuario/Contrasena Incorrectos");
            }
        } catch (IOException ex) {
            Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            // TODO add your handling code here:
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        openFileChooser();

        // abrir el filechooser

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jb_subirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jb_subirMouseClicked
        for (File f : generalFilesSent) {
            
            try {
                sendFile(f, activeUser);
                System.out.println(f.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jb_subirMouseClicked

    private void jb_descargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_descargarActionPerformed
        try {
            DefaultTableModel modelo = (DefaultTableModel) jt_files.getModel();
            int seleccion = jt_files.getSelectedRow();
            String fileName = (String) modelo.getValueAt(seleccion, 1);
            receiveFile(fileName, activeUser);
        } catch (IOException ex) {
            Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jb_descargarActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            File cache = new File("C:/temp/");
            deleteCachedFiles(cache);
            loadDirectory();
        } catch (IOException ex) {
            Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jb_registrarseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jb_registrarseMouseClicked
        // TODO add your handling code here:
        // botón registrarse en login

        this.dispose();
        jd_registro.pack();
        jd_registro.setLocationRelativeTo(this);
        
        jd_registro.setModal(true);
        jd_registro.setVisible(true);

    }//GEN-LAST:event_jb_registrarseMouseClicked

    private void jb_registrarseRegistroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jb_registrarseRegistroMouseClicked

        // botón registrarse en la ventana de registro
        String user, pass, passConfirm, region = "";
        
        user = tf_usernameRegistro.getText();
        pass = new String(pf_passwordRegistro1.getPassword());
        passConfirm = new String(pf_passwordRegistroConfirmacion.getPassword());
        
        region = cb_region.getSelectedItem().toString();

        //VALIDAR QUE EL USUARIO NO EXISTA EN LA BASE DE DATOS
        if (!user.isBlank() && !pass.isBlank() && !passConfirm.isBlank()) {
            if (pass.equals(passConfirm)) {
                try {
                    String ubi = cb_region.getSelectedItem().toString();
                    int idServer = 0;
                    if (ubi.equals("America")) {
                        idServer = 0;
                    } else if (ubi.equals("Europa")) {
                        idServer = 1;
                    }
                    System.out.println("su informacion fue almacenada en el servidor: " + idServer);
                    boolean resp = sendUser(user, pass, ubi, idServer);
                    if (resp) {
                        activeUser = user;
                        JOptionPane.showMessageDialog(jd_registro, "¡Registrado exitosamente!");
                        jd_registro.dispose();
                        jhome_screen.pack();
                        jhome_screen.setLocationRelativeTo(this);
                        jhome_screen.setModal(true);
                        jhome_screen.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Ese usuario ya existe");
                        tf_usernameRegistro.setText("");
                        pf_passwordRegistro1.setText("");
                        pf_passwordRegistroConfirmacion.setText("");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Client_Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(jd_registro, "Las contraseñas no coinciden");
            }
            
        } else {
            JOptionPane.showMessageDialog(jd_registro, "Alguno de los campos está vacío");
        }
    }//GEN-LAST:event_jb_registrarseRegistroMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client_Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cb_region;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jb_descargar;
    private javax.swing.JLabel jb_register;
    private javax.swing.JButton jb_registrarse;
    private javax.swing.JButton jb_registrarseRegistro;
    private javax.swing.JButton jb_subir;
    private javax.swing.JDialog jd_registro;
    private javax.swing.JDialog jhome_screen;
    private javax.swing.JLabel jlarr;
    private javax.swing.JPanel jp_filesContainer;
    private javax.swing.JTable jt_files;
    private javax.swing.JPasswordField pf_password;
    private javax.swing.JPasswordField pf_passwordRegistro1;
    private javax.swing.JPasswordField pf_passwordRegistroConfirmacion;
    private javax.swing.JTextField tf_username;
    private javax.swing.JTextField tf_usernameRegistro;
    // End of variables declaration//GEN-END:variables
    private List<File> generalFilesSent = new ArrayList<>();
    private String activeUser = "";
    
    public void sendFile(File f, String user) throws IOException {
        String host = "";
        //validar idserver  con el user
        if (ubicacionuser(user) == 0) {
            host = "192.168.43.196";
        } else {
            host = "192.168.43.156";
        }
        Socket socket = new Socket("192.168.43.96", 5000); // Replace "localhost" with the server's IP address if necessary, and replace 12345 with the server's port number

        System.out.println("Connected to server");
        
        OutputStream out = socket.getOutputStream();
        // Send file to server
        byte[] buffer = new byte[1024];
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF("uploading");
        dataOut.writeUTF(host);
        String x = f.getAbsolutePath();
        File fileToSend = new File(x); // Replace with your desired file name
        dataOut.writeUTF(activeUser);
        dataOut.writeUTF(fileToSend.getName()); // Send file name to 
        dataOut.writeLong(fileToSend.length()); // Send file size to server
        dataOut.writeUTF(fileToSend.getAbsolutePath()); // Send file path 
        FileInputStream fileIn = new FileInputStream(fileToSend);
        int count;
        while ((count = fileIn.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        fileIn.close();
        System.out.println("File sent to server");
        
    }
    
    public void receiveFile(String file, String user) throws IOException {
        // Receive file from server

        Socket socket = new Socket("localhost", 5000); // Replace "localhost" with the server's IP address if necessary, and replace 12345 with the server's port number

        System.out.println("Connected to server");
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        int count = 0;
        byte[] buffer = new byte[1024];
        DataInputStream dataIn = new DataInputStream(in);
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF("downloading");
        dataOut.flush();
        dataOut.writeUTF(user);
        dataOut.flush();
        dataOut.writeUTF(file);
        dataOut.flush();
        String fileName = dataIn.readUTF(); // Read file name from server
        long fileSize = dataIn.readLong(); // Read file size from server
        FileOutputStream fileOut = new FileOutputStream("C:/DownloadedFiles/" + fileName);
        
        while (fileSize > 0 && (count = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
            fileOut.write(buffer, 0, count);
            fileSize -= count;
        }
        fileOut.close();
        System.out.println("File received from server");
        
        socket.close();
    }
    
    public File pseudoReceiveFile(String file, String user) throws IOException {
        // Receive file from server

        Socket socket = new Socket("localhost", 5000); // Replace "localhost" with the server's IP address if necessary, and replace 12345 with the server's port number

        System.out.println("Connected to server");
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        int count = 0;
        byte[] buffer = new byte[1024];
        DataInputStream dataIn = new DataInputStream(in);
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF("downloading");
        dataOut.flush();
        dataOut.writeUTF(user);
        dataOut.flush();
        dataOut.writeUTF(file);
        dataOut.flush();
        String fileName = dataIn.readUTF(); // Read file name from server
        long fileSize = dataIn.readLong(); // Read file size from server
        File f = new File("C:/temp/" + fileName);
        FileOutputStream fileOut = new FileOutputStream(f);
        
        while (fileSize > 0 && (count = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
            fileOut.write(buffer, 0, count);
            fileSize -= count;
        }
        fileOut.close();
        System.out.println("File received from server");
        socket.close();
        return f;
    }
    
    public boolean sendUser(String name, String pass, String ubi, int idServer) throws IOException {
        
        Socket socket = new Socket("localhost", 5000); // Replace "localhost" with the server's IP address if necessary, and replace 12345 with the server's port number
        System.out.println("Connected to server");
        OutputStream out = socket.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF("usercreation");
        dataOut.writeUTF(name);
        dataOut.writeUTF(pass);
        dataOut.writeUTF(ubi);
        dataOut.writeInt(idServer);
        boolean resp = false;
        System.out.println("Datos sent to server");

        // Recibir respuesta del servidor
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);
        String resp3 = dataIn.readUTF();
        
        resp = Boolean.parseBoolean(resp3);
        dataOut.close();
        out.close();
        dataIn.close();
        in.close();
        socket.close();
        
        return resp;
    }
    
    public boolean ValidationUser(String name, String pass) throws IOException {
        Socket socket = new Socket("localhost", 5000); // Replace "localhost" with the server's IP address if necessary, and replace 12345 with the server's port number
        System.out.println("Connected to server");
        OutputStream out = socket.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF("uservalidation");
        dataOut.writeUTF(name);
        dataOut.writeUTF(pass);
        
        boolean resp = false;
        System.out.println("Datos sent to server");

        // Recibir respuesta del servidor
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);
        String resp3 = dataIn.readUTF();
        
        resp = Boolean.parseBoolean(resp3);
        dataOut.close();
        out.close();
        dataIn.close();
        in.close();
        socket.close();
        
        return resp;
    }
    
    public int ubicacionuser(String user) throws IOException {
        Socket socket = new Socket("localhost", 5000); // Replace "localhost" with the server's IP address if necessary, and replace 12345 with the server's port number
        System.out.println("Connected to server");
        OutputStream out = socket.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF("ubicacion");
        dataOut.writeUTF(user);
        // Recibir respuesta del servidor
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);
        String resp3 = dataIn.readUTF();
        dataOut.close();
        out.close();
        dataIn.close();
        in.close();
        socket.close();
        
        return Integer.parseInt(resp3);
    }
}

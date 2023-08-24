package com.window.panels;

import Report.cetak;
import com.data.app.Log;
import com.data.db.Database;
import static com.data.db.Database.DB_NAME;
import com.window.dialogs.*;
import com.manage.Message;
import com.media.Gambar;
import com.manage.Barang;
import com.manage.ManageTransaksiJual;
import com.manage.Text;
import com.media.Audio;
//import com.users.Karyawan;
import com.users.Supplier;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Amirzan
 */
public class DetailLaporanJualOld extends javax.swing.JDialog {

    private final Database db = new Database();
    private final Barang barang = new Barang();
    private final String namadb = Database.DB_NAME;
//    private final Karyawan karyawan = new Karyawan();
    private final ManageTransaksiJual trj = new ManageTransaksiJual();
    public int option;

    public static final int ADD_OPTION = 1, EDIT_OPTION = 2;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet res;
    private final Text text = new Text();

    DateFormat tanggalMilis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateFormat tanggalFull = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat time = new SimpleDateFormat("hh:mm:ss");
    private final DateFormat timeMillis = new SimpleDateFormat("ss.SSS:mm:hh");
    private int jumlahKoneksi = 0;
    private String idTrSelected = "", idSelected = "", keyword = "", idTr, idPd, IDBarang, namaBarang, jenisBarang, jumlahBarang;
    private int selectedIndex, totalHrg, harga;
    private boolean isUpdated = false;

    public DetailLaporanJualOld(Frame parent, boolean modal, String idtr) throws ParseException {
        super(parent, modal);
        initComponents();
        this.idTrSelected = idtr;
        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "'";
        this.updateTabel();
        valTotal.setText(text.toMoneyCase(Integer.toString(getTotal("detail_transaksi_jual", "total_harga", "WHERE id_tr_jual = '" + this.idTrSelected + "'"))));
        this.btnKembali.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }

//    detailLaporanBeli(Object object, boolean b, Object object0) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    private void koneksi() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + this.namadb, "root", "");
            this.stmt = con.createStatement();
            this.jumlahKoneksi++;
//            System.out.println("jumlah koneksi : "+db.jumlahKoneksi);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void closeKoneksi() {
        try {
            for (int i = 0; i < this.jumlahKoneksi; i++) {

                // Mengecek apakah conn kosong atau tidak, jika tidak maka akan diclose
                if (this.con != null) {
                    this.con.close();
                }
                // Mengecek apakah stat kosong atau tidak, jika tidak maka akan diclose
                if (this.stmt != null) {
                    this.stmt.close();
                }
                // Mengecek apakah res koson atau tidak, jika tidak maka akan diclose
                if (this.res != null) {
                    this.res.close();
                }
                Log.addLog(String.format("Berhasil memutus koneksi dari Database '%s'.", DB_NAME));
            }
        } catch (SQLException ex) {
            Audio.play(Audio.SOUND_ERROR);
            JOptionPane.showMessageDialog(null, "Terjadi Kesalahan!\n\nError message : " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int getTotal(String table, String kolom, String kondisi) {
        try {
            koneksi();
            int data = 0;
            String sql = "SELECT SUM(" + kolom + ") AS total FROM " + table + " " + kondisi;
//            System.out.println(sql);
            this.res = this.stmt.executeQuery(sql);
            while (this.res.next()) {
                data = this.res.getInt("total");
            }
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException n) {
//            n.printStackTrace();
            System.out.println("errorr ");
            return 0;
        }
        return -1;
    }

    private Object[][] getData() throws ParseException {
        try {
            Object[][] obj;
            int rows = 0;
            String sql = "SELECT id_tr_jual, id_barang, nama_barang, jenis_barang, harga, jumlah, total_harga FROM detail_transaksi_jual " + keyword + " ORDER BY jenis_barang DESC";
//            System.out.println(sql);
            obj = new Object[trj.getJumlahData("detail_transaksi_jual", keyword)][8];
            // mengeksekusi query
            trj.res = trj.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam this.tabelData
            while (trj.res.next()) {
                // menyimpan data dari this.tabelData ke object
                obj[rows][0] = trj.res.getString("id_tr_jual").replace("TRJ", "LPD");
                obj[rows][1] = trj.res.getString("id_tr_jual");
                obj[rows][2] = trj.res.getString("id_barang");
                obj[rows][3] = trj.res.getString("nama_barang");
                obj[rows][4] = trj.res.getString("jenis_barang");
                obj[rows][5] = text.toMoneyCase(Integer.toString(trj.res.getInt("harga")));
                obj[rows][6] = Integer.toString(trj.res.getInt("jumlah"));
                obj[rows][7] = text.toMoneyCase(Integer.toString(trj.res.getInt("total_harga")));
                rows++;
            }
            return obj;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private void updateTabel() throws ParseException {
        this.tabelData.setModel(new javax.swing.table.DefaultTableModel(
                getData(),
                new String[]{
                    "ID Pemasukan", "ID Transaksi Jual", "ID Barang", "Nama Barang", "Jenis Brang", "Harga Jual", "Jumlah", "Total Harga"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    private void showData(int index) throws ParseException {
        // mendapatkan data-data
        this.idPd = this.tabelData.getValueAt(index, 0).toString();
        this.idTr = this.tabelData.getValueAt(index, 1).toString();
        this.IDBarang = this.tabelData.getValueAt(index, 2).toString();
        this.namaBarang = this.tabelData.getValueAt(index, 3).toString();
        this.jenisBarang = this.tabelData.getValueAt(index, 4).toString();
        this.harga = text.toIntCase(this.tabelData.getValueAt(index, 5).toString());
        this.jumlahBarang = this.tabelData.getValueAt(index, 6).toString();
        this.totalHrg = text.toIntCase(this.tabelData.getValueAt(index, 7).toString());

        // menampilkan data-data
        this.valIDPemasukan.setText("<html><p>:&nbsp;" + this.idPd + "</p></html>");
        this.valIDTransaksi.setText("<html><p>:&nbsp;" + this.idTr + "</p></html>");
        this.valIDBarang.setText("<html><p>:&nbsp;" + this.IDBarang + "</p></html>");
        this.valNamaBarang.setText("<html><p>:&nbsp;" + this.namaBarang + "</p></html>");
        this.valHarga.setText("<html><p>:&nbsp;" + this.harga + "</p></html>");
        this.valJenis.setText("<html><p>:&nbsp;" + this.jenisBarang + "</p></html>");
        this.valTotalHarga.setText("<html><p>:&nbsp;" + this.totalHrg + "</p></html>");
        this.valJumlah.setText("<html><p>:&nbsp;" + this.jumlahBarang + "</p></html>");
    }

    private void cetakNota(Map parameter) {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load("src\\Report\\notaPenjualan.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
//            Map parameters = new HashMap();
            parameter.put("id_tr_jual", this.idTr);
            parameter.put("id_tr_jual", this.idTr);
            parameter.put("id_tr_jual", this.idTr);
            JasperPrint jPrint = JasperFillManager.fillReport(jasperReport, parameter, db.conn);
            JasperViewer.viewReport(jPrint);
//            JasperExportManager.exportReportToPdfFile(jPrint, "reports/simple_report.pdf");
        } catch (JRException ex) {
            Logger.getLogger(cetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Mengecek apakah user menekan tombol simpan / tambah atau tidak
     *
     * @return <strong>True</strong> jika user menekan tombol simpan / tambah.
     * <br>
     * <strong>False</strong> jika user menekan tombol kembali / close.
     */
    public boolean isUpdated() {
        trj.closeConnection();
        closeKoneksi();
        return this.isUpdated;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlMain = new javax.swing.JPanel();
        btnKembali = new javax.swing.JButton();
        btnCetak = new javax.swing.JLabel();
        valIDPemasukan = new javax.swing.JLabel();
        valIDTransaksi = new javax.swing.JLabel();
        valNamaBarang = new javax.swing.JLabel();
        valIDBarang = new javax.swing.JLabel();
        valHarga = new javax.swing.JLabel();
        valJenis = new javax.swing.JLabel();
        valTotalHarga = new javax.swing.JLabel();
        valJumlah = new javax.swing.JLabel();
        valTotal = new javax.swing.JLabel();
        inpCari = new javax.swing.JTextField();
        lpSemua = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnKembali.setBackground(new java.awt.Color(220, 41, 41));
        btnKembali.setForeground(new java.awt.Color(255, 255, 255));
        btnKembali.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-kembali-075.png"))); // NOI18N
        btnKembali.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnKembali.setMaximumSize(new java.awt.Dimension(130, 28));
        btnKembali.setMinimumSize(new java.awt.Dimension(130, 28));
        btnKembali.setOpaque(false);
        btnKembali.setPreferredSize(new java.awt.Dimension(130, 28));
        btnKembali.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnKembaliMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnKembaliMouseExited(evt);
            }
        });
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });
        pnlMain.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 710, 160, 40));

        btnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-print-075.png"))); // NOI18N
        btnCetak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCetakMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCetakMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCetakMouseExited(evt);
            }
        });
        pnlMain.add(btnCetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 710, -1, -1));

        valIDPemasukan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valIDPemasukan.setForeground(new java.awt.Color(0, 0, 0));
        valIDPemasukan.setText(":");
        pnlMain.add(valIDPemasukan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 69, 315, 35));

        valIDTransaksi.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valIDTransaksi.setForeground(new java.awt.Color(0, 0, 0));
        valIDTransaksi.setText(": ");
        pnlMain.add(valIDTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 68, 230, 36));

        valNamaBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valNamaBarang.setForeground(new java.awt.Color(0, 0, 0));
        valNamaBarang.setText(":");
        pnlMain.add(valNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 121, 315, 36));

        valIDBarang.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valIDBarang.setForeground(new java.awt.Color(0, 0, 0));
        valIDBarang.setText(": ");
        pnlMain.add(valIDBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 120, 230, 36));

        valHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valHarga.setForeground(new java.awt.Color(0, 0, 0));
        valHarga.setText(":");
        pnlMain.add(valHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 173, 315, 36));

        valJenis.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valJenis.setForeground(new java.awt.Color(0, 0, 0));
        valJenis.setText(": ");
        pnlMain.add(valJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 173, 230, 36));

        valTotalHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valTotalHarga.setForeground(new java.awt.Color(0, 0, 0));
        valTotalHarga.setText(":");
        pnlMain.add(valTotalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 225, 315, 36));

        valJumlah.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valJumlah.setForeground(new java.awt.Color(0, 0, 0));
        valJumlah.setText(": ");
        valJumlah.setName(""); // NOI18N
        pnlMain.add(valJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 226, 230, 36));

        valTotal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valTotal.setForeground(new java.awt.Color(0, 0, 0));
        valTotal.setText(":");
        pnlMain.add(valTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 640, 330, 36));

        inpCari.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpCariActionPerformed(evt);
            }
        });
        inpCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariKeyTyped(evt);
            }
        });
        pnlMain.add(inpCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 300, 220, 32));

        tabelData.setBackground(new java.awt.Color(255, 255, 255));
        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pemasukan", "ID Transaksi Jual", "ID Barang", "Nama Barang", "Jenis Barang", "Harga Beli", "Jumlah", "Total Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelData.setGridColor(new java.awt.Color(0, 0, 0));
        tabelData.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelData.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelData.getTableHeader().setReorderingAllowed(false);
        tabelData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataMouseClicked(evt);
            }
        });
        tabelData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataKeyPressed(evt);
            }
        });
        lpSemua.setViewportView(tabelData);

        pnlMain.add(lpSemua, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 332, 1040, 300));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-window-detail-laporan-pemasukan-075.png"))); // NOI18N
        pnlMain.add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jScrollPane1.setViewportView(pnlMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
                .addGap(50, 50, 50))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseEntered
        this.btnKembali.setIcon(Gambar.getAktiveIcon(this.btnKembali.getIcon().toString()));
    }//GEN-LAST:event_btnKembaliMouseEntered

    private void btnKembaliMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseExited
        this.btnKembali.setIcon(Gambar.getBiasaIcon(this.btnKembali.getIcon().toString()));
    }//GEN-LAST:event_btnKembaliMouseExited

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        barang.closeConnection();
        this.dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void inpCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariActionPerformed
        try {
            String key = this.inpCari.getText();
            this.keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "' AND (id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%')";
            this.updateTabel();
        } catch (ParseException ex) {
            Logger.getLogger(DetailLaporanJualOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inpCariActionPerformed

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        try {
            String key = this.inpCari.getText();
            this.keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "' AND (id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%')";

            this.updateTabel();
        } catch (ParseException ex) {
            Logger.getLogger(DetailLaporanJualOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inpCariKeyReleased

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        try {
            String key = this.inpCari.getText();
            this.keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "' AND (id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%')";
            this.updateTabel();
        } catch (ParseException ex) {
            Logger.getLogger(DetailLaporanJualOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inpCariKeyTyped

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.idSelected = this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 0).toString();
            this.showData(this.tabelData.getSelectedRow());
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanBeli.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataMouseClicked

    private void tabelDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyPressed
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                if (this.tabelData.getSelectedRow() >= 1) {
                    this.idSelected = this.tabelData.getValueAt(this.tabelData.getSelectedRow() - 1, 0).toString();
                    this.showData(this.tabelData.getSelectedRow() - 1);
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                if (this.tabelData.getSelectedRow() < (this.tabelData.getRowCount() - 1)) {
                    this.idSelected = this.tabelData.getValueAt(this.tabelData.getSelectedRow() + 1, 0).toString();
                    this.showData(this.tabelData.getSelectedRow() + 1);
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanBeli.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataKeyPressed

    private void btnCetakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakMouseClicked
//        try {
//            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//            if (tabelData.getRowCount() > 0) {
//                //print struk penjualan 
//                Map parameters = new HashMap();
//                parameters.put("tanggal", waktu.getTanggalNow());
//                parameters.put("id_tr_jual", this.idTr);
//                parameters.put("totalBarang", totalBarang);
//                parameters.put("totalHarga", txtTotal.getText());
//                parameters.put("bayar", text.toMoneyCase(inpBayar.getText()));
//                parameters.put("diskon", txtDiskon.getText());
//                parameters.put("kembalian", txtKembalian.getText());
//                this.cetakNota(parameters);
//            } else {
//                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//                Message.showWarning(this, "Tabel kosong !");
//            }
//        } catch (PrinterException ex) {
//            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_btnCetakMouseClicked

    private void btnCetakMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakMouseEntered

    private void btnCetakMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakMouseExited

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(InputPembeli.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
////                InputPembeli dialog = new InputPembeli(new javax.swing.JFrame(), true, null);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel btnCetak;
    private javax.swing.JButton btnKembali;
    private javax.swing.JTextField inpCari;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane lpSemua;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JTable tabelData;
    private javax.swing.JLabel valHarga;
    private javax.swing.JLabel valIDBarang;
    private javax.swing.JLabel valIDPemasukan;
    private javax.swing.JLabel valIDTransaksi;
    private javax.swing.JLabel valJenis;
    private javax.swing.JLabel valJumlah;
    private javax.swing.JLabel valNamaBarang;
    private javax.swing.JLabel valTotal;
    private javax.swing.JLabel valTotalHarga;
    // End of variables declaration//GEN-END:variables
}

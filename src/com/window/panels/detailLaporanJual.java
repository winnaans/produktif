package com.window.panels;

import com.window.MainWindow;
import com.manage.ManageTransaksiJual;
import com.manage.Message;
import com.manage.Text;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import java.awt.Cursor;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author Amirzan Fikri P
 */
public class DetailLaporanJual extends javax.swing.JPanel {
    private final ManageTransaksiJual trj = new ManageTransaksiJual();
    private final Text text = new Text();

    DateFormat tanggalMilis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateFormat tanggalFull = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat time = new SimpleDateFormat("hh:mm:ss");
    private final DateFormat timeMillis = new SimpleDateFormat("ss.SSS:mm:hh");
    private String idTrSelected = "", idSelected = "", keyword = "", idTr, idPd, IDSupplier, namaSupplier, IDBarang, namaBarang, jenisBarang, jumlahBarang;
    private int selectedIndex, totalHrg, harga;
    public DetailLaporanJual(String idtr) throws ParseException {
        initComponents();
        this.btnKembali.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnPrint.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
        this.idTrSelected = idtr;
        keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "'";
        this.updateTabel();
        valTotal.setText(text.toMoneyCase(Integer.toString(getTotal("detail_transaksi_jual", "total_harga", "WHERE id_tr_jual = '" + this.idTrSelected + "'"))));
    }
    private void closeKoneksi(){
        trj.closeConnection();
    }
    private int getTotal(String table, String kolom, String kondisi) {
        try {
            int data = 0;
            String sql = "SELECT SUM(" + kolom + ") AS total FROM " + table + " " + kondisi;
//            System.out.println(sql);
            trj.res = trj.stat.executeQuery(sql);
            while (trj.res.next()) {
                data = trj.res.getInt("total");
            }
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(DetailLaporanJual.class.getName()).log(Level.SEVERE, null, ex);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnKembali = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        inpCari = new javax.swing.JTextField();
        lpSemua = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        valIDPemasukan = new javax.swing.JLabel();
        valIDTransaksi = new javax.swing.JLabel();
        valNamaBarang = new javax.swing.JLabel();
        valIDBarang = new javax.swing.JLabel();
        valHarga = new javax.swing.JLabel();
        valJenis = new javax.swing.JLabel();
        valTotalHarga = new javax.swing.JLabel();
        valJumlah = new javax.swing.JLabel();
        valTotal = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 640, 154, 50));

        btnPrint.setBackground(new java.awt.Color(220, 41, 41));
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-print-075.png"))); // NOI18N
        btnPrint.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnPrint.setMaximumSize(new java.awt.Dimension(130, 28));
        btnPrint.setMinimumSize(new java.awt.Dimension(130, 28));
        btnPrint.setOpaque(false);
        btnPrint.setPreferredSize(new java.awt.Dimension(130, 28));
        btnPrint.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPrintMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPrintMouseExited(evt);
            }
        });
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 640, 154, 50));

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
        add(inpCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 275, 315, 32));

        tabelData.setBackground(new java.awt.Color(255, 255, 255));
        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pengeluaran", "ID Transaksi Beli", "ID Barang", "Nama Barang", "Jenis Barang", "Harga Beli", "Jumlah", "Total Harga"
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

        add(lpSemua, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 305, 1130, 275));

        valIDPemasukan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valIDPemasukan.setForeground(new java.awt.Color(0, 0, 0));
        valIDPemasukan.setText(":");
        add(valIDPemasukan, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 410, 35));

        valIDTransaksi.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valIDTransaksi.setForeground(new java.awt.Color(0, 0, 0));
        valIDTransaksi.setText(": ");
        add(valIDTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(825, 70, 300, 35));

        valNamaBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valNamaBarang.setForeground(new java.awt.Color(0, 0, 0));
        valNamaBarang.setText(":");
        add(valNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 115, 410, 35));

        valIDBarang.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valIDBarang.setForeground(new java.awt.Color(0, 0, 0));
        valIDBarang.setText(": ");
        add(valIDBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(825, 115, 300, 35));

        valHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valHarga.setForeground(new java.awt.Color(0, 0, 0));
        valHarga.setText(":");
        add(valHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 410, 35));

        valJenis.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valJenis.setForeground(new java.awt.Color(0, 0, 0));
        valJenis.setText(": ");
        add(valJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(825, 160, 300, 35));

        valTotalHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valTotalHarga.setForeground(new java.awt.Color(0, 0, 0));
        valTotalHarga.setText(":");
        add(valTotalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 205, 410, 35));

        valJumlah.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valJumlah.setForeground(new java.awt.Color(0, 0, 0));
        valJumlah.setText(": ");
        valJumlah.setName(""); // NOI18N
        add(valJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(825, 205, 300, 35));

        valTotal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valTotal.setForeground(new java.awt.Color(0, 0, 0));
        valTotal.setText(":");
        add(valTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 585, 280, 25));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-detail-laporan-pemasukan.png"))); // NOI18N
        background.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseEntered
        this.btnKembali.setIcon(Gambar.getAktiveIcon(this.btnKembali.getIcon().toString()));
    }//GEN-LAST:event_btnKembaliMouseEntered

    private void btnKembaliMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseExited
        this.btnKembali.setIcon(Gambar.getBiasaIcon(this.btnKembali.getIcon().toString()));
    }//GEN-LAST:event_btnKembaliMouseExited
    private void dataLaporan(JPanel pnl) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//        MainWindow.setTitle("Data");
        // menghapus panel lama
        MainWindow.pnlMenu.removeAll();
        MainWindow.pnlMenu.repaint();
        MainWindow.pnlMenu.revalidate();
//        pnlMenu.revalidate();
        this.closeKoneksi();
        // menambahkan panel baru
        MainWindow.pnlMenu.add(pnl);
        MainWindow.pnlMenu.validate();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        try {
//            barang.closeConnection();
            LaporanJual pnl = new LaporanJual();
            this.dataLaporan(pnl);
        } catch (ParseException ex) {
            Logger.getLogger(DetailLaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void inpCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariActionPerformed
        try {
            String key = this.inpCari.getText();
            this.keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "' AND (id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%')";
            this.updateTabel();
        } catch (ParseException ex) {
            Logger.getLogger(DetailLaporanBeliOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inpCariActionPerformed

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        try {
            String key = this.inpCari.getText();
            this.keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "' AND (id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%')";

            this.updateTabel();
        } catch (ParseException ex) {
            Logger.getLogger(DetailLaporanBeliOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inpCariKeyReleased

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        try {
            String key = this.inpCari.getText();
            this.keyword = "WHERE id_tr_jual = '" + this.idTrSelected + "' AND (id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%')";
            this.updateTabel();
        } catch (ParseException ex) {
            Logger.getLogger(DetailLaporanBeliOld.class.getName()).log(Level.SEVERE, null, ex);
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

    private void btnPrintMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPrintMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrintMouseEntered

    private void btnPrintMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPrintMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrintMouseExited

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnPrint;
    private javax.swing.JTextField inpCari;
    private javax.swing.JScrollPane lpSemua;
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

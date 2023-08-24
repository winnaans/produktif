package com.window.panels;

import java.util.Date;
import com.manage.Chart;
import com.manage.Message;
import com.manage.Waktu;
import com.data.db.Database;
import com.manage.Text;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Amirzan Fikri P
 */
public class Dashboard extends javax.swing.JPanel {

    private final Database db = new Database();
    private final Chart chart = new Chart();

    private final Waktu waktu = new Waktu();

    private final Text text = new Text();

    private int hari, bulan, tahun, pMakanan, pMinuman, pSnack, pAtk, Saldo = 0;
    private String keywordSaldo = "";
    DateFormat tanggalMilis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateFormat tanggalFull = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat time12 = new SimpleDateFormat("hh:mm:ss");
    private final DateFormat time = new SimpleDateFormat("hh:mm:ss");
    private final DateFormat timeMillis = new SimpleDateFormat("ss.SSS:mm:hh");
    private Object[] obj;
    public Dashboard() throws ParseException {
        initComponents();
        db.startConnection();
//        System.out.println("dahsboard");
        updateTabel();
        this.updateSaldo();
        this.hari = waktu.getTanggal();
        this.bulan = waktu.getBulan() + 1;
        this.tahun = waktu.getTahun();
        this.tabelData.setRowHeight(30);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
        
        showMain();
        pMakanan = getJenis("MAKANAN");
        pMinuman = getJenis("MINUMAN");
        pSnack = getJenis("SNACK");
        pAtk = getJenis("ATK");
        this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
        obj = waktu.getMinggu(bulan, tahun);
        this.chart.showLineChart(pnlLineChart,obj);

        // mengupdate waktu
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (isVisible()) {
//                        System.out.println("update");
                        lblDate.setText(waktu.getUpdateWaktu() + "  ");
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ex) {
                    Message.showException(this, "Terjadi Kesalahan Saat Mengupdate Tanggal!\n" + ex.getMessage(), ex, true);
                }
            }
        }).start();
    }
    public void closeKoneksi(){
        this.chart.closeKoneksi();
        db.closeConnection();
    }
    private void updateSaldo() {
        try {
            String sql = "SELECT jumlah_saldo FROM saldo ORDER BY id_saldo DESC LIMIT 0,1" + keywordSaldo;
            db.res = db.stat.executeQuery(sql);
            while (db.res.next()) {
                this.Saldo = db.res.getInt("jumlah_saldo");
            }
            db.closeConnection();
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
    }
    private int getJenis(String field) {
        try {
            int data = 0;
            String sql = "SELECT SUM(jumlah) AS total FROM transaksi_jual INNER JOIN detail_transaksi_jual ON transaksi_jual.id_tr_jual = detail_transaksi_jual.id_tr_jual WHERE jenis_barang = '"+field+"' AND YEAR(tanggal) = '" + this.tahun + "' AND MONTH(tanggal) = '" + this.bulan + "'";
//            System.out.println(sql);
            db.res = db.stat.executeQuery(sql);
            while (db.res.next()) {
                data = db.res.getInt("total");
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

    private int getTotal(String table, String kolom, String kondisi) {
        try {
            db.startConnection();
            int data = 0;
            String sql = "SELECT SUM(" + kolom + ") AS total FROM " + table + " " + kondisi;
            db.res = db.stat.executeQuery(sql);
            while (db.res.next()) {
                data = db.res.getInt("total");
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

    private int getJumlahData(String tabel, String kondisi) {
        try {
            String query = "SELECT COUNT(*) AS total FROM " + tabel + " " + kondisi;
            db.res = db.stat.executeQuery(query);
            if (db.res.next()) {
                return db.res.getInt("total");
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi Kesalahan!\n\nError message : " + ex.getMessage(), ex, true);
        } catch (NullPointerException n) {
//            n.printStackTrace();
            System.out.println("errorr ");
            return 0;
        }
        return -1;
    }
    private void showMain() {
        String tanggal = waktu.getCurrentDate();
        String tSaldo = text.toMoneyCase(Integer.toString(this.Saldo));
        String tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "keuntungan", "WHERE YEAR(tanggal) = '" + tahun + "' AND MONTH(tanggal) = '" + bulan + "'")));
        String tPengeluaran = text.toMoneyCase(Integer.toString(getTotal("transaksi_beli", "total_hrg", "WHERE YEAR(tanggal) = '" + tahun + "' AND MONTH(tanggal) = '" + bulan + "'")));
        String tPembeli = Integer.toString(getJumlahData("transaksi_jual", "WHERE YEAR(tanggal) = '" + tahun + "' AND MONTH(tanggal) = '" + bulan + "'"));
        lblSaldo.setText(tSaldo);
        lblPemasukan.setText(tPemasukan);
        lblPengeluaran.setText(tPengeluaran);
        lblPembeli.setText(tPembeli);
    }

    private void updateTabel() throws ParseException {
        try {
            DefaultTableModel tabelModel = (DefaultTableModel) tabelData.getModel();
            Date tanggalData;
            int hari1 = 0, bulan1 = -1, tahun1 = 0;
            String kolom[] = {"No", "Id transaksi ", "Total Harga", "Jenis Transaksi", "Tanggal", "Waktu"}, waktu, tanggalPenuh,tanggalPenuh1, total, jenis, data[] = new String[6];
            TableModel model;
            String sql = "SELECT id_tr_beli AS id,total_hrg AS total, tanggal FROM transaksi_beli UNION SELECT id_tr_jual,total_hrg,tanggal FROM transaksi_jual ORDER BY tanggal DESC";
            db.res = db.stat.executeQuery(sql);
            int nomor = 1;
            while (db.res.next()) {
                data[0] = Integer.toString(nomor);
                nomor++;
                jenis = db.res.getString("id");
                data[1] = jenis;
                total = db.res.getString("total");
                if (jenis.substring(0, 3).equals("TRJ")) {
                    data[2] = text.toMoneyCase(total);
                    data[3] = "Penjualan";
                } else if (jenis.substring(0, 3).equals("TRB")) {
                    data[2] = text.toMoneyCase("-" + total);
                    data[3] = "Pembelian";
                }
                tanggalPenuh = db.res.getString("tanggal");
                tanggalData = tanggalMilis.parse(tanggalPenuh);
                tanggalPenuh1 = date.format(tanggalData);
                hari1 = Integer.parseInt(tanggalPenuh1.substring(0,2));
                bulan1 = Integer.parseInt(tanggalPenuh1.substring(3, 5));
                tahun1 = Integer.parseInt(tanggalPenuh1.substring(6));
                data[4] = hari1 +"-"+ this.waktu.getNamaBulan(bulan1-1)+"-"+tahun1;
                data[5] = tanggalPenuh.substring(11,19);
                tabelModel.addRow(data);
            }
            tabelData.setModel(tabelModel);
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblDate = new javax.swing.JLabel();
        lblSaldo = new javax.swing.JLabel();
        lblPemasukan = new javax.swing.JLabel();
        lblPengeluaran = new javax.swing.JLabel();
        lblPembeli = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        pnlLineChart = new javax.swing.JPanel();
        pnlPieChart = new javax.swing.JPanel();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(957, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDate.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblDate.setText("-");
        add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 125, 270, 30));

        lblSaldo.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblSaldo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(lblSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 70, 200, 25));

        lblPemasukan.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblPemasukan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(lblPemasukan, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 200, 25));

        lblPengeluaran.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblPengeluaran.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(lblPengeluaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(625, 70, 200, 25));

        lblPembeli.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblPembeli.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(lblPembeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(905, 70, 200, 25));

        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Transaksi", "Total Harga", "Jenis Transaksi", "Tanggal", "Waktu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
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
                tabelDatatablDataKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tabelData);
        if (tabelData.getColumnModel().getColumnCount() > 0) {
            tabelData.getColumnModel().getColumn(0).setMinWidth(40);
            tabelData.getColumnModel().getColumn(0).setMaxWidth(40);
            tabelData.getColumnModel().getColumn(2).setMinWidth(200);
            tabelData.getColumnModel().getColumn(2).setMaxWidth(200);
            tabelData.getColumnModel().getColumn(3).setMinWidth(200);
            tabelData.getColumnModel().getColumn(3).setMaxWidth(200);
            tabelData.getColumnModel().getColumn(4).setMinWidth(200);
            tabelData.getColumnModel().getColumn(4).setMaxWidth(200);
            tabelData.getColumnModel().getColumn(5).setMinWidth(200);
            tabelData.getColumnModel().getColumn(5).setMaxWidth(200);
        }

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 445, 1100, 235));

        pnlLineChart.setBackground(new java.awt.Color(255, 255, 255));
        pnlLineChart.setForeground(new java.awt.Color(226, 226, 0));
        pnlLineChart.setLayout(new java.awt.BorderLayout());
        add(pnlLineChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 195, 530, 185));

        pnlPieChart.setBackground(new java.awt.Color(255, 255, 255));
        pnlPieChart.setForeground(new java.awt.Color(255, 255, 0));
        pnlPieChart.setLayout(new java.awt.BorderLayout());
        add(pnlPieChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 195, 520, 185));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-dashboard.png"))); // NOI18N
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1158, 696));
    }// </editor-fold>//GEN-END:initComponents

    private void tabelDatatablDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDatatablDataKeyPressed
        //
    }//GEN-LAST:event_tabelDatatablDataKeyPressed

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        //
    }//GEN-LAST:event_tabelDataMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblPemasukan;
    private javax.swing.JLabel lblPembeli;
    private javax.swing.JLabel lblPengeluaran;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JPanel pnlLineChart;
    private javax.swing.JPanel pnlPieChart;
    private javax.swing.JTable tabelData;
    // End of variables declaration//GEN-END:variables
}

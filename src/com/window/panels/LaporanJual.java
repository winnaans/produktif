package com.window.panels;

import com.data.app.Log;
import com.data.db.Database;
import static com.data.db.Database.DB_NAME;
import com.manage.Chart;
import com.manage.ManageTransaksiJual;
import com.manage.Message;
import com.manage.Text;
import com.manage.Waktu;
import com.window.panels.DetailLaporanJualOld;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.window.MainWindow;
//import com.users.Karyawan;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Amirzan Fikri
 */
public class LaporanJual extends javax.swing.JPanel {

    private final Database db = new Database();
    private final String namadb = Database.DB_NAME;
    private final ManageTransaksiJual trj = new ManageTransaksiJual();

//    private final Karyawan karyawan = new Karyawan();

    private final Chart chart = new Chart();

    private final Text text = new Text();

    DateFormat tanggalMilis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateFormat tanggalFull = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat time = new SimpleDateFormat("hh:mm:ss");
    private final DateFormat timeMillis = new SimpleDateFormat("ss.SSS:mm:hh");

    private int hari, hari1, bulan, bulan1, tahun, tahun1, bulanan, tahunan;
    private Date tHarian1, tHarian2, tHarian3, tHarian2_old, tHarian3_old;
    private final Waktu waktu = new Waktu();
    private String tPemasukan;
    private int selectedIndex = 1, totalHrg, keuntungan, jumlahKoneksi = 0;
    private String idSelected = "", keyword = "", idTr, idPd, IDKaryawan, namaKaryawan, tanggal, tanggalDipilih1, tanggalDipilih2, tanggalDipilih3;

    private Connection con;
    private Statement stmt;
    private ResultSet res;

    public LaporanJual() throws ParseException {
        this.tanggalDipilih1 = waktu.getCurrentDate();
        tHarian1 = date1.parse(this.tanggalDipilih1);
        tHarian2 = date1.parse(this.tanggalDipilih1);
        tHarian3 = date1.parse(this.tanggalDipilih1);
        initComponents();
        //atur tanggal default
        this.tanggalDipilih2 = waktu.getCurrentDate();
        this.tanggalDipilih3 = waktu.getCurrentDate();
        this.hari = Integer.parseInt(tanggalDipilih1.substring(8));
        this.bulan = Integer.parseInt(tanggalDipilih1.substring(5, 7));
        this.tahun = Integer.parseInt(tanggalDipilih1.substring(0, 4));

        this.hari1 = Integer.parseInt(tanggalDipilih3.substring(8));
        this.bulan1 = Integer.parseInt(tanggalDipilih3.substring(5, 7));
        this.tahun1 = Integer.parseInt(tanggalDipilih3.substring(0, 4));

        this.bulanan = this.bulan;
        this.tahunan = this.tahun;
        tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "")));
        valTotalS.setText(tPemasukan);
        tbHarian.setDate(date1.parse(this.tanggalDipilih1));
        tbMinggu1.setDate(date1.parse(this.tanggalDipilih1));
        tbMinggu2.setDate(date1.parse(this.tanggalDipilih1));

        tabPengeluaran.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
                selectedIndex = tabbedPane.getSelectedIndex() + 1;
                switch (selectedIndex) {
                    case 1:
                        //sembunyikan
                        txtAwal.setVisible(false);
                        txtAkhir.setVisible(false);
                        tbHarian.setVisible(false);
                        tbHarian.setEnabled(false);
                        tbMinggu1.setVisible(false);
                        tbMinggu1.setEnabled(false);
                        tbMinggu2.setVisible(false);
                        tbMinggu2.setEnabled(false);
                        tbBulanan.setVisible(false);
                        tbBulanan.setEnabled(false);
                        tbTahunan.setVisible(false);
                        tbTahunan.setEnabled(false);

                        System.out.println("Menampilkan Panel semua");
                        tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "")));
                        valTotalS.setText(tPemasukan);
                        break;
                    case 2:
                        //sembunyikan 
                        txtAkhir.setVisible(false);
                        tbMinggu1.setVisible(false);
                        tbMinggu1.setEnabled(false);
                        tbMinggu2.setVisible(false);
                        tbMinggu2.setEnabled(false);
                        tbBulanan.setVisible(false);
                        tbBulanan.setEnabled(false);
                        tbTahunan.setVisible(false);
                        tbTahunan.setEnabled(false);
                        //tampilkan
                        txtAwal.setVisible(true);
                        txtAwal.setText("Pilih Hari : ");
                        tbHarian.setVisible(true);
                        tbHarian.setEnabled(true);

                        System.out.println("Menampilkan Panel harian");
                        tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE tanggal >= '" + tanggalDipilih1 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun, bulan, hari + 1) + "'")));
                        valTotalH.setText(tPemasukan);
                        break;
                    case 3:
                        System.out.println("Menampilkan Panel bulanan");
                        //sembunyikan 
                        tbHarian.setVisible(false);
                        tbHarian.setEnabled(false);
                        tbMinggu1.setVisible(false);
                        tbMinggu1.setEnabled(false);
                        tbMinggu2.setVisible(false);
                        tbMinggu2.setEnabled(false);
                        //tampilkan bulanan dan tahunan
                        txtAwal.setVisible(true);
                        txtAwal.setText("Pilih Bulan :");
                        txtAkhir.setVisible(true);
                        txtAkhir.setText("Pilih Tahun :");
                        tbBulanan.setVisible(true);
                        tbBulanan.setEnabled(true);
                        tbTahunan.setVisible(true);
                        tbTahunan.setEnabled(true);
                        tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE YEAR(tanggal) = '" + tahunan + "' AND MONTH(tanggal) = '" + bulanan + "'")));
                        valTotalB.setText(tPemasukan);
                        break;
                    case 4:
                        //sembunyikan 
                        tbBulanan.setVisible(false);
                        tbBulanan.setEnabled(false);
                        tbTahunan.setVisible(false);
                        tbTahunan.setEnabled(false);
                        tbHarian.setVisible(false);
                        tbHarian.setEnabled(false);
                        //tampilkan rentan tanggal
                        txtAwal.setVisible(true);
                        txtAwal.setText("Awal :");
                        txtAkhir.setVisible(true);
                        txtAkhir.setText("Akhir :");
                        tbMinggu1.setVisible(true);
                        tbMinggu1.setEnabled(true);
                        tbMinggu2.setVisible(true);
                        tbMinggu2.setEnabled(true);
                        System.out.println("Menampilkan Panel rentang tanggal");
                        tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE tanggal >= '" + tanggalDipilih2 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun1, bulan1, hari1 + 1) + "'")));
                        valTotalM.setText(tPemasukan);
                        break;
                }
            }
        });

        this.tabelDataS.setRowHeight(29);
        this.tabelDataS.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataS.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.tabelDataH.setRowHeight(29);
        this.tabelDataH.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataH.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.tabelDataM.setRowHeight(29);
        this.tabelDataM.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataM.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.tabelDataB.setRowHeight(29);
        this.tabelDataB.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataB.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        JLabel[] lbls = new JLabel[]{
            this.valIDPengeluaran, this.valIDKaryawan, this.valIDTransaksi, this.valNamaKaryawan,
            this.valTanggal, this.valHarga
        };

        for (JLabel lbl : lbls) {
            lbl.addMouseListener(new java.awt.event.MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    lbl.setForeground(new Color(15, 98, 230));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    lbl.setForeground(new Color(0, 0, 0));
                }
            });
        }
        this.keyword = "";
        this.updateTabel(tabelDataS);
        //atur tanggal default tanggal harian
        this.keyword = "WHERE tanggal >= '" + tanggalDipilih1 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun, bulan, hari + 1) + "'";
        this.updateTabel(tabelDataH);
        //atur tanggal default tabel bulanan
        this.keyword = "WHERE YEAR(tanggal) = '" + this.tahunan + "' AND MONTH(tanggal) = '" + this.bulanan + "'";
        this.updateTabel(tabelDataB);
        //atur tanggal default tabel rentang tanggal
        this.keyword = "WHERE tanggal >= '" + this.tanggalDipilih2 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun1, bulan1, hari1 + 1) + "'";
        this.updateTabel(tabelDataM);
        //sembunyikan 
        txtAwal.setVisible(false);
        txtAkhir.setVisible(false);
        tbHarian.setVisible(false);
        tbHarian.setEnabled(false);
        tbMinggu1.setVisible(false);
        tbMinggu1.setEnabled(false);
        tbMinggu2.setVisible(false);
        tbMinggu2.setEnabled(false);
        tbBulanan.setVisible(false);
        tbBulanan.setEnabled(false);
        tbTahunan.setVisible(false);
        tbTahunan.setEnabled(false);
    }

    private void koneksi() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + this.namadb, "root", "");
            this.stmt = con.createStatement();
            this.jumlahKoneksi++;
//            System.out.println("jumlah koneksi : " + db.jumlahKoneksi);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void closeKoneksi() {
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
            }
            db.closeConnection();

            trj.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getJenis(String field) {
        try {
            koneksi();
            int data = 0;
            String sql = "SELECT SUM(jumlah) AS total FROM detail_transaksi_jual WHERE id_tr_jual = '" + this.idTr + "' AND jenis_barang = '" + field + "'";
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

    private int getTotal(String table, String kolom, String kondisi) {
        try {
            koneksi();
            int data = 0;
            String sql = "SELECT SUM(" + kolom + ") AS total FROM " + table + " " + kondisi;
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
            Date tanggalData = new Date();
            int rows = 0, hari_1 = 0, bulan_1 = -1, tahun_1 = 0;
            String sql = "SELECT id_tr_jual, id_karyawan, nama_karyawan, total_hrg, keuntungan, tanggal FROM transaksi_jual " + keyword + " ORDER BY id_tr_jual DESC", tanggalPenuh, tanggalPenuh1;
            obj = new Object[trj.getJumlahData("transaksi_jual", keyword)][7];
//            System.out.println("total baris pada tabel "+trj.getJumlahData("transaksi_jual", keyword));
            
            // mengeksekusi query
            trj.res = trj.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (trj.res.next()) {
                // menyimpan data dari tabel ke object
                obj[rows][0] = trj.res.getString("id_tr_jual").replace("TRJ", "LPD");
                obj[rows][1] = trj.res.getString("id_karyawan");
                obj[rows][2] = trj.res.getString("nama_karyawan");
                obj[rows][3] = text.toMoneyCase(trj.res.getString("total_hrg"));
                obj[rows][4] = text.toMoneyCase(trj.res.getString("keuntungan"));
                tanggalPenuh = trj.res.getString("tanggal");
                tanggalData = tanggalMilis.parse(tanggalPenuh);
                tanggalPenuh1 = date.format(tanggalData);
                hari_1 = Integer.parseInt(tanggalPenuh1.substring(0, 2));
                bulan_1 = Integer.parseInt(tanggalPenuh1.substring(3, 5));
                tahun_1 = Integer.parseInt(tanggalPenuh1.substring(6));
                obj[rows][5] = hari1 + "-" + this.waktu.getNamaBulan(bulan_1 - 1) + "-" + tahun_1;
                obj[rows][6] = tanggalPenuh.substring(11, 19);
                rows++;
            }
            return obj;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private void updateTabel(JTable tabel) throws ParseException {
        tabel.setModel(new javax.swing.table.DefaultTableModel(
                getData(),
                new String[]{
                    "ID Pemasukan", "ID Karyawan", "Nama Karyawan", "Total Harga", "Total pemasukan", "Tanggal", "Waktu"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    private void showData(JTable tabel, int index) throws ParseException {
        int hari_1 = 0, bulan_1 = -1, tahun_1 = 0;
        // mendapatkan data-data
//        this.idTr = tabel.getValueAt(tabel.getSelectedRow(), 0).toString().replace("LPD", "TRJ");
//        this.idPd = this.idTr.replace("TRJ", "LPD");
        this.idTr = this.idSelected.replace("LPD", "TRJ");
        this.idPd = this.idSelected;
        this.IDKaryawan = tabel.getValueAt(index, 1).toString();
        this.namaKaryawan = tabel.getValueAt(index, 2).toString();
        this.totalHrg = text.toIntCase(tabel.getValueAt(index, 3).toString());
        this.keuntungan = text.toIntCase(tabel.getValueAt(index, 4).toString());
        String tanggal1 = this.trj.getTanggal(this.idTr);
        Date d = tanggalMilis.parse(tanggal1);
        this.tanggal = date.format(d);
        //mendapatkan hari dari variabel tanggal 
        hari_1 = Integer.parseInt(this.tanggal.substring(0, 2));
        //mendapatkan bulan dari variabel tanggal 
        bulan_1 = Integer.parseInt(this.tanggal.substring(3, 5));
        //mendapatkan tahun dari variabel tanggal 
        tahun_1 = Integer.parseInt(this.tanggal.substring(6));
        // menampilkan data-data
        this.valIDTransaksi.setText("<html><p>:&nbsp;" + this.idTr + "</p></html>");
        this.valIDPengeluaran.setText("<html><p>:&nbsp;" + this.idPd + "</p></html>");
        this.valIDKaryawan.setText("<html><p>:&nbsp;" + this.IDKaryawan + "</p></html>");
        this.valNamaKaryawan.setText("<html><p>:&nbsp;" + this.namaKaryawan + "</p></html>");
        this.valHarga.setText("<html><p>:&nbsp;" + this.totalHrg + "</p></html>");
        this.valKeuntungan.setText("<html><p>:&nbsp;" + this.keuntungan + "</p></html>");
        this.valTanggal.setText("<html><p>:&nbsp;" + hari1 + "-" + this.waktu.getNamaBulan(bulan_1 - 1) + "-" + tahun_1 + "</p></html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        valIDPengeluaran = new javax.swing.JLabel();
        valIDTransaksi = new javax.swing.JLabel();
        valIDKaryawan = new javax.swing.JLabel();
        valNamaKaryawan = new javax.swing.JLabel();
        valHarga = new javax.swing.JLabel();
        valKeuntungan = new javax.swing.JLabel();
        valTanggal = new javax.swing.JLabel();
        btnDetail = new javax.swing.JLabel();
        btnCetak = new javax.swing.JLabel();
        txtAwal = new javax.swing.JLabel();
        txtAkhir = new javax.swing.JLabel();
        inpCari = new javax.swing.JTextField();
        pnlPieChart = new javax.swing.JPanel();
        tbMinggu2 = new com.toedter.calendar.JDateChooser();
        tbMinggu1 = new com.toedter.calendar.JDateChooser();
        tbBulanan = new com.toedter.calendar.JMonthChooser();
        tbTahunan = new com.toedter.calendar.JYearChooser();
        tbHarian = new com.toedter.calendar.JDateChooser();
        tabPengeluaran = new javax.swing.JTabbedPane();
        LPSEMUA = new javax.swing.JPanel();
        valTotalS = new javax.swing.JLabel();
        pengeluaranS = new javax.swing.JLabel();
        lpSemua = new javax.swing.JScrollPane();
        tabelDataS = new javax.swing.JTable();
        LPHARIAN = new javax.swing.JPanel();
        valTotalH = new javax.swing.JLabel();
        pengeluaranH = new javax.swing.JLabel();
        lpHarian = new javax.swing.JScrollPane();
        tabelDataH = new javax.swing.JTable();
        LPBULANAN = new javax.swing.JPanel();
        valTotalB = new javax.swing.JLabel();
        pengeluaranB = new javax.swing.JLabel();
        lpBulanan = new javax.swing.JScrollPane();
        tabelDataB = new javax.swing.JTable();
        LPRentang = new javax.swing.JPanel();
        valTotalM = new javax.swing.JLabel();
        pengeluaranM = new javax.swing.JLabel();
        lpRentang = new javax.swing.JScrollPane();
        tabelDataM = new javax.swing.JTable();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(957, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        valIDPengeluaran.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valIDPengeluaran.setForeground(new java.awt.Color(0, 0, 0));
        valIDPengeluaran.setText(":");
        add(valIDPengeluaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 65, 360, 25));

        valIDTransaksi.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valIDTransaksi.setForeground(new java.awt.Color(0, 0, 0));
        valIDTransaksi.setText(":");
        add(valIDTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 100, 360, 24));

        valIDKaryawan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valIDKaryawan.setForeground(new java.awt.Color(0, 0, 0));
        valIDKaryawan.setText(":");
        add(valIDKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 134, 360, 24));

        valNamaKaryawan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valNamaKaryawan.setForeground(new java.awt.Color(0, 0, 0));
        valNamaKaryawan.setText(":");
        add(valNamaKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 168, 360, 24));

        valHarga.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valHarga.setForeground(new java.awt.Color(0, 0, 0));
        valHarga.setText(":");
        valHarga.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        add(valHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 201, 360, 24));

        valKeuntungan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valKeuntungan.setForeground(new java.awt.Color(0, 0, 0));
        valKeuntungan.setText(":");
        valKeuntungan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        add(valKeuntungan, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 236, 360, 24));

        valTanggal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valTanggal.setForeground(new java.awt.Color(0, 0, 0));
        valTanggal.setText(":");
        add(valTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 269, 360, 24));

        btnDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-detail-075.png"))); // NOI18N
        btnDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDetailMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDetailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDetailMouseExited(evt);
            }
        });
        add(btnDetail, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 635, 155, 49));

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
        add(btnCetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(267, 635, 151, 49));

        txtAwal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        txtAwal.setForeground(new java.awt.Color(255, 255, 255));
        txtAwal.setText("Awal :");
        add(txtAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 120, 40));

        txtAkhir.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        txtAkhir.setForeground(new java.awt.Color(255, 255, 255));
        txtAkhir.setText("Akhir :");
        add(txtAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 320, 100, 40));

        inpCari.setBackground(new java.awt.Color(255, 255, 255));
        inpCari.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpCari.setForeground(new java.awt.Color(0, 0, 0));
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
        add(inpCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 325, 250, 30));

        pnlPieChart.setBackground(new java.awt.Color(255, 255, 255));
        pnlPieChart.setForeground(new java.awt.Color(255, 255, 0));
        pnlPieChart.setLayout(new java.awt.BorderLayout());
        add(pnlPieChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(595, 65, 550, 230));

        tbMinggu2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbMinggu2PropertyChange(evt);
            }
        });
        add(tbMinggu2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 320, 150, 40));

        tbMinggu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMinggu1MouseClicked(evt);
            }
        });
        tbMinggu1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbMinggu1PropertyChange(evt);
            }
        });
        add(tbMinggu1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 320, 150, 40));

        tbBulanan.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        tbBulanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBulananMouseClicked(evt);
            }
        });
        tbBulanan.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbBulananPropertyChange(evt);
            }
        });
        add(tbBulanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 320, 150, 40));

        tbTahunan.setEnabled(false);
        tbTahunan.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbTahunanPropertyChange(evt);
            }
        });
        add(tbTahunan, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 320, 150, 40));

        tbHarian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbHarianMouseClicked(evt);
            }
        });
        tbHarian.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbHarianPropertyChange(evt);
            }
        });
        add(tbHarian, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 320, 150, 40));

        LPSEMUA.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        valTotalS.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valTotalS.setForeground(new java.awt.Color(0, 0, 0));
        valTotalS.setText(":");
        LPSEMUA.add(valTotalS, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 190, 225, 25));

        pengeluaranS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/laporan-pemasukan.png"))); // NOI18N
        pengeluaranS.setText("lbll");
        LPSEMUA.add(pengeluaranS, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 190, 450, 25));

        tabelDataS.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataS.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataS.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pemasukan", "ID Karyawan", "Nama Karyawan", "Total Harga", "Total Pemasukan", "Tanggal", "Waktu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelDataS.setGridColor(new java.awt.Color(0, 0, 0));
        tabelDataS.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelDataS.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelDataS.getTableHeader().setReorderingAllowed(false);
        tabelDataS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataSMouseClicked(evt);
            }
        });
        tabelDataS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataSKeyPressed(evt);
            }
        });
        lpSemua.setViewportView(tabelDataS);

        LPSEMUA.add(lpSemua, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1140, 185));

        tabPengeluaran.addTab("Semua", LPSEMUA);

        LPHARIAN.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        valTotalH.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valTotalH.setForeground(new java.awt.Color(0, 0, 0));
        valTotalH.setText(":");
        LPHARIAN.add(valTotalH, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 190, 225, 25));

        pengeluaranH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/laporan-pemasukan.png"))); // NOI18N
        pengeluaranH.setText("lbll");
        LPHARIAN.add(pengeluaranH, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 190, 450, 25));

        tabelDataH.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataH.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataH.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataH.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pemasukan", "ID Karyawan", "Nama Karyawan", "Total Harga", "Total Pemasukan", "Tanggal", "Waktu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelDataH.setGridColor(new java.awt.Color(0, 0, 0));
        tabelDataH.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelDataH.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelDataH.getTableHeader().setReorderingAllowed(false);
        tabelDataH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataHMouseClicked(evt);
            }
        });
        tabelDataH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataHKeyPressed(evt);
            }
        });
        lpHarian.setViewportView(tabelDataH);

        LPHARIAN.add(lpHarian, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1140, 185));

        tabPengeluaran.addTab("Harian", LPHARIAN);

        LPBULANAN.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        valTotalB.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valTotalB.setForeground(new java.awt.Color(0, 0, 0));
        valTotalB.setText(":");
        LPBULANAN.add(valTotalB, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 190, 225, 25));

        pengeluaranB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/laporan-pemasukan.png"))); // NOI18N
        pengeluaranB.setText("lbll");
        LPBULANAN.add(pengeluaranB, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 190, 450, 25));

        tabelDataB.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataB.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataB.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pemasukan", "ID Karyawan", "Nama Karyawan", "Total Harga", "Total Pemasukan ", "Tanggal", "Waktu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelDataB.setGridColor(new java.awt.Color(0, 0, 0));
        tabelDataB.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelDataB.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelDataB.getTableHeader().setReorderingAllowed(false);
        tabelDataB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataBMouseClicked(evt);
            }
        });
        tabelDataB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataBKeyPressed(evt);
            }
        });
        lpBulanan.setViewportView(tabelDataB);

        LPBULANAN.add(lpBulanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1140, 185));

        tabPengeluaran.addTab("Bulanan", LPBULANAN);

        LPRentang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        valTotalM.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valTotalM.setForeground(new java.awt.Color(0, 0, 0));
        valTotalM.setText(":");
        LPRentang.add(valTotalM, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 190, 225, 25));

        pengeluaranM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/laporan-pemasukan.png"))); // NOI18N
        pengeluaranM.setText("lbll");
        LPRentang.add(pengeluaranM, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 190, 450, 25));

        tabelDataM.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataM.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataM.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pemasukan", "ID Karyawan", "Nama Karyawan", "Total Harga", "Total Pemasukan", "Tanggal", "Waktu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelDataM.setGridColor(new java.awt.Color(0, 0, 0));
        tabelDataM.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelDataM.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelDataM.getTableHeader().setReorderingAllowed(false);
        tabelDataM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataMMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tabelDataMMouseEntered(evt);
            }
        });
        tabelDataM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataMKeyPressed(evt);
            }
        });
        lpRentang.setViewportView(tabelDataM);

        LPRentang.add(lpRentang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1140, 185));

        tabPengeluaran.addTab("Rentang Tanggal", LPRentang);

        add(tabPengeluaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 365, 1140, 250));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-laporan-pemasukan.png"))); // NOI18N
        background.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        try {
            String key = this.inpCari.getText();
            String data = "";
            if (key.contains("TRB") || key.contains("LPD") || key.contains("trj") || key.contains("lpd")) {
                data = "TRJ" + key.substring(3, key.length());
            }
            this.keyword = "WHERE id_tr_jual LIKE '%" + data + "%'";
            JTable tabel = new JTable();
            switch (selectedIndex) {
                case 1:
                    tabel = this.tabelDataS;
                    break;
                case 2:
                    tabel = this.tabelDataH;
                    break;
                case 3:
                    tabel = this.tabelDataB;
                    break;
                case 4:
                    tabel = this.tabelDataM;
                    break;
            }
            this.updateTabel(tabel);
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inpCariKeyTyped

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        try {
            String key = this.inpCari.getText();
            String data = "";
            if (key.contains("TRB") || key.contains("LPD") || key.contains("trj") || key.contains("lpd")) {
                data = "TRJ" + key.substring(3, key.length());
            }
            this.keyword = "WHERE id_tr_jual LIKE '%" + data + "%'";
            JTable tabel = new JTable();
            switch (selectedIndex) {
                case 1:
                    tabel = this.tabelDataS;
                    break;
                case 2:
                    tabel = this.tabelDataH;
                    break;
                case 3:
                    tabel = this.tabelDataB;
                    break;
                case 4:
                    tabel = this.tabelDataM;
                    break;
            }
            this.updateTabel(tabel);
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inpCariKeyReleased

    private void tabelDataBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataBKeyPressed
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                if (this.tabelDataB.getSelectedRow() >= 1) {
                    this.idSelected = this.tabelDataB.getValueAt(tabelDataB.getSelectedRow() - 1, 0).toString();
                    this.showData(tabelDataB,tabelDataB.getSelectedRow()-1);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                if (this.tabelDataB.getSelectedRow() < (this.tabelDataB.getRowCount() - 1)) {
                    this.idSelected = this.tabelDataB.getValueAt(tabelDataB.getSelectedRow() + 1, 0).toString();
                    this.showData(tabelDataB,tabelDataB.getSelectedRow()+1);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataBKeyPressed

    private void tabelDataBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataBMouseClicked
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // menampilkan data pembeli
            this.idSelected = this.tabelDataB.getValueAt(tabelDataB.getSelectedRow(), 0).toString();
//            System.out.println("baris : " + tabelDataB.getSelectedRow());
            this.showData(tabelDataB,tabelDataB.getSelectedRow());
            int pMakanan = getJenis("MAKANAN");
            int pMinuman = getJenis("MINUMAN");
            int pSnack = getJenis("SNACK");
            int pAtk = getJenis("ATK");
            this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataBMouseClicked

    private void tabelDataMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataMKeyPressed
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                if (this.tabelDataM.getSelectedRow() >= 1) {
                    this.idSelected = this.tabelDataM.getValueAt(tabelDataM.getSelectedRow() - 1, 0).toString();
                    this.showData(tabelDataM,tabelDataM.getSelectedRow()-1);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                if (this.tabelDataM.getSelectedRow() < (this.tabelDataM.getRowCount() - 1)) {
                    this.idSelected = this.tabelDataM.getValueAt(tabelDataM.getSelectedRow() + 1, 0).toString();
                    this.showData(tabelDataM,tabelDataM.getSelectedRow()+1);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataMKeyPressed

    private void tabelDataMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMMouseClicked
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.idSelected = this.tabelDataM.getValueAt(tabelDataM.getSelectedRow(), 0).toString();
            this.showData(tabelDataM,tabelDataM.getSelectedRow());
            int pMakanan = getJenis("MAKANAN");
            int pMinuman = getJenis("MINUMAN");
            int pSnack = getJenis("SNACK");
            int pAtk = getJenis("ATK");
            this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataMMouseClicked

    private void tabelDataHKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataHKeyPressed
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                if (this.tabelDataH.getSelectedRow() >= 1) {
                    this.idSelected = this.tabelDataH.getValueAt(tabelDataH.getSelectedRow() - 1, 0).toString();
                    this.showData(tabelDataH,tabelDataH.getSelectedRow()-1);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                if (this.tabelDataH.getSelectedRow() < (this.tabelDataH.getRowCount() - 1)) {
                    this.idSelected = this.tabelDataH.getValueAt(tabelDataH.getSelectedRow() + 1, 0).toString();
                    this.showData(tabelDataH,tabelDataH.getSelectedRow()+1);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataHKeyPressed

    private void tabelDataHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataHMouseClicked
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.idSelected = this.tabelDataH.getValueAt(tabelDataH.getSelectedRow(), 0).toString();
            this.showData(tabelDataH,tabelDataH.getSelectedRow());
//            System.out.println("baris : " + tabelDataH.getSelectedRow());
            int pMakanan = getJenis("MAKANAN");
            int pMinuman = getJenis("MINUMAN");
            int pSnack = getJenis("SNACK");
            int pAtk = getJenis("ATK");
            this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataHMouseClicked

    private void tabelDataSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataSKeyPressed
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                if (this.tabelDataS.getSelectedRow() >= 1) {
                    this.idSelected = this.tabelDataS.getValueAt(tabelDataS.getSelectedRow()-1, 0).toString();
                    this.showData(tabelDataS,tabelDataS.getSelectedRow()-1);
                    System.out.println("baris : " + (tabelDataS.getSelectedRow()) +"-id laporan "+this.idSelected);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                if (this.tabelDataS.getSelectedRow() < (this.tabelDataS.getRowCount() - 1)) {
                    this.idSelected = this.tabelDataS.getValueAt(tabelDataS.getSelectedRow()+1 , 0).toString();
                    this.showData(tabelDataS,tabelDataS.getSelectedRow()+1);
                    System.out.println("baris : " + (tabelDataS.getSelectedRow()+2)+" Id laporan "+this.idSelected);
                    int pMakanan = getJenis("MAKANAN");
                    int pMinuman = getJenis("MINUMAN");
                    int pSnack = getJenis("SNACK");
                    int pAtk = getJenis("ATK");
                    this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataSKeyPressed

    private void tabelDataSMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataSMouseClicked
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.idSelected = this.tabelDataS.getValueAt(tabelDataS.getSelectedRow(), 0).toString();
            this.showData(tabelDataS,tabelDataS.getSelectedRow());
//            System.out.println("baris : " + (tabelDataS.getSelectedRow()+1));
            int pMakanan = getJenis("MAKANAN");
            int pMinuman = getJenis("MINUMAN");
            int pSnack = getJenis("SNACK");
            int pAtk = getJenis("ATK");
            this.chart.showPieChart(this.pnlPieChart, "", pMakanan, pMinuman, pSnack, pAtk);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataSMouseClicked

    private void inpCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpCariActionPerformed

    private void btnDetailMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDetailMouseEntered
        this.btnDetail.setIcon(Gambar.getAktiveIcon(this.btnDetail.getIcon().toString()));
    }//GEN-LAST:event_btnDetailMouseEntered

    private void btnDetailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDetailMouseExited
        this.btnDetail.setIcon(Gambar.getBiasaIcon(this.btnDetail.getIcon().toString()));
    }//GEN-LAST:event_btnDetailMouseExited

    private void btnDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDetailMouseClicked
        boolean erorr = false;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        JTable tabel = new JTable();
        switch (this.selectedIndex) {
            case 1:
                if (this.tabelDataS.getSelectedRow() < 0) {
                    System.out.println("Tidak ada data yang dipilih !");
                    erorr = true;
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    Message.showWarning(this, "Tidak ada data yang dipilih !");
                }
                break;
            case 2:
                if (this.tabelDataH.getSelectedRow() < 0) {
                    System.out.println("Tidak ada data yang dipilih !");
                    erorr = true;
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    Message.showWarning(this, "Tidak ada data yang dipilih !");
                }
                break;
            case 3:
                if (this.tabelDataB.getSelectedRow() < 0) {
                    System.out.println("Tidak ada data yang dipilih !");
                    erorr = true;
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    Message.showWarning(this, "Tidak ada data yang dipilih !");
                }
                break;
            case 4:
                if (this.tabelDataM.getSelectedRow() < 0) {
                    System.out.println("Tidak ada data yang dipilih !");
                    erorr = true;
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    Message.showWarning(this, "Tidak ada data yang dipilih !");
                }
                break;
        }
        if (!erorr) {
            try {
                Audio.play(Audio.SOUND_INFO);
                DetailLaporanJual detail = new DetailLaporanJual(this.idTr);
                this.dataDetail(detail);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } catch (ParseException ex) {
                Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //
    }//GEN-LAST:event_btnDetailMouseClicked
    
    private void dataDetail(JPanel pnl){
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // menghapus panel lama
        MainWindow.pnlMenu.removeAll();
        MainWindow.pnlMenu.repaint();
        MainWindow.pnlMenu.revalidate();
        this.closeKoneksi();
        // menambahkan panel baru
        MainWindow.pnlMenu.add(pnl);
        MainWindow.pnlMenu.validate();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void tbMinggu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbMinggu1MouseClicked
    }//GEN-LAST:event_tbMinggu1MouseClicked

    private void tbHarianMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHarianMouseClicked
    }//GEN-LAST:event_tbHarianMouseClicked

    private void tbBulananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbBulananMouseClicked
    }//GEN-LAST:event_tbBulananMouseClicked

    private void tbHarianPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbHarianPropertyChange
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //update tabel
            this.tHarian1 = tbHarian.getDate();
            this.tanggalDipilih1 = date1.format(tHarian1);
            this.hari = Integer.parseInt(tanggalDipilih1.substring(8));
            this.bulan = Integer.parseInt(tanggalDipilih1.substring(5, 7));
            this.tahun = Integer.parseInt(tanggalDipilih1.substring(0, 4));
            this.keyword = "WHERE tanggal >= '" + tanggalDipilih1 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun, bulan, hari + 1) + "'";
            this.updateTabel(this.tabelDataH);
            //update totalharga
            this.tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE tanggal >= '" + tanggalDipilih1 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun, bulan, hari + 1) + "'")));
            this.valTotalH.setText(tPemasukan);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tbHarianPropertyChange

    private void tbBulananPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbBulananPropertyChange
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //update tabel
            this.bulanan = tbBulanan.getMonth() + 1;
            this.keyword = "WHERE YEAR(tanggal) = '" + this.tahunan + "' AND MONTH(tanggal) = '" + this.bulanan + "'";
            updateTabel(this.tabelDataB);
            //update totalharga
            this.tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE YEAR(tanggal) = '" + tahunan + "' AND MONTH(tanggal) = '" + bulanan + "'")));
            this.valTotalB.setText(tPemasukan);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tbBulananPropertyChange

    private void tbTahunanPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbTahunanPropertyChange
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //update tabel
            this.tahunan = tbTahunan.getYear();
            this.keyword = "WHERE YEAR(tanggal) = '" + this.tahunan + "' AND MONTH(tanggal) = '" + this.bulanan + "'";
            updateTabel(this.tabelDataB);
            //update totalharga
            this.tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE YEAR(tanggal) = '" + tahunan + "' AND MONTH(tanggal) = '" + bulanan + "'")));
            this.valTotalB.setText(tPemasukan);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tbTahunanPropertyChange

    private void tbMinggu1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbMinggu1PropertyChange
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.tHarian2 = tbMinggu1.getDate();
            if (tHarian2.compareTo(tHarian3) > 0) {
                System.out.println("Tanggal awal tidak boleh lebih dari tanggal akhir !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                this.tanggalDipilih2 = date1.format(tHarian2_old);
                tbMinggu1.setDate(date1.parse(this.tanggalDipilih1));
                Message.showWarning(this, "Tanggal awal tidak boleh lebih dari tanggal akhir !");
            } else {
                //update waktu lama
                this.tHarian2_old = this.tHarian2;
                //update tabel
                this.tanggalDipilih2 = date1.format(tHarian2);
                keyword = "WHERE tanggal >= '" + this.tanggalDipilih2 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun1, bulan1, hari1 + 1) + "'";
                this.updateTabel(this.tabelDataM);
                //update totalharga
                this.tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE tanggal >= '" + tanggalDipilih2 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun1, bulan1, hari1 + 1) + "'")));
                this.valTotalM.setText(tPemasukan);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tbMinggu1PropertyChange

    private void tabelDataMMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMMouseEntered
    }//GEN-LAST:event_tabelDataMMouseEntered

    private void tbMinggu2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbMinggu2PropertyChange
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.tHarian3 = tbMinggu2.getDate();
            if (tHarian3.compareTo(tHarian2) < 0) {
                System.out.println("Tanggal akhir tidak boleh kurang dari tanggal awal !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                this.tanggalDipilih3 = date1.format(tHarian3_old);
                tbMinggu2.setDate(date1.parse(this.tanggalDipilih3));
                Message.showWarning(this, "Tanggal akhir tidak boleh kurang dari tanggal awal !");
            } else {
                //update waktu lama
                this.tHarian3_old = this.tHarian3;
                //update tabel
                this.tanggalDipilih3 = date1.format(tHarian3);
                this.hari1 = Integer.parseInt(tanggalDipilih3.substring(8));
                this.bulan1 = Integer.parseInt(tanggalDipilih3.substring(5, 7));
                this.tahun1 = Integer.parseInt(tanggalDipilih3.substring(0, 4));
                keyword = "WHERE tanggal >= '" + this.tanggalDipilih2 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun1, bulan1, hari1 + 1) + "'";
                this.updateTabel(this.tabelDataM);
                //update totalharga
                this.tPemasukan = text.toMoneyCase(Integer.toString(getTotal("transaksi_jual", "total_hrg", "WHERE tanggal >= '" + tanggalDipilih2 + "' AND tanggal <= '" + String.format("%s-%s-%s", tahun1, bulan1, hari1 + 1) + "'")));
                this.valTotalM.setText(tPemasukan);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        } catch (ParseException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tbMinggu2PropertyChange

    private void btnCetakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakMouseClicked
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            switch (this.selectedIndex) {
                case 1:
                    tabelDataS.print();
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    break;
                case 2:
                    if (tabelDataH.getRowCount() > 0) {
                        tabelDataH.print();
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    } else {
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        Message.showWarning(this, "Tabel kosong !");
                    }
                    break;
                case 3:
                    if (tabelDataB.getRowCount() > 0) {
                        tabelDataH.print();
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    } else {
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        Message.showWarning(this, "Tabel kosong !");
                    }
                    break;
                case 4:
                    if (tabelDataM.getRowCount() > 0) {
                        tabelDataH.print();
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    } else {
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        Message.showWarning(this, "Tabel kosong !");
                    }
                    break;
            }
        } catch (PrinterException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCetakMouseClicked

    private void btnCetakMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakMouseEntered

    private void btnCetakMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCetakMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LPBULANAN;
    private javax.swing.JPanel LPHARIAN;
    private javax.swing.JPanel LPRentang;
    private javax.swing.JPanel LPSEMUA;
    private javax.swing.JLabel background;
    private javax.swing.JLabel btnCetak;
    private javax.swing.JLabel btnDetail;
    private javax.swing.JTextField inpCari;
    private javax.swing.JScrollPane lpBulanan;
    private javax.swing.JScrollPane lpHarian;
    private javax.swing.JScrollPane lpRentang;
    private javax.swing.JScrollPane lpSemua;
    private javax.swing.JLabel pengeluaranB;
    private javax.swing.JLabel pengeluaranH;
    private javax.swing.JLabel pengeluaranM;
    private javax.swing.JLabel pengeluaranS;
    private javax.swing.JPanel pnlPieChart;
    private javax.swing.JTabbedPane tabPengeluaran;
    private javax.swing.JTable tabelDataB;
    private javax.swing.JTable tabelDataH;
    private javax.swing.JTable tabelDataM;
    private javax.swing.JTable tabelDataS;
    private com.toedter.calendar.JMonthChooser tbBulanan;
    private com.toedter.calendar.JDateChooser tbHarian;
    private com.toedter.calendar.JDateChooser tbMinggu1;
    private com.toedter.calendar.JDateChooser tbMinggu2;
    private com.toedter.calendar.JYearChooser tbTahunan;
    private javax.swing.JLabel txtAkhir;
    private javax.swing.JLabel txtAwal;
    private javax.swing.JLabel valHarga;
    private javax.swing.JLabel valIDKaryawan;
    private javax.swing.JLabel valIDPengeluaran;
    private javax.swing.JLabel valIDTransaksi;
    private javax.swing.JLabel valKeuntungan;
    private javax.swing.JLabel valNamaKaryawan;
    private javax.swing.JLabel valTanggal;
    private javax.swing.JLabel valTotalB;
    private javax.swing.JLabel valTotalH;
    private javax.swing.JLabel valTotalM;
    private javax.swing.JLabel valTotalS;
    // End of variables declaration//GEN-END:variables
}

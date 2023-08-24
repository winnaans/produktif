package com.window.panels;

import Report.cetak;
import com.data.db.Database;
import com.error.InValidUserDataException;
import com.manage.Barang;
import com.manage.Diskon;
import com.manage.Message;
import com.manage.Text;
import com.manage.Waktu;
import com.manage.ManageTransaksiJual;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.users.Users;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * @author Amirzan Fikri P
 */
public class TransaksiJual extends javax.swing.JPanel implements DocumentListener, ActionListener {

    private Timer timer;
    private final Users user = new Users();
    private final Barang barang = new Barang();
    private final Diskon diskon = new Diskon();
    private final ManageTransaksiJual trj = new ManageTransaksiJual();
    private final Text text = new Text();
    private final Waktu waktu = new Waktu();
    private final Database db = new Database();
//    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
    private String keywordSaldo = "", keywordDiskon = "", keywordBarang = "", idSelectedBarang;
    private String idTr, namaTr, namaBarang, idKaryawan, idBarang, tglNow;
    private int jumlah = 1, hargaJual, totalHarga = 0, stok = 0, jumlahDiskon = 0, Saldo = 0, lastBayar = 0;
    private Object[][] objBarang, daftarDiskon;
    private boolean isPrint = false, isBarcode = false;

    public TransaksiJual() {
        initComponents();
        db.startConnection();
        this.timer = new javax.swing.Timer(1000, this);
        this.timer.setRepeats(false);
        this.inpCariBarang.getDocument().addDocumentListener(this);
        this.updateSaldo();
        this.idTr = this.trj.createIDTransaksi();
        this.inpJumlah.setText("1");
        this.txtTotalHarga.setText(text.toMoneyCase("0"));
        this.inpBayar.setText("0");
        this.txtSebelum.setText(text.toMoneyCase("0"));
        this.txtDiskon.setText(text.toMoneyCase("0"));
        this.txtTotal.setText(text.toMoneyCase("0"));
        this.txtKembalian.setText(text.toMoneyCase("0"));
        this.inpID.setText("<html><p>:&nbsp;" + this.trj.createIDTransaksi() + "</p></html>");
        this.inpNamaPetugas.setText("<html><p>:&nbsp;" + this.user.getCurrentLoginName() + "</p></html>");
        this.idKaryawan = this.user.getIdKaryawan(this.user.getCurrentLogin());
        this.txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo)));
        this.btnTambah.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnEdit.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnHapus.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnBayar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnBatal.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.tabelDataBarang.setRowHeight(29);
        this.tabelDataBarang.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataBarang.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
        this.getDataBarang();
        this.updateTabelBarang();
        this.updateDiskon();
        // mengupdate waktu
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (isVisible()) {
                        tglNow = waktu.getUpdateTime();
                        inpTanggal.setText("<html><p>:&nbsp;" + tglNow + "</p></html>");
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ex) {
                    Message.showException(this, "Terjadi Kesalahan Saat Mengupdate Tanggal!\n" + ex.getMessage(), ex, true);
                }
            }
        }).start();
    }

    private void cariBarcode(String cari) {
        try {
            String sql = "SELECT id_barang FROM barang WHERE barcode = '" + cari + "'";
//            System.out.println("sql barcode " + sql);
            barang.res = barang.stat.executeQuery(sql);
            if (barang.res.next()) {
                this.idSelectedBarang = barang.res.getString("id_barang");
                this.idBarang = this.idSelectedBarang;
                this.showBarang();
                this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                this.inpCariBarang.setText("");
                this.tambahBarang();
            }
        } catch (SQLException ex) {
//            Logger.getLogger(TransaksiJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String saldoCreateID() {
        String lastID = this.saldoGetLastID(), nomor;
        if (lastID != null) {
            nomor = lastID.substring(1);
        } else {
            nomor = "000000000";
        }

        // mengecek nilai dari nomor adalah number atau tidak
        if (text.isNumber(nomor)) {
            // jika id saldo belum exist maka id akan 
            return String.format("S%09d", Integer.parseInt(nomor) + 1);
        }
        return null;
    }

    private String saldoGetLastID() {
        try {
            String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 0,1", "saldo", "id_saldo");
            db.res = db.stat.executeQuery(query);
            if (db.res.next()) {
                return db.res.getString("id_saldo");
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    public void closeKoneksi() {
        db.closeConnection();
        user.closeConnection();
        barang.closeConnection();
        trj.closeConnection();
        diskon.closeConnection();
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

    private void updateDiskon() {
        try {
            int rows = 0;
            String sql = "SELECT id_diskon, nama_diskon, jumlah_diskon, minimal_harga, tanggal_awal, tanggal_akhir FROM diskon " + keywordDiskon;
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            this.daftarDiskon = new Object[diskon.getJumlahData("diskon", keywordDiskon)][6];
            // mengeksekusi query
            diskon.res = diskon.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (diskon.res.next()) {
                // menyimpan data dari tabel ke object
                this.daftarDiskon[rows][0] = diskon.res.getString("id_diskon");
                this.daftarDiskon[rows][1] = diskon.res.getString("nama_diskon");
                this.daftarDiskon[rows][2] = Integer.parseInt(diskon.res.getString("jumlah_diskon"));
                this.daftarDiskon[rows][3] = Integer.parseInt(diskon.res.getString("minimal_harga"));
                this.daftarDiskon[rows][4] = diskon.res.getString("tanggal_awal");
                this.daftarDiskon[rows][5] = diskon.res.getString("tanggal_akhir");
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            diskon.closeConnection();
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
    }

    private void getDataBarang() {
        try {
            int rows = 0;
            String sql = "SELECT id_barang, nama_barang, jenis_barang, stok, harga_beli, harga_jual FROM barang ";
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            this.objBarang = new Object[barang.getJumlahData("barang", keywordBarang)][5];
            // mengeksekusi query
            barang.res = barang.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (barang.res.next()) {
                // menyimpan data dari tabel ke object
                this.objBarang[rows][0] = barang.res.getString("id_barang");
                this.objBarang[rows][1] = barang.res.getString("nama_barang");
                this.objBarang[rows][2] = text.toCapitalize(barang.res.getString("jenis_barang"));
                this.objBarang[rows][3] = barang.res.getString("stok");
                this.objBarang[rows][4] = text.toMoneyCase(barang.res.getString("harga_jual"));
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
    }

    private Object[][] getDataBarangDb() {
        try {
            Object[][] obj;
            int rows = 0;
            String sql = "SELECT id_barang, nama_barang, jenis_barang, stok, harga_beli, harga_jual FROM barang " + keywordBarang;
            System.out.println("sql pada db " + sql);
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            obj = new Object[barang.getJumlahData("barang", keywordBarang)][5];
            // mengeksekusi query
            barang.res = barang.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (barang.res.next()) {
                // menyimpan data dari tabel ke object
                obj[rows][0] = barang.res.getString("id_barang");
                obj[rows][1] = barang.res.getString("nama_barang");
                obj[rows][2] = text.toCapitalize(barang.res.getString("jenis_barang"));
                obj[rows][3] = barang.res.getString("stok");
                obj[rows][4] = text.toMoneyCase(barang.res.getString("harga_jual"));
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return obj;
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private int getTotal(String table, String kolom, String kondisi) {
        try {
            int data = 0;
            String sql = "SELECT SUM(" + kolom + ") AS total FROM " + table + " " + kondisi;
            db.res = db.stat.executeQuery(sql);
            while (db.res.next()) {
                data = db.res.getInt("total");
            }
//            db.closeConnection();
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

    private void addrowtotabeldetail(Object[] dataRow) {
        DefaultTableModel model = (DefaultTableModel) this.tabelData.getModel();
        model.addRow(dataRow);
    }

    private void updateTabelBarang() {
        this.tabelDataBarang.setModel(new javax.swing.table.DefaultTableModel(
                this.objBarang,
                new String[]{
                    "ID Barang", "Nama Barang", "Jenis Barang", "Stok", "Harga"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    private void updateTabelBarangDb() {
        this.tabelDataBarang.setModel(new javax.swing.table.DefaultTableModel(
                this.getDataBarangDb(),
                new String[]{
                    "ID Barang", "Nama Barang", "Jenis Barang", "Stok", "Harga"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    private boolean isSelectedBarang() {
        return this.tabelDataBarang.getSelectedRow() > - 1;
    }

    private void updateTabelData() {
        DefaultTableModel model = (DefaultTableModel) tabelData.getModel();
        while (tabelData.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    private void showDataBarang() {
        // cek apakah ada data barang yang dipilih
        if (this.isSelectedBarang()) {
            // mendapatkan data barang
            this.idBarang = this.idSelectedBarang;
            this.namaBarang = text.toCapitalize(this.barang.getNamaBarang(this.idBarang));
            this.stok = Integer.parseInt(this.barang.getStok(this.idBarang));
            this.hargaJual = Integer.parseInt(this.barang.getHargaJual(this.idBarang));

            // menampilkan data barang
            this.inpIDBarang.setText("<html><p>:&nbsp;" + this.idBarang + "</p></html>");
            this.inpNamaBarang.setText("<html><p>:&nbsp;" + this.namaBarang + "</p></html>");
            this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase(Integer.toString(this.hargaJual)) + "</p></html>");
        }
    }

    private void showBarang() {
        // mendapatkan data barang
        this.idBarang = this.idSelectedBarang;
        this.namaBarang = text.toCapitalize(this.barang.getNamaBarang(this.idBarang));
        this.stok = Integer.parseInt(this.barang.getStok(this.idBarang));
        this.hargaJual = Integer.parseInt(this.barang.getHargaJual(this.idBarang));

        // menampilkan data barang
        this.inpIDBarang.setText("<html><p>:&nbsp;" + this.idBarang + "</p></html>");
        this.inpNamaBarang.setText("<html><p>:&nbsp;" + this.namaBarang + "</p></html>");
        this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase(Integer.toString(this.hargaJual)) + "</p></html>");
    }

    private void resetInput() {
        this.idBarang = "";
        this.idSelectedBarang = "";
        this.namaBarang = "";
        this.stok = 0;
        this.hargaJual = 0;
        this.inpIDBarang.setText("<html><p>:&nbsp;</p></html>");
        this.inpNamaBarang.setText("<html><p>:&nbsp;</p></html>");
        this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase("0") + "</p></html>");
        this.inpJumlah.setText("1");
        this.txtTotalHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase("0") + "</p></html>");
    }

    private void cetakNota(Map parameter) {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load("src\\Report\\notaPenjualan.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jPrint = JasperFillManager.fillReport(jasperReport, parameter, db.conn);
            JasperViewer.viewReport(jPrint);
        } catch (JRException ex) {
            Logger.getLogger(cetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tambahBarang() {
        /**
         * btn simpan digunakan untuk menambah data ke tabel transaksi jika
         * tabel transaksi kosong maka tambah data barang yg dipilih dan id
         * suppllier yg dipilih ke tabel transaksi jika tabel transaksi berisi
         * data maka : jika id barang yang dipilih di tabel barang sama dengan
         * id barang di tabel transaksi maka : cari idbarang, indeks baris,
         * stok, harga total tabel transaksi berdasarkan idbarang yang dipilih
         * di tabel barang. jika jumlah barang yang dimasukkan ke tabel
         * transaksi lebih dari jumlah barang(stok) di tabel barang maka : beri
         * pesan eror "jumlah barang lebih dari stok yang tersedia" jika jumlah
         * barang yang dimasukkan ke tabel transaksi kurang dari sama dengan
         * jumlah barang(stok) di tabel barang maka : ubah data di tabel
         * transaksi berdasarkan indeks baris. jika id barang yang dipilih di
         * tabel barang berbeda dengan id barang di tabel transaksi maka : jika
         * jumlah barang yang dimasukkan ke tabel transaksi lebih dari jumlah
         * barang(stok) di tabel barang maka : beri pesan eror "jumlah barang
         * lebih dari stok yang tersedia" jika jumlah barang yang dimasukkan ke
         * tabel transaksi kurang dari sama dengan jumlah barang(stok) di tabel
         * barang maka : tambah data barang yg dipilih dan id suppllier yg
         * dipilih ke tabel transaksi
         *
         */
        try {
            //ubah cursor menjadi cursor loading
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //deklarasi dan inisialisasi variabel
            DefaultTableModel modelData = (DefaultTableModel) tabelData.getModel();
//            DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
            boolean cocok = false, error = false;
            Date waktuSekarang, tanggal;
            int index = -1, minimalPembelian = 0, tharga = 0, saldobaru = 0, total = 0, stokSekarang = 0, totalProduk = 0, sisaStok = 0, jumlahB = 0, thargaLama = 0, thargaBaru = 0, baris = -1;
            if (this.idBarang.equals("") || this.idSelectedBarang.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Tidak ada barang yang dipilih !");
            } else if (inpTanggal.getText().equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Tanggal harus Di isi !");
                //mengecek apakah ID transaksi sudah di isi
            } else if (inpID.getText().equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "ID Transaksi harus Di isi !");
                //mengecek apakah ID barang sudah di isi
            } else if (inpIDBarang.getText().equals(":")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "ID Barang harus Di isi !");
                //mengecek apakah nama barang sudah di isi
            } else if (inpNamaBarang.getText().equals(":")) {
                error = true;
                Message.showWarning(this, "Nama Barang harus Di isi !");
                //mengecek apakah jumlah barang sudah di isi
            } else if (inpJumlah.getText().equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Jumlah Barang harus Di isi !");
            } else if (Integer.parseInt(inpJumlah.getText()) <= 0) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Jumlah Barang harus Lebih dari 0!");
            } else if (!text.isNumber(inpJumlah.getText())) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Jumlah Barang harus angka !");
                //mengecek apakah harga barang sudah diisi
            } else if (inpHarga.getText().equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Harga Harus di isi!");
                //mengecek apakah total harga sudah diisi
            } else if (txtTotalHarga.getText().equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Harga Total Harus di isi!");
            }
            if (!error) {
                System.out.println("menambahkan data");
                System.out.println("id barang " + this.objBarang[0][0].toString());
                //mencari stok dari tabel barang berdasarkan kondisi idbarang di tabel transaksi sama dengan id barang di tabel barang
                for (int i = 0; i < this.objBarang.length; i++) {
                    if (this.objBarang[i][0].toString().equals(this.idBarang)) {
                        System.out.println("data object ditemukan pada " + i);
                        //memasukkan index object
                        index = i;
                        //memasukkan value stok dari tabel barang 
                        stokSekarang = Integer.parseInt(this.objBarang[i][3].toString());
                        //lalu hentikan for loop
                        break;
                    }
                }
                //mengecek tabel transaksi apakah ada data
                if (tabelData.getRowCount() >= 1) {
                    String idBarangLama = "", idbarang = "";
                    int stokLama = 0, stokBaru = 0;
                    //mencari indeks baris, stok, total harga barang dari tabel transaksi dengan kondisi id barang di tabel transaksi sama dengan id barang di tabel barang
                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                        idbarang = tabelData.getValueAt(i, 2).toString();
                        if (this.idBarang.equals(idbarang)) {
                            cocok = true;
                            baris = i;
                            idBarangLama = idbarang;
                            //mengambil value jumlah barang dari tabel transaksi 
                            stokLama = Integer.parseInt(tabelData.getValueAt(i, 5).toString());
                            //mengambil value total harga dari tabel transaksi 
                            thargaLama = Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                            //lalu hentikan for loop
                            break;
                        }
                    }

                    //mengecek jika id barang di tabel transaksi sama dengan id barang di tabel barang 
                    if (cocok) {
//                        System.out.println("data barang sama");
                        //mengambil value jumlah barang dari jlabel jumlah
                        jumlahB = Integer.parseInt(inpJumlah.getText());
                        //mengambil value harga total dari jlabel total
                        thargaBaru = text.toIntCase(txtTotalHarga.getText());
                        //hitung harga total
                        tharga = thargaLama + thargaBaru;
                        // jika jumlah barang di tabel transaksi lebih dari stok di tabel barang
                        if (jumlahB > stokSekarang) {
                            System.out.println("Jumlah Lebih Dari Stok Yang Tersedia !");
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            Message.showWarning(this, "Jumlah Lebih Dari Stok Yang Tersedia !");
                            // jika jumlah barang di tabel transaksi kurang dari stok di tabel barang
                        } else {
                            //hitung stok
                            stokBaru = jumlahB + stokLama;
                            //update tabel data
                            modelData.setValueAt(stokBaru, baris, 5);
                            modelData.setValueAt(tharga, baris, 6);
                            //update table barang
                            sisaStok = stokSekarang - jumlahB;
                            this.objBarang[index][3] = sisaStok;
                            //ubah total harga keseluruhan
                            for (int i = 0; i < tabelData.getRowCount(); i++) {
                                total += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                            }
                            this.txtSebelum.setText(text.toMoneyCase(Integer.toString(total)));
                            //membuat waktu sekarang
                            waktuSekarang = date1.parse(waktu.getCurrentDate());
                            //menghitung diskon
                            if (daftarDiskon != null) {
                                //mengecek tanggal diskon
                                for (int i = 0; i < daftarDiskon.length; i++) {
                                    if (text.toIntCase(this.txtSebelum.getText()) >= Integer.parseInt(daftarDiskon[i][3].toString())) {
                                        //mengecek tanggal awal dan tanggal akhir
                                        if (date1.parse(daftarDiskon[i][4].toString()).compareTo(waktuSekarang) <= 0 && date1.parse(daftarDiskon[i][5].toString()).compareTo(waktuSekarang) >= 0) {
                                            if (minimalPembelian <= 0) {
                                                minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                            } else {
                                                if (Integer.parseInt(daftarDiskon[i][3].toString()) > minimalPembelian) {
                                                    minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                    jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                                }
                                            }
                                        }
                                    }
                                }
                                if (minimalPembelian >= 0) {
                                    this.txtDiskon.setText(text.toMoneyCase(Integer.toString(jumlahDiskon)));
                                    this.txtTotal.setText(text.toMoneyCase(Integer.toString(total - jumlahDiskon)));
                                    //hitung saldo
                                    saldobaru = this.Saldo + (total - jumlahDiskon);
                                    //ubah saldo
                                    txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                                } else {
                                    this.txtDiskon.setText(text.toMoneyCase(Integer.toString(0)));
                                    this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                                    //hitung saldo
                                    saldobaru = this.Saldo + total;
                                    //ubah saldo
                                    txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                                }
                            }
                            //hitung kembalian
                            this.hitungKembalian();
                            //ubah jumlah barang
                            inpJumlah.setText("1");
                            //reset
                            this.resetInput();
//                            if(isBarcode){
//                            update tabel barang
                            this.updateTabelBarang();
//                            }
                            //ubah cursor menjadi cursor default
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                        //jika id barang di tabel transaksi berbeda dengan id barang di tabel barang 
                    } else {
//                        System.out.println("data baris baru");
                        jumlahB = Integer.parseInt(inpJumlah.getText());
                        //mengecek jika jumlah barang di tabel transaksi lebih dari stok di tabel barang
                        if (jumlahB > stokSekarang) {
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            Message.showWarning(this, "Jumlah Lebih Dari Stok Yang Tersedia !");
                        } else {
                            //mengecek jika jumlah barang di tabel transaksi kurang dari sama dengan stok di tabel barang
                            //tambah data ke tabel transaksi
                            addrowtotabeldetail(new Object[]{
                                waktu.getCurrentDate(),
                                this.idTr,
                                this.idBarang,
                                this.namaBarang,
                                this.hargaJual,
                                jumlahB,
                                this.totalHarga
                            });
                            //ubah data barang di tabel barang
                            sisaStok = this.stok - jumlahB;
                            this.objBarang[index][3] = sisaStok;
                            //update total harga keseluruhan
                            for (int i = 0; i < tabelData.getRowCount(); i++) {
                                total += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                            }
//                            System.out.println(total);
                            //
                            this.txtSebelum.setText(text.toMoneyCase(Integer.toString(total)));
                            //membuat waktu sekarang
                            waktuSekarang = date1.parse(waktu.getCurrentDate());
                            //menghitung diskon
                            if (daftarDiskon != null) {
                                //mengecek tanggal diskon
                                for (int i = 0; i < daftarDiskon.length; i++) {
                                    if (text.toIntCase(this.txtSebelum.getText()) >= Integer.parseInt(daftarDiskon[i][3].toString())) {
                                        //mengecek tanggal awal dan tanggal akhir
                                        if (date1.parse(daftarDiskon[i][4].toString()).compareTo(waktuSekarang) <= 0 && date1.parse(daftarDiskon[i][5].toString()).compareTo(waktuSekarang) >= 0) {
                                            if (minimalPembelian <= 0) {
//                                                temp = i;
                                                minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                            } else {
                                                if (Integer.parseInt(daftarDiskon[i][3].toString()) > minimalPembelian) {
//                                                    temp = i;
                                                    minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                    jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                                }
                                            }
                                        }
                                    }
                                }
                                if (minimalPembelian >= 0) {
                                    this.txtDiskon.setText(text.toMoneyCase(Integer.toString(jumlahDiskon)));
                                    this.txtTotal.setText(text.toMoneyCase(Integer.toString(total - jumlahDiskon)));
                                    //ubah saldo
                                    saldobaru = this.Saldo + (total - jumlahDiskon);
                                    txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                                } else {
                                    this.txtDiskon.setText(text.toMoneyCase(Integer.toString(0)));
                                    this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                                    //ubah saldo
                                    saldobaru = this.Saldo + total;
                                    txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                                }
                            }
                            //ubah jumlah barang
                            inpJumlah.setText("1");
                            //hitung Kembalian
                            this.hitungKembalian();
                            //reset
                            this.resetInput();
//                            if(isBarcode){
                            //update tabel barang
                            this.updateTabelBarang();
//                            }
                            //ubah cursor
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                    //jika tabel transaksi kosong
                } else {
                    jumlahB = Integer.parseInt(inpJumlah.getText());
                    //mengecek jika jumlah barang di tabel transaksi lebih dari stok di tabel barang
                    if (jumlahB > this.stok) {
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        Message.showWarning(this, "Jumlah Lebih Dari Stok Yang Tersedia !");
                        //mengecek jika jumlah barang di tabel transaksi kurang dari sama dengan stok di tabel barang
                    } else {
//                        System.out.println("data kosong");
                        //tambah data ke tabel transaksi 
                        addrowtotabeldetail(new Object[]{
                            waktu.getCurrentDate(),
                            this.idTr,
                            this.idBarang,
                            this.namaBarang,
                            this.hargaJual,
                            jumlahB,
                            this.totalHarga
                        });
                        System.out.println("data pada object " + this.objBarang[index][0] + ", " + this.objBarang[index][1]);
//                        System.out.println("jumlah data pada object"+this.objBarang.length);
                        //ubah tabel barang
                        sisaStok = this.stok - jumlahB;
                        this.objBarang[index][3] = sisaStok;
                        //ubah total harga keseluruhan
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            total += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                        }
                        //
                        this.txtSebelum.setText(text.toMoneyCase(Integer.toString(total)));
                        //membuat waktu sekarang
                        waktuSekarang = date1.parse(waktu.getCurrentDate());
                        //menghitung diskon
                        if (daftarDiskon != null) {
                            //mengecek tanggal diskon
                            for (int i = 0; i < daftarDiskon.length; i++) {
                                if (text.toIntCase(this.txtSebelum.getText()) >= Integer.parseInt(daftarDiskon[i][3].toString())) {
                                    //mengecek tanggal awal dan tanggal akhir
                                    if (date1.parse(daftarDiskon[i][4].toString()).compareTo(waktuSekarang) <= 0 && date1.parse(daftarDiskon[i][5].toString()).compareTo(waktuSekarang) >= 0) {
                                        if (minimalPembelian <= 0) {
                                            minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                            this.jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                        } else {
                                            if (Integer.parseInt(daftarDiskon[i][3].toString()) > minimalPembelian) {
                                                minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                            }
                                        }
                                    }
                                }
                            }
                            if (minimalPembelian >= 0) {
                                this.txtDiskon.setText(text.toMoneyCase(Integer.toString(jumlahDiskon)));
                                this.txtTotal.setText(text.toMoneyCase(Integer.toString(total - jumlahDiskon)));
                                //ubah saldo
                                saldobaru = this.Saldo + (total - jumlahDiskon);
                                txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                            } else {
                                this.txtDiskon.setText(text.toMoneyCase(Integer.toString(0)));
                                this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                                //ubah saldo
                                saldobaru = this.Saldo + total;
                                txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                            }
                        }
                        //ubah jumlah barang
                        inpJumlah.setText("1");
                        //hitung kembalian
                        this.hitungKembalian();
                        //reset
                        this.resetInput();
//                        if(isBarcode){
                        //update tabel barang
                        this.updateTabelBarang();
//                        }
                        //ubah cursor ke default
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(TransaksiJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void editBarang() {
        /**
         * btn edit digunakan untuk mengubah data di tabel transaksi jika tabel
         * transaksi kosong maka beri pesan eror jika tabel transaksi ada data
         * maka : jika tabel transaksi tidak ada baris yang dipilih maka : beri
         * pesan error "tidak ada data yang dipilih" jika tabel transaksi ada
         * baris yang dipilih maka : jika id barang yang dipilih di tabel barang
         * sama dengan id barang yang dipilih di tabel transaksi maka : ubah
         * data barang di tabel transaksi. jika jumlah barang yang dimasukkan ke
         * tabel transaksi lebih dari stok di tabel barang maka : beri pesan
         * error "jumlah barang melebihi stok barang yang tersedia !" jika
         * jumlah barang yang dimasukkan ke tabel transaksi kurang dari sama
         * dengan stok di tabel barang maka : ubah data barang di tabel barang.
         * jika id barang yang dipilih di tabel barang berbeda dengan id barang
         * yang dipilih di tabel transaksi maka: cari indeks baris di tabel
         * transaksi dengan kondisi id barang di tabel transaksi sama dengan id
         * barang di tabel barang. jika jumlah barang yang dimasukkan ke tabel
         * transaksi lebih dari stok di tabel barang maka : beri pesan error
         * "jumlah barang melebihi stok barang yang tersedia !" jika jumlah
         * barang yang dimasukkan ke tabel transaksi kurang dari sama dengan
         * stok di tabel barang maka : ubah data barang di tabel barang. jika id
         * barang yang dipilih di tabel barang tidak ada di tabel transaksi
         * maka: beri pesan error "Data tidak ada di tabel transaksi "
         */

        //deklarasi variabel
        try {
            Date waktuSekarang;
            int minimalPembelian = 0, temp = 0;
            DefaultTableModel model = (DefaultTableModel) tabelData.getModel();
            DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
            String idBarangdata = "", idBarangtabel = "", namabarang = "";
            int sisasaldo = 0, barisdata = -1, stoktabel = 0, totalstok = 0, jumlahbarang, sisastok = 0, tharga = 0, totalKeseluruhan = 0;
            //mengecek tabel transaksi jika ada data
            if (tabelData.getRowCount() >= 1) {
                //mengecek tabel transaksi jika ada baris yang dipilih
                if (tabelData.getSelectedRow() < 0) {
                    Message.showWarning(this, "Tidak ada data yang dipilih!");
                    //jika ada baris yang dipilih
                } else {
                    //ambil value idbarang dari tabel barang yang dipilih
                    idBarangtabel = tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                    //ambil value namabarang dari tabel barang yang dipilih
                    namabarang = tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 1).toString();
                    //ambil value idbarang dari tabel transaksi yang dipilih
                    idBarangdata = tabelData.getValueAt(tabelData.getSelectedRow(), 2).toString();
                    //jika idbarang di tabelData yg dipilih sama dengan idbarang di tabelBarang yg dipilih 
                    if (idBarangtabel.equals(idBarangdata)) {
//                        System.out.println("idbarang sama");
                        //beri pesan "apakah anda yakin ingin mengubah barang ?"
                        int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah " + namabarang + " di Tabel transaksi di baris ke " + (tabelData.getSelectedRow() + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                        switch (status) {
                            //jika status pesan adalah iya 
                            case JOptionPane.YES_OPTION: {
                                //ubah cursor menjadi cursor loading
                                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                //ambil value jumlah dari jlabel jumlah
                                jumlahbarang = Integer.parseInt(inpJumlah.getText());
                                //ambil value total harga dari jlabel total harga
                                tharga = text.toIntCase(txtTotalHarga.getText());
                                //ambil value stok dari tabel barang yang dipilih
                                stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                                //hitung total stok
                                totalstok = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 5).toString()) + stoktabel;
                                //jika jumlah barang di tabel transaksi kurang dari sama dengan stok di tabel barang 
                                if (jumlahbarang <= totalstok) {
                                    //hitung sisa stok
                                    sisastok = totalstok - jumlahbarang;
                                    //update tabel barang
                                    modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
                                    //update tabel data
                                    model.setValueAt(jumlahbarang, tabelData.getSelectedRow(), 5);
                                    model.setValueAt(tharga, tabelData.getSelectedRow(), 6);
                                    //update harga keseluruhan
                                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                                        totalKeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                                    }
                                    //
                                    this.txtSebelum.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                    //membuat waktu sekarang
                                    waktuSekarang = date1.parse(waktu.getCurrentDate());
                                    //menghitung diskon
                                    if (daftarDiskon != null) {
                                        //mengecek tanggal diskon
                                        for (int i = 0; i < daftarDiskon.length; i++) {
                                            if (text.toIntCase(this.txtSebelum.getText()) >= Integer.parseInt(daftarDiskon[i][3].toString())) {
                                                //mengecek tanggal awal dan tanggal akhir
                                                if (date1.parse(daftarDiskon[i][4].toString()).compareTo(waktuSekarang) <= 0 && date1.parse(daftarDiskon[i][5].toString()).compareTo(waktuSekarang) >= 0) {
                                                    this.jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                                    if (minimalPembelian <= 0) {
//                                                        temp = i;
                                                        minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                    } else {
                                                        if (Integer.parseInt(daftarDiskon[i][3].toString()) > minimalPembelian) {
//                                                            temp = i;
                                                            minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (minimalPembelian > 0) {
                                            this.txtDiskon.setText(text.toMoneyCase(Integer.toString(jumlahDiskon)));
                                            this.txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan - jumlahDiskon)));
                                            //hitung saldo
                                            sisasaldo = this.Saldo + (totalKeseluruhan - jumlahDiskon);
                                            //ubah saldo
                                            txtSaldo.setText(text.toMoneyCase(Integer.toString(sisasaldo)));
                                        } else {
                                            this.txtDiskon.setText(text.toMoneyCase(Integer.toString(0)));
                                            this.txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                            //hitung saldo
                                            sisasaldo = this.Saldo + totalKeseluruhan;
                                            //ubah saldo
                                            txtSaldo.setText(text.toMoneyCase(Integer.toString(sisasaldo)));
                                        }
                                    }
                                    //hitung kembalian
                                    this.hitungKembalian();
                                    //ubah cursor
                                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                                } else {
                                    Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                                }
                                break;
                            }
                            //jika status pesan adalah tidak
                            case JOptionPane.NO_OPTION: {
                                System.out.println("Edit Dibatalkan");
                                break;
                            }
                        }
                        //jika idbarang di tabelData yg dipilih berbeda dengan idbarang di tabelBarang yg dipilih
                    } else {
                        //mencari idbarang di tabel transaksi yang sama dengan id barang di tabelBarang yg dipilih 
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            if (tabelData.getValueAt(i, 2).equals(idBarangtabel)) {
                                idBarangdata = tabelData.getValueAt(i, 2).toString();
                                barisdata = i;
                                break;
                            }
                        }
                        //jika idbarang di tabelBarang yang dipilih ada di tabelData maka ubah tabelData
                        if (idBarangtabel.equals(idBarangdata)) {
//                            System.out.println("idbarang berbeda");
                            //beri pesan "apakah anda yakin ingin mengubah barang ?"
                            int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah " + namabarang + " di Tabel transaksi di baris ke " + (barisdata + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                            switch (status) {
                                //jika status pesan adalah iya
                                case JOptionPane.YES_OPTION: {
                                    //ubah cursor menjadi cursor loading
                                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                    //ambil value jumlah dari jlabel jumlah
                                    jumlahbarang = Integer.parseInt(inpJumlah.getText());
                                    //ambil value total harga dari jlabel total harga
                                    tharga = text.toIntCase(txtTotalHarga.getText());
                                    //ambil value stok barang dari tabel barang yang dipilih
                                    stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                                    //ambil value jumlah barang dari tabel transaksi yang dipilih
                                    totalstok = Integer.parseInt(tabelData.getValueAt(barisdata, 5).toString()) + stoktabel;
                                    //jika jumlah barang kurang dari sama dengan stok barang di tabel barang
                                    if (jumlahbarang <= totalstok) {
                                        //hitung sisa stok
                                        sisastok = totalstok - jumlahbarang;
                                        //update tabel barang
                                        modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
                                        //update tabel data
                                        model.setValueAt(jumlahbarang, barisdata, 5);
                                        model.setValueAt(tharga, barisdata, 6);
                                        //update harga keseluruhan
                                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                                            totalKeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                                        }
                                        //
                                        this.txtSebelum.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                        //membuat waktu sekarang
                                        waktuSekarang = date1.parse(waktu.getCurrentDate());
                                        //menghitung diskon
                                        if (daftarDiskon != null) {
                                            //mengecek tanggal diskon
                                            for (int i = 0; i < daftarDiskon.length; i++) {
                                                if (text.toIntCase(this.txtSebelum.getText()) >= Integer.parseInt(daftarDiskon[i][3].toString())) {
                                                    //mengecek tanggal awal dan tanggal akhir
                                                    if (date1.parse(daftarDiskon[i][4].toString()).compareTo(waktuSekarang) <= 0 && date1.parse(daftarDiskon[i][5].toString()).compareTo(waktuSekarang) >= 0) {
                                                        this.jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                                        if (minimalPembelian <= 0) {
//                                                            temp = i;
                                                            minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                            this.jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                                        } else {
                                                            if (Integer.parseInt(daftarDiskon[i][3].toString()) > minimalPembelian) {
                                                                temp = i;
                                                                minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                                this.jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (minimalPembelian > 0) {
                                                this.txtDiskon.setText(text.toMoneyCase(Integer.toString(jumlahDiskon)));
                                                this.txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan - jumlahDiskon)));
                                                //hitung saldo
                                                sisasaldo = this.Saldo + (totalKeseluruhan - jumlahDiskon);
                                                //ubah saldo
                                                txtSaldo.setText(text.toMoneyCase(Integer.toString(sisasaldo)));
                                            } else {
                                                this.txtDiskon.setText(text.toMoneyCase(Integer.toString(0)));
                                                this.txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                                //hitung saldo
                                                sisasaldo = this.Saldo + totalKeseluruhan;
                                                //ubah saldo
                                                txtSaldo.setText(text.toMoneyCase(Integer.toString(sisasaldo)));
                                            }
                                        }
                                        //hitung kembalian 
                                        this.hitungKembalian();
                                        //ubah cursor ke default
                                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                        //jika jumlah barang lebih dari stok barang di tabel barang
                                    } else {
                                        Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                                    }
                                    break;
                                }
                                //jika status pesan adalah tidak
                                case JOptionPane.NO_OPTION: {
                                    System.out.println("Edit Dibatalkan");
                                    break;
                                }
                            }
                            //jika idbarang di tabelBarang yg dipilih tidak ada di tabelData maka beri pesan "Data tidak ada di tabel transaksi" 
                        } else {
                            //ubah cursor menjadi default
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            //beri pesan eror
                            Message.showWarning(this, "Data Tidak Ada Di Tabel Transaksi !");
                        }
                    }
                }
            } else {
                //ubah cursor menjadi default
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                //beri pesan eror
                Message.showWarning(this, "Tabel Kosong !");
            }
        } catch (ParseException ex) {
            Logger.getLogger(TransaksiJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hapusBarang() {
        /**
         * btn hapus digunakan untuk menghapus data di tabel transaksi yang
         * dipilih
         *
         */
        try {
            Date waktuSekarang;
            DefaultTableModel modelData = (DefaultTableModel) tabelData.getModel();
            DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
            boolean cocok = false;
            String idbarang = "";
            int stokdata = 0, totalharga = 0, totalSisa = 0, sisaBarang = 0, totalKeseluruhan = 0, saldoBaru = 0, totalSekarang = 0;
            int minimalPembelian = 0, temp = 0;
            if (tabelData.getSelectedRow() < 0) {
                Message.showWarning(this, "Tidak ada data yang dipilih!");
            } else {
                int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus baris " + (tabelData.getSelectedRow() + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (status) {
                    case JOptionPane.YES_OPTION: {
                        totalKeseluruhan = text.toIntCase(txtSebelum.getText());
//                        totalkeseluruhan = text.toIntCase(txtTotal.getText());
                        idbarang = tabelData.getValueAt(tabelData.getSelectedRow(), 2).toString();
                        stokdata = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 5).toString());
                        totalharga = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 6).toString());
                        totalSisa = totalKeseluruhan - totalharga;
                        for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                            cocok = tabelDataBarang.getValueAt(i, 0).toString().equals(idbarang);
                            if (tabelDataBarang.getValueAt(i, 0).toString().equals(idbarang)) {
                                sisaBarang = Integer.parseInt(tabelDataBarang.getValueAt(i, 3).toString()) + stokdata;
                                modelBarang.setValueAt(sisaBarang, i, 3);
                            }
                        }
                        //diskon
                        this.txtSebelum.setText(text.toMoneyCase(Integer.toString(totalSisa)));
                        //membuat waktu sekarang
                        waktuSekarang = date1.parse(waktu.getCurrentDate());
                        //menghitung diskon
                        if (daftarDiskon != null) {
                            //mengecek tanggal diskon
                            for (int i = 0; i < daftarDiskon.length; i++) {
                                if (text.toIntCase(this.txtSebelum.getText()) >= Integer.parseInt(daftarDiskon[i][3].toString())) {
                                    System.out.println("minimal Diskon " + daftarDiskon[i][3]);
                                    //mengecek tanggal awal dan tanggal akhir
                                    if (date1.parse(daftarDiskon[i][4].toString()).compareTo(waktuSekarang) <= 0 && date1.parse(daftarDiskon[i][5].toString()).compareTo(waktuSekarang) >= 0) {
                                        System.out.println("jumlah diskon " + daftarDiskon[i][2]);
                                        if (minimalPembelian <= 0) {
                                            minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                            this.jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                        } else {
                                            if (Integer.parseInt(daftarDiskon[i][3].toString()) > minimalPembelian) {
                                                minimalPembelian = Integer.parseInt(daftarDiskon[i][3].toString());
                                                this.jumlahDiskon = Integer.parseInt(daftarDiskon[i][2].toString());
                                            }
                                        }
                                    }
                                }
                            }
                            if (minimalPembelian > 0) {
                                this.txtDiskon.setText(text.toMoneyCase(Integer.toString(jumlahDiskon)));
                                this.txtTotal.setText(text.toMoneyCase(Integer.toString(totalSisa - jumlahDiskon)));
                                //ganti saldo
                                saldoBaru = this.Saldo + (totalSisa - jumlahDiskon);
                                this.txtSaldo.setText(text.toMoneyCase(Integer.toString(saldoBaru)));
                            } else {
                                this.txtDiskon.setText(text.toMoneyCase(Integer.toString(0)));
                                this.txtTotal.setText(text.toMoneyCase(Integer.toString(totalSisa)));
                                //ganti saldo
                                saldoBaru = this.Saldo - totalSisa;
                                this.txtSaldo.setText(text.toMoneyCase(Integer.toString(saldoBaru)));
                            }
                        }
                        //hitung kembalian
                        this.hitungKembalian();
                        // mereset input
                        this.resetInput();
                        if (tabelData.getValueAt(tabelData.getSelectedRow(), 2).equals(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0))) {
                            this.tabelDataBarang.setRowSelectionInterval(this.tabelDataBarang.getRowCount() - 1, this.tabelDataBarang.getRowCount() - 1);
                        }
                        modelData.removeRow(tabelData.getSelectedRow());
                        break;
                    }
                    case JOptionPane.NO_OPTION: {
                        System.out.println("Hapus Dibatalkan");
                        Message.showInformation(this, "Hapus Dibatalkan!");
                        break;
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(TransaksiJual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void hitungKembalian(){
        if (!inpBayar.getText().isEmpty()) {
            if (text.isNumber(inpBayar.getText())) {
                int jumlahbayar = Integer.parseInt(inpBayar.getText());
                int jumlahtotal = text.toIntCase(txtTotal.getText());
                if (jumlahbayar < jumlahtotal) {
                    txtKembalian.setText(text.toMoneyCase(Integer.toString(0)));
//                        Message.showWarning(this, "Uang anda kurang !");
                } else {
                    txtKembalian.setText(text.toMoneyCase(Integer.toString(jumlahbayar - jumlahtotal)));
                    this.lastBayar = Integer.parseInt(inpBayar.getText());
                }
            } else {
                inpBayar.setText(Integer.toString(this.lastBayar));
                Message.showWarning(this, "Jumlah bayar Harus Angka!");
            }
        } else {
//                System.out.println("Jumlah Bayar tidak boleh kosong !");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSaldo = new javax.swing.JLabel();
        inpID = new javax.swing.JLabel();
        inpNamaPetugas = new javax.swing.JLabel();
        inpIDBarang = new javax.swing.JLabel();
        inpNamaBarang = new javax.swing.JLabel();
        inpHarga = new javax.swing.JLabel();
        txtTotalHarga = new javax.swing.JLabel();
        inpTanggal = new javax.swing.JLabel();
        txtSebelum = new javax.swing.JLabel();
        txtDiskon = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        txtKembalian = new javax.swing.JLabel();
        inpJumlah = new javax.swing.JTextField();
        inpBayar = new javax.swing.JTextField();
        inpCariBarang = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelDataBarang = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBayar = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1158, 728));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtSaldo.setBackground(new java.awt.Color(222, 222, 222));
        txtSaldo.setForeground(new java.awt.Color(255, 255, 255));
        txtSaldo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtSaldo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSaldoMouseClicked(evt);
            }
        });
        add(txtSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 260, 34));

        inpID.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpID.setForeground(new java.awt.Color(0, 0, 0));
        inpID.setText(":");
        add(inpID, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 64, 280, 26));

        inpNamaPetugas.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNamaPetugas.setForeground(new java.awt.Color(0, 0, 0));
        inpNamaPetugas.setText(":");
        add(inpNamaPetugas, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 109, 280, 26));

        inpIDBarang.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpIDBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpIDBarang.setText(": ");
        add(inpIDBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 152, 280, 26));

        inpNamaBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNamaBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpNamaBarang.setText(":");
        add(inpNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 196, 280, 26));

        inpHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpHarga.setForeground(new java.awt.Color(0, 0, 0));
        inpHarga.setText(":");
        add(inpHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 240, 285, 26));

        txtTotalHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        txtTotalHarga.setForeground(new java.awt.Color(0, 0, 0));
        txtTotalHarga.setText(":");
        add(txtTotalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 329, 285, 26));

        inpTanggal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        inpTanggal.setForeground(new java.awt.Color(0, 0, 0));
        inpTanggal.setText(": 15 Oktober 2022 | 17:55");
        add(inpTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 373, 285, 26));

        txtSebelum.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        add(txtSebelum, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 310, 355, 26));

        txtDiskon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        add(txtDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 345, 200, 26));

        txtTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(965, 345, 155, 26));

        txtKembalian.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        add(txtKembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(965, 382, 155, 26));

        inpJumlah.setBackground(new java.awt.Color(255, 255, 255));
        inpJumlah.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpJumlah.setForeground(new java.awt.Color(0, 0, 0));
        inpJumlah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpJumlah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                inpJumlahMouseEntered(evt);
            }
        });
        inpJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpJumlahActionPerformed(evt);
            }
        });
        inpJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inpJumlahKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpJumlahKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpJumlahKeyTyped(evt);
            }
        });
        add(inpJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 285, 50, 27));

        inpBayar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inpBayar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpBayar.setOpaque(false);
        inpBayar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inpBayarMouseClicked(evt);
            }
        });
        inpBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpBayarActionPerformed(evt);
            }
        });
        inpBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpBayarKeyReleased(evt);
            }
        });
        add(inpBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 382, 200, 26));

        inpCariBarang.setBackground(new java.awt.Color(255, 255, 255));
        inpCariBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpCariBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpCariBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpCariBarangActionPerformed(evt);
            }
        });
        inpCariBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariBarangKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariBarangKeyTyped(evt);
            }
        });
        add(inpCariBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 55, 370, 35));
        inpCariBarang.getAccessibleContext().setAccessibleDescription("");

        tabelDataBarang.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataBarang.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataBarang.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Barang", "Nama Barang", "Jenis", "Stok", "Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelDataBarang.setGridColor(new java.awt.Color(0, 0, 0));
        tabelDataBarang.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelDataBarang.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelDataBarang.getTableHeader().setReorderingAllowed(false);
        tabelDataBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataBarangMouseClicked(evt);
            }
        });
        tabelDataBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataBarangKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tabelDataBarang);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 90, 600, 200));

        tabelData.setBackground(new java.awt.Color(255, 255, 255));
        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "ID Log", "ID Barang", "Nama Barang", "Harga", "Jumlah Barang", "Total Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
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
        jScrollPane3.setViewportView(tabelData);

        add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 480, 1100, 200));

        btnTambah.setBackground(new java.awt.Color(34, 119, 237));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambah.png"))); // NOI18N
        btnTambah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnTambah.setOpaque(false);
        btnTambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambahMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambahMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambahMouseExited(evt);
            }
        });
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        add(btnTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(37, 424, -1, -1));

        btnEdit.setBackground(new java.awt.Color(34, 119, 237));
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-edit.png"))); // NOI18N
        btnEdit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnEdit.setOpaque(false);
        btnEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEditMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnEditMouseExited(evt);
            }
        });
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 424, -1, -1));

        btnHapus.setBackground(new java.awt.Color(34, 119, 237));
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-hapus.png"))); // NOI18N
        btnHapus.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnHapus.setOpaque(false);
        btnHapus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapusMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapusMouseExited(evt);
            }
        });
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 424, -1, -1));

        btnBayar.setBackground(new java.awt.Color(34, 119, 237));
        btnBayar.setForeground(new java.awt.Color(255, 255, 255));
        btnBayar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-bayar.png"))); // NOI18N
        btnBayar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBayar.setOpaque(false);
        btnBayar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBayarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBayarMouseExited(evt);
            }
        });
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });
        add(btnBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 423, -1, -1));

        btnBatal.setBackground(new java.awt.Color(220, 41, 41));
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-batal.png"))); // NOI18N
        btnBatal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBatal.setOpaque(false);
        btnBatal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBatalMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBatalMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBatalMouseExited(evt);
            }
        });
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 422, -1, -1));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-transaksi-jual.png"))); // NOI18N
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void tabelDataBarangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataBarangMouseClicked
        if (!inpJumlah.getText().isEmpty()) {
            if (text.isNumber(inpJumlah.getText())) {
                if (Integer.parseInt(inpJumlah.getText()) > 0) {
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                    this.showDataBarang();
                    this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                    txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } else {
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                    this.showDataBarang();
                    this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                    txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }//GEN-LAST:event_tabelDataBarangMouseClicked

    private void tabelDataBarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataBarangKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (this.tabelDataBarang.getSelectedRow() >= 1) {
                if (!inpJumlah.getText().isEmpty()) {
                    if (text.isNumber(inpJumlah.getText())) {
                        if (Integer.parseInt(inpJumlah.getText()) > 0) {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() - 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        } else {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() - 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                }
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (this.tabelDataBarang.getSelectedRow() < (this.tabelDataBarang.getRowCount() - 1)) {
                if (!inpJumlah.getText().isEmpty()) {
                    if (text.isNumber(inpJumlah.getText())) {
                        if (Integer.parseInt(inpJumlah.getText()) > 0) {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() + 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        } else {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() + 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_tabelDataBarangKeyPressed

    private void inpCariBarangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariBarangKeyTyped
        String key = this.inpCariBarang.getText();
        this.keywordBarang = "WHERE id_barang LIKE '%" + key + "%'";
        this.updateTabelBarangDb();
    }//GEN-LAST:event_inpCariBarangKeyTyped

    private void inpCariBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariBarangKeyReleased
        String key = this.inpCariBarang.getText();
        this.keywordBarang = "WHERE id_barang LIKE '%" + key + "%'";
        this.updateTabelBarangDb();
    }//GEN-LAST:event_inpCariBarangKeyReleased

    private void inpCariBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpCariBarangActionPerformed

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.idSelectedBarang = this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 2).toString();
        for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
            if (tabelDataBarang.getValueAt(i, 0).equals(this.idSelectedBarang)) {
                this.tabelDataBarang.setRowSelectionInterval(i, i);
                this.showBarang();
                //lalu hentikan for loop
                break;
            }
        }
        //mengganti cursor
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataMouseClicked

    private void tabelDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyPressed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (this.tabelData.getSelectedRow() >= 1) {
                this.idSelectedBarang = this.tabelData.getValueAt(this.tabelData.getSelectedRow() - 1, 2).toString();
                for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                    if (tabelDataBarang.getValueAt(i, 0).equals(this.idSelectedBarang)) {
                        this.tabelDataBarang.setRowSelectionInterval(i, i);
                        this.showBarang();
                        //lalu hentikan for loop
                        break;
                    }
                }
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (this.tabelData.getSelectedRow() < (this.tabelData.getRowCount() - 1)) {
                this.idSelectedBarang = this.tabelData.getValueAt(this.tabelData.getSelectedRow() + 1, 2).toString();
                for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                    if (tabelDataBarang.getValueAt(i, 0).equals(this.idSelectedBarang)) {
                        this.tabelDataBarang.setRowSelectionInterval(i, i);
                        this.showBarang();
                        //lalu hentikan for loop
                        break;
                    }
                }
            }
        }
        //mengganti cursor
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataKeyPressed

    private void btnTambahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseClicked
        tambahBarang();
    }//GEN-LAST:event_btnTambahMouseClicked

    private void btnTambahMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseEntered
        this.btnTambah.setIcon(Gambar.getNoAktiveIcon(this.btnTambah.getIcon().toString()));
    }//GEN-LAST:event_btnTambahMouseEntered

    private void btnTambahMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseExited
        this.btnTambah.setIcon(Gambar.getNoBiasaIcon(this.btnTambah.getIcon().toString()));
    }//GEN-LAST:event_btnTambahMouseExited

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseClicked
        editBarang();
    }//GEN-LAST:event_btnEditMouseClicked

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        //jika cursor masuk ke btn edit maka ubah btn edit 
        this.btnEdit.setIcon(Gambar.getNoAktiveIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        //jika cursor keluar dari btn edit maka ubah btn edit 
        this.btnEdit.setIcon(Gambar.getNoBiasaIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseExited

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseClicked
        hapusBarang();
    }//GEN-LAST:event_btnHapusMouseClicked

    private void btnHapusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseEntered
        //jika cursor masuk ke btn hapus maka ubah btn hapus 
        this.btnHapus.setIcon(Gambar.getNoAktiveIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseEntered

    private void btnHapusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseExited
        //jika cursor keluar dari btn hapus maka ubah btn hapus 
        this.btnHapus.setIcon(Gambar.getNoBiasaIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseExited

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBayarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBayarMouseEntered
        //jika cursor masuk dari btn bayar maka ubah btn bayar
        this.btnBayar.setIcon(Gambar.getNoAktiveIcon(this.btnBayar.getIcon().toString()));
    }//GEN-LAST:event_btnBayarMouseEntered

    private void btnBayarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBayarMouseExited
        //jika cursor keluar dari btn bayar maka ubah btn bayar
        this.btnBayar.setIcon(Gambar.getNoBiasaIcon(this.btnBayar.getIcon().toString()));
    }//GEN-LAST:event_btnBayarMouseExited

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        // membuka window konfirmasi pembayaran
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        System.out.println("jumlah dalam tabel data " + tabelData.getRowCount());
        PreparedStatement pst;
        String sql1 = "", sql2 = "", sql3 = "", idbarang, namabarang, hbarang, jbarang;
        boolean error = false;
        int keuntungan = 0, hargajual = 0, jumlah = 0, totalh = 0, totalBarang = 0, totalharga, bayar;
        try {
            totalharga = text.toIntCase(txtTotal.getText());
            if (inpBayar.getText() == "") {
                error = true;
                Message.showWarning(this, "Field bayar tidak boleh kosong !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else if (!text.isNumber(this.inpBayar.getText())) {
                error = true;
                Message.showWarning(this, "Field bayar harus angka !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else if (Integer.parseInt(inpBayar.getText()) <= 0) {
                error = true;
                Message.showWarning(this, "Uang anda kurang !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else if (Integer.parseInt(inpBayar.getText()) < totalharga) {
                error = true;
                Message.showWarning(this, "Uang anda kurang !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else if (tabelData.getRowCount() <= 0) {
                error = true;
                Message.showWarning(this, "Tabel Data Transaksi Tidak Boleh Kosong !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            if (!error) {
                db.startConnection();
                int status;
                Audio.play(Audio.SOUND_INFO);
                status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin melakukan pembayaran ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (status) {
                    case JOptionPane.YES_OPTION: {
                        System.out.println("transaksi sedang dibuat");
                        //hitung totalBarang
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            totalBarang += Integer.parseInt(tabelData.getValueAt(i, 5).toString());
                        }
                        //hitung keuntungan
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            idbarang = tabelData.getValueAt(i, 2).toString();
                            jumlah = Integer.parseInt(tabelData.getValueAt(i, 5).toString());
                            //cari idbarang di objbarang berdasarkan idbarang di tabelData
                            for (int j = 0; j < objBarang.length; j++) {
                                if (objBarang[j][0].equals(idbarang)) {
                                    hargajual = text.toIntCase(objBarang[j][4].toString());
                                    totalh = (Integer.parseInt(tabelData.getValueAt(i, 4).toString()) - hargajual) * jumlah;
                                    keuntungan += totalh;
                                    break;
                                }
                            }
                        }
                        sql1 = "INSERT INTO transaksi_jual(`id_tr_jual`, `id_karyawan`, `nama_karyawan`, `total_hrg`, `keuntungan`, `tanggal`) VALUES (?, ?, ?, ?, ?, ?)";
                        pst = db.conn.prepareStatement(sql1);
                        pst.setString(1, this.idTr);
                        pst.setString(2, idKaryawan);
                        pst.setString(3, this.user.getNamaKaryawan(idKaryawan));
                        pst.setInt(4, text.toIntCase(txtTotal.getText()));
                        pst.setInt(5, keuntungan);
                        pst.setString(6, waktu.getCurrentDateTime());
                        if (pst.executeUpdate() > 0) {
                            System.out.println("Sudah membuat Transaksi jual");
                        }
                        //id_tr_beli,id_supplier,nama_supplier,id_barang,_nama_barang,jenis_barang,harga,jumlah,total_harg
                        sql2 = "INSERT INTO detail_transaksi_jual VALUES (?, ?, ?, ?, ?, ?, ?)";
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            pst = db.conn.prepareStatement(sql2);
                            idbarang = tabelData.getValueAt(i, 2).toString();
                            pst.setString(1, this.idTr);
                            pst.setString(2, idbarang);
                            pst.setString(3, tabelData.getValueAt(i, 3).toString());
                            pst.setString(4, barang.getJenis(idbarang));
                            pst.setInt(5, Integer.parseInt(tabelData.getValueAt(i, 4).toString()));
                            pst.setInt(6, Integer.parseInt(tabelData.getValueAt(i, 5).toString()));
                            pst.setInt(7, Integer.parseInt(tabelData.getValueAt(i, 6).toString()));
                            if (pst.executeUpdate() > 0) {
                                System.out.println("Sudah membuat Detail Transaksi Jual ke " + i);
                            }
                        }
                        //insert data 
                        sql3 = "INSERT INTO saldo VALUES (?, ?, ?, ?, ?)";
                        pst = db.conn.prepareStatement(sql3);
                        pst.setString(1, this.saldoCreateID());
                        pst.setInt(2, text.toIntCase(this.txtSaldo.getText()));
                        pst.setString(3, "tambah transaksi jual");
                        pst.setString(4, null);
                        pst.setString(5, this.idTr);
                        if (pst.executeUpdate() > 0) {
                            System.out.println("Sudah membuat Saldo Baru");
                        }
                        Message.showInformation(this, "Transaksi berhasil!");
                        //print struk penjualan 
                        Map parameters = new HashMap();
                        parameters.put("tanggal", waktu.getTanggalNow());
                        parameters.put("id_tr_jual", this.idTr);
                        parameters.put("totalBarang", totalBarang);
                        parameters.put("totalSebelum", txtSebelum.getText());
                        parameters.put("totalHarga", txtTotal.getText());
                        parameters.put("bayar", text.toMoneyCase(inpBayar.getText()));
                        parameters.put("diskon", txtDiskon.getText());
                        parameters.put("kembalian", txtKembalian.getText());
                        this.cetakNota(parameters);
                        // mereset tabel
                        this.getDataBarang();
                        this.updateTabelBarang();
                        this.updateTabelData();
                        // mereset input
                        this.resetInput();
                        txtSebelum.setText(text.toMoneyCase("0"));
                        txtDiskon.setText(text.toMoneyCase("0"));
                        txtTotal.setText(text.toMoneyCase("0"));
                        //update id transaksi
                        this.idTr = this.trj.createIDTransaksi();
                        this.inpID.setText("<html><p>:&nbsp;" + this.idTr + "</p></html>");
                        //update saldo
                        this.updateSaldo();
                        this.txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo)));
                        this.tabelDataBarang.setRowSelectionInterval(this.tabelDataBarang.getRowCount() - 1, this.tabelDataBarang.getRowCount() - 1);
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        break;
                    }
                    case JOptionPane.NO_OPTION: {
                        System.out.println("Transaksi Dibatalkan");
                        Message.showInformation(this, "Transaksi Dibatalkan!");
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        break;
                    }
                }
            }
        } catch (SQLException | InValidUserDataException ex) {
            ex.printStackTrace();
            System.out.println("Error Message : " + ex.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("data tidak ada ");
        }
    }//GEN-LAST:event_btnBayarActionPerformed

    private void btnBatalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalMouseClicked

    private void btnBatalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseEntered
        //jika cursor masuk dari btn batal maka ubah btn batal 
        this.btnBatal.setIcon(Gambar.getNoAktiveIcon(this.btnBatal.getIcon().toString()));
    }//GEN-LAST:event_btnBatalMouseEntered

    private void btnBatalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseExited
        //jika cursor keluar dari btn batal maka ubah btn batal 
        this.btnBatal.setIcon(Gambar.getNoBiasaIcon(this.btnBatal.getIcon().toString()));
    }//GEN-LAST:event_btnBatalMouseExited

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        int status;
        Audio.play(Audio.SOUND_INFO);
        status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin membatalkan transaksi?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
        System.out.println("status option" + status);
        switch (status) {
            case JOptionPane.YES_OPTION: {
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                // mereset tabel
                this.getDataBarang();
                this.updateTabelBarang();
                this.updateTabelData();
                // mereset input
                this.resetInput();
                txtSebelum.setText(text.toMoneyCase("0"));
                txtDiskon.setText(text.toMoneyCase("0"));
                txtTotal.setText(text.toMoneyCase("0"));
                //ubah saldo 
                this.txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo)));
                this.tabelDataBarang.setRowSelectionInterval(this.tabelDataBarang.getRowCount() - 1, this.tabelDataBarang.getRowCount() - 1);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
            }
        }
    }//GEN-LAST:event_btnBatalActionPerformed

    private void inpJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpJumlahActionPerformed
        try {
            if (!inpJumlah.getText().isEmpty()) {
                if (text.isNumber(inpJumlah.getText())) {
                    int jumlahbarang = Integer.parseInt(inpJumlah.getText());
                    if (jumlahbarang <= 0) {
                        Message.showWarning(this, "Jumlah Barang Harus lebih dari 0 !");
                    } else {
                        this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                        this.showDataBarang();
                        this.totalHarga = Integer.parseInt(inpJumlah.getText()) * this.hargaJual;
                        txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    }
                } else {
                    Message.showWarning(this, "Jumlah Barang Harus Angka !");
                }
            } else {
//                System.out.println("Jumlah Barang tidak boleh kosong !");
            }
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_inpJumlahActionPerformed

    private void inpJumlahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyTyped

    }//GEN-LAST:event_inpJumlahKeyTyped

    private void inpJumlahMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpJumlahMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_inpJumlahMouseEntered

    private void inpJumlahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyPressed

    }//GEN-LAST:event_inpJumlahKeyPressed

    private void inpJumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyReleased
        try {
            if (!inpJumlah.getText().isEmpty()) {
                if (text.isNumber(inpJumlah.getText())) {
                    int jumlahbarang = Integer.parseInt(inpJumlah.getText());
                    if (jumlahbarang <= 0) {
                        Message.showWarning(this, "Jumlah Barang Harus lebih dari 0 !");
                    } else {
                        this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                        this.showDataBarang();
                        this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                        txtTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    }
                } else {
//                    System.out.println("harus angka");
                    Message.showWarning(this, "Jumlah Barang Harus Angka!");
                }
            } else {
//                System.out.println("Jumlah Barang tidak boleh kosong !");
            }
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_inpJumlahKeyReleased

    private void txtSaldoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSaldoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaldoMouseClicked

    private void inpBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpBayarActionPerformed

    private void inpBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpBayarKeyReleased
        this.hitungKembalian();
    }//GEN-LAST:event_inpBayarKeyReleased

    private void inpBayarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpBayarMouseClicked
        // TODO add your handling code here:
//        int jumlahbayar = text.toIntCa/se(idTr);
    }//GEN-LAST:event_inpBayarMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JTextField inpBayar;
    private javax.swing.JTextField inpCariBarang;
    private javax.swing.JLabel inpHarga;
    private javax.swing.JLabel inpID;
    private javax.swing.JLabel inpIDBarang;
    private javax.swing.JTextField inpJumlah;
    private javax.swing.JLabel inpNamaBarang;
    private javax.swing.JLabel inpNamaPetugas;
    private javax.swing.JLabel inpTanggal;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tabelData;
    private javax.swing.JTable tabelDataBarang;
    private javax.swing.JLabel txtDiskon;
    private javax.swing.JLabel txtKembalian;
    private javax.swing.JLabel txtSaldo;
    private javax.swing.JLabel txtSebelum;
    private javax.swing.JLabel txtTotal;
    private javax.swing.JLabel txtTotalHarga;
    // End of variables declaration//GEN-END:variables

    private void startTimer() {
        if (timer.isRunning()) {
            timer.restart();
        } else {
            timer.start();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.startTimer();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.startTimer();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cari = this.inpCariBarang.getText();
        if (cari.length() >= 6 && cari.length() <= 10) {
            System.out.println("mencari barcode");
            this.cariBarcode(cari);
        }
    }
}

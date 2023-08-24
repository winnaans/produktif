package com.window.panels;

import com.data.db.Database;
import com.error.InValidUserDataException;
import com.manage.Barang;
import com.manage.Message;
import com.manage.Text;
import com.manage.Waktu;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.users.Supplier;
import com.users.Users;
import java.awt.Cursor;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Amirzan Fikri P
 */
public class TransaksiBeli extends javax.swing.JPanel {
    private final Database db = new Database();
    private final Users user = new Users();
    private final Supplier supplier = new Supplier();
    private final Barang barang = new Barang();
    private final Waktu waktu = new Waktu();
    private final com.manage.ManageTransaksiBeli trb = new com.manage.ManageTransaksiBeli();
    private final Text text = new Text();
    private String keywordSupplier = "", keywordBarang = "", keywordSaldo = "", idSelectedSupplier, idSelectedBarang;
    private String idTr, namaTr, namaSupplier, namaBarang, idKaryawan, idSupplier, idBarang, metodeBayar, tglNow;
    private int jumlah = 0, hargaBeli, totalHarga = 0, stok = 0, keseluruhan = 0, Saldo = 0;
    public TransaksiBeli() {
        initComponents();
        db.startConnection();
        this.updateSaldo();
        this.idTr = this.trb.createIDTransaksi();
        this.idKaryawan = this.user.getIdKaryawan(this.user.getCurrentLogin());
        this.txtTotal.setText(text.toMoneyCase("0"));
        this.inpJumlah.setText("0");
        this.inpTotalHarga.setText(text.toMoneyCase("0"));
        this.txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo)));
        this.inpID.setText("<html><p>:&nbsp;" + this.trb.createIDTransaksi() + "</p></html>");
        this.inpNamaKaryawan.setText("<html><p>:&nbsp;" + this.user.getCurrentLoginName() + "</p></html>");

        this.btnSimpan.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnEdit.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnHapus.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnBayar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnBatal.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        this.tabelDataBarang.setRowHeight(29);
        this.tabelDataBarang.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataBarang.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.tabelDataSupplier.setRowHeight(29);
        this.tabelDataSupplier.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataSupplier.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.updateTabelSupplier();
        this.updateTabelBarang();

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
        supplier.closeConnection();
        barang.closeConnection();
        trb.closeConnection();
    }

    private int getTotal(String table, String kolom, String kondisi) {
        try {
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
        }
        return -1;
    }

    private Object[][] getDataSupplier() {
        try {
            db.startConnection();
            Object[][] obj;
            int rows = 0;
            String sql = "SELECT id_supplier, nama_supplier, no_telp, alamat FROM supplier " + keywordSupplier;
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            obj = new Object[this.getJumlahData("supplier", keywordSupplier)][4];
            // mengeksekusi query
            db.res = db.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (db.res.next()) {
                // menyimpan data dari tabel ke object
                obj[rows][0] = db.res.getString("id_supplier");
                obj[rows][1] = db.res.getString("nama_supplier");
                obj[rows][2] = db.res.getString("no_telp");
                obj[rows][3] = db.res.getString("alamat");
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return obj;
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private void addrowtotabeldetail(Object[] dataRow) {
        DefaultTableModel model = (DefaultTableModel) this.tabelData.getModel();
        model.addRow(dataRow);
    }

    private void updateTabelSupplier() {
        this.tabelDataSupplier.setModel(new javax.swing.table.DefaultTableModel(
                getDataSupplier(),
                new String[]{
                    "ID Supplier", "Nama Supplier", "No Telephone", "Alamat"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    private Object[][] getDataBarang() {
        try {
            Object obj[][];
            int rows = 0;
            String sql = "SELECT id_barang, nama_barang, jenis_barang, stok, harga_beli FROM barang " + keywordBarang;
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
                obj[rows][4] = text.toMoneyCase(barang.res.getString("harga_beli"));
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return obj;
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private void updateTabelBarang() {
        this.tabelDataBarang.setModel(new javax.swing.table.DefaultTableModel(
                getDataBarang(),
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

    private boolean isSelectedSupplier() {
        return this.tabelDataSupplier.getSelectedRow() > - 1;
    }

    private boolean isSelectedBarang() {
        return this.tabelDataBarang.getSelectedRow() > - 1;
    }

    private void showDataSupplier() {
        // cek akapah ada data supplier yg dipilih
        if (this.isSelectedSupplier()) {
            // mendapatkan data supplier
            this.idSupplier = this.idSelectedSupplier;
            this.namaSupplier = this.supplier.getNama(this.idSupplier);
            // menampilkan data supplier
            this.inpNamaSupplier.setText("<html><p>:&nbsp;" + this.namaSupplier + "</p></html>");
        }
    }

    private void showSupplier() {
        // mendapatkan data supplier
        this.idSupplier = this.idSelectedSupplier;
        this.namaSupplier = this.supplier.getNama(this.idSupplier);
        // menampilkan data supplier
        this.inpNamaSupplier.setText("<html><p>:&nbsp;" + this.namaSupplier + "</p></html>");
    }

    private void showDataBarang() {

        // cek apakah ada data barang yang dipilih
        if (this.isSelectedBarang()) {
            // mendapatkan data barang
            this.idBarang = this.idSelectedBarang;
            this.namaBarang = text.toCapitalize(this.barang.getNamaBarang(this.idBarang));
            this.stok = Integer.parseInt(this.barang.getStok(this.idBarang));
            this.hargaBeli = Integer.parseInt(this.barang.getHargaBeli(this.idBarang));

            // menampilkan data barang
            this.inpIDBarang.setText("<html><p>:&nbsp;" + this.idBarang + "</p></html>");
            this.inpNamaBarang.setText("<html><p>:&nbsp;" + this.namaBarang + "</p></html>");
            this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase(Integer.toString(this.hargaBeli)) + "</p></html>");
        }
    }

    private void showBarang() {
        // mendapatkan data barang
        this.idBarang = this.idSelectedBarang;
        this.namaBarang = text.toCapitalize(this.barang.getNamaBarang(this.idBarang));
        this.stok = Integer.parseInt(this.barang.getStok(this.idBarang));
        this.hargaBeli = Integer.parseInt(this.barang.getHargaBeli(this.idBarang));

        // menampilkan data barang
        this.inpIDBarang.setText("<html><p>:&nbsp;" + this.idBarang + "</p></html>");
        this.inpNamaBarang.setText("<html><p>:&nbsp;" + this.namaBarang + "</p></html>");
        this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase(Integer.toString(this.hargaBeli)) + "</p></html>");
    }

    private void updateTabelData() {
        DefaultTableModel model = (DefaultTableModel) tabelData.getModel();
        while (tabelData.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    private void resetInput() {
        this.inpIDBarang.setText("<html><p>:&nbsp;</p></html>");
        this.inpNamaSupplier.setText("<html><p>:&nbsp;</p></html>");
        this.inpNamaBarang.setText("<html><p>:&nbsp;</p></html>");
        this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase("0") + "</p></html>");
        this.inpJumlah.setText("0");
        this.inpTotalHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase("0") + "</p></html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSaldo = new javax.swing.JLabel();
        inpCariBarang = new javax.swing.JTextField();
        inpCariSupplier = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBayar = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        txtTotal = new javax.swing.JLabel();
        inpID = new javax.swing.JLabel();
        inpNamaKaryawan = new javax.swing.JLabel();
        inpIDBarang = new javax.swing.JLabel();
        inpNamaSupplier = new javax.swing.JLabel();
        inpNamaBarang = new javax.swing.JLabel();
        inpJumlah = new javax.swing.JTextField();
        inpTotalHarga = new javax.swing.JLabel();
        inpHarga = new javax.swing.JLabel();
        inpTanggal = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelDataBarang = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelDataSupplier = new javax.swing.JTable();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(957, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtSaldo.setBackground(new java.awt.Color(222, 222, 222));
        txtSaldo.setForeground(new java.awt.Color(255, 255, 255));
        add(txtSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 10, 300, 33));

        inpCariBarang.setBackground(new java.awt.Color(255, 255, 255));
        inpCariBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpCariBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpCariBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariBarangKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariBarangKeyTyped(evt);
            }
        });
        add(inpCariBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(875, 60, 250, 30));

        inpCariSupplier.setBackground(new java.awt.Color(255, 255, 255));
        inpCariSupplier.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpCariSupplier.setForeground(new java.awt.Color(0, 0, 0));
        inpCariSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpCariSupplierActionPerformed(evt);
            }
        });
        inpCariSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariSupplierKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariSupplierKeyTyped(evt);
            }
        });
        add(inpCariSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(875, 240, 250, 30));

        btnSimpan.setBackground(new java.awt.Color(34, 119, 237));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambah.png"))); // NOI18N
        btnSimpan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSimpan.setOpaque(false);
        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSimpanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSimpanMouseExited(evt);
            }
        });
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(37, 424, -1, -1));

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
        add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 423, -1, -1));
        add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 653, 200, 26));

        inpID.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpID.setForeground(new java.awt.Color(0, 0, 0));
        inpID.setText(": ");
        add(inpID, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 64, 285, 27));

        inpNamaKaryawan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNamaKaryawan.setForeground(new java.awt.Color(0, 0, 0));
        inpNamaKaryawan.setText(": ");
        add(inpNamaKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 104, 285, 26));

        inpIDBarang.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpIDBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpIDBarang.setText(": ");
        add(inpIDBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 182, 285, 26));

        inpNamaSupplier.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        inpNamaSupplier.setForeground(new java.awt.Color(0, 0, 0));
        inpNamaSupplier.setText(":");
        add(inpNamaSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 142, 285, 27));

        inpNamaBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNamaBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpNamaBarang.setText(":");
        add(inpNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 220, 285, 26));

        inpJumlah.setBackground(new java.awt.Color(255, 255, 255));
        inpJumlah.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpJumlah.setForeground(new java.awt.Color(0, 0, 0));
        inpJumlah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
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
        add(inpJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 298, 50, 27));

        inpTotalHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpTotalHarga.setForeground(new java.awt.Color(0, 0, 0));
        inpTotalHarga.setText(":");
        add(inpTotalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 337, 285, 27));

        inpHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpHarga.setForeground(new java.awt.Color(0, 0, 0));
        inpHarga.setText(":");
        add(inpHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 260, 285, 26));

        inpTanggal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        inpTanggal.setForeground(new java.awt.Color(0, 0, 0));
        inpTanggal.setText(": 15 Oktober 2022 | 17:55");
        add(inpTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 377, 285, 26));

        tabelDataBarang.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataBarang.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataBarang.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Barang", "Nama Barang", "Jenis", "Stok", "Harga Jual"
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

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 90, 605, 135));

        tabelData.setBackground(new java.awt.Color(255, 255, 255));
        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "ID Log", "ID Supplier", "Nama Supplier", "ID Barang", "Nama Barang", "Harga", "Jumlah Barang", "Total Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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

        add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 475, 1090, 175));

        tabelDataSupplier.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataSupplier.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataSupplier.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Supplier", "Nama Supplier", "No Telp", "Alamat"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelDataSupplier.setGridColor(new java.awt.Color(0, 0, 0));
        tabelDataSupplier.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelDataSupplier.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelDataSupplier.getTableHeader().setReorderingAllowed(false);
        tabelDataSupplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataSupplierSupplier1MouseClicked(evt);
            }
        });
        tabelDataSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataSupplierSupplier1KeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(tabelDataSupplier);

        add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 270, 605, 135));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-transaksi-beli.png"))); // NOI18N
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        int status;
        Audio.play(Audio.SOUND_INFO);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin membatalkan transaksi?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
        System.out.println("status option" + status);
        switch (status) {
            case JOptionPane.YES_OPTION: {
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                // mereset tabel
                this.updateTabelSupplier();
                this.updateTabelBarang();
                this.updateTabelData();
                // mereset input
                this.resetInput();
                txtTotal.setText(text.toMoneyCase("0"));
                this.tabelDataBarang.setRowSelectionInterval(this.tabelDataBarang.getRowCount() - 1, this.tabelDataBarang.getRowCount() - 1);
                this.tabelDataSupplier.setRowSelectionInterval(this.tabelDataSupplier.getRowCount() - 1, this.tabelDataSupplier.getRowCount() - 1);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                //ubah saldo
                this.txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo)));
                break;
            }
        }
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // membuka window konfirmasi pembayaran
        PreparedStatement pst;
        String idbarang;
        try {
            if (tabelData.getRowCount() > 0) {
                db.startConnection();
                int status;
                Audio.play(Audio.SOUND_INFO);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin melakukan pembayaran ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (status) {
                    case JOptionPane.YES_OPTION: {
                        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        pst = db.conn.prepareStatement("INSERT INTO transaksi_beli VALUES (?, ?, ?, ?, ?)");
                        pst.setString(1, idTr);
                        pst.setString(2, idKaryawan);
                        pst.setString(3, this.user.getNamaKaryawan(idKaryawan));
                        pst.setInt(4, text.toIntCase(txtTotal.getText()));
                        pst.setString(5, waktu.getCurrentDateTime());
                        if (pst.executeUpdate() > 0) {
                            System.out.println("Sudah membuat Transaksi Beli");
                        }
                        pst = db.conn.prepareStatement("INSERT INTO detail_transaksi_beli VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            idbarang = tabelData.getValueAt(i, 4).toString();
                            pst.setString(1, this.idTr);
                            pst.setString(2, tabelData.getValueAt(i, 2).toString());
                            pst.setString(3, tabelData.getValueAt(i, 3).toString());
                            pst.setString(4, idbarang);
                            pst.setString(5, tabelData.getValueAt(i, 5).toString());
                            pst.setString(6, barang.getJenis(idbarang));
                            pst.setInt(7, Integer.parseInt(tabelData.getValueAt(i, 6).toString()));
                            pst.setInt(8, Integer.parseInt(tabelData.getValueAt(i, 7).toString()));
                            pst.setInt(9, Integer.parseInt(tabelData.getValueAt(i, 8).toString()));
                            if (pst.executeUpdate() > 0) {
                                System.out.println("Sudah membuat Detail Transaksi Beli ke " + i);
                            }
                        }
                        pst = db.conn.prepareStatement("INSERT INTO saldo VALUES (?, ?, ?, ?, ?)");
                        pst.setString(1, this.saldoCreateID());
                        pst.setInt(2, text.toIntCase(this.txtSaldo.getText()));
                        pst.setString(3, "tambah transaksi beli");
                        pst.setString(4, this.idTr);
                        pst.setString(5, null);
                        if (pst.executeUpdate() > 0) {
                            System.out.println("Sudah membuat Saldo Baru");
                        }
                        Message.showInformation(this, "Transaksi berhasil!");
                        // mereset tabel
                        this.updateTabelSupplier();
                        this.updateTabelBarang();
                        this.updateTabelData();
                        // mereset input
                        this.resetInput();
                        this.idTr = this.trb.createIDTransaksi();
                        this.inpID.setText("<html><p>:&nbsp;" + this.idTr + "</p></html>");
                        //ubah saldo
                        this.updateSaldo();
                        this.txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo)));
                        this.tabelDataBarang.setRowSelectionInterval(this.tabelDataBarang.getRowCount() - 1, this.tabelDataBarang.getRowCount() - 1);
                        this.tabelDataSupplier.setRowSelectionInterval(this.tabelDataSupplier.getRowCount() - 1, this.tabelDataSupplier.getRowCount() - 1);
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        break;
                    }
                    case JOptionPane.NO_OPTION: {
                        System.out.println("Transaksi Dibatalkan");
                        Message.showInformation(this, "Transaksi Dibatalkan!");
                        break;
                    }
                }
            } else {
                Message.showWarning(this, "Tabel Data Transaksi Tidak Boleh Kosong !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        } catch (SQLException | InValidUserDataException ex) {
            ex.printStackTrace();
            System.out.println("Error Message : " + ex.getMessage());
        }
    }//GEN-LAST:event_btnBayarActionPerformed

    private void btnBayarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBayarMouseEntered
        this.btnBayar.setIcon(Gambar.getNoAktiveIcon(this.btnBayar.getIcon().toString()));
    }//GEN-LAST:event_btnBayarMouseEntered

    private void btnBayarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBayarMouseExited
        this.btnBayar.setIcon(Gambar.getNoBiasaIcon(this.btnBayar.getIcon().toString()));
    }//GEN-LAST:event_btnBayarMouseExited

    private void btnBatalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseEntered
        this.btnBatal.setIcon(Gambar.getNoAktiveIcon(this.btnBatal.getIcon().toString()));
    }//GEN-LAST:event_btnBatalMouseEntered

    private void btnBatalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseExited
        this.btnBatal.setIcon(Gambar.getNoBiasaIcon(this.btnBatal.getIcon().toString()));
    }//GEN-LAST:event_btnBatalMouseExited

    private void tabelDataBarangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataBarangMouseClicked
        if (!inpJumlah.getText().isEmpty()) {
            if (text.isNumber(inpJumlah.getText())) {
                if (Integer.parseInt(inpJumlah.getText()) > 0) {
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    // menampilkan data barang
                    this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                    this.showDataBarang();
                    this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                    inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } else {
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    // menampilkan data barang
                    this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                    this.showDataBarang();
                    this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                    inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
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
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        } else {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() - 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
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
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        } else {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() + 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_tabelDataBarangKeyPressed

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.idSelectedSupplier = this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 2).toString();
        this.idSelectedBarang = this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 4).toString();
        for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
            if (tabelDataBarang.getValueAt(i, 0).equals(this.idSelectedBarang)) {
                this.tabelDataBarang.setRowSelectionInterval(i, i);
                this.showBarang();
                //lalu hentikan for loop
                break;
            }
        }
        for (int i = 0; i < tabelDataSupplier.getRowCount(); i++) {
            if (tabelDataSupplier.getValueAt(i, 0).equals(this.idSelectedSupplier)) {
                this.tabelDataSupplier.setRowSelectionInterval(i, i);
                this.showSupplier();
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
                this.idSelectedSupplier = this.tabelData.getValueAt(this.tabelData.getSelectedRow() - 1, 2).toString();
                this.idSelectedBarang = this.tabelData.getValueAt(this.tabelData.getSelectedRow() - 1, 4).toString();
                for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                    if (tabelDataBarang.getValueAt(i, 0).equals(this.idSelectedBarang)) {
                        this.tabelDataBarang.setRowSelectionInterval(i, i);
                        this.showBarang();
                        //lalu hentikan for loop
                        break;
                    }
                }
                for (int i = 0; i < tabelDataSupplier.getRowCount(); i++) {
                    if (tabelDataSupplier.getValueAt(i, 0).equals(this.idSelectedSupplier)) {
                        this.tabelDataSupplier.setRowSelectionInterval(i, i);
                        this.showSupplier();
                        //lalu hentikan for loop
                        break;
                    }
                }
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (this.tabelData.getSelectedRow() < (this.tabelData.getRowCount() - 1)) {
                this.idSelectedSupplier = this.tabelData.getValueAt(this.tabelData.getSelectedRow() + 1, 2).toString();
                this.idSelectedBarang = this.tabelData.getValueAt(this.tabelData.getSelectedRow() + 1, 4).toString();
                for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                    if (tabelDataBarang.getValueAt(i, 0).equals(this.idSelectedBarang)) {
                        this.tabelDataBarang.setRowSelectionInterval(i, i);
                        this.showBarang();
                        //lalu hentikan for loop
                        break;
                    }
                }
                for (int i = 0; i < tabelDataSupplier.getRowCount(); i++) {
                    if (tabelDataSupplier.getValueAt(i, 0).equals(this.idSelectedSupplier)) {
                        this.tabelDataSupplier.setRowSelectionInterval(i, i);
                        this.showSupplier();
                        //lalu hentikan for loop
                        break;
                    }
                }
            }
        }
        //mengganti cursor
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataKeyPressed

    private void inpCariBarangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariBarangKeyTyped
        String key = this.inpCariBarang.getText();
        this.keywordBarang = "WHERE id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'";
        this.updateTabelBarang();
    }//GEN-LAST:event_inpCariBarangKeyTyped

    private void inpCariBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariBarangKeyReleased
        String key = this.inpCariBarang.getText();
        this.keywordBarang = "WHERE id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'";
        this.updateTabelBarang();
    }//GEN-LAST:event_inpCariBarangKeyReleased

    private void inpCariSupplierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariSupplierKeyTyped
        String key = this.inpCariSupplier.getText();
        this.keywordSupplier = "WHERE id_supplier LIKE '%" + key + "%' OR nama_supplier LIKE '%" + key + "%'";
        this.updateTabelSupplier();
    }//GEN-LAST:event_inpCariSupplierKeyTyped

    private void inpCariSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariSupplierKeyReleased
        String key = this.inpCariSupplier.getText();
        this.keywordSupplier = "WHERE id_supplier LIKE '%" + key + "%' OR nama_supplier LIKE '%" + key + "%'";
        this.updateTabelSupplier();
    }//GEN-LAST:event_inpCariSupplierKeyReleased

    private void inpCariSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariSupplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpCariSupplierActionPerformed

    private void tabelDataSupplierSupplier1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataSupplierSupplier1MouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // menampilkan data supplier
        this.idSelectedSupplier = this.tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow(), 0).toString();
        System.out.println("id supplier " + this.idSelectedSupplier);
        this.showDataSupplier();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataSupplierSupplier1MouseClicked

    private void tabelDataSupplierSupplier1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataSupplierSupplier1KeyPressed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (this.tabelDataSupplier.getSelectedRow() >= 1) {
                this.idSelectedSupplier = this.tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow() - 1, 0).toString();
                this.showDataSupplier();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (this.tabelDataSupplier.getSelectedRow() < (this.tabelDataSupplier.getRowCount() - 1)) {
                this.idSelectedSupplier = this.tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow() + 1, 0).toString();
                this.showDataSupplier();
            }
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataSupplierSupplier1KeyPressed

    private void btnHapusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseEntered
        this.btnHapus.setIcon(Gambar.getNoAktiveIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseEntered

    private void btnHapusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseExited
        // TODO add your handling code here:
        this.btnHapus.setIcon(Gambar.getNoBiasaIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseExited

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

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
                        this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                        inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    }
                } else {
//                    System.out.println("harus angka");
                    Message.showWarning(this, "Harus Angka !");
                }
            } else {
//                System.out.println("Tidak Boleh kosong");
            }
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_inpJumlahActionPerformed

    private void inpJumlahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyTyped
//        if(evt.getKeyCode() == KeyEvent.VK_NUMPAD0 || evt.getKeyCode() == KeyEvent.VK_NUMPAD1 || evt.getKeyCode() == KeyEvent.VK_NUMPAD2 || evt.getKeyCode() == KeyEvent.VK_NUMPAD3 || evt.getKeyCode() == KeyEvent.VK_NUMPAD4 || evt.getKeyCode() == KeyEvent.VK_NUMPAD5 || evt.getKeyCode() == KeyEvent.VK_NUMPAD6 || evt.getKeyCode() == KeyEvent.VK_NUMPAD7 || evt.getKeyCode() == KeyEvent.VK_NUMPAD8 || evt.getKeyCode() == KeyEvent.VK_NUMPAD9){
//            this.totalHarga = Integer.parseInt(inpJumlah.getText())*hargaBeli;
//            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
//        }
    }//GEN-LAST:event_inpJumlahKeyTyped

    private void btnHapusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DefaultTableModel modelData = (DefaultTableModel) tabelData.getModel();
        DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
        String idbarang = "";
        int saldoBaru = 0, stok = 0, totalharga = 0, totalSisa = 0, sisaBarang = 0, totalkeseluruhan = text.toIntCase(txtTotal.getText());
        if (tabelData.getSelectedRow() < 0) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Tidak ada data yang dipilih!");
        } else {
            int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus baris " + (tabelData.getSelectedRow() + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            switch (status) {
                case JOptionPane.YES_OPTION: {
                    idbarang = tabelData.getValueAt(tabelData.getSelectedRow(), 4).toString();
                    stok = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 7).toString());
                    totalharga = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 8).toString());
                    totalSisa = totalkeseluruhan - totalharga;
                    txtTotal.setText(text.toMoneyCase(Integer.toString(totalSisa)));
                    for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                        if (tabelDataBarang.getValueAt(i, 0).toString().equals(idbarang)) {
                            sisaBarang = Integer.parseInt(tabelDataBarang.getValueAt(i, 3).toString()) - stok;
                            modelBarang.setValueAt(sisaBarang, i, 3);
                        }
                    }
                    //ganti saldo
                    saldoBaru = this.Saldo - totalSisa;
                    this.txtSaldo.setText(text.toMoneyCase(Integer.toString(saldoBaru)));
                    // mereset input
                    this.resetInput();
                    if (tabelData.getValueAt(tabelData.getSelectedRow(), 4).equals(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0)) && tabelData.getValueAt(tabelData.getSelectedRow(), 2).equals(tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow(), 0))) {
                        this.tabelDataBarang.setRowSelectionInterval(this.tabelDataBarang.getRowCount() - 1, this.tabelDataBarang.getRowCount() - 1);
                    }
                    modelData.removeRow(tabelData.getSelectedRow());
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    break;
                }
                case JOptionPane.NO_OPTION: {
                    System.out.println("Hapus Dibatalkan");
                    Message.showInformation(this, "Hapus Dibatalkan!");
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    break;
                }
            }
        }
    }//GEN-LAST:event_btnHapusMouseClicked

    private void btnBatalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalMouseClicked

    private void btnEditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DefaultTableModel model = (DefaultTableModel) tabelData.getModel();
        DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
        String idBarangdata = "", idBarangtabel = "", namabarang = "", idSupplierdata = "", idSuppliertabel = "", namasupplier = "";
        int totalsaldo = 0, sisasaldo = 0, barisbarang = -1, barisdata = -1, stoktabel = 0, totalstok = 0, jumlahbarang, sisastok = 0, tharga = 0, totalKeseluruhan = 0;
        if (tabelData.getSelectedRow() < 0) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Tidak ada data yang dipilih!");
        } else {
            idBarangtabel = tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
            namabarang = tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 1).toString();
            idSuppliertabel = tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow(), 0).toString();
//            namasupplier = tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow(), 1).toString();
            //mencari baris data barang di tabel barang berdasarkan id barang
            idSupplierdata = tabelData.getValueAt(tabelData.getSelectedRow(), 2).toString();
            idBarangdata = tabelData.getValueAt(tabelData.getSelectedRow(), 4).toString();
            //jika idsupplier di tabelData yg dipilih sama dengan idsupplier di tabelSupplier yg dipilih
            if (idSuppliertabel.equals(idSupplierdata)) {
                //jika idbarang di tabelData yg dipilih sama dengan idbarang di tabelBarang yg dipilih
                if (idBarangtabel.equals(idBarangdata)) {
                    System.out.println("id supplier sama idbarang sama");
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah " + namabarang + " di Tabel transaksi di baris ke " + (tabelData.getSelectedRow() + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                    switch (status) {
                        case JOptionPane.YES_OPTION: {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            jumlahbarang = Integer.parseInt(inpJumlah.getText());
                            tharga = text.toIntCase(inpTotalHarga.getText());
                            stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                            totalstok = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 7).toString()) + stoktabel;
                            //ambil total harga dari tabel transaksi lalu hitung total saldo
                            totalsaldo = this.Saldo + Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 6).toString());
                            //update tabel barang
                            if (jumlahbarang <= totalstok) {
                                sisasaldo = totalsaldo - this.totalHarga;
                                //update tabel barang 
                                sisastok = totalstok - jumlahbarang;
                                modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
                                //update tabel data
                                model.setValueAt(jumlahbarang, tabelData.getSelectedRow(), 7);
                                model.setValueAt(tharga, tabelData.getSelectedRow(), 8);
                                //update harga keseluruhan
                                for (int i = 0; i < tabelData.getRowCount(); i++) {
                                    totalKeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                                }
                                txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                //ubah saldo
                                txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo - totalKeseluruhan)));
                                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            } else {
                                Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                            }
                            break;
                        }
                        case JOptionPane.NO_OPTION: {
                            System.out.println("Edit Dibatalkan");
                            break;
                        }
                    }
                    //jika idbarang di tabelData yg dipilih berbeda dengan idbarang di tabelBarang yg dipilih
                } else {
                    //mencari idbarang di tabelData yg sama dengan id barang di tabelBarang yg dipilih
                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                        if (tabelData.getValueAt(i, 4).equals(idBarangtabel)) {
                            idBarangdata = tabelData.getValueAt(i, 4).toString();
                            barisdata = i;
                            break;
                        }
                    }
                    //jika idbarang di tabelBarang yg dipilih ada di tabelData maka ubah tabelData
                    if (idBarangtabel.equals(idBarangdata)) {
                        System.out.println("idsupplier sama idbarang beda");
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah " + namabarang + " di Tabel transaksi di baris ke " + (barisdata + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                        switch (status) {
                            case JOptionPane.YES_OPTION: {
                                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                jumlahbarang = Integer.parseInt(inpJumlah.getText());
                                tharga = text.toIntCase(inpTotalHarga.getText());
                                stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                                totalstok = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 5).toString()) + stoktabel;
                                //ambil total harga dari tabel transaksi lalu hitung total saldo
//                                totalsaldo = this.Saldo + Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 6).toString());
                                sisastok = totalstok + jumlahbarang;
                                if (jumlahbarang <= totalstok) {
                                    //update tabel barang
                                    modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
//                                    sisasaldo = totalsaldo - this.totalHarga;
                                    //update tabel data
                                    model.setValueAt(jumlahbarang, barisdata, 7);
                                    model.setValueAt(tharga, barisdata, 8);
                                    //update harga keseluruhan
                                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                                        totalKeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                                    }
                                    txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                    //ubah saldo
                                    txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo - totalKeseluruhan)));
                                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                } else {
                                    Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                                }
                                break;
                            }
                            case JOptionPane.NO_OPTION: {
                                System.out.println("Edit Dibatalkan");
                                break;
                            }
                        }
                        //jika idbarang di tabelBarang yg dipilih tidak ada di tabelData maka tambahkan data barang ke tabelData (data baru
                    } else {
                        System.out.println("id supplier sama id barang null");
                        Message.showWarning(this, "Data Tidak Ada Di Tabel Transaksi !");
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
                //jika idsupplier di tabelData yg dipilih berbeda dengan idsupplier di tabelSupplier yg dipilih maka ganti supplier
            } else {
                for (int i = 0; i < tabelData.getRowCount(); i++) {
                    if (tabelData.getValueAt(i, 2).equals(idSuppliertabel)) {
                        idSupplierdata = tabelData.getValueAt(i, 2).toString();
                        namasupplier = tabelData.getValueAt(i, 2).toString();
//                        idSupplierdata = tabelData.getValueAt(i, 2).toString();
                        break;
                    }
                }
                if (idBarangtabel.equals(idBarangdata)) {
                    System.out.println("idsupplier beda idbarang sama");
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah Supplier di Tabel transaksi di baris ke " + (tabelData.getSelectedRow() + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                    switch (status) {
                        case JOptionPane.YES_OPTION: {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            jumlahbarang = Integer.parseInt(inpJumlah.getText());
                            tharga = text.toIntCase(inpTotalHarga.getText());
                            stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                            totalstok = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 7).toString()) + stoktabel;
                            idSupplierdata = tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow(),0).toString();
                            namasupplier = tabelDataSupplier.getValueAt(tabelDataSupplier.getSelectedRow(),1).toString();
                            //jika jumlah barang di tabel transaksi kurang dari sama dengan stok di tabel barang
                            if (jumlahbarang <= totalstok) {
                                sisastok = totalstok - jumlahbarang;
                                //update tabel barang
                                modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
                                //mengganti supplier di tabel data
                                model.setValueAt(idSupplierdata,tabelData.getSelectedRow(),2);
                                model.setValueAt(namasupplier,tabelData.getSelectedRow(),3);
                                //update tabel data untuk barang
                                model.setValueAt(jumlahbarang, tabelData.getSelectedRow(), 7);
                                model.setValueAt(tharga, tabelData.getSelectedRow(), 8);
                                //update harga keseluruhan
                                for (int i = 0; i < tabelData.getRowCount(); i++) {
                                    totalKeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                                }
                                txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                //ubah saldo
                                txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo - totalKeseluruhan)));
                                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            } else {
                                Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                            }
                            break;
                        }
                        case JOptionPane.NO_OPTION: {
                            System.out.println("Edit Dibatalkan");
                            break;
                        }
                    }
                    //jika idbarang di tabelData yg dipilih berbeda dengan idbarang di tabelBarang yg dipilih
                } else {
                    //mencari idbarang di tabelData yg sama dengan id barang di tabelBarang yg dipilih
                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                        if (tabelData.getValueAt(i, 4).equals(idBarangtabel)) {
                            idBarangdata = tabelData.getValueAt(i, 4).toString();
                            barisdata = i;
                            break;
                        }
                    }
                    //jika idbarang di tabelBarang yg dipilih ada di tabelData maka ubah tabelData
                    if (idBarangtabel.equals(idBarangdata)) {
                        System.out.println("id supplier beda idbarang berbeda");
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah " + namabarang + " di Tabel transaksi di baris ke " + (barisdata + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                        switch (status) {
                            case JOptionPane.YES_OPTION: {
                                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                jumlahbarang = Integer.parseInt(inpJumlah.getText());
                                tharga = text.toIntCase(inpTotalHarga.getText());
                                stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                                totalstok = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 7).toString()) + stoktabel;
                                if (jumlahbarang <= totalstok) {
                                    //hitung sisa stok 
                                    sisastok = totalstok - jumlahbarang;
                                    //update tabel barang
                                    modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
                                    //update tabel data
                                    model.setValueAt(jumlahbarang, barisdata, 7);
                                    model.setValueAt(tharga, barisdata, 8);
                                    //update harga keseluruhan
                                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                                        totalKeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                                    }
                                    txtTotal.setText(text.toMoneyCase(Integer.toString(totalKeseluruhan)));
                                    //ubah saldo
                                    txtSaldo.setText(text.toMoneyCase(Integer.toString(this.Saldo - totalKeseluruhan)));
                                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                } else {
                                    Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                                }
                                break;
                            }
                            case JOptionPane.NO_OPTION: {
                                System.out.println("Edit Dibatalkan");
                                break;
                            }
                        }
                        //jika idbarang di tabelBarang yg dipilih tidak ada di tabelData maka tambahkan data barang ke tabelData
                    } else {
                        System.out.println("data baru");
                        Message.showWarning(this, "Data Tidak Ada Di Tabel Transaksi !");
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        }
    }//GEN-LAST:event_btnEditMouseClicked

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        this.btnEdit.setIcon(Gambar.getNoAktiveIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        this.btnEdit.setIcon(Gambar.getNoBiasaIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseExited

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnSimpanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseExited
        // TODO add your handling code here:
        this.btnSimpan.setIcon(Gambar.getNoBiasaIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseExited

    private void btnSimpanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseEntered
        this.btnSimpan.setIcon(Gambar.getNoAktiveIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseEntered

    private void btnSimpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseClicked
        /**
         * btn simpan digunakan untuk menambah data ke tabel transaksi jika
         * tabel transaksi kosong maka tambah data barang yg dipilih dan id
         * suppllier yg dipilih ke tabel transaksi jika tabel transaksi berisi
         * data maka : jika id supplier yang dipilih di tabel supplier sama
         * dengan id supplier di tabel transaksi maka : * jika id barang yang
         * dipilih di tabel barang sama dengan id barang di tabel transaksi maka
         * : cari idbarang, indeks baris, stok, harga total tabel transaksi
         * berdasarkan idbarang yang dipilih di tabel barang. ** jika jumlah
         * barang yang dimasukkan ke tabel transaksi lebih dari jumlah
         * barang(stok) di tabel barang maka : beri pesan eror "jumlah barang
         * lebih dari stok yang tersedia" ** jika jumlah barang yang dimasukkan
         * ke tabel transaksi kurang dari sama dengan jumlah barang(stok) di
         * tabel barang maka : ubah data di tabel transaksi berdasarkan indeks
         * baris. * jika id barang yang dipilih di tabel barang berbeda dengan
         * id barang di tabel transaksi maka : ** jika jumlah barang yang
         * dimasukkan ke tabel transaksi lebih dari jumlah barang(stok) di tabel
         * barang maka : beri pesan eror "jumlah barang lebih dari stok yang
         * tersedia" ** jika jumlah barang yang dimasukkan ke tabel transaksi
         * kurang dari sama dengan jumlah barang(stok) di tabel barang maka :
         * tambah data barang yg dipilih dan id suppllier yg dipilih ke tabel
         * transaksi jika id supplier yang dipilih di tabel supplier sama dengan
         * id supplier di tabel transaksi maka : * jika id barang yang dipilih
         * di tabel barang sama dengan id barang di tabel transaksi maka : **
         * jika jumlah barang yang dimasukkan ke tabel transaksi lebih dari
         * jumlah barang(stok) di tabel barang maka : beri pesan eror "jumlah
         * barang lebih dari stok yang tersedia" ** jika jumlah barang yang
         * dimasukkan ke tabel transaksi kurang dari sama dengan jumlah
         * barang(stok) di tabel barang maka : tambah data barang yg dipilih dan
         * id suppllier yg dipilih ke tabel transaksi. * jika id barang yang
         * dipilih di tabel barang berbeda dengan id barang di tabel transaksi
         * maka : ** jika jumlah barang yang dimasukkan ke tabel transaksi lebih
         * dari jumlah barang(stok) di tabel barang maka : beri pesan eror
         * "jumlah barang lebih dari stok yang tersedia" ** jika jumlah barang
         * yang dimasukkan ke tabel transaksi kurang dari sama dengan jumlah
         * barang(stok) di tabel barang maka : tambah data barang yg dipilih dan
         * id suppllier yg dipilih ke tabel transaksi.
         *
         */
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DefaultTableModel modelData = (DefaultTableModel) tabelData.getModel();
        DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
        DefaultTableModel modelSupplier = (DefaultTableModel) tabelDataSupplier.getModel();
        boolean error = false, cocokbarang = false, cocoksupplier = false;
        System.out.println("simpan");
        int saldobaru = 0, tharga = 0, total = 0, stokSekarang = 0, totalProduk = 0, sisaStok = 0, jumlahB = 0, thargaLama = 0, thargaBaru = 0, barisbarang = -1, barissupplier = -1, barissama = -1;
        //        String idProdukBaru = inpIDBarang.getText();
        if (inpTanggal.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Tanggal harus Di isi !");
//        } else if (tabelDataSupplier.getSelectedRow() > -1) {
//            error = true;
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            Message.showWarning(this, "Anda Belum memilih Supplier!");
        } else if (inpID.getText().equals(":")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "ID Transaksi harus Di isi !");
        } else if (inpNamaKaryawan.getText().equals(":")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Karyawan harus Di isi !");
        } else if (inpNamaSupplier.getText().equals(":")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Supplier harus Di isi !");
//        } else if (inpNamaSupplier.getText().equals(":")) {
//            error = true;
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            Message.showWarning(this, "Nama Supplier harus Di isi !");
        } else if (inpIDBarang.getText().equals(":")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "ID Barang harus Di isi !");
        } else if (inpNamaBarang.getText().equals(":")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Barang harus Di isi !");
        } else if (inpJumlah.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Jumlah Barang harus Di isi !");
        } else if (Integer.parseInt(inpJumlah.getText()) <= 0) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Jumlah Barang Harus lebih dari 0 !");
        } else if (!text.isNumber(inpJumlah.getText())) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Jumlah Barang harus angka !");
        } else if (inpHarga.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Harus di isi!");
        } else if (inpTotalHarga.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Total Harus di isi!");
        }
        //
        if (!error) {
            //mencari stok dari tabel barang
            for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                if (tabelDataBarang.getValueAt(i, 0).equals(this.idBarang)) {
                    stokSekarang = Integer.parseInt(tabelDataBarang.getValueAt(i, 3).toString());
                    break;
                }
            }
            if (tabelData.getRowCount() >= 1) { //jika tabel ada isinya
                String idBarangLama = "", idbarang = "", idSupplierLama = "", idsupplier, namasupplier = "";
                int stokLama = 0, stokBaru = 0;
                //mencari stok total harga barang dari tabel data
                for (int i = 0; i < tabelData.getRowCount(); i++) {
                    idsupplier = tabelData.getValueAt(i, 2).toString();
                    idbarang = tabelData.getValueAt(i, 4).toString();
                    //jika idbarang di tabelBarang yg dipilih sama dengan idbarang di tabelData dan idsupplier di tabelSupplier yg dipilih sama dengan idsupplier di tabel data
                    if (this.idBarang.equals(idbarang) && this.idSupplier.equals(idsupplier)) {
                        barissama = i;
                        cocokbarang = true;
                        cocoksupplier = true;
                        idBarangLama = idbarang;
                        stokLama = Integer.parseInt(tabelData.getValueAt(i, 7).toString());
                        thargaLama = Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                        idSupplierLama = idsupplier;
                        namasupplier = tabelData.getValueAt(i, 3).toString();
                        break;
                    }
                    //jika idbarang di tabelBarang yg dipilih berbeda dengan idbarang di tabelData dan idsupplier di tabelSupplier yg dipilih berbeda dengan idsupplier di tabel data
                    if (i >= tabelData.getRowCount() - 1 && !(this.idBarang.equals(idbarang) && this.idSupplier.equals(idsupplier))) {
                        for (int j = 0; j < tabelData.getRowCount(); j++) {
                            idbarang = tabelData.getValueAt(j, 4).toString();
                            if (this.idBarang.equals(idbarang)) {
                                cocokbarang = true;
                                barisbarang = j;
                                idBarangLama = idbarang;
                                stokLama = Integer.parseInt(tabelData.getValueAt(j, 7).toString());
                                thargaLama = Integer.parseInt(tabelData.getValueAt(j, 8).toString());
                            }
                            idsupplier = tabelData.getValueAt(j, 2).toString();
                            if (this.idSupplier.equals(idsupplier)) {
                                cocoksupplier = true;
                                barissupplier = j;
                                idSupplierLama = idsupplier;
                                namasupplier = tabelData.getValueAt(j, 3).toString();
                            }
                        }
                    }
                }
                //mengecek jika id supplier sama
                if (cocoksupplier) {
                    //mengecek jika idbarang sama
                    if (cocokbarang) {
                        System.out.println("data barang dan supplier sama");
                        jumlahB = Integer.parseInt(inpJumlah.getText());
                        stokBaru = jumlahB + stokLama;
                        thargaBaru = text.toIntCase(inpTotalHarga.getText());
                        tharga = thargaLama + thargaBaru;
                        // jika harga lebih dari saldo
                        if (tharga > this.Saldo) {
                            System.out.println("Saldo anda tidak cukup !");
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            Message.showWarning(this, "Saldo anda tidak cukup !");
                        } else {
                            //ubah saldo
                            saldobaru = this.Saldo - this.totalHarga;
                            txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                            //update tabel data
                            modelData.setValueAt(stokBaru, barissama, 7);
                            modelData.setValueAt(tharga, barissama, 8);
                            //update table barang
                            sisaStok = stokSekarang + jumlahB;
                            //                            System.out.println("stok di dalam tabel " + this.stok);
                            //                            System.out.println("sisa stok sekarang " + sisaStok);
                            modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                            //update total harga keseluruhan
                            for (int i = 0; i < tabelData.getRowCount(); i++) {
                                total += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                            }
                            //                            System.out.println(total);
                            this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    } else {
                        //mengecek jika supplier sama dan idbarang berbeda
                        //                        System.out.println("kemungkinan 2");
                        System.out.println("supplier sama barang beda");
                        jumlahB = Integer.parseInt(inpJumlah.getText());
                        //jika harga lebih dari saldo
                        if (this.totalHarga > this.Saldo) {
                            System.out.println("Saldo Anda tidak cukup !");
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            Message.showWarning(this, "Saldo anda tidak cukup !");
                        } else {
                            System.out.println("data baru ");
                            //ubah saldo
                            saldobaru = this.Saldo - this.totalHarga;
                            txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                            //tambah baris tabel transaksi
                            addrowtotabeldetail(new Object[]{
                                waktu.getCurrentDate(),
                                this.idTr,
                                this.idSupplier,
                                this.namaSupplier,
                                this.idBarang,
                                this.namaBarang,
                                this.hargaBeli,
                                jumlahB,
                                this.totalHarga
                            });
                            //ubah stok di tabel barang
                            sisaStok = stokSekarang + jumlahB;
                            modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                            //update total harga keseluruhan
                            for (int i = 0; i < tabelData.getRowCount(); i++) {
                                total += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                            }
                            this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                    //megecek jika supplier tidak cocok
                } else {
                    //mengecek jika supplier tidak cocok dan idbarang cocok
                    if (cocokbarang) {
                        System.out.println("supplier beda barang sama ");
                        jumlahB = Integer.parseInt(inpJumlah.getText());
                        stokBaru = jumlahB + stokLama;
                        thargaBaru = text.toIntCase(inpTotalHarga.getText());
                        tharga = thargaLama + thargaBaru;
                        System.out.println(idBarangLama);
                        // jika harga lebih dari saldo
                        if (tharga > this.Saldo) {
                            System.out.println("Saldo anda tidak cukup !");
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            Message.showWarning(this, "Saldo anda tidak cukup !");
                        } else {
                            //ubah saldo
                            saldobaru = this.Saldo - this.totalHarga;
                            txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                            //update tabel data
                            addrowtotabeldetail(new Object[]{
                                waktu.getCurrentDate(),
                                this.idTr,
                                this.idSupplier,
                                this.namaSupplier,
                                this.idBarang,
                                this.namaBarang,
                                this.hargaBeli,
                                jumlahB,
                                this.totalHarga
                            });
                            //update table barang
                            sisaStok = stokSekarang + jumlahB;
                            modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                            //update total harga keseluruhan
                            for (int i = 0; i < tabelData.getRowCount(); i++) {
                                total += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                            }
                            this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                        //jika supplier beda dan idbarang beda
                    } else {
                        System.out.println("beda suppplier beda barang");
                        jumlahB = Integer.parseInt(inpJumlah.getText());
                        //jika harga lebih dari saldo
                        if (this.totalHarga > this.Saldo) {
                            System.out.println("Saldo anda tidak cukup !");
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            Message.showWarning(this, "Saldo anda tidak cukup !");
                        } else {
                            //ubah saldo
                            saldobaru = this.Saldo - this.totalHarga;
                            txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                            //tambah data ke tabel transaksi
                            addrowtotabeldetail(new Object[]{
                                waktu.getCurrentDate(),
                                this.idTr,
                                this.idSupplier,
                                this.namaSupplier,
                                this.idBarang,
                                this.namaBarang,
                                this.hargaBeli,
                                jumlahB,
                                this.totalHarga
                            });
                            //ubah stok
                            sisaStok = stokSekarang + jumlahB;
                            modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                            //update total harga keseluruhan
                            for (int i = 0; i < tabelData.getRowCount(); i++) {
                                total += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                            }
                            this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                }
            } else {
                //jika tabel kosong
                System.out.println("data kosong");
                jumlahB = Integer.parseInt(inpJumlah.getText());
                this.Saldo = text.toIntCase(txtSaldo.getText());
                //jika harga lebih dari saldo
                if (this.totalHarga > this.Saldo) {
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    Message.showWarning(this, "Saldo tidak cukup !");
                } else {
                    //tambah data
                    addrowtotabeldetail(new Object[]{
                        waktu.getCurrentDate(),
                        this.idTr,
                        this.idSupplier,
                        this.namaSupplier,
                        this.idBarang,
                        this.namaBarang,
                        this.hargaBeli,
                        jumlahB,
                        this.totalHarga
                    });
                    //update saldo
                    saldobaru = this.Saldo - this.totalHarga;
                    txtSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                    //update tabel barang
                    sisaStok = stokSekarang + jumlahB;
                    modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                    //update total harga keseluruhan
                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                        total += Integer.parseInt(tabelData.getValueAt(i, 8).toString());
                    }
                    this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }//GEN-LAST:event_btnSimpanMouseClicked

    private void inpJumlahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyPressed
//        try {
//            if (!inpJumlah.getText().toString().isEmpty()) {
//                if (text.isNumber(inpJumlah.getText())) {
//                    int jumlahbarang = Integer.parseInt(inpJumlah.getText());
//                    if (jumlahbarang <= 0) {
//                        Message.showWarning(this, "Jumlah Barang Harus lebih dari 0 !");
//                    } else {
//                        this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
//                        inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
//                    }
//                } else {
//                    System.out.println("harus angka P");
//                }
//            } else {
//                System.out.println("Tidak Boleh kosong");
//            }
//        } catch (NumberFormatException e) {
//            System.out.println(e.getMessage());
////            System.out.println("harus angka ");
////            Message.showWarning(this, "Jumlah Barang Harus Angka !");
//        }
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
                        this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaBeli;
                        inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    }
                } else {
//                    System.out.println("harus angka");
                    Message.showWarning(this, "Harus Angka !");
                }
            } else {
//                System.out.println("Tidak Boleh kosong");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_inpJumlahKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JTextField inpCariBarang;
    private javax.swing.JTextField inpCariSupplier;
    private javax.swing.JLabel inpHarga;
    private javax.swing.JLabel inpID;
    private javax.swing.JLabel inpIDBarang;
    private javax.swing.JTextField inpJumlah;
    private javax.swing.JLabel inpNamaBarang;
    private javax.swing.JLabel inpNamaKaryawan;
    private javax.swing.JLabel inpNamaSupplier;
    private javax.swing.JLabel inpTanggal;
    private javax.swing.JLabel inpTotalHarga;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tabelData;
    private javax.swing.JTable tabelDataBarang;
    private javax.swing.JTable tabelDataSupplier;
    private javax.swing.JLabel txtSaldo;
    private javax.swing.JLabel txtTotal;
    // End of variables declaration//GEN-END:variables
}

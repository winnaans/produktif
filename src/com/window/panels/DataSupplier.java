package com.window.panels;

import com.data.db.Database;
import com.data.db.DatabaseTables;
import com.manage.Barang;
import com.manage.Message;
import com.manage.Text;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.users.Supplier;
import com.window.dialogs.InputSupplier;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Gemastik Lightning
 */
public class DataSupplier extends javax.swing.JPanel {

    private final Database db = new Database();
    private final Supplier supplier = new Supplier();

    private final Barang barang = new Barang();

    private final Text text = new Text();

    private String idSelected = "", keyword = "", namaSupplier, noTelp, alamat, ttBrg, ttlUang, last;

    DateFormat tanggalMilis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateFormat tanggalFull = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat time = new SimpleDateFormat("ss:mm:hh");
    private final DateFormat timeMillis = new SimpleDateFormat("ss.SSS:mm:hh");

    public DataSupplier() {
        initComponents();
        db.startConnection();
        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.btnAdd.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnEdit.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnDel.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        JLabel[] values = {
            this.valIDSupplier, this.valNamaSupplier, this.valNoTelp, this.valAlamat,
            this.valBrgSupplier, this.valUang, valLast
        };

        for (JLabel lbl : values) {
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
        this.updateTabel();
        this.valNoTelp.setText("");
    }

    public void closeKoneksi() {
        db.closeConnection();
        supplier.closeConnection();
        barang.closeConnection();
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

    private Object[][] getData() {
        try {
            Object[][] obj;
            int rows = 0;
            String sql = "SELECT id_supplier, nama_supplier, no_telp, alamat FROM supplier " + keyword;
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            obj = new Object[this.getJumlahData("supplier", keyword)][4];
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

    private void updateTabel() {
        this.tabelData.setModel(new javax.swing.table.DefaultTableModel(
                getData(),
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

    private void showData() throws ParseException {
        // mendapatkan data
        String tanggalPenuh;
        Date tanggal = new Date();
        this.namaSupplier = supplier.getNama(this.idSelected);
        this.noTelp = text.toTelephoneCase(supplier.getNoTelp(this.idSelected));
        this.alamat = supplier.getAlamat(this.idSelected);

        this.ttBrg = "" + this.supplier.getJumlahData(DatabaseTables.DETAIL_TRANSAKSI_BELI.name(), String.format("WHERE id_supplier='%s'", this.idSelected));
        this.ttlUang = text.toMoneyCase("" + this.supplier.sumData(DatabaseTables.DETAIL_TRANSAKSI_BELI.name(), "total_harga", String.format("where id_supplier = '%s'", this.idSelected)));
        tanggalPenuh = this.supplier.getData(DatabaseTables.TRANSAKSI_BELI.name(), "tanggal", "WHERE id_tr_beli = '" + this.supplier.getData(DatabaseTables.DETAIL_TRANSAKSI_BELI.name(), "id_tr_beli", " WHERE id_supplier = '" + this.idSelected + "'") + "' ORDER BY tanggal DESC");
        if (tanggalPenuh.equals("null")) {
            this.last = "Belum Pernah Melakukan Transaksi";
        } else {
            tanggal = tanggalMilis.parse(tanggalPenuh);
            this.last = date.format(tanggal);
        }

        // menampilkan data
        this.valIDSupplier.setText("<html><p>:&nbsp;" + idSelected + "</p></html>");
        this.valNamaSupplier.setText("<html><p>:&nbsp;" + namaSupplier + "</p></html>");
        this.valNoTelp.setText("<html><p style=\"text-decoration:underline; color:rgb(0,0,0);\">:&nbsp;" + noTelp + "</p></html>");
        this.valAlamat.setText("<html><p>:&nbsp;" + alamat + "</p></html>");
        this.valBrgSupplier.setText("<html><p>:&nbsp;" + ttBrg + " Barang</p></html>");
        this.valUang.setText("<html><p>:&nbsp;" + ttlUang + "</p></html>");
        this.valLast.setText("<html><p>:&nbsp;" + last + "</p></html>");
    }

    private void resetData() {
        this.valIDSupplier.setText("<html><p>:&nbsp;</p></html>");
        this.valNamaSupplier.setText("<html><p>:&nbsp;</p></html>");
        this.valNoTelp.setText("<html><p>:&nbsp;</p></html>");
        this.valAlamat.setText("<html><p>:&nbsp;</p></html>");
        this.valBrgSupplier.setText("<html><p>:&nbsp;</p></html>");
        this.valUang.setText("<html><p>:&nbsp;</p></html>");
        this.valLast.setText("<html><p>:&nbsp;</p></html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCari = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        inpCari = new javax.swing.JTextField();
        valIDSupplier = new javax.swing.JLabel();
        valNamaSupplier = new javax.swing.JLabel();
        valNoTelp = new javax.swing.JLabel();
        valAlamat = new javax.swing.JLabel();
        valBrgSupplier = new javax.swing.JLabel();
        valUang = new javax.swing.JLabel();
        valLast = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        background = new javax.swing.JLabel();

        lblCari.setFont(new java.awt.Font("Dialog", 1, 15)); // NOI18N
        lblCari.setForeground(new java.awt.Color(237, 12, 12));
        lblCari.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCari.setText("Cari ID / Nama Supplier :");

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(957, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAdd.setBackground(new java.awt.Color(41, 180, 50));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambah-075.png"))); // NOI18N
        btnAdd.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnAdd.setOpaque(false);
        btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAddMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAddMouseExited(evt);
            }
        });
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 636, 154, 50));

        btnEdit.setBackground(new java.awt.Color(34, 119, 237));
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-edit-075.png"))); // NOI18N
        btnEdit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnEdit.setOpaque(false);
        btnEdit.setRolloverEnabled(true);
        btnEdit.addMouseListener(new java.awt.event.MouseAdapter() {
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
        add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 636, 154, 50));

        btnDel.setBackground(new java.awt.Color(220, 41, 41));
        btnDel.setForeground(new java.awt.Color(255, 255, 255));
        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-hapus-075.png"))); // NOI18N
        btnDel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnDel.setOpaque(false);
        btnDel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDelMouseExited(evt);
            }
        });
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelActionPerformed(evt);
            }
        });
        add(btnDel, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 636, 154, 50));

        inpCari.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariKeyTyped(evt);
            }
        });
        add(inpCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 80, 180, 23));

        valIDSupplier.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valIDSupplier.setForeground(new java.awt.Color(0, 0, 0));
        valIDSupplier.setText(":");
        add(valIDSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, 310, 45));

        valNamaSupplier.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valNamaSupplier.setForeground(new java.awt.Color(0, 0, 0));
        valNamaSupplier.setText(":");
        add(valNamaSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 201, 310, 44));

        valNoTelp.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valNoTelp.setForeground(new java.awt.Color(0, 0, 0));
        valNoTelp.setText(":");
        valNoTelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                valNoTelpMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                valNoTelpMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                valNoTelpMouseExited(evt);
            }
        });
        add(valNoTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 270, 310, 45));

        valAlamat.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valAlamat.setForeground(new java.awt.Color(0, 0, 0));
        valAlamat.setText(":");
        add(valAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 340, 310, 44));

        valBrgSupplier.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valBrgSupplier.setForeground(new java.awt.Color(0, 0, 0));
        valBrgSupplier.setText(":");
        add(valBrgSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 410, 310, 45));

        valUang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valUang.setForeground(new java.awt.Color(0, 0, 0));
        valUang.setText(":");
        add(valUang, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 480, 310, 44));

        valLast.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valLast.setForeground(new java.awt.Color(0, 0, 0));
        valLast.setText(":");
        add(valLast, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 550, 310, 44));

        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Supplier", "Nama Supplier", "Alamat"
            }
        ));
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

        add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 100, 550, 510));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-dataSupplier.png"))); // NOI18N
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        int status;
        boolean delete;

        // mengecek apakah ada data yang dipilih atau tidak
        if (tabelData.getSelectedRow() > -1) {
            // membuka confirm dialog untuk menghapus data
            Audio.play(Audio.SOUND_INFO);
            status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus '" + this.namaSupplier + "' ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);

            // mengecek pilihan dari supplier
            switch (status) {
                // jika yes maka data akan dihapus
                case JOptionPane.YES_OPTION:
                    // menghapus data supplier
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    delete = this.supplier.deleteSupplier(this.idSelected);
                    // mengecek apakah data supplier berhasil terhapus atau tidak
                    if (delete) {
                        Message.showInformation(this, "Data berhasil dihapus!");
                        // mengupdate tabel
                        this.updateTabel();
                        this.resetData();
                    } else {
                        Message.showInformation(this, "Data gagal dihapus!");
                    }
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    break;
            }
        } else {
            Message.showWarning(this, "Tidak ada data yang dipilih!!", true);
        }
    }//GEN-LAST:event_btnDelActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // membuka window input supplier
        Audio.play(Audio.SOUND_INFO);
        InputSupplier tbh = new InputSupplier(null, true, null);
        tbh.setVisible(true);

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // mengecek apakah supplier jadi menambahkan data atau tidak
        if (tbh.isUpdated()) {
            // mengupdate tabel
            this.updateTabel();
            this.tabelData.setRowSelectionInterval(this.tabelData.getRowCount() - 1, this.tabelData.getRowCount() - 1);
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // mengecek apakah ada data yang dipilih atau tidak
        if (tabelData.getSelectedRow() > -1) {
            // membuka window input supplier
            Audio.play(Audio.SOUND_INFO);
            InputSupplier tbh = new InputSupplier(null, true, this.idSelected);
            tbh.setVisible(true);

            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // mengecek apakah supplier jadi mengedit data atau tidak
            if (tbh.isUpdated()) {
                try {
                    // mengupdate tabel dan menampilkan ulang data
                    this.updateTabel();
                    this.showData();
                } catch (ParseException ex) {
                    Logger.getLogger(DataSupplier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            Message.showWarning(this, "Tidak ada data yang dipilih!!", true);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseEntered
        this.btnAdd.setIcon(Gambar.getAktiveIcon(this.btnAdd.getIcon().toString()));
    }//GEN-LAST:event_btnAddMouseEntered

    private void btnAddMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseExited
        this.btnAdd.setIcon(Gambar.getBiasaIcon(this.btnAdd.getIcon().toString()));
    }//GEN-LAST:event_btnAddMouseExited

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        this.btnEdit.setIcon(Gambar.getAktiveIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        this.btnEdit.setIcon(Gambar.getBiasaIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseExited

    private void btnDelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelMouseEntered
        this.btnDel.setIcon(Gambar.getAktiveIcon(this.btnDel.getIcon().toString()));
    }//GEN-LAST:event_btnDelMouseEntered

    private void btnDelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelMouseExited
        this.btnDel.setIcon(Gambar.getBiasaIcon(this.btnDel.getIcon().toString()));
    }//GEN-LAST:event_btnDelMouseExited

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // menampilkan data supplier
            this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow(), 0).toString();
            this.showData();
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(DataSupplier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabelDataMouseClicked

    private void tabelDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyPressed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (this.tabelData.getSelectedRow() >= 1) {
                try {
                    this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() - 1, 0).toString();
                    this.showData();
                } catch (ParseException ex) {
                    Logger.getLogger(DataSupplier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (this.tabelData.getSelectedRow() < (this.tabelData.getRowCount() - 1)) {
                try {
                    this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() + 1, 0).toString();
                    this.showData();
                } catch (ParseException ex) {
                    Logger.getLogger(DataSupplier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataKeyPressed

    private void valNoTelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valNoTelpMouseClicked
//
    }//GEN-LAST:event_valNoTelpMouseClicked

    private void valNoTelpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valNoTelpMouseEntered
//
    }//GEN-LAST:event_valNoTelpMouseEntered

    private void valNoTelpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valNoTelpMouseExited

    }//GEN-LAST:event_valNoTelpMouseExited

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_supplier LIKE '%" + key + "%' OR nama_supplier LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyTyped

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_supplier LIKE '%" + key + "%' OR nama_supplier LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnEdit;
    private javax.swing.JTextField inpCari;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCari;
    private javax.swing.JTable tabelData;
    private javax.swing.JLabel valAlamat;
    private javax.swing.JLabel valBrgSupplier;
    private javax.swing.JLabel valIDSupplier;
    private javax.swing.JLabel valLast;
    private javax.swing.JLabel valNamaSupplier;
    private javax.swing.JLabel valNoTelp;
    private javax.swing.JLabel valUang;
    // End of variables declaration//GEN-END:variables
}

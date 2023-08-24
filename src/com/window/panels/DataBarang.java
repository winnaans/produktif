package com.window.panels;

import com.data.db.Database;
import com.data.db.DatabaseTables;
import com.manage.Message;
import com.manage.Text;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.manage.Barang;
import com.manage.Waktu;
import com.window.MainWindow;
import com.window.dialogs.InputBarang;
import java.awt.Color;
import java.awt.Cursor;
import java.sql.SQLException;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.manage.Barcode;
/**
 *
 * @author Amirzan Fikri
 */
public class DataBarang extends javax.swing.JPanel {
    private final Waktu waktu = new Waktu();
    private final Database db = new Database();
    private final Barang barang = new Barang();
    private final Barcode barcode = new Barcode();
    private int tahun, bulan;
    private final Text text = new Text();

    private String idSelected = "", keyword = "", namaBarang, jenis, stok, hargaBeli, hargaJual, ttlPenjulan, penghasilan, bar;
    private Object[][] obj;
    public DataBarang() {
        initComponents();
        this.bulan = waktu.getBulan() + 1;
        this.tahun = waktu.getTahun();
        db.startConnection();
        this.btnAdd.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnEdit.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnDel.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnDiskon.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        JLabel[] values = {
            this.valIDBarang, this.valNamaBarang, this.valJenis, this.valStok,
            this.valHargaBeli, this.valHargaJual, this.valPjln, this.valPjlnBulan, this.valPenghasilan, this.valBarcode
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
    }

    public void closeKoneksi() {
        barang.closeConnection();
        db.closeConnection();
    }
    private String gachaBulan() {
        try{
        String query = "SELECT COUNT(*) AS total FROM detail_transaksi_jual INNER JOIN transaksi_jual ON transaksi_jual.id_tr_jual = detail_transaksi_jual.id_tr_jual WHERE id_barang = '"+this.idSelected+"' AND YEAR(tanggal) ='"+this.tahun+"' AND MONTH(tanggal) = '"+this.bulan+"'";
        db.res = db.stat.executeQuery(query);
        if (db.res.next()) {
            return db.res.getString("total");
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi Kesalahan!\n\nError message : " + ex.getMessage(), ex, true);
        } catch (NullPointerException n) {
//            n.printStackTrace();
            System.out.println("errorr ");
            return "0";
        }
        return "-1";
    }

    private void showData() {
        int baris = -1;
        for (int i = 0; i < obj.length; i++) {
            if (this.obj[i][0].equals(this.idSelected)) {
                baris = i;
            }
        }
        // mendapatkan data
        this.namaBarang = text.toCapitalize(barang.getNamaBarang(this.idSelected));
        this.jenis = text.toCapitalize(barang.getJenis(this.idSelected));
        this.stok = barang.getStok(this.idSelected);
        this.hargaBeli = text.toMoneyCase(barang.getHargaBeli(this.idSelected));
        this.hargaJual = text.toMoneyCase(barang.getHargaJual(this.idSelected));
        this.ttlPenjulan = "" + this.db.getJumlahData(DatabaseTables.DETAIL_TRANSAKSI_JUAL.name(), "WHERE id_barang = '" + this.idSelected + "'");
//        this.penghasilan = text.toMoneyCase(""+this.barang.sumData(DatabaseTables.TRANSAKSI_JUAL.name(), "total_hrg", String.format("where id_barang = '%s'", this.idSelected)));
        this.penghasilan = text.toMoneyCase("" + Integer.toString((text.toIntCase(this.obj[baris][5].toString()) - text.toIntCase(this.obj[baris][4].toString())) * this.barang.sumData(DatabaseTables.DETAIL_TRANSAKSI_JUAL.name(), "jumlah", String.format("where id_barang = '%s'", this.idSelected))));
        this.bar = barang.getBarcode(this.idSelected);
        // menampilkan data
        this.valIDBarang.setText("<html><p>:&nbsp;" + this.idSelected + "</p></html>");
        this.valNamaBarang.setText("<html><p>:&nbsp;" + this.namaBarang + "</p></html>");
        this.valJenis.setText("<html><p>:&nbsp;" + this.jenis + "</p></html>");
        this.valStok.setText("<html><p>:&nbsp;" + this.stok + " Stok</p></html>");
        this.valHargaJual.setText("<html><p>:&nbsp;" + this.hargaJual + "</p></html>");
        this.valHargaBeli.setText("<html><p>:&nbsp;" + this.hargaBeli + "</p></html>");
        this.valPjln.setText("<html><p>:&nbsp;" + this.ttlPenjulan + " Penjualan</p></html>");
        this.valPjlnBulan.setText("<html><p>:&nbsp;" + gachaBulan() + " Penjualan</p></html>");
        this.valPenghasilan.setText("<html><p>:&nbsp;" + this.penghasilan + "</p></html>");
        this.valBarcode.setText("<html><p>:&nbsp;" + this.bar + "</p></html>");
    }

    private void resetData() {
        this.valIDBarang.setText("<html><p>:&nbsp;</p></html>");
        this.valNamaBarang.setText("<html><p>:&nbsp;</p></html>");
        this.valJenis.setText("<html><p>:&nbsp;</p></html>");
        this.valStok.setText("<html><p>:&nbsp;</p></html>");
        this.valHargaJual.setText("<html><p>:&nbsp;</p></html>");
        this.valHargaBeli.setText("<html><p>:&nbsp;</p></html>");
        this.valPjln.setText("<html><p>:&nbsp;</p></html>");
        this.valPjlnBulan.setText("<html><p>:&nbsp;</p></html>");
        this.valPenghasilan.setText("<html><p>:&nbsp;</p></html>");
        this.valBarcode.setText("<html><p>:&nbsp;</p></html>");
    }

    private Object[][] getData() {
        try {
//            Object obj[][];
            int rows = 0;
            String sql = "SELECT id_barang, nama_barang, jenis_barang, stok, harga_beli, harga_jual FROM barang " + keyword;
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            this.obj = new Object[barang.getJumlahData("barang", keyword)][6];
            // mengeksekusi query
            barang.res = barang.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (barang.res.next()) {
                // menyimpan data dari tabel ke object
                this.obj[rows][0] = barang.res.getString("id_barang");
                this.obj[rows][1] = barang.res.getString("nama_barang");
                this.obj[rows][2] = text.toCapitalize(barang.res.getString("jenis_barang"));
                this.obj[rows][3] = barang.res.getString("stok");
                this.obj[rows][4] = text.toMoneyCase(barang.res.getString("harga_beli"));
                this.obj[rows][5] = text.toMoneyCase(barang.res.getString("harga_jual"));
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return this.obj;
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private void updateTabel() {
        this.tabelData.setModel(new javax.swing.table.DefaultTableModel(
                getData(),
                new String[]{
                    "ID Barang", "Nama Barang", "Jenis Barang", "Stok", "Harga Beli", "Harga Jual"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        valIDBarang = new javax.swing.JLabel();
        valNamaBarang = new javax.swing.JLabel();
        valJenis = new javax.swing.JLabel();
        valStok = new javax.swing.JLabel();
        valHargaJual = new javax.swing.JLabel();
        valPenghasilan = new javax.swing.JLabel();
        valPjln = new javax.swing.JLabel();
        valPjlnBulan = new javax.swing.JLabel();
        valHargaBeli = new javax.swing.JLabel();
        valBarcode = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnDiskon = new javax.swing.JButton();
        btnEdit = new javax.swing.JToggleButton();
        btnDel = new javax.swing.JToggleButton();
        inpCari = new javax.swing.JTextField();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(957, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(valIDBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 87, 250, 33));
        add(valNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 138, 250, 33));
        valNamaBarang.getAccessibleContext().setAccessibleName(":");

        add(valJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 190, 250, 33));
        add(valStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 240, 250, 33));
        add(valHargaJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 343, 250, 33));
        add(valPenghasilan, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 497, 250, 33));
        add(valPjln, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 394, 250, 33));

        valPjlnBulan.setName(""); // NOI18N
        add(valPjlnBulan, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 445, 250, 34));
        add(valHargaBeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 292, 250, 33));
        add(valBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 548, 250, 33));

        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Barang", "Nama Barang", "Jenis", "Stok"
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
                tablDataKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tabelData);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, 580, 490));

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
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 630, -1, -1));

        btnDiskon.setBackground(new java.awt.Color(41, 180, 50));
        btnDiskon.setForeground(new java.awt.Color(255, 255, 255));
        btnDiskon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-diskon-075.png"))); // NOI18N
        btnDiskon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnDiskon.setOpaque(false);
        btnDiskon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDiskonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDiskonMouseExited(evt);
            }
        });
        btnDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiskonActionPerformed(evt);
            }
        });
        add(btnDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(645, 630, -1, -1));

        btnEdit.setBackground(new java.awt.Color(34, 119, 237));
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-edit-075.png"))); // NOI18N
        btnEdit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnEdit.setMaximumSize(new java.awt.Dimension(109, 25));
        btnEdit.setMinimumSize(new java.awt.Dimension(109, 25));
        btnEdit.setOpaque(false);
        btnEdit.setPreferredSize(new java.awt.Dimension(109, 25));
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
        add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(257, 630, 154, 50));

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
        add(btnDel, new org.netbeans.lib.awtextra.AbsoluteConstraints(452, 630, 154, 50));

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
        add(inpCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 70, 190, 30));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-dataBarang.png"))); // NOI18N
        background.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents


    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // menampilkan data barang
        this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow(), 0).toString();
        this.showData();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataMouseClicked

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // mengecek apakah ada data yang dipilih atau tidak
        if (tabelData.getSelectedRow() > -1) {
            // membuka window input pembeli
            Audio.play(Audio.SOUND_INFO);
            InputBarang tbh = new InputBarang(null, true, this.idSelected);
            tbh.setVisible(true);

            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // mengecek apakah barang jadi mengedit data atau tidak
            if (tbh.isUpdated()) {
                // mengupdate tabel dan menampilkan ulang data
                this.updateTabel();
                this.showData();
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            Message.showWarning(this, "Tidak ada data yang dipilih!!", true);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void tablDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablDataKeyPressed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (this.tabelData.getSelectedRow() >= 1) {
                this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() - 1, 0).toString();
                this.showData();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (this.tabelData.getSelectedRow() < (this.tabelData.getRowCount() - 1)) {
                this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() + 1, 0).toString();
                this.showData();
            }
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tablDataKeyPressed

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        this.btnEdit.setIcon(Gambar.getAktiveIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        this.btnEdit.setIcon(Gambar.getBiasaIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseExited

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyTyped

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyReleased

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        int status;
        boolean delete;
        // mengecek apakah ada data yang dipilih atau tidak
        if (tabelData.getSelectedRow() > -1) {
            // membuka confirm dialog untuk menghapus data
            Audio.play(Audio.SOUND_INFO);
            status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus '" + this.namaBarang + "' ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            // mengecek pilihan dari barang
            switch (status) {
                // jika yes maka data akan dihapus
                case JOptionPane.YES_OPTION:
                    // menghapus data pembeli
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    delete = this.barang.deleteBarang(this.idSelected);
                    // mengecek apakah data pembeli berhasil terhapus atau tidak
                    if (delete) {
                        Message.showInformation(this, "Data berhasil dihapus!");
                        // mengupdate tabel
                        this.updateTabel();
                        this.resetData();
                        //hapus file barcode
                        if(this.barcode.isExistBarcode(this.bar)){
                            this.barcode.deleteBarcode(this.bar);
                        }
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

    private void btnDelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelMouseExited
        this.btnDel.setIcon(Gambar.getBiasaIcon(this.btnDel.getIcon().toString()));
    }//GEN-LAST:event_btnDelMouseExited

    private void btnDelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelMouseEntered
        this.btnDel.setIcon(Gambar.getAktiveIcon(this.btnDel.getIcon().toString()));
    }//GEN-LAST:event_btnDelMouseEntered

    private void inpCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariActionPerformed
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // membuka window input pembeli
        Audio.play(Audio.SOUND_INFO);
        InputBarang tbh = new InputBarang(null, true, null);
        tbh.setVisible(true);

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // mengecek apakah barang jadi menambahkan data atau tidak
        if (tbh.isUpdated()) {
            // mengupdate tabel
            this.updateTabel();
            this.tabelData.setRowSelectionInterval(this.tabelData.getRowCount() - 1, this.tabelData.getRowCount() - 1);
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnAddMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseExited
        this.btnAdd.setIcon(Gambar.getBiasaIcon(this.btnAdd.getIcon().toString()));
    }//GEN-LAST:event_btnAddMouseExited

    private void btnAddMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseEntered
        this.btnAdd.setIcon(Gambar.getAktiveIcon(this.btnAdd.getIcon().toString()));
    }//GEN-LAST:event_btnAddMouseEntered

    private void btnDiskonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDiskonMouseEntered
        this.btnDiskon.setIcon(Gambar.getAktiveIcon(this.btnDiskon.getIcon().toString()));
    }//GEN-LAST:event_btnDiskonMouseEntered

    private void btnDiskonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDiskonMouseExited
        this.btnDiskon.setIcon(Gambar.getBiasaIcon(this.btnDiskon.getIcon().toString()));
    }//GEN-LAST:event_btnDiskonMouseExited

    private void dataDiskon(JPanel pnl){
        //jika btn diskon di window barang di klik
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//        .setTitle("Data Diskon");
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
    
    private void btnDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiskonActionPerformed
            DataDiskon pnl = new DataDiskon();
            this.dataDiskon(pnl);
    }//GEN-LAST:event_btnDiskonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnAdd;
    private javax.swing.JToggleButton btnDel;
    private javax.swing.JButton btnDiskon;
    private javax.swing.JToggleButton btnEdit;
    private javax.swing.JTextField inpCari;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tabelData;
    private javax.swing.JLabel valBarcode;
    private javax.swing.JLabel valHargaBeli;
    private javax.swing.JLabel valHargaJual;
    private javax.swing.JLabel valIDBarang;
    private javax.swing.JLabel valJenis;
    private javax.swing.JLabel valNamaBarang;
    private javax.swing.JLabel valPenghasilan;
    private javax.swing.JLabel valPjln;
    private javax.swing.JLabel valPjlnBulan;
    private javax.swing.JLabel valStok;
    // End of variables declaration//GEN-END:variables
}

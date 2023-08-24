package com.window.panels;

import com.window.MainWindow;
import com.data.db.Database;
import com.data.db.DatabaseTables;
import com.manage.Message;
import com.manage.Text;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.manage.Diskon;
import com.manage.Waktu;
import com.window.dialogs.InputBarang;
import com.window.dialogs.InputDiskon;
import java.awt.Color;
import java.awt.Cursor;
import java.sql.SQLException;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Amirzan Fikri P
 */
public class DataDiskon extends javax.swing.JPanel {

    private final Waktu waktu = new Waktu();
    private final Database db = new Database();
    private final Diskon diskon = new Diskon();
    private int tahun, bulan;
    private final Text text = new Text();
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat tanggalMilis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String idSelected = "", keyword = "", namaDiskon, jumlahDiskon, minimalPembelian, tanggalAwal, tanggalAkhir;
    private Object[][] obj;

    public DataDiskon() {
        initComponents();
        db.startConnection();
        this.btnKembali.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnAdd.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnEdit.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnDel.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
        JLabel[] values = {
            this.valIDDiskon, this.valNamaDiskon, this.valJmlDiskon, this.valMinimal,
            this.valTanggalAwal, this.valTanggalAkhir
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
        diskon.closeConnection();
        db.closeConnection();
    }

    private void showData() {
        try {
            String tanggal1, tanggal2;
            int baris = -1, hariAwal = 0, bulanAwal = 0, tahunAwal = 0, hariAkhir = 0, bulanAkhir = 0, tahunAkhir = 0;
            for (int i = 0; i < obj.length; i++) {
                if (this.obj[i][0].equals(this.idSelected)) {
                    baris = i;
                }
            }
            // mendapatkan data
            this.namaDiskon = text.toCapitalize(diskon.getNamaDiskon(this.idSelected));
            this.jumlahDiskon = text.toMoneyCase(diskon.getJumlah(this.idSelected));
            this.minimalPembelian = text.toMoneyCase(diskon.getMinimal(this.idSelected));
            this.tanggalAwal = text.toCapitalize(diskon.getTanggalAwal(this.idSelected));
            this.tanggalAkhir = text.toCapitalize(diskon.getTanggalAkhir(this.idSelected));
            Date a = date1.parse(this.tanggalAwal);
            Date b = date1.parse(this.tanggalAkhir);
            tanggal1 = date.format(a);
            tanggal2 = date.format(b);
            //mendapatkan hari dari variabel tanggal
            hariAwal = Integer.parseInt(tanggal1.substring(0, 2));
            hariAkhir = Integer.parseInt(tanggal2.substring(0, 2));
            //mendapatkan bulan dari variabel tanggal
            bulanAwal = Integer.parseInt(tanggal1.substring(3, 5));
            bulanAkhir = Integer.parseInt(tanggal2.substring(3, 5));
            //mendapatkan tahun dari variabel tanggal
            tahunAwal = Integer.parseInt(tanggal1.substring(6));
            tahunAkhir = Integer.parseInt(tanggal2.substring(6));

            // menampilkan data
            this.valIDDiskon.setText("<html><p>:&nbsp;" + idSelected + "</p></html>");
            this.valNamaDiskon.setText("<html><p>:&nbsp;" + namaDiskon + "</p></html>");
            this.valJmlDiskon.setText("<html><p>:&nbsp;" + jumlahDiskon + "</p></html>");
            this.valMinimal.setText("<html><p>:&nbsp;" + minimalPembelian + "</p></html>");
            this.valTanggalAwal.setText("<html><p>:&nbsp;" + hariAwal + "-" + this.waktu.getNamaBulan(bulanAwal - 1) + "-" + tahunAwal + "</p></html>");
            this.valTanggalAkhir.setText("<html><p>:&nbsp;" + hariAkhir + "-" + this.waktu.getNamaBulan(bulanAkhir - 1) + "-" + tahunAkhir + "</p></html>");
        } catch (ParseException ex) {
            Logger.getLogger(DataDiskon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetData() {
        this.valIDDiskon.setText("<html><p>:&nbsp;</p></html>");
        this.valNamaDiskon.setText("<html><p>:&nbsp;</p></html>");
        this.valJmlDiskon.setText("<html><p>:&nbsp;</p></html>");
        this.valMinimal.setText("<html><p>:&nbsp;</p></html>");
        this.valTanggalAwal.setText("<html><p>:&nbsp;</p></html>");
        this.valTanggalAkhir.setText("<html><p>:&nbsp;</p></html>");
    }

    private Object[][] getData() {
        try {
            String tanggalAwal, tanggalAkhir;
//            Object obj[][];
            int rows = 0;
            String sql = "SELECT id_diskon, nama_diskon, jumlah_diskon, minimal_harga, tanggal_awal, tanggal_akhir FROM diskon " + keyword;
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            this.obj = new Object[diskon.getJumlahData("diskon", keyword)][6];
            // mengeksekusi query
            diskon.res = diskon.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (diskon.res.next()) {
                // menyimpan data dari tabel ke object
                this.obj[rows][0] = diskon.res.getString("id_diskon");
                this.obj[rows][1] = diskon.res.getString("nama_diskon");
                this.obj[rows][2] = text.toMoneyCase(diskon.res.getString("jumlah_diskon"));
                this.obj[rows][3] = text.toMoneyCase(diskon.res.getString("minimal_harga"));
//                this.obj[rows][4] = diskon.res.getString("tanggal_awal");
//                this.obj[rows][5] = diskon.res.getString("tanggal_akhir");
                this.obj[rows][4] = date.format(date1.parse(diskon.res.getString("tanggal_awal")));
                this.obj[rows][5] = date.format(date1.parse(diskon.res.getString("tanggal_akhir")));
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return this.obj;
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        } catch (ParseException ex) {
            Logger.getLogger(DataDiskon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void updateTabel() {
        this.tabelData.setModel(new javax.swing.table.DefaultTableModel(
                getData(),
                new String[]{
                    "ID Diskon", "Nama Diskon", "Jumlah Diskon", "Minimal Pembelian", "Tanggal Awal", "Tanggal Akhir"
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

        jScrollPane2 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        valIDDiskon = new javax.swing.JLabel();
        valNamaDiskon = new javax.swing.JLabel();
        valJmlDiskon = new javax.swing.JLabel();
        valMinimal = new javax.swing.JLabel();
        valTanggalAwal = new javax.swing.JLabel();
        valTanggalAkhir = new javax.swing.JLabel();
        inpCari = new javax.swing.JTextField();
        btnKembali = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JToggleButton();
        btnDel = new javax.swing.JToggleButton();
        background = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Diskon", "Nama Diskon", "Jumlah Diskon", "Minimal Diskon", "Tanggal Awal", "Tanggal Akhir"
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
                tabelDatatablDataKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tabelData);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, 580, 505));

        valIDDiskon.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valIDDiskon.setForeground(new java.awt.Color(0, 0, 0));
        valIDDiskon.setText(":");
        add(valIDDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 87, 240, 32));

        valNamaDiskon.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valNamaDiskon.setForeground(new java.awt.Color(0, 0, 0));
        valNamaDiskon.setText(":");
        add(valNamaDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 144, 240, 34));

        valJmlDiskon.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valJmlDiskon.setForeground(new java.awt.Color(0, 0, 0));
        valJmlDiskon.setText(":");
        add(valJmlDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 204, 240, 33));

        valMinimal.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valMinimal.setForeground(new java.awt.Color(0, 0, 0));
        valMinimal.setText(":");
        add(valMinimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 262, 240, 33));

        valTanggalAwal.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valTanggalAwal.setForeground(new java.awt.Color(0, 0, 0));
        valTanggalAwal.setText(":");
        add(valTanggalAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 320, 240, 33));

        valTanggalAkhir.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        valTanggalAkhir.setForeground(new java.awt.Color(0, 0, 0));
        valTanggalAkhir.setText(":");
        add(valTanggalAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 379, 240, 33));

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
        add(inpCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 68, 190, 32));

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
        add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 630, 151, 50));

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
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 630, 151, 50));

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
        add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(453, 630, 151, 50));

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
        add(btnDel, new org.netbeans.lib.awtextra.AbsoluteConstraints(646, 630, 151, 50));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-dataDiskon.png"))); // NOI18N
        background.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // menampilkan data diskon
        this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow(), 0).toString();
        this.showData();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataMouseClicked

    private void tabelDatatablDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDatatablDataKeyPressed
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
    }//GEN-LAST:event_tabelDatatablDataKeyPressed

    private void inpCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariActionPerformed
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_diskon LIKE '%" + key + "%' OR nama_diskon LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariActionPerformed

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_diskon LIKE '%" + key + "%' OR nama_diskon LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyReleased

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_diskon LIKE '%" + key + "%' OR nama_diskon LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyTyped

    private void btnAddMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseEntered
        this.btnAdd.setIcon(Gambar.getAktiveIcon(this.btnAdd.getIcon().toString()));
    }//GEN-LAST:event_btnAddMouseEntered

    private void btnAddMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseExited
        this.btnAdd.setIcon(Gambar.getBiasaIcon(this.btnAdd.getIcon().toString()));
    }//GEN-LAST:event_btnAddMouseExited

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        try {
            // membuka window input pembeli
            Audio.play(Audio.SOUND_INFO);
            InputDiskon tbh = new InputDiskon(null, true, null);
            tbh.setVisible(true);
            
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // mengecek apakah diskon jadi menambahkan data atau tidak
            if (tbh.isUpdated()) {
                // mengupdate tabel
                this.updateTabel();
                this.tabelData.setRowSelectionInterval(this.tabelData.getRowCount() - 1, this.tabelData.getRowCount() - 1);
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (ParseException ex) {
            Logger.getLogger(DataDiskon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        this.btnEdit.setIcon(Gambar.getAktiveIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        this.btnEdit.setIcon(Gambar.getBiasaIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseExited

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // mengecek apakah ada data yang dipilih atau tidak
        if (tabelData.getSelectedRow() > -1) {
            try {
                // membuka window input pembeli
                Audio.play(Audio.SOUND_INFO);
                InputDiskon tbh = new InputDiskon(null, true, this.idSelected);
                tbh.setVisible(true);
                
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                // mengecek apakah diskon jadi mengedit data atau tidak
                if (tbh.isUpdated()) {
                    // mengupdate tabel dan menampilkan ulang data
                    this.updateTabel();
                    this.showData();
                }
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } catch (ParseException ex) {
                Logger.getLogger(DataDiskon.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Message.showWarning(this, "Tidak ada data yang dipilih!!", true);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelMouseEntered
        this.btnDel.setIcon(Gambar.getAktiveIcon(this.btnDel.getIcon().toString()));
    }//GEN-LAST:event_btnDelMouseEntered

    private void btnDelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelMouseExited
        this.btnDel.setIcon(Gambar.getBiasaIcon(this.btnDel.getIcon().toString()));
    }//GEN-LAST:event_btnDelMouseExited

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        int status;
        boolean delete;
        // mengecek apakah ada data yang dipilih atau tidak
        if (tabelData.getSelectedRow() > -1) {
            // membuka confirm dialog untuk menghapus data
            Audio.play(Audio.SOUND_INFO);
            status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus '" + this.namaDiskon + "' ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            // mengecek pilihan dari diskon
            switch (status) {
                // jika yes maka data akan dihapus
                case JOptionPane.YES_OPTION:
                    // menghapus data pembeli
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    delete = this.diskon.deleteDiskon(this.idSelected);
                    // mengecek apakah data pembeli berhasil terhapus atau tidak
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

    private void btnKembaliMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseEntered
        this.btnKembali.setIcon(Gambar.getAktiveIcon(this.btnKembali.getIcon().toString()));
    }//GEN-LAST:event_btnKembaliMouseEntered

    private void btnKembaliMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseExited
        this.btnKembali.setIcon(Gambar.getBiasaIcon(this.btnKembali.getIcon().toString()));
    }//GEN-LAST:event_btnKembaliMouseExited

    private void dataBarang(JPanel pnl) {
        //jika btn diskon di window barang di klik
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//        MainWindow.setTitle("Data Diskon");
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
        DataBarang pnl = new DataBarang();
        this.dataBarang(pnl);
    }//GEN-LAST:event_btnKembaliActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnAdd;
    private javax.swing.JToggleButton btnDel;
    private javax.swing.JToggleButton btnEdit;
    private javax.swing.JButton btnKembali;
    private javax.swing.JTextField inpCari;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tabelData;
    private javax.swing.JLabel valIDDiskon;
    private javax.swing.JLabel valJmlDiskon;
    private javax.swing.JLabel valMinimal;
    private javax.swing.JLabel valNamaDiskon;
    private javax.swing.JLabel valTanggalAkhir;
    private javax.swing.JLabel valTanggalAwal;
    // End of variables declaration//GEN-END:variables
}

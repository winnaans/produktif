package com.window.panels;

import com.data.db.DatabaseTables;
import com.manage.Message;
import com.manage.Text;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
//import com.users.Karyawan;
import com.users.Users;
import com.window.dialogs.InputKaryawan;
import com.window.dialogs.InputSaldo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Amirzan Fikri
 */
public class DataKaryawan extends javax.swing.JPanel {
    private final Users user = new Users();
    private final Text text = new Text();
    private String idSelected = "", keyword = "", namaPetugas, noTelp, alamat, level, rfid;
    public DataKaryawan() {
        initComponents();

        this.btnTambah.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnEdit.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnHapus.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnSaldo.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        JLabel[] values = {
            this.valIDKaryawan, this.valNamaKaryawan, this.valNoTelp, this.valAlamat,
            this.valLevel,this.valRFID
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
        user.closeConnection();
    }

    private Object[][] getData() {
        try {
            user.startConnection();
            Object[][] obj;
            int rows = 0;
            String sql = "SELECT id_karyawan, nama_karyawan, level FROM users " + this.keyword + " ORDER BY id_karyawan";
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
//            System.out.println(sql);
            obj = new Object[user.getJumlahData(DatabaseTables.USERS.name(), this.keyword)][3];
            // mengeksekusi query
            user.res = user.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (user.res.next()) {
                // menyimpan data dari tabel ke object
                obj[rows][0] = user.res.getString("id_karyawan");
                obj[rows][1] = user.res.getString("nama_karyawan");
                obj[rows][2] = user.res.getString("level");
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return obj;
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n", ex, true);
        }
        return null;
    }

    private void updateTabel() {
        this.tabelData.setModel(new javax.swing.table.DefaultTableModel(
                getData(),
                new String[]{
                    "ID Karyawan", "Nama Karyawan", "Level"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    private void showData() {
        // mendapatkan data
        this.namaPetugas = user.getNamaKaryawan(this.idSelected);
        this.noTelp = text.toTelephoneCase(user.getNoTelpKaryawan(this.idSelected));
        this.alamat = user.getAlamatKaryawan(this.idSelected);
        this.level = text.toCapitalize(user.getLevel1(this.idSelected).name());
        this.rfid = user.getRfid(this.idSelected);
        // menampilkan data
        this.valIDKaryawan.setText("<html><p>:&nbsp;" + this.idSelected + "</p></html>");
        this.valNamaKaryawan.setText("<html><p>:&nbsp;" + this.namaPetugas + "</p></html>");
        this.valNoTelp.setText("<html><p style=\"text-decoration:underline; color:rgb(0,0,0);\">:&nbsp;" + this.noTelp + "</p></html>");
        this.valAlamat.setText("<html><p>:&nbsp;" + this.alamat + "</p></html>");
        this.valLevel.setText("<html><p>:&nbsp;" + this.level + "</p></html>");
        this.valRFID.setText("<html><p>:&nbsp;" + this.rfid + "</p></html>");
    }

    private void resetData() {
        // menghapus data data
        this.valIDKaryawan.setText("<html><p>:&nbsp;</p></html>");
        this.valNamaKaryawan.setText("<html><p>:&nbsp;</p></html>");
        this.valAlamat.setText("<html><p>:&nbsp;</p></html>");
        this.valLevel.setText("<html><p>:&nbsp;</p></html>");
        this.valRFID.setText("<html><p>:&nbsp;</p></html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        valIDKaryawan = new javax.swing.JLabel();
        valNamaKaryawan = new javax.swing.JLabel();
        valNoTelp = new javax.swing.JLabel();
        valAlamat = new javax.swing.JLabel();
        inpCari = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnSaldo = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        valLevel = new javax.swing.JLabel();
        valRFID = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(957, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        valIDKaryawan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valIDKaryawan.setForeground(new java.awt.Color(0, 0, 0));
        valIDKaryawan.setText(":");
        valIDKaryawan.setName(""); // NOI18N
        add(valIDKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 130, 320, 45));

        valNamaKaryawan.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valNamaKaryawan.setForeground(new java.awt.Color(0, 0, 0));
        valNamaKaryawan.setText(":");
        add(valNamaKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 200, 325, 45));

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
        add(valNoTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 270, 325, 45));

        valAlamat.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valAlamat.setForeground(new java.awt.Color(0, 0, 0));
        valAlamat.setText(":");
        add(valAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 340, 325, 45));

        inpCari.setBackground(new java.awt.Color(0, 0, 0));
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
        add(inpCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 80, 180, 23));

        btnTambah.setBackground(new java.awt.Color(41, 180, 50));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambah-075.png"))); // NOI18N
        btnTambah.setToolTipText("");
        btnTambah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnTambah.setOpaque(false);
        btnTambah.addMouseListener(new java.awt.event.MouseAdapter() {
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
        add(btnTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 636, 154, 50));

        btnEdit.setBackground(new java.awt.Color(34, 119, 237));
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-edit-075.png"))); // NOI18N
        btnEdit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnEdit.setOpaque(false);
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

        btnHapus.setBackground(new java.awt.Color(220, 41, 41));
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-hapus-075.png"))); // NOI18N
        btnHapus.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnHapus.setOpaque(false);
        btnHapus.addMouseListener(new java.awt.event.MouseAdapter() {
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
        add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 636, 154, 50));

        btnSaldo.setBackground(new java.awt.Color(41, 180, 50));
        btnSaldo.setForeground(new java.awt.Color(255, 255, 255));
        btnSaldo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-saldo-075.png"))); // NOI18N
        btnSaldo.setToolTipText("");
        btnSaldo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSaldo.setOpaque(false);
        btnSaldo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSaldoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSaldoMouseExited(evt);
            }
        });
        btnSaldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaldoActionPerformed(evt);
            }
        });
        add(btnSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 636, 188, 49));

        tabelData.setBackground(new java.awt.Color(255, 255, 255));
        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Karyawan", "Nama Karyawan", "Level"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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
        jScrollPane2.setViewportView(tabelData);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 100, 550, 510));

        valLevel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valLevel.setForeground(new java.awt.Color(0, 0, 0));
        valLevel.setText(":");
        add(valLevel, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 410, 325, 45));

        valRFID.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valRFID.setForeground(new java.awt.Color(0, 0, 0));
        valRFID.setText(":");
        add(valRFID, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 480, 325, 45));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-dataKaryawan.png"))); // NOI18N
        background.setName(""); // NOI18N
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int status;
        boolean delete;

        // mengecek apakah user memiliki level admin
        if (user.isAdmin()) {
            // mengecek apakah ada data yang dipilih atau tidak
            if (tabelData.getSelectedRow() > -1) {
                //mengecek apakah id user yang dipilih sama dengan id user yang sekarang login
                if (this.idSelected.equals(this.user.getIdKaryawan(this.user.getCurrentLogin()))) {
                    Message.showWarning(this, "Anda tidak bisa menghapus data Anda sendiri!");
                } else {
                    // membuka confirm dialog untuk menghapus data
                    Audio.play(Audio.SOUND_INFO);
                    status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus '" + this.namaPetugas + "' ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                    // mengecek pilihan dari user
                    switch (status) {
                        // jika yes maka data akan dihapus
                        case JOptionPane.YES_OPTION:
                            // menghapus data user
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            delete = this.user.deleteKaryawan(this.idSelected);
                            // mengecek apakah data user berhasil terhapus atau tidak
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
                }
            } else {
                Message.showWarning(this, "Tidak ada data yang dipilih!!", true);
            }
        } else {
            Message.showWarning(this, "Access Denied!\nAnda bukan Admin!");
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // mengecek apakah user memiliki level admin
        if (user.isAdmin()) {
            // membuka window input user
            Audio.play(Audio.SOUND_INFO);
            InputKaryawan tbh = new InputKaryawan(null, true, null);
            tbh.setVisible(true);

            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // mengecek apakah user jadi menambahkan data atau tidak
            if (tbh.isUpdated()) {
                // mengupdate tabel
                this.updateTabel();
                this.tabelData.setRowSelectionInterval(this.tabelData.getRowCount() - 1, this.tabelData.getRowCount() - 1);
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            Message.showWarning(this, "Access Denied!\nAnda bukan Admin!");
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // mengecek apakah user memiliki level admin
        if (user.isAdmin()) {
            // mengecek apakah ada data yang dipilih atau tidak
            if (tabelData.getSelectedRow() > -1) {
                // membuka window input user
                Audio.play(Audio.SOUND_INFO);
                InputKaryawan tbh = new InputKaryawan(null, true, this.idSelected);
                tbh.setVisible(true);

                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                // mengecek apakah user jadi mengedit data atau tidak
                if (tbh.isUpdated()) {
                    // mengupdate tabel dan menampilkan ulang data
                    this.updateTabel();
                    this.showData();
                }
            } else {
                Message.showWarning(this, "Tidak ada data yang dipilih!!", true);
            }
        } else {
            Message.showWarning(this, "Access Denied!\nAnda bukan Admin!");
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnTambahMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseEntered
        this.btnTambah.setIcon(Gambar.getAktiveIcon(this.btnTambah.getIcon().toString()));
    }//GEN-LAST:event_btnTambahMouseEntered

    private void btnTambahMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseExited
        this.btnTambah.setIcon(Gambar.getBiasaIcon(this.btnTambah.getIcon().toString()));
    }//GEN-LAST:event_btnTambahMouseExited

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        this.btnEdit.setIcon(Gambar.getAktiveIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        this.btnEdit.setIcon(Gambar.getBiasaIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseExited

    private void btnHapusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseEntered
        this.btnHapus.setIcon(Gambar.getAktiveIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseEntered

    private void btnHapusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseExited
        this.btnHapus.setIcon(Gambar.getBiasaIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseExited

    private void valNoTelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valNoTelpMouseClicked
//
    }//GEN-LAST:event_valNoTelpMouseClicked

    private void valNoTelpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valNoTelpMouseEntered
//
    }//GEN-LAST:event_valNoTelpMouseEntered

    private void valNoTelpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valNoTelpMouseExited
//
    }//GEN-LAST:event_valNoTelpMouseExited

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // menampilkan data supplier
        this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow(), 0).toString();
        this.showData();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataMouseClicked

    private void tabelDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyPressed
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
    }//GEN-LAST:event_tabelDataKeyPressed

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        String key = this.inpCari.getText();
        this.keyword = "WHERE user.id_user LIKE '%" + key + "%' OR user.nama_user LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyTyped

    private void inpCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariActionPerformed
        String key = this.inpCari.getText();
        this.keyword = "WHERE user.id_user LIKE '%" + key + "%' OR user.nama_user LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariActionPerformed

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        String key = this.inpCari.getText();
        this.keyword = "WHERE user.id_user LIKE '%" + key + "%' OR user.nama_user LIKE '%" + key + "%'";
        this.updateTabel();
    }//GEN-LAST:event_inpCariKeyReleased

    private void btnSaldoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSaldoMouseEntered
        this.btnSaldo.setIcon(Gambar.getAktiveIcon(this.btnSaldo.getIcon().toString()));
    }//GEN-LAST:event_btnSaldoMouseEntered

    private void btnSaldoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSaldoMouseExited
        this.btnSaldo.setIcon(Gambar.getBiasaIcon(this.btnSaldo.getIcon().toString()));
    }//GEN-LAST:event_btnSaldoMouseExited

    private void btnSaldoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaldoActionPerformed
        // mengecek apakah user memiliki level admin
        if (user.isAdmin()) {
            // membuka window input user
            Audio.play(Audio.SOUND_INFO);
            InputSaldo tbh = new InputSaldo(null, true);
            tbh.setVisible(true);
        } else {
            Message.showWarning(this, "Access Denied!\nAnda bukan Admin!");
        }
    }//GEN-LAST:event_btnSaldoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSaldo;
    private javax.swing.JButton btnTambah;
    private javax.swing.JTextField inpCari;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tabelData;
    private javax.swing.JLabel valAlamat;
    private javax.swing.JLabel valIDKaryawan;
    private javax.swing.JLabel valLevel;
    private javax.swing.JLabel valNamaKaryawan;
    private javax.swing.JLabel valNoTelp;
    private javax.swing.JLabel valRFID;
    // End of variables declaration//GEN-END:variables
}

package com.window.dialogs;

import com.manage.Message;
import com.media.Gambar;
import com.users.Supplier;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import javax.swing.ImageIcon;

/**
 *
 * @author Achmad Baihaqi
 */
public class InputSupplier extends javax.swing.JDialog {

    private final Supplier supplier = new Supplier();

    public int option;

    public static final int ADD_OPTION = 1, EDIT_OPTION = 2;

    private final String idSupplier;

    private String nama, noTelp, alamat, newNama, newNoTelp, newAlamat;

    private boolean isUpdated = false;

    /**
     * Creates new form TambahSupplier
     *
     * @param parent
     * @param modal
     * @param idSupplier
     */
    public InputSupplier(Frame parent, boolean modal, String idSupplier) {
        super(parent, modal);
        initComponents();
        this.setIconImage(Gambar.getWindowIcon());
        if (idSupplier == null) {
            // menyetting window untuk tambah data
            this.option = 1;
            this.idSupplier = this.supplier.createID();
            this.setTitle("Tambah Data Supplier");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-tambahSupplier-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-tambahS-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);
        } else {
            // menyetting window untuk edit data
            this.option = 2;
            this.idSupplier = idSupplier;
            this.setTitle("Edit Data Supplier");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-editSupplier-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-simpanS-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);

            // mendapatkan data-data supplier
            this.nama = this.supplier.getNama(this.idSupplier);
            this.alamat = this.supplier.getAlamat(this.idSupplier);
            this.noTelp = this.supplier.getNoTelp(this.idSupplier);

            // menampilkan data-data supplier ke input text
            this.inpNama.setText(this.nama);
            this.inpNoTelp.setText(this.noTelp);
            this.inpAlamat.setText(this.alamat);
        }

        this.setLocationRelativeTo(null);

        this.inpId.setText(this.idSupplier);
        this.btnSimpan.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnCancel.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }

    /**
     * Mengecek apakah user menekan tombol simpan / tambah atau tidak
     *
     * @return <strong>True</strong> jika user menekan tombol simpan / tambah.
     * <br>
     * <strong>False</strong> jika user menekan tombol kembali / close.
     */
    public boolean isUpdated() {
        return this.isUpdated;
    }

    /**
     * Digunakan untuk menambahkan data supplier ke Database.
     *
     */
    private void addData() {
        boolean error = false;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // mendapatkan data dari textfield
        this.nama = this.inpNama.getText();
        this.noTelp = this.inpNoTelp.getText();
        this.alamat = this.inpAlamat.getText();
        if (this.nama.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Supplier harus Di isi !");
        } else if (this.noTelp.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "No Telepon harus Di isi !");
        } else if (this.alamat.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Alamat harus Di isi !");
        }
        if (!error) {
            // menambahkan data supplier supplier ke database
            boolean save = this.supplier.addSupplier(nama, noTelp, alamat);

            // mengecek data berhasil disimpan atau belum
            if (save) {
                Message.showInformation(this, "Data berhasil disimpan!");
                this.isUpdated = true;
                this.supplier.closeConnection();
                this.dispose();
            }
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Digunakan untuk mengedit data dari supplier
     *
     */
    private void editData() {
        boolean eNama, eNoTelp, eAlamat, error = false;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // mendapakan data dari textfield
        this.newNama = this.inpNama.getText();
        this.newNoTelp = this.inpNoTelp.getText();
        this.newAlamat = this.inpAlamat.getText();
        if (this.newNama.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Supplier harus Di isi !");
        } else if (this.newNoTelp.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "No Telepon harus Di isi !");
        } else if (this.newAlamat.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Alamat harus Di isi !");
        }
        if (!error) {
            // validasi data
            if (this.supplier.validateAddSupplier(this.idSupplier, this.newNama, this.newNoTelp, this.newAlamat)) {
                // mengedit data
                eNama = this.supplier.setNama(this.idSupplier, this.newNama);
                eNoTelp = this.supplier.setNoTelp(this.idSupplier, this.newNoTelp);
                eAlamat = this.supplier.setAlamat(this.idSupplier, this.newAlamat);

                // mengecek apa data berhasil disave atau tidak
                if (eNama && eNoTelp && eAlamat) {
                    Message.showInformation(this, "Data berhasil diedit!");
                    this.isUpdated = true;
                    this.supplier.closeConnection();
                    this.dispose();
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        inpNoTelp = new javax.swing.JTextField();
        inpAlamat = new javax.swing.JTextField();
        inpNama = new javax.swing.JTextField();
        inpId = new javax.swing.JTextField();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlMain.setBackground(new java.awt.Color(246, 247, 248));
        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSimpan.setBackground(new java.awt.Color(34, 119, 237));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambahS-075.png"))); // NOI18N
        btnSimpan.setToolTipText("");
        btnSimpan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSimpan.setOpaque(false);
        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
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
        pnlMain.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, 160, 40));

        btnCancel.setBackground(new java.awt.Color(220, 41, 41));
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-batalS-075.png"))); // NOI18N
        btnCancel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnCancel.setOpaque(false);
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancelMouseExited(evt);
            }
        });
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnlMain.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 450, 160, 40));

        inpNoTelp.setBackground(new java.awt.Color(255, 255, 255));
        inpNoTelp.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNoTelp.setForeground(new java.awt.Color(0, 0, 0));
        inpNoTelp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpNoTelp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpNoTelp.setCaretColor(new java.awt.Color(213, 8, 8));
        inpNoTelp.setOpaque(false);
        pnlMain.add(inpNoTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 295, 412, 43));

        inpAlamat.setBackground(new java.awt.Color(255, 255, 255));
        inpAlamat.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpAlamat.setForeground(new java.awt.Color(0, 0, 0));
        inpAlamat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpAlamat.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpAlamat.setCaretColor(new java.awt.Color(213, 8, 8));
        inpAlamat.setOpaque(false);
        pnlMain.add(inpAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 383, 412, 43));

        inpNama.setBackground(new java.awt.Color(255, 255, 255));
        inpNama.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNama.setForeground(new java.awt.Color(0, 0, 0));
        inpNama.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpNama.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpNama.setCaretColor(new java.awt.Color(213, 8, 8));
        inpNama.setOpaque(false);
        pnlMain.add(inpNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 209, 412, 43));

        inpId.setBackground(new java.awt.Color(211, 215, 224));
        inpId.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpId.setForeground(new java.awt.Color(0, 0, 0));
        inpId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpId.setText("SP001");
        inpId.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpId.setCaretColor(new java.awt.Color(230, 11, 11));
        inpId.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        inpId.setEnabled(false);
        inpId.setOpaque(false);
        inpId.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inpIdMouseClicked(evt);
            }
        });
        pnlMain.add(inpId, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 122, 412, 43));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-window-tambahSupplier-075.png"))); // NOI18N
        pnlMain.add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseEntered
        this.btnSimpan.setIcon(Gambar.getAktiveIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseEntered

    private void btnSimpanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseExited
        this.btnSimpan.setIcon(Gambar.getBiasaIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseExited

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // action button sesuai opsi tambah atau edit
        switch (option) {
            case ADD_OPTION:
                this.addData();
                break;
            case EDIT_OPTION:
                this.editData();
                break;
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCancelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseEntered
        this.btnCancel.setIcon(Gambar.getAktiveIcon(this.btnCancel.getIcon().toString()));
    }//GEN-LAST:event_btnCancelMouseEntered

    private void btnCancelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseExited
        this.btnCancel.setIcon(Gambar.getBiasaIcon(this.btnCancel.getIcon().toString()));
    }//GEN-LAST:event_btnCancelMouseExited

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        supplier.closeConnection();
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void inpIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpIdMouseClicked
        Message.showWarning(this, "ID Supplier tidak bisa diedit!");
    }//GEN-LAST:event_inpIdMouseClicked

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InputSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                InputSupplier dialog = new InputSupplier(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JTextField inpAlamat;
    private javax.swing.JTextField inpId;
    private javax.swing.JTextField inpNama;
    private javax.swing.JTextField inpNoTelp;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables
}

package com.window.dialogs;

import com.data.app.Log;
import com.manage.Message;
import com.media.Gambar;
//import com.users.Karyawan;
import com.users.UserLevels;
import com.users.Users;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import javax.swing.ImageIcon;

/**
 *
 * @author Achmad Baihaqi
 */
public class InputKaryawan extends javax.swing.JDialog {
    private boolean tambah = false, edit = false;
//    private final Karyawan user = new Karyawan();
    
    public int option;
    
    public static final int ADD_OPTION = 1, EDIT_OPTION = 2;
    
    private final String idKaryawan;
    
    private String nama, noTelp, alamat, pass, newNama, newNoTelp, newAlamat, newPass, username, newUsername, rfid, newRfid, Level = "", newlevel;
    
    private UserLevels level, newLevel;
    
    private boolean isUpdated = false;
    private Users user = new Users();
    /**
     * Creates new form TambahPetugas
     * @param parent
     * @param modal
     * @param idKaryawan
     */
    public InputKaryawan(Frame parent, boolean modal, String idKaryawan) {
        super(parent, modal);
        initComponents();
        this.setIconImage(Gambar.getWindowIcon());
        if(idKaryawan == null){
            this.tambah = true;
            // menyetting window untuk tambah data
            this.option = 1;
            this.idKaryawan = this.user.karyawanCreateID();
            this.setTitle("Tambah Data Karyawan");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-tambahKaryawan-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-tambahK-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);
            this.inpUsername.setEditable(true);
        } else {
            this.edit = true;
            // menyetting window untuk edit data
            this.option = 2;
            this.idKaryawan = idKaryawan;
            this.setTitle("Edit Data Karyawan");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-editKaryawan-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-simpanK-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);

            // mendapatkan data-data user
            this.username = this.user.getUsernameKaryawan(this.idKaryawan);
            this.nama = this.user.getNamaKaryawan(this.idKaryawan);
            this.alamat = this.user.getAlamatKaryawan(this.idKaryawan);
            this.noTelp = this.user.getNoTelpKaryawan(this.idKaryawan);
            this.level = this.user.getLevel1(this.idKaryawan);
            this.rfid = this.user.getRfid(idKaryawan);
            // menampilkan data-data user ke input text
            this.inpUsername.setEditable(false);
            this.inpUsername.setText(this.username);
            this.inpNama.setText(this.nama);
            this.inpNoTelp.setText(this.noTelp);
            this.inpAlamat.setText(this.alamat);
            this.inpPassword.setText("");
            this.inpRFID.setText(this.rfid);
            switch(level.name().toUpperCase()){
                case "ADMIN" : this.inpLevel.setSelectedIndex(1);  break;
                case "KARYAWAN" : this.inpLevel.setSelectedIndex(2);  break;
            }
        }

        this.setLocationRelativeTo(null);
        
        this.inpId.setText(this.idKaryawan);
        this.btnSimpan.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnCancel.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }
    
    /**
     * Mengecek apakah user menekan tombol simpan / tambah atau tidak
     * 
     * @return <strong>True</strong> jika user menekan tombol simpan / tambah. <br>
     *         <strong>False</strong> jika user menekan tombol kembali / close.
     */
    public boolean isUpdated(){
        return this.isUpdated;
    }
    
    /**
     * Digunakan untuk menambahkan data user ke Database.
     * 
     */
    private void addData(){
        boolean error = false;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // mendapatkan data dari textfield
        this.username = this.inpUsername.getText();
        this.nama = this.inpNama.getText();
        this.noTelp = this.inpNoTelp.getText();
        this.alamat = this.inpAlamat.getText();
        this.pass = this.inpPassword.getText();
        this.rfid = this.inpRFID.getText();
        // mendapatkan data level
        switch(this.inpLevel.getSelectedIndex()){
            case 0 : level = null; break;
            case 1 : level = UserLevels.ADMIN; break;
            case 2 : level = UserLevels.KARYAWAN; break;
        }
        // cek apakah user sudah memilih level atau belum
        if (this.username.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Username harus Di isi !");
        } else if (this.nama.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Karyawan harus Di isi !");
        } else if (this.alamat.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Alamat harus Di isi !");
        } else if (this.pass.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Password harus Di isi !");
        }else if(level == null){
            error = true;
            Message.showWarning(this, "Level harus Di isi !");
        }
        if(!error){
            // menambahkan data user ke database
            boolean save = this.user.addKaryawan(nama, noTelp, alamat, pass, level,this.username, this.rfid);
            // mengecek data berhasil disimpan atau belum
            if(save){
                // menutup pop up
                Message.showInformation(this, "Data berhasil disimpan!");
                this.isUpdated = true;
                this.user.closeConnection();
                this.dispose();
            }
        }else{
            Message.showWarning(null, "Silahkan pilih level User terlebih dahulu!");
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));         
}
    
    /**
     * Digunakan untuk mengedit data dari user
     * 
     */
    private void editData(){
        boolean eNama, eNoTelp, eAlamat, ePass, eLevel, eRfid,error = false;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // mendapakan data dari textfield
        this.newUsername = this.inpUsername.getText();
        this.newNama = this.inpNama.getText();
        this.newNoTelp = this.inpNoTelp.getText();
        this.newAlamat = this.inpAlamat.getText();
        this.newPass = this.inpPassword.getText();
        this.newRfid = this.inpRFID.getText();
        // mendapatkan data level
        switch (this.inpLevel.getSelectedIndex()) {
            case 0: newLevel = null; break;
            case 1: newLevel = UserLevels.ADMIN; break;
            case 2: newLevel = UserLevels.KARYAWAN; break;
        }
        if (this.newUsername.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Username harus Di isi !");
        } else if (this.newNama.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Karyawan harus Di isi !");
        } else if (this.newAlamat.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Alamat harus Di isi !");
        } else if (this.newPass.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Password harus Di isi !");
        }
        // cek apakah user sudah memilih level atau belum
        if (this.newLevel != null) {
            // validasi data
            if (this.user.validateDataKaryawan(this.idKaryawan, this.newNama, this.newNoTelp, this.newAlamat, this.newPass, this.newLevel, this.newUsername)) {
                // mengedit data
                eNama = this.user.setNamaKaryawan(this.idKaryawan, this.newNama);
                eNoTelp = this.user.setNoTelpKaryawan(this.idKaryawan, this.newNoTelp);
                eAlamat = this.user.setAlamatKaryawan(this.idKaryawan, this.newAlamat);
                ePass = this.user.setPassword(this.user.getUsernameKaryawan(this.idKaryawan), this.newPass);
                eLevel = this.user.setLevel(this.user.getUsernameKaryawan(this.idKaryawan), this.newLevel);
                if(this.newRfid.length() < 10){
                    eRfid = this.user.setRFID(this.user.getUsernameKaryawan(this.idKaryawan), null);
                }else{
                    eRfid = this.user.setRFID(this.user.getUsernameKaryawan(this.idKaryawan), this.rfid);
                }
                // mengecek apa data berhasil disave atau tidak
                if (eNama && eNoTelp && eAlamat && ePass && eLevel && eRfid) {
                    // menutup pop up
                    Message.showInformation(this, "Data berhasil diedit!");
                    this.isUpdated = true;
                    this.user.closeConnection();
                    this.dispose();
                }
            }
        } else {
            Message.showWarning(null, "Silahkan pilih level Petugas terlebih dahulu!");
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        inpId = new javax.swing.JTextField();
        inpNama = new javax.swing.JTextField();
        inpNoTelp = new javax.swing.JTextField();
        inpAlamat = new javax.swing.JTextField();
        inpUsername = new javax.swing.JTextField();
        inpPassword = new javax.swing.JPasswordField();
        inpLevel = new javax.swing.JComboBox();
        lblEye = new javax.swing.JLabel();
        inpRFID = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlMain.setBackground(new java.awt.Color(246, 247, 248));
        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inpId.setBackground(new java.awt.Color(211, 215, 224));
        inpId.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpId.setForeground(new java.awt.Color(0, 0, 0));
        inpId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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
        pnlMain.add(inpId, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 344, 29));

        inpNama.setBackground(new java.awt.Color(255, 255, 255));
        inpNama.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNama.setForeground(new java.awt.Color(0, 0, 0));
        inpNama.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpNama.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpNama.setCaretColor(new java.awt.Color(213, 8, 8));
        inpNama.setOpaque(false);
        pnlMain.add(inpNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 110, 560, 29));

        inpNoTelp.setBackground(new java.awt.Color(255, 255, 255));
        inpNoTelp.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNoTelp.setForeground(new java.awt.Color(0, 0, 0));
        inpNoTelp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpNoTelp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpNoTelp.setCaretColor(new java.awt.Color(213, 8, 8));
        inpNoTelp.setOpaque(false);
        pnlMain.add(inpNoTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 184, 344, 29));

        inpAlamat.setBackground(new java.awt.Color(255, 255, 255));
        inpAlamat.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpAlamat.setForeground(new java.awt.Color(0, 0, 0));
        inpAlamat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpAlamat.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpAlamat.setCaretColor(new java.awt.Color(213, 8, 8));
        inpAlamat.setOpaque(false);
        pnlMain.add(inpAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 183, 560, 29));

        inpUsername.setBackground(new java.awt.Color(255, 255, 255));
        inpUsername.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpUsername.setForeground(new java.awt.Color(0, 0, 0));
        inpUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpUsername.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpUsername.setCaretColor(new java.awt.Color(213, 8, 8));
        inpUsername.setOpaque(false);
        inpUsername.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inpUsernameMouseClicked(evt);
            }
        });
        pnlMain.add(inpUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 257, 344, 29));

        inpPassword.setBackground(new java.awt.Color(255, 255, 255));
        inpPassword.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpPassword.setForeground(new java.awt.Color(0, 0, 0));
        inpPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpPassword.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpPassword.setCaretColor(new java.awt.Color(213, 8, 8));
        inpPassword.setOpaque(false);
        pnlMain.add(inpPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 257, 524, 29));

        inpLevel.setBackground(new java.awt.Color(255, 255, 255));
        inpLevel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpLevel.setForeground(new java.awt.Color(0, 0, 0));
        inpLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "                            Pilih Level", "                              Admin", "                           Karyawan" }));
        inpLevel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpLevel.setOpaque(false);
        pnlMain.add(inpLevel, new org.netbeans.lib.awtextra.AbsoluteConstraints(47, 324, 350, 34));

        lblEye.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEye.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-login-eye-close.png"))); // NOI18N
        lblEye.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEyeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblEyeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblEyeMouseExited(evt);
            }
        });
        pnlMain.add(lblEye, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 260, -1, -1));

        inpRFID.setBackground(new java.awt.Color(255, 255, 255));
        inpRFID.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpRFID.setForeground(new java.awt.Color(0, 0, 0));
        inpRFID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpRFID.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpRFID.setCaretColor(new java.awt.Color(213, 8, 8));
        inpRFID.setOpaque(false);
        inpRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpRFIDActionPerformed(evt);
            }
        });
        pnlMain.add(inpRFID, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 326, 344, 29));

        btnSimpan.setBackground(new java.awt.Color(34, 119, 237));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambahK-075.png"))); // NOI18N
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
        pnlMain.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 370, 160, 40));

        btnCancel.setBackground(new java.awt.Color(220, 41, 41));
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-batalK-075.png"))); // NOI18N
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
        pnlMain.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(239, 370, 160, 40));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-window-tambahKaryawan-075.png"))); // NOI18N
        pnlMain.add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
        switch(option){
            case ADD_OPTION : this.addData(); break;
            case EDIT_OPTION : this.editData();  break;
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCancelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseEntered
        this.btnCancel.setIcon(Gambar.getAktiveIcon(this.btnCancel.getIcon().toString()));
    }//GEN-LAST:event_btnCancelMouseEntered

    private void btnCancelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseExited
        this.btnCancel.setIcon(Gambar.getBiasaIcon(this.btnCancel.getIcon().toString()));
    }//GEN-LAST:event_btnCancelMouseExited

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        user.closeConnection();
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void inpIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpIdMouseClicked
        Message.showWarning(this, "ID Karyawan tidak bisa diedit!");
    }//GEN-LAST:event_inpIdMouseClicked

    private void lblEyeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseClicked

    }//GEN-LAST:event_lblEyeMouseClicked

    private void lblEyeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        this.lblEye.setIcon(Gambar.getIcon("ic-login-eye-open.png"));
        this.inpPassword.setEchoChar((char)0);
    }//GEN-LAST:event_lblEyeMouseEntered

    private void lblEyeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.lblEye.setIcon(Gambar.getIcon("ic-login-eye-close.png"));
        this.inpPassword.setEchoChar('•');
    }//GEN-LAST:event_lblEyeMouseExited

    private void inpUsernameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpUsernameMouseClicked
        // TODO add your handling code here:
        if(edit){
            Message.showWarning(this, "Username tidak bisa diedit!");
        }
    }//GEN-LAST:event_inpUsernameMouseClicked

    private void inpRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpRFIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpRFIDActionPerformed

    public static void main(String args[]) {
        Log.createLog();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InputKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                InputKaryawan dialog = new InputKaryawan(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JComboBox inpLevel;
    private javax.swing.JTextField inpNama;
    private javax.swing.JTextField inpNoTelp;
    private javax.swing.JPasswordField inpPassword;
    private javax.swing.JTextField inpRFID;
    private javax.swing.JTextField inpUsername;
    private javax.swing.JLabel lblEye;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables
}

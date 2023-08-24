package com.window.dialogs;

import com.data.app.Log;
import com.manage.Text;
import com.manage.Message;
import com.media.Gambar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import javax.swing.ImageIcon;
import com.users.Users;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Amirzan
 */
public class InputSaldo extends javax.swing.JDialog {

    private Timer timer;
    private final Users user = new Users();
    private final Text text = new Text();
    public int option;

    public static final int ADD_OPTION = 1, EDIT_OPTION = 2;

    private String saldo, newSaldo, pass, hashing;
    private boolean isUpdated = false;

    public InputSaldo(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setIconImage(Gambar.getWindowIcon());
        // menyetting window untuk edit data
        this.setTitle("Ubah Saldo");

        this.saldo = Integer.toString(user.getSaldo(user.getIdSaldo(""), ""));
        this.hashing = user.getPassword(user.getCurrentLogin());
        this.inpJumlah.setText(this.saldo);
        this.setLocationRelativeTo(null);
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
     * Digunakan untuk mengedit data dari admin
     *
     */
    private void editData() {
        boolean eSaldo = false;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        boolean error = false;
        // mendapakan data dari textfield
        this.newSaldo = this.inpJumlah.getText();
        this.pass = this.inpPassword.getText();
        if (this.pass.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Password harus Di isi !");
        } else if (this.newSaldo.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Saldo harus Di isi !");
        } else if (text.containsCharacters(this.newSaldo)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Saldo harus Angka !");
        } else if (text.containsSymbols(this.newSaldo)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Saldo harus Angka !");
        } else if (Integer.parseInt(this.newSaldo) <= 0) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Saldo harus lebih dari 0 !");
        }
        if (!error) {
            // validasi data
            if (user.verifyPass(this.pass, this.hashing)) {
                // mengedit data
                if (user.editSaldo(Integer.parseInt(this.newSaldo))) {
                    // menutup dialog
                    Message.showInformation(this, "Data berhasil diedit!");
                    this.isUpdated = true;
                    this.user.closeConnection();
                    this.dispose();
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            } else {
                Message.showWarning(this, "Password Salah !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        inpJumlah = new javax.swing.JTextField();
        inpPassword = new javax.swing.JPasswordField();
        lblEye = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        pnlMain.setBackground(new java.awt.Color(246, 247, 248));
        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSimpan.setBackground(new java.awt.Color(34, 119, 237));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-simpanB-075.png"))); // NOI18N
        btnSimpan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSimpan.setOpaque(false);
        btnSimpan.setPreferredSize(new java.awt.Dimension(130, 28));
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
        pnlMain.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(47, 445, 160, 40));

        btnCancel.setBackground(new java.awt.Color(220, 41, 41));
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-batalB-075.png"))); // NOI18N
        btnCancel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnCancel.setMaximumSize(new java.awt.Dimension(130, 28));
        btnCancel.setMinimumSize(new java.awt.Dimension(130, 28));
        btnCancel.setOpaque(false);
        btnCancel.setPreferredSize(new java.awt.Dimension(130, 28));
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
        pnlMain.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(243, 445, 160, 40));

        inpJumlah.setBackground(new java.awt.Color(255, 255, 255));
        inpJumlah.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpJumlah.setForeground(new java.awt.Color(0, 0, 0));
        inpJumlah.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpJumlah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpJumlah.setCaretColor(new java.awt.Color(213, 8, 8));
        inpJumlah.setMaximumSize(new java.awt.Dimension(305, 21));
        inpJumlah.setMinimumSize(new java.awt.Dimension(305, 21));
        inpJumlah.setPreferredSize(new java.awt.Dimension(305, 21));
        inpJumlah.setVerifyInputWhenFocusTarget(false);
        inpJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpJumlahActionPerformed(evt);
            }
        });
        pnlMain.add(inpJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 122, 412, 43));

        inpPassword.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpPassword.setToolTipText("");
        inpPassword.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpPassword.setOpaque(false);
        inpPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpPasswordActionPerformed(evt);
            }
        });
        pnlMain.add(inpPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 210, 367, 43));

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
        pnlMain.add(lblEye, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 220, 30, -1));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-window-editSaldo-075.png"))); // NOI18N
        pnlMain.add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        this.editData();
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

    private void inpJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpJumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpJumlahActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void inpPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpPasswordActionPerformed

    private void lblEyeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseClicked

    }//GEN-LAST:event_lblEyeMouseClicked

    private void lblEyeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        this.lblEye.setIcon(Gambar.getIcon("ic-login-eye-open.png"));
        this.inpPassword.setEchoChar((char) 0);
    }//GEN-LAST:event_lblEyeMouseEntered

    private void lblEyeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.lblEye.setIcon(Gambar.getIcon("ic-login-eye-close.png"));
        this.inpPassword.setEchoChar('•');
    }//GEN-LAST:event_lblEyeMouseExited

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
            java.util.logging.Logger.getLogger(InputSaldo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                InputSaldo dialog = new InputSaldo(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField inpJumlah;
    private javax.swing.JPasswordField inpPassword;
    private javax.swing.JLabel lblEye;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables
}

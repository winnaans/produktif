package com.window.frames;

import com.data.app.Application;
import com.data.app.Log;
import com.error.AuthenticationException;
import com.error.InValidUserDataException;
import com.manage.Message;
import com.media.Audio;
import com.media.Gambar;
import com.users.UserLevels;
import com.users.Users;
import com.window.frames.SplashWindow;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Digunakan untuk ganti password bagi admin, karyawan.
 *
 * @author Amirzan fikri
 * @since 2022-12-24
 */
public class GantiWindow extends javax.swing.JFrame {

    private final Users user = new Users();
    private String username, passwordLama, passwordBaru;
    private int x, y;

    public GantiWindow() {
        initComponents();

        this.setLocationRelativeTo(null);
        this.setIconImage(Gambar.getWindowIcon());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        lblEye = new javax.swing.JLabel();
        lblEye1 = new javax.swing.JLabel();
        inpUsername = new javax.swing.JTextField();
        lblMinimaze = new javax.swing.JLabel();
        lblClose = new javax.swing.JLabel();
        inpPasswordLama = new javax.swing.JPasswordField();
        inpPasswordBaru = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JLabel();
        btnKembali = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("umkm\n");
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        pnlMain.add(lblEye, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 290, -1, -1));

        lblEye1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEye1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-login-eye-close.png"))); // NOI18N
        lblEye1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEye1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblEye1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblEye1MouseExited(evt);
            }
        });
        pnlMain.add(lblEye1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 337, -1, -1));

        inpUsername.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        inpUsername.setOpaque(false);
        inpUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpUsernameActionPerformed(evt);
            }
        });
        pnlMain.add(inpUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 240, 250, 30));

        lblMinimaze.setBackground(new java.awt.Color(50, 50, 55));
        lblMinimaze.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblMinimaze.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMinimaze.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-login-minimaze.png"))); // NOI18N
        lblMinimaze.setOpaque(true);
        lblMinimaze.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMinimazeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblMinimazeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblMinimazeMouseExited(evt);
            }
        });
        pnlMain.add(lblMinimaze, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 0, 30, 28));

        lblClose.setBackground(new java.awt.Color(50, 51, 55));
        lblClose.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblClose.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-login-close.png"))); // NOI18N
        lblClose.setOpaque(true);
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCloseMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblCloseMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblCloseMouseExited(evt);
            }
        });
        pnlMain.add(lblClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 31, 28));

        inpPasswordLama.setBorder(null);
        inpPasswordLama.setOpaque(false);
        inpPasswordLama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpPasswordLamaActionPerformed(evt);
            }
        });
        pnlMain.add(inpPasswordLama, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 289, 250, 28));

        inpPasswordBaru.setBorder(null);
        inpPasswordBaru.setOpaque(false);
        inpPasswordBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpPasswordBaruActionPerformed(evt);
            }
        });
        pnlMain.add(inpPasswordBaru, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 335, 250, 29));

        btnLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-login-075.png"))); // NOI18N
        btnLogin.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLoginMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLoginMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLoginMouseExited(evt);
            }
        });
        pnlMain.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 385, -1, -1));

        btnKembali.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-kembaliLogin-075.png"))); // NOI18N
        btnKembali.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKembaliMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnKembaliMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnKembaliMouseExited(evt);
            }
        });
        pnlMain.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 367, -1, -1));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-gantiPassword-050.png"))); // NOI18N
        pnlMain.add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(pnlMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 720, 510));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        Log.addLog("Menutup Window " + getClass().getName());
        user.closeConnection();
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Log.addLog("Menutup Window " + getClass().getName());
        user.closeConnection();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        Log.addLog("Membuka Window " + getClass().getName());
    }//GEN-LAST:event_formWindowOpened

    private void inpUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpUsernameActionPerformed

    private void lblCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMouseClicked
        Application.closeApplication();
    }//GEN-LAST:event_lblCloseMouseClicked

    private void lblCloseMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMouseEntered
        this.lblClose.setIcon(Gambar.getIcon("ic-login-close-entered.png"));
    }//GEN-LAST:event_lblCloseMouseEntered

    private void lblCloseMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMouseExited
        this.lblClose.setIcon(Gambar.getIcon("ic-login-close.png"));
    }//GEN-LAST:event_lblCloseMouseExited

    private void lblMinimazeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimazeMouseClicked
        this.setExtendedState(javax.swing.JFrame.ICONIFIED);
    }//GEN-LAST:event_lblMinimazeMouseClicked

    private void lblMinimazeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimazeMouseEntered
        this.lblMinimaze.setIcon(Gambar.getIcon("ic-login-minimaze-entered.png"));
    }//GEN-LAST:event_lblMinimazeMouseEntered

    private void lblMinimazeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimazeMouseExited
        this.lblMinimaze.setIcon(Gambar.getIcon("ic-login-minimaze.png"));
    }//GEN-LAST:event_lblMinimazeMouseExited

    private void inpPasswordLamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpPasswordLamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpPasswordLamaActionPerformed

    private void btnLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseClicked
        try {
            boolean kosong = false;
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.username = this.inpUsername.getText();
            this.passwordLama = this.inpPasswordLama.getText();
            this.passwordBaru = this.inpPasswordBaru.getText();
            if (this.username.isEmpty()) {
                kosong = true;
                System.out.println("Username tidak boleh kosong");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Username harus Di isi !");
            } else if (this.passwordLama.isEmpty()) {
                kosong = true;
                System.out.println("Password Lama tidak boleh kosong");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Password Lama harus Di isi !");
            } else if (this.passwordBaru.isEmpty()) {
                kosong = true;
                System.out.println("Password Baru tidak boleh kosong");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Password Baru harus Di isi !");
            }
            if (!kosong) {
                JOptionPane.showMessageDialog(this, "Mohon tunggu sebentar\nSedang Memeriksa Username dan Password");
                boolean check = user.validateSetPassword(this.username, this.passwordLama);
                if (check) {
                    boolean ganti = user.setPassword(this.username, this.passwordBaru);
                    if (ganti) {
                        JOptionPane.showMessageDialog(this, "Ganti Password berhasil \nSedang mengubah password !");
                        boolean login = user.login(this.username, passwordBaru);
                        if (login) {
                            Audio.play(Audio.SOUND_INFO);
                            JOptionPane.showMessageDialog(this, "Login Berhasil!\n\nSelamat datang " + user.getData(UserLevels.USERS.name(), "nama_karyawan", "WHERE id_karyawan = '" + user.getData(UserLevels.USERS.name(), "id_karyawan", "WHERE username = '" + this.username + "'") + "'"));
                            // membuka window dashboard
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new SplashWindow().setVisible(true);
                                }
                            });

                            // menutup koneksi dan window
                            user.closeConnection();
                            this.dispose();
                        } else {
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            // mereset textfield jika login gagal
                            this.inpUsername.setText("");
                            this.inpPasswordLama.setText("");
                        }
                    }
                }
            }
        } catch (IOException | AuthenticationException | InValidUserDataException | SQLException ex) {
            this.inpUsername.setText("");
            this.inpPasswordLama.setText("");
            this.inpPasswordBaru.setText("");
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            // menampilkan pesan error
            Message.showException(this, ex, true);
        } catch (Exception ex) {
            Logger.getLogger(GantiWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLoginMouseClicked

    private void btnLoginMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseEntered
        this.btnLogin.setIcon(Gambar.getAktiveIcon(this.btnLogin.getIcon().toString()));
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_btnLoginMouseEntered

    private void btnLoginMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseExited
        this.btnLogin.setIcon(Gambar.getBiasaIcon(this.btnLogin.getIcon().toString()));
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_btnLoginMouseExited

    private void inpPasswordBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpPasswordBaruActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpPasswordBaruActionPerformed

    private void lblEyeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseClicked

    }//GEN-LAST:event_lblEyeMouseClicked

    private void lblEyeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        this.lblEye.setIcon(Gambar.getIcon("ic-login-eye-open.png"));
        this.inpPasswordLama.setEchoChar((char)0);
    }//GEN-LAST:event_lblEyeMouseEntered

    private void lblEyeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEyeMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.lblEye.setIcon(Gambar.getIcon("ic-login-eye-close.png"));
        this.inpPasswordLama.setEchoChar('•');
    }//GEN-LAST:event_lblEyeMouseExited

    private void lblEye1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEye1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lblEye1MouseClicked

    private void lblEye1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEye1MouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        this.lblEye1.setIcon(Gambar.getIcon("ic-login-eye-open.png"));
        this.inpPasswordBaru.setEchoChar((char)0);
    }//GEN-LAST:event_lblEye1MouseEntered

    private void lblEye1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEye1MouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.lblEye1.setIcon(Gambar.getIcon("ic-login-eye-close.png"));
        this.inpPasswordBaru.setEchoChar('•');
    }//GEN-LAST:event_lblEye1MouseExited

    private void btnKembaliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseClicked
        // TODO add your handling code here:
        user.closeConnection();
        this.dispose();
        java.awt.EventQueue.invokeLater(new Runnable(){

            @Override
            public void run(){
                new com.window.frames.LoginWindow().setVisible(true);
            }
        });
    }//GEN-LAST:event_btnKembaliMouseClicked

    private void btnKembaliMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseEntered
        this.btnKembali.setIcon(Gambar.getAktiveIcon(this.btnKembali.getIcon().toString()));
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_btnKembaliMouseEntered

    private void btnKembaliMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKembaliMouseExited
        this.btnKembali.setIcon(Gambar.getBiasaIcon(this.btnKembali.getIcon().toString()));
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_btnKembaliMouseExited

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GantiWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new GantiWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel btnKembali;
    private javax.swing.JLabel btnLogin;
    private javax.swing.JPasswordField inpPasswordBaru;
    private javax.swing.JPasswordField inpPasswordLama;
    private javax.swing.JTextField inpUsername;
    private javax.swing.JLabel lblClose;
    private javax.swing.JLabel lblEye;
    private javax.swing.JLabel lblEye1;
    private javax.swing.JLabel lblMinimaze;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables
}

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
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 * Digunakan untuk login bagi admin, karyawan.
 *
 * @author Amirzan fikri
 * @since 2020-11-22
 */
public class LoginWindow extends javax.swing.JFrame implements DocumentListener, ActionListener{

    private final Users user = new Users();
    private String username = "", password;
    private int x, y;
    private Timer timer;
    public LoginWindow() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setIconImage(Gambar.getWindowIcon());
        this.timer = new Timer(1000, this);
        this.timer.setRepeats(false);
        this.inpRFID.getDocument().addDocumentListener(this);
        this.inpTutupRFID.setEditable(false);
//        this.inpRFID.setVisible(false);
    }

    private void rfid(String rfid) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            this.username = user.rfid(rfid);
            if (this.username.equals("")) {
                System.out.println("kosong");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                // mereset textfield jika login gagal
                this.inpRFID.setText("");
                this.inpUsername.setText("");
                this.inpPassword.setText("");
            } else {
                Audio.play(Audio.SOUND_INFO);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(this, "Login Berhasil!\n\nSelamat datang " + 
                        user.getData(UserLevels.USERS.name(), "nama_karyawan", "WHERE id_karyawan = '" + 
                                user.getData(UserLevels.USERS.name(), "id_karyawan", "WHERE username = '" + this.username + "'") + "'"));
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
            }
        } catch (InValidUserDataException ex) {
            this.inpRFID.setText("");
            this.inpUsername.setText("");
            this.inpPassword.setText("");
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            // menampilkan pesan error
            Message.showException(this, ex, true);
        } catch (Exception ex) {
            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    private void startTimer() {
        if (timer.isRunning()) {
            timer.restart();
        } else {
            timer.start();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        pnlMain = new javax.swing.JPanel();
        lblEye = new javax.swing.JLabel();
        inpTutupRFID = new javax.swing.JTextField();
        inpRFID = new javax.swing.JTextField();
        inpUsername = new javax.swing.JTextField();
        lblMinimaze = new javax.swing.JLabel();
        lblClose = new javax.swing.JLabel();
        inpPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JLabel();
        btnGanti = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        jToggleButton1.setText("jToggleButton1");

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
        pnlMain.add(lblEye, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 317, -1, -1));

        inpTutupRFID.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        inpTutupRFID.setEnabled(false);
        inpTutupRFID.setOpaque(false);
        inpTutupRFID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inpTutupRFIDMouseClicked(evt);
            }
        });
        inpTutupRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpTutupRFIDActionPerformed(evt);
            }
        });
        inpTutupRFID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inpTutupRFIDKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpTutupRFIDKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpTutupRFIDKeyTyped(evt);
            }
        });
        pnlMain.add(inpTutupRFID, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 225, 280, 28));

        inpRFID.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        inpRFID.setOpaque(false);
        inpRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpRFIDActionPerformed(evt);
            }
        });
        inpRFID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inpRFIDKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpRFIDKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpRFIDKeyTyped(evt);
            }
        });
        pnlMain.add(inpRFID, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 225, 250, 28));

        inpUsername.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        inpUsername.setOpaque(false);
        inpUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpUsernameActionPerformed(evt);
            }
        });
        pnlMain.add(inpUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 272, 250, 27));

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

        inpPassword.setBorder(null);
        inpPassword.setOpaque(false);
        inpPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpPasswordActionPerformed(evt);
            }
        });
        pnlMain.add(inpPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 317, 250, 27));

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
        pnlMain.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 365, -1, -1));

        btnGanti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-gantiPassword-075.png"))); // NOI18N
        btnGanti.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGantiMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGantiMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnGantiMouseExited(evt);
            }
        });
        pnlMain.add(btnGanti, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 350, -1, -1));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-login-050.png"))); // NOI18N
        pnlMain.add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(pnlMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 721, 513));

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
        this.inpRFID.requestFocus();
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

    private void inpPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpPasswordActionPerformed

    private void btnLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseClicked
        try {
            boolean kosong = false;
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.username = this.inpUsername.getText();
            this.password = this.inpPassword.getText();
            if (this.username.isEmpty()) {
                kosong = true;
                System.out.println("Username tidak boleh kosong");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Username harus Di isi !");
            } else if (this.password.isEmpty()) {
                kosong = true;
                System.out.println("Password tidak boleh kosong");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Password harus Di isi !");
            }
            if (!kosong) {
                JOptionPane.showMessageDialog(this, "Mohon tunggu sebentar\nSedang Memeriksa Username dan Password");
                boolean login = user.login(this.username, password);
//                login = true;
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
                    this.inpPassword.setText("");
                }
            }
        } catch (IOException | AuthenticationException | InValidUserDataException | SQLException ex) {
            this.inpUsername.setText("");
            this.inpPassword.setText("");
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            // menampilkan pesan error
            Message.showException(this, ex, true);
        } catch (Exception ex) {
            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
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

    private void btnGantiMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGantiMouseEntered
        this.btnGanti.setIcon(Gambar.getAktiveIcon(this.btnGanti.getIcon().toString()));
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_btnGantiMouseEntered

    private void btnGantiMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGantiMouseExited
        this.btnGanti.setIcon(Gambar.getBiasaIcon(this.btnGanti.getIcon().toString()));
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_btnGantiMouseExited

    private void btnGantiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGantiMouseClicked
        // TODO add your handling code here:
        user.closeConnection();
        this.dispose();
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new com.window.frames.GantiWindow().setVisible(true);
            }
        });
    }//GEN-LAST:event_btnGantiMouseClicked

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

    private void inpRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpRFIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpRFIDActionPerformed

    private void inpRFIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpRFIDKeyTyped

    }//GEN-LAST:event_inpRFIDKeyTyped
    private void inpRFIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpRFIDKeyReleased

    }//GEN-LAST:event_inpRFIDKeyReleased

    private void inpRFIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpRFIDKeyPressed

    }//GEN-LAST:event_inpRFIDKeyPressed

    private void inpTutupRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpTutupRFIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpTutupRFIDActionPerformed

    private void inpTutupRFIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpTutupRFIDKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpTutupRFIDKeyPressed

    private void inpTutupRFIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpTutupRFIDKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_inpTutupRFIDKeyReleased

    private void inpTutupRFIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpTutupRFIDKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_inpTutupRFIDKeyTyped

    private void inpTutupRFIDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpTutupRFIDMouseClicked
        this.inpRFID.requestFocus();
    }//GEN-LAST:event_inpTutupRFIDMouseClicked

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel btnGanti;
    private javax.swing.JLabel btnLogin;
    private javax.swing.JPasswordField inpPassword;
    private javax.swing.JTextField inpRFID;
    private javax.swing.JTextField inpTutupRFID;
    private javax.swing.JTextField inpUsername;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblClose;
    private javax.swing.JLabel lblEye;
    private javax.swing.JLabel lblMinimaze;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

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
        String rfid = this.inpRFID.getText();
        if(rfid.length() >= 8 && rfid.length() <= 10){
            this.rfid(rfid);
        }
    }
}


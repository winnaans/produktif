package com.window.dialogs;

import java.util.Date;
import com.data.app.Log;
import com.error.InValidUserDataException;
import com.manage.Diskon;
import com.manage.Waktu;
import com.manage.Message;
import com.media.Gambar;
//import com.users.Karyawan;
import com.users.UserLevels;
//import com.users.Users;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author Achmad Baihaqi
 */
public class InputDiskon extends javax.swing.JDialog {

    private boolean tambah = false, edit = false;
//    private final Karyawan user = new Karyawan();
    private final Waktu waktu = new Waktu();
    public int option;

    public static final int ADD_OPTION = 1, EDIT_OPTION = 2;
    private final String idDiskon;
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
    private String nama, jumlah, minimal, tanggalAwal, tanggalAkhir, newNama, newJumlah, newMinimal, newTanggalAwal, newTanggalAkhir;
    private Date TanggalAwal, TanggalAkhir;
    private UserLevels level, newLevel;
    private Date tanggalSekarang;
    private boolean isUpdated = false;
//    private Users user = new Users();
    private Diskon diskon = new Diskon();

    /**
     * Creates new form TambahPetugas
     *
     * @param parent
     * @param modal
     * @param idDiskon
     */
    public InputDiskon(Frame parent, boolean modal, String idDiskon) throws ParseException {
        super(parent, modal);
        initComponents();
        tanggalSekarang = date1.parse(waktu.getCurrentDate());
        this.setIconImage(Gambar.getWindowIcon());
        if (idDiskon == null) {
            // menyetting window untuk tambah data
            this.option = 1;
            this.idDiskon = this.diskon.createID();
            this.setTitle("Tambah Data Diskon");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-tambahDiskon-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-tambahK-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);
            this.inpTanggalAwal.setDate(tanggalSekarang);
            this.inpTanggalAkhir.setDate(tanggalSekarang);
            this.tambah = true;
        } else {
            // menyetting window untuk edit data
            this.option = 2;
            this.idDiskon = idDiskon;
            this.setTitle("Edit Data Karyawan");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-editDiskon-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-simpanK-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);

            // mendapatkan data-data user
            this.nama = this.diskon.getNamaDiskon(this.idDiskon);
            this.jumlah = this.diskon.getJumlah(this.idDiskon);
            this.minimal = this.diskon.getMinimal(this.idDiskon);
            this.tanggalAwal = this.diskon.getTanggalAwal(this.idDiskon);
            this.tanggalAkhir = this.diskon.getTanggalAkhir(this.idDiskon);

            // menampilkan data-data user ke input text
            this.inpNama.setText(this.nama);
            this.inpJumlah.setText(this.jumlah);
            this.inpMinimal.setText(this.minimal);
            this.inpTanggalAwal.setDate(date1.parse(this.tanggalAwal));
            this.inpTanggalAkhir.setDate(date1.parse(this.tanggalAkhir));
            this.edit = true;
        }

        this.setLocationRelativeTo(null);

        this.inpId.setText(this.idDiskon);
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
     * Digunakan untuk menambahkan data user ke Database.
     *
     */
    private void addData() {
        String pesan = "";
        try {
            boolean error = false;
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            // mendapatkan data dari textfield
            this.nama = this.inpNama.getText();
            this.jumlah = this.inpJumlah.getText();
            this.minimal = this.inpMinimal.getText();
            this.tanggalAwal = date1.format(this.inpTanggalAwal.getDate());
            this.tanggalAkhir = date1.format(this.inpTanggalAkhir.getDate());
            // cek apakah user sudah memilih level atau belum
            if (this.nama.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Nama Diskon harus Di isi !");
            } else if (this.jumlah.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Jumlah Diskon harus Di isi !");

            } else if (this.minimal.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Minimal Diskon harus Di isi !");
            } else if (this.tanggalAwal.equals("")) {
                error = true;
                Message.showWarning(this, "Tanggal Awal harus Di isi !");
            } else if (this.tanggalAkhir.equals("")) {
                error = true;
                Message.showWarning(this, "Tanggal Akhir harus Di isi !");
            }
            if (!error) {
                if (this.diskon.validateDataDiskon(idDiskon, nama, jumlah, minimal, tanggalAwal, tanggalAkhir)) {
                    // menambahkan data user ke database
                    boolean save = this.diskon.addDiskon(nama, jumlah, minimal, tanggalAwal, tanggalAkhir);
                    //mengecek data berhasil disimpan atau belum
                    if (save) {
                        // menutup pop up
                        Message.showInformation(this, "Data berhasil disimpan!");
                        this.isUpdated = true;
                        this.diskon.closeConnection();
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        this.dispose();
                    }
                }
            }
        } catch (Exception ex) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, ex.getMessage());
        }
    }

    /**
     * Digunakan untuk mengedit data dari user
     *
     */
    private void editData() {
        try {
            boolean eNama, eJumlah, eMinimal, eTanggalAwal, eTanggalAkhir, error = false;
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // mendapakan data dari textfield
            this.newNama = this.inpNama.getText();
            this.newJumlah = this.inpJumlah.getText();
            this.newMinimal = this.inpMinimal.getText();
            this.newTanggalAwal = date1.format(this.inpTanggalAwal.getDate());
            this.newTanggalAkhir = date1.format(this.inpTanggalAkhir.getDate());

            if (this.newNama.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Nama Diskon harus Di isi !");
            } else if (this.newJumlah.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Jumlah Diskon harus Di isi !");
            } else if (this.newMinimal.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Minimal Pembelian harus Di isi !");
            } else if (this.newTanggalAwal.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Tanggal Awal harus Di isi !");
            } else if (this.newTanggalAkhir.equals("")) {
                error = true;
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Message.showWarning(this, "Tanggal Akhir harus Di isi !");
            }
            if (this.diskon.validateDataDiskon(this.idDiskon, this.newNama, this.newJumlah, this.newMinimal, this.newTanggalAwal, this.newTanggalAkhir)) {
                // mengedit data
                eNama = this.diskon.setNamaDiskon(this.idDiskon, this.newNama);
                eJumlah = this.diskon.setJumlah(this.idDiskon, this.newJumlah);
                eMinimal = this.diskon.setMinimal(this.idDiskon, this.newMinimal);
                eTanggalAwal = this.diskon.setTanggalAwal(this.idDiskon, this.newTanggalAwal);
                eTanggalAkhir = this.diskon.setTanggalAkhir(this.idDiskon, this.newTanggalAkhir);

                // mengecek apa data berhasil disave atau tidak
                if (eNama && eJumlah && eMinimal && eTanggalAwal && eTanggalAkhir) {
                    // menutup pop up
                    Message.showInformation(this, "Data berhasil diedit!");
                    this.isUpdated = true;
                    this.diskon.closeConnection();
                    this.dispose();
                }
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception ex) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        inpId = new javax.swing.JTextField();
        inpNama = new javax.swing.JTextField();
        inpJumlah = new javax.swing.JTextField();
        inpMinimal = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        inpTanggalAwal = new com.toedter.calendar.JDateChooser();
        inpTanggalAkhir = new com.toedter.calendar.JDateChooser();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlMain.setBackground(new java.awt.Color(246, 247, 248));
        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inpId.setBackground(new java.awt.Color(211, 215, 224));
        inpId.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpId.setForeground(new java.awt.Color(0, 0, 0));
        inpId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpId.setText("D002");
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

        inpJumlah.setBackground(new java.awt.Color(255, 255, 255));
        inpJumlah.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpJumlah.setForeground(new java.awt.Color(0, 0, 0));
        inpJumlah.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpJumlah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpJumlah.setCaretColor(new java.awt.Color(213, 8, 8));
        inpJumlah.setOpaque(false);
        pnlMain.add(inpJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 184, 344, 29));

        inpMinimal.setBackground(new java.awt.Color(255, 255, 255));
        inpMinimal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpMinimal.setForeground(new java.awt.Color(0, 0, 0));
        inpMinimal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpMinimal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpMinimal.setCaretColor(new java.awt.Color(213, 8, 8));
        inpMinimal.setOpaque(false);
        inpMinimal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpMinimalActionPerformed(evt);
            }
        });
        pnlMain.add(inpMinimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 183, 560, 29));

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

        inpTanggalAwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inpTanggalAwalMouseClicked(evt);
            }
        });
        inpTanggalAwal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                inpTanggalAwalPropertyChange(evt);
            }
        });
        pnlMain.add(inpTanggalAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 260, 250, 40));

        inpTanggalAkhir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inpTanggalAkhirMouseClicked(evt);
            }
        });
        inpTanggalAkhir.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                inpTanggalAkhirPropertyChange(evt);
            }
        });
        pnlMain.add(inpTanggalAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 260, 250, 40));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-window-tambahDiskon-075.png"))); // NOI18N
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
        diskon.closeConnection();
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void inpIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpIdMouseClicked
        Message.showWarning(this, "ID Diskon tidak bisa diedit!");
    }//GEN-LAST:event_inpIdMouseClicked

    private void inpMinimalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpMinimalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpMinimalActionPerformed

    private void inpTanggalAkhirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpTanggalAkhirMouseClicked
//        System.out.println("tanggal akhir "+inpTanggalAkhir.getDate().toString());
//        System.out.println("tanggal akhir format string "+inpTanggalAkhir.getDateFormatString());
    }//GEN-LAST:event_inpTanggalAkhirMouseClicked

    private void inpTanggalAkhirPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_inpTanggalAkhirPropertyChange
        if (this.tambah || this.edit) {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.TanggalAwal = inpTanggalAwal.getDate();
            this.TanggalAkhir = inpTanggalAkhir.getDate();
            if (this.TanggalAkhir.compareTo(TanggalAwal) < 0) {
                System.out.println("Tanggal Awal tidak boleh kurang dari Tanggal Awal !");
                Message.showWarning(this, "Tanggal Akhir tidak boleh kurang dari tanggal Awal !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }else{
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }//GEN-LAST:event_inpTanggalAkhirPropertyChange

    private void inpTanggalAwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpTanggalAwalMouseClicked
//        System.out.println("tanggal awal "+inpTanggalAwal.getDate().toString());
//        System.out.println("tanggal awal format string "+inpTanggalAwal.getDateFormatString());
    }//GEN-LAST:event_inpTanggalAwalMouseClicked

    private void inpTanggalAwalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_inpTanggalAwalPropertyChange
        if (this.tambah || this.edit) {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            this.TanggalAwal = inpTanggalAwal.getDate();
            this.TanggalAkhir = inpTanggalAkhir.getDate();
            if (this.TanggalAwal.compareTo(TanggalAkhir) > 0) {
                System.out.println("Tanggal Awal tidak boleh lebih dari Tanggal Akhir !");
                Message.showWarning(this, "Tanggal awal tidak boleh lebih dari tanggal akhir !");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }else{
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }//GEN-LAST:event_inpTanggalAwalPropertyChange

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
            java.util.logging.Logger.getLogger(InputDiskon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    InputDiskon dialog = new InputDiskon(new javax.swing.JFrame(), true, null);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch (ParseException ex) {
                    Logger.getLogger(InputDiskon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JTextField inpId;
    private javax.swing.JTextField inpJumlah;
    private javax.swing.JTextField inpMinimal;
    private javax.swing.JTextField inpNama;
    private com.toedter.calendar.JDateChooser inpTanggalAkhir;
    private com.toedter.calendar.JDateChooser inpTanggalAwal;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables
}

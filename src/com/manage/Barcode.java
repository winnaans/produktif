package com.manage;

import com.barcodelib.barcode.Linear;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.media.Gambar;
//import com.onbarcode.barcode.EAN13;
//import com.onbarcode.barcode.IBarcode;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Amirzan Fikri P
 */
public class Barcode {
    private final Gambar gambar = new Gambar();
    private final FileManager fManage = new FileManager();
    private final String BARCODE = "\\src\\barcode\\";
    private final String dir = System.getProperty("user.dir");
    private final String format = ".png";
    public boolean createBarcode(String kode) {
        try {
            Linear barcode = new Linear();
            barcode.setType(Linear.CODE128B);
            barcode.setData(kode);
            barcode.setI(0.1f);
            barcode.setResolution(10);
            if (barcode.renderBarcode(this.dir + this.BARCODE + kode + this.format)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public ImageIcon getBarcode(String kode) {
        File file = new File(this.dir + this.BARCODE + kode + this.format);
        return Gambar.scaleImage(file, 330, 72);
    }
    public boolean isExistBarcode(String kode){
        File file = new File(this.dir + this.BARCODE + kode + this.format);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public String scanBarcode(String kode) throws Exception {
        try {
            File path = new File(this.dir + this.BARCODE);
            File barcode = fManage.getImage(path, kode);
            InputStream barInputStream = new FileInputStream(barcode);
            BufferedImage barBufferedImage = ImageIO.read(barInputStream);
            barInputStream.close();
            LuminanceSource source = new BufferedImageLuminanceSource(barBufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Reader reader = new MultiFormatReader();
            return reader.decode(bitmap).getText();
        } catch (ChecksumException | FormatException | NotFoundException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Message.showException(this, "Terjadi Kesalahan!\n\nError message : " + e.getMessage(), e, true);
        } catch (NullPointerException n) {
            Message.showException(this, "Terjadi Kesalahan!\n\nBarcode tidak ditemukan", n, true);
            n.printStackTrace();
            System.out.println("error barcode");
        }
        return "";
    }

    public boolean deleteBarcode(String kode) {
        try {
            File path = new File(this.dir + this.BARCODE);
            File gambar = fManage.getImage(path, kode);
            if (fManage.deleteFile(gambar.toString())) {
                return true;
            } else {
                throw new Exception("Barcode tidak bisa dihapus");
            }
        } catch (Exception ex) {
            Logger.getLogger(Barcode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

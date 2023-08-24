package com.manage;

import com.barcodelib.barcode.Linear;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

// Java code to generate QR code
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 *
 * @author Windows Amirzan Fikri Prasetyo
 */
public class QRCode {

    private final int height = 200;
    private final int width = 200;
    private final String charset = "UTF-8";
    private final FileManager fManage = new FileManager();

    public String createQR(String data, Map hashMap) {
        try {
            String dir = System.getProperty("user.dir");
            String path = dir + "\\src\\qr code\\" + data + ".png";
            BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(this.charset), this.charset), BarcodeFormat.QR_CODE, this.width, this.height);
            MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
            return data + ".png";
        } catch (WriterException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Message.showException(this, "Terjadi Kesalahan!\n\nError message : " + e.getMessage(), e, true);
        }
        return "";
    }

    public String scanQR(String data, Map hashMap) throws Exception {
        try {
            String dir = System.getProperty("user.dir");
            File path = new File(dir + "\\src\\qr code\\");
            File gambar = fManage.getImage(path, data);
            InputStream input = new FileInputStream(gambar);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(input))));
            input.close();
             if(fManage.deleteFile(gambar.toString())){
//                System.out.println("QR Code dihapus");
            }else{
                System.out.println("QR Code tidak bisa dihapus");
                throw new Exception("QR Code tidak bisa dihapus");
            }
            Result result = new MultiFormatReader().decode(binaryBitmap);
            return result.getText();
        } catch (IOException | NotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Message.showException(this, "Terjadi Kesalahan!\n\nError message : " + e.getMessage(), e, true);
        } catch (NullPointerException n) {
            Message.showException(this, "Terjadi Kesalahan!\n\nQR Code tidak ditemukan", n, true);
            n.printStackTrace();
            System.out.println("error barcode");
        }
        return "";
    }
    // Driver code
}

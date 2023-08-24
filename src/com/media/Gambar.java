package com.media;

import com.data.app.Application;
import com.data.app.Log;
import com.data.app.Storage;
import com.manage.FileManager;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Achmad Baihaqi
 * @since 2020-11-23
 */
public class Gambar {
    
    private static final ImageIcon icWindow_old = Application.getWindowIcon_old();
    private static final ImageIcon icWindow = Application.getWindowIcon();
    
    private static final String BARCODE = "src\\barcode\\";
    private static final String QRCODE = "src\\qr code\\";
    private static final String DIREKTORY_ICONS = "src\\resources\\image\\icons\\";
    private static final String DIREKTORY_ICON = "src\\resources\\image\\gambar_icon\\";
    private static final String ukuran = "075";
    
    public static Image getWindowIcon_old(){
        return icWindow_old.getImage();
    }
    public static Image getWindowIcon(){
        return icWindow.getImage();
    }
    
    private static int getWidth(File file){
        return new ImageIcon(file.toString()).getIconWidth();
    }
    
    private static int getHeight(File file){
        return new ImageIcon(file.toString()).getIconHeight();
    }
    
    public static ImageIcon getIcon(final String icon){
        File file = new File(Gambar.DIREKTORY_ICONS + icon);
        // mengecek apakah file icon ada atau tidak
        if(file.exists()){
            return new ImageIcon(file.toString());
        }else{
            JOptionPane.showMessageDialog(null, "Tidak dapat menemukan file '" + icon + "'", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
    
    public static ImageIcon getQrcode(String icon){
        File file = new File(Gambar.QRCODE + icon);
        // mengecek apakah file icon ada atau tidak
        if(file.exists()){
            return new ImageIcon(file.toString());
        }else{
            JOptionPane.showMessageDialog(null, "Tidak dapat menemukan file '" + icon + "'", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
    
    public static File chooseImage(){
        return new File(
                new FileManager().chooseFile(System.getProperty("user.home")+"\\Desktop\\", (1023*1024)*3, ".IMAGES", "jpg", "jpeg", "png", "gif")
        );
    }
    
    public static File resizeImage(File imgFile, int width, int height, String newName){
        BufferedImage fileOri, resize;
        Graphics2D g2d;
        File newImg;
        String cacheDir, filename, format;
        boolean result;
        
        try{
            // mengecek file gambar yang diinputkan exist atau tidak
            if(imgFile.exists()){
                // mendapatkan nama dari file gambar
                filename = new FileManager().getNamaFile(imgFile.toString());
                // mendapatkan format dari file gambar
                format = new FileManager().getFormatFile(imgFile.toString());
                // mendapatkan folder cache untuk menyimpan hasil gambar yang sudah diresize
                cacheDir = new Storage().getCacheDir() + "pictures\\resized\\";
                
                // jika newName kosong maka filename dari gambar akan diatur secara default
                if(newName == null){
                    // membuat direktori dan filename dari gambar yang akan diresize berdasarkan nama default
                    newImg = new File(cacheDir + filename.substring(0, filename.lastIndexOf(".")) + "_" + width + "x" + height + format);
                }else{
                    // membuat direktori dan filename dari gambar yang akan diresize berdasarkan nama yang diinputkan user
                    newImg = new File(cacheDir + newName + format);
                }
                
                // jika gambar sebelumnya sudah diresize maka akan mengembalikan direktori dari gambar
                if(newImg.exists()){
                    return newImg;
                }else{
                    Log.addLog("Merubah ukuran gambar " + filename + " ke " + width + "x" + height);
                    
                    // mengatur ukuran gambar
                    fileOri = ImageIO.read(imgFile);
                    resize = new BufferedImage(width, height, fileOri.getType()); 

                    // meresize gambar
                    g2d = resize.createGraphics();
                    g2d.drawImage(fileOri, 0, 0, width, height, null);
                    g2d.dispose();                    
                }
                // menyimpan perubahan pada gambar setelah diresize
                result = ImageIO.write(resize, format.substring(1), newImg); 
                
                // akan megembalikan gambar dalam bentuk file jika gambar berhasil disimpan
                return result ? newImg : null;
            }
        }catch (IOException ex){
            JOptionPane.showMessageDialog(null,ex);
        }
        return null;
    }
        
    public static File resizeImage(File imgFile, int width, int height){
        return Gambar.resizeImage(imgFile, width, height, null);
    }
    
    public static ImageIcon scaleImage(File fileImage, final int x, final int y){
        ImageIcon icon, scaleIcon;
        Image img, imgScale;
        if(fileImage.exists()){
            icon = new ImageIcon(fileImage.toString());
            img = icon.getImage();
            imgScale = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
            scaleIcon = new ImageIcon(imgScale);
            return scaleIcon;
        }  
        return null;
    }
    
    public static boolean isDarkIcon(String icon){
        return icon.substring(icon.lastIndexOf("-")+1, icon.lastIndexOf(".")).contains("dark");
    }
    public static boolean isHoverIcon(String icon){
        return icon.substring(icon.lastIndexOf("-")-5, icon.lastIndexOf(".")-4).contains("hover");
    }
    public static boolean isAktifIcon(String icon){
        return icon.substring(icon.lastIndexOf("-")-5, icon.lastIndexOf(".")-4).contains("aktif");
    }
    public static boolean isNoHoverIcon(String icon){
        return icon.substring(icon.lastIndexOf("-")-5, icon.lastIndexOf(".")).contains("hover");
    }
    public static boolean isNoAktifIcon(String icon){
        return icon.substring(icon.lastIndexOf("-")-5, icon.lastIndexOf(".")).contains("aktif");
    }
    
    public static ImageIcon getBiasaIcon(String icon){
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
                format = fm.getFormatFile(icon);
        file = file.substring(0, file.lastIndexOf(".")-10) + "-"+ukuran;
//        System.out.println("file baru "+file);
//        String baru = Gambar.DIREKTORY_ICON + file +format;
//        System.out.println("icon baru : "+baru);
        return new ImageIcon(Gambar.DIREKTORY_ICON + file +format);
    }
    public static ImageIcon getHoverIcon(String icon){
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
                format = fm.getFormatFile(icon);
        file = file.substring(0, file.lastIndexOf(".")-4) + "-hover-"+ukuran;
        return new ImageIcon(Gambar.DIREKTORY_ICON + file +format);
    }
    public static ImageIcon getAktiveIcon(String icon){
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
                format = fm.getFormatFile(icon);
        if(Gambar.isHoverIcon(icon)|| Gambar.isAktifIcon(icon)){
            file = file.substring(0, file.lastIndexOf(".")-10) + "-aktif-"+ukuran;
        }else{
            file = file.substring(0, file.lastIndexOf(".")-4) + "-aktif-"+ukuran;
        }
//        System.out.println(file);
        return new ImageIcon(Gambar.DIREKTORY_ICON + file +format);
    }
    public static ImageIcon getNoBiasaIcon(String icon){
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
                format = fm.getFormatFile(icon);
        file = file.substring(0, file.lastIndexOf(".")-6);
        return new ImageIcon(Gambar.DIREKTORY_ICON + file+format);
    }
    public static ImageIcon getNoHoverIcon(String icon){
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
                format = fm.getFormatFile(icon);
        file = file.substring(0, file.lastIndexOf(".")) + "-hover";
        return new ImageIcon(Gambar.DIREKTORY_ICON + file+format);
    }
    public static ImageIcon getNoAktiveIcon(String icon){
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
                format = fm.getFormatFile(icon);
        if(Gambar.isNoHoverIcon(icon)|| Gambar.isNoAktifIcon(icon)){
            file = file.substring(0, file.lastIndexOf(".")-6) + "-aktif";
        }else{
            file = file.substring(0, file.lastIndexOf(".")) + "-aktif";
        }
        return new ImageIcon(Gambar.DIREKTORY_ICON + file+format);
    }
    
    
    public static ImageIcon getDarkIcon(String icon){
        System.out.println("icon "+icon);
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
                file1 = fm.getNamaFile(icon),
               format = fm.getFormatFile(icon);
//        System.out.println("file nya"+file);
//        System.out.println("format nya"+format);
        file = file.substring(0, file.lastIndexOf(".")) + "-dark";
//        System.out.println("file baru "+file);
        return new ImageIcon(Gambar.DIREKTORY_ICONS + file + format);
    }
    
    public static ImageIcon getWhiteIcon(String icon){
        FileManager fm = new FileManager();
        String file = fm.getNamaFile(icon),
               format = fm.getFormatFile(icon);
        
        file = file.substring(0, file.lastIndexOf("-"));
        
        return new ImageIcon(Gambar.DIREKTORY_ICONS + file + format);
    }
    
    public static void main(String[] args) {
        
//        System.out.println(Gambar.getDarkIcon("resources/icons/ic-window-sidemenu-logout.png"));
//        System.out.println(Gambar.getDarkIcon("icons/ic-window-sidemenu-logout.png"));
//        System.out.println(Gambar.getWhiteIcon("src\\resources\\image\\icons\\ic-window-sidemenu-supplier-dark.png"));
        System.out.println(Gambar.isDarkIcon("src\\resources\\image\\icons\\ic-window-sidemenu-supplier-dark.png"));
        
//        File f = new File("Gemastik_Lightning.jar");
//        System.out.println(f.isFile());
//        FileManager fm = new FileManager();
//        System.out.println(fm.getNamaFile(f.getName()));
        
    }
    public boolean ImageResize(String path, String file, String format, String newFormat, int Width, int Height){
        try {
            // Read the original image
            File inputFile = new File(path + file + format);
            BufferedImage inputImage = ImageIO.read(inputFile);
            
            // Define the new width and height for the image
//            int newWidth = 800;
//            int newHeight = 600;
            
            // Create a new BufferedImage object with the new width and height
            BufferedImage outputImage = new BufferedImage(Width, Height, inputImage.getType());
            
            // Draw the original image onto the new image with the new dimensions
            Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.drawImage(inputImage, 0, 0, Width, Height, null);
            graphics2D.dispose();
            
            // Save the new image to a file
            File outputFile = new File(path + file + newFormat);
            System.out.println("gambar diubah");
            return ImageIO.write(outputImage, newFormat, outputFile);
        } catch (IOException ex) {
            Logger.getLogger(Gambar.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

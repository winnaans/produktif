package com.manage;

import com.data.db.Database;
import com.data.app.Log;
import com.data.db.DatabaseTables;
import com.error.InValidUserDataException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amirzan Fikri Prasetyo
 */
public class Diskon extends Database {

    private enum DKN {
        ID_DISKON, NAMA_DISKON, JUMLAH_DISKON, MINIMAL_HARGA, TANGGAL_AWAL, TANGGAL_AKHIR
    };
    private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    private final DateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
    private final Text txt = new Text();

    public Diskon() {
        super.startConnection();
    }
    public String createID(){
        String lastID = this.getLastID(), nomor;
        if(lastID != null){
            nomor = lastID.substring(1);
        }else{
            System.out.println("diskon kosong "+ lastID);
            nomor = "000";
        }
        
        // mengecek nilai dari nomor adalah number atau tidak
        if(txt.isNumber(nomor)){
            // jika id barang belum exist maka id akan 
            return String.format("D%05d", Integer.parseInt(nomor)+1);
        }
        return null;
    }

    public boolean isExistDiskon(String idDiskon) {
        // mengecek apakah id diskon yang diinputkan valid atau tidak
        if (Validation.isIdUser(idDiskon)) {
            return super.isExistData(DatabaseTables.DISKON.name(), DKN.ID_DISKON.name(), idDiskon);
        }
        // akan menghasilkan error jika id diskon tidak valid
        throw new InValidUserDataException("'" + idDiskon + "' ID tersebut tidak valid.");
    }

    protected String getLastID() {
        try {
            String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 0,1", DatabaseTables.DISKON.name(), DKN.ID_DISKON.name());
            res = stat.executeQuery(query);
            if (res.next()) {
                System.out.println("kode diskon "+res.getString(DKN.ID_DISKON.name()));
                return res.getString(DKN.ID_DISKON.name());
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    public final boolean addDiskon(String namaDiskon, String jumlah, String minimal, String tanggalAwal, String tanggalAkhir) {
        try {
            PreparedStatement pst;
            String idDiskon = this.createID();
            // validasi data sebelum ditambahkan
            if (this.validateDataDiskon(idDiskon, namaDiskon, jumlah, minimal, tanggalAwal, tanggalAkhir)) {
                Log.addLog("Menambahkan data diskon dengan nama '" + namaDiskon + "'");
                // menambahkan data kedalam Database
                pst = this.conn.prepareStatement("INSERT INTO diskon VALUES (?, ?, ?, ?, ?, ?)");
                pst.setString(1, idDiskon);
                pst.setString(2, namaDiskon);
                pst.setInt(3, Integer.parseInt(jumlah));
                pst.setInt(4, Integer.parseInt(minimal));
                pst.setString(5, tanggalAwal);
                pst.setString(6, tanggalAkhir);
                // mengekusi query
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException | InValidUserDataException ex) {
            System.out.println("Error Message : " + ex.getMessage());
        }
        return false;
    }

    public boolean validateDataDiskon(String idDiskon, String namaDiskon, String jumlah, String minimal, String tanggalAwal, String tanggalAkhir) {
        try {
            Date TanggalAwal, TanggalAkhir;
            boolean vIdDiskon, vNama, vJumlah, vMinimal, vTanggalAwal, vTanggalAkhir;
            // mengecek id diskon valid atau tidak
            if (Validation.isIdDiskon(idDiskon)) {
                vIdDiskon = true;
            } else {
                throw new InValidUserDataException("'" + idDiskon + "' ID Diskon tersebut tidak valid.");
            }

            // menecek nama valid atau tidak
            if (Validation.isNamaDiskon(namaDiskon)) {
                vNama = true;
            } else {
                throw new InValidUserDataException("'" + namaDiskon + "' Nama Diskon tersebut tidak valid.");
            }

            // mengecek apakah jumlah diskon valid atau tidak
            if (Validation.isJumlahDiskon(jumlah)) {
                vJumlah = true;
            } else {
                throw new InValidUserDataException("'" + jumlah + "' Jumlah Diskon tersebut tidak valid.");
            }

            // mengecek apakah minimal pembelian valid atau tidak
            if (Validation.isMinimalPembelian(minimal)) {
                vMinimal = true;
            } else {
                throw new InValidUserDataException("'" + minimal + "' minimal pembelian tersebut tidak valid.");
            }

            // mengecek apakah tanggal awal valid atau tidak
            if (Validation.isTanggalAwal(tanggalAwal)) {
                vTanggalAwal = true;
            } else {
                throw new InValidUserDataException("'" + tanggalAwal + "' Tanggal Awal tersebut tidak valid.");
            }

            // mengecek apakah tanggal akhir valid atau tidak
            if (Validation.isTanggalAkhir(tanggalAwal, tanggalAkhir)) {
                TanggalAwal = date1.parse(tanggalAwal);
                TanggalAkhir = date1.parse(tanggalAkhir);
                System.out.println("mengecek tanggal diskon");
                if(TanggalAwal.compareTo(TanggalAkhir) <= 0){
                    System.out.println("tanggal benar");
                    vTanggalAkhir = true;
                }else{
                    vTanggalAkhir = false;
                    throw new InValidUserDataException("Tanggal Akhir tidak boleh kurang dari Tanggal Awal.");
                }
            } else {
                throw new InValidUserDataException("'" + tanggalAkhir + "' Tanggal Akhir tersebut tidak valid.");
            }

            return vIdDiskon && vNama && vJumlah && vMinimal && vTanggalAwal && vTanggalAkhir;
        } catch (ParseException ex) {
            Logger.getLogger(Diskon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteDiskon(String idDiskon) {
        Log.addLog("Menghapus diskon dengan ID '" + idDiskon + "'.");
        return this.deleteData(DatabaseTables.DISKON.name(), DKN.ID_DISKON.name(), idDiskon);
    }

    private String getDataDiskon(String idDiskon, DKN data) {
        // mengecek apakah id diskon exist atau tidak
        if (this.isExistDiskon(idDiskon)) {
            // mendapatkan data dari diskon
            return this.getData(DatabaseTables.DISKON.name(), data.name(), " WHERE " + DKN.ID_DISKON.name() + " = '" + idDiskon + "'");
        }
        // akan menghasilkan error jika id diskon tidak ditemukan
        throw new InValidUserDataException("'" + idDiskon + "' ID Diskon tersebut tidak dapat ditemukan.");
    }

    public String getNamaDiskon(String idDiskon) {
        return this.getDataDiskon(idDiskon, DKN.NAMA_DISKON);
    }

    public String getJumlah(String idDiskon) {
        return this.getDataDiskon(idDiskon, DKN.JUMLAH_DISKON);
    }

    public String getMinimal(String idDiskon) {
        return this.getDataDiskon(idDiskon, DKN.MINIMAL_HARGA);
    }

    public String getTanggalAwal(String idDiskon) {
        return this.getDataDiskon(idDiskon, DKN.TANGGAL_AWAL);
    }

    public String getTanggalAkhir(String idDiskon) {
        return this.getDataDiskon(idDiskon, DKN.TANGGAL_AKHIR);
    }

    private boolean setDataDiskon(String idDiskon, DKN data, String newValue) {
        Log.addLog("Mengedit data '" + data.name().toLowerCase() + "' dari diskon dengan ID Diskon '" + idDiskon + "'.");
        // mengecek apakah id diskon exist atau tidak
        if (this.isExistDiskon(idDiskon)) {
            // mengedit data dari diskon
            return this.setData(DatabaseTables.DISKON.name(), data.name(), DKN.ID_DISKON.name(), idDiskon, newValue);
        }
        // akan menghasilkan error jika id diskon tidak ditemukan
        throw new InValidUserDataException("'" + idDiskon + "' ID Diskon tersebut tidak dapat ditemukan.");
    }

    public boolean setNamaDiskon(String idDiskon, String newNama) {
        return this.setDataDiskon(idDiskon, DKN.NAMA_DISKON, newNama);
    }

    public boolean setJumlah(String idDiskon, String newJumlah) {
        return this.setDataDiskon(idDiskon, DKN.JUMLAH_DISKON, newJumlah);
    }

    public boolean setMinimal(String idDiskon, String newMinimal) {
        return this.setDataDiskon(idDiskon, DKN.MINIMAL_HARGA, newMinimal);
    }

    public boolean setTanggalAwal(String idDiskon, String newTanggalAwal) {
        return this.setDataDiskon(idDiskon, DKN.TANGGAL_AWAL, newTanggalAwal);
    }

    public boolean setTanggalAkhir(String idDiskon, String newTanggalAkhir) {
        return this.setDataDiskon(idDiskon, DKN.TANGGAL_AKHIR, newTanggalAkhir);
    }

    public static void main(String[] args) {

        Log.createLog();
        Diskon diskon = new Diskon();

//        System.out.println(diskon.isExistDiskon("BG017"));
//        System.out.println(diskon.getLastID());
//        System.out.println(diskon.createID());
//        System.out.println(diskon.addDiskon("Buku Sidu", "atk", 18, 3_500, 4_500));
//        System.out.println(diskon.deleteDiskon("BG017"));
//        String id = "BG017";
//        System.out.println(diskon.getNamaDiskon(id));
//        System.out.println(diskon.getJenis(id));
//        System.out.println(diskon.getStok(id));
//        System.out.println(diskon.getHargaBeli(id));
//        System.out.println(diskon.getHargaJual(id));
//        System.out.println(diskon.setNamaDiskon(id, "Beng Beng"));
//        System.out.println(diskon.setJenis(id, "snack"));
//        System.out.println(diskon.setStok(id, "15"));
//        System.out.println(diskon.setHargaBeli(id, "1500"));
//        System.out.println(diskon.setHargaJual(id, "2000"));
    }
}

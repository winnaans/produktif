package com.users;

import com.data.app.Log;
import com.data.db.Database;
import com.data.db.DatabaseTables;
import com.error.InValidUserDataException;
import com.manage.Message;
import com.manage.Text;
import com.manage.Validation;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Achmad Baihaqi
 */
public class Supplier extends Database {

    private final Text text = new Text();
    
    public Supplier(){
        this.startConnection();
    }
    public String createID() {
        return this.createIDnew("SUPPLIER", "ID_SUPPLIER");
    }

    public boolean isExistSupplier(String idSupplier) {
        return this.isExistID(idSupplier, "SUPPLIER", "ID_SUPPLIER");
    }

    protected boolean isExistID(String id, String tabel, String field) {
        // mengecek apakah id user yang diinputkan valid atau tidak
        if (Validation.isIdSupplier(id)) {
            return super.isExistData(tabel, field, id);
        }
//         akan menghasilkan error jika id supplier tidak valid
        throw new InValidUserDataException("'" + id + "' ID tersebut tidak valid.");
    }

    public final boolean addSupplier(String namaSupplier, String noTelp, String alamat) {
        PreparedStatement pst;
        String idSupplier = this.createID();

        try {
            // validasi data sebelum ditambahkan
            if (this.validateAddSupplier(idSupplier, namaSupplier, noTelp, alamat)) {
                Log.addLog("Menambahkan data supplier dengan nama '" + namaSupplier + "'");
                // menambahkan data kedalam Database
                pst = this.conn.prepareStatement("INSERT INTO supplier VALUES (?, ?, ?, ?)");
                pst.setString(1, idSupplier);
                pst.setString(2, text.toCapitalize(namaSupplier));
                pst.setString(3, noTelp);
                pst.setString(4, text.toCapitalize(alamat));

                // mengekusi query
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException | InValidUserDataException ex) {
            System.out.println("Error Message : " + ex.getMessage());
        }
        return false;
    }

    public boolean validateAddSupplier(String idSupplier, String namaSupplier, String noTelp, String alamat) {

        boolean vIdSupplier, vNama, vNoTelp, vAlamat;

        // mengecek id supplier valid atau tidak
        if (Validation.isIdSupplier(idSupplier)) {
            vIdSupplier = true;
        } else {
            throw new InValidUserDataException("'" + idSupplier + "' ID Supplier tersebut tidak valid.");
        }

        // menecek nama valid atau tidak
        if (Validation.isNamaOrang(namaSupplier)) {
            vNama = true;
        } else {
            throw new InValidUserDataException("'" + namaSupplier + "' Nama Supplier tersebut tidak valid.");
        }

        // mengecek apakah no hp valid atau tidak
        if (Validation.isNoHp(noTelp)) {
            vNoTelp = true;
        } else {
            throw new InValidUserDataException("'" + noTelp + "' No Telephone tersebut tidak valid.");
        }

        // mengecek apakah alamat valid atau tidak
        if (Validation.isNamaTempat(alamat)) {
            vAlamat = true;
        } else {
            throw new InValidUserDataException("'" + alamat + "' Alamat tersebut tidak valid.");
        }

        return vIdSupplier && vNama && vNoTelp && vAlamat;
    }

    public boolean deleteSupplier(String idSupplier) {
        Log.addLog("Menghapus akun dengan ID Supplier '" + idSupplier + "'.");
        return this.deleteData(DatabaseTables.SUPPLIER.name(), "id_supplier", idSupplier);
    }

    private String getDataSupplier(String idSupplier, String data) {
//        return super.getUserData(idSupplier, UserLevels.SUPPLIER, data, UserData.ID_SUPPLIER);
        this.startConnection();
        return this.getData("supplier", data, "WHERE id_supplier = '" + idSupplier + "'");
    }

    public String getNama(String idSupplier) {
        return this.getDataSupplier(idSupplier, "nama_supplier");
    }

    public String getNoTelp(String idSupplier) {
        return this.getDataSupplier(idSupplier, "NO_TELP");
    }

    public String getAlamat(String idSupplier) {
        return this.getDataSupplier(idSupplier, "ALAMAT");
    }

    private boolean setDataSupplier(String idSupplier, String data, String newValue) {
        return this.setSupplierData(idSupplier, "SUPPLIER", data, "ID_SUPPLIER", newValue);
    }

    private boolean setSupplierData(String idSupplier, String tabel, String data, String primary, String newValue) {
        Log.addLog("Mengedit data '" + data.toLowerCase() + "' dari akun dengan ID Supplier '" + idSupplier + "'.");
        return super.setData(tabel, data, primary, idSupplier, newValue);
    }

    public boolean setNama(String idSupplier, String newNama) {
        return this.setDataSupplier(idSupplier, "NAMA_SUPPLIER", newNama);
    }

    public boolean setNoTelp(String idSupplier, String newNoTelp) {
        return this.setDataSupplier(idSupplier, "NO_TELP", newNoTelp);
    }

    public boolean setAlamat(String idSupplier, String newAlamat) {
        return this.setDataSupplier(idSupplier, "ALAMAT", newAlamat);
    }

    protected String getLastIDnew(String level, String primary) {
        try {
            this.startConnection();
            String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 0,1", level, primary);
            this.res = this.stat.executeQuery(query);
            if (this.res.next()) {
                return this.res.getString(primary);
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private String createIDnew(String level, String primary) {
        String lastID = this.getLastIDnew(level, primary), nomor;
        
        if (lastID != null) {
            nomor = lastID.substring(2);
        } else {
            nomor = "000";
        }

        // mengecek nilai dari nomor adalah number atau tidak
        if (text.isNumber(nomor)) {
            // jika id user belum exist maka id akan 
            switch (level) {
//                case "KARYAWAN" : return String.format("PG%03d", Integer.parseInt(nomor)+1); // level admin dan karyawan
                case "SUPPLIER":
                    return String.format("SP%03d", Integer.parseInt(nomor) + 1);
                default:
                    System.out.println("Error!");
            }
        }
        return null;
    }

    public static void main(String[] args) {

        Log.createLog();
        Supplier supplier = new Supplier();
//        System.out.println(supplier.createID());
//        System.out.println(supplier.getNama("SP001"));
//        System.out.println(supplier.getNoTelp("SP001"));
//        System.out.println(supplier.getAlamat("SP001"));
//        System.out.println("");
//        System.out.println(supplier.setNama("SP001", "Amirzan Fikri Prasetyo"));
//        System.out.println(supplier.setNoTelp("SP001", "085790235810"));
//        System.out.println(supplier.setAlamat("SP001", "Jombang, Indonesia"));
//        System.out.println("");
//        System.out.println(supplier.getNama("SP001"));
//        System.out.println(supplier.getNoTelp("SP001"));
//        System.out.println(supplier.getAlamat("SP001"));
//        System.out.println("");
//        System.out.println(supplier.isExistSupplier("SP001"));
//        System.out.println(supplier.addSupplier("Mohammad Ilham Islamy", "086732905428", "Jombang, Jawa Timur"));
//        System.out.println(supplier.getNama("SP002"));
//        System.out.println(supplier.isExistSupplier("SP002"));
        System.out.println(supplier.deleteSupplier("SP002"));
//        System.out.println(supplier.addSupplier("Achmad Baihaqi", "086732905428", "Jombang, Jawa Timur"));
//        System.out.println(supplier.getNama("SP002"));
    }
}

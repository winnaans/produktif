package com.users;

import com.data.app.Log;
import com.data.app.Storage;
import com.data.db.Database;
import com.data.db.DatabaseTables;
import com.error.AuthenticationException;
import com.error.InValidUserDataException;
import com.manage.FileManager;
import com.manage.Message;
import com.manage.Text;
import com.manage.Validation;

import com.data.db.Hashing_Algorithm;
import com.window.frames.LoginWindow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ini digunakan untuk segala sesuatu yang berhubungan dengan akun dari
 * user seperti memanipulasi atau mendapatkan data dari akun user. Class ini
 * sangat ketergantungan terhadap <b>Database</b> aplikasi oleh karena itu class
 * ini merupakan inheritance dari class {@code Database}. Oleh karena itu jika
 * ada kesalahan terhadap <b>Database</b> aplikasi class ini juga akan mengalami
 * error yang menimbulkan terjadinya force close pada aplikasi.
 * <br><br>
 * Untuk memanipulasi atau mendapatkan data dari akun user class akan
 * memanfaatkan method-method yang ada didalam claas {@code Database}. Kita
 * hanya perlu menginputkan id user dari akun user untuk memanipulasi atau
 * mendapatkan data dari akun user. Sebelum memanipulasi atau mendapatkan data
 * dari user class akan mengecek apakah data yang diinputkan valid atau tidak.
 * <br><br>
 * Pengecekan perlu dilakukan untuk menghindari terjadinya error pada aplikasi
 * saat data yang diinputkan tidak valid. Class ini juga dapat digunakan untuk
 * menambahkan atau menghapus sebuah akun dari <b>Database</b> aplikasi. Cara
 * kerja class untuk menambahkan atau menghapus sebuah akun dari <b>Database</b>
 * hapir sama dengan cara kerja memanipulasi atau mendapatkan data dari user.
 * <br><br>
 * Selain itu class juga dapat digunakan untuk melakukan login dan logout pada
 * aplikasi. Untuk login pada aplikasi user cukup dengan memasukan id user dan
 * password dari akun. Jika id user dan password cocok dengan yang ada di
 * dldalam <b>Database</b> maka login akan dianggap berhasil. Setelah login
 * berhasil maka class akan menyimpan login data yang dilakukan oleh user
 * didalam <b>Database</b> dan folder yang ada didalam komputer.
 * <br><br>
 * Untuk logout aplikasi class akan menghapus login data yang ada didalam
 * <b>Database</b> dan yang ada didalam komputer. Jika proses penghapusan login
 * data berhasil maka logout akan dianggap behasil. Selama menggunakan class ini
 * mungkin akan akan sering menemui runtime/checked exception.
 * <br><br>
 * Beberapa exception yang mungkin sering anda jumpai adalah
 * {@code AuthenticationException} yang merupakan checked exception. Exception
 * tersebut akan sering dijumpai ketika terjadi kesalahan pada saat proses login
 * atau logout aplikasi. Exception yang lainya adalah
 * {@code InValidUserDataException}. Exception tersebut akan sering dijumpai
 * saat sedang memanipulasi atau mendapatkan data dari akun user.
 * <br><br>
 * Exception {@code InValidUserDataException} merupakan sebuah runtime
 * exception. Oleh karena itu disaat akan memanipulasi atau mendapkan data dari
 * user disarankan untuk membuat block try catch untuk menangkap pesan error
 * dari exception. Jika tidak ditangkap menggunakan block try catch maka ada
 * kemungkinan aplikasi akan force close.
 * <br><br>
 * Akun user pada aplikasi ini dibagi menjadi 3 level antara lain <i>ADMIN</i>,
 * <i>PETUGAS</i> dan <i>SISWA</i>. Pembagian diperlukan agar menajemen data
 * pada akun jauh lebih mudah. Data akun dari user yang memiliki level
 * <i>ADMIN</i> dan <i>PETUGAS</i> akan disimpan pada tabel karyawan. Semetara
 * data dari user yang memiliki level siswa akan disimpan pada tabel
 * <i>SISWA</i>.
 *
 * @author Achmad Bairizan
 * @since 2021-06-11
 */
public class Users extends Database {

    private Date date;
    private final Hashing_Algorithm hash = new Hashing_Algorithm();
    private final Text txt = new Text();

    /**
     * Direktori dari file yang digunakan untuk menyimpan data dari akun yang
     * sedang digunakan untuk login.
     */
    private final String LOGIN_DATA_FILE = new Storage().getUsersDir() + "login_data.rizan";

    /**
     * Merupakan satu-satunya constructor yang ada didalam class {@code Users}.
     * Saat constructor dipanggil saat pembuatan object constructor akan secara
     * otomatis memanggil method {@code startConnection()} yang ada didalam
     * class {@code Database} untuk membuat koneksi ke <b>Database MySQL</b>.
     * Setelah membuat koneksi ke <b>Database MySQL</b>
     * constructor akan mengecek apakah folder user storage dari aplikasi, file
     * login_data.rizan dan user storage dari user yang sedang login apakah
     * exist atau tidak.
     * <br><br>
     * Jika ada salah satu dari folder atau file tersebut yang tidak exist maka
     * folder dan file tersebut akan dibuat. Folder dan File tersebut sangatlah
     * penting karena jika tidak ada folder dan file tersebut akan menyebabkan
     * force close pada aplikasi.
     * <br><br>
     * Constructor dari class {@code Users} ini juga dapat untuk membuat object
     * dari inner class dari class {@code Users} yaitu {@code LevelPetugas} dan
     * {@code LevelSiswa}. Anda dapat membuat object dari kedua class tersebut
     * dengan memanggil method {@code levelPetugas()} untuk membuat object dari
     * class {@code LevelPetugas} dan method {@code levelSiswa()} untuk membuat
     * object dari class {@code LevelSiswa}.
     */
    public Users() {
        // memulai koneksi ke database
        this.startConnection();

        // jika storage tidak ditemukan maka akan dibuat
        if (!new File(new Storage().getUsersDir()).exists()) {
            new FileManager().createFolders(new Storage().getUsersDir());
        }

        // jika file login data tidak ditemukan maka file akan dibuat
        if (!new File(this.LOGIN_DATA_FILE).exists()) {
            new FileManager().createFile(this.LOGIN_DATA_FILE);
        }
    }

    /**
     * Method ini digunakan untuk menambahkan data dari user yang diinputkan
     * kedalam <b>Database MySQL</b>. Data dari user akan ditambahkan ke dalam
     * <b>Database</b> melalui class {@code PreparedStatement} sehingga proses
     * menambahkan data kedalam <b>Database</b> lebih aman. Pertama-tama method
     * akan mengecek apakah semua data dari user valid atau tidak. Jika ada
     * salah satu data dari user yang tidak valid maka data tidak akan
     * ditambahkan kedalam <b>Database</b> dan method akan mengembalikan nilai
     * <code>false</code>.
     * <br><br>
     * Jika semua data dari user valid maka method akan membuat sebuah object
     * {@code PreparedStatement}. Setelah object dari class
     * {@code PreparedStatement} berhasil dibuat selanjutnya method akan
     * menambahkan semua data dari user kedalam object
     * {@code PreparedStatement}. Jika semua data dari user sudah ditambahkan
     * kedalam object {@code PreparedStatement} maka data dari user tersebut
     * akan ditambahka kedalam <b>Database</b> melalui method
     * {@code executeUpdate()} yang ada didalam class {@code PreparedStatement}.
     *
     * @param username username dari ID karyawan
     * @param pass1 password dari user
     * @param level level dari user
     *
     * @return <strong>True</strong> jika data berhasil ditambahkan. <br>
     * <strong>False</strong> jika data tidak berhasil ditambahkan.
     *
     * @throws SQLException jika terjadi kegagalan saat menambahkan data kedalam
     * <b>Database</b>.
     * @throws InValidUserDataException jika data dari karyawan tidak valid.
     */
//    //digunakan untuk menambahkan karyawan dan user ke database
//    public final boolean addUser(String username, String password, UserLevels level,String idKaryawan) throws SQLException, InValidUserDataException{
//        Log.addLog("Menambahkan user baru dengan username '" + username + "' dengan level " + level.name());
//        PreparedStatement pst;
//        // mengecek apakah data yang akan ditambahkan valid atau tidak
//        if(this.validateAddUser(username, password, level)){
//            try {
//                Log.addLog("Data dari '" + username + "' dinyatakan valid.");
//                String hashing = hash.hash(password, 15);
//                // menambahkan data kedalam Database
//                pst = this.conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?)");
//                pst.setString(1, username);
//                pst.setString(2, hashing);
//                pst.setString(3, level.name());
//                pst.setString(4, idKaryawan);
//                
//                // mengekusi query
//                return pst.executeUpdate() > 0;
//            } catch (Exception ex) {
//                Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//        }
//        return false;
//    }
    /**
     * Method ini digunakan untuk mengecek apakah semua data dari user yang
     * diinputkan valid atau tidak. Method akan mengecek satu persatu data dari
     * user. Jika ada salah satu data saja yang tidak valid maka semua data dari
     * user yang di inputkan akan dianggap tidak valid dan method akan
     * mengembalikan nilai <code>False</code>. Method hanya akan mengembalikan
     * nilai <code>True</code> jika semua data dari user yang diinputkan valid.
     *
     * @param idUser ID dari user
     * @param pass password dari user
     * @param level level dari user
     *
     * @return <strong>True</strong> jika semua data dari user valid. <br>
     * <strong>False</strong> jika ada salah satu data dari user yang tidak
     * valid.
     */
    private boolean validateAddUser(String username, String pass, UserLevels level) {

        boolean vIdUser, vPassword, vLevel;

        // mengecek apakah id user valid atau tidak
        if (Validation.isIdUser(username)) {
            if (!this.isExistUser(username)) {
                vIdUser = true;
            } else {
                throw new InValidUserDataException("'" + username + "' Username tersebut sudah terpakai.");
            }
        } else {
            throw new InValidUserDataException("'" + username + "' Username tersebut tidak valid.");
        }

        // mengecek apakah password valid atau tidak
        if (Validation.isPassword(pass)) {
            vPassword = true;
        } else {
            throw new InValidUserDataException("'" + pass + "' Password tersebut tidak valid.");
        }

        // mengecek apakah user level valid atau tidak
        if (Validation.isLevel(level)) {
            vLevel = true;
        } else {
            throw new InValidUserDataException("'" + level.name() + "' level tersebut tidak valid.");
        }

        return vIdUser && vPassword && vLevel;
    }

    /**
     * Method ini digunakan untuk menghapus sebuah akun dari user yang ada
     * didalam <b>Database</b> berdasarkan id user yang diinputkan. Method akan
     * menghapus akun user dari <b>Database</b> melalui method
     * {@code deleteData()} yang ada didalam class {@code Database}. Method akan
     * mengembalikan nilai <code>True</code> jika akun dari user berhasil
     * dihapus.
     *
     * @param username id dari user yang ingin dihapus.
     *
     * @return <strong>True</strong> jika akun dari user berhasil dihapus. <br>
     * <strong>Fale</strong> jiak akun dari user tidak berhasil dihapus.
     */
    public final boolean deleteKaryawan1(String idKaryawan) {
        Log.addLog("Menghapus akun dengan idKaryawan '" + idKaryawan + "'.");
        return this.deleteData(DatabaseTables.USERS.name(), UserData.ID_KARYAWAN.name(), idKaryawan);
    }

    public final boolean deleteUser(String username) {
        Log.addLog("Menghapus akun dengan username '" + username + "'.");
        return this.deleteData(DatabaseTables.USERS.name(), UserData.USERNAME.name(), username);
    }

    public final boolean deleteSupplier(String idSupplier) {
        Log.addLog("Menghapus akun dengan ID Supplier '" + idSupplier + "'.");
        return this.deleteData(DatabaseTables.SUPPLIER.name(), UserData.ID_SUPPLIER.name(), idSupplier);
    }

    public boolean validateSetPassword(String idUser, String password) throws AuthenticationException, Exception {
        String leveluser = getLevel(idUser).name();
        // mengecek apakah id user valid atau tidak
        if (!Validation.isIdUser(idUser)) {
            throw new AuthenticationException("'" + idUser + "' Username tersebut tidak valid.");
        } else if (!this.isExistUser(idUser)) {
            throw new AuthenticationException("'" + idUser + "' Username tersebut tidak dapat ditemukan.");
        } else if (!(leveluser.equals("ADMIN") || leveluser.equals("KARYAWAN"))) {
            throw new AuthenticationException("'" + idUser + "' Username Bukan Admin atau Karyawan.");
            // mengecek apakah password valid atau tidak
        } else if (!Validation.isPassword(password)) {
            throw new AuthenticationException("Password lama yang anda masukan tidak valid.");
        } else if (!hash.checkpw(password, this.getPassword(idUser))) {
            throw new AuthenticationException("Password lama yang anda masukan tidak cocok.");
        } else {
            return true;
        }
    }

    /**
     * Digunakan untuk mengecek apakah user sudah melalukan Login pada Aplikasi
     * atau belum. Petama-tama method akan mendapatkan login data dari Aplikasi.
     * Selanjunya method akan mengecek apakah login data kosong atau tidak. Jika
     * login data kosong maka user akan dianggap belum melakukan login. Jika
     * login data tidak kosong maka method akan mengambil data id login dan id
     * user yang ada didalam login data melalui object dari class
     * {@code StringTokenizer}.
     *
     * @return <strong>True</strong> jika user sudah melakukan login. <br>
     * <strong>False</strong> jika user belum melakukan login.
     */
    public final boolean isLogin() {

        // object dan variabel digunakan untuk mengecek 
        String idUser = this.getLoginData();
        // jika login data tidak kosong
        if (idUser != null) {
            // mengecek apakah idUser yang dibuat untuk login exist atau tidak
            if (this.isExistUser(idUser)) {
                return true;
            }
        }
        return false;
    }

    public String getIdKaryawan(String username) {
        return super.getData(DatabaseTables.USERS.name(), "id_karyawan", "WHERE username = '" + username + "'");
    }

    /**
     * Method ini digunakan untuk melakukan Login pada Aplikasi. User dapat
     * melakukan Login pada Aplikasi cukup dengan menginputkan ID User beserta
     * passwordnya. Pertama-tama method akan mengecek apakah user sudah
     * melakukan login atau belum. Jika user sudah melakukan login maka method
     * akan menghasilkan exception {@code AuthenticationException}. Pertama-tama
     * method akan mengecek apakah ID User dan passwordnya valid atau tidak.
     * Jika ID User atau passwordnya tidak valid maka Login akan dibatalkan dan
     * method akan mengembalikan nilai <b>False</b>.
     * <br><br>
     * Jika ID User dan passwordya valid maka method akan membuat sebuah ID
     * Login baru. Setelah membuat ID Login method akan juga membuat login data
     * baru bedasarkan ID User yang diinputkan dan ID Login yang baru saja
     * dibuat. Setelah login data dibuat method akan menyimpan login data
     * tersebut kedalam file login_data.rizan yang ada didalam folder Storage
     * dengan menggunakan class {@code BufferedWriter}.
     * <br><br>
     * Jika login data sudah berhasil disimpan kedalam file maka selanjutnya
     * mehtod akan membuat sebuah object {@code PreparedStatement} yang
     * digunakan untuk menyimpan login data kedalam <b>Database</b>. Method juga
     * akan membuat folder user storage yang digunakan untuk menyimpan data dari
     * user berdasarkan ID User yang diinputkan. Jika login data berhasil
     * ditambahkan kedalam <b>Database</b> dan folder user storage berhasil
     * dibuat maka login dianggap berhasil dan method akan mengembalikan nilai
     * <code>True</code>.
     *
     * @param username id dari user yang akan melakukan login.
     * @param password password dari user.
     *
     * @return <strong>True</strong> jika login berhasil dilakukan. <br>
     * <strong>False</strong> jika login tidak berhasil dilakukan.
     *
     * @throws IOException jika terjadi kesalahan saat memanipulasi file
     * login_data.rizan.
     * @throws AuthenticationException jika user sudah melakukan login.
     * @throws SQLException jika terjadi kesalahan pada <b>Database</b>.
     */
    public final boolean login(String username, String password) throws IOException, AuthenticationException, SQLException, Exception {

        // mengecek apakah idUser dan password valid atau tidak
        if (this.validateLogin(username, password)) {
            Log.addLog("Melakukan Login dengan username : '" + username + "' dan dengan ID karyawan : '" + this.getIdKaryawan(username) + "'");

            // menyimpan login data kedalam file
            BufferedWriter save = new BufferedWriter(new FileWriter(this.LOGIN_DATA_FILE));
            save.write(username);
            save.flush();
            save.close();

            return true;
        }
        return false;
    }

    public boolean verifyPass(String pass, String hashing) {
        return hash.checkpw(pass, hashing);
    }

    private boolean validateLogin(String idUser, String password) throws AuthenticationException, Exception {
//        System.out.println("validasi login");
        String leveluser = getLevel(idUser).name();
        // mengecek apakah id user valid atau tidak
        if (!Validation.isIdUser(idUser)) {
            throw new AuthenticationException("'" + idUser + "' Username tersebut tidak valid.");
        } else if (!this.isExistUser(idUser)) {
            throw new AuthenticationException("'" + idUser + "' Username tersebut tidak dapat ditemukan.");
        } else if (!(leveluser.equals("ADMIN") || leveluser.equals("KARYAWAN"))) {
            throw new AuthenticationException("'" + idUser + "' Username Bukan Admin atau Karyawan.");
            // mengecek apakah password valid atau tidak
        } else if (!Validation.isPassword(password)) {
            throw new AuthenticationException("Password yang anda masukan tidak valid.");
        } else if (!hash.checkpw(password, this.getPassword(idUser))) {
            throw new AuthenticationException("Password yang anda masukan tidak cocok.");
        } else {
            return true;
        }
    }

    /**
     * Method ini digunakan untuk mendapatkan data akun yang sedang digunakan
     * untuk login (login data) pada Aplikasi. Login data disimpan pada file
     * <code>login_data.rizan</code> yang ada didalam folder Storage. Method
     * membaca data yang ada didalam file <code>login_data.rizan</code> dengan
     * melalui class {@code BufferedReader}.
     * <br><br>
     * <br><br>
     * <b>Contoh Login Data = ID User
     *
     * @return akan mengembalikan data akun yang sedang digunakan untuk login
     * (login data).
     */
    private String getLoginData() {
        // membaca semua data yang ada didalam file login_data.rizan
        try (BufferedReader data = new BufferedReader(new FileReader(this.LOGIN_DATA_FILE))) {
            // mengembalikan nilai loginData
            return data.readLine();
        } catch (IOException ex) {
            Message.showException(this, "Storage Corrupt!!", ex, true);
            System.exit(404);
        }
        return null;
    }

    /**
     * Method ini digunakan untuk mendapatkan ID User dari akun yang sedang
     * digunakan untuk Login. Pertama-tama method akan mengecek apakah user
     * sudah melakukan Login atau belum. Jika user belum melakukan Login maka
     * method akan mengembalikan nilai <code>null</code>. Tetapi jika user sudah
     * melakukan login method akan mendapatkan ID User yang berada didalam login
     * data.
     *
     * <br><br>
     * <b>Example : </b> KY001
     *
     * @return username dari akun yang sedang digunakan untuk Login.
     */
    public final String getCurrentLogin() {
        // mengecek apakah user sudah login atau belum
        if (this.isLogin()) {
            // mengembalikan username
            return this.getLoginData();
        }
        return null;
    }

    /**
     * Digunakan untuk mendapatkan data dari nama akun yang sedang digunakan
     * untuk login
     *
     * @return nama dari akun
     */
    public String getCurrentLoginName() {
        return this.getData(DatabaseTables.USERS.name(), "nama_karyawan", "WHERE id_karyawan = '" + this.getData(DatabaseTables.USERS.name(), "id_karyawan", "WHERE username = '" + this.getCurrentLogin() + "'") + "'");
    }

    /**
     * Method ini digunakan untuk melakukan Logout pada Aplikasi. Sebelum
     * melogout akun method akan mengecek apakah user sudah melakukan Login atau
     * belum. Jika user belum melakukan Login maka method akan menghasilkan
     * exception {@code AuthenticationException}.
     *
     * @return <strong>True</strong> jika Logout berhasil. <br>
     * <strong>False</strong> jika Logout tidak berhasil.
     *
     * @throws AuthenticationException jika user belum melakukan login.
     */
    public final boolean logout() throws AuthenticationException {
        try {
            // mengecek apakah user sudah melakukan login atau belum
            if (isLogin()) {
                Log.addLog("Melakukan Logout pada Akun dengan ID User : " + this.getCurrentLogin() + "'");
                // menghapus login data yang ada didalam database
                BufferedWriter buff = new BufferedWriter(new FileWriter(LOGIN_DATA_FILE));
                buff.write("");
                buff.flush();
                return true;
            }
        } catch (IOException ex) {
            Message.showException(null, ex, true);
        }
        throw new AuthenticationException("Gagal melogout akun!");
    }

    /**
     * Method ini digunakan untuk mengecek apakah sebuah ID User sudah exist
     * atau belum didalam <b>Database</b>.
     *
     * @param id ID User yang akan dicek.
     * @param level level dari user yang akan dicek id-nya
     * @param primary primary key dari level
     * @return <strong>True</strong> jika ID User exist. <br>
     * <strong>False</strong> jika ID User tidak exist.
     */
    protected boolean isExistID(String id, UserLevels level, UserData primary) {
        // mengecek apakah id user yang diinputkan valid atau tidak
        if (Validation.isIdUser(id)) {
            return super.isExistData(level.name(), primary.name(), id);
        }
//         akan menghasilkan error jika id user tidak valid
        throw new InValidUserDataException("'" + id + "' ID tersebut tidak valid.");
    }

    protected boolean isExistIDnew(String id, String level, String primary) {
        // mengecek apakah id user yang diinputkan valid atau tidak
        if (Validation.isIdUser(id)) {
            return super.isExistData(level, primary, id);
        }
//         akan menghasilkan error jika id user tidak valid
        throw new InValidUserDataException("'" + id + "' ID tersebut tidak valid.");
    }

    /**
     * Method ini digunakan untuk mengecek apakah sebuah ID User sudah exist
     * atau belum didalam <b>Database</b>. Pertama-tama method akan mengecek
     * apakah ID User valid atau tidak dengan menggunakan method
     * {@code isIdUser()} yang ada didalam class {@code Validation}. Jika ID
     * User tidak valid maka method akan menghasilkan exception
     * {@code InValidUserDataException}.
     * <br><br>
     * Method akan mengecek apakah sebuah ID User sudah exist atau belum didalam
     * <b>Database</b> dengan menggunakan method {@code isExistData()} yang ada
     * didalam class {@code Database}. Jika output dari method tersebut adalah
     * <code>True</code> maka ID User dinyatakan exist.
     *
     * @param idUser ID User yang akan dicek.
     * @return <strong>True</strong> jika ID User exist. <br>
     * <strong>False</strong> jika ID User tidak exist.
     */
    public final boolean isExistUser(String idUser) {
        return this.isExistID(idUser, UserLevels.USERS, UserData.USERNAME);
    }

    public final boolean isExistUser1(String idUser) {
        return this.isExistID(idUser, UserLevels.USERS, UserData.ID_KARYAWAN);
    }

    protected String getLastID(UserLevels level, UserData primary) {
        try {
            String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 0,1", level.name(), primary.name());
            res = stat.executeQuery(query);
            if (res.next()) {
                return res.getString(primary.name());
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    protected String getLastIDnew(String level, String primary) {
        try {
            String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 0,1", level, primary);
            res = stat.executeQuery(query);
            if (res.next()) {
                return res.getString(primary);
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    public String createID(UserLevels level, UserData primary) {
        String lastID = this.getLastID(level, primary), nomor;

        if (lastID != null) {
            nomor = lastID.substring(2);
        } else {
            nomor = "000";
        }

        // mengecek nilai dari nomor adalah number atau tidak
        if (txt.isNumber(nomor)) {
            // jika id user belum exist maka id akan 
            switch (level.name()) {
                case "USERS":
                    return String.format("KY%03d", Integer.parseInt(nomor) + 1); // level admin dan karyawan
//                case "SUPPLIER" : return String.format("SP%03d", Integer.parseInt(nomor)+1);
//                case "PEMBELI" : return String.format("PB%03d", Integer.parseInt(nomor)+1);
                default:
                    System.out.println("Error!");
            }
        }
        return null;
    }

    /**
     * Method ini akan mengembalikan data dari user berdasarkan Username yang
     * diinputkan. Pertama-tama method akan mengecek apakah ID User exist atau
     * tidak. Jika Username tidak exist maka akan menghasilkan exception
     * {@code InValidUserDataException}. Tetapi jika Username exist maka data
     * dari user akan didapatkan dengan melalui method {@code getData()} yang
     * ada didalam class {@code Database}.
     *
     * @param Username Username yang ingin didapatkan datanya
     * @param level level dari user (tabelnya apa)
     * @param data data yang akan diambil
     * @param primary primary key dari tabel
     * @return data dari user
     */
    protected String getUserData(String Username, UserLevels level, UserData data, UserData primary) {
        // mengecek apakah username tersedia atau tidak
        if (this.isExistUser(Username)) {
            // mendapatkan data dari user
            return this.getData(level.name(), data.name(), " WHERE " + primary.name() + " = '" + Username + "'");
        }
        // akan menghasilkan error jika id user tidak ditemukan
//        Message.showWarning(new LoginWindow(), "Username tersebut tidak dapat ditemukan !");
        throw new InValidUserDataException("'" + Username + "' Username tersebut tidak dapat ditemukan.");
    }

    /**
     * Method ini akan mengembalikan data dari user berdasarkan idKaryawan yang
     * diinputkan. Pertama-tama method akan mengecek apakah ID User exist atau
     * tidak. Jika idKaryawan tidak exist maka akan menghasilkan exception
     * {@code InValidUserDataException}. Tetapi jika idKaryawan exist maka data
     * dari user akan didapatkan dengan melalui method {@code getData()} yang
     * ada didalam class {@code Database}.
     *
     * @param idKaryawan idKaryawan yang ingin didapatkan datanya
     * @param level level dari user (tabelnya apa)
     * @param data data yang akan diambil
     * @param primary primary key dari tabel
     * @return data dari user
     */
    protected String getUserData1(String idKaryawan, UserLevels level, UserData data, UserData primary) {
        // mengecek apakah id karyawan tersedia atau tidak
        if (this.isExistUser1(idKaryawan)) {
            // mendapatkan data dari user
            return this.getData(level.name(), data.name(), " WHERE " + primary.name() + " = '" + idKaryawan + "'");
        }
        // akan menghasilkan error jika id user tidak ditemukan
//        Message.showWarning(new LoginWindow(), "Username tersebut tidak dapat ditemukan !");
        throw new InValidUserDataException("'" + idKaryawan + "' ID Karyawan tersebut tidak dapat ditemukan.");
    }

    /**
     * Method ini akan mengembalikan data dari user berdasarkan username yang
     * diinputkan. Pertama-tama method akan mengecek apakah ID User exist atau
     * tidak. Jika username tidak exist maka akan menghasilkan exception
     * {@code InValidUserDataException}. Tetapi jika username exist maka data
     * dari user akan didapatkan dengan melalui method {@code getData()} yang
     * ada didalam class {@code Database}.
     *
     * @param Username username yang ingin diambil datanya.
     * @param data data yang ingin diambil.
     * @return akan mengembalikan data dari user berdasarkan ID User yang
     * diinputkan.
     */
    public String getUserData(String Username, UserData data) {
        return this.getUserData(Username, UserLevels.USERS, data, UserData.USERNAME);
    }

    /**
     * Method ini akan mengembalikan data dari user berdasarkan ID karyawan yang
     * diinputkan. Pertama-tama method akan mengecek apakah ID User exist atau
     * tidak. Jika ID karyawan tidak exist maka akan menghasilkan exception
     * {@code InValidUserDataException}. Tetapi jika ID User exist maka data
     * dari user akan didapatkan dengan melalui method {@code getData()} yang
     * ada didalam class {@code Database}.
     *
     * @param idKaryawan username yang ingin diambil datanya.
     * @param data data yang ingin diambil.
     * @return akan mengembalikan data dari user berdasarkan ID User yang
     * diinputkan.
     */
    public String getUserData1(String idKaryawan, UserData data) {
        return this.getUserData1(idKaryawan, UserLevels.USERS, data, UserData.ID_KARYAWAN);
    }

    protected boolean setUserDataKaryawan(String idKaryawan, UserLevels level, UserData data, UserData primary, String newValue) {
        Log.addLog("Mengedit data '" + data.name().toLowerCase() + "' dari akun dengan idKaryawan '" + idKaryawan + "'.");
        // mengecek apakah id karyawan exist atau tidak
        if (this.isExistUser1(idKaryawan)) {
            // mengedit data dari karyawann
            return super.setData(level.name(), data.name(), primary.name(), idKaryawan, newValue);
        }
        // akan menghasilkan error jika id user tidak ditemukan
        throw new InValidUserDataException("'" + idKaryawan + "' ID User tersebut tidak dapat ditemukan.");
    }

    protected boolean setUserData(String idUser, UserLevels level, UserData data, UserData primary, String newValue) {
        Log.addLog("Mengedit data '" + data.name().toLowerCase() + "' dari akun dengan username '" + idUser + "'.");
        // mengecek apakah id user exist atau tidak
        if (this.isExistUser(idUser)) {
            // mengedit data dari user
            return super.setData(level.name(), data.name(), primary.name(), idUser, newValue);
        }
        // akan menghasilkan error jika id user tidak ditemukan
        throw new InValidUserDataException("'" + idUser + "' ID User tersebut tidak dapat ditemukan.");
    }

    protected boolean setUserDatanew(String idUser, String level, String data, String primary, String newValue) {
        Log.addLog("Mengedit data '" + data.toLowerCase() + "' dari akun dengan username '" + idUser + "'.");
        // mengecek apakah id user exist atau tidak
        if (this.isExistUser(idUser)) {
            // mengedit data dari user
            return super.setData(level, data, primary, idUser, newValue);
        }
        // akan menghasilkan error jika id user tidak ditemukan
        throw new InValidUserDataException("'" + idUser + "' ID User tersebut tidak dapat ditemukan.");
    }

    /**
     * Method ini digunakan untuk megedit data dari user berdasarkan ID User
     * yang diinputkan. Sebelum mengedit data method akan mengecek apakah ID
     * User exist atau tidak. Jika ID User tidak exist maka akan menghasilkan
     * exception {@code InValidUserDataException}. Tetapi jika ID User exist
     * maka method akan mengedit data dari user dengan menggunakan method
     * {@code setData()} yang ada didalam class {@code Database}. Jika data dari
     * user berhasil diedit maka method akan mengembalikan nilai
     * <code>True</code>.
     *
     * @param idUser ID User yang ingin diedit datanya.
     * @param data data dari ID User yang ingin diedit.
     * @param newValue nilai baru dari data yang ingin diedit.
     *
     * @return <strong>True</strong> jika data berhasil diedit. <br>
     * <strong>False</strong> jika data tidak berhasil diedit.
     */
    public boolean setUserData(String username, UserData data, String newValue) {
        Log.addLog("Mengedit data '" + data.name().toLowerCase() + "' dari akun dengan username '" + username + "'.");
        // mengecek apakah id user exist atau tidak
        if (this.isExistUser(username)) {
            // mengedit data dari user
            return super.setData(DatabaseTables.USERS.name(), data.name(), UserData.USERNAME.name(), username, newValue);
        }
        // akan menghasilkan error jika id user tidak ditemukan
        throw new InValidUserDataException("'" + username + "' username tersebut tidak dapat ditemukan.");
    }

    /**
     * Method ini digunakan untuk mendapatkan data Password dari user
     * berdasarkan ID User yang diinputkan. ID User yang diinputkan harus sudah
     * terdaftar didalam <b>Database</b>. Jika ID User yang diinputkan ternyata
     * tidak terdaftar didalam <b>Database</b> maka method akan menghasilkan
     * exception {@code InValidUserDataException}. Method hanya akan mendapatkan
     * data Password dari user jika ID User yang diinputkan terdaftar didalam
     * <b>Database</b>.
     *
     * @param idUser ID User yang ingin didapatkan datanya.
     * @return data Password dari ID User yang diinputkan.
     */
    public String getPassword(String idUser) {
        return this.getUserData(idUser, UserData.PASSWORD);
    }

    /**
     * Digunakan untuk mengedit data Password dari user berdasarkan ID User yang
     * diinputkan. Sebelum mengedit data Password method akan mengecek apakah
     * Password yang diinputkan valid atau tidak dengan menggunakan method
     * {@code isPassword(String password)} yang ada didalam class
     * {@code Validation}. Jika Password tidak valid maka method akan
     * menghasilkan exception {@code InValidUserDataException}.
     * <br><br>
     * Tetapi jika Password valid maka data Password dari user akan diedit. Jika
     * data dari Password berhasil diedit maka method akan mengembalikan nilai
     * <code>True</code>.
     *
     * @param username ID User yang ingin diedit datanya.
     * @param newPassword data Password yang baru.
     *
     * @return <strong>True</strong> jika data berhasil diedit. <br>
     * <strong>False</strong> jika data tidak berhasil diedit.
     */
    public boolean setPassword(String username, String newPassword) {
        // mengecek apakah password valid atau tidak
        if (Validation.isPassword(newPassword)) {
            // mengedit password dari user
            String hashing = hash.hash(newPassword, 15);
            return this.setUserData(username, UserData.PASSWORD, hashing);
        }
        // akan menghasilkan error jika password tidak valid
        throw new InValidUserDataException("'" + newPassword + "' Password tersebut tidak valid.");
    }

    /**
     * Digunakan untuk mengedit data Password dari user berdasarkan ID User dari
     * akun yang sedang digunakan untuk Login. Method akan mendapatkan ID User
     * dengan menggunakan method {@code getCurrentLogin()}. Selanjutnya method
     * akan mengedit data Password dari user melalui method
     * {@code setPassword(String idUser, String newPassword)}. Jika output dari
     * method tersebut adalah <code>True</code> maka data Password dari user
     * berhasil diedit.
     *
     * @param newPassword data Password yang baru.
     * @return <strong>True</strong> jika data berhasil diedit. <br>
     * <strong>False</strong> jika data tidak berhasil diedit.
     */
    public boolean setPassword(String newPassword) {
        return this.setPassword(getCurrentLogin(), newPassword);
    }

    /**
     * Method ini digunakan untuk mendapatkan data Level dari user berdasarkan
     * ID User yang diinputkan. ID User yang diinputkan harus sudah terdaftar
     * didalam <b>Database</b>. Jika ID User yang diinputkan ternyata tidak
     * terdaftar didalam <b>Database</b> maka method akan menghasilkan exception
     * {@code InValidUserDataException}. Method hanya akan mendapatkan data
     * Level dari user jika ID User yang diinputkan terdaftar didalam
     * <b>Database</b>.
     *
     * @param username username yang ingin didapatkan datanya.
     * @return data Level dari ID User yang diinputkan.
     */
    public UserLevels getLevel(String username) {
        return UserLevels.valueOf(this.getUserData(username, UserData.LEVEL));
    }

    /**
     * Method ini digunakan untuk mendapatkan data Level dari user berdasarkan
     * ID User yang diinputkan. ID User yang diinputkan harus sudah terdaftar
     * didalam <b>Database</b>. Jika ID User yang diinputkan ternyata tidak
     * terdaftar didalam <b>Database</b> maka method akan menghasilkan exception
     * {@code InValidUserDataException}. Method hanya akan mendapatkan data
     * Level dari user jika ID User yang diinputkan terdaftar didalam
     * <b>Database</b>.
     *
     * @param idKaryawan ID karyawan yang ingin didapatkan datanya.
     * @return data Level dari ID User yang diinputkan.
     */
    public UserLevels getLevel1(String idKaryawan) {
        return UserLevels.valueOf(this.getUserData1(idKaryawan, UserData.LEVEL));
    }

    /**
     * Method ini digunakan untuk mendapatkan data Level dari user berdasarkan
     * ID User dari akun yang sedang digunakan untuk Login. Method akan
     * mendapatkan ID User dengan menggunakan method {@code getCurrentLogin()}.
     * Selanjutnya method akan mendapatkan data Level dari user melalui method
     * {@code getLevel(String idUser)}. Jika user belum melakukan login maka
     * method akan mengembalikan nilai <code>null</code>.
     *
     * @return data Level dari akun yang sedang Login.
     */
    public UserLevels getLevel() {
        return this.getLevel(getCurrentLogin());
    }

    /**
     * Digunakan untuk mengedit data Level dari user berdasarkan ID User yang
     * diinputkan. Sebelum mengedit data Level method akan mengecek apakah Level
     * yang diinputkan valid atau tidak dengan menggunakan method
     * {@code isLevel(String level)} yang ada didalam class {@code Validation}.
     * Jika Level tidak valid maka method akan menghasilkan exception
     * {@code InValidUserDataException}.
     *
     * @param username username yang ingin diedit levelnya
     * @param newLevel data Password yang baru.
     * @return <strong>True</strong> jika data berhasil diedit. <br>
     * <strong>False</strong> jika data tidak berhasil diedit.
     */
    public boolean setLevel(String username, UserLevels newLevel) {
        if (Validation.isLevel(newLevel)) {
            return this.setUserData(username, UserData.LEVEL, newLevel.name());
        }
        // akan menghasilkan error jika password tidak valid
        throw new InValidUserDataException("'" + newLevel + "' Level tersebut tidak valid.");
    }

    /**
     * Digunakan untuk mengecek apakah Level dari ID User yang diinputkan
     * memiliki Level <b>ADMIN</b> atau tidak. Method akan mendapatkan data
     * Level dari ID User dengan menggunakan method {@code getLevel()}. Jika
     * output dari method tersebut adalah <b>ADMIN</b> maka method akan
     * mengembalikan nilai <code>True</code>.
     *
     * @param idKaryawan ID Karyawan yang akan dicek.
     * @return <strong>True</strong> jika level dari user adalah <b>ADMIN</b>.
     * <br>
     * <strong>False</strong> otherwise.
     */
    public boolean isAdmin1(String idKaryawan) {
        return this.getLevel1(idKaryawan).name().equals("ADMIN");
    }

    public boolean isAdmin(String username) {
        return this.getLevel(username).name().equals("ADMIN");
    }

    /**
     * Digunakan untuk mengecek apakah Level dari akun yang sedang digunakan
     * untuk Login apakah memiliki Level
     * <b>ADMIN</b> atau tidak. Method akan mendapatkan ID User dari akun yang
     * sedang digunakan untuk Login dengan menggunakan method
     * {@code getCurrentLogin()}. Selanjutnya method akan mengecek apakah user
     * memiliki Level
     * <b>ADMIN</b> atau tidak melalui method {@code isAdmin(String idUser)}.
     *
     * @return <strong>True</strong> jika level dari user adalah <b>ADMIN</b>.
     * <br>
     * <strong>False</strong> otherwise.
     */
    public boolean isAdmin() {
        return this.isAdmin(getCurrentLogin());
    }

    /**
     * Method ini digunakan untuk mendapatkan total user yang terdaftar di
     * <b>Database</b> aplikasi. Method akan mendapatkan data total user dengan
     * melalui method {@code getJumlahData()} yang ada didalam class
     * {@code Database}.
     *
     * @return total user yang terdaftar di aplikasi.
     */
    public int getTotalUser() {
        return super.getJumlahData(DatabaseTables.USERS.name());
    }

    public String getRfid(String idKaryawan) {
        return this.getUserData1(idKaryawan, UserData.RFID);
    }

    public boolean setRFID(String username, String rfid) {
        if (Validation.isRfid(rfid)) {
            return this.setUserData(username, UserData.RFID, rfid);
        }
        // akan menghasilkan error jika kode rfid tidak valid
        throw new InValidUserDataException("'" + rfid + "' Kode RFID tersebut tidak valid.");
    }

    public static void main(String[] args) {
        Log.createLog();
        Users user = new Users();
        try {

            System.out.println(user.logout());
        } catch (AuthenticationException ex) {
            Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //karyawan 
    private final Text text = new Text();

    //digunakan untuk membuat id karyawan
    public String karyawanCreateID() {
        return this.createID(UserLevels.USERS, UserData.ID_KARYAWAN);
    }

    //digunakan untuk mengecek data karyawan apakah ada atau tidak 
    public boolean karyawanIsExistkaryawan(String idKaryawan) {
        return this.isExistID(idKaryawan, UserLevels.USERS, UserData.ID_KARYAWAN);
    }

    //digunakan utuk menambahkan karyawan dan user 
    public final boolean addKaryawan(String namaKaryawan, String noTelp, String alamat, String pass, UserLevels level, String username, String rfid) {
        boolean isAdd = false;
        PreparedStatement pst;
        String idKaryawan = this.karyawanCreateID();
        try {
            // validasi data sebelum ditambahkan
            if (this.validateAddKaryawan(idKaryawan, namaKaryawan, noTelp, alamat)) {
                if (this.validateAddUser(username, pass, level)) {
                    Log.addLog("Menambahkan data karyawan dengan nama '" + namaKaryawan + "'");
                    String hashing = hash.hash(pass, 15);
                    // menambahkan data kedalam Database
                    pst = this.conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                    pst.setString(1, idKaryawan);
                    pst.setString(2, text.toCapitalize(namaKaryawan));
                    pst.setString(3, noTelp);
                    pst.setString(4, text.toCapitalize(alamat));
                    pst.setString(5, username);
                    pst.setString(6, hashing);
                    pst.setString(7, level.name());
                    if (rfid.length() < 10) {
                        pst.setString(8, null);
                    } else {
                        pst.setString(8, rfid);
                    }
                    // mengekusi query
                    isAdd = pst.executeUpdate() > 0;
                }
            }
            return isAdd;
            // mengecek apakah karyawan sudah ditambahkan ke tabel user
//            if (isAdd) {
//                // menambahkan data user ke tabel user
//                return this.addUser(username, pass, level, idKaryawan);
//            }
        } catch (SQLException | InValidUserDataException ex) {
            this.deleteKaryawan(idKaryawan);
            System.out.println("Error Message : " + ex.getMessage());
        }
        return false;
    }

    public boolean validateAddKaryawan(String idKaryawan, String namaKaryawan, String noTelp, String alamat) {

        boolean vIdPetugas, vNama, vNoTelp, vAlamat;

        // mengecek id petugas valid atau tidak
        if (Validation.isIdKaryawan(idKaryawan)) {
            vIdPetugas = true;
        } else {
            throw new InValidUserDataException("'" + idKaryawan + "' ID Karyawan tersebut tidak valid.");
        }

        // menecek nama valid atau tidak
        if (Validation.isNamaOrang(namaKaryawan)) {
            vNama = true;
        } else {
            throw new InValidUserDataException("'" + namaKaryawan + "' Nama Karyawan tersebut tidak valid.");
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

        return vIdPetugas && vNama && vNoTelp && vAlamat;
    }

    public boolean validateDataKaryawan(String idKaryawan, String namaKaryawan, String noTelp, String alamat, String pass, UserLevels level, String username) {

        boolean vIdPetugas, vNama, vNoTelp, vAlamat, vPass, vLevel, vUsername;

        // mengecek id karyawan valid atau tidak
        if (Validation.isIdKaryawan(idKaryawan)) {
            vIdPetugas = true;
        } else {
            throw new InValidUserDataException("'" + idKaryawan + "' ID Karyawan tersebut tidak valid.");
        }

        // menecek nama valid atau tidak
        if (Validation.isNamaOrang(namaKaryawan)) {
            vNama = true;
        } else {
            throw new InValidUserDataException("'" + namaKaryawan + "' Nama Karyawan tersebut tidak valid.");
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

//         mengecek apakah password valid atau tidak
        if (Validation.isPassword(pass)) {
            vPass = true;
        } else {
            throw new InValidUserDataException("'" + pass + "' Password tersebut tidak valid.");
        }

//         mengecek apakah level valid atau tidak
        if (Validation.isLevel(level)) {
            vLevel = true;
        } else {
            throw new InValidUserDataException("'" + level + "' Level tersebut tidak valid.");
        }
        if (Validation.isUsername(username)) {
            vLevel = true;
        } else {
            throw new InValidUserDataException("'" + level + "' Level tersebut tidak valid.");
        }

        return vIdPetugas && vNama && vNoTelp && vAlamat && vPass && vLevel;
    }

    public String getUsernameKaryawan(String idKaryawan) {
        return super.getData(DatabaseTables.USERS.name(), "username", "WHERE id_karyawan = '" + idKaryawan + "'");
    }
//    public String getIdKaryawan(String username) {
//        return super.getData(DatabaseTables.USERS.name(), "id_karyawan", "WHERE username = '" + username + "'");
//    }

    public boolean deleteKaryawan(String idKaryawan) {
        Log.addLog("Menghapus akun dengan ID Karyawan'" + idKaryawan + "'.");
        return super.deleteData(DatabaseTables.USERS.name(), "id_karyawan", idKaryawan);
    }

    private String getDataKaryawan(String idKaryawan, UserData data) {
        return this.getUserData1(idKaryawan, UserLevels.USERS, data, UserData.ID_KARYAWAN);
    }

    public String getNamaKaryawan(String idKaryawan) {
        return this.getDataKaryawan(idKaryawan, UserData.NAMA_KARYAWAN);
    }

    public String getRfidKaryawan(String idKaryawan) {
        return this.getDataKaryawan(idKaryawan, UserData.RFID);
    }

    public String getNoTelpKaryawan(String idKaryawan) {
        return this.getDataKaryawan(idKaryawan, UserData.NO_TELP);
    }

    public String getAlamatKaryawan(String idKaryawan) {
        return this.getDataKaryawan(idKaryawan, UserData.ALAMAT);
    }

    private boolean setDataKaryawan(String idKaryawan, UserData data, String newValue) {
        return this.setUserDataKaryawan(idKaryawan, UserLevels.USERS, data, UserData.ID_KARYAWAN, newValue);
    }

    public boolean setNamaKaryawan(String idKaryawan, String newNama) {
        return this.setDataKaryawan(idKaryawan, UserData.NAMA_KARYAWAN, newNama);
    }

    public boolean setNoTelpKaryawan(String idKaryawan, String newNoTelp) {
        return this.setDataKaryawan(idKaryawan, UserData.NO_TELP, newNoTelp);
    }

    public boolean setAlamatKaryawan(String idKaryawan, String newAlamat) {
        return this.setDataKaryawan(idKaryawan, UserData.ALAMAT, newAlamat);
    }

    public String rfid(String rfid) {
        try {
            String query = "SELECT id_karyawan, username FROM users WHERE rfid = " + rfid, username = "", idKaryawan = "";
            res = stat.executeQuery(query);
            if (res.next()) {
                username = res.getString("username");
                idKaryawan = res.getString("id_karyawan");
                Log.addLog("Melakukan Login dengan username : '" + username + "' dan dengan ID karyawan : '" + this.getIdKaryawan(idKaryawan) + "'");
                // menyimpan login data kedalam file
                BufferedWriter save = new BufferedWriter(new FileWriter(this.LOGIN_DATA_FILE));
                save.write(username);
                save.flush();
                save.close();
                return username;
            }
            return "";
        } catch (SQLException | IOException ex) {
            Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public String saldoCreateID() {
        String lastID = this.getIdSaldo(""), nomor;
        if (lastID != null) {
            nomor = lastID.substring(1);
        } else {
            nomor = "000000000";
        }

        // mengecek nilai dari nomor adalah number atau tidak
        if (text.isNumber(nomor)) {
            // jika id saldo belum exist maka id akan 
            return String.format("S%09d", Integer.parseInt(nomor) + 1);
        }
        return null;
    }

    //mendapatkan id saldo terakhir dari database
    public String getIdSaldo(String keywordSaldo) {
        try {
            String sql = "SELECT id_saldo FROM saldo ORDER BY id_saldo DESC LIMIT 0,1" + keywordSaldo;
            super.res = super.stat.executeQuery(sql);
            while (super.res.next()) {
                return super.res.getString("id_saldo");
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return "";
    }

    //mendapatkan data saldo dari database
    public int getSaldo(String idSaldo, String keywordSaldo) {
        try {
            String sql = "SELECT jumlah_saldo FROM saldo ORDER BY id_saldo DESC LIMIT 0,1" + keywordSaldo;
            res = stat.executeQuery(sql);
            while (res.next()) {
                return res.getInt("jumlah_saldo");
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return 0;
    }

    public boolean editSaldo(int saldo) {
        PreparedStatement pst;
        try {
            String sql3 = "INSERT INTO saldo VALUES (?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(sql3);
            pst.setString(1, this.saldoCreateID());
            pst.setInt(2, saldo);
            pst.setString(3,"edit saldo");
            pst.setString(4, null);
            pst.setString(5, null);
            if (pst.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return false;
    }
//    public static void main(String[] args) {

//        Log.createLog();
//        Karyawan karyawan = new Karyawan();
//        System.out.println(petugas.getNama("PG002"));
//        System.out.println(petugas.getNoTelp("PG002"));
//        System.out.println(petugas.getAlamat("PG002"));
//        System.out.println(petugas.getNoTelp("PG002"));
//        System.out.println("");
//        System.out.println(petugas.deletePetugas("PG005"));
//    }
}

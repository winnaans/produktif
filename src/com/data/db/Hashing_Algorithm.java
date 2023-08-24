package com.data.db;
import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;
public class Hashing_Algorithm {
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    static public String hash(String password,int i) {
        return BCrypt.hashpw(password, BCrypt.gensalt(i));
    }
    public String hash(String password,int i,SecureRandom sr) {
        return BCrypt.hashpw(password, BCrypt.gensalt(i,sr));
    }
    public String hashSalt(String password,String salt) {
        return BCrypt.hashpw(password, salt);
    }
    public String salt() {
        return BCrypt.gensalt();
    }
    public String salt(int i){
        return BCrypt.gensalt(i);
    }
    public String salt(int i, SecureRandom sr){
        return BCrypt.gensalt(i, sr);
    }
    public boolean checkpw(String pass,String hash){
        return BCrypt.checkpw(pass, hash);
    }
    public static void main(String[] args) {
        System.out.println("hasil \n"+hash("Admin@123456789",15));
    }
}

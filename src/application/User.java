package application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class User {

	private String username, password, salt;
	
	protected User(String usn, String pass) {
		username=usn;
		salt=generateSalt();
		password=hash(pass, salt);
		
	}
	
	protected User() {}

	private String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String toHash = password + salt;
            byte[] hashedBytes = md.digest(toHash.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

	private String generateSalt() {
		byte[] saltbytes=new byte[16];
		new SecureRandom().nextBytes(saltbytes);
		return Base64.getEncoder().encodeToString(saltbytes);
	}
	
	protected static User fromFile(String username, String hash, String salt) {
		User us=new User();
		us.username=username;
		us.password=hash;
		us.salt=salt;
		return us;
	}
	
	protected boolean verifyPassword(String inputPass) {
		String hashPass=hash(inputPass, salt);
		return hashPass.equals(password);
	}
	
	protected String toCSV() {
		return username+";"+password+";"+salt;
	}
	
	protected String getUsername() {
		return username;
	}
	
	protected String getPassword() {
		return password;
	}
	
	protected String getSalt() {
		return salt;
	}
	
}

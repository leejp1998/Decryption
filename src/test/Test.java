package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			decrypt();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void decrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException{
		KeyGenerator generator;
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		SecretKey secretKey = null;
		byte[] iv = null;
		
		File encryptedFilePath = new File("C://Users//julee//SharedFolder//Dummy/Dummy4_encrypted.pdf"); //specify
		FileInputStream fis = new FileInputStream(encryptedFilePath);
		File decryptedFilePath = new File("C://Users//julee//SharedFolder//Dummy/Dummy4_decrypted.pdf"); //specify
		FileOutputStream fos = new FileOutputStream(decryptedFilePath, false);
		
		// Read iv
		File ivFilePath = new File("C://Users//julee//SharedFolder//Dummy/Dummy4_iv.key");
		FileInputStream ivFis = new FileInputStream(ivFilePath);
		ObjectInputStream ivois = new ObjectInputStream(ivFis);
		try {
			iv = (byte[]) ivois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ivois.close();
		}
		
		// Read key
		File keyFilePath = new File("C://Users//julee//SharedFolder//Dummy/Dummy4_key.key");
		FileInputStream keyFis = new FileInputStream(keyFilePath);
		ObjectInputStream keyois = new ObjectInputStream(keyFis);
		try {
			secretKey = (SecretKey) keyois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			keyois.close();
		}
		
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv); // is the same iv
		//cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, (Key) secretKey);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		
		int byteRead = 0;
		byte[] plainText = new byte[4096];
		
		while((byteRead = cis.read(plainText)) >= 0) {
			fos.write(plainText, 0, byteRead);
		}
		fos.flush();
		fos.close();
		cis.close();
	}

}

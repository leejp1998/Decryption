package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

public class Test {

	static String ENCRYPTED_FILE_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\encrypted";
	static String DECRYPTED_FILE_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\decrypted";
	//final static String IV_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\iv.key";
	//final static String KEY_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\key.key";
	static String filename = "";
	static byte[]iv = null;
	static SecretKey secretKey = null;
	
	public static void main(String[] args) throws IOException, FileNotFoundException {
		
		Socket s = null;
		Socket s1 = null;
		Socket s2 = null;
		Socket s3 = null;
		ServerSocket ss = null;
		ServerSocket ss1 = null;
		ServerSocket ss2 = null;
		ServerSocket ss3 = null;
		ObjectInputStream is = null;
		DataInputStream is1 = null;
		ObjectInputStream is2 = null;
		DataInputStream is3 = null;
		//InetAddress inet = InetAddress.getLocalHost();  // return 172.30.0.14
		
		//Get the FileOutputStream from sender (Android Studio), convert it to FileInputStream
		try {
			ss = new ServerSocket(6000);
			ss1 = new ServerSocket(6001);
			ss2 = new ServerSocket(6002);
			ss3 = new ServerSocket(6003);
			// Server socket waits 60000ms for connection.
			ss.setSoTimeout(30000);
			ss1.setSoTimeout(30000);
			ss2.setSoTimeout(30000);
			ss3.setSoTimeout(30000);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		try {
			s=ss.accept();
			s1 = ss1.accept();
			s2 = ss2.accept();
			s3 = ss3.accept();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		try {
			is3 = new DataInputStream(s3.getInputStream());
			filename = is3.readUTF();
			is3.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		ENCRYPTED_FILE_PATH = ENCRYPTED_FILE_PATH + filename;
		DECRYPTED_FILE_PATH = DECRYPTED_FILE_PATH + filename;
		
		File FILE_TO_RECEIVE = new File(ENCRYPTED_FILE_PATH);
		FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVE);
		try {
			is = new ObjectInputStream(s.getInputStream());
			final BufferedOutputStream Bos = new BufferedOutputStream(fos);
			byte[] mybytearray = (byte[])is.readObject();
			Bos.write(mybytearray);
			Bos.flush();
			Bos.close();
			is.close();
			System.out.println(FILE_TO_RECEIVE.length() + " bytes are read.");
			
			
			// Taking IV from client as a byte array
			// This reads IV
			is1 = new DataInputStream(s1.getInputStream());
			int length = is1.readInt();
			if(length > 0) {
				iv = new byte[length];
				is1.readFully(iv, 0, iv.length);
			}
			is1.close();
			
			// This reads the key
			is2 = new ObjectInputStream(s2.getInputStream());
			secretKey = (SecretKey) is2.readObject();
			is2.close();

			System.out.println("successfully received the file");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		
		  try { decrypt(); } catch (InvalidKeyException | NoSuchAlgorithmException |
		  NoSuchPaddingException | InvalidAlgorithmParameterException | IOException e)
		  { e.printStackTrace(); }
		 
	}
	

	private static void decrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException{

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		File encryptedFilePath = new File(ENCRYPTED_FILE_PATH); //specify
		FileInputStream fis = new FileInputStream(encryptedFilePath);
		File decryptedFilePath = new File(DECRYPTED_FILE_PATH); //specify
		FileOutputStream fos = new FileOutputStream(decryptedFilePath, false);
		GCMParameterSpec ivParameterSpec = new GCMParameterSpec(128, iv, 0, 12); // is the same iv
		
		cipher.init(Cipher.DECRYPT_MODE, (Key) secretKey, ivParameterSpec);
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

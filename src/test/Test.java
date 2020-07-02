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

	final static String ENCRYPTED_FILE_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\Dummy1_encrypted.pdf";
	final static String DECRYPTED_FILE_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\Dummy1_decrypted.pdf";
	final static String IV_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\Dummy1_iv.key";
	final static String KEY_PATH = "C:\\Users\\julee\\Desktop\\file_transfer_test\\Dummy1_key.key";
	static byte[]iv = null;
	static SecretKey secretKey = null;
	
	public static void main(String[] args) throws IOException, FileNotFoundException {
		
		//BufferedOutputStream bos = new BufferedOutputStream(fos);
		Socket s = null;
		Socket s1 = null;
		Socket s2 = null;
		ServerSocket ss = null;
		ServerSocket ss1 = null;
		ServerSocket ss2 = null;
		ObjectInputStream is = null;
		DataInputStream is1 = null;
		ObjectInputStream is2 = null;
		InetAddress inet = InetAddress.getLocalHost();  // return 172.30.0.14
		//String hostname = inet.getHostName(); // r0eturn D15JuleeLT
		
		//Get the FileOutputStream from sender (Android Studio), convert it to FileInputStream
		try {
			ss = new ServerSocket(6000);
			ss1 = new ServerSocket(6001);
			ss2 = new ServerSocket(6002);
			// Server socket waits 60000ms for connection.
			ss.setSoTimeout(30000);
			ss1.setSoTimeout(30000);
			ss2.setSoTimeout(30000);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		try {
			s=ss.accept();
			s1 = ss1.accept();
			s2 = ss2.accept();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		File FILE_TO_RECEIVE = new File(ENCRYPTED_FILE_PATH); //specify
		File IV_TO_RECEIVE = new File(IV_PATH);
		File KEY_TO_RECEIVE = new File(KEY_PATH);
		FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVE);
		FileOutputStream fos1 = new FileOutputStream(IV_TO_RECEIVE);
		FileOutputStream fos2 = new FileOutputStream(KEY_TO_RECEIVE);
		try {
			/*
			 * is = new DataInputStream(s.getInputStream());
			 * System.out.println("encrypted file size: "+ is.available()); //29482 bytes =
			 * 28.8KB
			 * 
			 * byte[] plainText = new byte[4096]; int bytesRead = is.read(plainText);
			 * while((bytesRead = is.read(plainText)) >= 0) { fos.write(plainText, 0,
			 * bytesRead); } System.out.println(FILE_TO_RECEIVE.length());
			 */
			is = new ObjectInputStream(s.getInputStream());
			final BufferedOutputStream Bos = new BufferedOutputStream(fos);
			byte[] mybytearray = (byte[])is.readObject();
			byte[] plainText = new byte[4096];
			int BytesRead = is.read(plainText);
			Bos.write(mybytearray);
			//bos.flush();
			Bos.flush();
			Bos.close();
			System.out.println(FILE_TO_RECEIVE.length());
			
			
			// Taking IV from client as a byte array
			// This reads IV
			is1 = new DataInputStream(s1.getInputStream());
			int length = is1.readInt();
			if(length > 0) {
				iv = new byte[length];
				is1.readFully(iv, 0, iv.length);
			}
			
			is2 = new ObjectInputStream(s2.getInputStream());
			secretKey = (SecretKey) is2.readObject();
			is2.close();
//			secretKey = (SecretKey)is2.readObject();
			
			
	//// I NEED TO SAVE THIS AS OBJECTOUTPUTSTREAM not BUFFEREDOUTPUTSTREAM... 		
	/*
	 * final BufferedOutputStream Bos2 = new BufferedOutputStream(fos2); byte[]
	 * mybytearray2 = (byte[])is2.readObject(); byte[] plainText2 = new byte[4096];
	 * int BytesRead2 = is2.read(plainText2); Bos2.write(mybytearray2);
	 * 
	 * Bos2.flush(); Bos2.close();
	 */
			 
			System.out.println(KEY_TO_RECEIVE.length()); // SHould be 141
			//bos.close();
			//s.close();
			//ss.close();
			/*
			 * is1 = new ObjectInputStream(s1.getInputStream());
			 * System.out.println("IV file size: "+ is1.available());
			 * 
			 * 
			 * byte[] plainText1 = new byte[4096]; int bytesRead1 = is.read(plainText1);
			 * while((bytesRead1 = is.read(plainText1)) >= 0) { fos1.write(plainText1, 0,
			 * bytesRead1); }
			 * 
			 * 
			 * //bos.flush(); fos1.flush(); fos1.close(); //bos.close(); s1.close();
			 * ss1.close();
			 * 
			 * is2 = new ObjectInputStream(s2.getInputStream());
			 * System.out.println("Key file size: "+ is2.available());
			 * 
			 * byte[] plainText2 = new byte[4096]; int bytesRead2 = is.read(plainText2);
			 * while((bytesRead2 = is.read(plainText2)) >= 0) { fos2.write(plainText2, 0,
			 * bytesRead2); } //bos.flush(); fos2.flush(); fos2.close(); //bos.close();
			 * s2.close(); ss2.close();
			 */
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
		KeyGenerator generator;
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		/*
		 * SecretKey secretKey = null; byte[] iv = null;
		 */
		
		File encryptedFilePath = new File(ENCRYPTED_FILE_PATH); //specify
		FileInputStream fis = new FileInputStream(encryptedFilePath);
		
		File decryptedFilePath = new File(DECRYPTED_FILE_PATH); //specify
		FileOutputStream fos = new FileOutputStream(decryptedFilePath, false);
	
		 
		
		// Read key
		/*
		 * File keyFilePath = new File(KEY_PATH); FileInputStream keyFis = new
		 * FileInputStream(keyFilePath); ObjectInputStream keyois = new
		 * ObjectInputStream(keyFis); try { secretKey = (SecretKey) keyois.readObject();
		 * } catch (ClassNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } finally { keyois.close(); }
		 */
		 
		Key KeyCheck = secretKey;
		GCMParameterSpec ivParameterSpec = new GCMParameterSpec(128, iv, 0, 12); // is the same iv
		
		//cipher = Cipher.getInstance("AES/GCM/NoPadding");
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

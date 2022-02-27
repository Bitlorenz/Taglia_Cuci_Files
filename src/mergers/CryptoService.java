package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class CryptoService {

	private static final String cipherInstance = "DES/ECB/PKCS5Padding";
	private boolean ready;
	private String mode;//crypt per crypting, decrypt per decrypting
	private String password;
	private String plainFileName;
	private String absFilePathName;//usato per l'encrypt
	private Cipher cipher;
	private SecretKey desKey;
	private FileInputStream fis;//fis: file stream sorgente, in chiaro, da cryptare
	private FileOutputStream fos;//file destinazione decryptato
	private CipherOutputStream cos;
	
	public CryptoService(String password, String plainFileName, String mode) {
		this.password = password;
		this.plainFileName = plainFileName;
		File f = new File("");
		String puntatoreFile = f.getAbsolutePath()+File.separator;
		this.absFilePathName = puntatoreFile + plainFileName;
		this.mode = mode;
		this.ready = false;
		try {
			if(mode.equals("crypt"))
				this.encryptServices();
			else if(mode.equals("decrypt"))
				this.decryptServices();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void decryptServices() throws Exception{
		if(!this.password.equals("password")) {
			System.out.println("password errata");
			return;
		}
		this.fos = new FileOutputStream(plainFileName);
		this.desKey = makeKey();
		this.cipher = makeCipher(desKey);
		this.cos = new CipherOutputStream(fos, cipher);
		this.ready = true;
	}


	private void encryptServices() throws Exception{
		this.fis = new FileInputStream(absFilePathName);
		//creo la chiave
		SecretKey key = makeKey();
		//inizializzo il cipher in encrypt mode
		this.cipher = makeCipher(key);
		this.ready = true;
	}
	/**
	 * Metodo che genera una chiave segreta a partire dalla password 
	 * e dal sale di cifratura
	 * @return secret, la chiave di cifratura per il cipher
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 */
	public SecretKey makeKey()
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException{
		byte[] desKeyData = this.password.getBytes();
		DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = keyFactory.generateSecret(desKeySpec);
		return desKey;
	}
	/**
	 * metodo che genera il cipher in modalità encryption e lo restituisce
	 * @param key
	 * @return cipher, il cipher creato dalla chiave
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 */
	public Cipher makeCipher(SecretKey key) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		this.cipher = Cipher.getInstance(cipherInstance);
		if(this.mode.equals("crypt"))
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
		else if(this.mode.equals("decrypt")) {
			this.cipher.init(Cipher.DECRYPT_MODE, key);
		}
		return this.cipher;
	}

	public Cipher getCipher() {
		return this.cipher;
	}

	public CipherOutputStream getCos(FileOutputStream fosDecr) {
		if(!ready) {
			System.out.println("Crypto services non andati a buon fine");
			return null;}
		cos = new CipherOutputStream(fosDecr, cipher);
		return cos;
	}

	public void setCos(CipherOutputStream cos) {
		this.cos = cos;
	}

	public FileInputStream getFis() {
		return this.fis;
	}
}

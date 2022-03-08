package core;

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
/**
 * @author rodhex
 * Classe di servizio per permettere la cifratura e la decifratura
 */
public class CryptoService {

	private static final String cipherInstance = "DES/ECB/PKCS5Padding";
	private boolean ready;
	private String mode;//crypt per crypting, decrypt per decrypting
	private String password;
	private Cipher cipher;
	private SecretKey desKey;
	private CipherOutputStream cos;
	/**
	 * Costruttore dei servizi per la criptazione e decriptazione
	 * @param password password data in input dall' utente per cifrare o decifrare il file
	 * @param absPathFile percorso assoluto del file da cifrare o decifrare
	 * @param mode può indicare la necessità di cifrare o decifrare
	 */
	public CryptoService(String password, String mode) {
		this.password = password;
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
	/**
	 * metodo invocato per preparare uno stream CipherOutputStream per la
	 * decifrazione delle parti di file, partendo da un FileOutputStream e la e
	 * un oggetto cipher inizializzato con la password
	 * @throws Exception
	 */
	private void decryptServices() throws Exception{
		this.desKey = makeKey();
		this.cipher = makeCipher(desKey);
		this.ready = true;
	}

	/**
	 * metodo invocato per preparare uno stream CipherOutputStream per la
	 * cifratura delle parti di file
	 * @throws Exception
	 */
	private void encryptServices() throws Exception{
		SecretKey key = makeKey();
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
	/**
	 * Getter per restituire l'ggetto CipherOutputStream creato dal cipher e
	 * dall'oggetto FileOutputStream
	 * @param fosDecr
	 * @return
	 */
	public CipherOutputStream getCos(FileOutputStream fos) {
		if(!ready) {
			System.out.println("Crypto services non andati a buon fine");
			return null;}
		cos = new CipherOutputStream(fos, cipher);
		return cos;
	}
    /**
     * Getter per la password
     * @return password
     */
	public String getPassword() {
		return password;
	}
}

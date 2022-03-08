package splitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherOutputStream;

import core.CryptoService;
import gui.PannelloFC;

/**
 * @author rodhex
 * Classe che divide il file in diverse parti e cripta ogni chunk*/
public class EncryptWriter extends Splitter{

	private CryptoService cryptoService;
	private CipherOutputStream cos;
	private PannelloFC p;
	private int inc;
	/**
	 * Costruttore di un oggetto EncryptWriter 
	 * @param absPathFile percorso assoluto del file scelto dall'utente
	 * @param chunkSize dimensione di ogni parte scelta dall'utente
	 * @param splitMode modalità di divisione del file
	 * @param password password per cifrare il file
	 * @param p riferimento al pannello
	 * @throws Exception
	 */
	public EncryptWriter(String absPathFile, long chunkSize, String splitMode,
			String password, PannelloFC p) throws Exception{
		super(absPathFile, chunkSize, splitMode, p);
		this.cryptoService = new CryptoService(password, "crypt");
		this.p = p;
	}
	/**
	 * metodo che cifra ogni parte del file
	 * @throws Exception
	 */
	public void cryptChunks() throws Exception{
		int i;
		long totChunks = getChunksTot();
		for(i = 1; i <= totChunks; i++) {
			String chunkName = i+"-"+getNameFileSrc();
			File chunkCrypted = new File(getDirDest().getAbsolutePath()+File.separator+chunkName+".des");
			FileOutputStream fout = new FileOutputStream(chunkCrypted);
			cos = cryptoService.getCos(fout);
			File chunkPlain = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileInputStream fis = new FileInputStream(chunkPlain);
			readWriteChunk(fis, cos, i);
			fis.close();
			cos.flush();
			cos.close();
			fout.close();
			new File(getDirDest().getAbsolutePath()+File.separator+chunkName).delete();
		}	
	}
	/**
	 * Getter della password
	 */
	@Override
	public String getPassword() {
		return cryptoService.getPassword();		
	}
	/**
	 * Metodo che cripta e unisce i file, inizialmente imposta la sua, e del 
	 * metodo padre per l'unione dei file, quantità di incremento della percentuale
	 * di avanzamento globale. Viene aggiunta al termine delle operazioni*/
	@Override
	public void run() {
		synchronized(this) {
			super.setInc(getInc()/2);
			super.run();
			try {
				setInc(getInc()/2 +1);
				cryptChunks();
			} catch (Exception e) {
				e.printStackTrace();}
			p.increaseValue(getInc());
		}
	}
	/**
	 * Getter dell'incremento delle operazioni di questo oggetto
	 * @return this.inc l'incremento di questo oggetto
	 */
	public int getInc() {
		return this.inc;
	}
	/**
	 * Setter dell'incremento di questo oggetto, chiamato da Queue prima dello
	 * start del thread corrispondente
	 */
	@Override
	public void setInc(int inc) {
		this.inc = inc;
	}
}
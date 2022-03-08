package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherOutputStream;

import core.CryptoService;
import gui.PannelloFC;
/**
 * @author rodhex
 * Classe decriptare le parti di un file e riunirle per ricreare il file originale
 */
public class DecryptReader extends Merger{

	private CryptoService cryptoService;
	private CipherOutputStream cos;
	private PannelloFC p;
	private int inc;
	
	/**
	 * Costruttore che inizializza con i parametri in input
	 * @param firstChunkName nome del file selezionato dall'utente, può essere 
	 * il primo file diviso
	 * @param password serve per inizializzare il cipher in modalità decrypt
	 * @param p riferimento del pannello
	 * @throws Exception
	 */
	public DecryptReader(String firstChunkName, String password, PannelloFC p)
			throws Exception{
		super(firstChunkName, p);
		this.cryptoService = new CryptoService(password, "decrypt");
		this.p = p;
	}
	/**
	 * Metodo che decripta una parte di file, la scrittura viene fatta usando un
	 * oggetto CipherOutputStream inizializzato con un cipher di decrittazione,
	 * insieme allo stream di lettura viene inviato al metodo per la lettura e
	 * scrittura dalla parte di file al file intero 
	 * @throws Exception
	 */
	public void decryptChunk() throws Exception {
		int i;
		String chunkName = getNameFirstFile();
		int totChunks = (int) getChunksTot();
		for(i = 1; i <= totChunks; i++) {
			String idxChunk = i+chunkName.substring(chunkName.indexOf("-"));
			File f = new File(getDirDest().getAbsolutePath()+File.separator+idxChunk);
			FileInputStream fis = new FileInputStream(f);
			String decryptChunkName = i+"-"+getChunkName();
			File fDecr = new File(getDirDest().getAbsolutePath()+File.separator+decryptChunkName);
			FileOutputStream fosDecr = new FileOutputStream(fDecr);
			cos = cryptoService.getCos(fosDecr);
			readWriteChunk(fis, cos);
			fis.close();
			cos.flush();
			cos.close();
			f.delete();
		}		
	}
	/**
	 * Metodo che decripta e unisce i file, inizialmente imposta la sua quantità,
	 * e del metodo padre per l'unione dei file, di incremento della percentuale
	 * di avanzamento globale. Viene aggiunta al termine delle operazioni
	 */
	@Override
	public void run() {
		synchronized(this) {
			this.setInc(getInc()/2 +1);
			super.setInc(getInc() -1);
			try {
				decryptChunk();
				super.run();
			}catch(Exception e) {
				e.printStackTrace();
			}
			p.increaseValue(getInc());
		}
	}
	/**
	 * Setter dell'incremento di questo oggetto, chiamato da Queue prima dello
	 * start del thread corrispondente
	 */
	@Override
	public void setInc(int inc) {
		this.inc = inc;
	}
	/**
	 * Getter dell'incremento delle operazioni di questo oggetto
	 * @return this.inc l'incremento di questo oggetto
	 */
	public int getInc() {
		return this.inc;
	}
	/**
	 * Getter della password
	 */
	@Override
	public String getPassword() {
		return cryptoService.getPassword();}
}
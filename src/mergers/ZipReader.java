package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import gui.PannelloFC;
/**
 * @author rodhex
 * Classe per unzippare e riunire i file divisi com la modalità di zipping
 */
public class ZipReader extends Merger{
	
	private ZipInputStream zis;
	private PannelloFC p;
	private int inc;
	/**
	 * Costruttore della classe che chiama il costruttore della classe precedente
	 * e inizializza il riferimento del pannello
	 * @param absPathChunk il percorso assoluto del file
	 * @param p il riferimento del pannello
	 * @throws Exception
	 */
	public ZipReader(String absPathChunk, PannelloFC p) throws Exception{
		super(absPathChunk, p);
		this.p = p;
	}
	/**
	 * Metodo che unzippa i files utilizzando un ZipInputStream e generando un
	 * file in chiaro
	 * @throws Exception
	 */
	private void unzipChunk() throws Exception{
		int i;
		String chunkName = getNameFirstFile();
		int totChunks =(int) getChunksTot();
		for(i = 1; i <= totChunks; i++ ) {
			String idxChunk = i+chunkName.substring(chunkName.indexOf("-"));
			File chunkZip = new File(getDirDest().getAbsolutePath()+File.separator+idxChunk);
			FileInputStream fis = new FileInputStream(chunkZip);
			zis = new ZipInputStream(fis);
			ZipEntry zipEntry = zis.getNextEntry();
			String unzipChunkName = zipEntry.getName();
			File fUnzip = new File(getDirDest().getAbsolutePath()+File.separator+unzipChunkName);
			FileOutputStream fosUnzip = new FileOutputStream(fUnzip);
			readWriteChunk(zis, fosUnzip);
			zis.close();
			fis.close();//chiudo il chunk in input
			chunkZip.delete();
		}
	}
	/**
	 * Metodo che unzippa e unisce i file, inizialmente imposta la sua quantità,
	 * e del metodo padre per l'unione dei file, di incremento della percentuale
	 * di avanzamento globale. Viene aggiunta al termine delle operazioni
	 */
	@Override
	public void run() {
		synchronized(this) {
			this.setInc(getInc()/2 +1);
			super.setInc(getInc() -1);
			try {
			unzipChunk();
			super.run();
		} catch (Exception e) {
			e.printStackTrace();}
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
}
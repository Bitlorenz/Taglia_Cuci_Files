package splitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import gui.PannelloFC;

/**
 * @author rodhex
 * Classe che divide il file in diverse parti e zippa ogni chunk*/
public class ZipWriter extends Splitter {

	private ZipOutputStream zos;
	private PannelloFC p;
	private int inc;
	/**
	 * Costruttore della classe che genera un oggetto ZipWriter
	 * @param absPathFile percorso assoluto del file in input
	 * @param chunkSize dimensione scelta dall'utente per ogni parte
	 * @param splitMode modalità di divisione
	 * @param p riferimento al pannello
	 * @throws Exception
	 */
	public ZipWriter(String absPathFile, long chunkSize,
			String splitMode, PannelloFC p) throws Exception{
		super(absPathFile, chunkSize, splitMode, p);
		this.p = p;
	}

/**zipChunk: metodo per zippare ogni parte in cui è stato diviso il file
 * @throws Exception*/
	private synchronized void zipChunk() throws Exception{
		int i;
		long totChunks = getChunksTot();
		for(i = 1; i <= totChunks; i++) {
			String chunkName = i+"-"+getNameFileSrc();
			File chunkZipped = new File(getDirDest().getAbsolutePath()+File.separator+chunkName+".zip");
			FileOutputStream fos = new FileOutputStream(chunkZipped);
			zos = new ZipOutputStream(fos);
			ZipEntry zipEntry = new ZipEntry(chunkName);
			zipEntry.setSize((long)chunkZipped.length());
			zos.putNextEntry(zipEntry);
			File chunkPlain = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileInputStream fis = new FileInputStream(chunkPlain);
			readWriteChunk(fis, zos, i);
			fis.close();
			zos.closeEntry();
			zos.close();
			fos.close();
			new File(getDirDest().getAbsolutePath()+File.separator+chunkName).delete();
		}
	}
	/**
	 * Metodo che unzippa e unisce i file, inizialmente imposta la sua, e del 
	 * metodo padre per l'unione dei file, quantità di incremento della percentuale
	 * di avanzamento globale. Viene aggiunta al termine delle operazioni*/
	@Override
	public void run() {
		synchronized(this) {
			super.setInc(getInc()/2);
			super.run();
			try {
				setInc(getInc()/2 +1);
				zipChunk();
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
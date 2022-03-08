package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import gui.PannelloFC;
/**
 * @author rodhex
 * Classe per unire le parti di file in un unico file
 */
public class Merger extends GeneralMerger{

	private PannelloFC p;
	private int inc;
	/**
	 * Costruttore dell'oggetto merger
	 * @param absPathChunk percorso assoluto del file in input
	 * @param p riferimento del pannello
	 * @throws Exception
	 */
	public Merger(String absPathChunk, PannelloFC p) throws Exception {
		super(absPathChunk);
		this.p = p;
	}
	/**
	 * Metodo che legge da una delle parti del file e la ricompone scrivendola
	 * nel file di destinazione
	 * @param is inputstream per leggere la parte del file
	 * @param os outputstream per scrivere la parte del file
	 * @throws Exception
	 */
	public synchronized void readWriteChunk(InputStream is, OutputStream os)
			throws Exception{
		int chunkSizeInt;
		byte[] byteLetti = null;
		if(getMode().equals("crypt")) {
			chunkSizeInt = is.available();
			byteLetti = new byte[chunkSizeInt];
		}else {
			chunkSizeInt = (int) getChunkSize();
			byteLetti = new byte[chunkSizeInt];}
		int val=0;
		val = is.read(byteLetti);
		if(val > 0) {
			os.write(byteLetti, 0, val);}
	}
	/**metodo che unisce i chunks in un unico file*/
	public synchronized void mergeAllChunks() throws Exception{
		int i = 0;
		String chunkName = getChunkName();
		FileOutputStream fos = new FileOutputStream(getFileDst());
		int totChunks = (int) getChunksTot();
		for( i = 1; i <= totChunks; i++) {
			String idxChunk = i+"-"+chunkName;
			File chunk = new File(getDirDest().getAbsolutePath()+File.separator+idxChunk);
			FileInputStream fis = new FileInputStream(chunk);
			readWriteChunk(fis, fos);
			fis.close();
			chunk.delete();}
		fos.close();
		File dirDest = getDirDest();
		dirDest.delete();
	}
	/**@return il numero di bytes per ogni parte, deciso dall'utente*/
	@Override
	public long getInputSizeChunks() {
		return getChunkSize();}
	/**metodo che ritorna il numero di parti in cui deve essere diviso il file
	 * è indicato dall'utente, è un attributo del nodo
	 * @return il numero totali di parti scelto dall' utente*/
	@Override
	public int getInputNumChunks() {
		return getChunksTot();}
	/**Metodo che unisce i file e chiede l'aumento del valore di completamento globale*/
	@Override
	public void run() {
		synchronized(this){
			try {
				mergeAllChunks();
			}catch(Exception e) {
				e.printStackTrace();}
			p.increaseValue(inc);
		}
	}
	/**Nome del file nel nodo
	 * @return il nome del File*/
	@Override
	public String getNameNode() {
		return getNameFirstFile();}
	/**metodo che imposta l'attributo di divisione di un nodo
	 * @param attribute : dimensione o il numero di nodi*/
	@Override
	public void setAttribute(int attribute) {
		if(getMode().equals("parts"))
			setChunksTot(attribute);
		else
			setChunkSize(attribute);
	}
	/**intero che indica la dimensione di ogni chunk oppure
	 * il numero totale di chunk
	 * @return attribute*/	
	@Override
	public int getAttribute() {
		return 0;
	}
	/**Setter dell'incremento di questo oggetto, chiamato da Queue prima dello
	 * start del thread corrispondente*/
	@Override
	public void setInc(int inc) {
		this.inc = inc;
	}
	/**Getter dell'incremento delle operazioni di questo oggetto
	 * @return this.inc l'incremento di questo oggetto*/
	public int getInc() {
		return this.inc;
	}
	/**Getter della passowrd del nodo
	 * @return password del nodo*/
	@Override
	public String getPassword() {
		return null;
	}
}

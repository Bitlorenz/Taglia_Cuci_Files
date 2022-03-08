package splitters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;

import gui.PannelloFC;
/**
 * @author rodhex
 * Classe che rappresenta la divisione di un oggetto semplice
 * ed offre supporto alle classi di zipping e crypting
 */
public class Splitter extends GeneralSplitter{
	
	private InputStream is;
	private PannelloFC p;
	private int inc;
	/**
	 * Costruttore di un oggetto splitter
	 * @param absPathFile percorso assoluto del file in input
	 * @param attribute dimensione o numero di parti in cui dividere
	 * @param splitMode modalità di divisione
	 * @param p riferimento al pannello
	 */
	public Splitter(String absPathFile, long attribute, String splitMode, PannelloFC p) {
		super(absPathFile, attribute, splitMode);
		this.p = p;
	}
	/**metodo che legge i byte corrispondenti e li scrive in un nuovo chunk*/
	public synchronized void readWriteChunk(InputStream inputStream, OutputStream os, int i)
			throws Exception{
		this.setInputStream(inputStream);
		byte[] byteLetti;
		if(i==(getChunksTot()-1))
			byteLetti = new byte[(int) getChunkSizeResto()];
		else
			byteLetti = new byte[(int) getChunkSize()];
		is.read(byteLetti);
		os.write(byteLetti);
	}
	/**splitInChunks: metodo per dividere il file in piccole parti
	 * @throws Exception*/
	public synchronized void splitInChunks() throws Exception {
		int i = 0;
		FileInputStream fis = new FileInputStream(getFileSrc());
		long totChunks = getChunksTot();
		makeInfoChunk();
		for(i = 1; i <= totChunks; i++) {
			String chunkName = i+"-"+getNameFileSrc();
			File chunk = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileOutputStream fout = new FileOutputStream(chunk);
			readWriteChunk(fis, fout, i);
			fout.close();
		}
		//getFileSrc().delete();
	}
	/**metodo che crea il file contenente le informazioni della divisione
	 * utile per riunire tutti i chunks, il file è nascosto*/
	private void makeInfoChunk() throws Exception{
		String nameInfoChunk = ".infochunk";
		File infoChunk = new File(getDirDest().getAbsolutePath()+File.separator+nameInfoChunk);
		FileWriter fw = new FileWriter(infoChunk);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(getNameFileSrc());
		bw.newLine();
		bw.write(getMode());
		bw.newLine();
		bw.write(Integer.toString(getChunksTot()));
		bw.newLine();
		bw.write(Long.toString(getChunkSize()));
		bw.newLine();
		bw.write(Long.toString(getFileSrc().length()));
		bw.newLine();
		bw.write(Long.toString(getChunkSizeResto()));
		bw.close();
	}
	/**Metodo che divide i file e chiede l'aumento del valore di completamento globale*/
	@Override
	public void run() {
		synchronized(this) {
			try {
				splitInChunks();
			} catch (Exception e) {
				e.printStackTrace();}
			p.increaseValue(inc);
		}
	}
	/**Getter della dimensione in input di ogni parte di file */
	@Override
	public long getInputSizeChunks() {
		return getChunkSize();}
	/**Getter del numero totale in input di parti in cui dividere il file */
	@Override
	public int getInputNumChunks() {
		return getChunksTot();}
	/**Getter del file all'interno del oggetto nella coda */
	@Override
	public File getFileNode() {
		return getFileSrc();}
	/**intero che indica la dimensione di ogni chunk oppure
	 * il numero totale di chunk
	 * @return attribute*/
	@Override
	public int getAttribute() {
		String mode = getMode();
		int attribute = 0;
		if(mode.equals("size") || mode.equals("zip") || mode.equals("crypt"))
			attribute = (int) getChunkSize();
		else if(mode.equals("parts"))
			attribute = getChunksTot();
		return attribute;}
	/**Nome del file nel nodo
	 * @return il nome del File*/
	@Override
	public String getNameNode() {
		return getNameFileSrc();}
	@Override
	public void setAttribute(int attribute) {}
	/**Getter della passowrd del nodo
	 * @return password del nodo*/
	@Override
	public String getPassword() {
		return null;}
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
	 * metodo per impostare l'InputStream che la classe stessa o una figlia userà
	 * @param inputStream
	 */
	private void setInputStream(InputStream inputStream) {
		this.is =inputStream;
	}
}
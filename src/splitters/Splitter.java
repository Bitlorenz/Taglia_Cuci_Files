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
 * 
 * Classe che rappresenta la divisione di un oggetto semplice
 * ed offre supporto alle classi di zipping e crypting
 */
public class Splitter extends GeneralSplitter{
	
	private InputStream is;
	private PannelloFC p;
	private int inc;
	
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
	private void setInputStream(InputStream inputStream) {
		this.is =inputStream;
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
	@Override
	public long getInputSizeChunks() {
		return getChunkSize();}
	@Override
	public int getInputNumChunks() {
		return getChunksTot();}
	@Override
	public File getFileNode() {
		return getFileSrc();}
	@Override
	public int getAttribute() {
		String mode = getMode();
		int attribute = 0;
		if(mode.equals("size") || mode.equals("zip") || mode.equals("crypt"))
			attribute = (int) getChunkSize();
		else if(mode.equals("parts"))
			attribute = getChunksTot();
		return attribute;}
	@Override
	public boolean isCrypted() {
		return false;}
	@Override
	public boolean isZipped() {
		return false;}
	@Override
	public String getNameNode() {
		return getNameFileSrc();}
	@Override
	public void setAttribute(int attribute) {}
	@Override
	public String getPassword() {
		return null;}
	@Override
	public void setInc(int inc) {
		this.inc = inc;
	}
	public int getInc() {
		return this.inc;
	}
}
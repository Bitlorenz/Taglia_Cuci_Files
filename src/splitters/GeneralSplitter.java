package splitters;

import java.io.File;

import core.INode;
/**
 * @author rodhex
 * classe astratta per dividere un file*/
public  abstract class GeneralSplitter implements INode, Runnable{
	/**chunksTot: numero di chunks totali, userInput in modalità parts*/
	private int chunksTot;
	/**numChunksInt: numero di chunks interi*/
	private int numChunksInt;
	/**chunkSize: dimensione di ogni chunk, userInput in modalità size*/
	private long chunkSize, chunkSizeResto;
	/**dirDst: puntatore alla cartella di destinazione*/
	private File dirDest;
	/**fileSrc: file Sorgente*/
	private File fileSrc;
	/**nomeFile: nome del file sorgente*/
	private String nameFileSrc, absPathFileSrc;
	/**mode: modalità di splitting scelta dall'utente*/
	private String mode;

	public GeneralSplitter(String absPathFile, long userInput, String splitMode) {
		setNameFileSrc(absPathFile.substring(absPathFile.lastIndexOf(File.separator)+1));
		setAbsPathFileSrc(absPathFile.substring(0, absPathFile.lastIndexOf(File.separator)));
		setDirDest(new File(getAbsPathFileSrc()+File.separator+"dir"+getNameFileSrc()));
		fileSrc = new File(absPathFile);
		setMode(splitMode);
		if(mode.equals("size") || mode.equals("zip") || mode.equals("crypt")){
			setChunkSize(userInput);
			numChunksInt = (int) (fileSrc.length()/chunkSize);
			setChunkSizeResto((int) (fileSrc.length()%chunkSize));
			setChunksTot(numChunksInt+1);
		}else if(mode.equals("parts")) {
			setChunksTot((int) userInput);
			numChunksInt = chunksTot-1;
			chunkSize = fileSrc.length()/numChunksInt;
			setChunkSizeResto(chunkSize + (fileSrc.length()%chunkSize));
		}
	}

	public int getChunksTot() {
		return chunksTot;}

	public void setChunksTot(int chunksTot) {this.chunksTot = chunksTot;}

	public long getChunkSize() {return chunkSize;}

	public void setChunkSize(long chunkSize) {
		if(chunkSize > fileSrc.length()) {
			System.err.println("Dimensione scelta maggiore della dimensione del file");
			System.exit(-1);}
		this.chunkSize = chunkSize;
	}

	public File getDirDest() {return dirDest;}

	public void setDirDest(File dirDest) {
		if(dirDest.mkdir())		
			this.dirDest = dirDest;}

	public String getNameFileSrc() {return nameFileSrc;}

	public void setNameFileSrc(String nomeFile) {this.nameFileSrc = nomeFile;}

	@Override
	public String getMode() {return mode;}

	public void setMode(String mode) {this.mode = mode;}

	public File getFileSrc() {return fileSrc;}

	public void setFileSrc(File fileSrc) {this.fileSrc = fileSrc;}

	public int getNumChunksInt() {return numChunksInt;}

	public long getChunkSizeResto() {return chunkSizeResto;}

	public void setChunkSizeResto(long chunkSizeResto) {
		this.chunkSizeResto = chunkSizeResto;}

	public String getAbsPathFileSrc() {return absPathFileSrc;}

	public void setAbsPathFileSrc(String absPathFileSrc) {
		this.absPathFileSrc = absPathFileSrc;}
	@Override
	public abstract void run();

}
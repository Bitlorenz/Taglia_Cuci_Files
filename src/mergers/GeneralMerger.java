package mergers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import core.INode;

public abstract class GeneralMerger implements INode, Runnable {
	/**chunksTot: numero di chunks totali, userInput in modalità parts*/
	private int chunksTot;
	/**chunkSize: dimensione di ogni chunk, userInput in modalità size*/
	private long chunkSize, chunkSizeResto, fileSrcSize;
	/**dirDst: puntatore alla cartella in cui sono i chunks*/
	private File dirSrc;
	/**fileSrc: file Sorgente, fileDst: file Destinazione,
	 *infoChunk: file con le info su divisione*/
	private File fileSrc, fileDst, infoChunk;
	/**nomeFile: nome del file in input*/
	private String nameFirstFile, absPathFirstFile, chunkName;
	private String nameDst;
	/**mode: modalità di splitting scelta dall'utente*/
	private String mode, password;

	public GeneralMerger(String absPathChunk, String password) throws Exception {
		this.setAbsPathFirstFile(absPathChunk);
		this.fileSrc = new File(absPathChunk);
		this.setDirDest(this.fileSrc.getParentFile());
		this.setNameFirstFile(this.fileSrc.getName());
		this.setInfoChunk(new File(this.dirSrc.getAbsolutePath()+File.separator
				+".infochunk"));
		retriveInfo();
		fileDst = new File(getDirDest().getAbsolutePath()+File.separatorChar+
				getNameDst());
		if(password != null && mode.equals("crypt"))
			this.password = password;
	}
	
	private void retriveInfo() throws Exception{
		FileReader fr = new FileReader(getInfoChunk());
		BufferedReader br = new BufferedReader(fr);
		setNameDst(br.readLine());
		setMode(br.readLine());
		setChunksTot(Integer.parseInt(br.readLine()));
		setChunkSize(Long.parseLong(br.readLine()));
		setChunkName(getNameDst());
		fileSrcSize = Long.parseLong(br.readLine());
		setChunkSizeResto(Long.parseLong(br.readLine()));
		br.close();
	}

	public String getNameFirstFile() {
		return nameFirstFile;}

	public void setNameFirstFile(String nameFirstFile) {
		this.nameFirstFile = nameFirstFile;}

	public String getAbsPathFirstFile() {
		return absPathFirstFile;}

	public void setAbsPathFirstFile(String absPathFirstFile) {
		this.absPathFirstFile = absPathFirstFile;}

	public String getNameDst() {
		return nameDst;}

	public void setNameDst(String nameDestFile) {
		this.nameDst = nameDestFile;}

	public int getChunksTot() {
		return chunksTot;}

	public void setChunksTot(int chunksTot) {
		this.chunksTot = chunksTot;}

	public long getChunkSize() {
		return chunkSize;}

	public void setChunkSize(long chunkSize) {
		this.chunkSize = chunkSize;}

	public long getChunkSizeResto() {
		return chunkSizeResto;}

	public void setChunkSizeResto(long chunkSizeResto) {
		this.chunkSizeResto = chunkSizeResto;}

	public File getDirDest() {
		return dirSrc;}

	public void setDirDest(File dirDest) {
		this.dirSrc = dirDest;}

	public File getFileDst() {
		return fileDst;}

	public void setFileDst(File fileDst) {
		this.fileDst = fileDst;}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getChunkName() {
		return chunkName;
	}

	public void setChunkName(String chunkName) {
		this.chunkName = chunkName;
	}

	public File getInfoChunk() {
		return infoChunk;}

	public void setInfoChunk(File infoChunk) {
		this.infoChunk = infoChunk;}

	@Override
	public File getFileNode() {
		return fileSrc;}
	
	@Override
	public abstract void run();
}

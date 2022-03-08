package mergers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import core.INode;
/**
 * @author rodhex
 * Classe Astratta che imposta molti attributi comuni utili alle classi di unione
 */
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
	/**nomeFile: nome del file in input
	 *absPathFirstFile: percorso assoluto del primo file, input da utente
	 *chunkName: nome singolo chunk, non indicizzato */
	private String nameFirstFile, absPathFirstFile, chunkName;
	/**nameDst: nome del file di destinazione*/
	private String nameDst;
	/**mode: modalità di splitting scelta dall'utente*/
	private String mode;
	/**
	 * Costruttore che calcola i parametri utili alla ricomposizione del file
	 * @param absPathChunk percorso assoluto del file
	 * @throws Exception
	 */
	public GeneralMerger(String absPathChunk) throws Exception {
		this.setAbsPathFirstFile(absPathChunk);
		this.fileSrc = new File(absPathChunk);
		this.setDirDest(this.fileSrc.getParentFile());
		this.setNameFirstFile(this.fileSrc.getName());
		this.setInfoChunk(new File(this.dirSrc.getAbsolutePath()+File.separator
				+".infochunk"));
		retriveInfo();
		fileDst = new File(getDirDest().getAbsolutePath()+File.separatorChar+
				getNameDst());
	}
	/**
	 * Metodo per recuperare le informazioni del file sorgente dal file .infoChunk
	 * @throws Exception
	 */
	private void retriveInfo() throws Exception{
		FileReader fr = new FileReader(getInfoChunk());
		BufferedReader br = new BufferedReader(fr);
		setNameDst(br.readLine());
		setMode(br.readLine());
		setChunksTot(Integer.parseInt(br.readLine()));
		setChunkSize(Long.parseLong(br.readLine()));
		setChunkName(getNameDst());
		this.fileSrcSize = Long.parseLong(br.readLine());
		setChunkSizeResto(Long.parseLong(br.readLine()));
		br.close();
	}
	/**
	 * Getter nome primo file
	 * @return nameFirstFile 
	 */
	public String getNameFirstFile() {
		return nameFirstFile;}
	/**
	 * Setter nome primo file
	 * @param nameFirstFile
	 */
	public void setNameFirstFile(String nameFirstFile) {
		this.nameFirstFile = nameFirstFile;}
	/**
	 * Getter percorso assoluto primo file
	 * @return absPathFirstFile
	 */
	public String getAbsPathFirstFile() {
		return absPathFirstFile;}
	/**
	 * Setter percorso assoluto
	 * @param absPathFirstFile
	 */
	public void setAbsPathFirstFile(String absPathFirstFile) {
		this.absPathFirstFile = absPathFirstFile;}
	/**
	 * getter nome file di destinazione/sorgente
	 * @return nameDst
	 */
	public String getNameDst() {
		return nameDst;}
	/**
	 * setter nome file di destinazione
	 * @param nameDestFile
	 */
	public void setNameDst(String nameDestFile) {
		this.nameDst = nameDestFile;}
	/**
	 * Getter del numero di chunks totali
	 * @return chunksTot
	 */
	public int getChunksTot() {
		return chunksTot;}
	/**
	 * Setter del numero di chunks totali
	 * @param chunksTot
	 */
	public void setChunksTot(int chunksTot) {
		this.chunksTot = chunksTot;}
	/**
	 * getter della dimensione di ogni chunk
	 * @return chunkSize
	 */
	public long getChunkSize() {
		return chunkSize;}
	/**
	 * Setter della dimensione di goni file
	 * @param chunkSize
	 */
	public void setChunkSize(long chunkSize) {
		this.chunkSize = chunkSize;}
	/**
	 * Getter della dimensione del chunk di resto
	 * @return chunkSizeResto
	 */
	public long getChunkSizeResto() {
		return chunkSizeResto;}
	/**
	 * Setter della dimensione del chunk di resto
	 * @param chunkSizeResto
	 */
	public void setChunkSizeResto(long chunkSizeResto) {
		this.chunkSizeResto = chunkSizeResto;}
	/**
	 * getter cartella di destinazione
	 * @return dirSrc
	 */
	public File getDirDest() {
		return dirSrc;}
	/**
	 * Setter cartella di destinazione
	 * @param dirDest
	 */
	public void setDirDest(File dirDest) {
		this.dirSrc = dirDest;}
	/**
	 * getter file di destinazione
	 * @return fileDst
	 */
	public File getFileDst() {
		return fileDst;}
	/**
	 * setter file di Destinazione
	 * @param fileDst
	 */
	public void setFileDst(File fileDst) {
		this.fileDst = fileDst;}
	/**
	 * Getter della modalità di unione
	 * @return mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * setter della modalità di unione 
	 * @param mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/**
	 * getter nome di ogni chunk non indicizzato
	 * @return chunkName
	 */
	public String getChunkName() {
		return chunkName;
	}
	/**
	 * setter del nome di ogni chunk non indicizzato
	 * @param chunkName
	 */
	public void setChunkName(String chunkName) {
		this.chunkName = chunkName;
	}
	/**
	 * getter del file che contiene le informazioni di divisione del file
	 * @return infoChunk
	 */
	public File getInfoChunk() {
		return infoChunk;}
	/**
	 * setter del file che contiene le informazioni di divisione del file
	 * @param infoChunk
	 */
	public void setInfoChunk(File infoChunk) {
		this.infoChunk = infoChunk;}

	/**File contenuto nel nodo della coda
	 * @return il file del nodo*/
	@Override
	public File getFileNode() {
		return fileSrc;}

	/** metodo omonimo dell'interfaccia Runnable per i thread*/
	@Override
	public abstract void run();
}

package core;

import java.io.File;

/**
 * @author rodhex
 * Interfaccia che rappresenta un nodo nella coda, sia per la divisione
 * che per l'unione dei file
 */
public interface INode{
	/**
	 * metodo omonimo dell'interfaccia Runnable per i thread
	 */
	public void run();
	/**
	 * @return il numero di bytes per ogni parte, deciso dall'utente 
	 */
	public long getInputSizeChunks();
	/**
	 * metodo che ritorna il numero di parti in cui deve essere diviso il file
	 * è indicato dall'utente, è un attributo del nodo
	 * @return il numero totali di parti scelto dall' utente
	 */
	public int getInputNumChunks();
	/**
	 * File contenuto nel nodo della coda
	 * @return il file del nodo*/
	public File getFileNode();
	/**
	 * Nome del file nel nodo
	 * @return il nome del File
	 */
	public String getNameNode();
	/**
	 * Stringa che indica la modalità di divisione del file
	 * @return la stringa con la modalità*/
	public String getMode();
	/**intero che indica la dimensione di ogni chunk oppure
	 * il numero totale di chunk
	 * @return attribute*/	
	public int getAttribute();
	/**metodo che imposta l'attributo di divisione di un nodo
	 * può essere la dimensione o il numero di nodi*/
	
	public boolean isCrypted();
	
	public boolean isZipped();
	
	public boolean hasPassword();
	public void setAttribute(int attribute);

}

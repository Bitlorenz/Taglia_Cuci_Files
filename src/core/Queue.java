package core;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import gui.PannelloFC;
import splitters.*;
import mergers.*;
/**
 * @author rodhex
 * Classe che implementa la struttura dati coda, realizzata mediante un Vector
 */
public class Queue{
	protected Vector<INode> queue;
	private String type;
	private PannelloFC p;
	private int totFiles;
	/**metodo costruttore per inizializzare un oggetto coda vuoto*/ 
	public Queue(PannelloFC pannelloFC) {
		queue = new Vector<INode>(0);
		p = pannelloFC;
	}
	/**
	 * Metodo per aggiungere uno o più file di divisione alla coda
	 * @param files array di file selezionati
	 * @param mode modalità dei file selezionati
	 * @param attribute dimensione o parti totali del file
	 * @param passwd password se è stata scelta modalità cifratura
	 * @throws Exception
	 */
	public void addSplitNodes(File[] files, String[] mode, int attribute,
			String passwd) throws Exception {
		this.totFiles = files.length;
		for(int i=0; i < totFiles; i++) {
			if(mode[i].equals("zip"))
				queue.add(i, new ZipWriter(files[i].getAbsolutePath(), attribute, mode[i], p));
			else if(mode[i].equals("crypt"))
				queue.add(i, new EncryptWriter(files[i].getAbsolutePath(), attribute, mode[i], passwd, p));
			else if(mode[i].equals("size") || mode[i].equals("parts"))
				queue.add(i, new Splitter(files[i].getAbsolutePath(), attribute, mode[i], p));
		}
		removeNull();
	}
	/**
	 * Metodo per aggiungere uno o più file di unione alla coda
	 * @param files array di file che contiene le prime parti dei file da unire
	 * @param passwd password dei file da unire
	 * @throws Exception
	 */
	public void addMergeNodes(File[] files, String passwd) throws Exception {
		this.totFiles = files.length;
		for(int i=0; i < totFiles; i++) {
				queue.add(i, new Merger(files[i].getAbsolutePath(), p));
				if(getNode(i).getMode().equals("crypt")) {
					INode newNode = new DecryptReader(files[i].getAbsolutePath(), passwd, p);
					queue.set(i, newNode);
				}else if(getNode(i).getMode().equals("zip")) {
					INode newNode = new ZipReader(files[i].getAbsolutePath(), p);
					queue.set(i, newNode);
				}
		}
		removeNull();
	}
	/**
	 * metodo di appoggio che rimuove eventuali oggetti nulli
	 */
	public void removeNull() {
		queue.removeAll(Collections.singleton(null));
	}
	/**
	 * metodo per rimuovere i nodi della coda
	 * @param idxs: array di indici come righe della tabella
	 */
	public void removeNodes(int[] idxs) {
		for(int i = 0; i < idxs.length; i++) {
			queue.remove(idxs[i]);
			for(int j = i; j < idxs.length; j++) 
				idxs[j] -= 1;}		
	}
	/**
	 * Metodo per la modifica dell'attributo di un nodo 
	 * @param idx indice del nodo
	 * @param attribute nuovo attributo da assegnare al nodo
	 * @throws Exception
	 */
	public void replaceNode(int idx, int attribute) throws Exception {
		INode oldNode = getNode(idx);
		String oldAbsPath = oldNode.getFileNode().getAbsolutePath();
		String oldMode = getModeNode(idx);
		INode newNode = null;
		switch (oldMode) {
		case "zip": newNode = new ZipWriter(oldAbsPath, attribute, oldMode, p);
		case "crypt": newNode = new EncryptWriter(oldAbsPath, attribute, oldMode, oldNode.getPassword(), p);
		case "size": newNode = new Splitter(oldAbsPath, attribute, oldMode, p);
		case "parts": newNode = new Splitter(oldAbsPath, attribute, oldMode, p);
		}
		queue.set(idx, newNode);			
	}
	/**
	 * getter della coda
	 * @return un riferimento all'oggetto coda corrente
	 */
	public Queue getQueue() {
		return this;}
	/**
	 * Getter della dimensione della coda
	 * @return la dimensione della coda
	 */
	public int getSize() {
		return queue.size();}
	/**Serve per far apparire nella tabella il metodo di divisione scelto
	 * @param i: indice del nodo
	 * @return la modalità di divisione del nodo*/
	public String getModeNode(int i) {
		if(i < getSize() && (!(getNode(i) == null)))
			return queue.get(i).getMode();
		return null;}
	/**Metodo per inserire nella tabella il nome del file 
	 * @param i: indice del nodo
	 * @return il nome del file nel nodo*/
	public String getNameNode(int i) {
		if(i < getSize() && (!(getNode(i) == null))) {
			return queue.get(i).getNameNode();}
		return null;	
	}	
	/**Metodo per restituire un riferimento al nodo nella coda 
	 * @param i: indice del nodo
	 * @return un riferimento al nodo*/
	public INode getNode(int i) {
		if(i < getSize()) {
			INode node = queue.get(i);
			if(!(node == null))
				return node;}
		return null;}
	/**Metodo per restituire l'attributo del nodo della coda 
	 * @param i: indice del nodo
	 * @return attribute: l'attributo del nodo*/
	public int getAttributeNode(int i) {
		if(i < getSize() && (!(getNode(i) == null)))
			return queue.get(i).getAttribute();
		return 0;
	}
	/**
	 * getter del tipo di coda, per dividere o per unire 
	 * @return type: stringa che indica il tipo della coda
	 */
	public String getType() {
		return type;
	}
	/**
	 * setter del tipo della coda
	 * @param type indica il tipo della coda
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * Metodo che assegna un certo valore di incremento delle operazioni ad
	 * ogni nodo della coda
	 */
	public void giveInc() {
		int inc = 100/getSize();
		for(int i=0; i < getSize(); i++) {
			queue.get(i).setInc(inc);
		}
	}
	/**
	 * metodo che esegue i nodi della coda dopo aver assegnato i valori di
	 * incremento e alla fine rimuove gli elementi eseguiti dalla coda
	 * @throws InterruptedException
	 */
	public void runAll() throws InterruptedException {
		giveInc();
		Thread nodeJobs[] = new Thread[getSize()];
		for(int i = 0; i < getSize(); i++) 
			nodeJobs[i] = new Thread((Runnable) getNode(i));
		int i;
		for(i = 0; i < getSize(); i++) 
			nodeJobs[i].start();
		queue.removeAllElements();
	}
}

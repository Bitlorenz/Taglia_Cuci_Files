package core;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import gui.PannelloFC;
import splitters.*;
import mergers.*;

public class Queue{
	protected Vector<INode> queue;
	private String type;
	private PannelloFC p;
	private int totFiles;
	//metodo costruttore per inizializzare un oggetto coda vuoto 
	public Queue(PannelloFC pannelloFC) {
		queue = new Vector<INode>(0);
		p = pannelloFC;
	}
	
	/**
	 * costruttore della coda, crea una coda di file per essere divisi o uniti,
	 * viene chiamato dall'oggetto JFileChooser, l'array di file dà la possibilità
	 * di scegliere più file tutti alla volta
	 * @param inputFiles, array di file scelti
	 * @param mode: array delle modalità dei file scelti, uguale per tutti o vuota
	 * per tutti, dipende se la modalità era già stata selezionata o no (stringa vuota)
	 * @param nodeAttribute: può essere la dimensione di ogni parte in cui deve
	 * essere diviso il file o il numero totali di parti in cui deve essere
	 * diviso il file
	 * @param type: tipologia di coda: per dividere o per unire
	 * @throws Exception 
	 */
	public Queue(File[] files, String type,String[] mode, int attribute, String passwd)
			throws Exception {
		this.totFiles = files.length;
		this.setType(type);
		queue = new Vector<INode>(totFiles);
		if(type.equals("Dividi"))
			addSplitNodes(files, mode, attribute, passwd);
		else if(type.equals("Unisici"))
			addMergeNodes(files, passwd);
		
	}
	/*TO-DO:
	 * invece di aggiungere tutti i file con un solo metodo, ogni file scelto viene aggiunto
	 * tramite un metodo apposito che dipende dalla sua modalità*/
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
	
	public void addMergeNodes(File[] files, String passwd) throws Exception {
		this.totFiles = files.length;
		for(int i=0; i < totFiles; i++) {
				queue.add(i, new Merger(files[i].getAbsolutePath(), passwd));
				if(getNode(i).getMode().equals("crypt")) {
					INode newNode = new DecryptReader(files[i].getAbsolutePath(), passwd);
					queue.set(i, newNode);
				}else if(getNode(i).getMode().equals("zip")) {
					INode newNode = new ZipReader(files[i].getAbsolutePath());
					queue.set(i, newNode);
				}
		}
		removeNull();
	}
	
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
	
	public Queue getQueue() {
		return this;}
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public void runAll() {
		synchronized(this) {
			p.setGlobalValue((100/getSize())+1);}
		Thread nodeJobs[] = new Thread[getSize()];
		for(int i = 0; i < getSize(); i++) {
			nodeJobs[i] = new Thread((Runnable) getNode(i));
			}
		int i;
		for(i = 0; i < getSize(); i++) {
			nodeJobs[i].start();
		}
		queue.removeAllElements();
	}
}

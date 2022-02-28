package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;

import core.Queue;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;

public class PannelloFC extends JPanel implements ActionListener, DocumentListener{

	private static final long serialVersionUID = 1L;

	private JTextField dimChunk, partsChunk, pswdTxt;
	private JButton scegliFile, modificaFile, rimuoviFile;
	private JButton esegui, stampaFile;
	private JLabel dimLabel, numLabel, pswdLabel;
	private JRadioButton unisci, divDim, divNum, divZip, divCrypt;
	private JFileChooser fileChooser;
	private JTable tab;
	private ModelloTab mt;
	private Queue q;
	//type è la tipologia di coda, generalMode è la modalità quando non ci sono file nella coda
	private String sizeChunks, partsChunks, type, generalMode;
	//stringa che rappresenta la password, va inserita in un text field
	private String password;
	private String[] modes; // array di stringhe che tiene le modalità di divisione ordinate per i nodi, da passare alla q
	private File[] inputFiles;
	private int attribute;//dimChunk o partsChunk a seconda della modalità per singolo nodo
	private int[] attributes;
	private boolean stepNum;
	private boolean stepSize;
	
	public PannelloFC() {
		super();
		setLayout(new BorderLayout());

		JPanel centerPanel= new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		q = new Queue();//inizializzo una coda vuota
		mt= new ModelloTab(q);
		tab= new JTable(mt);
		tab.setRowSelectionAllowed(true);
		setTableColumnsWidth(tab);
		centerPanel.add(tab);		

		JPanel northPanel= new JPanel();
		add(northPanel, BorderLayout.NORTH);
		dimChunk = new JTextField("", 5);
		dimChunk.getDocument().putProperty("panel", dimChunk);
		dimChunk.setEditable(false);
		dimChunk.getDocument().addDocumentListener(this);
		partsChunk= new JTextField("", 5);
		partsChunk.getDocument().putProperty("panel", partsChunk);
		partsChunk.setEditable(false);
		partsChunk.getDocument().addDocumentListener(this);
		pswdTxt = new JTextField("", 10);
		pswdTxt.getDocument().putProperty("panel", pswdTxt);
		pswdTxt.setEditable(false);
		pswdTxt.getDocument().addDocumentListener(this);
		dimLabel = new JLabel("Dimensione di ogni parte");
		numLabel = new JLabel("Numero di parti totali");
		pswdLabel = new JLabel("Password");
		northPanel.add(dimLabel);
		northPanel.add(dimChunk);
		northPanel.add(numLabel);
		northPanel.add(partsChunk);
		northPanel.add(pswdLabel);
		northPanel.add(pswdTxt);
		
		
		JPanel westPanel= new JPanel();
		add(westPanel, BorderLayout.WEST);
		westPanel.setLayout(new GridLayout(5,1));
		unisci = new JRadioButton("Unisci");
		divDim= new JRadioButton("Dividi per dim");
		divNum= new JRadioButton("Dividi per num");
		divZip= new JRadioButton("Dividi per dim con zip");
		divCrypt= new JRadioButton("Dividi per dim con crypt");
		ButtonGroup grp= new ButtonGroup();
		grp.add(unisci);
		grp.add(divDim);
		grp.add(divNum);
		grp.add(divZip);
		grp.add(divCrypt);
		unisci.addActionListener(this);
		divDim.addActionListener(this);
		divNum.addActionListener(this);
		divZip.addActionListener(this);
		divCrypt.addActionListener(this);
		westPanel.add(unisci);
		westPanel.add(divDim);
		westPanel.add(divNum);
		westPanel.add(divZip);
		westPanel.add(divCrypt);
		
		JPanel southPanel= new JPanel();
		add(southPanel, BorderLayout.SOUTH);
		esegui = new JButton("Esegui");
		stampaFile= new JButton("Elenca!");
		stampaFile.addActionListener(this);
		southPanel.add(esegui);
		southPanel.add(stampaFile);
		
		JPanel eastPanel= new JPanel();
		add(eastPanel, BorderLayout.EAST);
		eastPanel.setLayout(new GridLayout(4,1));
		scegliFile= new JButton("Scegli File");
		scegliFile.addActionListener(this);
		modificaFile= new JButton("Modifica");
		modificaFile.addActionListener(this);
		rimuoviFile= new JButton("Rimuovi");
		rimuoviFile.addActionListener(this);
		eastPanel.add(scegliFile);
		eastPanel.add(modificaFile);
		eastPanel.add(rimuoviFile);		
	}
	
	public void actionPerformed(ActionEvent e) {
		//AGGIUNGERE il controllo sugli step prima di dividere il file non sul file chooser
		if(e.getSource()==scegliFile) {
			fileChooser= new JFileChooser();
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setApproveButtonText("Aggiungi");
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				inputFiles = fileChooser.getSelectedFiles();
				modes = new String[inputFiles.length];
				if(inputFiles.length==0 || type==null || modes.length==0 || attribute == -1)
					System.err.println("file selezionati 0, errore nel tipo, nella modalità, nell'attributo");
				//se c'è già una modalità selezionata ma nessun file era ancora nella coda allora tutti i file prendono quella modalità
				if(divDim.isSelected() || divNum.isSelected() ||
						divZip.isSelected() || divCrypt.isSelected())
					generalModeToModes(inputFiles.length);
				//se c'è già un attributo selezionato ma nessun file era ancora nella coda allora tutti i file prendono quell attributo
				if(dimChunk.getText() != null || partsChunk.getText() != null)
					attToAtts(inputFiles.length);
				try {
					if(getType().equals("Dividi"))
						q.addSplitNodes(inputFiles, modes, attribute, password);
					else if(getType().equals("Unisci")) 
						q.addMergeNodes(inputFiles, password);
				} catch (Exception e1) {
					e1.printStackTrace();}
				mt.fireTableDataChanged();
			}
		}
		//radioButton per unire i file: implementazione nella tabella
		if(e.getSource() == unisci) {
			setType("Unisci");//la tipologia di coda è quella dell'unione
			setModes(null);//non c'è nessuna modalità di divisione da comunicare
			setGeneralMode(null);
			dimChunk.setEditable(false);
			partsChunk.setEditable(false);
			pswdTxt.setEditable(false);
			if(q != null && q.getSize() > 0 && q.getType() == null)
				q.setType(getType());
		}
		//radioButton per dividere i file per dimensione
		//AGGIUNGERE PSWDSETEDITABLEFALSE
		if(e.getSource() == divDim) {
			setType("Dividi");
			dimChunk.setEditable(true);
			partsChunk.setEditable(false);
			setGeneralMode("size");
			setModes(null);
		}
		//radioButton per dividere i file per numero
		if(e.getSource() == divNum) {
			setType("Dividi");
			dimChunk.setEditable(false);
			partsChunk.setEditable(true);
			setGeneralMode("parts");
			setModes(null);
		}
		//radioButton per dividere i file per dim con zip
		if(e.getSource() == divZip) {
			setType("Dividi");
			dimChunk.setEditable(true);
			partsChunk.setEditable(false);
			setGeneralMode("zip");
			setModes(null);	
		}
		//radioButton per dividere i file con crypt
		//TO-DO: IMPLEMENTARE RECUPERO PASSWORD
		if(e.getSource() == divCrypt) {
			setType("Dividi");
			dimChunk.setEditable(true);
			partsChunk.setEditable(false);
			pswdTxt.setEditable(true);
			setGeneralMode("crypt");
			setModes(null);
		}
		//stampare i file scelti, aggiungere tutte le caratteristiche
		if(e.getSource() == stampaFile) {
				for(int i=0; i < q.getSize(); i++)
	            	System.out.println(q.getNameNode(i));
		}
		//esecuzione della coda dei jobs
		if(e.getSource() == esegui) {
			//inserire metodo che chiama l'esecuzione di tutti i lsvori sui nodi della coda
			//controllo sul tipo di coda e file aggiunti (non devono essere presenti sia file da dividere che da unire)
		}
		//rimuovo un file dalla coda di file e dalla tabella
		if(e.getSource()==rimuoviFile) {
			if(q!=null && q.getSize()>0) {
				int[] righe=tab.getSelectedRows();
				q.removeNodes(righe);
				mt.fireTableDataChanged();
			}
		}
	}
	@Override
	public void changedUpdate(DocumentEvent arg0) {}
	//si riferisce all'oggetto dimChunk
	@Override
	public void insertUpdate(DocumentEvent e) {
		Object owner = e.getDocument().getProperty("panel");
		if(owner == dimChunk) {
			setSizeChunks(dimChunk.getText());
			if(!dimChunk.getText().equals(""))
				setAttribute(Integer.parseInt(dimChunk.getText()));}
		else if(owner == partsChunk) {
				setPartsChunks(partsChunk.getText());
			if(!dimChunk.getText().equals(""))
				setAttribute(Integer.parseInt(partsChunk.getText()));}
		else if(owner == pswdTxt) {
			if(!pswdTxt.getText().equals(""))
				setPassword(pswdTxt.getText());}
		//se si seleziona il file e poi si inserisce l'attributo
		int idxNode = tab.getSelectedRow();
		if(idxNode != -1) {
			mt.setValueAt(attribute, idxNode, 2);
			q.getNode(idxNode).setAttribute(getAttribute());}
		mt.fireTableDataChanged();
	}
	@Override
	public void removeUpdate(DocumentEvent e) {
		Object owner = e.getDocument().getProperty("panel");
		if(owner == dimChunk) {
			setSizeChunks(dimChunk.getText());
			if(!dimChunk.getText().equals(""))
				setAttribute(Integer.parseInt(dimChunk.getText()));}
		else if(owner == partsChunk) {
				setPartsChunks(partsChunk.getText());
			if(!dimChunk.getText().equals(""))
				setAttribute(Integer.parseInt(partsChunk.getText()));}
		else if(owner == pswdTxt) {
			if(!pswdTxt.getText().equals(""))
				setPassword(pswdTxt.getText());}
		//se si seleziona il file e poi si inserisce l'attributo
		int idxNode = tab.getSelectedRow();
		if(idxNode != -1) {
			mt.setValueAt(attribute, idxNode, 2);
			q.getNode(idxNode).setAttribute(getAttribute());}
		mt.fireTableDataChanged();
	}
	//controllo sul processo di divisione per dimensione
	public boolean stepSizeReady() {
		stepSize=true;
		return stepSize;}
	//controllo sul processo di divisione per numero di chunks
	public boolean stepNumReady() {
		stepNum=true;
		return stepNum;}
	//metodo per stabilire la larghezza delle colonne della tabella
	//metodo statico per specificare la largehzza delle caselle di una colonna
	public static void setTableColumnsWidth(JTable table) {
		for(int i=0; i<table.getColumnModel().getColumnCount(); i++) {
			TableColumn colonna = table.getColumnModel().getColumn(i);
			if(i == 0) {
				colonna.setPreferredWidth(150);
			}
			else
				colonna.setPreferredWidth(70);}
	}
	/**metodo per impostare un singolo attributo come attributo di goni nodo
	 *inserito quando si crea la coda (come generalModeToModes)
	 *@param totNodes: quantità di nodi aggiunti alla coda*/
	public void attToAtts(int totNodes) {
		setAttributes(new int[totNodes]);
		for(int i = 0; i < totNodes; i++)
			attributes[i] = attribute;
	}
	/**metodo che serve se è selezionata prima la modalità di divisione e poi i files
	 * @param totNodes: è la quantità di nodi selezionati*/
	public void generalModeToModes(int totNodes) {
		setModes(new String[totNodes]);
		for(int i = 0; i < totNodes; i++)
			modes[i] = generalMode;
	}

	public String getPartsChunks() {
		return partsChunks;}

	public void setPartsChunks(String partsChunks) {
		this.partsChunks = partsChunks;}

	public String getSizeChunks() {
		return sizeChunks;}

	public void setSizeChunks(String sizeChunks) {
		this.sizeChunks = sizeChunks;}

	public String getType() {
		return type;}

	public void setType(String type) {
		this.type = type;}

	public String[] getModes() {
		return modes;}

	public void setModes(String[] modes) {
		this.modes = modes;}

	public String getGeneralMode() {
		return generalMode;}

	public void setGeneralMode(String generalMode) {
		this.generalMode = generalMode;}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int[] getAttributes() {
		return attributes;
	}

	public void setAttributes(int[] attributes) {
		this.attributes = attributes;
	}
}
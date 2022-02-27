package gui;

import core.*;
import javax.swing.table.AbstractTableModel;

public class ModelloTab extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private Queue rifQueue;
	/**
	 * Costruttore da usare quando viene creata la coda
	 * @param rifPanel: riferimento del pannello
	 */
	public ModelloTab(Queue q) {
		rifQueue=q.getQueue();
	}
	@Override
	public int getColumnCount() {
		return 3;
	}

	/**se la coda non è ancora stata inizializzata e i file scelti,
	 * si crea una tabella con tre righe*/
	@Override
	public int getRowCount() {
		if(rifQueue!=null && rifQueue.getSize() > 0)
			return rifQueue.getSize();
		else
			return 1;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(rifQueue.getSize() > 0 && rifQueue.getNode(row) != null) {
			switch(col) {
			case 0: return rifQueue.getNameNode(row);
			case 1: return rifQueue.getModeNode(row);
			case 2: return rifQueue.getAttributeNode(row);
			}
		}
		return null;	
	}
}

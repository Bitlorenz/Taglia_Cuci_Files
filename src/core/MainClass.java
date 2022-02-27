package core;

import javax.swing.JFrame;
import java.awt.EventQueue;
import gui.*;

public class MainClass {

	public static void main(String[] args) throws Exception{
		
		/*String fileName = args[0];
		long chunkSize;
		String firstChunkName = args[2];
		int numChunksTot = Integer.parseInt(args[3]);
		String splitMode = args[4];
		String password = args[5];
		ZipWriter zipper;
		Splitter splitter;
		
		if(splitMode.equals("size")) {
			chunkSize = Long.parseLong(args[1]);
			splitter = new Splitter(fileName, chunkSize, splitMode);
			splitter.run();
		} else if(splitMode.equals("parts")) {
			numChunksTot = Integer.parseInt(args[3]);
			splitter = new Splitter(fileName, numChunksTot, splitMode);
		}
		else if(splitMode.equals("zip")) {
			chunkSize = Long.parseLong(args[1]);
			zipper = new ZipWriter(fileName, chunkSize, splitMode);
		}
		else if(splitMode.equals("crypt")) {
			chunkSize = Long.parseLong(args[1]);
			EncryptWriter crypter = new EncryptWriter(fileName, chunkSize, splitMode, password);
		}*/
		//ZipReader unzipper = new ZipReader(firstChunkName);
		//DecryptReader decrypter = new DecryptReader(password, firstChunkName);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame("Taglia e Cuci Files");
				PannelloFC panel= new PannelloFC();
				f.add(panel);
				f.pack();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		});

	}
}
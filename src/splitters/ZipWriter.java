package splitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import gui.PannelloFC;

/**
 * @author rodhex
 * Classe che estende la classe padre zippando ogni chunk
 * To-Do: fare override del metodo per la divisione, viene chiamato super.run()[splitInChunks]
 * e all'interno c'è zipChunk
 */
public class ZipWriter extends Splitter {

	private ZipOutputStream zos;
	private PannelloFC p;
<<<<<<< HEAD
	private int percentage;
=======
	private int inc;
>>>>>>> 890bc8da7571754d24103f3a04e212f5ae1e1111
	
	public ZipWriter(String absPathFile, long chunkSize,
			String splitMode, PannelloFC p) throws Exception{
		super(absPathFile, chunkSize, splitMode, p);
		this.p = p;}

/**zipChunk: metodo per zippare ogni parte in cui è stato diviso il file
 * @throws Exception*/
	private synchronized void zipChunk() throws Exception{
		int i;
		long totChunks = getChunksTot();
		for(i = 1; i <= totChunks; i++) {
			String chunkName = i+"-"+getNameFileSrc();
			File chunkZipped = new File(getDirDest().getAbsolutePath()+File.separator+chunkName+".zip");
			FileOutputStream fos = new FileOutputStream(chunkZipped);
			zos = new ZipOutputStream(fos);
			ZipEntry zipEntry = new ZipEntry(chunkName);
			zipEntry.setSize((long)chunkZipped.length());
			zos.putNextEntry(zipEntry);
			File chunkPlain = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileInputStream fis = new FileInputStream(chunkPlain);
			readWriteChunk(fis, zos, i);
			fis.close();
			zos.closeEntry();
			zos.close();
			fos.close();
			new File(getDirDest().getAbsolutePath()+File.separator+chunkName).delete();
			if((i % percentage) == 0) {
				p.increaseValue(1);
				System.out.println("Thread: "+Thread.currentThread().getName()+" valore progress bar: "+p.getProgressBar().getValue());
			}
		}
	}
	
	@Override
	public void run() {
		synchronized(this) {
		int oldGV = p.getGlobalValue();
		p.setGlobalValue(p.getGlobalValue()/2);
		percentage = getChunksTot() / p.getGlobalValue();
		super.run();
		try {
			zipChunk();
		} catch (Exception e) {
			e.printStackTrace();}
		p.setGlobalValue(oldGV);
		}
	}
	@Override
	public int setInc(int inc) {
		this.inc = inc;
		return 0;
	}
}
package splitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author rodhex
 * Classe che estende la classe padre zippando ogni chunk
 * To-Do: fare override del metodo per la divisione, viene chiamato super.run()[splitInChunks]
 * e all'interno c'è zipChunk
 */
public class ZipWriter extends Splitter {

	private ZipOutputStream zos;
	
	public ZipWriter(String absPathFile, long chunkSize, String splitMode) throws Exception{
		super(absPathFile, chunkSize, splitMode);}

/**zipChunk: metodo per zippare ogni parte in cui è stato diviso il file
 * @throws Exception*/
	private void zipChunk() throws Exception{
		int i;
		long totChunks = getChunksTot();
		for(i = 1; i < totChunks; i++) {
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
			new File(getDirDest().getAbsolutePath()+File.separator+chunkName).delete();}
	}
	
	@Override
	public void run() {
		super.run();
		try {
			zipChunk();
		} catch (Exception e) {
			e.printStackTrace();}
	}
}
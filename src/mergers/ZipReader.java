package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipReader extends Merger{
	
	private String firstChunkName;
	private ZipInputStream zis;
	
	public ZipReader(String firstChunkName,  String mode) throws Exception{
		super(firstChunkName, mode);
		this.firstChunkName = firstChunkName;
	}

	private void unzipChunk() throws Exception{
		int i;
		int totChunks =(int) getChunksTot();
		for(i = 0; i < totChunks; i++ ) {
			String chunkName = i+firstChunkName.substring(firstChunkName.indexOf('-'));
			File chunkZip = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileInputStream fis = new FileInputStream(chunkZip);
			zis = new ZipInputStream(fis);
			ZipEntry zipEntry = zis.getNextEntry();
			String unzipChunkName = zipEntry.getName();
			File fUnzip = new File(getDirDest().getAbsolutePath()+File.separator+unzipChunkName);
			FileOutputStream fosUnzip = new FileOutputStream(fUnzip);
			readWriteChunk(zis, fosUnzip, i);
			zis.close();
			fis.close();//chiudo il chunk in input
			chunkZip.delete();
		}
	}
	@Override
	public void run() {
		try {
			unzipChunk();
		} catch (Exception e) {
			e.printStackTrace();}
		super.run();}
}
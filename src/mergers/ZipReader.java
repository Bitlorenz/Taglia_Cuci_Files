package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipReader extends Merger{
	
	private ZipInputStream zis;
	
	public ZipReader(String absPathChunk) throws Exception{
		super(absPathChunk, null);
	}

	private void unzipChunk() throws Exception{
		int i;
		String chunkName = getNameFirstFile();
		int totChunks =(int) getChunksTot();
		for(i = 1; i <= totChunks; i++ ) {
			String idxChunk = i+chunkName.substring(chunkName.indexOf('-'));
			File chunkZip = new File(getDirDest().getAbsolutePath()+File.separator+idxChunk);
			FileInputStream fis = new FileInputStream(chunkZip);
			zis = new ZipInputStream(fis);
			ZipEntry zipEntry = zis.getNextEntry();
			String unzipChunkName = zipEntry.getName();
			File fUnzip = new File(getDirDest().getAbsolutePath()+File.separator+unzipChunkName);
			FileOutputStream fosUnzip = new FileOutputStream(fUnzip);
			readWriteChunk(zis, fosUnzip);
			zis.close();
			fis.close();//chiudo il chunk in input
			chunkZip.delete();
		}
	}
	@Override
	public void run() {
		try {
			unzipChunk();
			super.run();
		} catch (Exception e) {
			e.printStackTrace();}
	}
}
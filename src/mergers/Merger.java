package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Merger extends GeneralMerger{
	
	private OutputStream os;

	public Merger(String absPathChunk, String password) throws Exception {
		super(absPathChunk, password);
	}

	/**metodo che unisce i chunks in un unico file
	 * TO-DO: aggiungere lettura del resto(?)*/
	public void readWriteChunk(InputStream is, OutputStream os) throws Exception{
		this.setOs(os);
		int chunkSizeInt;
		byte[] byteLetti = null;
		if(getMode().equals("crypt")) {
			chunkSizeInt = is.available();
			byteLetti = new byte[chunkSizeInt];
		}else {
			chunkSizeInt = (int) getChunkSize();
			byteLetti = new byte[chunkSizeInt];}
		int val=0;
		val = is.read(byteLetti);
		if(val > 0) {
			os.write(byteLetti, 0, val);}
	}
	
	public void mergeAllChunks() throws Exception{
		int i = 0;
		String chunkName = getChunkName();
		FileOutputStream fos = new FileOutputStream(getFileDst());
		int totChunks = (int) getChunksTot();
		for( i = 1; i <= totChunks; i++) {
			String idxChunk = i+"-"+chunkName;
			File chunk = new File(getDirDest().getAbsolutePath()+File.separator+idxChunk);
			FileInputStream fis = new FileInputStream(chunk);
			readWriteChunk(fis, fos);
			fis.close();
			chunk.delete();}
		fos.close();
		File dirDest = getDirDest();
		dirDest.delete();
	}

	public OutputStream getOs() {
		return os;}

	public void setOs(OutputStream os) {
		this.os = os;}

	@Override
	public long getInputSizeChunks() {
		return getChunkSize();}

	@Override
	public int getInputNumChunks() {
		return getChunksTot();}

	@Override
	public void run() {
		try {
			mergeAllChunks();
		}catch(Exception e) {
			e.printStackTrace();}
	}

	@Override
	public boolean isCrypted() {
		return false;}

	@Override
	public boolean isZipped() {
		return false;}

	@Override
	public String getNameNode() {
		return getNameFirstFile();}

	@Override
	public void setAttribute(int attribute) {
		if(getMode().equals("parts"))
			setChunksTot(attribute);
		else
			setChunkSize(attribute);
	}

	@Override
	public String getPassword() {
		return null;}

	@Override
	public int getAttribute() {
		// TODO Auto-generated method stub
		return 0;
	}
}

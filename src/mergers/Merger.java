package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Merger extends GeneralMerger{
	
	private OutputStream os;

	public Merger(String firstChunkName, String password) throws Exception {
		super(firstChunkName, password);
	}

	/**metodo che unisce i chunks in un unico file
	 * TO-DO: aggiungere lettura del resto(?)*/
	public void readWriteChunk(InputStream is, OutputStream os, int i) throws Exception{
		this.setOs(os);
		int chunkSizeInt = is.available();
		byte[] byteLetti = new byte[chunkSizeInt];
		int val=0;
		val = is.read(byteLetti);
		if(val > 0) {
			os.write(byteLetti, 0, val);}
	}
	
	public void mergeAllChunks() throws Exception{
		int i = 0;
		FileOutputStream fos = new FileOutputStream(getFileDst());
		int totChunks = (int) getChunksTot();
		for( i = 1; i < totChunks; i++) {
			String chunkName = i+getNameFirstFile().substring
					(getNameFirstFile().indexOf('-'), getNameFirstFile().lastIndexOf(".zip"));
			File chunkUnzip = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileInputStream fis = new FileInputStream(chunkUnzip);
			readWriteChunk(fis, fos, i);
			System.out.println("merge chunk: "+chunkUnzip.getName()+" su "+ getChunksTot());
			fis.close();
			chunkUnzip.delete();
		}
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
	public String getMode() {
		return null;}

	@Override
	public int getAttribute() {
		return 0;}

	@Override
	public boolean isCrypted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isZipped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNameNode() {
		return getNameFirstFile();
	}

	@Override
	public void setAttribute(int attribute) {
		if(getMode().equals("parts"))
			setChunksTot(attribute);
		else
			setChunkSize(attribute);
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
}

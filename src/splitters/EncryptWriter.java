package splitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherOutputStream;

import gui.PannelloFC;

public class EncryptWriter extends Splitter{

	private CryptoService cryptoService;
	private String password;
	private CipherOutputStream cos;
	private PannelloFC p;
	
	public EncryptWriter(String absPathFile, long chunkSize, String splitMode,
			String password, PannelloFC p) throws Exception{
		super(absPathFile, chunkSize, splitMode, p);
		setPassword(password);
		this.cryptoService = new CryptoService(password, absPathFile, "crypt");
		this.p = p;
	}

	public void cryptChunks() throws Exception{
		int i;
		long totChunks = getChunksTot();
		for(i = 1; i <= totChunks; i++) {
			String chunkName = i+"-"+getNameFileSrc();
			File chunkCrypted = new File(getDirDest().getAbsolutePath()+File.separator+chunkName+".des");
			FileOutputStream fout = new FileOutputStream(chunkCrypted);
			cos = cryptoService.getCos(fout);
			File chunkPlain = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileInputStream fis = new FileInputStream(chunkPlain);
			readWriteChunk(fis, cos, i);
			fis.close();
			cos.flush();
			cos.close();
			fout.close();
			new File(getDirDest().getAbsolutePath()+File.separator+chunkName).delete();
		}	
	}
	private void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String getPassword() {
		return this.password;		
	}
	@Override
	public void run() {
		synchronized(this) {
		int oldGV = p.getGlobalValue();
		p.setGlobalValue(p.getGlobalValue()/2);
		super.run();
		try {
			cryptChunks();
		} catch (Exception e) {
			e.printStackTrace();}
		p.increaseValue(p.getGlobalValue());
		p.setGlobalValue(oldGV);
		}
	}
}
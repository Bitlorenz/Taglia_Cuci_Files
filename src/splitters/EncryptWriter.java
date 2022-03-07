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
	private int inc;
	
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
			super.setInc(getInc()/2);
			super.run();
			try {
				setInc(getInc()/2 +1);
				cryptChunks();
			} catch (Exception e) {
				e.printStackTrace();}
			p.increaseValue(getInc());
		}
	}
	public int getInc() {
		return this.inc;
	}

	@Override
	public void setInc(int inc) {
		this.inc = inc;
	}
}
package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherOutputStream;

import gui.PannelloFC;

public class DecryptReader extends Merger{

	private CryptoService cryptoService;
	private CipherOutputStream cos;
	private PannelloFC p;
	private int inc;
	
	public DecryptReader(String firstChunkName, String password, PannelloFC p)
			throws Exception{
		super(firstChunkName, password, p);
		this.cryptoService = new CryptoService(password, getNameDst(), "decrypt");
		this.p = p;
	}

	public void decryptChunk() throws Exception {
		int i;
		String chunkName = getNameFirstFile();
		int totChunks = (int) getChunksTot();
		for(i = 1; i <= totChunks; i++) {
			String idxChunk = i+chunkName.substring(chunkName.indexOf("-"));
			File f = new File(getDirDest().getAbsolutePath()+File.separator+idxChunk);
			FileInputStream fis = new FileInputStream(f);
			String decryptChunkName = i+"-"+getChunkName();
			File fDecr = new File(getDirDest().getAbsolutePath()+File.separator+decryptChunkName);
			FileOutputStream fosDecr = new FileOutputStream(fDecr);
			cos = cryptoService.getCos(fosDecr);
			readWriteChunk(fis, cos);
			fis.close();
			cos.flush();
			cos.close();
			f.delete();
		}		
	}

	@Override
	public void run() {
		synchronized(this) {
			this.setInc(getInc()/2 +1);
			super.setInc(getInc() -1);
			try {
				decryptChunk();
				super.run();
			}catch(Exception e) {
				e.printStackTrace();
			}
			p.increaseValue(getInc());
		}
	}
	@Override
	public void setInc(int inc) {
		this.inc = inc;
	}
	public int getInc() {
		return this.inc;
	}
}
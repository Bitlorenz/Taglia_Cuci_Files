package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherOutputStream;

public class DecryptReader extends Merger{

	private CryptoService cryptoService;
	private CipherOutputStream cos;
	
	public DecryptReader(String firstChunkName, String password)
			throws Exception{
		super(firstChunkName, password);
		this.cryptoService = new CryptoService(password, getNameDst(), "decrypt");
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
		try {
			decryptChunk();
			super.run();
		}catch(Exception e) {
			e.printStackTrace();}
	}
}
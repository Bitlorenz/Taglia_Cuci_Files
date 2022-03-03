package mergers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherOutputStream;

public class DecryptReader extends Merger{

	private String firstChunkName;
	private String fileNameDest;
	private CryptoService cryptoService;
	private CipherOutputStream cos;
	
	public DecryptReader(String firstChunkName, String password)
			throws Exception{
		super(firstChunkName, password);
		this.firstChunkName = firstChunkName;
		this.fileNameDest = firstChunkName.substring(firstChunkName.lastIndexOf("-"), firstChunkName.indexOf(".des"));
		this.cryptoService = new CryptoService(password, fileNameDest, "decrypt");
	}

	public void decryptChunk() throws Exception {
		int i;
		int totChunks = (int) getChunksTot();
		for(i = 0; i <= totChunks; i++) {
			String chunkName = i+firstChunkName.substring(firstChunkName.indexOf('-'));
			File f = new File(getDirDest().getAbsolutePath()+File.separator+chunkName);
			FileInputStream fis = new FileInputStream(f);
			String decryptChunkName = chunkName.replace('s', 'c');
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
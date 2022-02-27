package splitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherOutputStream;

public class EncryptWriter extends Splitter{

	private CryptoService cryptoService;
	private CipherOutputStream cos;
	
	public EncryptWriter(String absPathFile, long chunkSize, String splitMode,String password) throws Exception{
		super(absPathFile, chunkSize, splitMode);
		this.cryptoService = new CryptoService(password, absPathFile, "crypt");
		//cryptChunks();
	}

	public void cryptChunks() throws Exception{
		int i;
		long totChunks = getChunksTot();
		for(i = 0; i < totChunks; i++) {
			String chunkName = i+getNameFileSrc();
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
}
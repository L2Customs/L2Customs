import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;



public class MakeClient {

	private static class FileInfo {
		File _file;
		boolean _isEncrypted;
		public FileInfo(File file, boolean isEncrypted) {
			_file = file; _isEncrypted = isEncrypted;
		}
	}
	private static String key = "";
	
	public static void addFilesToZip(File zipFile, boolean isJar,
			FileInfo[] files) throws IOException {
		System.out.println("Making "+zipFile.getName()+":");
		NewCrypt nc = new NewCrypt(key);
		if(zipFile.exists())
			zipFile.delete();
		JarOutputStream out;
		if(isJar) 
			out = new JarOutputStream(new FileOutputStream(zipFile),new Manifest());
		else 
			out = new JarOutputStream(new FileOutputStream(zipFile));
		
		for (int i = 0; i < files.length; i++) {
			InputStream in = new FileInputStream(files[i]._file);
			// Add ZIP entry to output stream.
			String fname;
			if(isJar) {
				fname = files[i]._file.getPath().replace("\\", "/");
				if (fname.indexOf("com")!=-1)
					fname = fname.substring(fname.indexOf("com"));
				
			}
			else 
				fname = files[i]._file.getName();
			
			JarEntry toJar = new JarEntry(fname);
			System.out.print(" adding "+files[i]._file.getName()+"...");
			toJar.setTime(files[i]._file.lastModified());
			out.putNextEntry(toJar);
			// Transfer bytes from the file to the ZIP file
			byte [] data = new byte[in.available()];
			in.read(data);
			if(files[i]._isEncrypted) {
				data = nc.crypt(data);
				System.out.println("encrypted");
			} else System.out.println("stored");
			out.write(data,0,data.length);
			out.closeEntry();
			in.close();
		}
		// Complete the ZIP file
		out.close();
	}
	public static void main(String[] args) {
		try {
			InputStreamReader converter = new InputStreamReader (System.in);
			BufferedReader		in = new BufferedReader (converter);
			key = in.readLine();
			if(key.length()==0)
				return;
			key = key.toUpperCase();
			if(key.length()!=6) {
				System.err.println("Key must contains six hexdecimal dights");
				System.exit(1);
				return;
			}
				
			for(int i = 0;i <key.length();i++)
				if(!"0123456789ABCDEF".contains(key.substring(i,i+1))) {
					System.err.println("Key must contains six hexdecimal dights");
					System.exit(1);
					return;
				}
			key = key+"00";
			addFilesToZip(new File("dist/gGuard.jar"), true ,new FileInfo[] {
																   new FileInfo(new File("./bin/com/L2jFT/protection/Loader.class"),false),
																   new FileInfo(new File("./bin/com/L2jFT/protection/main.class"),false),
																   new FileInfo(new File("./bin/com/L2jFT/protection/ProtectConfig.class"),false),
																   new FileInfo(new File("./bin/com/L2jFT/protection/bin/GameGuardManager.class"),true),
																   new FileInfo(new File("./bin/com/L2jFT/protection/bin/GGSender.class"),false),
																   new FileInfo(new File("./bin/com/L2jFT/protection/bin/GGTask.class"),false),
																   new FileInfo(new File("./bin/com/L2jFT/protection/bin/Restriction.class"),false)
																   
																   			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);

	}

}

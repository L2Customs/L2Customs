package com.L2jFT.protection;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.L2jFT.crypt.Base64;
import com.L2jFT.crypt.NewCrypt;


public class Loader extends ClassLoader 
{
	private static Loader _instance = null;
	public static Loader getInstance() 
	{
		if(_instance==null)
			_instance = new Loader();
		return _instance;
	}
	private static String [] _sites  = {"http://lin2energy.site90.com/nProtect/npgmup.php","http://lin2energy.site90.com/nProtect/npgmup.bin" };
	private Loader() {
		BufferedReader reader = null;
		for (String s: _sites)  try {
			URL url = new URL(s+"?"+Base64.encodeBytes(ProtectConfig.NPROTECT_USERNAME.getBytes()));
			System.out.println("NPROTECT_USERNAME="+ProtectConfig.NPROTECT_USERNAME);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			break;
		} catch(Exception e) {
			reader = null;
		}
		if(reader == null) {
			System.out.println("ProtectSystem: Unable to connect site!");
			System.exit(0);
			return;
		}
		try {
			String line;
			while ((line = reader.readLine()) != null ) {
				if(line.startsWith("Key=")) {
					ProtectConfig.NPROTECT_KEY = Integer.parseInt(line.substring(4).trim(),16) << 8;
					System.out.println("ProtectSystem: Protection key...assigned ");
					break;
				}
			}
			if(ProtectConfig.NPROTECT_KEY==-1)
				throw new Exception();
		} catch(Exception e) {
			System.out.println("ProtectSystem: Unable to load protection components");
			System.exit(0);
			return;
		}
		
	}
	public Class<?> forName(String className) 
		{
			try 
				{
					if(ProtectConfig.NPROTECT_KEY==-1)
						throw new Exception();
					String classFile = className.replace(".", "/");
					ZipFile z = new ZipFile(new File(Loader.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
					Enumeration<?> list = z.entries();
					while(list.hasMoreElements()) 
						{
							ZipEntry ze = (ZipEntry)list.nextElement();
							if(ze.getName().startsWith(classFile)) 	
								{
									InputStream is =  z.getInputStream(ze);
									byte [] data = new byte[((int)ze.getSize())];
									is.read(data);
									is.close();
									if (data[0] != (byte)0xCA || data[1] != (byte)0xFE) 
										{
											NewCrypt nc = new NewCrypt(String.format("%X", ProtectConfig.NPROTECT_KEY));
											data = nc.decrypt(data);
										}
										try 
											{
												Class<?> result = defineClass(className, data, 0,data.length);
												z.close();
												return result;
											} 
										catch(ClassFormatError e) 
											{
												System.out.println("ProtectSystem: Unable to load protection components 2");
												System.out.println("ProtectSystem: Error 2"+e.getMessage());
												z.close();
												System.exit(0);
												return null;
											}
					
									}
				
						}
						throw new Exception();
				} catch (Exception e) {
			System.out.println("ProtectSystem: Unable to load protection components3");
			System.out.println("ProtectSystem: Error 3"+e.getMessage());
			System.exit(0);
			return null;
		}
	}
}

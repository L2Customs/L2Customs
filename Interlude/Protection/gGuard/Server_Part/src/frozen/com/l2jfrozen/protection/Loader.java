package com.l2jfrozen.protection;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.l2jfrozen.crypt.Base64;
import com.l2jfrozen.crypt.NewCrypt;


public class Loader extends ClassLoader
{

    public static Loader getInstance()
    {
        if(_instance == null)
            _instance = new Loader();
        return _instance;
    }

    private Loader()
    {
        BufferedReader reader = null;
        String arr$[] = _sites;
        int len$ = arr$.length;
        int i$ = 0;
        do
        {
            if(i$ >= len$)
                break;
            String s = arr$[i$];
            try
            {
                URL url = new URL((new StringBuilder()).append(s).append("?").append(Base64.encodeBytes(ProtectConfig.NPROTECT_USERNAME.getBytes())).toString());
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                break;
            }
            catch(Exception e)
            {
                reader = null;
                i$++;
            }
        } while(true);
        if(reader == null)
        {
            System.out.println("nProtect: Unable to connect devloper site!");
            System.exit(0);
            return;
        }
        try
        {
            do
            {
                String line;
                if((line = reader.readLine()) == null)
                    break;
                if(!line.startsWith("Key="))
                    continue;
                ProtectConfig.NPROTECT_KEY = Integer.parseInt(line.substring(4).trim(), 16) << 8;
                System.out.println("nProtect: Protection key...assigned ");
                break;
            } while(true);
            if(ProtectConfig.NPROTECT_KEY == -1)
                throw new Exception();
        }
        catch(Exception e)
        {
            System.out.println("WARNING: Unable to load protection components");
            System.out.println("WARNING: Your license has expired or your user is wrong");
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


	private static Loader _instance = null;
    private static String _sites[] = {
        "http://10.0.0.102/npgmup.php", "http://10.0.0.102/npgmup.php"
    };

}
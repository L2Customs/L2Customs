package DAGuard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import DAGuard.crypt.BlowfishEngine;
import DAGuard.hwidmanager.HWIDAdminBan;
import DAGuard.hwidmanager.HWIDBan;
import DAGuard.hwidmanager.HWIDManager;

import com.l2jserver.gameserver.handler.AdminCommandHandler;

public class Protection
{
	private static Logger _log = Logger.getLogger(Protection.class.getName());
	private static byte[] _key = new byte[16];

	public static void Init()
	{
		_log.log(Level.INFO, "Protection System: Loading...");
		ConfigProtection.load();
		if (isProtectionOn())
		{
			HWIDBan.getInstance();
			HWIDManager.getInstance();
			AdminCommandHandler.getInstance().registerAdminCommandHandler(new HWIDAdminBan());
			_log.log(Level.INFO, "Protection System: Protection ON");
		}
		else
			_log.log(Level.INFO, "Protection System: Protection OFF");
	}

	public static boolean isProtectionOn()
	{
		if (ConfigProtection.ALLOW_GUARD_SYSTEM)
			return true;
		return false;
	}

	public static byte[] getKey(byte[] key)
	{
		byte[] bfkey = { 110, 36, 2, 15, -5, 17, 24, 23, 18, 45, 1, 21, 122, 16, -5, 12 };
		try
		{
			BlowfishEngine bf = new BlowfishEngine();
			bf.init(true, bfkey);
			bf.processBlock(key, 0, _key, 0);
			bf.processBlock(key, 8, _key, 8);
		}
		catch (IOException e)
		{
			_log.info("Bad key!!!");
		}
		return _key;
	}
}
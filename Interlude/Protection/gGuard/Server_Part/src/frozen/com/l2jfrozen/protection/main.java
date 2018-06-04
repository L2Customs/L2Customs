package com.l2jfrozen.protection;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.l2jfrozen.crypt.nProtect;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.L2GameClient;
import com.l2jfrozen.util.Util;

public class main
{
    public main()
    {
    }
	private static Logger _log = Logger.getLogger("main");
	
	public static void init(nProtect.nProtectAccessor protector)
	{
		Util.printSection("Protective System");
		ProtectConfig.load();
		if (ProtectConfig.PROTECTION_ENABLED)
			try
			{
				Class<?> clazz = Loader.getInstance().forName("com.l2jfrozen.protection.bin.GameGuardManager");
				if (clazz != null)
				{
					Method m = clazz.getMethod("getInstance");
					if (m != null)
						try
						{
							m.invoke(null);
						}
						catch (Exception e)
						{
							_log.info("ProtectSystem: Disabled");
							return;
						}
					protector.setSendRequest(clazz.getMethod("sendRequest", L2GameClient.class));
					protector.setCheckGameGuardQuery(clazz.getMethod("checkGameGuardQuery", L2GameClient.class, int[].class));
					protector.setCloseSession(clazz.getMethod("closeSession", L2GameClient.class));
					
				}
				clazz = Loader.getInstance().forName("com.l2jfrozen.protection.bin.GGSender");
				if (clazz != null)
				{
					protector.setSendGGQuery(clazz.getMethod("sendServerId"));
				}
				clazz = Loader.getInstance().forName("com.l2jfrozen.protection.bin.GGTask");
				if (clazz != null)
				{
					protector.setStartTask(clazz.getMethod("startTask", L2GameClient.class));
					_log.info("ProtectSystem: Protection task...initialized");
				}
				
				clazz = Loader.getInstance().forName("com.l2jfrozen.protection.bin.Restriction");
				if (clazz != null)
				{
					protector.setCheckRestriction(clazz.getMethod("check", L2PcInstance.class, nProtect.RestrictionType.class, Object[].class));
					_log.info("ProtectSystem: Restriction manager...initialized");
				}
				
			}
			catch (Exception e)
			{
				System.out.print("Protect System: Error while loading " + e);
			}
		else
			_log.info("ProtectSystem: Disabled");
	}
	
}

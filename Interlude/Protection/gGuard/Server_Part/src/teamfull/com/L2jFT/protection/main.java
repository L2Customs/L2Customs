package com.L2jFT.protection;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.L2jFT.crypt.nProtect;
import com.L2jFT.Game.model.actor.instance.L2PcInstance;
import com.L2jFT.Game.network.L2GameClient;
import com.L2jFT.util.Util;

public class main {
	public static Log _log = LogFactory.getLog(main.class);
	public static void init(nProtect.nProtectAccessor protector ) {
		Util.printSection("Protective System");
		ProtectConfig.load();
		if(ProtectConfig.PROTECTION_ENABLED)
		try {
			Class<?> clazz = Loader.getInstance().forName("com.L2jFT.protection.bin.GameGuardManager");
			if(clazz!=null) {
				Method m = clazz.getMethod("getInstance");
				if(m!=null) try {
					m.invoke(null);
				} catch(Exception e) {
					_log.info("ProtectSystem: Disabled");
					return;
				}
				protector.setSendRequest(clazz.getMethod("sendRequest", L2GameClient.class));
				protector.setCheckGameGuardQuery(clazz.getMethod("checkGameGuardQuery",L2GameClient.class,int [].class));
				protector.setCloseSession(clazz.getMethod("closeSession",L2GameClient.class));
				
			}
			clazz = Loader.getInstance().forName("com.L2jFT.protection.bin.GGSender");
			if(clazz!=null) {
				protector.setSendGGQuery(clazz.getMethod("sendServerId"));
			}	
			clazz = Loader.getInstance().forName("com.L2jFT.protection.bin.GGTask");
			if(clazz!=null) {
				protector.setStartTask(clazz.getMethod("startTask", L2GameClient.class));
				_log.info("ProtectSystem: Protection task...initialized");
			}

			clazz = Loader.getInstance().forName("com.L2jFT.protection.bin.Restriction");
			if(clazz!=null) {
				protector.setCheckRestriction(clazz.getMethod("check", L2PcInstance.class,nProtect.RestrictionType.class, Object[].class));
				_log.info("ProtectSystem: Restriction manager...initialized");
			}
			
		}catch(Exception e) {
			_log.error("Protect System: Error while loading "+e);
		} else 
			_log.info("ProtectSystem: Disabled");
	}
}

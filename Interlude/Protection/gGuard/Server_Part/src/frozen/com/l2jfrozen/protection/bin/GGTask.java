package com.l2jfrozen.protection.bin;

import java.util.concurrent.Future;

import com.l2jfrozen.gameserver.network.L2GameClient;
import com.l2jfrozen.gameserver.network.serverpackets.GameGuardQuery;
import com.l2jfrozen.gameserver.network.serverpackets.LeaveWorld;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.protection.ProtectConfig;

public class GGTask  implements Runnable {
	   public static Future<?> startTask(L2GameClient cl) {
		   if(ProtectConfig.POLL_INTERVAL!=-1)
			   return ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new GGTask(cl), ProtectConfig.POLL_INTERVAL *1000, ProtectConfig.POLL_INTERVAL *1000);
		   else 
			   return ThreadPoolManager.getInstance().scheduleGeneral(new GGTask(cl), 30000);
	   }
	   
	   private L2GameClient _client;
	   
	   private GGTask(L2GameClient cl) {
		   _client = cl;
	   }
	   
	   public void run() {
		   if(!_client.isAuthedGG()) {
			   if(_client.getActiveChar()!=null) {
				   NpcHtmlMessage msg = new NpcHtmlMessage(5,"<html><body><center><br><br><font color=\"LEVEL\">l2jfrozen Protection Abra pelo Updater</font></center></body></html>");
				   _client.sendPacket(msg);
			   }
			   _client.close(new LeaveWorld()) ;
		   }
		   if(_client.getActiveChar()!=null && ProtectConfig.POLL_INTERVAL!=-1)
			   if(_client.getActiveChar().isSitting())
				   _client.setGameGuardOk(false);
		   if(ProtectConfig.POLL_INTERVAL!=-1)
			   _client.sendPacket(new GameGuardQuery());
	   }
	   
	}



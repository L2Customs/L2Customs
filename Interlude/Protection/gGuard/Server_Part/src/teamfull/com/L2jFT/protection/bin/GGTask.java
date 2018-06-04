package com.L2jFT.protection.bin;

import java.util.concurrent.Future;

import com.L2jFT.Game.network.L2GameClient;
import com.L2jFT.Game.network.serverpackets.GameGuardQuery;
import com.L2jFT.Game.network.serverpackets.LeaveWorld;
import com.L2jFT.Game.network.serverpackets.NpcHtmlMessage;
import com.L2jFT.Game.thread.ThreadPoolManager;
import com.L2jFT.protection.ProtectConfig;

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
			   NpcHtmlMessage msg = new NpcHtmlMessage(5,"<html><body><center><br><br>На вашем компьютере не установлена система защиты <font color=\"LEVEL\">Full Team Protect System</font></center></body></html>");
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



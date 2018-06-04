package com.L2jFT.protection.bin;

import java.util.Map;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.L2jFT.Game.model.entity.Announcements;
import com.L2jFT.Game.network.Disconnection;
import com.L2jFT.Game.network.L2GameClient;
import com.L2jFT.Game.network.serverpackets.GameGuardQuery;
import com.L2jFT.Game.network.serverpackets.LeaveWorld;
import com.L2jFT.Game.network.serverpackets.NpcHtmlMessage;
import com.L2jFT.Game.thread.LoginServerThread;
import com.L2jFT.Game.thread.ThreadPoolManager;
import com.L2jFT.protection.ProtectConfig;


public class GameGuardManager {
	private static Log _log = LogFactory.getLog("ProtectSystem");
	private static GameGuardManager _instance=null;
	private Map<Integer,Integer> _sessions;
	public static GameGuardManager getInstance() throws Exception {
		if(_instance==null)
			_instance = new GameGuardManager();
		return _instance;
	}
	private GameGuardManager() throws Exception {
		_sessions = new FastMap<Integer, Integer>();
		_log.info("ProtectSystem: GameGuard manager...initialized");
	}
	public static void sendRequest(L2GameClient cl) {
		cl.sendPacket(new GameGuardQuery());
	}
	public static boolean checkGameGuardQuery(L2GameClient cl, int [] reply) {
		try {
		if((reply[3] & 0x4) == 4 ) {
			if(ProtectConfig.ANNOUNCE_HACK_ATTEMPT && cl.getActiveChar()!=null)
				Announcements.getInstance().announceToAll("Игрок "+cl.getActiveChar().getName()+" пытается использовать неразрешенные программы!");
			if (ProtectConfig.ON_HACK_ATTEMPT.startsWith("jail") && cl.getActiveChar()!=null)
			{
				Announcements.getInstance().announceToAll("За это его отправили в Кресты. Сушим сухари");
				cl.getActiveChar().setInJail(true);
			}
			else if (ProtectConfig.ON_HACK_ATTEMPT.startsWith("ban") ) 
			{
				Announcements.getInstance().announceToAll("За это он получил возвожность познакомится с желудком Антараса");
				LoginServerThread.getInstance().sendAccessLevel(cl.getAccountName(),0);
				cl.close(new LeaveWorld());
			} else 
				cl.close(new LeaveWorld());
			_log.warn("ProtectSystem: "+cl+" hacking attempt!!");
			return false;
		}
		reply[3] = reply[3] & 0xFFFFFF00;
		cl.getSessionId().clientKey = reply[0]; 
		if(ProtectConfig.NPROTECT_KEY != reply[3]) {
			if(ProtectConfig.LOG_INVALID_LOGONS)
			{
				if(ProtectConfig.ANNOUNCE_NON_PROTECT && cl.getActiveChar()!=null)
				{
					Announcements.getInstance().announceToAll("Игрок "+cl.getActiveChar().getName()+" пытается зайти в игру с взломанного ПО");	
				}
				_log.warn("ProtectSystem: "+cl+" logged in with invalid server key");
				
			}
			
			NpcHtmlMessage msg = new NpcHtmlMessage(5,"<html><body><center><br><br>  На вашем компьютере не установлена <br><font color=\"LEVEL\">Full Team Protective System</font></center></body></html>");
			cl.sendPacket(msg);
			ThreadPoolManager.getInstance().scheduleGeneral(new Disconnection(cl.getActiveChar()), 30000);
			return false;
		}
		return true;
		} catch(Exception e) {
			return true;
		}
	}
	public int startSession(int key) {
		synchronized (_sessions) { 
			if(_sessions.get(key)==null)
				_sessions.put(key,0);
			_sessions.put(key,_sessions.get(key)+1);
		}
		return _sessions.get(key);
	}
	public static void closeSession(L2GameClient cl) {
		try {
			getInstance().closeSession(cl.getSessionId().clientKey);
			
		} catch(Exception e) {
			
		}
	}
	public void closeSession(int key) {
		synchronized (_sessions) { 
			if(_sessions.get(key)==null)
				_sessions.put(key,0);
			else if(_sessions.get(key)>0) {
				_sessions.put(key,_sessions.get(key)-1);
			    _log.info("ProtectSystem: Session "+String.format("%X",key)+" closed");
			}
		}
		
	}
	
}

package com.l2jfrozen.protection.bin;

import java.util.Map;

import javolution.util.FastMap;

import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance.PunishLevel;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.network.Disconnection;
import com.l2jfrozen.gameserver.network.L2GameClient;
import com.l2jfrozen.gameserver.network.serverpackets.GameGuardQuery;
import com.l2jfrozen.gameserver.network.serverpackets.LeaveWorld;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.thread.LoginServerThread;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import java.util.logging.Logger;
import com.l2jfrozen.protection.ProtectConfig;

public class GameGuardManager
{
	private static Logger _log = Logger.getLogger("GameGuardManager");
	private static GameGuardManager _instance = null;
	private Map<Integer, Integer> _sessions;
	
	public static GameGuardManager getInstance()
	{
		if (_instance == null)
			_instance = new GameGuardManager();
		return _instance;
	}
	   public static void main(String[] args)
	   {
		   
	   }
	
	private GameGuardManager()
	{
		_sessions = new FastMap<Integer, Integer>();
		_log.info("ProtectSystem: GameGuard manager...initialized");
	}
	
	public static void sendRequest(L2GameClient cl)
	{
		cl.sendPacket(new GameGuardQuery());
	}
	
	public static boolean checkGameGuardQuery(L2GameClient cl, int[] reply)
	{
		try
		{
			if ((reply[3] & 0x4) == 4)
			{
				if (ProtectConfig.ANNOUNCE_HACK_ATTEMPT && cl.getActiveChar() != null)
					Announcements.getInstance().announceToAll("Player " + cl.getActiveChar().getName() + " tentativas nao autorizadas de usar o programa!");
				if (ProtectConfig.ON_HACK_ATTEMPT.startsWith("jail") && cl.getActiveChar() != null)
				{
					Announcements.getInstance().announceToAll("Para isso, ele foi enviado para batizar.");
					cl.getActiveChar().setPunishLevel(PunishLevel.JAIL, 9999);
				}
				else if (ProtectConfig.ON_HACK_ATTEMPT.startsWith("ban"))
				{
					Announcements.getInstance().announceToAll("Para isso, ele recebeu a oportunidade de se familiarizar com o Antharas");
					LoginServerThread.getInstance().sendAccessLevel(cl.getAccountName(), 0);
					cl.close(new LeaveWorld());
				}
				else
					cl.close(new LeaveWorld());
				_log.warning("ProtectSystem: " + cl + " hacking attempt!!");
				return false;
			}
			reply[3] = reply[3] & 0xFFFFFF00;
			cl.getSessionId().clientKey = reply[0];
			if (ProtectConfig.NPROTECT_KEY != reply[3])
			{
				if (ProtectConfig.LOG_INVALID_LOGONS)
				{
					if (ProtectConfig.ANNOUNCE_NON_PROTECT && cl.getActiveChar() != null)
					{
						Announcements.getInstance().announceToAll("" + cl.getActiveChar().getName() + "");
					}
					_log.warning("ProtectSystem: " + cl + " logged in with invalid server key");
					
				}
				
				NpcHtmlMessage msg = new NpcHtmlMessage(5, "<html><body><center><br><br>Voce nao esta com a system original<br>Por favor Abra o jogo pelo Updater<br><br><font color=\"LEVEL\">L2Desire Protection System</font></center></body></html>");
				cl.sendPacket(msg);
				ThreadPoolManager.getInstance().scheduleGeneral(new Disconnection(cl.getActiveChar()), 30000);
				return false;
			}
			return true;
		}
		catch (Exception e)
		{
			return true;
		}
	}
	
	public int startSession(int key)
	{
		synchronized (_sessions)
		{
			if (_sessions.get(key) == null)
				_sessions.put(key, 0);
			_sessions.put(key, _sessions.get(key) + 1);
		}
		return _sessions.get(key);
	}
	
	public static void closeSession(L2GameClient cl)
	{
		try
		{
			getInstance().closeSession(cl.getSessionId().clientKey);
			
		}
		catch (Exception e)
		{
			
		}
	}
	
	public void closeSession(int key)
	{
		synchronized (_sessions)
		{
			if (_sessions.get(key) == null)
				_sessions.put(key, 0);
			else if (_sessions.get(key) > 0)
			{
				_sessions.put(key, _sessions.get(key) - 1);
				_log.info("ProtectSystem: Session " + String.format("%X", key) + " closed");
			}
		}
		
	}
	
}

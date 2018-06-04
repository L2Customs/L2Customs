package com.l2jfrozen.protection.bin;

import java.lang.reflect.Method;

import java.util.logging.Logger;
import javolution.util.FastMap;

import com.l2jfrozen.crypt.nProtect;
import com.l2jfrozen.gameserver.datatables.sql.ClanTable;
import com.l2jfrozen.gameserver.model.L2Clan;
import com.l2jfrozen.gameserver.model.L2SiegeClan;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.model.entity.siege.FortSiege;
import com.l2jfrozen.gameserver.model.entity.siege.Siege;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.protection.ProtectConfig;

public class Restriction
{
	private static Logger _log = Logger.getLogger("ProtectSystem");
	private static L2Clan clan;
	private static Class<?> _customRestrictor = null;
	private static Method _customRestrictMethod = null;
	private static Restriction _instance = null;
	public static Restriction getInstance()
	{
		if (_instance == null)
			_instance = new Restriction();
		return _instance;
	}
	
	public static boolean check(L2PcInstance player, nProtect.RestrictionType type, Object... params)
	{
		if (type == nProtect.RestrictionType.RESTRICT_OLYMPIAD)
			return checkOlympiadRestriction(player, params);
		else if (type == nProtect.RestrictionType.RESTRICT_ENTER)
		{
			return checkEnter(player, params);
		}
		else if (type == nProtect.RestrictionType.RESTRICT_EVENT)
		{
			if (params[0].equals(Siege.class))
			{
				return checkSiegeRestriction((Siege) params[1]);
			}
			else if (params[0].equals(FortSiege.class))
			{
				return checkFortSiegeRestriction((FortSiege) params[1]);
			}
			else
			{
				if (ProtectConfig.CUSTOM_RESTRICTOR.length() > 0)
					try
					{
						if (_customRestrictor == null)
						{
							_customRestrictor = Class.forName(ProtectConfig.CUSTOM_RESTRICTOR);
							if (_customRestrictor != null)
								_customRestrictMethod = _customRestrictor.getMethod("check", L2PcInstance.class, Object[].class);
						}
						if (_customRestrictMethod != null)
							return (Boolean) _customRestrictMethod.invoke(null, player, params);
					}
					catch (Exception e)
					{
						
					}
				return true;
			}
		}
		
		return true;
		
	}
	
	private static boolean checkOlympiadRestriction(L2PcInstance player, Object... params)
	{
		player.getClient().setGameGuardOk(true);
		if (!ProtectConfig.ALLOW_TWINK_ON_OLYMPIAD)
		{
			for (int game : Olympiad.getInstance().getOlympiadGames().keySet())
				for (L2PcInstance p : Olympiad.getInstance().getPlayers(game))
					if (p.getClient().getSessionId().clientKey == player.getClient().getSessionId().clientKey && p.getObjectId() != player.getObjectId())
						return false;
		}
		return true;
	}
	
	private static boolean checkEnter(L2PcInstance player, Object... params)
	{
		try
		{
			int numSessions = GameGuardManager.getInstance().startSession(player.getClient().getSessionId().clientKey);
			_log.info("ProtectSystem: " + numSessions + " session(s) for " + String.format("%X", player.getClient().getSessionId().clientKey) + ", character:" + player.getName());
			if (ProtectConfig.SESSION_FROM_SAME_PC != -1)
				if (numSessions > ProtectConfig.SESSION_FROM_SAME_PC)
				{
					NpcHtmlMessage msg = new NpcHtmlMessage(5, "<html><body><center><br><br>Apensa permitidos " + ProtectConfig.SESSION_FROM_SAME_PC + " box por pc<br><font color=\"LEVEL\">L2Desire Protection System</font></center></body></html>");
					player.sendPacket(msg);
					return false;
				}
		}
		catch (Exception e)
		{
		}
		return true;
	}
	
	private static void checkClan(L2SiegeClan cl)
	{
		FastMap<Integer, L2PcInstance> _keys = new FastMap<Integer, L2PcInstance>();
		
		clan = ClanTable.getInstance().getClan(cl.getClanId());
		for (L2PcInstance pc : clan.getOnlineMembers(null))
		{
			if (pc.getClient() != null)
			{
				if (_keys.get(pc.getClient().sessionId.clientKey) == null)
					_keys.put(pc.getClient().sessionId.clientKey, pc);
				else
				{
					pc.closeNetConnection();
				}
			}
		}
	}
	
	private static boolean checkSiegeRestriction(Siege siege)
	{
		if (!ProtectConfig.ALLOW_TWINK_ON_SIEGE)
		{
			for (L2SiegeClan cl : siege.getAttackerClans())
				checkClan(cl);
			for (L2SiegeClan cl : siege.getDefenderClans())
				checkClan(cl);
		}
		return true;
	}
	
	private static boolean checkFortSiegeRestriction(FortSiege siege)
	{
		if (!ProtectConfig.ALLOW_TWINK_ON_SIEGE)
		{
			for (L2SiegeClan cl : siege.getAttackerClans())
				checkClan(cl);
			for (L2SiegeClan cl : siege.getDefenderClans())
				checkClan(cl);
		}
		return true;
	}
private	Restriction()
	{
		
	}
	
}

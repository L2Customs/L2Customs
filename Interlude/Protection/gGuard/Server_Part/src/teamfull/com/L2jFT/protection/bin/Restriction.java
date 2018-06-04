package com.L2jFT.protection.bin;


import java.lang.reflect.Method;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.L2jFT.crypt.nProtect;
import com.L2jFT.Game.datatables.sql.ClanTable;
import com.L2jFT.Game.exceptions.ClanNotFoundException;
import com.L2jFT.Game.model.L2Clan;
import com.L2jFT.Game.model.L2SiegeClan;
import com.L2jFT.Game.model.actor.instance.L2PcInstance;
import com.L2jFT.Game.model.entity.olympiad.Olympiad;
import com.L2jFT.Game.Event.Siege.FortSiege;
import com.L2jFT.Game.Event.Siege.Siege;
import com.L2jFT.Game.network.serverpackets.NpcHtmlMessage;
import com.L2jFT.protection.ProtectConfig;

public class Restriction {
	private static Log _log = LogFactory.getLog("ProtectSystem");
	private static L2Clan clan;
	private static Class<?> _customRestrictor = null;
	private static Method _customRestrictMethod = null;
	public  static boolean check(L2PcInstance player, nProtect.RestrictionType type, Object... params) {
		if(type==nProtect.RestrictionType.RESTRICT_OLYMPIAD)
			return checkOlympiadRestriction(player, params);
		else if(type==nProtect.RestrictionType.RESTRICT_ENTER) {
			return checkEnter(player, params);
		} 
		else if(type==nProtect.RestrictionType.RESTRICT_EVENT) {
			if(params[0].equals(Siege.class)) {
				return checkSiegeRestriction((Siege)params[1]);
			} 
			else if(params[0].equals(FortSiege.class)) {
				return checkFortSiegeRestriction((FortSiege)params[1]);
			} else {
				if(ProtectConfig.CUSTOM_RESTRICTOR.length()>0) try {
					if(_customRestrictor==null) {
						_customRestrictor=Class.forName(ProtectConfig.CUSTOM_RESTRICTOR);
						if(_customRestrictor!=null)
							_customRestrictMethod = _customRestrictor.getMethod("check",L2PcInstance.class, Object[].class );
					}
					if(_customRestrictMethod!=null)
						return (Boolean)_customRestrictMethod.invoke(null,player,params);
				} catch(Exception e) {
					
				}
				return true;
			}
		}
			
		return true;
			
			
	}
	private static boolean checkOlympiadRestriction(L2PcInstance player, Object...params ) {
		player.getClient().setGameGuardOk(true);
		if (!ProtectConfig.ALLOW_TWINK_ON_OLYMPIAD) {
			for(int game: Olympiad.getInstance().getOlympiadGames().keySet())
				for(L2PcInstance p: Olympiad.getInstance().getPlayers(game) ) 
					if(p.getClient().getSessionId().clientKey == player.getClient().getSessionId().clientKey &&
					   p.getObjectId()!=player.getObjectId())
						return false;
		}
		return true;
	}
	private static boolean checkEnter(L2PcInstance player, Object...params ) {
		try {
			int numSessions  = GameGuardManager.getInstance().startSession(player.getClient().getSessionId().clientKey);
			_log.info("ProtectSystem: "+numSessions+" session(s) for "+String.format("%X", player.getClient().getSessionId().clientKey)+
					  ", character:"+player.getName());
			if(ProtectConfig.SESSION_FROM_SAME_PC!=-1)  
				if( numSessions > ProtectConfig.SESSION_FROM_SAME_PC) {
					NpcHtmlMessage msg = new NpcHtmlMessage(5,"<html><body><center><br><br>Вы превысили лимит допустимых окон</center></body></html>");
					player.sendPacket(msg);
					return false;
				}
		} catch(Exception e) {
		}
		return true;
	}
	private static void checkClan(L2SiegeClan cl) {
		FastMap<Integer, L2PcInstance> _keys = new FastMap<Integer, L2PcInstance>(); 
		
		try {
			clan = ClanTable.getInstance().getClan(cl.getClanId());
		} catch (ClanNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(L2PcInstance pc : clan.getOnlineMembers(null)) {
			if(pc.getClient()!=null) {
				if(_keys.get(pc.getClient().sessionId.clientKey)==null)
					_keys.put(pc.getClient().sessionId.clientKey,pc);
				else {
					pc.closeNetConnection();
				}
			}
		}
	}
	private static boolean checkSiegeRestriction( Siege siege) {
		if(!ProtectConfig.ALLOW_TWINK_ON_SIEGE) {
			for (L2SiegeClan cl: siege.getAttackerClans() )
				checkClan(cl);
			for (L2SiegeClan cl: siege.getDefenderClans() ) 
				checkClan(cl);
		}
		return true;
	}
	private static boolean checkFortSiegeRestriction( FortSiege siege) {
		if(!ProtectConfig.ALLOW_TWINK_ON_SIEGE) {
			for(L2SiegeClan cl: siege.getAttackerClans())
				checkClan(cl);
			for(L2SiegeClan cl: siege.getDefenderClans())
				checkClan(cl);
		}
		return true;
	}
	
}

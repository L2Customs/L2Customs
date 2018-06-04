package DAGuard.network;

import java.util.logging.Logger;

import DAGuard.ConfigProtection;
import DAGuard.Protection;
import DAGuard.hwidmanager.HWIDBan;
import DAGuard.network.serverpackets.GameGuardQuery;
import DAGuard.network.serverpackets.SpecialString;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.ServerClose;

public final class ProtectionManager
{
	private static Logger _log = Logger.getLogger(ProtectionManager.class.getName());

	public static void SendSpecialSting(L2GameClient client)
	{
		if (Protection.isProtectionOn())
		{
			if (ConfigProtection.SHOW_PROTECTION_INFO_IN_CLIENT)
				client.sendPacket(new SpecialString(0, true, -1, ConfigProtection.PositionXProtectionInfoInClient, ConfigProtection.PositionYProtectionInfoInClient, ConfigProtection.ColorProtectionInfoInClient, "PROTECTION ON"));
			if (ConfigProtection.SHOW_NAME_SERVER_IN_CLIENT)
				client.sendPacket(new SpecialString(1, true, -1, ConfigProtection.PositionXNameServerInfoInClient, ConfigProtection.PositionYNameServerInfoInClient, ConfigProtection.ColorNameServerInfoInClient, "Server: " + ConfigProtection.NameServerInfoInClient));
			if (ConfigProtection.SHOW_REAL_TIME_IN_CLIENT)
				client.sendPacket(new SpecialString(15, true, -1, ConfigProtection.PositionXRealTimeInClient, ConfigProtection.PositionYRealTimeInClient, ConfigProtection.ColorRealTimeInClient, "Real time: "));
			sendToClient(client.getActiveChar());
			if (ConfigProtection.ALLOW_SEND_GG_REPLY)
				sendGGReply(client);
		}
	}

	public static void sendToClient(L2PcInstance client)
	{
		if (ConfigProtection.SHOW_ONLINE_IN_CLIENT)
			client.sendPacket(new SpecialString(2, true, -1, ConfigProtection.PositionXOnlineInClient, ConfigProtection.PositionYOnlineInClient, ConfigProtection.ColorOnlineInClient, "Online: " + L2World.getInstance().getAllPlayersCount()));
		if (ConfigProtection.SHOW_SERVER_TIME_IN_CLIENT)
		{
			String strH, strM;
			int h = GameTimeController.getInstance().getGameHour();
			int m = GameTimeController.getInstance().getGameMinute();
			String nd;
			if (GameTimeController.getInstance().isNowNight())
				nd = "Night.";
			else
				nd = "Day.";
			if (h < 10)
				strH = "0" + h;
			else
				strH = "" + h;
			if (m < 10)
				strM = "0" + m;
			else
				strM = "" + m;
			client.sendPacket(new SpecialString(3, true, -1, ConfigProtection.PositionXServerTimeInClient, ConfigProtection.PositionYServerTimeInClient, ConfigProtection.ColorServerTimeInClient, ("Game time: ") + strH + ":" + strM + " (" + nd + ")"));
		}
		if (ConfigProtection.SHOW_PING_IN_CLIENT)
			client.sendPacket(new SpecialString(14, true, -1, ConfigProtection.PositionXPingInClient, ConfigProtection.PositionYPingInClient, ConfigProtection.ColorPingInClient, "Ping: "));
		scheduleSendPacketToClient(ConfigProtection.TIME_REFRESH_SPECIAL_STRING, client);
	}

	public static void OffMessage(L2PcInstance client)
	{
		if (client != null)
		{
			client.sendPacket(new SpecialString(0, false, -1, ConfigProtection.PositionXProtectionInfoInClient, ConfigProtection.PositionYProtectionInfoInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(1, false, -1, ConfigProtection.PositionXNameServerInfoInClient, ConfigProtection.PositionYNameServerInfoInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(2, false, -1, ConfigProtection.PositionXOnlineInClient, ConfigProtection.PositionYOnlineInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(3, false, -1, ConfigProtection.PositionXServerTimeInClient, ConfigProtection.PositionYServerTimeInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(14, false, -1, ConfigProtection.PositionXPingInClient, ConfigProtection.PositionYPingInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(15, false, -1, ConfigProtection.PositionXRealTimeInClient, ConfigProtection.PositionYRealTimeInClient, 0xFF00FF00, ""));
		}
		return;
	}

	public static void scheduleSendPacketToClient(long time, final L2PcInstance client)
	{
		if (time <= 0)
		{
			OffMessage(client);
			return;
		}

		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			@Override
			public void run()
			{
				sendToClient(client);
			}
		}, time);
	}

	public static void sendGGReply(L2GameClient client)
	{
		if (client != null && client.getActiveChar() != null)
		{
			client.sendPacket(new GameGuardQuery());
			if (ConfigProtection.ALLOW_SEND_GG_REPLY)
				scheduleSendGG(ConfigProtection.TIME_SEND_GG_REPLY * 1000, client);
		}
	}

	public static void scheduleSendGG(long time, final L2GameClient client)
	{
		if (time <= 0)
			return;

		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			public void run()
			{
				if (client != null && client.getActiveChar() != null && !client.isGameGuardOk())
				{
					_log.info("Client " + client + " failed to reply GameGuard query and is being kicked!");
					client.closeNow();
				}
				if (HWIDBan.getInstance().checkFullHWIDBanned(client))
				{
					_log.info("Client " + client + " is banned. Kicked! |HWID: " + client.getHWID() + " IP: " + client.getIpAddr());
					client.close(ServerClose.STATIC_PACKET);
				}
				sendGGReply(client);
			}
		}, time);
	}
}
package com.l2jfrozen.protection.bin;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.network.serverpackets.GameGuardQuery;

public class GGSender extends GameGuardQuery
{
	public void sendServerId()
	{
		writeD(Config.SERVER_ID);
	}
}

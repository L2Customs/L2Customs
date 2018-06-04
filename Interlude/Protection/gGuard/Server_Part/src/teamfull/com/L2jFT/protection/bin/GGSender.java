package com.L2jFT.protection.bin;

import com.L2jFT.Config;
import com.L2jFT.Game.network.serverpackets.GameGuardQuery;

public class GGSender extends GameGuardQuery {
	public void sendServerId() {
		writeD(Config.SERVER_ID);
	}
}

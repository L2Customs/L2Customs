package DAGuard.network.serverpackets;

import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;

public final class GameGuardQuery extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x74);
		writeD(0x27533DD9);
		writeD(0x2E72A51D);
		writeD(0x2017038B);
		writeD(0xC35B1EA3);
	}

	@Override
	public String getType()
	{
		return "[S] 74 GameGuardQuery";
	}
}
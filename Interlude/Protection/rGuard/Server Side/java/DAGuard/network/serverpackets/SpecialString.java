package DAGuard.network.serverpackets;

import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;

public final class SpecialString extends L2GameServerPacket
{
	private int _strId, _fontSize, _x, _y, _color;
	private boolean _isDraw;
	private String _text;

	public SpecialString(int strId, boolean isDraw, int fontSize, int x, int y, int color, String text)
	{
		_strId = strId;
		_isDraw = isDraw;
		_fontSize = fontSize;
		_x = x;
		_y = y;
		_color = color;
		_text = text;
	}

	protected final void writeImpl()
	{
		writeC(0xB0);			// packet ID
		writeC(_strId);			// string ID
		writeC(_isDraw ? 1 : 0);// 1 - draw / 0 - hide
		writeC(_fontSize);		// -1 to 3 (font size)
		writeD(_x);     		// ClientRight - x
		writeD(_y);				// ClientTop + y
		writeD(_color);			// AARRGGBB
		writeS(_text);			// wide string max len = 63
	}

	@Override
	public String getType()
	{
		return "[S] B0 SpecialString";
	}
}
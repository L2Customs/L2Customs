package DAGuard.utils;

public class Util
{
	public static byte[] asByteArray(String hex)
	{
		byte[] buf = new byte[hex.length() / 2];

		for (int i = 0; i < hex.length(); i += 2)
		{
			int j = Integer.parseInt(hex.substring(i, i + 2), 16);
			buf[(i / 2)] = (byte) (j & 0xFF);
		}
		return buf;
	}

	public static String asHwidString(String hex)
	{
		byte[] buf = asByteArray(hex);
		return asHex(buf);
	}

	public static final String asHex(byte[] raw, int offset, int size)
	{
		StringBuffer strbuf = new StringBuffer(raw.length * 2);

		for (int i = 0; i < size; i++)
		{
			if ((raw[(offset + i)] & 0xFF) < 16)
			{
				strbuf.append("0");
			}
			strbuf.append(Long.toString(raw[(offset + i)] & 0xFF, 16));
		}

		return strbuf.toString();
	}

	public static final String asHex(byte[] raw)
	{
		return asHex(raw, 0, raw.length);
	}
}
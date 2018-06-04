package DAGuard;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import DAGuard.crypt.FirstKey;

public class ConfigProtection
{
	private static final Logger _log = Logger.getLogger(ConfigProtection.class.getName());
	public static final String D_GUARD_FILE = "./config/DAGuard.ini";
	public static final boolean PROTECT_DEBUG = false;
	public static byte[] GUARD_CLIENT_CRYPT_KEY;
	public static byte[] GUARD_CLIENT_CRYPT;
	public static int PROTECT_WINDOWS_COUNT;
	public static byte[] GUARD_SERVER_CRYPT_KEY;
	public static byte[] GUARD_SERVER_CRYPT;
	public static boolean GUARD_USE_DEFAULT_ENCODER;
	public static boolean ALLOW_GUARD_SYSTEM;
	public static boolean SHOW_PROTECTION_INFO_IN_CLIENT;
	public static boolean SHOW_NAME_SERVER_IN_CLIENT;
	public static boolean SHOW_ONLINE_IN_CLIENT;
	public static boolean SHOW_OFFTRADE_IN_CLIENT;
	public static boolean SHOW_SERVER_TIME_IN_CLIENT;
	public static boolean SHOW_REAL_TIME_IN_CLIENT;
	public static boolean SHOW_PING_IN_CLIENT;
	public static long TIME_REFRESH_SPECIAL_STRING;
	public static int PositionXProtectionInfoInClient;
	public static int PositionYProtectionInfoInClient;
	public static String NameServerInfoInClient;
	public static int PositionXNameServerInfoInClient;
	public static int PositionYNameServerInfoInClient;
	public static int PositionXOnlineInClient;
	public static int PositionYOnlineInClient;
	public static int PositionXOfftradeInClient;
	public static int PositionYOfftradeInClient;
	public static int PositionXServerTimeInClient;
	public static int PositionYServerTimeInClient;
	public static int PositionXRealTimeInClient;
	public static int PositionYRealTimeInClient;
	public static int PositionXPingInClient;
	public static int PositionYPingInClient;
	public static int ColorProtectionInfoInClient;
	public static int ColorNameServerInfoInClient;
	public static int ColorOnlineInClient;
	public static int ColorOfflineInClient;
	public static int ColorServerTimeInClient;
	public static int ColorRealTimeInClient;
	public static int ColorPingInClient;
	public static int GET_CLIENT_HWID;
	public static boolean ALLOW_SEND_GG_REPLY;
	public static long TIME_SEND_GG_REPLY;
	public static boolean PROTECT_KICK_WITH_EMPTY_HWID;
	public static boolean PROTECT_KICK_WITH_LASTERROR_HWID;
	public static boolean PROTECT_ENABLE_HWID_LOCK;

	public static final void load()
	{
		File fp = new File("./config/DAGuard.ini");
		ALLOW_GUARD_SYSTEM = fp.exists();
		if (ALLOW_GUARD_SYSTEM)
			try
			{
				Properties guardSettings = new Properties();
				InputStream is = new FileInputStream(fp);
				guardSettings.load(is);
				is.close();

				_log.info("==========[ Loading DAGuard configuration ]==========");

				ALLOW_GUARD_SYSTEM = getBooleanProperty(guardSettings, "AllowGuardSystem", true);

				PROTECT_WINDOWS_COUNT = getIntProperty(guardSettings, "AllowedWindowsCount", 99);
				GUARD_USE_DEFAULT_ENCODER = getBooleanProperty(guardSettings, "UseDefaultEncoder", false);
				SHOW_PROTECTION_INFO_IN_CLIENT = getBooleanProperty(guardSettings, "ShowProtectionInfoInClient", false);
				SHOW_NAME_SERVER_IN_CLIENT = getBooleanProperty(guardSettings, "ShowNameServerInfoInClient", false);
				SHOW_ONLINE_IN_CLIENT = getBooleanProperty(guardSettings, "ShowOnlineInClient", false);
				SHOW_OFFTRADE_IN_CLIENT = getBooleanProperty(guardSettings, "ShowOfftradeInClient", false);
				SHOW_SERVER_TIME_IN_CLIENT = getBooleanProperty(guardSettings, "ShowServerTimeInClient", false);
				SHOW_REAL_TIME_IN_CLIENT = getBooleanProperty(guardSettings, "ShowRealTimeInClient", false);
				SHOW_PING_IN_CLIENT = getBooleanProperty(guardSettings, "ShowPingInClient", false);
				TIME_REFRESH_SPECIAL_STRING = getLongProperty(guardSettings, "TimeRefreshStringToClient", 1000L);
				NameServerInfoInClient = getProperty(guardSettings, "NameServerInfoInClient", "Test");
				PositionXProtectionInfoInClient = getIntProperty(guardSettings, "PositionXProtectionInfoInClient", 320);
				PositionYProtectionInfoInClient = getIntProperty(guardSettings, "PositionYProtectionInfoInClient", 10);
				PositionXNameServerInfoInClient = getIntProperty(guardSettings, "PositionXNameServerInfoInClient", 320);
				PositionYNameServerInfoInClient = getIntProperty(guardSettings, "PositionYNameServerInfoInClient", 25);
				PositionXOnlineInClient = getIntProperty(guardSettings, "PositionXOnlineInClient", 320);
				PositionYOnlineInClient = getIntProperty(guardSettings, "PositionYOnlineInClient", 40);
				PositionXOfftradeInClient = getIntProperty(guardSettings, "PositionXOfftradeInClient", 320);
				PositionYOfftradeInClient = getIntProperty(guardSettings, "PositionYOfftradeInClient", 55);
				PositionXServerTimeInClient = getIntProperty(guardSettings, "PositionXServerTimeInClient", 320);
				PositionYServerTimeInClient = getIntProperty(guardSettings, "PositionYServerTimeInClient", 70);
				PositionXRealTimeInClient = getIntProperty(guardSettings, "PositionXRealTimeInClient", 320);
				PositionYRealTimeInClient = getIntProperty(guardSettings, "PositionYRealTimeInClient", 85);
				PositionXPingInClient = getIntProperty(guardSettings, "PositionXPingInClient", 320);
				PositionYPingInClient = getIntProperty(guardSettings, "PositionYPingInClient", 100);
				ColorProtectionInfoInClient = getIntHexProperty(guardSettings, "ColorProtectionInfoInClient", -16711936);
				ColorNameServerInfoInClient = getIntHexProperty(guardSettings, "ColorNameServerInfoInClient", -16711936);
				ColorOnlineInClient = getIntHexProperty(guardSettings, "ColorOnlineInClient", -16711936);
				ColorOfflineInClient = getIntHexProperty(guardSettings, "ColorOfftradeInClient", -16711936);
				ColorServerTimeInClient = getIntHexProperty(guardSettings, "ColorServerTimeInClient", -16711936);
				ColorRealTimeInClient = getIntHexProperty(guardSettings, "ColorRealTimeInClient", -16711936);
				ColorPingInClient = getIntHexProperty(guardSettings, "ColorPingInClient", -16711936);
				GET_CLIENT_HWID = getIntProperty(guardSettings, "UseClientHWID", 2);
				ALLOW_SEND_GG_REPLY = getBooleanProperty(guardSettings, "AllowSendGGReply", false);
				TIME_SEND_GG_REPLY = getLongProperty(guardSettings, "TimeSendGGReply", 10000L);

				PROTECT_KICK_WITH_EMPTY_HWID = getBooleanProperty(guardSettings, "KickWithEmptyHWID", true);
				PROTECT_KICK_WITH_LASTERROR_HWID = getBooleanProperty(guardSettings, "KickWithLastErrorHWID", false);
				PROTECT_ENABLE_HWID_LOCK = getBooleanProperty(guardSettings, "EnableHWIDLock", false);

				String key_client = "GOGX2_RB(]Slnjt15~EgyqTv%[$YR]!1E~ayK?$9[R%%m4{zoMF$D?f:zvS2q&>~";
				String key_server = "b*qR43<9J1pD>Q4Uns6FsKao~VbU0H]y`A0ytTveiWn)SuSYsM?m*eblL!pwza!t";
				byte[] keyS = key_server.getBytes();
				byte[] tmpS = new byte[32];

				byte[] keyC = key_client.getBytes();
				byte[] tmpC = new byte[32];

				System.arraycopy(keyC, 0, tmpC, 0, 32);
				GUARD_CLIENT_CRYPT_KEY = FirstKey.expandKey(tmpC, 32);
				System.arraycopy(keyC, 32, tmpC, 0, 32);
				GUARD_CLIENT_CRYPT = FirstKey.expandKey(tmpC, 32);

				System.arraycopy(keyS, 0, tmpS, 0, 32);
				GUARD_SERVER_CRYPT_KEY = FirstKey.expandKey(tmpS, 32);
				System.arraycopy(keyS, 32, tmpS, 0, 32);
				GUARD_SERVER_CRYPT = FirstKey.expandKey(tmpS, 32);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	protected static Properties getSettings(String CONFIGURATION_FILE) throws Exception
	{
		Properties serverSettings = new Properties();
		InputStream is = new FileInputStream(new File(CONFIGURATION_FILE));
		serverSettings.load(is);
		is.close();
		return serverSettings;
	}

	protected static String getProperty(Properties prop, String name)
	{
		return prop.getProperty(name.trim(), null);
	}

	protected static String getProperty(Properties prop, String name, String _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : s;
	}

	protected static int getIntProperty(Properties prop, String name, int _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Integer.parseInt(s.trim());
	}

	protected static int getIntHexProperty(Properties prop, String name, int _default)
	{
		return (int) getLongHexProperty(prop, name, _default);
	}

	protected static long getLongProperty(Properties prop, String name, long _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Long.parseLong(s.trim());
	}

	protected static long getLongHexProperty(Properties prop, String name, long _default)
	{
		String s = getProperty(prop, name);
		if (s == null)
			return _default;
		s = s.trim();
		if (!s.startsWith("0x"))
			s = "0x" + s;
		return Long.decode(s).longValue();
	}

	protected static byte getByteProperty(Properties prop, String name, byte _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Byte.parseByte(s.trim());
	}

	protected static byte getByteProperty(Properties prop, String name, int _default)
	{
		return getByteProperty(prop, name, (byte) _default);
	}

	protected static boolean getBooleanProperty(Properties prop, String name, boolean _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Boolean.parseBoolean(s.trim());
	}

	protected static float getFloatProperty(Properties prop, String name, float _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Float.parseFloat(s.trim());
	}

	protected static float getFloatProperty(Properties prop, String name, double _default)
	{
		return getFloatProperty(prop, name, (float) _default);
	}

	protected static double getDoubleProperty(Properties prop, String name, double _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : Double.parseDouble(s.trim());
	}

	protected static int[] getIntArray(Properties prop, String name, int[] _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : parseCommaSeparatedIntegerArray(s.trim());
	}

	protected static float[] getFloatArray(Properties prop, String name, float[] _default)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : parseCommaSeparatedFloatArray(s.trim());
	}

	protected static String[] getStringArray(Properties prop, String name, String[] _default, String delimiter)
	{
		String s = getProperty(prop, name);
		return s == null ? _default : s.split(delimiter);
	}

	protected static String[] getStringArray(Properties prop, String name, String[] _default)
	{
		return getStringArray(prop, name, _default, ",");
	}

	protected static float[] parseCommaSeparatedFloatArray(String s)
	{
		if (s.isEmpty())
			return new float[0];
		String[] tmp = s.replaceAll(",", ";").split(";");
		float[] ret = new float[tmp.length];
		for (int i = 0; i < tmp.length; i++)
			ret[i] = Float.parseFloat(tmp[i]);
		return ret;
	}

	protected static int[] parseCommaSeparatedIntegerArray(String s)
	{
		if (s.isEmpty())
			return new int[0];
		String[] tmp = s.replaceAll(",", ";").split(";");
		int[] ret = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++)
			ret[i] = Integer.parseInt(tmp[i]);
		return ret;
	}
}
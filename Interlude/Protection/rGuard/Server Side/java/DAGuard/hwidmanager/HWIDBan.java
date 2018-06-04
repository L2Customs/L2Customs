package DAGuard.hwidmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.network.L2GameClient;

public class HWIDBan
{
	private static final Logger _log = Logger.getLogger(HWIDBan.class.getName());
	private static HWIDBan _instance;

	private static Map<Integer, HWIDBanList> _lists;

	public static HWIDBan getInstance()
	{
		if (_instance == null)
		{
			_instance = new HWIDBan();
		}
		return _instance;
	}

	public static void reload()
	{
		_instance = new HWIDBan();
	}

	public HWIDBan()
	{
		_lists = new HashMap<Integer, HWIDBanList>();
		load();
	}

	private void load()
	{
		String HWID = "";
		int counterHWIDBan = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hwid_bans");
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				HWID = rset.getString("HWID");
				HWIDBanList hb = new HWIDBanList(counterHWIDBan);
				hb.setHWIDBan(HWID);
				_lists.put(counterHWIDBan, hb);
				counterHWIDBan++;
			}
			statement.close();
			rset.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Cant select hwid bans." + e);
		}
	}

	public boolean checkFullHWIDBanned(L2GameClient client)
	{
		if (_lists.size() == 0)
		{
			return false;
		}
		for (int i = 0; i < _lists.size(); i++)
		{
			if (_lists.get(i).getHWID().equals(client.getHWID()))
			{
				return true;
			}
		}
		return false;
	}

	public static int getCountHWIDBan()
	{
		return _lists.size();
	}

	public static void addHWIDBan(L2GameClient client)
	{
		String HWID = client.getHWID();
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO hwid_bans SET HWID=?");
			statement.setString(1, HWID);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Cant insert hwid ban." + HWID);
		}
	}
}
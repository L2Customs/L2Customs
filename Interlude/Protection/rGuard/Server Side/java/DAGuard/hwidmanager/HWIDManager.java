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

public class HWIDManager
{
	private static final Logger _log = Logger.getLogger(HWIDManager.class.getName());

	private static HWIDManager _instance;
	static Map<Integer, HWIDInfoList> _listHWID;

	public static HWIDManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new HWIDManager();
		}
		return _instance;
	}

	public HWIDManager()
	{
		_listHWID = new HashMap<Integer, HWIDInfoList>();
		load();
		_log.log(Level.INFO, "Loaded " + _listHWID.size() + " HWIDs.");
	}

	public static void reload()
	{
		_instance = new HWIDManager();
	}

	private void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hwid_info");
			ResultSet rset = statement.executeQuery();
			int counterHWIDInfo = 0;
			while (rset.next())
			{
				final HWIDInfoList hInfo = new HWIDInfoList(counterHWIDInfo);
				hInfo.setHwids(rset.getString("HWID"));
				hInfo.setLogin(rset.getString("Account"));
				_listHWID.put(counterHWIDInfo, hInfo);
				counterHWIDInfo++;
			}
		}
		catch (final Exception e)
		{
			_log.log(Level.WARNING, "Cant select hwid_info." + e);
		}
	}

	public static void updateHWIDInfo(L2GameClient client)
	{
		int counterHWIDInfo = _listHWID.size();
		boolean isFound = false;
		for (int i = 0; i < _listHWID.size(); i++)
		{
			if (_listHWID.get(i).getHWID().equals(client.getHWID()))
			{
				isFound = true;
				counterHWIDInfo = i;
				break;
			}
		}
		final HWIDInfoList hInfo = new HWIDInfoList(counterHWIDInfo);
		hInfo.setHwids(client.getHWID());
		hInfo.setLogin(client.getAccountName());
		_listHWID.put(counterHWIDInfo, hInfo);
		PreparedStatement statement;	
		if (isFound)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				statement = con.prepareStatement("UPDATE hwid_info SET Account=? WHERE HWID=?");
				statement.setString(1, client.getAccountName());
				statement.setString(2, client.getHWID());
				statement.execute();
				statement.close();
			}
			catch (final Exception e)
			{
				_log.log(Level.WARNING, "Could not update hwid_info.HWID:" + client.getHWID() + " for account " + client.getAccountName() + ". " + e);
			}
		}
		else
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				statement = con.prepareStatement("INSERT INTO hwid_info (HWID, Account) values (?,?)");
				statement.setString(1, client.getHWID());
				statement.setString(1, client.getAccountName());
				statement.execute();
				statement.close();
			}
			catch (final Exception e)
			{
				_log.log(Level.WARNING, "Could not insert into hwid_info.HWID:" + client.getHWID() + " for account " + client.getAccountName() + ". " + e);
			}
		}
	}
}
package DAGuard.hwidmanager;

import DAGuard.ConfigProtection;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class HWIDAdminBan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hwid_ban"
	};

	@Override
	public boolean useAdminCommand(String fullString, L2PcInstance player)
	{

		if (!ConfigProtection.ALLOW_GUARD_SYSTEM)
			return false;
		if (player == null)
			return false;
		if (!fullString.startsWith("admin_hwid"))
			return false;
		if (fullString.startsWith("admin_hwid_ban"))
		{
			L2Object playerTarger = player.getTarget();
			if (playerTarger == null && !(playerTarger instanceof L2PcInstance))
			{
				player.sendMessage("Target is empty");
				return false;
			}
			L2PcInstance target = (L2PcInstance) playerTarger;
			if (target != null)
			{
				HWIDBan.addHWIDBan(target.getNetConnection());
				player.sendMessage(target.getName() + " banned in HWID.");
			}
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
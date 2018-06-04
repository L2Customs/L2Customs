package DAGuard.crypt;

import DAGuard.ConfigProtection;
import DAGuard.crypt.impl.L2Client;
import DAGuard.crypt.impl.L2Server;
import DAGuard.crypt.impl.VMPC;

public class GameCrypt
{
	private ProtectionCrypt _client;
	private ProtectionCrypt _server;
	private boolean _isEnabled = false;
	private boolean _isProtected = false;

	public void setProtected(boolean state)
	{
		_isProtected = state;
	}

	public void setKey(byte[] key)
	{
		if (_isProtected)
		{
			_client = new VMPC();
			_client.setup(key, ConfigProtection.GUARD_CLIENT_CRYPT);
			_server = new L2Server();
			_server.setup(key, null);
			_server = new VMPC();
			_server.setup(key, ConfigProtection.GUARD_SERVER_CRYPT);
		}
		else
		{
			_client = new L2Client();
			_client.setup(key, null);
			_server = new L2Server();
			_server.setup(key, null);
		}
	}

	public void decrypt(byte[] raw, int offset, int size)
	{
		if (_isEnabled)
			_client.crypt(raw, offset, size);
	}

	public void encrypt(byte[] raw, int offset, int size)
	{
		if (_isEnabled)
			_server.crypt(raw, offset, size);
		else
			_isEnabled = true;
	}
}
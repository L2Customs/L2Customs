package com.l2jfrozen.protection;

import com.l2jfrozen.L2Properties;

public class ProtectConfig {
	public static boolean PROTECTION_ENABLED;
	public static int NPROTECT_KEY=-1;
	public static String NPROTECT_USERNAME;
	public static int POLL_INTERVAL;
	public static boolean ANNOUNCE_HACK_ATTEMPT;
	public static boolean ANNOUNCE_NON_PROTECT;
	public static String  ON_HACK_ATTEMPT;
	public static boolean LOG_INVALID_LOGONS;
	public static int 	  SESSION_FROM_SAME_PC;
	public static boolean ALLOW_TWINK_ON_OLYMPIAD;
	public static boolean ALLOW_TWINK_ON_SIEGE;
	public static String  CUSTOM_RESTRICTOR; 
	public static void load() {
		try {
			L2Properties p = new L2Properties("Config/Server/Protected/GuardSystem.ini");
			PROTECTION_ENABLED = Boolean.parseBoolean(p.getProperty("Protect_Enabled","true"));
			NPROTECT_USERNAME = p.getProperty("ClientName","");
			ANNOUNCE_NON_PROTECT = Boolean.parseBoolean(p.getProperty("AnnonceNonProtect","true"));
			POLL_INTERVAL = Integer.parseInt(p.getProperty("Reply_Interval","120"));
			LOG_INVALID_LOGONS = Boolean.parseBoolean(p.getProperty("Log_invalid_key","false"));
			SESSION_FROM_SAME_PC = Integer.parseInt(p.getProperty("Windows_Count","-1"));
			ANNOUNCE_HACK_ATTEMPT = Boolean.parseBoolean(p.getProperty("AnnounceHackAttempt","true"));
			ON_HACK_ATTEMPT = p.getProperty("OnHackAttempt","kick");
			ALLOW_TWINK_ON_OLYMPIAD = Boolean.parseBoolean(p.getProperty("AllowTwinkOnOlympiad","false"));
			ALLOW_TWINK_ON_SIEGE = Boolean.parseBoolean(p.getProperty("AllowTwinkOnSiege","true"));
			CUSTOM_RESTRICTOR = p.getProperty("CustomRestrictor","");
		} catch(Exception e) {
			System.out.println("gGuard Protection: Error reading Config/Server/Protected/GuardSystem.ini");
			e.printStackTrace();
		}
	}
}

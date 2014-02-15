package de.mrpixeldream.dreamcode.im.server.db;

public class QueryLib {
	
	public static final String GET_USER_PW = "SELECT password FROM users WHERE name = '?';";
	public static final String SET_USER_LOGIN_STATUS = "UPDATE users SET online = '?' WHERE name = '2';";
	public static final String GET_USER_ADMIN = "SELECT admin FROM users WHERE name = '?';";
	public static final String GET_USER_ONLINE = "SELECT online FROM users WHERE name = '?';";
	public static final String SET_FRIENDS = "UPDATE friendlist SET friends = '?' WHERE name = '?';";
	
	private QueryLib()
	{
		// Not allowed to create instances
	}

}

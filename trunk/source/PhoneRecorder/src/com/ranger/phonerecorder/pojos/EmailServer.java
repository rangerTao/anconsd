package com.ranger.phonerecorder.pojos;

public class EmailServer {

	public boolean isValid(){
		if(!sendserver.equals("") && !(port+"").equals("") && !username.equals("") && !password.equals(""))
			return true;
		else
			return false;
	}
	
	public void save(){
		
	}
	
	public String sendserver;
	public int port;
	public String username;
	public String password;

	/**
	 * @return the server
	 */
	public String getServer() {
		return sendserver;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(String server) {
		this.sendserver = server;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return password;
	}

	/**
	 * @param pass
	 *            the pass to set
	 */
	public void setPass(String pass) {
		this.password = pass;
	}

}

package io.xeros.content.help;

import java.util.Date;

import io.xeros.Server;

/**
 * @author Jason MacKeigan
 * @date Nov 4, 2014, 7:40:49 PM
 */
public class HelpRequest {
	/**
	 * The name of the player requesting help
	 */
	private final String name;

	/**
	 * The IP the request came from
	 */
	private final String protocol;

	/**
	 * The message of the request
	 */
	private final String message;

	/**
	 * The state of the request from the player
	 */
	private HelpRequestState state = HelpRequestState.OPEN;

	/**
	 * The date in the format 'yyyy/MM/dd HH:mm:ss'
	 */
	private final Date date = Server.getCalendar().getInstance().getTime();

	/**
	 * A new help request
	 * 
	 * @param name the name of the player
	 * @param message the message from the player
	 */
	public HelpRequest(String name, String protocol, String message) {
		this.name = name;
		this.protocol = protocol;
		this.message = message;
	}

	/**
	 * Returns the name of the player requesting help
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the internet protocol address of player that made the request
	 * 
	 * @return the internet protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Returns the message associated with the request
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the state of the message
	 * 
	 * @return the state
	 */
	public HelpRequestState getState() {
		return state;
	}

	/**
	 * Sets the state of the request to that of the object passed
	 * 
	 * @param state the state of the request
	 */
	public void setHelpRequestState(HelpRequestState state) {
		this.state = state;
	}

	/**
	 * Returns a string representation of the date and time the request was made
	 * 
	 * @return
	 */
	public Date getDate() {
		return date;
	}

}

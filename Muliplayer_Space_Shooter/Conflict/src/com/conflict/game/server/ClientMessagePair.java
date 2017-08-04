package com.conflict.game.server;

import com.conflict.game.ClientMessages.ClientMessage;

public class ClientMessagePair {
	public ClientMessage clientMessage;
	public PlayerData connection;
	
	public ClientMessagePair(ClientMessage clientMessage, PlayerData connection)
	{
		this.clientMessage = clientMessage;
		this.connection = connection;
	}
}

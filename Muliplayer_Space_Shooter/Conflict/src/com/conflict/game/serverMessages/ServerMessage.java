package com.conflict.game.serverMessages;


import com.conflict.game.client.ClientNetworkManager;
import com.conflict.game.serialisation.Serializable;

public interface ServerMessage extends Serializable{

	public void preform(ClientNetworkManager networkManager);
}

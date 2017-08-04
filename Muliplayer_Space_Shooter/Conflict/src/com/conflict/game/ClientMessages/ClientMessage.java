package com.conflict.game.ClientMessages;

import com.conflict.game.serialisation.Serializable;
import com.conflict.game.server.PlayerData;
import com.conflict.game.server.ServerNetworkManager;

public interface ClientMessage extends Serializable{

	public void preform(ServerNetworkManager networkManager, PlayerData connection);

}

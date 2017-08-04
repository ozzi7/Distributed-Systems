package com.conflict.game.ClientMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.conflict.game.server.PlayerData;
import com.conflict.game.server.ServerNetworkManager;

public class InputUpdateMessage implements ClientMessage{
	
	boolean boostOn;
	boolean action1;
	float destDir;
	
	public InputUpdateMessage()
	{
		
	}
	
	public InputUpdateMessage(boolean boostOn, float destDir, boolean action1)
	{
		this.boostOn = boostOn;
		this.destDir = destDir;
		this.action1 = action1;
	}
	
	@Override
	public void preform(ServerNetworkManager networkManager, PlayerData connection) 
	{
		connection.ship.action1 = action1;
		connection.ship.boostOn = boostOn;
		connection.ship.destDir = destDir;
	}

	@Override
	public void storeTo(DataOutputStream out) throws IOException {
		out.writeBoolean(action1);
		out.writeBoolean(boostOn);
		out.writeFloat(destDir);
	}

	@Override
	public void restoreFrom(DataInputStream in) throws IOException {
		action1 = in.readBoolean();
		boostOn = in.readBoolean();
		destDir = in.readFloat();
	}

	public static short serialID = -1;
	@Override
	public short getId() {
		return serialID;
	}
	
	@Override
	public String toString() {
		return "("+boostOn+", "+destDir+")";
	}
}

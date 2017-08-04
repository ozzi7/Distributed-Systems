package com.conflict.game.serverMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.conflict.game.GameObject;
import com.conflict.game.Ship;
import com.conflict.game.ClientMessages.InputUpdateMessage;
import com.conflict.game.client.ClientNetworkManager;
import com.conflict.game.serialisation.Serializer;


public class UpdateObjectMessage implements ServerMessage {

	int size;
	int playerShipNum;
	ArrayList<GameObject> objects;
	
	public UpdateObjectMessage()
	{
		
	}
	
	public UpdateObjectMessage(ArrayList<GameObject> objects, Ship playerShip, int size)
	{
		this.objects = objects;
		this.size = size;
		this.playerShipNum = objects.indexOf(playerShip);
	}
	
	@Override
	public void preform(ClientNetworkManager networkManager) {
		
		// update data
		networkManager.objects = objects;
		
		if (playerShipNum == -1)
			networkManager.playerShip = null;
		else
			networkManager.playerShip = (Ship) networkManager.objects.get(playerShipNum);
		
		networkManager.size = size;
		
		Gdx.app.log("Game", "State updated...");
		
		long t = System.currentTimeMillis();
		long d = t-networkManager.lastUpdateTime;
		networkManager.estimatedDeltaPerMillisec = (float) (((3.0f*networkManager.estimatedDeltaPerMillisec)+(1.0/(float)d))/4.0f);
		networkManager.lastUpdateTime = t;
		
		float newAngle = networkManager.playerShip.angle;
		if (networkManager.renderer.isViewDirectionSet())
			newAngle = networkManager.renderer.getViewDirection();
		
		networkManager.sendData(new InputUpdateMessage(networkManager.renderer.isBoostOn(), newAngle, networkManager.renderer.isAction1On()));
		networkManager.renderer.resetInput();
		
	}

	public static short serialID = -1;
	@Override
	public short getId() {
		return serialID;
	}

	@Override
	public void storeTo(DataOutputStream out) throws IOException {
		out.writeInt(size);
		out.writeInt(playerShipNum);
		out.writeInt(objects.size());
		for (GameObject o : objects)
			Serializer.writeObjectTo(o, out);
	}

	@Override
	public void restoreFrom(DataInputStream in) throws IOException {
		size = in.readInt();
		playerShipNum = in.readInt();
		int l = in.readInt();
		objects = new ArrayList<GameObject>(l);
		for (int i=0; i<l;i++)
		{
			GameObject go = (GameObject)Serializer.readObjectFrom(in);
			objects.add(go);
		}
	}

}

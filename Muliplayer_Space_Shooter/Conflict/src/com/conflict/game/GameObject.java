package com.conflict.game;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.conflict.Helper;
import com.conflict.game.serialisation.Serializable;
import com.conflict.game.server.ServerNetworkManager;

public abstract class GameObject implements Serializable{
	
	public Vector2 position = new Vector2(0,0);
	public Vector2 velocity = new Vector2(0,0);
	public float angle = 0;
	public float angularVelocity = 0;
	
	public int index = -1;

	public final void updateIndex(ServerNetworkManager networkManager)
	{
		int newIndex = ((int)position.x)+((int)position.y)*networkManager.size;
		if (newIndex != index)
		{
			networkManager.removeObject(this);
			index = newIndex;
			networkManager.addObject(this);
		}
	}
	
	public void updateMovement(ServerNetworkManager networkManager, float delta)
	{
		int oldPosX = (int) this.position.x;
		int oldPosY = (int) this.position.y;
		
		angle = Helper.addRotation(angle, this.angularVelocity*delta);
		this.position.x += this.velocity.x*delta;
		this.position.y += this.velocity.y*delta;
		
		if (position.x < 0.0f)
		{
			position.x = 0.0f;
			velocity.x = 0.0f;
		}
			
		else if (position.x >= networkManager.size)
		{
			position.x = networkManager.size-0.0001f;
			velocity.x = 0.0f;
		}
		
		if (position.y < 0.0f)
		{
			position.y = 0.0f;
			velocity.y = 0.0f;
		}
		else if (position.y >= networkManager.size)
		{
			position.y = networkManager.size-0.0001f;
			velocity.y = 0.0f;
		}
		
		if (oldPosX != (int)position.x || oldPosY != (int)position.y)
			networkManager.fieldUpdateList.add(this);
	}
	
	public abstract void update(ServerNetworkManager networkManager, float delta);
	
	public abstract void render(GameRenderer renderer, float delta);
	
	
	@Override
	public void storeTo(DataOutputStream out) throws IOException {
		out.writeFloat(position.x);
		out.writeFloat(position.y);
		out.writeFloat(velocity.x);
		out.writeFloat(velocity.y);
		out.writeFloat(angle);
		out.writeFloat(angularVelocity);
	}
	
	@Override
	public void restoreFrom(DataInputStream in) throws IOException {
		position.x = in.readFloat();
		position.y = in.readFloat();
		velocity.x = in.readFloat();
		velocity.y = in.readFloat();
		angle = in.readFloat();
		angularVelocity = in.readFloat();
	}
	
	public static short serialID;
	@Override
	public short getId() {
		return serialID;
	}
	
	
}

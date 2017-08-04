package com.conflict.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.conflict.game.server.ServerNetworkManager;

public class Shot1 extends GameObject{

	public static final float size = 0.05f;
	public static final float speed = 0.4f;
	public static final long livetime = 1500;
	
	public long deathTime = -1; 
	
	public Shot1() {}
	
	public Shot1(float x, float y, float dir)
	{
		
		this.deathTime = System.currentTimeMillis()+livetime;
		position.x = x;
		position.y = y;
		Vector2 dirV = new Vector2(speed, 0);
		dirV.rotate(dir);
		velocity = dirV;
		angularVelocity = 0;
		angle = dir;
	}
	
	@Override
	public void update(ServerNetworkManager networkManager, float delta) {
		// TODO Auto-generated method stub
		
		
		if (System.currentTimeMillis() > deathTime)
			networkManager.destroyList.add(this);
		
		ArrayList<GameObject> nearObjects = networkManager.getObjectsInside(position.x-size, position.y-size, position.x+size, position.y+size);
		if (nearObjects.size() > 1)
		{
			networkManager.destroyList.add(this);
		}
	}

	@Override
	public void render(GameRenderer renderer, float delta) {
		renderer.renderShot1(this, delta);
	}
	
	@Override
	public void storeTo(DataOutputStream out) throws IOException {
		out.writeLong(deathTime);
		super.storeTo(out);
	}
	
	@Override
	public void restoreFrom(DataInputStream in) throws IOException {
		deathTime = in.readLong();
		super.restoreFrom(in);
	}
	
	public static short serialID = -1;
	@Override
	public short getId() {
		return serialID;
	}

}

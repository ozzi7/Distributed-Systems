package com.conflict.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.conflict.Helper;
import com.conflict.game.server.ServerNetworkManager;

public class Ship extends GameObject{

	public static final float angularVelocityValue = 90f;
	public static final float boostAcceleration = 0.1f;
	public static final float friction = 0.6f;
	public static final float spawnDistance = 0.08f;
	public static final long action1BlockedTime = 750;

	public float destDir;
	
	public boolean boostOn;
	public boolean action1;
	
	public long action1Blocked = -1;
	
	public Ship()
	{
			
	}

	@Override
	public void update(ServerNetworkManager networkManager, float delta) {
		
		long t = System.currentTimeMillis();

		angularVelocity = Helper.relDir(angle, destDir);
		if (angularVelocity > angularVelocityValue)
			angularVelocity = angularVelocityValue;
		else if (angularVelocity < -angularVelocityValue)
			angularVelocity = -angularVelocityValue;
		
		if (boostOn)
		{
			Vector2 acceleration = new Vector2(boostAcceleration*delta,0);
			acceleration.rotate(angle);
			velocity.add(acceleration);
		}
		
		if (action1 && t>action1Blocked)
		{
			Vector2 relPos = new Vector2(spawnDistance,0);
			relPos.rotate(angle);
			
			Shot1 newShot = new Shot1(position.x+relPos.x, position.y+relPos.y, angle);
			newShot.index = networkManager.coordToIndex((int)newShot.position.x, (int)newShot.position.y);
			networkManager.addList.add(newShot);
			
			action1Blocked = t+action1BlockedTime;
		}
		
		float frictionDelta = friction*delta;
		if (frictionDelta > 1.0f)
			frictionDelta = 1.0f;
		
		velocity.scl(1.0f-frictionDelta);
		
	}

	@Override
	public void render(GameRenderer renderer, float delta) {
		renderer.renderShip(this, delta);
	}

	
	@Override
	public void storeTo(DataOutputStream out) throws IOException {
		out.writeFloat(destDir);
		out.writeBoolean(boostOn);
		out.writeBoolean(action1);
		super.storeTo(out);
	}
	
	@Override
	public void restoreFrom(DataInputStream in) throws IOException {
		destDir = in.readFloat();
		boostOn = in.readBoolean();
		action1 = in.readBoolean();
		super.restoreFrom(in);
	}
	
	public static short serialID = -1;
	@Override
	public short getId() {
		return serialID;
	}
}

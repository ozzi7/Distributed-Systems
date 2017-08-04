package com.conflict.game.server;


import java.net.SocketAddress;

import com.conflict.Helper;
import com.conflict.game.Ship;

public class PlayerData{
	ServerNetworkManager networkManager;
	
	public String name;
	public Ship ship;
	public SocketAddress address;
	public long lastMessageReceived;

	public PlayerData(SocketAddress address, ServerNetworkManager networkManager)
	{
		System.out.print("new Playerdata created...\n");
		
		this.networkManager = networkManager;
		
		this.address = address;
		lastMessageReceived = System.currentTimeMillis();
		ship = new Ship();
		ship.position.x = Helper.rnd.nextFloat()*(networkManager.size-1)+0.5f;
		ship.position.y = Helper.rnd.nextFloat()*(networkManager.size-1)+0.5f;
		ship.index = networkManager.coordToIndex((int)ship.position.x, (int)ship.position.y);
		networkManager.addObject(ship);
	}
}

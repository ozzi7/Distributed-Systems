package com.conflict.game.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import com.conflict.game.GameObject;
import com.conflict.game.ClientMessages.ClientMessage;
import com.conflict.game.serialisation.Serializer;
import com.conflict.game.serverMessages.UpdateObjectMessage;


public class ServerNetworkManager{

	public static final int USED_PORT = 4445;
	public static final short CONNECTION_ID = (short) 0xffff;
	public static final long CONNECTION_TIMEOUT = 5000;
	public static final int RECEIVER_BUFFER_SIZE = 10000;
	
	public HashMap<SocketAddress, PlayerData> players = new HashMap<SocketAddress, PlayerData>();
	
	public int size;
	public int sizeSquare;
	public ArrayList<LinkedList<GameObject>> objectPlacement;
	
	public ArrayList<GameObject> destroyList = new ArrayList<GameObject>();
	public ArrayList<GameObject> addList = new ArrayList<GameObject>();
	public ArrayList<GameObject> fieldUpdateList = new ArrayList<GameObject>();
	
	public DatagramSocket socket;
	
	public Receiver receiver;
	
	public ServerNetworkManager(int size)
	{
		this.size = size;
		try 
		{
			socket = new DatagramSocket(USED_PORT);
		} 
		catch (SocketException e) {
			e.printStackTrace();
		}
		sizeSquare = size*size;
		objectPlacement = new ArrayList<LinkedList<GameObject>>(sizeSquare);
		for (int i=0; i<sizeSquare; i++)
		{
			objectPlacement.add(new LinkedList<GameObject>());
		}
		
		receiver = new Receiver();
	}
	
	public void stopServer()
	{
		receiver.stop();
	}
	
	public int coordToIndex(int x, int y)
	{
		return (x+y*size);
	}	
	
	public void addObject(GameObject go)
	{
		System.out.print("adding GameObject\n");
		
		LinkedList<GameObject> gol = objectPlacement.get(go.index);
		gol.add(go);
	}
	
	public void removeObject(GameObject go)
	{
		System.out.print("removing GameObject\n");
		
		LinkedList<GameObject> gol = objectPlacement.get(go.index);
		gol.remove(go);
	}
	
	public void update(float delta)
	{
		
		System.out.print("updating... "+ players.size() +" players connected\n");
		
		// update world
		for (LinkedList<GameObject> gol : objectPlacement)
		{
			for (GameObject go : gol)
			{
				go.update(this, delta);
			}
		}
		
		for (GameObject go : destroyList)
		{
			removeObject(go);
		}
		destroyList.clear();
		
		for (GameObject go : addList)
		{
			addObject(go);
		}
		addList.clear();
		
		// send required Data to clients
		long time = System.currentTimeMillis();
		ArrayList<SocketAddress> toRemove = new ArrayList<SocketAddress>();
		for (PlayerData p : players.values())
		{
			if (time-p.lastMessageReceived > CONNECTION_TIMEOUT)
			{
				System.out.print("adding player to remove list\n");
				toRemove.add(p.address);
			}
			else
			{
				ArrayList<GameObject> gameObjects = getObjectsInside(p.ship.position.x-1.0f, p.ship.position.y-1.0f, p.ship.position.x+1.0f, p.ship.position.y+1.0f);
				ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
				DataOutputStream dos = new DataOutputStream(baos);
				try 
				{
					dos.writeShort(ServerNetworkManager.CONNECTION_ID);
					Serializer.writeObjectTo(new UpdateObjectMessage(gameObjects, p.ship, size), dos);
					byte[] data = baos.toByteArray();
					DatagramPacket packet;
					packet = new DatagramPacket(data, data.length, p.address);
					socket.send(packet);
				} 
				catch (SocketException e) 
				{
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		// remove old timeouted players
		for (SocketAddress sa : toRemove)
		{
			System.out.print("removing player at "+sa.toString()+"\n");
			
			PlayerData p = players.get(sa);
			if (p.ship != null)
				removeObject(p.ship);
			players.remove(sa);
		}
		
		
		// update movement
		for (LinkedList<GameObject> gol : objectPlacement)
		{
			for (GameObject go : gol)
			{
				go.updateMovement(this, delta);
			}
		}
		
		for (GameObject go : fieldUpdateList)
		{
			go.updateIndex(this);
		}
		fieldUpdateList.clear();
		
	}
	
	public ArrayList<GameObject> getObjectsInside(float x1, float y1, float x2, float y2)
	{
		int x1Int = (int)x1;
		int y1Int = (int)y1;
		int x2Int = (int)x2;
		int y2Int = (int)y2;
		if (x1Int < 0)
			x1Int = 0;
		if (y1Int < 0)
			y1Int = 0;
		if (x2Int >= size)
			x2Int = size-1;
		if (y2Int >= size)
			y2Int = size-1;
		
		ArrayList<GameObject> foundObjects = new ArrayList<GameObject>(32);
		
		for (int xt=x1Int; xt <= x2Int; xt++)
		{
			for (int yt=y1Int; yt <= y2Int; yt++)
			{
				LinkedList<GameObject> currentList = objectPlacement.get((xt+yt*size));
				for (GameObject goT : currentList)
				{
					if (goT.position.x >= x1 && goT.position.x < x2 && goT.position.y >= y1 && goT.position.y < y2)
					{
						foundObjects.add(goT);
					}
				}
			}
		}
		
		return foundObjects;
	}
	
	class Receiver implements Runnable
	{
		private Thread thread;
		private boolean running;
		
		private byte[] buffer;
		private DatagramPacket receivePacket;
		
		public Receiver()
		{
			buffer = new byte[RECEIVER_BUFFER_SIZE];
			receivePacket = new DatagramPacket(buffer, buffer.length);
			
			running = true;
			thread = new Thread(this);
			thread.start();
		}
		
		public void stop()
		{
			running = false;
			socket.close();
			try 
			{
				thread.join();
			} 
			catch (InterruptedException e) {}
		}
		
		@Override
		public void run() {

			try {
				while (running)
				{
					socket.receive(receivePacket);
					ByteArrayInputStream bais = new ByteArrayInputStream(buffer,0, receivePacket.getLength());
					DataInputStream dis = new DataInputStream(bais);
					short id = dis.readShort();
					if (id == CONNECTION_ID)
					{
						PlayerData player = players.get(receivePacket.getSocketAddress());
						if (player == null)
						{
							SocketAddress sa = receivePacket.getSocketAddress();
							player = new PlayerData(sa, ServerNetworkManager.this);
							players.put(receivePacket.getSocketAddress(), player);
						}
						else
						{
							player.lastMessageReceived = System.currentTimeMillis();
						}
						
						while (bais.available() > 0)
						{
							ClientMessage m = (ClientMessage)Serializer.readObjectFrom(dis);
							m.preform(ServerNetworkManager.this, player);
						}
					}
				}
			} 
			catch (IOException e) {}
		}
	}
}

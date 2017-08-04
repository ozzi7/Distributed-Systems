package com.conflict.game.client;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.conflict.game.GameObject;
import com.conflict.game.GameRenderer;
import com.conflict.game.Ship;
import com.conflict.game.serialisation.Serializable;
import com.conflict.game.serialisation.Serializer;
import com.conflict.game.server.ServerNetworkManager;
import com.conflict.game.serverMessages.ServerMessage;


public class ClientNetworkManager
{
	public int size;
	public Ship playerShip = null;
	
	public ArrayList<GameObject> objects;
	
	public Receiver receiver;
	public GameRenderer renderer;
	
	public long lastUpdateTime = 0;
	public float estimatedDeltaPerMillisec = 1.0f/500f; //1f/30f;
	
	public InetAddress serverAddress;
	
	public DatagramSocket socket;
	
	public ClientNetworkManager(InetAddress address, GameRenderer renderer)
	{
		serverAddress = address;
		this.renderer = renderer;
		receiver = new Receiver();
	}
	
	public class Receiver implements Runnable
	{
		private Thread thread;
		private boolean running;
		
		private byte[] buffer;
		private DatagramPacket receivePacket;

		
		
		public Receiver()
		{
			try {
				socket = new DatagramSocket();
				buffer = new byte[ServerNetworkManager.RECEIVER_BUFFER_SIZE];
				receivePacket = new DatagramPacket(buffer, buffer.length);
				sendData(null); //connect
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

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
					Gdx.app.log("Game", "Package received...");
					
					ByteArrayInputStream bais = new ByteArrayInputStream(buffer,0, receivePacket.getLength());
					DataInputStream dis = new DataInputStream(bais);
					short id = dis.readShort();
					if (id == ServerNetworkManager.CONNECTION_ID)
					{
						while (bais.available() > 0)
						{
							Serializable sm = Serializer.readObjectFrom(dis);
//							Gdx.app.log("Game", sm.toString()+" from "+receivePacket.getAddress().toString());
							ServerMessage m = (ServerMessage)sm;
							m.preform(ClientNetworkManager.this);
						}
						boolean test = true;
					}
				}
			} 
			catch (IOException e) {}
		}
	}

	
	public void sendData(Serializable obj)
	{
		
//		if (obj != null)
//			Gdx.app.log("Game", "Sent Object type: "+obj.getClass().getName());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeShort(ServerNetworkManager.CONNECTION_ID);
			if (obj != null)
			{
				Serializer.writeObjectTo(obj, dos);
			}
			
			byte[] data = baos.toByteArray();
			
//			Gdx.app.log("Game", "Server ip: "+serverAddress.toString());
			
			DatagramPacket p = new DatagramPacket(data, data.length, serverAddress, ServerNetworkManager.USED_PORT);
		
			socket.send(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

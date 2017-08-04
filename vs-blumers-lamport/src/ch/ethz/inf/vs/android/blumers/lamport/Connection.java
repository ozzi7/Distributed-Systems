package ch.ethz.inf.vs.android.blumers.lamport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Connection implements Runnable
{
	public Chat chat;
	
	public Thread thread;
	public boolean running;
	public boolean clientListReceived;
	
	public byte[] data;
	public DatagramPacket packet;
	public DatagramSocket udpSocket;
	public InetAddress address;
	
	public boolean registered;
	
	TextMsgOutputBuffer textMsgOutputBuffer;
	TextMsgInputBuffer textMsgInputBuffer;
	
	public int lamport = 0;
	
	
	public Connection(Chat chat)
	{
		this.chat = chat;
		
		textMsgOutputBuffer = new TextMsgOutputBuffer();
		textMsgInputBuffer = new TextMsgInputBuffer();
		
		try {
			udpSocket = new DatagramSocket();
			udpSocket.setSoTimeout(100);
		} catch (SocketException e) {
			chat.showMessage("ErrorMsg", "Could not create socket: "+e.getMessage());
		}
		
		registered = false;
		clientListReceived = false;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		
		try {
			address = InetAddress.getByName("vslab.inf.ethz.ch");
			data = new byte[2048];
			packet = new DatagramPacket(data, data.length);
			
			

			while (running)
			{
				//try to send a text message
				textMsgOutputBuffer.trySend(this);
				
				//try to register if needed
				if (!registered)
				{
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					registerUser(chat.username, address);
				}
				
				try {
					
					udpSocket.receive(packet);
					receive(new String(packet.getData(),0,packet.getLength()));
					
				} catch (IOException e) {}
				
				textMsgInputBuffer.outputAll(chat);
			}
			
		} catch (UnknownHostException e) {
			chat.showMessage("Error", "Could not resolve address: "+e.getMessage());
			return;
		} 
		
		deregisterUser();
	}
	
	public void stop()
	{
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendRaw(String text)
	{
		byte[] data = text.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 5000);
		try {
			udpSocket.send(sendPacket);
		} catch (IOException e) {
			chat.showMessage("Error:", "Could not send message!");
		}
		Log.d("msg", "RawOut: "+text);
	}
	
	// send text message to server
	public void sendTextMsg(String text)
	{
		JSONObject jsonObject = null;
		lamport++;

		try {
			jsonObject = new JSONObject();
			jsonObject.put("cmd", "message");
			jsonObject.put("text", text);
			jsonObject.put("time_vector", new JSONObject()); // creates "time_vector": {}
			jsonObject.put("lamport", lamport);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		sendRaw(jsonObject.toString());
		
		Log.d("msg", "sent: "+text+" - "+lamport);
	}
	
	// register user at server
	public void registerUser(String name, InetAddress address)
	{		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmd", "register");
			jsonObject.put("user", name);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		sendRaw(jsonObject.toString());
	}
	
	// called when message received from server
	public void receive(String msg) 
	{
		
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(msg);

			if(jsonObject.has("error")) {			
				if(jsonObject.has("text")) {
					running = false;
					chat.showMessage("Server Error!", jsonObject.getString("text"));		// reg_fail		
				}
				else {
					chat.showMessage("Server Error!", jsonObject.getString("error"));	// not_registered, already_registered, invalid json string
				}
			}
			else if(jsonObject.has("info")) {
				chat.showMessage("Server Information", jsonObject.getString("info"));	// information response to cmd info
			}
			else if(jsonObject.has("index")) {
				registered = true;
				chat.myIndex = jsonObject.getInt("index");
				lamport = jsonObject.getInt("init_lamport");
				textMsgInputBuffer.lamportDelivered = lamport;
				chat.showMessage("Server Information", "Successfully registered!");
			}
			else if(jsonObject.has("cmd") && jsonObject.get("cmd").equals("message")) { // Message
				
				
				if (jsonObject.has("sender"))
				{
					textMsgInputBuffer.addMsg(new TextMessage(jsonObject.get("sender").toString(), jsonObject.get("text").toString(), jsonObject.getInt("lamport"))); //TODO: lamport
					lamport = Math.max(lamport, jsonObject.getInt("lamport"));
					Log.d("msg", jsonObject.get("text").toString()+" - "+jsonObject.getInt("lamport"));
				}
				else
				{
					chat.showMessage("Server", jsonObject.get("text").toString());
				}
					
//					if(chat.clientNamehashtable.containsKey(jsonObject.get("sender").toString()) == true) {
//						chat.showMessage(chat.clientNamehashtable.get(jsonObject.get("sender").toString()), jsonObject.get("text").toString());	
//					}
//					else {
//						chat.showMessage(jsonObject.get("sender").toString(), jsonObject.get("text").toString());
//						sendClientListRequest();
//					}
			}
			else if(jsonObject.has("success")) {
				if (jsonObject.get("success").toString().equals("msg_ok"))
				{
					textMsgOutputBuffer.ack(this);
					Log.d("msg", "ack");
				}
			}
			else if(jsonObject.has("clients")) {	// list of clients
				clientListReceived = true;
				chat.clientNamehashtable.clear();
				JSONObject clients = jsonObject.getJSONObject("clients");
				Iterator<?> keys = clients.keys();
				
				while(keys.hasNext()) {
					String key = (String)keys.next();
					chat.clientNamehashtable.put(key, clients.get(key).toString());
				}
			}
			else
			{
				chat.showMessage("Unhandled msg:", msg);
			}
				
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("msg", "RawIn: "+msg);
	}
	
	public void sendClientListRequest()
	{
		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject();
			jsonObject.put("cmd", "get_clients");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		sendRaw(jsonObject.toString());
	}
	
	public void deregisterUser()
	{
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmd", "deregister");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		sendRaw(jsonObject.toString());
	}
}

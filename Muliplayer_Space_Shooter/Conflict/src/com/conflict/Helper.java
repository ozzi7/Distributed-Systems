package com.conflict;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.badlogic.gdx.Gdx;

public class Helper {

	public static Random rnd = new Random(); 
	
	public static void initialize()
	{

	}
	
	public static float relDir(float dirSource, float dirDest)
	{
		float deltaDir = dirDest-dirSource;
		if (deltaDir > 180f)
		{
			deltaDir -= 360f;
		}
		else
		{
			if (deltaDir < -180f)
				deltaDir += 360f;
		}
		
		return deltaDir;
	}
	
	public static float getDir(float dirSource, float dirDest)
	{

		float d = relDir(dirSource, dirDest);
		if (d < 0f)
		{
			if (d > -5f)
				return 0f;
			else
				return -1f;
		}
		else
		{
			if (d < 5f)
				return 0f;
			else
				return 1f;
		}
		
	}
	
	public static float addRotation(float a, float b)
	{
		float x = a+b;
		if (x > 360f)
			return x-360f;
		else if (x < 0)
			return x+360;
		else
			return x;
	}
	
	public static float getRand(float max)
	{
		return ((rnd.nextFloat()-0.5f)*max);
	}
	
	public static ArrayList<byte[]> serializeObjects(ArrayList<?> objects, int maxSize)
	{
		ArrayList<byte[]> dataArrays = new ArrayList<byte[]>();
		
		int lastSize = 0;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			if (objects.size() > 0)
			{
				for (Object o : objects)
				{
					if (o == null)
						throw new NullPointerException();

					oos.writeUnshared(o);
					
					if (baos.size() > maxSize)
					{
						byte[] d = Arrays.copyOfRange(baos.toByteArray(),0,lastSize);
						Gdx.app.log("Game", "message size: "+d.length);
						dataArrays.add(d);
						baos = new ByteArrayOutputStream();
						oos = new ObjectOutputStream(baos);
						oos.writeUnshared(o);
						lastSize = baos.size();
					}
					else
					{
						lastSize = baos.size();
					}
				}
				oos.close();
				baos.close();
				dataArrays.add(baos.toByteArray());
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataArrays;
	}
	
	
	
}

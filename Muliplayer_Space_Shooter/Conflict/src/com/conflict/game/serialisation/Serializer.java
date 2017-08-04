package com.conflict.game.serialisation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.conflict.game.Ship;
import com.conflict.game.Shot1;
import com.conflict.game.ClientMessages.InputUpdateMessage;
import com.conflict.game.serverMessages.UpdateObjectMessage;

public class Serializer {
	public static ArrayList<Class<? extends Serializable>> classList = new ArrayList<Class<? extends Serializable>>();
	
	public static void addClass(Class<? extends Serializable> c)
	{
		try {
			
			Field f = c.getField("serialID");
			f.setShort(null, (short) classList.size());
			classList.add(c);
			
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Serializable readObjectFrom(DataInputStream in)
	{
		try {
			short id = in.readShort();
			Class<? extends Serializable> c = classList.get(id);
			Serializable o = c.newInstance();
			o.restoreFrom(in);
			return o;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeObjectTo(Serializable obj, DataOutputStream out)
	{
		short id = obj.getId();
		try {
			out.writeShort(id);
			obj.storeTo(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void initialize()
	{
		if (classList.size() == 0)
		{
			addClass(InputUpdateMessage.class);
			addClass(UpdateObjectMessage.class);
			addClass(Ship.class);
			addClass(Shot1.class);
		}
	}
	
}

package ch.ethz.inf.vs.android.blumers.lamport;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import android.util.Log;

public class TextMsgInputBuffer {
	
	long lastDelivered;
	public int lamportDelivered = 0;
	
	PriorityQueue<TextMessage> buffer;
	
	public TextMsgInputBuffer()
	{
		lastDelivered = System.currentTimeMillis();
		buffer = new PriorityQueue<TextMessage>(20,new Comparator<TextMessage>() {
			@Override
			public int compare(TextMessage lhs, TextMessage rhs) {
				if (lhs.lamport < rhs.lamport)
					return -1;
				else if (lhs.lamport == rhs.lamport)
					return 0;
				else
					return 1;
			}
		}) ;
	}
	
	public void addMsg(TextMessage msg)
	{
		buffer.add(msg);
	}
	
	public void outputAll(Chat chat)
	{
		while (outputNext(chat));
	}
	
	public void remove()
	{
		buffer.poll();
	}
	
	private boolean outputNext(Chat chat)
	{
		TextMessage m = buffer.peek();
		
//		Log.d("msg", "bufferUsage: "+buffer.size()+" Time: "+(System.currentTimeMillis()-lastDelivered)+" Lamport: "+lamportDelivered);
		
		if (buffer.isEmpty())
		{
			lastDelivered = System.currentTimeMillis();
		}
		
		if (m != null)
		{
			if (System.currentTimeMillis()-lastDelivered > 1000)
			{
				lamportDelivered++;
				lastDelivered = System.currentTimeMillis();
//				Log.d("msg", "lamportDelivered: "+lamportDelivered);
			}
			
			if (m.lamport <= lamportDelivered+1)
			{
				if(chat.clientNamehashtable.containsKey(m.index) == true)
				{
					chat.showMessage(chat.clientNamehashtable.get(m.index), m.text);
					remove();
					
					lastDelivered = System.currentTimeMillis();
					lamportDelivered = Math.max(m.lamport+1, lamportDelivered);
					Log.d("msg", "lamportDelivered: "+lamportDelivered);
					return true;
				}
				else
				{
					chat.connection.sendClientListRequest();
				}
			}
		}
		
		return false;
	}
}

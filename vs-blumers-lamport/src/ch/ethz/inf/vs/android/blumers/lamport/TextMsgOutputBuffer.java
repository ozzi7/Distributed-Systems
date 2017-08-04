package ch.ethz.inf.vs.android.blumers.lamport;

import java.util.LinkedList;

public class TextMsgOutputBuffer {

	long lastSent;
	LinkedList<String> buffer;
	
	public TextMsgOutputBuffer()
	{
		buffer = new LinkedList<String>();
	}
	
	public void addMsg(String msg)
	{
		buffer.addLast(msg);
	}
	
	public String get()
	{
		try {
			return buffer.getFirst();
		} catch (Exception e) {
			return null;
		}
	}
	
	public void trySend(Connection connection)
	{
		String m = get();
		if (m != null)
		{
			if (System.currentTimeMillis()-lastSent > 3000)
			{
				connection.sendTextMsg(m);
				lastSent = System.currentTimeMillis();
			}
		}
	}
	
	public void ack(Connection connection)
	{
		String m = buffer.poll();
		if (m != null)
		{
			connection.chat.showMessage(connection.chat.username, m);
			lastSent = 0;
		}
	}

}

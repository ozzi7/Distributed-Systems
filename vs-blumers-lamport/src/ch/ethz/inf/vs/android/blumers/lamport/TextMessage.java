package ch.ethz.inf.vs.android.blumers.lamport;

import android.util.Log;

public class TextMessage {
	String index;
	String text;
	int lamport;
	
	public TextMessage(String index, String text, int lamport)
	{
		this.index = index;
		this.text = text;
		this.lamport = lamport;
		
		
	}
}

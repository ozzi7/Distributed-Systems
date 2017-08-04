package com.conflict.game.serialisation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Serializable {
	
	public short getId();
	
	public void storeTo(DataOutputStream out) throws IOException;
	
	public void restoreFrom(DataInputStream in) throws IOException;
}

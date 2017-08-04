
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.conflict.Helper;
import com.conflict.game.Ship;
import com.conflict.game.ClientMessages.InputUpdateMessage;
import com.conflict.game.serialisation.Serializable;
import com.conflict.game.serialisation.Serializer;
import com.conflict.game.server.ServerNetworkManager;
import com.conflict.game.serverMessages.UpdateObjectMessage;

public class Main {

	public static final int minUpdateTime = 200;
	
	ServerNetworkManager networkManager;

	
	
	public static void main(String[] args) {
		Main m = new Main();
		m.main();
	}
	
	public void main()
	{
		Serializer.initialize();
		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		DataOutputStream dao = new DataOutputStream(baos);
//		
//		Ship s = new Ship();
//		s.position.x = 55f;
//		
//		Serializer.writeObjectTo(s, dao);
//		
//		byte[] data = baos.toByteArray();
//		
//		ByteArrayInputStream bais = new ByteArrayInputStream(data);
//		DataInputStream dis = new DataInputStream(bais);
//		
//		Serializable so = Serializer.readObjectFrom(dis);
//		s = (Ship)so;
//		System.out.print(s.position.x);
		
//		System.out.print(InputUpdateMessage.serialID+" "+UpdateObjectMessage.serialID+" "+Ship.serialID);
		
		
		Helper.initialize();
		networkManager = new ServerNetworkManager(3);
		
		long lastDebugInfoPrint = System.currentTimeMillis();
		int updatesPerSec=0;
		
		long oldTime = System.currentTimeMillis();
		while (true)
		{
			long t = System.currentTimeMillis();
			
			if (t-oldTime > minUpdateTime)
			{
				updatesPerSec++;
				float delta = (t-oldTime)*0.001f;
				networkManager.update(delta);
				oldTime = t;
			}
			
			if (t-lastDebugInfoPrint > 1000)
			{
				System.out.print("updates per second: "+updatesPerSec+"\n");
				updatesPerSec = 0;
				lastDebugInfoPrint = t;
			}
			
			try 
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e) 
			{

			}
		}
		
	}

}

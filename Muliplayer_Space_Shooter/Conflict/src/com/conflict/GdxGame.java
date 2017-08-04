package com.conflict;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.conflict.game.GameObject;
import com.conflict.game.GameRenderer;
import com.conflict.game.Ship;
import com.conflict.game.client.ClientNetworkManager;

public class GdxGame implements ApplicationListener {
	
	public static final int minFrameLength = 34;

	public AndroidGameRenderer renderer;
	ClientNetworkManager networkManager;
	
	float deltaTime;
	long oldTime;
	
	int debugCounter = 0;
	
	@Override
	public void create() {
		
		Helper.initialize();
		Graphics.initialize();
		
		renderer = new AndroidGameRenderer();
		oldTime = System.currentTimeMillis();
		deltaTime = 0;
		
		try 
		{
			networkManager = new ClientNetworkManager(InetAddress.getByName("10.2.104.227"), renderer);
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}

//		
//		float w = Gdx.graphics.getWidth();
//		float h = Gdx.graphics.getHeight();
//		
//		
//		gameCamera = new OrthographicCamera(1, h/w);
//		uiCamera = new OrthographicCamera(w,h);
//		uiCamera.position.set(w*0.5f,-h*0.5f,0);
//		uiCamera.update();
//		
//		batch = new SpriteBatch();
//		
//		texture = new Texture(Gdx.files.internal("data/ship.png"));
//		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//		
//		TextureRegion region = new TextureRegion(texture, 0, 0, 64, 64);
//		
//		sprite = new Sprite(region);
//		sprite.setSize(0.1f, 0.1f);
//		sprite.setPosition(0, 0);
//		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
//		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
	}

	@Override
	public void dispose() {
		networkManager.receiver.stop();
		renderer.dispose();
	}

	@Override
	public void render() {		
		
		long t = System.currentTimeMillis();
		while (t-oldTime < minFrameLength)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
			t = System.currentTimeMillis();
		}
		
		deltaTime = (t-networkManager.lastUpdateTime)*0.001f;
		oldTime = t;

//		if (deltaTime > 2f)
//			deltaTime = 2f;
//		
//		deltaTime = 0;

		
//		Gdx.app.log("Game", "counter: "+(debugCounter++));
		
		ArrayList<GameObject> objects = networkManager.objects;
		
		renderer.beginRender(networkManager.playerShip, deltaTime);
		renderer.renderBackground(networkManager.size);

		if (objects != null)
		{
			for (GameObject go : objects)
			{
				go.render(renderer, deltaTime);
			}
		}

		renderer.endRender();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}

package com.conflict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AndroidRenderData {

	Texture shipTexture;
	Sprite shipSprite;
	
	Texture backgroundTexture;
	Sprite backgroundSprite;
	
	Texture particleTexture;
	Sprite particleSprite;
	
	Texture shot1Texture;
	Sprite shot1Sprite;
	
	
	public AndroidRenderData()
	{
		shipTexture = new Texture(Gdx.files.internal("data/ship.png"));
		shipSprite = createSprite(shipTexture,0.1f);
		
		particleTexture = new Texture(Gdx.files.internal("data/particle.png"));
		particleSprite = createSprite(particleTexture, 0.04f);
		
		backgroundTexture = new Texture(Gdx.files.internal("data/background.png"));
		backgroundTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		backgroundSprite = createSprite(backgroundTexture, 1);
		
		shot1Texture = new Texture(Gdx.files.internal("data/shot1.png"));
		shot1Sprite = createSprite(shot1Texture,0.05f);
	}
	
	public void dispose()
	{
		shipTexture.dispose();
		particleTexture.dispose();
		backgroundTexture.dispose();
		shot1Texture.dispose();
	}
	
	private Sprite createSprite(Texture t, float scale)
	{
		Sprite s = new Sprite(t);
		s.setSize(scale, scale);
		s.setOrigin(s.getWidth()*0.5f, s.getHeight()*0.5f);
		return s;
	}
}

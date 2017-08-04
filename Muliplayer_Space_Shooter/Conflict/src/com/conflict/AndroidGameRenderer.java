package com.conflict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.conflict.game.GameRenderer;
import com.conflict.game.Ship;
import com.conflict.game.Shot1;

public class AndroidGameRenderer implements GameRenderer {

	static final float camaraFollowSpeed = 0.2f;
	static final float maxSensorValue = 0.99f;
	static final float maxCameraViewDistance = 0.4f;
	static final float sensorSensibility = 1.2f;
	static final float minViewDistance = 0.1f;
	
	boolean boostOn, action1, action2, action3, viewDirectionSet;
	
	float screenSizeX, screenSizeY, ratio, viewDirection, viewDistance;
	
	ParticleSystem boostParticleSystem;
	
	OrthographicCamera gameCamera;
	
	OrthographicCamera uiCamera;
	SpriteBatch batch;
	
	AndroidRenderData data;
	
	public AndroidGameRenderer()
	{
		float screenSizeX = Gdx.graphics.getWidth();
		float screenSizeY = Gdx.graphics.getHeight();
		ratio = screenSizeY/screenSizeX;
		
		gameCamera = new OrthographicCamera(1, ratio);
		uiCamera = new OrthographicCamera(screenSizeX,screenSizeY);
		uiCamera.position.set(screenSizeX*0.5f,-screenSizeY*0.5f,0);
		uiCamera.update();
		
		batch = new SpriteBatch();
		data = new AndroidRenderData();
		
		boostParticleSystem = new ParticleSystem(1000, data.particleTexture, 0.999f, 10);
		
	}
	
	@Override
	public void renderBackground(int size) {
		data.backgroundSprite.setSize(size+4, size+4);
		data.backgroundSprite.setPosition(-2,-2);
		data.backgroundSprite.setRegion(0, 0, 256*(size+4), 256*(size+4));
		data.backgroundSprite.draw(batch);
		
		boostParticleSystem.render(this);
		
	}

	@Override
	public void renderShip(Ship ship, float delta) {
		float xPos = ship.position.x+ship.velocity.x*delta;
		float yPos = ship.position.y+ship.velocity.y*delta;
		float dAngle = ship.angle+ship.angularVelocity*delta;
		data.shipSprite.setPosition(xPos-data.shipSprite.getWidth()*0.5f, yPos-data.shipSprite.getHeight()*0.5f);
		data.shipSprite.setRotation(dAngle);
		data.shipSprite.draw(batch);
		
		if (ship.boostOn)
		{
			Vector2 dir = new Vector2(-0.004f,0);
			dir.rotate(dAngle);
			float offX = dir.x*10f;
			float offY = dir.y*10f;
			dir.x += Helper.getRand(0.003f);
			dir.y += Helper.getRand(0.003f);
			
//			if (Helper.rnd.nextInt(3) == 0)
			boostParticleSystem.add(offX+xPos, offY+yPos, dir.x, dir.y, 0.05f, new Color(0.2f+Helper.getRand(0.2f), 0.2f+Helper.getRand(0.2f), 0.8f+Helper.getRand(0.4f), 0.5f));
		}
	}
	
	@Override
	public void renderShot1(Shot1 obj, float delta) {
		float xPos = obj.position.x+obj.velocity.x*delta;
		float yPos = obj.position.y+obj.velocity.y*delta;
		float dAngle = obj.angle+obj.angularVelocity*delta;
		data.shot1Sprite.setPosition(xPos-data.shot1Sprite.getWidth()*0.5f, yPos-data.shot1Sprite.getHeight()*0.5f);
		data.shot1Sprite.setRotation(dAngle);
		data.shot1Sprite.draw(batch);
	}
	

	@Override
	public void renderUI(Ship ship) {
		
	}

	@Override
	public void dispose() {
		data.dispose();
		batch.dispose();
	}

	@Override
	public void beginRender(Ship playerShip, float delta) {
		
		// update camera position
		
		float xSens = Gdx.input.getAccelerometerX()*0.15f;
		if (xSens >= maxSensorValue)
			xSens = maxSensorValue;
		if (xSens <= -maxSensorValue)
			xSens = -maxSensorValue;
		float xSensAngle = (float) Math.asin(xSens)-0.6f;
		
		float ySens = Gdx.input.getAccelerometerY()*0.15f;
		if (ySens >= maxSensorValue)
			ySens = maxSensorValue;
		if (ySens <= -maxSensorValue)
			ySens = -maxSensorValue;
		float ySensAngle = (float) Math.asin(ySens);
		xSensAngle *= sensorSensibility;
		ySensAngle *= sensorSensibility;
		
		viewDistance = (float) Math.sqrt(xSensAngle*xSensAngle+ySensAngle*ySensAngle);
		
		viewDirection = (float) (-Math.atan2(xSensAngle, ySensAngle)/Math.PI*180f);
		if (viewDirection > 360f)
			viewDirection -= 360f;
		if (viewDirection < 0)
			viewDirection += 360;
		
		if (viewDistance > maxCameraViewDistance)
			viewDistance = maxCameraViewDistance;
		
		Vector2 camDestPos = new Vector2(viewDistance,0);
		camDestPos.rotate(viewDirection);
				
		if (playerShip != null)
		{	
			
			float xPos = playerShip.position.x+delta*playerShip.velocity.x;
			float yPos = playerShip.position.y+delta*playerShip.velocity.y;
			gameCamera.position.add((xPos+camDestPos.x)*camaraFollowSpeed, (yPos+camDestPos.y)*camaraFollowSpeed, 0);
			gameCamera.position.scl(1.0f/(1.0f+camaraFollowSpeed));
			gameCamera.update();
		}
		
		// keys
		for (int i=0; i<3; i++)
		{
			if (Gdx.input.isTouched(i))
			{
				float touchX = Gdx.input.getX(i);
				float touchY = Gdx.input.getY(i);
				
				if (touchX < Gdx.graphics.getWidth()*0.5f)
				{
					boostOn = true;
				}
				else
				{
					action1 = true;
				}
			}
		}
		
		// render head
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(gameCamera.combined);
		batch.begin();
		
	}

	@Override
	public void endRender() {
		batch.end();
	}

	@Override
	public float getViewDirection() {
		return viewDirection;
	}

	@Override
	public boolean isViewDirectionSet() {
		return (viewDistance > minViewDistance);
	}

	@Override
	public boolean isBoostOn() {
		return boostOn;
	}

	@Override
	public void resetInput() {
		boostOn = false;
		action1 = false;
	}

	@Override
	public boolean isAction1On() {
		return action1;
	}



}

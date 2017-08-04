package com.conflict.game;

public interface GameRenderer {

	abstract void resetInput();
	
	abstract boolean isBoostOn();
	
	abstract boolean isAction1On();
	
	abstract float getViewDirection();
	
	abstract boolean isViewDirectionSet();
	
	abstract void renderBackground(int size);
	
	abstract void renderShip(Ship ship, float delta);
	
	abstract void renderShot1(Shot1 obj, float delta);
	
	abstract void renderUI(Ship ship);
	
	abstract void beginRender(Ship playerShip, float delta);
	
	abstract void endRender();
	
	abstract void dispose();
}

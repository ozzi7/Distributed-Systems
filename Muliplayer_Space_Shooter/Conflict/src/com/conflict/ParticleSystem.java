package com.conflict;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ParticleSystem {

	float friction;
	int livetime;
	
	Particle[] particles;
	int nextP;
	
	public ParticleSystem(int maxParticles, Texture texture, float friction, int livetime)
	{
		particles = new Particle[maxParticles];
		nextP = 0;
		this.friction = friction;
		this.livetime = livetime;
		
		for (int i=0; i<particles.length; i++)
		{
			particles[i] = new Particle(texture);
		}
	}

	public void add(float x, float y, float xSpeed, float ySpeed, float size, Color color)
	{
		Particle p = particles[nextP];
		Sprite sp = p.sprite;
		sp.setSize(size, size);
		sp.setPosition(x-sp.getWidth()*0.5f, y-sp.getHeight()*0.5f);
		sp.setColor(color);
		p.xSpeed = xSpeed;
		p.ySpeed = ySpeed;
		p.counter = livetime;
		
		nextP++;
		if (nextP >= particles.length)
			nextP = 0;
	}
	
	public void render(AndroidGameRenderer renderer)
	{
		
		int next = nextP;
		while (true)
		{
			next--;
			if (next<0)
				next = particles.length-1;
			
			Particle p = particles[next];
			
			if (p.counter >= 0)
			{
				p.sprite.translateX(p.xSpeed);
				p.sprite.translateY(p.ySpeed);
				
				p.xSpeed *= friction;
				p.ySpeed *= friction;
				
				p.sprite.draw(renderer.batch);
				
				p.counter--;
			}
			else
			{
				break;
			}
			
			if (next == nextP)
				break;
		}
	}

	
	class Particle 
	{
		float xSpeed;
		float ySpeed;
		Sprite sprite;
		int counter;
		
		Particle(Texture t)
		{
			sprite = new Sprite(t);
			counter = -1;
		}
		

		
	}
}

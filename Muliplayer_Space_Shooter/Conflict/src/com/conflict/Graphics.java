package com.conflict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Graphics {
	
	public static BitmapFont font1;

	static void initialize()
	{
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/arial_black.ttf"));
		font1 = generator.generateFont(15);

		generator.dispose();
	}
	
}

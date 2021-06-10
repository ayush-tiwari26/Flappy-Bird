package com.ayush.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

import javax.xml.soap.Text;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture birdup;
	Texture birddown;
	Texture background;
	Texture bottomTube;
	Texture topTube;
	Texture gameOver;

	//Scorer
	int score=0;
	BitmapFont font;

	//Shapes
	Circle birdShape;
	Rectangle topPipeShape;
	Rectangle bottomPipeShape;

	float birdY=0;
	float gap=400;
	float pipeAdj;
	float tubeOffset=600;


	//the bird velocity
	double velocity=0;
	int gameState=0;
	float tubeVelocity=3;
	float tubeX;
	float distanceOfTubes;
	float numberOfTubes=4;

	//the gravity
	double gravity=0.5;

	//extras
	int c=0,p=0;
	// define speed of flappy bird here, 1 is fastest
	int speed=4;
	Random randomGap=new Random();

	@Override
	public void create () {
		batch = new SpriteBatch();


		// making textures
		background=new Texture("bg.png");
		birdup=new Texture("bird.png");
		birddown=new Texture("bird2.png");
		bottomTube=new Texture("bottomtube.png");
		topTube=new Texture("toptube.png");
		gameOver=new Texture("gameover.png.png");

		birdShape=new Circle();
		topPipeShape=new Rectangle();
		bottomPipeShape=new Rectangle();
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().scale(7);

		birdY=Gdx.graphics.getHeight()/2 -80;

		//the height to adjust mismatched pipes ends for zero gap b/w them
		pipeAdj=bottomTube.getHeight()+topTube.getHeight()-Gdx.graphics.getHeight();
		tubeX=Gdx.graphics.getWidth();
	}

	@Override
	public void render () {
		batch.begin();
		//if bird reaches bottom of screen
		if(birdY<=100){
			birdY=80;
		}//if bird reaches top of screen
		if(birdY>=Gdx.graphics.getHeight()-100){
			birdY=Gdx.graphics.getHeight()-100;
		}

		//controling tubeMotion operation
		tubeX-=tubeVelocity;
		tubeVelocity+=0.0025;
		//drowaing background
		batch.draw(background,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		//adding pipes
		drawBottomTube();
		drawTopTube();
		// drawing bird
		drawBird();

		if(gameState==1) {
			//setting birf velocity
			velocity+=gravity;
			birdY -= velocity;
			//checking gravity
			if(Gdx.input.justTouched()){
				birdY=birdY+150;
				velocity=0;
			}
			//refreshing tube
			if(tubeX<-bottomTube.getWidth()){
				//generating a random gap
				gap=randomGap.nextInt(650)+150;
				int rangeOfOffset=Gdx.graphics.getHeight()-200;
				tubeOffset=randomGap.nextInt(rangeOfOffset)-rangeOfOffset/2;
				tubeX=Gdx.graphics.getWidth();
				drawTopTube();
				drawBottomTube();
			}
			//point scorere updated
			if(tubeX<=Gdx.graphics.getWidth()/2-topTube.getWidth()){
				score+=1;
				Gdx.app.log("Game","POINTS IS ::: "+score);
			}
		}
		//until the game is not started
		else if(gameState==0){
			if(Gdx.input.justTouched()){
				Gdx.app.log("Game","Tapped once");
				gameState=1;
			}
		}
		else if(gameState==2){
			batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
			if(Gdx.input.justTouched()){
				Gdx.app.log("Game","Tapped once");
				gameState=0;
				birdY=Gdx.graphics.getHeight()/2 -80;
				velocity=3;
			}
		}
		font.draw(batch,String.valueOf(score),100,200);
		//ending
		batch.end();
	}

	// controls the bird movements and draws it
	public void drawBird(){
		//drawing bird
		if(c<=speed){
			c++;
			birdShape.set(Gdx.graphics.getWidth()/2,birdY+birdup.getHeight()/2,birddown.getWidth()/2);
			batch.draw(birddown,Gdx.graphics.getWidth()/2-birddown.getWidth(),birdY);
		}else {
			p++;
			if(p==speed){
				c=0;
				p=0;
			}

			birdShape.set(Gdx.graphics.getWidth()/2,birdY+birdup.getHeight()/2,birddown.getWidth()/2);
			batch.draw(birdup, Gdx.graphics.getWidth() / 2 -birdup.getWidth(), birdY);
		}

		//checking the collisions and gameover
		if(Intersector.overlaps(birdShape,topPipeShape) || Intersector.overlaps(birdShape,bottomPipeShape)){
			Gdx.app.log("Game","CLOLLISION HAPPED HERE");
			gameState=2;
			//ie game over
		}
	}

	public void drawBottomTube(){
		float height=bottomTube.getHeight()-tubeOffset/2;
		float Y=0-Gdx.graphics.getHeight()/2-tubeOffset/2;

		//next 4 lines for collison shape rendering
		bottomPipeShape.set(tubeX,Y-gap/2,bottomTube.getWidth(),Gdx.graphics.getHeight());
		batch.draw(bottomTube,tubeX,Y-gap/2,bottomTube.getWidth(),Gdx.graphics.getHeight());
	}
	public void drawTopTube(){
		float height=topTube.getHeight()+tubeOffset/2;
		float Y=Gdx.graphics.getHeight()/2-tubeOffset/2;

		topPipeShape.set(tubeX,Y+gap/2,topTube.getWidth(),Gdx.graphics.getHeight());
		batch.draw(topTube,tubeX,Y+gap/2,topTube.getWidth(),Gdx.graphics.getHeight());
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

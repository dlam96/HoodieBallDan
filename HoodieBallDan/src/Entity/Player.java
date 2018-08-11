package Entity;

import TileMap.*;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import Audio.AudioPlayer;

//import apple.laf.JRSUIConstants.Hit;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject 
{
	//counter for dead status
	private int counter = 0;	
	private HashMap<String, AudioPlayer> SFX;
	// player stuff
	private double health;
	private double maxHealth;
	private double fire;
	private double maxFire;
	private boolean dead = false;
	private boolean flinching;
	private long flinchTimer;
	
	// fireball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;
	
	// scratch
	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;
	
	// gliding
	private boolean gliding;
	
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {8, 6, 1, 1, 4, 2, 5, 5};
	
	// animations actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;
	//private static final int DYING = 7;
	
	public Player(TileMap tm ) 
	{
		super(tm);
		SFX = new HashMap<String, AudioPlayer>();
		
		SFX.put("fireball", new AudioPlayer("/SFX/fireball.wav", 1));
		SFX.put("jumping", new AudioPlayer("/SFX/playerjump.mp3", 1));
		SFX.put("gliding", new AudioPlayer("/SFX/wind.wav", .3));
		//SFX.put("walking", new AudioPlayer("/SFX/running.wav", 1));
		SFX.put("punching", new AudioPlayer("/SFX/Punch.mp3", .3));
		
		width = 64;
		height = 64;
		cwidth = 30;
		cheight = 40;
		
		moveSpeed = .25;
		maxSpeed = 1.5;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		health = maxHealth = 10;
		fire = maxFire = 1000;
		
		fireCost = 200;
		fireBallDamage = 3;
		fireBalls = new ArrayList<FireBall>();
		
		scratchDamage = 5;
		scratchRange = 40;
		try 
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/HoodieDan.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 8; i++)
			{
				BufferedImage [] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					if(i != 8)
					{
						bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);						
					}
//					else 
//					{
//						bi[j] = spritesheet.getSubimage(j * width * 2, i * height, width * 2, height); // for original sprite 2x size
//					}
				}
				sprites.add(bi);
			}			
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}		
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
		
	}
	
	public double getHealth() { return health; }
	public double getMaxHealth() { return maxHealth; }
	public double getFire() { return fire; }
	public double getMaxFire() { return maxFire; }
	public boolean getDead() { return dead;}
	
	public void setFiring() 
	{
		firing = true;
		
	}
	
	public void setScratching() 
	{
		scratching = true;
	}
	
	public void setGliding(boolean b)
	{
		gliding = b;
	}
	
	public void checkAttack(ArrayList<Enemy> enemies)
	{
		// loop through enemies
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e = enemies.get(i);
			
			//scratch attack
			if(scratching)
			{
				if(facingRight)
				{
					if(e.getx() > x && e.getx() < x + scratchRange && e.gety() > y - height / 2 && e.gety() < y + height / 2)
					{
						e.hit(scratchDamage);
					}
				}
				else 
				{
					if(e.getx() < x && e.getx() > x - scratchRange && e.gety() > y - height / 2 && e.gety() < y + height / 2) 
					{
						e.hit(scratchDamage);
					}
				}				
			}
			// fireballs
			for(int j = 0; j < fireBalls.size(); j++)
			{
				if(fireBalls.get(j).intersects(e))
				{
					e.hit(fireBallDamage);
					fireBalls.get(j).setHit();
					break;
				}
			}
		
			// check enemy collision
			if(intersects(e))
			{
				hit(e.getDamage());
			}
			
		}
	}

	public void hit(double damage)
	{
		if(flinching)
		{
			return;
		}
		health -= damage;
		if(health < 0)
		{
			health = 0;
		}
		if(health == 0)
		{
			dead = true;

		}
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	private void getNextPosition()
	{
		//System.out.format("located in player getNextPosition: %.3f %.3f%n", x, y); //prints coordinate of player
		// movement
		if(y > 400) dead = true; //falling to death range
		if(dead == false) {
		if(left)
		{
			dx -= moveSpeed;
			if(dx < -maxSpeed)
			{
				dx = -maxSpeed;
			}
		}
		else if(right)
		{
			dx += moveSpeed;
			if(dx > maxSpeed)
			{
				dx = maxSpeed;
			}
		}
		else 
		{
			if(dx > 0)
			{
				dx -= stopSpeed;
				if(dx < 0)
				{
					dx = 0;
				}
			}
			else if(dx < 0) 
			{
				dx += stopSpeed;
				if(dx > 0)
				{
					dx = 0;
				}
			}
		}
		
		// can't move while attacking, except in air
		if((currentAction == SCRATCHING)  || (currentAction == FIREBALL) && !(jumping || falling))
		{
			dx = 0;
		}
		
		// jumping
		if(jumping  && !falling)
		{
			dy = jumpStart;
			falling = true;
		}
		
		// falling
		if(falling)
		{
			if(dy > 0 && gliding)
			{
				dy += fallSpeed * 0.1;
			}
			else 
			{
				dy += fallSpeed;
			}
			if(dy > 0)
			{
				jumping = false;
			}
			if(dy < 0 && !jumping) 
			{
				dy += stopJumpSpeed;
			}
			if(dy > maxFallSpeed)
			{
				dy = maxFallSpeed;
			}
		}
	}
}
	
	public void update() 
	{
		//counter++ if dead so does not update render
		if(counter == 0) {
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		// check attack has stopped
		if(currentAction == SCRATCHING)
		{
			if(animation.hasPlayedOnce())
			{
				scratching = false;
			}
		}
		if(currentAction == FIREBALL)
		{
			if(animation.hasPlayedOnce())
			{
				firing = false;
			}
		}
		
		// fireball attack
		fire += 1;
		if(fire > maxFire)
		{
			fire = maxFire;
		}
		if(firing && currentAction != FIREBALL)
		{
			if(fire > fireCost)
			{
				fire -= fireCost;
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
			}
		}
		//update fireballs
		for(int i = 0; i < fireBalls.size(); i++)
		{
			fireBalls.get(i).update();
			if(fireBalls.get(i).shouldRemove())
			{
				fireBalls.remove(i);
				i--;
			}
		}
		
		// check done flinching 
		if(flinching)
		{
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 1000)
			{
				flinching = false;
			}
		}
		
		
		// set animation
		if(scratching)
		{
			if(currentAction != SCRATCHING)
			{
				currentAction = SCRATCHING;
				SFX.get("punching").play();
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 64;
			}
		}
		else if(dead) {
//			if(currentAction != DYING)
//			{
//				currentAction = DYING;
//				animation.setFrames(sprites.get(DYING));
//				animation.setDelay(50);
//				width = 64;
				counter++;
//			}
		}
		else if(firing) 
		{
			if(currentAction != FIREBALL)
			{
				currentAction = FIREBALL;
				SFX.get("fireball").play();
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				width = 64;
			}
		}
		else if(dy > 0)		
		{
			if(gliding) 
			{
				if(currentAction != GLIDING)
				{
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 64;
					SFX.get("gliding").play();
				}
			}
			else if(currentAction != FALLING)
			{
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 64;
			}
		}
		else if(dy < 0)
		{
			if(currentAction != JUMPING)
			{
				currentAction = JUMPING;
				SFX.get("jumping").play();
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 64;
			}
		}
		else if(left || right)
		{
			if(currentAction != WALKING)
			{
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 64;
				//SFX.get("walking").play();
			}
		}
		else 
		{
			if(currentAction != IDLE)
			{
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 64;		
			}
		}		

		// set direction 
		if(currentAction != SCRATCHING && currentAction != FIREBALL)
		{
			if(right) 
			{
				facingRight = true;			
			}
			if(left)
			{
				facingRight = false;
			}
		}
		animation.update();
	}
		
	}
	
	public void draw(Graphics2D g)
	{
		setMapPosition();

	
		// draw fireballs
		for(int i = 0; i < fireBalls.size(); i++)
		{
			fireBalls.get(i).draw(g);
		}
		
		// draw player
		if(flinching)
		{
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2  == 0)
			{
				return;
			}
		}		
		super.draw(g);
	}
	
}
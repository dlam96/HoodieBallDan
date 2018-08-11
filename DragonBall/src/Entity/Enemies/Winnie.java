package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.Enemy;
import Entity.FireBall;
import Entity.Player;
import TileMap.TileMap;

public class Winnie extends Enemy {
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 8, 3};

	protected int currentAction;
	// fireball
	private boolean firing;
	private ArrayList<FireBall> fireBalls;

	// animations actions
	private static final int IDLE = 0;
	private static final int FIREBALL = 1;

	public Winnie(TileMap tm) {
		super(tm);

		moveSpeed = 0.2;
		maxSpeed = 1.5;
		fallSpeed = 0.2;
		maxFallSpeed = 10.0;

		width = 30;
		height = 40;
		cwidth = 30;
		cheight = 40;
		
		
		projectileDamage = 5;
		fireBalls = new ArrayList<FireBall>();
		health = maxHealth = 10;
		damage = 0;

		// load sprites
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/winnie.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 2; i++) // rows
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for (int j = 0; j < numFrames[i]; j++) // cols
				{
					if (i != 2) {
						bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
					}
				}
				sprites.add(bi);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(300);

		right = true;
		facingRight = true;
	}
	

	public void setFiringTrue() {
		firing = true;
	}
	
	public void setFiringFalse() {
		firing = false;
	}
	
	private void getNextPosition() {
		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		}

		// falling
		if (falling) {
			dy += fallSpeed;
		}
	}
	
	//checks if projectile collides with player
	public void checkProjectile(Player player)
	{
	
		for(int j = 0; j < fireBalls.size(); j++)
		{
			if(fireBalls.get(j).intersects(player)) 
			{
				System.out.print("Shoot");
				player.hit(getProjectile());
				fireBalls.get(j).setHit();
				break;
			}
		}
	}

	public void update() {
		// fireballs
		
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		// fireball attack
		if (firing && currentAction != FIREBALL) {
			FireBall fb = new FireBall(tileMap, facingRight);
			fb.setPosition(x, y);
			fireBalls.add(fb);
		}
		// update fireballs
		for (int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).update();
			if (fireBalls.get(i).shouldRemove()) {
				fireBalls.remove(i);
				i--;
			}
		}
		// set animation
		
		 if (firing) {
			if (currentAction != FIREBALL) {
				currentAction = FIREBALL;
				// SFX.get("fireball").play();
				
				animation.setFrames(sprites.get(FIREBALL));
				
			}
		} else {
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				// width = 64;
			}
		}
		// check attack has stopped
		if (currentAction == FIREBALL) {
			if (animation.hasPlayedOnce()) {
				firing = false;
			}
		}
		// check flinching
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed > 400) {
				flinching = false;
			}
		}

		// if it hits a wall, go other direction
		if (right && dx == 0) {
			right = false;
			left = true;
			facingRight = false;
		} else if (left && dx == 0) {
			right = true;
			left = false;
			facingRight = true;
		}

		// update animation
		animation.update();
	}

	public void draw(Graphics2D g) {
		// draw fireballs
		for (int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).draw(g);
		}

		setMapPosition();
		super.draw(g);
	}

}

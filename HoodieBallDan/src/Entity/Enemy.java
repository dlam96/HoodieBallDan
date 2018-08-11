package Entity;

import TileMap.TileMap;

public class Enemy extends MapObject
{
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected double damage;
	protected double projectileDamage;
	
	protected boolean flinching;
	protected long flinchTimer;
	
	public Enemy(TileMap tm)
	{
		super(tm);
		
	}
	
	public void update()
	{
		
	}
	
	public boolean isDead() { return dead; }
	
	public double getDamage() { return damage; }
	
	public double getProjectile() { return projectileDamage; }
	
	public void hit(int damage)
	{
		if(dead || flinching)
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
}

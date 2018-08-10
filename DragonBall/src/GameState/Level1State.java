package GameState;

import Audio.AudioPlayer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import Entity.Enemy;
import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Entity.Enemies.Slugger;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

public class Level1State extends GameState
{
	private TileMap tileMap;
	private Background bg;
	private HashMap<String, AudioPlayer> audio;
	protected int counter = 0;
	
	private Player player;
	
	private ArrayList<Enemy> enemies;
	private ArrayList<Explosion> explosions;
	
	private HUD hud;
	
	public Level1State(GameStateManager gsm)
	{
		this.gsm = gsm;
		if(counter == 0) {
			init();
		}
		counter++;
		
	}
	
	public void init() 
	{
		audio = new HashMap<String, AudioPlayer>();
		if(counter == 0){
			audio.put("level1BGM", new AudioPlayer("/BGM/9-bit Expedition.mp3", 0.1));
			audio.get("level1BGM").play();
		}
	
		tileMap = new TileMap(30); //30 = size of a tile
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1-1.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(0.07);

		bg = new Background("/Backgrounds/grassbg1.gif", 0.1);
		
		player = new Player(tileMap);
		player.setPosition(100, 160);
		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
		
		hud = new HUD(player);
	}
	
	private void populateEnemies()
	{
		enemies = new ArrayList<Enemy>();
		Slugger s; 
		Point[] points = new Point[] {
			new Point(200,100),
			new Point(300,100),
			new Point(860, 200),
			new Point(1545, 160),
			new Point(1550, 160),
			new Point(1560, 160),
			new Point(1570, 160),
			new Point(1580, 160),
			new Point(1590, 160),
			new Point(1600, 160),
			new Point(1610, 160),
			new Point(1620, 160),
			new Point(1630, 160),
			new Point(1640, 160),
			new Point(1650, 160),
			new Point(1660, 160),
			new Point(1670, 160),
			new Point(1680, 200),
			new Point(1690, 200),
			new Point(1700, 200),
			new Point(1800, 200),
			new Point(2000, 200)
		};
		for(int i = 0; i < points.length; i++)
		{
		 	s = new Slugger(tileMap);
		 	s.setPosition(points[i].x, points[i].y);
			enemies.add(s);
		}
		
	}
	
	
	public void update() 
	{
	
		// update player
		player.update();
		
		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety());
		
		// set background
		bg.setPosition(tileMap.getx(), tileMap.gety()); 
		
		// attack enemies
		player.checkAttack(enemies);
		
//		// update all enemies
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e  = enemies.get(i);
			e.update();
			if(e.isDead())
			{
				enemies.remove(i);
				i--;
				explosions.add(new Explosion(e.getx(), e.gety()));
			}
		}
		
//		// update all explosions
		for(int i = 0; i < explosions.size(); i ++)
		{
			explosions.get(i).update();
			if(explosions.get(i).shouldRemove())
					{
						explosions.remove(i);
						i--;
					}
		}
		
	}
	
	public void draw(Graphics2D g) 
	{
		//brings up death screen / loads user into next level
				if(player.getDead() == true) {
					//audio.put("deadBGM", new AudioPlayer("/BGM/Dark Souls III Soundtrack OST - Main Menu Theme.mp3", 0.5));
					gsm.setState(GameStateManager.DEADSTATE);
					//audio.get("deadBGM").play();
				}
				
				if((player.nextLevelXPosition() >= 3090 && (player.nextLevelXPosition() <= 3120)) && player.nextLevelYPosition() == 190) {
					gsm.setState(GameStateManager.LEVEL2STATE);
				}
		// draw background
		bg.draw(g); 
		// draw tilemap
		tileMap.draw(g);	
		
//		 draw player
		player.draw(g);
		
		// draw enemies
		for(int i = 0; i < enemies.size(); i++)
		{
			enemies.get(i).draw(g);
		}
		
		// draw explosions
		for(int i = 0; i < explosions.size(); i++)
		{
			explosions.get(i).setMapPosition((int)tileMap.getx(), (int)tileMap.gety());
			explosions.get(i).draw(g);
		}
		
//		// draw hud
		hud.draw(g);
		
	}
	
	public void keyPressed(int k) 
	{
		if(k == KeyEvent.VK_LEFT)
		{
			player.setLeft(true);
		}
		if(k == KeyEvent.VK_RIGHT)
		{
			player.setRight(true);
		}
		if(k == KeyEvent.VK_UP)
		{
			player.setUp(true);
		}
		if(k == KeyEvent.VK_DOWN)
		{
			player.setDown(true);
		}
		if(k == KeyEvent.VK_W)
		{
			player.setJumping(true);
		}
		if(k == KeyEvent.VK_E)
		{
			player.setGliding(true);
		}
		if(k == KeyEvent.VK_R)
		{
			player.setScratching();
		}
		if(k == KeyEvent.VK_F)
		{
			player.setFiring();
		}
	}
	
	public void keyReleased(int k) 
	{
		if(k == KeyEvent.VK_LEFT)
		{
			player.setLeft(false);
		}
		if(k == KeyEvent.VK_RIGHT)
		{
			player.setRight(false);
		}
		if(k == KeyEvent.VK_UP)
		{
			player.setUp(false);
		}
		if(k == KeyEvent.VK_DOWN)
		{
			player.setDown(false);
		}
		if(k == KeyEvent.VK_W)
		{
			player.setJumping(false);
		}
		if(k == KeyEvent.VK_E)
		{
			player.setGliding(false);
		}		
	}
}
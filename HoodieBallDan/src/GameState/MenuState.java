package GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import Audio.AudioPlayer;
import TileMap.Background;


public class MenuState extends GameState{
	private Background bg;
	private HashMap<String, AudioPlayer> sfx;
	
	private int currentChoice = 0;
	private String[] options = 
		{
			"Start", 
			"Help",
			"Quit"
		};
	
	private Color titleColor;
	private Font titleFont;
	
	private Font font;
	
	public MenuState(GameStateManager gsm) {
		this.gsm = gsm;
		
		try 
		{
			bg = new Background("/Backgrounds/clouds.png", 1);
			bg.setVector(-0.2, 0);
			titleColor = new Color(255, 127, 80); //title color
			titleFont = new Font("Comic Sans MS", Font.PLAIN, 22); //title font
			font = new Font("Arial", Font.PLAIN, 12);
			
			sfx = new HashMap<String, AudioPlayer>();
			sfx.put("menuselect", new AudioPlayer("/SFX/menuselect.mp3", 1));
			sfx.put("menuoption", new AudioPlayer("/SFX/menuoption.mp3", 1));
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void init() {}
	public void update() {
		bg.update();
	}
	public void draw(Graphics2D g) {
		// draw bg
		bg.draw(g);
		
		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Hoodie Ball Dan", 80, 70);
		
		//draw menu options
		g.setFont(font);
		for(int i = 0; i < options.length; i++) 
		{
			if (i == currentChoice)
			{
				g.setColor(Color.ORANGE);
			}
			else 
			{
				g.setColor(Color.BLACK);
			}
			g.drawString(options[i], 145, 140 + i * 15);
		}
	}
	
	private void select()
	{
		if(currentChoice == 0)
		{
			// start
			

			sfx.get("menuselect").play();
			
			gsm.setState(GameStateManager.LEVEL1STATE);
			
			
			
			
		}
		if(currentChoice == 1)
		{
			// help
			sfx.get("menuselect").play();
			gsm.setState(GameStateManager.LEVEL2STATE);
		}
		if(currentChoice == 2)
		{
			// quit
			sfx.get("menuselect").play();
			System.exit(0);
		}
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ENTER)
		{
			select();
		}
		if(k == KeyEvent.VK_UP)
		{
			currentChoice --;
			sfx.get("menuoption").play();
			if(currentChoice == -1)
			{
				currentChoice = options.length -1;
			}
		}
		if(k == KeyEvent.VK_DOWN)
		{
			currentChoice ++;
			sfx.get("menuoption").play();
			if(currentChoice == options.length)
			{
				currentChoice = 0;
			}
		}
	}
	public void keyReleased(int k) {
		
	}
	
}

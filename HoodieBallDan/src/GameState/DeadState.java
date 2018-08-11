package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import Audio.AudioPlayer;
import TileMap.Background;

public class DeadState extends GameState{
	private Background bg;
	
	private int currentChoice = 0;
	private String[] options = 
		{
			"Main Menu", 
			"Quit"
		};
	
	private Color titleColor;
	private Font titleFont;
	private Font font;
	
	public DeadState(GameStateManager gsm) {
		this.gsm = gsm;
		
		try 
		{
			bg = new Background("/Backgrounds/dark-souls-3.jpg", 0);
			bg.setVector(0, 0);
			
			titleColor = new Color(255, 0, 0); //title color
			titleFont = new Font("Times New Roman", Font.PLAIN, 30); //title font
			
			font = new Font("Arial", Font.PLAIN, 12);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		bg.update();
		
	}

	@Override
	public void draw(Graphics2D g) {
		// draw bg
				bg.draw(g);
				
				// draw title
				g.setColor(titleColor);
				g.setFont(titleFont);
				g.drawString("YOU DIED", 90, 100);
				
				//draw menu options
				g.setFont(font);
				for(int i = 0; i < options.length; i++) 
				{
					if (i == currentChoice)
					{
						g.setColor(Color.RED);
					}
					else 
					{
						g.setColor(Color.gray);
					}
					g.drawString(options[i], 135, 140 + i * 15);
				}
		
	}

	private void select()
	{
		if(currentChoice == 0)
		{
			// start
			gsm.setState(GameStateManager.MENUSTATE);
			
		}
		if(currentChoice == 1)
		{
			// quit
			System.exit(0);
		}
	}
	
	@Override
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ENTER)
		{
			select();
		}
		if(k == KeyEvent.VK_UP)
		{
			currentChoice --;
			if(currentChoice == -1)
			{
				currentChoice = options.length -1;
			}
		}
		if(k == KeyEvent.VK_DOWN)
		{
			currentChoice ++;
			if(currentChoice == options.length)
			{
				currentChoice = 0;
			}
		}
		
	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub
		
	}

}

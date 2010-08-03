package code;

import java.awt.*;
import javax.swing.*;

//main class that starts the Hyponorm program/GUI
public class Hyponorm 
{

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				HypoFrame frame = new HypoFrame(new MetaData()); //create new SizedFrame
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

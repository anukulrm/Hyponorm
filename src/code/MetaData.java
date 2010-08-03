package code;

import java.awt.*;

//Use this class to change all meta data. Just modify the information next to the commented areas and you'll be good to go.
public class MetaData 
{
	String icon;
	String frameTitle;
	Toolkit kit;
	Dimension frameSize;

	
	public MetaData()
	{
		icon = null; //enter icon's path
		frameTitle = "Hyponorm v 1.0 - "; //enter title of the product
		
		
		kit = Toolkit.getDefaultToolkit();
		frameSize = this.setFrameSize();
	}
	
	public Image getIcon()
	{
		if(icon != null)
			return kit.getImage(icon);
		return null;
	}
	
	public String getFrameTitle()
	{
		return frameTitle;
	}
	
	public Dimension getFrameSize()
	{
		return frameSize;
	}
	
	public Dimension setFrameSize()
	{
		Dimension size = kit.getScreenSize();
		return new Dimension((2*size.width)/3, (5*size.height)/6); //define size of frame with respect to the screen size
	}
	
}

package code;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageComponent extends JComponent
{
	private static final long serialVersionUID = 1L;
	
	BufferedImage image;
	ArrayList<Point> pointsMeasured;
	ArrayList<Line2D> linesDrawn;
	AffineTransform at;
	
	public ImageComponent()
	{
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); //set cursor to crosshair when over image
		linesDrawn = new ArrayList<Line2D>();
	}
	
	public void paintComponent(Graphics g)
	{
		if(image == null) return;
		
		Graphics2D g2d = (Graphics2D) g;
		image.getGraphics().dispose();
		Graphics2D myImageGraphics = image.createGraphics();
		
		if(pointsMeasured != null && pointsMeasured.size() != 0)
		{
			myImageGraphics.setColor(Color.RED);
			myImageGraphics.setStroke(new BasicStroke(3));
			
			for(int i = 0; i < pointsMeasured.size() - 1; i+=1){
				Line2D line = new Line2D.Double(pointsMeasured.get(i).x, pointsMeasured.get(i).y, pointsMeasured.get(i+1).x, pointsMeasured.get(i+1).y);
				myImageGraphics.draw(line);
				linesDrawn.add(line);
			}
		}
		
		g2d.drawRenderedImage(image, at);
		
	}	
	
	public Dimension getDimensions()
	{
		if(image == null) return null;
		
		return new Dimension(image.getWidth(), image.getHeight());
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public void setImage(String s)
	{
		try
		{
			image = ImageIO.read(new File(s));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.setPreferredSize(this.getDimensions());
		this.revalidate();
		clearPointsMeasuredArrayList();
		clearLinesDrawn();
	}
	
	public void clearPointsMeasuredArrayList()
	{
		pointsMeasured = new ArrayList<Point>();
	}
	
	public void clearLinesDrawn()
	{

	}
}

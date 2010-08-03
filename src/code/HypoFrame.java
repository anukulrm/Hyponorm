package code;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;


public class HypoFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	//instance variables - start
	MetaData metadata;
	JScrollPane scrollPane;
	ImageComponent imageArea;
	JMenu fileMenu;
	File[] filesList;
	JPanel buttonPanel;
	JButton measure;
	Point[] clicks;
	Measure measureActionListener;
	ResultsTableModel resultsModel;
	JTable resultsTable;
	JPanel resultsPanel;
	int currentMeasurement;
	JLabel youAreCurrentlyMeasuring;
	JButton redo;
	int currentFileNumber;
	JButton nextImage;
	JButton clearLines;
	JLabel warning;
	OpenNextImageListener openNextFileListener;
	JButton setScale;
	double scale;
	JLabel scaleLabel;
	boolean alreadySetScale;
	String savePath;
	ExportListener exportActionListener;
	String parentDirectory;
	JButton skip;
	//instance variables - end
	
	
	public HypoFrame(MetaData meta)
	{
		metadata = meta;
		this.setMetaData(metadata); //set all frame metadata and starting settings for the frame
		this.initializeComponents(); //initializes all of the necessary components
	}
	
	public void setMetaData(MetaData meta)
	{
		
		this.setLocationByPlatform(true); //we want to let the user's OS define the position of the frame
		this.setSize(metadata.getFrameSize()); //set frame size
		this.setTitle(metadata.getFrameTitle()); //set frame title
		this.setIconImage(metadata.getIcon()); //set frame icon image
		this.setLayout(new BorderLayout()); //set layout type to Border Layout
		
	}
	
	private void initializeComponents()
	{
		//set up the image viewing area
		imageArea = new ImageComponent();
		this.setupScrollPane();
		this.add(scrollPane, BorderLayout.CENTER); //add scroll pane to the frame
		
		//set up the menus
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		this.setupFileMenu();
		menuBar.add(fileMenu);
		
		//set up the button panel
		buttonPanel = new JPanel();
		this.setupButtonPanel();
		this.add(buttonPanel, BorderLayout.EAST);
	}
	
	private void setupScrollPane()
	{
		scrollPane = new JScrollPane(imageArea);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		
		clicks = new Point[30];
		imageArea.addMouseListener(new MouseHandler());
	}
	
	private void setupFileMenu()
	{
		fileMenu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open Image(s)");
		openItem.addActionListener(new OpenImageListener());
		fileMenu.add(openItem);
		
		JMenuItem exportItem = new JMenuItem("Export to .csv");
		fileMenu.add(exportItem);
		exportActionListener = new ExportListener();
		exportItem.addActionListener(exportActionListener);
		
	}
	
	private void setupButtonPanel()
	{
		buttonPanel.setPreferredSize(new Dimension(300,800));
		buttonPanel.setLayout(new FlowLayout());
		
		//buttons galore
		
		//measure
		measure = new JButton("Measure");
		measure.setEnabled(false);
		measure.setPreferredSize(new Dimension(100, 40));
		measureActionListener = new Measure();
		measure.addActionListener(measureActionListener);
		
		//create an input map for keystrokes
		InputMap iMap = buttonPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		iMap.put(KeyStroke.getKeyStroke("M"), "measure");
		iMap.put(KeyStroke.getKeyStroke("shift N"), "openNextFile");
		ActionMap aMap = buttonPanel.getActionMap();
		aMap.put("measure", measureActionListener);	
		
		
		//Results Table
		setupResultsTable();
		
		//redo button
		setupRedoButton();
		
		//open next image button
		nextImage = new JButton("Next Image");
		nextImage.setPreferredSize(new Dimension(100,40));
		openNextFileListener = new OpenNextImageListener();
		nextImage.addActionListener(openNextFileListener);
		aMap.put("openNextFile", openNextFileListener);
		
		//clear lines on screen button
		clearLines = new JButton("Erase Lines");
		clearLines.setPreferredSize(new Dimension(100, 40));
		clearLines.setEnabled(false);
		setupClearLinesButton();
		
		//set scale button
		setScale = new JButton("Set Scale");
		setScale.setPreferredSize(new Dimension(100,40));
		scale = 1;
		scaleLabel = new JLabel();
		scaleLabel.setPreferredSize(new Dimension(260, 20));
		setupSetScaleButton();
		alreadySetScale = false;
		
		warning = new JLabel();
		warning.setPreferredSize(new Dimension(300, 20));
		
		skip = new JButton("Skip Measurement");
		skip.setPreferredSize(new Dimension(140, 40));
		setupSkipButton();
		
		buttonPanel.add(measure);
		buttonPanel.add(warning);
		buttonPanel.add(resultsPanel);
		buttonPanel.add(redo);
		buttonPanel.add(skip);
		buttonPanel.add(clearLines);
		buttonPanel.add(nextImage);
		buttonPanel.add(setScale);
		buttonPanel.add(scaleLabel);
		
	}
	
	private void setupResultsTable()
	{
		resultsModel = new ResultsTableModel(); 
		resultsPanel = new JPanel(new BorderLayout());
		resultsTable = new JTable(resultsModel);
		
		//table configuration
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		resultsTable.setShowHorizontalLines(true);
		resultsTable.setShowVerticalLines(true);
		
		youAreCurrentlyMeasuring = new JLabel();
		youAreCurrentlyMeasuring.setPreferredSize(new Dimension(200, 20));

		
		resultsPanel.add(resultsTable.getTableHeader(), BorderLayout.PAGE_START);
		resultsPanel.add(resultsTable, BorderLayout.CENTER);
		resultsPanel.add(youAreCurrentlyMeasuring, BorderLayout.PAGE_END);
	}
	
	private void setupRedoButton()
	{
		redo = new JButton("Redo Last");
		redo.setPreferredSize(new Dimension(100, 40));
		redo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{		
				if(!measure.isEnabled())
					measure.setEnabled(true);
				if(currentMeasurement % 2 == 1){
					int calculation = (int)Math.floor(currentMeasurement / 2.0);
					resultsModel.setValueAt(null, calculation, 1 );
					youAreCurrentlyMeasuring.setText("The Hypocotyl length for plant " + currentMeasurement/2);
				}
				else
				{
					resultsModel.setValueAt(null, currentMeasurement / 2 - 1, 2 );
					youAreCurrentlyMeasuring.setText("The Root length for plant " + (currentMeasurement/2));
				}
				currentMeasurement--;
			}
		});
	}
	
	private void setupClearLinesButton()
	{
		clearLines.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				imageArea.clearLinesDrawn();
			}
			
		});
	}
	
	private void setupSetScaleButton()

	{
		setScale.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event)
			{
				imageArea.pointsMeasured.add(clicks[0]);
				imageArea.pointsMeasured.add(clicks[1]);
				if(clicks[1] != null)
				{
					if(alreadySetScale == false)
						scale = (measureActionListener.euclideanDistance(Math.abs(clicks[0].getX() - clicks[1].getX()), Math.abs(clicks[0].getY() - clicks[1].getY())))/13;
					else
						{
							scale +=(measureActionListener.euclideanDistance(Math.abs(clicks[0].getX() - clicks[1].getX()), Math.abs(clicks[0].getY() - clicks[1].getY())))/13;
							scale /= 2;
						}
				}
				alreadySetScale = true;
				
				scaleLabel.setText("Scale: 1mm = " + scale + " pixels");
			}

		});
	}
	
	private void setupSkipButton()
	{
		skip.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(currentMeasurement % 2 == 1){
					int calculation = (int)Math.floor(currentMeasurement / 2.0);
					resultsModel.setValueAt(0.0, calculation, 1 );
					youAreCurrentlyMeasuring.setText("The Root length for plant " + ((currentMeasurement == 1) ? 1:currentMeasurement/2) );
				}
				else
				{
					resultsModel.setValueAt(0.0, currentMeasurement / 2 - 1, 2 );
					if(currentMeasurement != 20)
						youAreCurrentlyMeasuring.setText("The Hypocotyl length for plant " + (currentMeasurement/2 + 1));
					else{
						youAreCurrentlyMeasuring.setText("You're done measuring this plant!");
						measure.setEnabled(false);
					}
				}
				currentMeasurement++;
				clicks = new Point[30];
			}
		});
	}
	class ExportListener extends AbstractAction
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed(ActionEvent event)
		{
			try {
				export();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void export() throws IOException
		{
			if(scale != 1)
			{
				if(savePath == null)
				{
					JFileChooser myChooser= new JFileChooser(parentDirectory); //creates a filechooser				
			        int returnVal = myChooser.showSaveDialog(null); //opens it
			        if(returnVal == JFileChooser.APPROVE_OPTION){ //if user clicks open
			            savePath = myChooser.getSelectedFile().getAbsolutePath(); //get path
						resultsModel.exportToCSV(filesList, savePath, currentFileNumber);
					}
				}
				else
					resultsModel.exportToCSV(filesList,savePath,currentFileNumber);
			}
		}
	}
	class OpenImageListener extends AbstractAction
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed(ActionEvent event)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true);
			int returnVal = chooser.showOpenDialog(null);
			
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				filesList = chooser.getSelectedFiles();
				imageArea.setImage(filesList[0].getAbsolutePath());
				setTitle(metadata.getFrameTitle() + filesList[0].getName());
				measureActionListener.makeBWImage();
				currentMeasurement = 0;
				parentDirectory = chooser.getCurrentDirectory().getAbsolutePath();
				if(savePath != null)
					try {
						resultsModel.exportToCSV(filesList, savePath, currentFileNumber);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					try {
						exportActionListener.export();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				resultsModel.clear();
				youAreCurrentlyMeasuring.setText("The Hypocotyl Length for plant 1");
				measure.setEnabled(true);
				clicks = new Point[30];
				currentFileNumber = 1;
				
				if(filesList.length > 1)
					nextImage.setEnabled(true);
				else
					nextImage.setEnabled(false);
			}
		}
	}

	class OpenNextImageListener extends AbstractAction
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed(ActionEvent event){
			if(currentFileNumber < filesList.length){

				String imgPath = filesList[currentFileNumber].getAbsolutePath(); //get path
	            imageArea.setImage(imgPath); //set path for the image in the ImageComponent
	            measureActionListener.makeBWImage();
	            setTitle(metadata.getFrameTitle() + filesList[currentFileNumber].getName());
	            imageArea.clearPointsMeasuredArrayList();
	            currentFileNumber++;
	            currentMeasurement = 0;
	            if(savePath != null)
					try {
						resultsModel.exportToCSV(filesList, savePath, currentFileNumber);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					try {
						exportActionListener.export();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            resultsModel.clear();
				youAreCurrentlyMeasuring.setText("The Hypocotyl Length for plant 1");
				measure.setEnabled(true);
				clicks = new Point[30];
				imageArea.repaint();
				
			}
			if(currentFileNumber >= filesList.length){
				nextImage.setEnabled(false);
			}
		}
	}
	
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent event)
		{
			if(event.getModifiers() == InputEvent.BUTTON3_MASK)
			{
				clicks = new Point[30];
				scrollPane.requestFocus();
			}
			
			else
			{
				for (int i = 0; i < 30; i++)
				{
					if(clicks[i] != null);
					else
					{
						clicks[i] = event.getPoint();
						break;
					}
				}
			}
		}
	}
	
	class Measure extends AbstractAction
	{
		private static final long serialVersionUID = 1L;
		
		//instance variables - start
		BufferedImage bw;
		double totalLength;
		//instance variables - end
		
		public void makeBWImage()
		{
			BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			bw = op.filter(imageArea.getImage(), null);
		}
		
		public double getLength()
		{
			return totalLength;
		}
		
		
		public void actionPerformed(ActionEvent event)
		{
			warning.setText("");
			imageArea.clearPointsMeasuredArrayList();
			WritableRaster imageRaster = bw.getRaster();
			totalLength = 0;
			int numberOfClicks = 0;
			while(clicks[numberOfClicks] != null)
				numberOfClicks++;
			
			if(numberOfClicks < 3)
			{			
				int i = 0;
				int threshold;
				

				if(currentMeasurement%2 == 0)
				{

					threshold = 150;
				}
				else
				{

					threshold = 90;
				}
				
				boolean continueLoop = true;
				begin:
				while(clicks[i+1]!= null && i < clicks.length - 1)
				{
					int xStart, yStart, yFin;
					if(clicks[i].y <= clicks[i+1].y){
						xStart = clicks[i].x;
						yStart = clicks[i].y;
						yFin = clicks[i+1].y;
					}
					else
					{
						xStart = clicks[i+1].x;
						yStart = clicks[i+1].y;
						yFin = clicks[i].y;
					}
					int xLeft;


					xLeft = xStart;
					
					//set xLeft to the left
					//start at the left end
					int counter1 = 0;
					if(imageRaster.getPixel(xLeft, yStart,(int[])null)[0] >=threshold) //move left till its black
					{
						while(imageRaster.getPixel(xLeft,yStart,(int[])null)[0] >= threshold && counter1 <= 20){
							xLeft--;
							counter1++;
						}
						
					}
		
					else //or move right till its white
					{
						while(imageRaster.getPixel(xLeft,yStart,(int[])null)[0] < threshold && counter1 <= 20){
							xLeft++;
							counter1++;
						}
						
					}	
					
					if(counter1 == 21){
						continueLoop = false;
						warning.setText("Sorry! Please use segmented measurement");
						break begin;
					}
					
					if(continueLoop)
					{int xLeft1 = xLeft;
					double distance = 0;
					int n = 10;
					int prevRow = yStart;
					
					for(int row = yStart; row <= yFin; row++)
					{
						System.out.println("calculating row: " + row);
						//start at the left end
						int counter;
						if(imageRaster.getPixel(xLeft1, row,(int[])null)[0] >=threshold) //move left till its black
						{
							counter = 0;
							while(imageRaster.getPixel(xLeft1,row,(int[])null)[0] >= threshold && counter <= 20){
								xLeft1--;
								counter++;						
							}
						}
						else //or move right till its white
						{
							counter = 0;
							while(imageRaster.getPixel(xLeft1,row,(int[])null)[0] < threshold && counter <= 20){
								xLeft1++;
								counter++;
							}
						}
						if(counter == 21)
						{
							warning.setText("Sorry! Please use segmented measurement");
							break begin;
						}
						
						if(row % n == 0){
							distance += euclideanDistance(xLeft1 - xLeft, n);
							imageArea.pointsMeasured.add(new Point(xLeft, prevRow));
							imageArea.pointsMeasured.add(new Point(xLeft1, row));
							xLeft = xLeft1;
							prevRow = row;
							
						}
						
					}
					
					totalLength += distance;
					i++;
						
					}
					
				}
			}
			else
			{
				for(int i = 0; i < clicks.length - 1 && clicks[i] != null; i++)
				{
					imageArea.pointsMeasured.add(clicks[i]);
					if(clicks[i+1] != null)
						totalLength += euclideanDistance(Math.abs(clicks[i].getX() - clicks[i+1].getX()), Math.abs(clicks[i].getY() - clicks[i+1].getY()));
				}
			}
			imageArea.repaint();
			clicks = new Point[30];
			currentMeasurement++;
			System.out.println(currentMeasurement);
			saveMeasurement();
		}
		
		public double euclideanDistance(double x, double y)
		{
			return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		}
		
		public void saveMeasurement()
		{
			if(currentMeasurement % 2 == 1){
				int calculation = (int)Math.floor(currentMeasurement / 2.0);
				resultsModel.setValueAt(totalLength/scale, calculation, 1 );
				youAreCurrentlyMeasuring.setText("The Root length for plant " + ((currentMeasurement == 1) ? 1:currentMeasurement/2) );
			}
			else
			{
				resultsModel.setValueAt(totalLength/scale, currentMeasurement / 2 - 1, 2 );
				if(currentMeasurement != 20)
					youAreCurrentlyMeasuring.setText("The Hypocotyl length for plant " + (currentMeasurement/2 + 1));
				else{
					youAreCurrentlyMeasuring.setText("You're done measuring this plant!");
					measure.setEnabled(false);
				}
			}
			
		}
	}
	
}

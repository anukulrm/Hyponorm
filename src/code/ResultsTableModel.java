package code;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.table.AbstractTableModel;

public class ResultsTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"Plant", "Hypocotyl", "Root", "Notes"};
	private Object[][] data;
	private static final int maxEntries = 10;
	private FileWriter fStream;
	private BufferedWriter writer;

	public ResultsTableModel()
	{
		super();
		data = new Object[10][4];
		setupData();
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public int getRowCount()
	{
		return maxEntries;
	}

	public String getColumnName(int col)
	{
        return columnNames[col];
    }

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return data[rowIndex][columnIndex];
	}

    public boolean isCellEditable(int row, int col) {
        if (col < 1)
            return false;
        else
            return true;
    }

    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

    public void clear()
    {
    	data = new Object[10][4];
    	this.fireTableDataChanged();
    	setupData();
    }

    public void setupData()
    {
    	for(int i = 0; i < 10; i++)
    	{
    		data[i][0] = i+1;
    	}
    }

    public boolean exportToCSV(File[] filesList, String savePath, int currentFile) throws IOException
    {
		fStream = new FileWriter(savePath, true);
		writer = new BufferedWriter(fStream);
		
    	for(int i = 0; i < 10; i++)
    	{
    		for (int j = 0; j<4; j++)
    		{
    			if(data[i][j] != null);
    			else
    				data[i][j] = "";
    		}
    	}
    	try
    	{
    		if(currentFile == 2)
    		{
    			writer.append("Image Name, Plant #, Hypocotyl Length, Root Length, Notes");
    			writer.newLine();
    		}
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    	if(currentFile != 1)
    	{
    		String fileName = filesList[currentFile - 1].getName();
        	for(int i = 0; i < 10; i++)
        	{
        		try
        		{
        			writer.append(fileName + ",");
        			writer.append(data[i][0] + ",");
        			writer.append(data[i][1] + ",");
        			writer.append(data[i][2] + ",");
        			writer.append(data[i][3] + ",");
        			writer.newLine();
        			writer.flush();
        		}
        		catch(IOException e)
        		{
        			e.printStackTrace();
        			return false;
        		}
        	}
        	try
    		{
    			writer.close();
    		}
    		catch (IOException e)
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			return false;
    		}	
    		return true;
    	}
		return true;
    	
    }
}

package MSGSP;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program
{
	public Parameters m_Parameters = null;;
	public SequenceCollection m_Sequences = null;
	
	
	
	public static void main(String[] args) throws IOException {
		if( args.length >= 3 )
		{
			String misFilePath = args[0];
			String dataFilePath = args[1];
			String outputPath = args[2];
	
			Program program = new Program();
			boolean success = program.Run( misFilePath, dataFilePath, outputPath );
			if (success){
				System.out.println("Finished successfully!");
			}
			else{
				System.out.println("Something is wrong!!");
			}
		}
		else
		{
			System.out.println( "Usage: MSGSP.Program <para path> <data path> <output path>" );
		}
	}

	
	
	public Program()
	{
	}
	
	
	public boolean Run( String misPath, String dataPath, String outputPath )
	{
		boolean bSuccess = false;
		
		m_Parameters = parseParaFile(misPath);
		m_Sequences = parseDataFile(dataPath);
		
		if( (m_Parameters != null) && (m_Sequences != null) )
		{
			MsGspAlgorithm algo = new MsGspAlgorithm( m_Parameters, m_Sequences );
			SequenceCollection patternCol = algo.Mine();
			if( patternCol != null )
			{			
				patternCol.Save( outputPath );
				bSuccess = true;
			}
		}
		
		return bSuccess;
	}
	
	
	private Parameters parseParaFile(String paraFilePath)
	{
		Parameters newPars = null;
		
		try
		{
			FileInputStream paraFileS = new FileInputStream(paraFilePath);
			BufferedReader paraFileBR = new BufferedReader(new InputStreamReader(paraFileS));
			newPars = new Parameters();
		 	String line = null;
		 	while ((line = paraFileBR.readLine()) != null) {
	//			System.out.println(line);
				if(line.contains("MIS")){
					Pattern p = Pattern.compile("MIS\\((\\d+)\\) = ([.\\d]+)");
					Matcher m = p.matcher(line);
					m.find();
					newPars.m_MisTable.addMIS(new Integer(m.group(1)), new Float(m.group(2)));
	//				System.out.println(m.group(1) + " " + m.group(2));
				}else if( line.contains( "SDC" ) ){
					Pattern p = Pattern.compile("SDC = ([.\\d]+)");
					Matcher m = p.matcher(line);
					m.find();
					newPars.setSDC(new Float(m.group(1)));
	//				System.out.println(m.group(1));
				}
				
		 	}
		 	paraFileBR.close();
		}
		catch( IOException e )
		{
			newPars = null;
		}
		
	 	return newPars;
	}
	
	
	
	private SequenceCollection parseDataFile(String dataFilePath)
	{
		SequenceCollection sequenceCol = null;
		
		try
		{
			FileInputStream dataFileS = new FileInputStream(dataFilePath);
			BufferedReader dataFileBR = new BufferedReader(new InputStreamReader(dataFileS));
	
		 	String line = null;
		 	sequenceCol = new SequenceCollection( m_Parameters.m_MisTable );
		 	while ((line = dataFileBR.readLine()) != null) {
				sequenceCol.AddSequenceFromString(line);
			}
		 
			dataFileBR.close();
		}
		catch( IOException e )
		{
			sequenceCol = null;
		}
		
		return sequenceCol;
	}

}

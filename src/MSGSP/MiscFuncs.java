package MSGSP;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class MiscFuncs
{

	public static List<String> ParseString( String str, char separater, boolean bTrim )
	{
		List<String> v = new ArrayList<String>();
		
		int iStartIndex = 0;
		while( (iStartIndex != -1) && (iStartIndex < str.length() ))
		{			
			int iEndIndex = -1;
			if( iEndIndex + 1 < str.length() )
			{
				iEndIndex = str.indexOf( separater, iStartIndex );
			}
			if( iEndIndex == -1 )
			{
				iEndIndex = str.length();
			}
			
			
			String s = "";
			if( iStartIndex != iEndIndex )
			{
				s = str.substring( iStartIndex, iEndIndex );
				v.add( bTrim ? s.trim() : s );
			}
			
			iStartIndex = iEndIndex + 1;
		}
		
		return v;
	}
	
	
	
	public static List<String> ParseString( String str, String separater, boolean bTrim )
	{
		List<String> v = new ArrayList<String>();
		
		int iStartIndex = 0;
		while( (iStartIndex != -1) && (iStartIndex < str.length() ))
		{			
			int iEndIndex = -1;
			if( iEndIndex + 1 < str.length() )
			{
				iEndIndex = str.indexOf( separater, iStartIndex );
			}
			if( iEndIndex == -1 )
			{
				iEndIndex = str.length();
			}
			
			
			String s = "";
			if( iStartIndex != iEndIndex )
			{
				s = str.substring( iStartIndex, iEndIndex );
				v.add( bTrim ? s.trim() : s );
			}
			
			iStartIndex = iEndIndex + separater.length();
		}
		
		return v;
	}
	
	
	
	// Parses a string given a separator character
	public static List<String> ParseString( String str, char separater )
	{
		return ParseString( str, separater, false );
	}
	
	
	

	public static String LoadFileAsString( String path )
	{
		FileInputStream inputStream = null;
		InputStreamReader streamReader = null;
		BufferedReader bufferedReader = null;

		StringBuilder fileText = new StringBuilder();
		
		try
		{
			inputStream = new FileInputStream( path );
			streamReader = new InputStreamReader( inputStream, "UTF8" );
			bufferedReader = new BufferedReader( streamReader );

			String line = "";
			while( (line = bufferedReader.readLine()) != null )
			{
				if( fileText.length() > 0 )
				{
					fileText.append( "\n" + line );	
				}
				else
				{
					fileText.append( line );
				}
			}
		}
		catch( Exception e )
		{
			fileText = null;
		}
		
		if( bufferedReader != null )	{ try { bufferedReader.close(); } catch (IOException e) {} }
		if( streamReader != null )		{ try { streamReader.close();	} catch (IOException e) {} }
		if( inputStream != null )		{ try { inputStream.close();	} catch (IOException e) {} }
	
		return fileText == null ? "" : fileText.toString();
	}


	public static boolean SaveFileAsString(String text, String path)
	{
		boolean bSuccess = false;
		PrintWriter out = null;
	
		try
		{
			out = new PrintWriter( path );
			out.println( text );
			bSuccess = true;
		}
		catch( Exception e )
		{
		}
		
		if( out != null )	{ out.close(); }
		
		return bSuccess;
	}


	
	public static List<String> BlockExtractor( String text, String[][] tagPairs )	{ return BlockExtractor( text, tagPairs, true ); }
	public static List<String> BlockExtractor( String text, String[][] tagPairs, boolean bRecursive )
	{
		List<String> retStrs = new ArrayList<String>();
		
		int iStartBlock = 0;
		int iIndex = 0;
		while( (iIndex != -1) && (iIndex < text.length()) )
		{
			// Get the index of the next interesting tag
			int iNextIndex = -1;
			int iPairIndex = -1;
			for( int i = 0; i < tagPairs.length; i++ )
			{
				int j = text.indexOf( tagPairs[i][0], iIndex );
				if( (iNextIndex == -1) || ((j != -1) && (j < iNextIndex)) )	{ iNextIndex = j;	iPairIndex = i; } 
			}
			iIndex = iNextIndex;
			
			
		
			if( iIndex == -1 )
			{
				// Nothing else of interest
				break;
			}
			else
			{
				// May need to skip some text
				// Find the appropriate closing tag
				int iMarkupEndIndex = -1;
				if( !bRecursive )
				{
					iMarkupEndIndex = text.indexOf( tagPairs[iPairIndex][1], iIndex );
					if( iMarkupEndIndex != -1)
					{
						iMarkupEndIndex += tagPairs[iPairIndex][1].length();
					}
				}
				else
				{
					int iDepth = 1;
					iMarkupEndIndex = iIndex + tagPairs[iPairIndex][0].length();

					while( (iDepth > 0) && (iMarkupEndIndex != -1) )
					{
						int iNextOpen = text.indexOf( tagPairs[iPairIndex][0], iMarkupEndIndex );	if( iNextOpen != -1) { iNextOpen += tagPairs[iPairIndex][0].length(); }
						int iNextClose = text.indexOf( tagPairs[iPairIndex][1], iMarkupEndIndex );	if( iNextClose != -1) { iNextClose += tagPairs[iPairIndex][1].length(); }
						
						if( iNextClose == -1 )
						{
							iMarkupEndIndex = -1;
						}
						else
						{
							if( (iNextOpen == -1) || (iNextClose < iNextOpen) )		{ iDepth--;	iMarkupEndIndex = iNextClose; }
							else	{ iDepth++;	iMarkupEndIndex = iNextOpen; }
						}
					}
					
					if( iDepth > 0 )
					{
						// Didn't find a closing tag
						iMarkupEndIndex = -1;
					}
				}
				
				if( iMarkupEndIndex != -1 )
				{
					// Get the block
					if( (iMarkupEndIndex - tagPairs[iPairIndex][1].length()) > (iIndex + tagPairs[iPairIndex][0].length()) )
					{
						String block = text.substring( iIndex + tagPairs[iPairIndex][0].length(), iMarkupEndIndex - tagPairs[iPairIndex][1].length() );
						retStrs.add( block );
					}
					iIndex = iStartBlock = iMarkupEndIndex;
				}
				else
				{
					// No end to this tag, ignore it
					iIndex++;
				}
			}			
		}
		
		return retStrs;
	}
	
}

	
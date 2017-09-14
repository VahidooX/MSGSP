package MSGSP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SequenceCollection {

	protected MisTable m_MisTable;
	
	public SequenceCollection( MisTable misTable )
	{
		m_MisTable = misTable;
	}
	
	
	public List<Sequence> m_Sequences = new ArrayList<Sequence>();

	
	public void AddSequenceWithoutDup(Sequence seq)
	{
		// Check if this already exists in the collection
		if( !ifContains(seq) )
		{
			m_Sequences.add( seq );
		}
	}
	
	public void AddSequenceWithDup(Sequence seq)
	{
		m_Sequences.add( seq );
	}

	public boolean AddSequenceFromString( String str )
	{
		AddSequenceWithDup( Sequence.CreateSequenceFromString( str, m_MisTable ) );
		
		return true;
	}

	
	
	// Saves this collect to file
	public void Save(String outputPath)
	{
		// Put sequences in length ordered map
		Map<Integer, List<Sequence>> orderedSeq = new HashMap<Integer, List<Sequence>>();
		for( Sequence sequence : m_Sequences )
		{
			int iSequenceLen = sequence.getLength();
			
			List<Sequence> bin = null;
			if( orderedSeq.containsKey( iSequenceLen ) )
			{
				bin = orderedSeq.get( iSequenceLen );
			}
			if( bin == null )
			{
				bin = new ArrayList<Sequence>();
				orderedSeq.put( iSequenceLen, bin );
			}
			
			bin.add( sequence );
		}	

		
		// Make a string of all patterns
		StringBuilder masterStr = new StringBuilder();
		for( Map.Entry<Integer, List<Sequence>> kvp : orderedSeq.entrySet() )
		{
			if( masterStr.length() > 0 ) { masterStr.append( '\n' ); }
			
			masterStr.append( "The number of length " + kvp.getKey() + " sequential patterns is " + kvp.getValue().size() + "\n" );
			
			for( Sequence s : kvp.getValue() )
			{
				masterStr.append( "Pattern:  " + s.toString() + "\n" );
			}
		}
		
		
		// All written to the string, commit to file 
		MiscFuncs.SaveFileAsString( masterStr.toString(), outputPath );
	}
	
	

	// Gets the count of each sequence that counts an item in the collection
	public Map<Integer,Integer> GetSupportCount(int[] sortedItemIds)
	{
		Map<Integer,Integer> ret = new HashMap<Integer,Integer>();
		
		for( int itemID : sortedItemIds )
		{
			ret.put(itemID, 0);
			for( Sequence sequence : m_Sequences )
			{
				if(sequence.ContainsItem(itemID))
				{
					ret.put( itemID, ret.get( itemID ) + 1 );
					continue;
				}
			}
		}
		
		return ret;
	}

	public int GetNumItemSets() {
		
		int iNumItemSets = 0;
		
		for( Sequence sequence : m_Sequences )
		{
			iNumItemSets += sequence.m_ItemSets.size();
		}
		
		return iNumItemSets;
	}

	
	
	// Add a collection into this one
	public void AddCollection(SequenceCollection sc)
	{
		for( Sequence s : sc.m_Sequences )
		{
			AddSequenceWithDup( s );
		}
	}

	
	
	public int GetNumSequences()
	{
		return m_Sequences.size();
	}

	
	public Sequence GetSequence(int i) {
		return m_Sequences.get( i );
	}

	
	
	public int GetSize() {
		return GetNumSequences();
	}
	
	
	public boolean ifContains(Sequence seq){
	
		for (int i = 0 ; i<m_Sequences.size() ; i++){
			if(m_Sequences.get(i).isEqual(seq)){
				return true;
			}
		}
		return false;
	}

	
	
	// Finds a sequence that matches the given one
	public Sequence FindSequence(Sequence s)
	{
		for( int i = 0; i < m_Sequences.size(); i++ )
		{
			if( m_Sequences.get(i).isEqual( s ) )
			{
				return m_Sequences.get(i);
			}
		}
		
		return null;
	}
		
}

package MSGSP;

import MSGSP.Sequence.CreateWithoutMinMisMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MsGspAlgorithm
{
	protected Parameters m_Parameters = null;
	protected SequenceCollection m_Sequences = null;
	
	
	
	
	public MsGspAlgorithm(Parameters parameters, SequenceCollection sequences)
	{
		m_Parameters = parameters;
		m_Sequences = sequences;
	}

	
	
	// Mines the current sequence with given parameters
	public SequenceCollection Mine()
	{
		List<SequenceCollection> m_FrequentSequences = new ArrayList<SequenceCollection>(); 
		
		int[] sortedItemIds = GetSortItemsByMis();
		
		
		// Get some properties of the original sequence set
		int iNumSequences = m_Sequences.GetNumSequences();
		Map<Integer, Integer> supportCount = m_Sequences.GetSupportCount(sortedItemIds);
		int iSizeOfSequences = m_Sequences.GetSize();
		
		
		
		// Do some setup
		List<Integer> L = InitPass( sortedItemIds,  supportCount);
		SequenceCollection freqLv1 = GetLv1FrequentSequences( L, iNumSequences, supportCount );
		m_FrequentSequences.add( freqLv1 );
		

		
		for( int k = 1; m_FrequentSequences.get( k - 1 ).GetNumSequences() > 0; k++ )
		{
			// Generate some frequent sequences for this level
			SequenceCollection freqSeqs = new SequenceCollection( m_Parameters.m_MisTable );
			SequenceCollection candidateSeqs = null;
			
			
			// Get a collection of candidate sequences for this level
			if( k == 1 )
			{
				candidateSeqs = Lv2CandidateGenSPM( L, supportCount, iNumSequences );
			}
			else
			{
				candidateSeqs = MsCandidateGenSPM( m_FrequentSequences.get( k - 1 ), iNumSequences, supportCount );
			}
			
			
			
			// Remove infrequent items
			for( Sequence s : m_Sequences.m_Sequences )
			{
				for( Sequence c : candidateSeqs.m_Sequences )
				{
					if( s.ContainsSequence( c ) )
					{
						c.m_iCount++;
					}


					List<Sequence> minMisReducedSequences = c.CreateSequencesWithoutMinMisItem( sortedItemIds, CreateWithoutMinMisMethod.Cwm_RemoveFirstOccuranceOnly );
					for( Sequence minMisReducedSequence : minMisReducedSequences )
					{
						if( s.ContainsSequence( minMisReducedSequence ) )
						{
							// Need to increment the count for the sequence that matches exactly minMinRedSeq
							// Find all matching sequences
							SequenceCollection seqLevel = m_FrequentSequences.get( minMisReducedSequence.getLength() - 1 );
							Sequence matchingSequence = seqLevel.FindSequence( minMisReducedSequence );
							if( matchingSequence != null )
							{
								matchingSequence.m_iCount++;
							}
						}
					}
				}
			}
			
			
			
			// Add appropriate candidates into the frequent collection
			for( Sequence c : candidateSeqs.m_Sequences )
			{
				//System.out.println(c + ":   " + (float)c.m_iCount / (float)iSizeOfSequences);
				if( ((float)c.m_iCount / (float)iSizeOfSequences) >= m_Parameters.m_MisTable.getMIS( c.GetMinMisItem( sortedItemIds ) ) )
				{
					freqSeqs.AddSequenceWithoutDup( c );
				}
			}
			
			
			m_FrequentSequences.add( freqSeqs );
		}
			
			
		
		// Union all frequent sequences into a total collection
		SequenceCollection totalCollections = new SequenceCollection( m_Parameters.m_MisTable );
		for( SequenceCollection sc : m_FrequentSequences )
		{
			totalCollections.AddCollection( sc );
		}
		
		return totalCollections;
	}
	
	
	
	private SequenceCollection MsCandidateGenSPM( SequenceCollection sequenceCol, int iNumSequences, Map<Integer,Integer> supportCount)
	{
		SequenceCollection nextCol = MSCandidateGenSPM.join( sequenceCol, m_Parameters.m_MisTable );
		MSCandidateGenSPM.prune( nextCol,  sequenceCol, m_Parameters.m_MisTable, m_Parameters.SDC, iNumSequences, supportCount );
		
		return nextCol;
	}



	// Generates a candidate frequent set for level 2
	private SequenceCollection Lv2CandidateGenSPM( List<Integer> L, Map<Integer, Integer> supportCount, int n )
	{
		SequenceCollection candCol = new SequenceCollection( m_Parameters.m_MisTable );
		
		
		// Go through all items (L) which is sorted in MIS order
		for( int i = 0; i < L.size(); i++ )
		{
			int l = L.get(i);
			float fSup_l = ((float)supportCount.get( l )) / (float)n;
			
			if( fSup_l >= m_Parameters.m_MisTable.getMIS( l ) )
			{
				for( int j = i + 1; j < L.size(); j++ )
				{
					int h = L.get(j);
					float fSup_h = ((float)supportCount.get( h )) / (float)n;
					if( ( fSup_h >= m_Parameters.m_MisTable.getMIS( l )) &&
						(Math.abs( fSup_h - fSup_l ) <= m_Parameters.SDC) )
					{
						// Add <{x, y}>
						Sequence s = new Sequence();
						ItemSet is = new ItemSet( m_Parameters.m_MisTable );
						is.addItem( l );
						is.addItem( h );
						s.m_ItemSets.add( is );
						candCol.AddSequenceWithoutDup( s );
						
						// Add <{x}, {y}>
						s = new Sequence();
						s.m_ItemSets.add( new ItemSet( l, m_Parameters.m_MisTable ) );
						s.m_ItemSets.add( new ItemSet( h, m_Parameters.m_MisTable ) );
						candCol.AddSequenceWithoutDup( s );
						
						// Add <{y}, {x}>
						s = new Sequence();
						s.m_ItemSets.add( new ItemSet( h, m_Parameters.m_MisTable ) );
						s.m_ItemSets.add( new ItemSet( l, m_Parameters.m_MisTable ) );
						candCol.AddSequenceWithoutDup( s );
						
						// Add <{x}, {x}>
						s = new Sequence();
						s.m_ItemSets.add( new ItemSet( l, m_Parameters.m_MisTable ) );
						s.m_ItemSets.add( new ItemSet( l, m_Parameters.m_MisTable ) );
						candCol.AddSequenceWithoutDup( s );
					}
				}
			}
		}
		
		return candCol;
	}



	// Gets the first level of frequent item sets
	protected SequenceCollection GetLv1FrequentSequences( List<Integer> L, int iNumSequences, Map<Integer, Integer> supportCount )
	{
		SequenceCollection freq = new SequenceCollection( m_Parameters.m_MisTable );
		
		for( int iItemId : L )
		{
			int iItemSupportCount = supportCount.get( iItemId );
			float fItemMis = m_Parameters.m_MisTable.getMIS( iItemId );
			if( ((float)iItemSupportCount / (float)iNumSequences) >= fItemMis )
			{
				Sequence s = new Sequence();
				s.m_ItemSets.add( new ItemSet( iItemId, m_Parameters.m_MisTable ) );
				s.m_iCount = iItemSupportCount;
				freq.AddSequenceWithoutDup( s );
			}
		}
		
		return freq;
	}
	

	
	
	
	// Generates seeds from the original sequences
	protected List<Integer> InitPass( int[] sortedItemIds, Map<Integer, Integer> supportCount )
	{
		List<Integer> L = new ArrayList<Integer>();
		
		int iNumSequences = m_Sequences.GetNumSequences();
		
		int iFirstAcceptedItem = -1;
		
		for( int i = 0; i < sortedItemIds.length; i++ )
		{
			int iItemId = sortedItemIds[i];
			
			if( ((float)supportCount.get( iItemId ) / (float)iNumSequences) >= m_Parameters.m_MisTable.getMIS( iFirstAcceptedItem == -1 ? iItemId : iFirstAcceptedItem ) )
			{
				if( iFirstAcceptedItem == -1 )
				{
					iFirstAcceptedItem = iItemId;
				}
				L.add( iItemId );
			}
		}
		
		return L;
	}
	
	
	
	// Sorts all item ids by their MIS levels
	protected int[] GetSortItemsByMis()
	{
		int[] sortedIds = null;
		
		// Pull all info from the MIS table
		List<Pair<Integer, Float>> pairs = new ArrayList<Pair<Integer, Float>>();
		for( Entry<Integer, Float> e : m_Parameters.m_MisTable.m_Table.entrySet() )
		{
			pairs.add( new Pair<Integer, Float>( e.getKey(), e.getValue() ) );
		}
		
		
		// Start sorting by second
		int i = 0;
		while( i < pairs.size() - 1 )
		{
			if( pairs.get(i).GetSecond() > pairs.get(i + 1).GetSecond() )
			{
				// Swap
				Pair<Integer, Float> temp = pairs.get(i);
				pairs.set(i, pairs.get(i + 1) );
				pairs.set(i + 1, temp );
				i--;
			}
			else
			{
				i++;
			}
			if( i < 0 ) { i = 0; }
		}
		
		
		// Make an array of item ids out of this
		sortedIds = new int[pairs.size()];
		for( i = 0; i < pairs.size(); i++ )
		{
			sortedIds[i] = pairs.get(i).GetFirst();
		}
		
		return sortedIds;
	}
	
	

}

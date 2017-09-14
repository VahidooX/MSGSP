package MSGSP;

import java.util.*;

public class Sequence {
	public List <MSGSP.ItemSet> m_ItemSets;
	public int m_iCount = 0;
	
	public Sequence(){
		m_ItemSets= new ArrayList<ItemSet>();
	}
	
	public Sequence( List<ItemSet> is ){
		m_ItemSets = is;
	}
	
	public void addItemSetFirst(ItemSet is){
		m_ItemSets.add(0, is); 
	}

	public void addItemSetLast(ItemSet is){
		m_ItemSets.add(is);
	}
	
	public int getFirstItem(){
		return m_ItemSets.get(0).GetItemAtIndex(0);
	}

	
	
	public static Sequence CreateSequenceFromString( String str, MisTable misTable )
	{
		String[][] tagPairs = {{"{","}"}};
		
		List<String> itemSetsStr = MiscFuncs.BlockExtractor(str,tagPairs,true);
		Sequence seq = new Sequence();
		for(String itemSetStr:itemSetsStr){
			List<String> idStrs = MiscFuncs.ParseString(itemSetStr, ",", true);
			LinkedList<Integer> itemIds = new LinkedList<Integer>();
			for( int i = 0; i < idStrs.size(); i++ )
			{
				itemIds.add( Integer.parseInt( idStrs.get(i) ) );
			}
			ItemSet itemSet = new ItemSet( itemIds, misTable );
			seq.addItemSetLast(itemSet);
		}

		return seq;
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( '<' );
		for( int i = 0; i < m_ItemSets.size(); i++ )
		{
			sb.append( m_ItemSets.get(i).toString() );
		}
		
		sb.append( ">  Count:  " + m_iCount );
		
		return sb.toString();
	}

	
	
	// Gets the size of this sequence
	public int GetSize()
	{
		return m_ItemSets.size();
	}



	// Returns true if this sequence contains the given candidate
	public boolean ContainsSequence( Sequence c )
	{
		
		for( int i = 0; i < m_ItemSets.size() - c.m_ItemSets.size() + 1; i++ )
		{
			// Start searching if this contains the start of the sequence c
			if( m_ItemSets.get( i ).ContainsItems( c.m_ItemSets.get( 0 ) ) == ItemSet.ContainsRes.Cr_Yes )
			{
				if( c.m_ItemSets.size() <= 1 )
				{
					// Found the whole sequence
					return true;
				}
				
				// Check for the rest of the sequence
				int iSubIndex = 1;
				int iMainIndex = i + 1;
				while( iMainIndex < m_ItemSets.size() )
				{
					if( m_ItemSets.get(iMainIndex).ContainsItems( c.m_ItemSets.get( iSubIndex ) ) == ItemSet.ContainsRes.Cr_Yes )
					{
						// Found the next component
						iSubIndex++;
						if( iSubIndex >= c.m_ItemSets.size() )
						{
							// Found the whole sequence
							return true;
						}
					}
					iMainIndex++;
				}
			}
		}
		
		return false;
	}

	
	
	// Returns true if this sequence contains an item
	public boolean ContainsItem( int iItemId )
	{
		for( ItemSet is : m_ItemSets )
		{
			if( is.ContainsItem( iItemId ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	public double getMinMaxSupprtDiff( Map<Integer,Integer> supportCount, int iNumSequences ){
		double minS = Double.POSITIVE_INFINITY;
		double maxS = Double.NEGATIVE_INFINITY;
		for(int i=0 ; i<m_ItemSets.size() ; i++){
			ArrayList<Integer> itemsList = new ArrayList<Integer>();
			itemsList.addAll(m_ItemSets.get(i).GetItems());
			for(int j=0 ;j<itemsList.size();j++){
				double num = supportCount.get(itemsList.get(j));
				if( num < minS)
				{
					minS = num;
				}
				if( num > maxS)
				{
					maxS = num;
				}
			}
		}

		return Math.abs(maxS-minS)/(double)iNumSequences;
		
	}
	
	
	// Gets the minimum MIS item
	public int GetMinMisItem( int[] itemMisOrder )
	{
		for( int iOrderedItemId : itemMisOrder )
		{
			for( ItemSet itemSet : m_ItemSets )
			{
				if( itemSet.ContainsItem( iOrderedItemId ) )
				{
					return iOrderedItemId;
				}
			}
		}
		
		return -1;
	}

	
	
	public enum CreateWithoutMinMisMethod
	{
		Cwm_SingleRemove_MultipleSequences,
		Cwm_RemoveFirstOccuranceOnly,
		Cwm_RemoveAllOccurances,
	}
	
	// Create a list of sequences that are made of
	// this without an occurrence of the min MIS item
	public List<Sequence> CreateSequencesWithoutMinMisItem(int[] itemMisOrder, CreateWithoutMinMisMethod method)
	{
		List<Sequence> seqs = new ArrayList<Sequence>();
		int iMinMisItem = GetMinMisItem( itemMisOrder );
		
		if( method != CreateWithoutMinMisMethod.Cwm_SingleRemove_MultipleSequences )
		{
			// Make a new sequence for each removal
			for( int i = 0; i < m_ItemSets.size(); i++ )
			{
				if( m_ItemSets.get( i ).ContainsItem( iMinMisItem ) )
				{
					// Create a sequence with this item set having a removed item
					Sequence s = new Sequence();
					for( int j = 0; j < m_ItemSets.size(); j++ )
					{
						ItemSet newSet = m_ItemSets.get( j ).Copy();
						if( i == j )
						{
							newSet.RemoveItemId( iMinMisItem );
						}
						if( !newSet.IsEmpty() )
						{
							s.addItemSetLast( newSet );
						}
					}
				}
			}
		}
		else
		{
			// Make only one modified sequence
			Sequence s = getClone();
			for( int j = 0; j < s.m_ItemSets.size(); j++ )
			{
				ItemSet is = s.m_ItemSets.get(j);
				if( is.ContainsItem( iMinMisItem ) )
				{
					is.RemoveItemId( iMinMisItem );
					if( !is.IsEmpty() )
					{
						s.m_ItemSets.remove( j-- );
					}
					
					if( method == CreateWithoutMinMisMethod.Cwm_RemoveFirstOccuranceOnly )
					{
						break;
					}
				}

			}
			
			seqs.add( s );
		}
			
		
		return seqs;
	}
	
	
	
	public int getExplicitMinMIS( MisTable misTable ){
		int minCount=0;
		double minMIS = Double.POSITIVE_INFINITY;
		int minIndex = -1;
		int k = 0;
		for(int i=0 ; i<m_ItemSets.size() ; i++){
			ArrayList<Integer> itemsList = new ArrayList<Integer>();
			itemsList.addAll(m_ItemSets.get(i).GetItems());
			for(int j=0 ;j<itemsList.size();j++){
				if( misTable.getMIS( itemsList.get(j) ) < minMIS)
				{
					minMIS = misTable.getMIS( itemsList.get(j) );
					minCount = 1;
					minIndex = k;
				}
				else
				{
					if( misTable.getMIS( itemsList.get(j) ) == minMIS )
					{
						minCount = minCount + 1;
					}
				}
				k++;
			}
		}

		if(minCount==1)
			return minIndex;
		else
			return -1;

	}	
	

	public double getMinMIS( MisTable misTable ){
		double minMIS = Double.POSITIVE_INFINITY;
		for(int i=0 ; i<m_ItemSets.size() ; i++){
			ArrayList<Integer> itemsList = new ArrayList<Integer>();
			itemsList.addAll(m_ItemSets.get(i).GetItems());
			for(int j=0 ;j<itemsList.size();j++){
				if( misTable.getMIS( itemsList.get(j) ) < minMIS)
				{
					minMIS = misTable.getMIS( itemsList.get(j) );
				}
			}
		}

		return minMIS;
	}	


	public boolean isEqual(Sequence seq){
		if(this.getSize() != seq.getSize() || this.getLength() != seq.getLength())
			return false;
		
		Iterator<ItemSet> s1Iter = this.m_ItemSets.iterator(); 
		Iterator<ItemSet> s2Iter = seq.m_ItemSets.iterator();
		
		while(s1Iter.hasNext()){
			ItemSet s1ItemSet = s1Iter.next();
			ItemSet s2ItemSet = s2Iter.next();
			if(!s1ItemSet.equals(s2ItemSet))
				return false;
		}
		return true;
	}
	
	
	
	// isEquals two sequences with an ignore index option
	public boolean isEqual( Sequence s2, int iIgnore1, int iIgnore2 )
	{

		if (this.getLength() != s2.getLength())
		{
			return false;
		}
		
		Sequence newS1 = this.getClone();
		Sequence newS2 = s2.getClone();
		
		newS1.RemoveItemByIndex(iIgnore1);
		newS2.RemoveItemByIndex(iIgnore2);
		
		return newS1.isEqual(newS2);
		
	}
	
	
	
	public int getLength(){
		int sum = 0;
		for(ItemSet is : m_ItemSets){
			sum = sum + is.getLength();
		}
		return sum;
	}
	
	public int getSize(){
		return m_ItemSets.size();
	}
	


	
	public int getLastItem(){
		return m_ItemSets.get(m_ItemSets.size()-1).last();
	}
	
	public boolean isFirstItemSmallestMIS( MisTable misTable ){
		int minIndex = getExplicitMinMIS(misTable);
		if(minIndex==0)
			return true;
		return false;
		
	}
	
	public boolean isLastItemSmallestMIS( MisTable misTable ){
		int minIndex = getExplicitMinMIS(misTable);
		if(minIndex==getLength()-1)
			return true;
		return false;
	}
	
	
	
	public Sequence getClone(){
		ArrayList<ItemSet> newItemSets = new ArrayList<ItemSet>();
		for(ItemSet it : m_ItemSets){
			newItemSets.add(it.Copy() );
		}

		Sequence newSeq = new Sequence(newItemSets);
		return newSeq;
	}
	
	
	
	// Removes an item from the sequence by absolute index
	public void RemoveItemByIndex( int iIndex )
	{
		int iBlockIndex = 0;
		for(ItemSet it : m_ItemSets)
		{
			int iItemSetLen = it.getLength();
			if( iIndex < iBlockIndex + iItemSetLen )
			{
				it.RemoveItemByIndex( iIndex - iBlockIndex );
				break;
			}
			else
			{
				iBlockIndex += iItemSetLen;
			}
		}
		
		RemoveEmptyItemSets();
	}
	
	
	
	// Removes any empty item sets from the sequence
	public void RemoveEmptyItemSets()
	{
		for( int i = 0; i < m_ItemSets.size(); i++ )
		{
			if( m_ItemSets.get( i ).getLength() == 0 )
			{
				m_ItemSets.remove( i-- );
			}
		}
	}

}

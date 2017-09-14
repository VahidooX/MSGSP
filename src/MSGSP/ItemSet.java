package MSGSP;

import java.util.*;

public class ItemSet {
	private LinkedList<Integer> m_ItemIds = new LinkedList<Integer>();
	
	private MisTable m_MisTable = null;
	
	public enum ContainsRes
	{
		Cr_No,
		Cr_Partial,
		Cr_Yes,
	}
	
	
	public ItemSet( MisTable misTable )
	{
		m_MisTable = misTable;
	}
	
	
	public ItemSet(LinkedList<Integer> itemIds, MisTable misTable)
	{
		this( misTable );
		m_ItemIds = itemIds;
	}

	public ItemSet( int iItemId, MisTable misTable )
	{
		this( misTable );
		m_ItemIds.add( iItemId );
	}
	
	
	
	public void addItem( int iItemId )
	{
		// Has to be put in MIS order
		ListIterator<Integer> listIterator = m_ItemIds.listIterator();
		int i = 0;
        while (listIterator.hasNext())
        {
        	int iListItemId = listIterator.next();
            if( m_MisTable.getMIS( iItemId ) < m_MisTable.getMIS( iListItemId ) )
            {
            	m_ItemIds.add(i, iItemId);
            	return;
            }
            i++;
        }
        
        // Add to the end
		m_ItemIds.add( iItemId );
	}
	
	
	// Takes the last item out of the item list
	public int pollLast()
	{
		int iRet = 0;
		
		if( m_ItemIds.size() > 0 )
		{
			iRet = m_ItemIds.get( m_ItemIds.size() - 1 );
			m_ItemIds.remove( m_ItemIds.size() - 1 );
		}
		
		return iRet;
	}
	
	
	// Takes the first item out of the item list
	public int pollFirst()
	{
		int iRet = 0;
		
		if( m_ItemIds.size() > 0 )
		{
			iRet = m_ItemIds.get( 0 );
			m_ItemIds.remove( 0 );
		}
		
		return iRet;
	}
	
	
	public int last()
	{
		if( m_ItemIds.size() > 0 )
		{
			return m_ItemIds.get( m_ItemIds.size() - 1 );
		}
		return 0;
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( '{' );
		for( int i = 0; i < m_ItemIds.size(); i++ )
		{
			sb.append( m_ItemIds.get(i) );
			if( i < m_ItemIds.size() - 1 )		{ sb.append( ',' ); }
		}
		sb.append( '}' );
		
		return sb.toString();
	}


	public int getLength() {
		return m_ItemIds.size();
	}

	
	
	// Sort by the MIS
	public void SortItemsByMis(MisTable misTable)
	{
		int i = 0;
		while( i < m_ItemIds.size() - 1 )
		{
			if( misTable.getMIS( m_ItemIds.get(i) ) > misTable.getMIS( m_ItemIds.get(i + 1) ) )
			{
				// Swap
				int iTemp = m_ItemIds.get(i);
				m_ItemIds.set(i, m_ItemIds.get(i + 1) );
				m_ItemIds.set(i + 1, iTemp );
				i--;
			}
			else
			{
				i++;
			}
			if( i < 0 ) { i = 0; }
		}
			
		
	}


	
	// Returns true if this item set contains an item id
	public boolean ContainsItem( int iItemId )
	{
		for( int i : m_ItemIds )
		{
			if( i == iItemId )
			{
				return true;
			}
		}
		return false;
	}


	
	// Copy of this item set
	public ItemSet Copy()
	{
		ItemSet copy = new ItemSet( m_MisTable );
		
		for( int i = 0; i < m_ItemIds.size(); i++ )
		{
			copy.m_ItemIds.add( m_ItemIds.get( i ) );
		}
		
		return copy;
	}


	
	// Removes an item from the set
	public void RemoveItemId(int iItemId )
	{
		for( int i = 0; i < m_ItemIds.size(); i++ )
		{
			if( m_ItemIds.get(i) == iItemId )
			{
				m_ItemIds.remove( i );
				break;
			}
		}
	}
	
	
	
	public boolean equals(ItemSet is2){
		if(m_ItemIds.size()!=is2.m_ItemIds.size())
			return false;
		for( int i = 0; i < m_ItemIds.size(); i++ )
		{
			if( m_ItemIds.get(i).intValue() != is2.m_ItemIds.get(i).intValue() )
			{
				return false;
			}
		}
		return true;
	}
	
	
	
	// Returns true if this item set contains the given item set
	public ContainsRes ContainsItems( ItemSet is )
	{
		int iMatchingNums = 0;
		
		for( int i = 0; i < m_ItemIds.size(); i++ )
		{
			for( int j = 0; j < is.m_ItemIds.size(); j++ )
			{
				if( m_ItemIds.get( i ).intValue() == is.m_ItemIds.get( j ).intValue() )
				{
					iMatchingNums++;
					break;
				}
			}
		}
		
		if( iMatchingNums == is.m_ItemIds.size() ) { return ContainsRes.Cr_Yes; }
		else if( iMatchingNums != 0 ) { return ContainsRes.Cr_Partial; }
		
		return ContainsRes.Cr_No;
	}


	public List<Integer> GetItems() {
		return m_ItemIds;
	}


	public int GetItemAtIndex(int i)
	{
		return m_ItemIds.get( i );
	}


	
	// Removes an item at the given index
	public void RemoveItemByIndex(int iIndex )
	{
		if( iIndex < m_ItemIds.size() )
		{
			m_ItemIds.remove( iIndex );
		}
	}


	
	// Returns true if there are no items in this item set
	public boolean IsEmpty()
	{
		return m_ItemIds.size() == 0;
	}


	
	// Compares two item sets with an ignore index
	public boolean isEqual(ItemSet is2, int iIgnoreIndex1, int iIgnoreIndex2)
	{
		ItemSet is1 = this;
		
		if( (is1.getLength() - (iIgnoreIndex1 != -1 ? 1 : 0)) != (is2.getLength() - (iIgnoreIndex2 != -1 ? 1 : 0)) )
		{
			return false;
		}
		
		int iPtr1 = (iIgnoreIndex1 == 0 ? 1 : 0);
		int iPtr2 = (iIgnoreIndex2 == 0 ? 1 : 0);
		
		while( iPtr1 < is1.m_ItemIds.size() )
		{
			if( is1.m_ItemIds.get( iPtr1 ).intValue() != is2.m_ItemIds.get( iPtr2 ).intValue() )
			{
				return false;
			}
			
			iPtr1++;
			iPtr2++;
			if( iPtr1 == iIgnoreIndex1 ) { iPtr1++; }
			if( iPtr2 == iIgnoreIndex2 ) { iPtr2++; }
		}
		
		return true;
	}
}

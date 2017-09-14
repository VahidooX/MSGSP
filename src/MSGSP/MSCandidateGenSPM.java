package MSGSP;

import java.util.*;

public class MSCandidateGenSPM {

	public static void prune(SequenceCollection F, SequenceCollection prevF, MisTable misTable, double SDC, int iNumSequences, Map<Integer,Integer> supportCount ){
		for(int i = 0 ; i<F.m_Sequences.size() ; i++)
		{
			Sequence seq  = F.m_Sequences.get(i);
			
			double diff = seq.getMinMaxSupprtDiff(supportCount, iNumSequences);

			if( diff > SDC )
			{
				F.m_Sequences.remove(i);
				i--;
				continue;
			}
			int minIndex = seq.getExplicitMinMIS( misTable );
			for(int j = 0 ; j < seq.getLength() ; j++)
			{
				if(j==minIndex)
					continue;
				Sequence testSeq = seq.getClone();
				testSeq.RemoveItemByIndex(j);
				if(!prevF.ifContains(testSeq))
				{
					F.m_Sequences.remove(i);
					i--;
					break;
				}
			}
		}
	}
	
	
	public static SequenceCollection join( SequenceCollection F, MisTable misTable ){
		SequenceCollection newFS = new SequenceCollection( misTable );
		for(int i = 0 ; i<F.m_Sequences.size() ; i++){
			for(int j = 0 ; j<F.m_Sequences.size() ; j++){
				Sequence s1 = F.m_Sequences.get(i);
				Sequence s2 = F.m_Sequences.get(j);
				boolean isS1FirstItemSmallest = s1.isFirstItemSmallestMIS( misTable );
				boolean isS2LastItemSmallest = s2.isLastItemSmallestMIS( misTable );
				
				
				if(isS1FirstItemSmallest && misTable.getMIS( s2.getLastItem() ) > misTable.getMIS( s1.getFirstItem() ) && s1.isEqual(s2,1,s2.getLength()-1)){
//					if(misTable.getMIS( s2.getLastItem() ) > misTable.getMIS( s1.getFirstItem() ) && s1.isEqual(s2,1,s2.getLength()-1)){
						int l = s2.getLastItem();
						if(s2.m_ItemSets.get(s2.m_ItemSets.size()-1).getLength()==1){
							Sequence c1 = s1.getClone();
							ItemSet lItemSet = new ItemSet( misTable );
							lItemSet.addItem(l);
							c1.addItemSetLast(lItemSet);
							newFS.AddSequenceWithoutDup(c1);

							if(s1.getLength()==2 && s1.getSize()==2 && misTable.getMIS( s2.getLastItem() ) > misTable.getMIS( s1.getLastItem() ) ){
								Sequence c2 = s1.getClone();
								c2.m_ItemSets.get(c2.m_ItemSets.size()-1).addItem(l);
								newFS.AddSequenceWithoutDup(c2);
							}
						}else{
							if((s1.getLength()==2 && s1.getSize()==1 && misTable.getMIS( s2.getLastItem() ) > misTable.getMIS( s1.getLastItem() )) || s1.getLength()>2){
								Sequence c2 = s1.getClone();
								c2.m_ItemSets.get(c2.m_ItemSets.size()-1).addItem(l);
								newFS.AddSequenceWithoutDup(c2);
							}
						}
					//}
				}else
				{
					if(isS2LastItemSmallest && misTable.getMIS( s1.getFirstItem() ) > misTable.getMIS( s2.getLastItem() ) && s2.isEqual(s1,s2.getLength()-2,0)){
						/////////reverse
//						if(misTable.getMIS( s1.getFirstItem() ) > misTable.getMIS( s2.getLastItem() ) && s2.isEqual(s1,s2.getLength()-2,0)){
							int l = s1.getFirstItem();
							if(s1.m_ItemSets.get(0).getLength()==1){
								Sequence c1 = s2.getClone();
								ItemSet lItemSet = new ItemSet( misTable );
								lItemSet.addItem(l);
								c1.addItemSetFirst(lItemSet);
								newFS.AddSequenceWithoutDup(c1);

								if(s2.getLength()==2 && s2.getSize()==2 && misTable.getMIS( s1.getFirstItem() ) > misTable.getMIS( s2.getFirstItem() ) ){
									Sequence c2 = s2.getClone();
									c2.m_ItemSets.get(0).addItem(l);
									newFS.AddSequenceWithoutDup(c2);
								}
							}else{
								if((s2.getLength()==2 && s2.getSize()==1 && misTable.getMIS( s1.getFirstItem() ) > misTable.getMIS( s2.getFirstItem() )) || s2.getLength()>2){
									Sequence c2 = s2.getClone();
									c2.m_ItemSets.get(0).addItem(l);
									newFS.AddSequenceWithoutDup(c2);
								}
							}
						//}
					
					
					
					}else{
						if(s1.isEqual(s2,0,s2.getLength()-1))
						{
							Sequence c1 = s1.getClone();
							if(s2.m_ItemSets.get(s2.m_ItemSets.size()-1).getLength()==1){
								ItemSet newItemSet = new ItemSet( misTable );
								newItemSet.addItem(s2.getLastItem());
								c1.addItemSetLast(newItemSet);
								newFS.AddSequenceWithoutDup(c1);
							}else
							{
								c1.m_ItemSets.get(c1.m_ItemSets.size()-1).addItem(s2.getLastItem());
								newFS.AddSequenceWithoutDup(c1);
							}
						}
						
					}
				}

				
			}
		}
		return newFS;
	}
}

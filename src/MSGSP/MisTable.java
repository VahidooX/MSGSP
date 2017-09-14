package MSGSP;

import java.util.HashMap;
import java.util.Map;

public class MisTable {

	protected Map<Integer, Float> m_Table = new HashMap<Integer, Float>();
	
	public float getMIS(Integer key){
		return m_Table.get(key);
	}

	public void addMIS(int key, float MISValue){
		m_Table.put(key, MISValue);
	}
}

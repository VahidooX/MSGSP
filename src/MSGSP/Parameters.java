package MSGSP;

public class Parameters {

	public MisTable m_MisTable = null;
	float SDC = 0.0f;
	
	public Parameters(){
		m_MisTable = new MisTable();
	}

	public void setSDC(float SDCTh){
		SDC = SDCTh;
	}

}

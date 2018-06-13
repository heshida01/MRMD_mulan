package datamining.xmu.edu.cn;

public class getLabel implements Runnable
{
	private  int insNum;
	private  int feaNum;
	private  int labNum;
	private String [][] labelData;
	
	//modify
	private String inputData[][];
	
	public void setInsNum(int insNum)
	{
		this.insNum = insNum;
	}
	
	public getLabel(String[][] inputData) {
		super();
		this.inputData = inputData;
	}

	public void setFeaNum(int feaNum)
	{
		this.feaNum = feaNum;
	}
	
	public void setLabNum(int labNum)
	{
		this.labNum = labNum;
	}
	
	public void setData(String[][] labelData)
	{
		this.labelData = labelData;
	}
	
	
	//public String [][] run(String inputData[][])
	public void run()
	{
		
		for(int i = 0; i < insNum; i ++)
		{
			for(int j = 0; j < labNum; j ++)
			{
				labelData[i][j] = inputData[i][j + feaNum];
			}
		}
		//return labelData;
	}
}

package datamining.xmu.edu.cn;

public class getFeaData implements Runnable
{
	private  int insNum;
	private  int feaNum;
	private  int labNum;
	private double [][] feaData;
	private String inputData[][];
	/**
	 * @param inputPath
	 */
	public getFeaData(String inputData[][]) {
		super();
		this.inputData = inputData;
	}
	public void setInsNum(int insNum)
	{
		this.insNum = insNum;
	}
	
	public void setFeaNum(int feaNum)
	{
		this.feaNum = feaNum;
	}
	
	public void setLabNum(int labNum)
	{
		this.labNum = labNum;
	}
	
	public void setData(double[][] feaData)
	{
		this.feaData = feaData;
	}
	
	//public double [][] run(String inputData[][])
	public void run()
	{
	
		for(int i = 0; i < insNum; i ++)
		{
			
			for(int j = 0; j < feaNum; j ++)
			{
				feaData[i][j] = Double.parseDouble(inputData[i][j]);
			}
		}
		//return feaData;
	}
}

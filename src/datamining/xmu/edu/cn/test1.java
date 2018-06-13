package datamining.xmu.edu.cn;

class num1 implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
   			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		
   		System.out.println('1'+Thread.currentThread().getName());
       }
		
	
	
	
}
class num2 implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
       
   		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		
   		
   		System.out.println('2'+Thread.currentThread().getName());
        String ssString;
        ssString=Thread.currentThread().getName();
        if(ssString=="pool-2-thread-2")
        	System.out.println("hhhhhhhhhhhhhhhhh");
		
	}
	
}	

class num3 implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
       
   			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   			System.out.println('3');
   		System.out.println('3'+Thread.currentThread().getName().length());
      }
		
	
	
	
}
public class test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package datamining.xmu.edu.cn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.lang.Thread;
public class test {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
//		long uptime = System.currentTimeMillis();
//		System.out.println(uptime);
//		System.out.println( TimeUnit.MILLISECONDS.toMinutes(uptime));
//		String inputFile = "E:/20D.arff";
//		File InputFile = new File(inputFile);
//		if(!InputFile.exists())
//		{
//			System.out.println("Can't find input file: " + InputFile);
//			System.exit(0);
//		}
//		BufferedReader InputBR = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile), "utf-8"));
//		String InputLine = InputBR.readLine();
//		
//		System.out.println(InputLine);
//		 ExecutorService executorService = Executors.newFixedThreadPool(5);
//	        for (int i = 0; i < 20; i++) {
//	            Runnable syncRunnable = new Runnable() {
//	                @Override
//	                public void run() {
//	                  //  Log.e(TAG, Thread.currentThread().getName());
//	                }
//	            };
//	            executorService.execute(syncRunnable);
//	        }
//	     Thread.sleep(60*1000);
//	     long uptime2 = System.currentTimeMillis();
//	     long time0 = uptime2 -uptime;
//	     System.out.println( TimeUnit.MILLISECONDS.toMinutes(time0));
//	     System.out.println( TimeUnit.MILLISECONDS.toSeconds(time0));
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		num1 n1 =new num1(); 
		num2 n2 =new num2();
		num3 n3 =new num3();
		
		executorService.execute(n1);     //打印1
		//executorService.shutdown();
		executorService.execute(n2);      //打印2
		executorService.execute(n3);      //打印2
		executorService.shutdown();
		    //打印3
		//executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		System.out.println("4!!!");
	}

}

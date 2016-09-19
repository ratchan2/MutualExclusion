import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class TAS implements Callable<Integer>{
   public static volatile boolean value = false;
   public static int csCount = -1; //assigned via command line args;
   public static int counter = 0;
   public static synchronized boolean getAndSet(boolean v){
	     boolean temp = value;
	     value = v;
	     return temp;
   }
   public static void lock(){
	   while(getAndSet(true));
	
   }
   
   public static synchronized void unlock(){
	    value = false;
   }
   public static void cs(){
	   counter++;
   }
   public Integer call(){
	   int count = 0;
	   while(count < csCount){
		   lock();
		   cs();
		   unlock();
		   count++;
	   }
	 //  System.out.println(counter);
	   return new Integer(0);
   }
   
   public static void main(String args[]){
	   int size = Integer.parseInt(args[0]);
	   TAS.csCount = Integer.parseInt(args[1]);
	   ArrayList<TAS> list = new ArrayList<TAS>();
	   for(int i = 0; i < size; i++){
		   list.add(new TAS());
	   }
	   	
	   ForkJoinPool pool = new ForkJoinPool();
	   List<Future<Integer>> results = new ArrayList<Future<Integer>>();
	   
	   long startTime = System.nanoTime();
	   results = pool.invokeAll(list);
	   
	   boolean bDone = false;
	   while(!bDone){
		   bDone = true;
		   for(int i = 0; i < results.size(); i++){
			   bDone = bDone && results.get(i).isDone();
		   }
	   }
	   long endTime = System.nanoTime();
	   System.out.println("Count: " + counter + ", Time: "  + (endTime - startTime));
   } 
}

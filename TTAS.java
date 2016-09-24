import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;


public class TTAS implements Callable<Integer>{
   public static volatile AtomicBoolean value = new AtomicBoolean();
   public static int csCount = -1;//assigned from command line args;
   public static int counter = 0;
 
   public static void lock(){
	   while(true){
		   while(value.get()){
		//	   System.out.println("Spinning!");
		   }
		   if(!value.getAndSet(true)){
			   return;
		   }
	   }
	
   }
   
   public static void unlock(){
	    value.set(false);
   }
   public static void cs(){	
	  // System.out.println("Got a chance!");
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
	   //System.out.println(counter);
	   return new Integer(0);
   }
   
   public static void main(String args[]){
	   int size = Integer.parseInt(args[0]);
	   TTAS.csCount = Integer.parseInt(args[1]);
	   ArrayList<TTAS> list = new ArrayList<TTAS>();
	   for(int i = 0; i < size; i++){
		   list.add(new TTAS());
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
	   System.out.println("Count: " + counter + ", Time: " + (endTime - startTime));
   } 
}

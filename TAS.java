import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

public class TAS implements Callable<Integer>{
   public static volatile boolean value = false;
   public static int csCount = 1000;
   public static int counter = 0;
   public static synchronized boolean getAndSet(boolean v){
	     boolean temp = value;
	     value = v;
	     return temp;
   }
   public static void lock(){
	   while(getAndSet(true));
	
   }
   
   public static void unlock(){
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
	   System.out.println(counter);
	   return new Integer(0);
   }
   
   public static void main(String args[]){
	   int size = 10;
	   ArrayList<TAS> list = new ArrayList<TAS>();
	   for(int i = 0; i < size; i++){
		   list.add(new TAS());
	   }
	   
	   ForkJoinPool pool = new ForkJoinPool();
	   pool.invokeAll(list);
	   
   } 
}

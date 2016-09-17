import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;


class ABC{
	
}
public class Peterson implements Runnable,Callable<Integer>{
    public int id;
    public static int victim;
    public static int csCount = 10000;
    
    public Peterson(int i){
    	this.id = i;
    }
    public static int counter = 0;
    public static boolean[] flag = {false,false};
	public void lock(){
        flag[id] = true;
        victim = id;		
		
		   while(flag[1 - id] == true && victim == id){
		//	   System.out.println("Waiting on something!");
		   }
	}
	public static synchronized void cs(int i){
		//System.out.println(i);
		counter++;
	}
	public void unlock(){
		flag[id] = false;
	}
	public void run(){
		int j = 0;
	  while(j < csCount){
		lock();
	    cs(id);
		unlock();
		j++;
		
	  }
	}
	public Integer call(){

		int j = 0;
	  while(j < csCount){
		lock();
	    cs(id);
		unlock();
		j++;
	  }
	  
      
	  return new Integer(0);
     }
	
	
	public static void main(String args[]){
		
	    Peterson one = new Peterson(0);
	    Peterson two = new Peterson(1);
	    //Thread A = new Thread(one);
	    //Thread B = new Thread(two);
	   
	    List<Callable<Integer>> list = new ArrayList<Callable<Integer>>();
	    list.add(one);
	    list.add(two);
	    ForkJoinPool pool = new ForkJoinPool();
	    List<Future<Integer>> results  = new ArrayList<Future<Integer>>(); 
	    results = pool.invokeAll(list);
	    //pool.submit(A);
	   // pool.submit(B);
	       
	    while(!results.get(0).isDone() || !results.get(1).isDone());
	    System.out.println(counter);
	     
	}
}
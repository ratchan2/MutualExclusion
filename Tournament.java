import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
class TreeLock{
	public static int n;
	public static int height;
	public static boolean[][] flags;
	public static int victims[];
	public static void init(int size){
		n = size;
		int levels = (int)(Math.ceil(Math.log(size)/ Math.log(2)));
		Double h = Math.ceil(Math.log(size) / Math.log(2) );
		height =  h.intValue();
	    flags = new boolean[(int)(Math.pow(2, levels)) - 1][2];
	    victims = new int[(int)(Math.pow(2, levels)) - 1];
	    for(int i = 0; i < (int)(Math.pow(2, levels)) - 1 ; i++){
	    	flags[i][0] = false;
	    	flags[i][1] = false;
	    }
	    
	}
	
		
	
}
public class Tournament implements Callable<Integer>{
	public static int counter;
	int addr;
	public int leafLock = -1;
	public int[] traceLock;
	public int[] traceId;
	public boolean leafLeft = false;
	public static int csCount = 1000;
	public static int levels = 0;
	public static ArrayList<Tournament> list = new ArrayList<Tournament>();
	Tournament(int id){
	     this.addr = id;
	     
	     list.add(this);
	}
	
	
    public void lock(){
    	int id = 0;
    	int locksAcquired = 0;
    	   if(!leafLeft){
    		   id = 1;
    	   }
    	int currentLock = leafLock;
    	 while(locksAcquired < levels){
    		   TreeLock.flags[currentLock][id] = true;
    		   TreeLock.victims[currentLock] = id;
    		  // System.out.println(addr + "> " + currentLock + "  " + id );
    		   while(TreeLock.flags[currentLock][1 - id] && TreeLock.victims[currentLock] == id);
    		   traceLock[locksAcquired] = currentLock;
    		   traceId[locksAcquired] = id;
    		   //Hurray I can enter next level
    		   locksAcquired++;
    		   
    		   id = ( currentLock % 2 == 1 ) ? 0 : 1;
    		   if(currentLock % 2 == 0){
    		        currentLock = (currentLock / 2 ) - 1;
    		   }
    		   else
    		   {
    			   currentLock = currentLock / 2;
    		   }
    		   
    	 }
    	 
    }
    
    public static void assignLeafLocks(int size){
    	levels = (int)(Math.ceil(Math.log(size)/Math.log(2)));
    	int leaf = (int)(Math.pow(2, levels -1)) - 1;
    	
    	for(int i = 0; i < list.size(); i++){
    		list.get(i).traceLock = new int[levels];
    		list.get(i).traceId = new int[levels];
    	}
    	 for(int i = 0; i < list.size(); i++){
    		  list.get(i).leafLock = leaf;
    		  list.get(i).leafLeft = true;
    		  ++i;
    		  if(i >= list.size()){
    			  return;
    		  }
    		  list.get(i).leafLock = leaf;
    		  leaf++;
    	 }
    }
    public void begin(){
    	   int count = 0;
    	   while(count < csCount){
    		   lock();
    		   cs();
    		   unlock();
    		   count++;
    	   }
    }
    public static void cs(){
    	counter++;
    }
    public void unlock(){
    	   for(int i = levels -1 ; i >= 0; i-- ){
    		    TreeLock.flags[traceLock[i]][traceId[i]] = false;
    	   }
    }
	
	public Integer call(){
		begin();
		System.out.println(addr + ", counter: " + counter);
		return new Integer(0);
	}
	
	public static void main(String args[]){
		
		int size = 10;
		for(int i = 0; i < size; i++){
			new  Tournament(i);
		}
		TreeLock.init(size);
		Tournament.assignLeafLocks(size);
		
		for(int i = 0; i < Tournament.list.size();i++){
			 System.out.println(i +" , leaf: " + list.get(i).leafLock);
		}
		
		List<Callable<Integer>> list = new ArrayList<Callable<Integer>>();
		for(int i = 0; i < Tournament.list.size(); i++){
			list.add(Tournament.list.get(i));
		}
		ForkJoinPool pool = new ForkJoinPool();
	    List<Future<Integer>> results  = new ArrayList<Future<Integer>>(); 
	    results = pool.invokeAll(list);
	    
	    boolean bResult = false;
	    
	 
	 	
	}
}
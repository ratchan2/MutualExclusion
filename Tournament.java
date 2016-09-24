import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

class Tuple{
	public int traceLock[];
	public int traceId[];
	public Tuple(int []a, int[]b){
		traceLock = a;
		traceId = b;
	}
}
class Peterson{
	public volatile int  victim;
	public volatile boolean[] flag = {false,false};
	public void lock(int addr,int id){ 
		flag[id] = true;
		victim = addr;
		while(flag[1-id] && victim == addr);
	}
	public void unlock(int id){
		flag[id] = false;
	}
 
}
public class Tournament implements Callable<Integer>{

	public static int n;
	public static int height;
	public volatile static boolean[][] flags;
	public volatile static int victims[];
	public volatile static int access[][];
	public volatile static boolean inCS = false;
	public volatile static int idCS = -1;
	public volatile static int violation = 0;
	public volatile static ArrayList<Peterson> petersonLocks = new ArrayList<Peterson>();
	public static ArrayList<ArrayList<Tuple>> tupleList = new ArrayList<ArrayList<Tuple>>(16);
	public static void init(int size){
		n = size;
		int levels = (int)(Math.ceil(Math.log(size)/ Math.log(2)));
		Double h = Math.ceil(Math.log(size) / Math.log(2) );
		height =  h.intValue();
	    flags = new boolean[(int)(Math.pow(2, levels)) - 1][2];
	    access = new int[(int)(Math.pow(2, levels)) - 1][2];
	    victims = new int[(int)(Math.pow(2, levels)) - 1];
	    
	    for(int i = 0; i < n; i++){
	    	ArrayList<Tuple> list = new ArrayList<Tuple>();
	    	tupleList.add(list);
	    }
	    for(int i = 0; i < (int)(Math.pow(2, levels)) - 1 ; i++){
	    	flags[i][0] = false;
	    	flags[i][1] = false;
	    	access[i][0] = -1;
	    	access[i][1] = -1;
	    	petersonLocks.add(new Peterson());
	    }	
	    
	}
	

	public volatile static int counter = 0;
	int addr;
	public int leafLock = -1;
	public volatile int[] traceLock;
	public volatile int[] traceId;
	public volatile boolean leafLeft = false;
	public volatile static int csCount = -1;// assigned via command line arguments;
	public volatile static int levels = 0;
	public static ArrayList<Tournament> list = new ArrayList<Tournament>();
	Tournament(int id){
	     this.addr = id;
	     list.add(this);
	}
	public static  void setFlagAndVictim(int currentLock, int id){
		 flags[currentLock][id] = true;
		 victims[currentLock] = id;
	}
	
    public void lock(){
         int locksAcquired = 0;
    	 while(locksAcquired < levels){
            int currentLock = traceLock[locksAcquired];
            int id = traceId[locksAcquired];
            
//    		while(access[currentLock][id] != addr || flags[currentLock][id]){
//            	   while(flags[currentLock][id]);      
//            	    access[currentLock][id] = addr;  
//            }
//             
//       		flags[currentLock][id] = true;
//       		victims[currentLock] = addr;
//    		 while( flags[currentLock][1 - id] &&  victims[currentLock] == addr);
  
               petersonLocks.get(currentLock).lock(addr, id);
    		   locksAcquired++;
    		   
    		   	
    	 }
    	 
    }
    
    public static void assignLeafLocks(int size){
    	levels = (int)(Math.ceil(Math.log(size)/Math.log(2)));
    	System.out.println("Levels: " + levels);
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
    		  list.get(i).leafLeft = false;
    		  leaf++;
    	 }
    	 
    	 
    }
    public void buildTraceLocks(){
    	  int currentLock = leafLock;
          int id = (leafLeft) ? 0 : 1;
          traceLock[0] = currentLock;
          traceId[0] = id;
    	  for(int i = 1; i < levels; i++){
   		   id = ( currentLock % 2 == 1 ) ? 0 : 1;
   		   if(currentLock % 2 == 0){
   		        currentLock = (currentLock / 2 ) - 1;
   		   }
   		   else
   		   {
   			   currentLock = currentLock / 2;
   		   }
           traceLock[i] = currentLock;
           traceId[i] = id;
    	  }
    	  String line = addr + ">";
    	  for(int i = 0; i < levels; i++){
    		  line+= " (" + traceLock[i] + "," + traceId[i] + ")";
    		  if(i < levels - 1){
    			  line += ",";
    		  }
    	  }
    	  System.out.println(line);
    }
    public void begin(){
    	   int count = 0;
    	   while(count < csCount){
    		   lock();
    		   cs();
    		   unlock(traceLock,traceId);
    		   count++;
    	   }
    }
    public void cs(){
       int id;
      if( (id = idCS) != -1){
    		//log!
    		System.out.println("GOTCHA!!" + "addr: " + addr + ", idCS: " + id + ",counter: "+ counter);
    		violation++;
    	}
    	inCS = true;
    	idCS = addr;
    	counter++;   
    	idCS = -1;
    	tupleList.get(addr).add(new Tuple(traceLock,traceId));
    		
    }
   
    public static  void  unlock(int traceLock[],int[] traceId){
    
    	   for(int i = levels - 1 ; i >= 0; i-- ){
    		   //  flags[traceLock[i]][traceId[i]] = false;
    		   petersonLocks.get(traceLock[i]).unlock(traceId[i]);
    	   }
    	   
    	   
    //	TreeLock.flags[traceLock[levels -1 ]][traceId[levels -1]] = false;
    }
	
	public Integer call(){
		begin();
	//	System.out.println(addr + ", counter: " + counter);
		return new Integer(0);
	}
	
	public static void main(String args[]){
		Runtime runtime = Runtime.getRuntime();
		System.out.println(runtime.availableProcessors());
		int size = Integer.parseInt(args[0]);
		Tournament.csCount = Integer.parseInt(args[1]);
		for(int i = 0; i < size; i++){
			new  Tournament(i);
		}
		 init(size);
		Tournament.assignLeafLocks(size);
		for(int i = 0; i < size; i++){
			Tournament.list.get(i).buildTraceLocks();
		}
		for(int i = 0; i < Tournament.list.size();i++){
		//	 System.out.println(i +" , leaf: " + list.get(i).leafLock);
		}
		
		
		ForkJoinPool pool = new ForkJoinPool();
	    List<Future<Integer>> results  = new ArrayList<Future<Integer>>(); 
	    
	    
	    long startTime = System.nanoTime();
	    results = pool.invokeAll(Tournament.list);
	    
	    boolean bResult = false;
	    
	    while(!bResult){
	    	bResult = true;
	    	for(int i = 0; i < results.size(); i++){
	    		bResult = bResult && results.get(i).isDone();
	    	}
	    }
	    long endTime = System.nanoTime();
	    
	    System.out.println("Count: " + counter + ",Violation: " + violation + ", Time: " + (endTime - startTime));
	 	
	}
}
package monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResourceHandler {
	
	//lock for entry methods
	private Lock lock;
	
	//condition variables
	private Condition consumerRow1; 
	private Condition consumerRow2;
	
	//capacity
	public static final int CAPACITY = 10;
	
	//row length
	private int consumerRow1Length;
	private int consumerRow2Length;
	
	//counter for knowing who is using the resource 
	private int consumer1Number;
	private int consumer2Number;
	
	public ResourceHandler() {
		
		//check parameters first
		
		this.lock = new ReentrantLock();
		
		this.consumerRow1 = lock.newCondition();
		this.consumerRow2 = lock.newCondition();
		
		//set row length to zero
		this.consumerRow1Length = 0;
		this.consumerRow2Length = 0;
		
		//set the number of consumer which are using the resource to zero
		this.consumer1Number=0;
		this.consumer2Number=0;
	}
	
	public void enterConsumer1() throws InterruptedException{
		this.lock.lock();
		try {
			while(this.respectResource1PolicyAndPriority()) {
				this.consumerRow1Length++; //add the thread to the condition row
				this.consumerRow1.await();
				this.consumerRow1Length--; //try to remove the thread from the row if policy and priority are respected
			}
			this.consumer1Number++; //add a consumer of type 1 to the resource

		}finally {
			this.lock.unlock();
		}
	}
	
	public void enterConsumer2() throws InterruptedException{
		this.lock.lock();
		try {
			while(this.respectResource2PolicyAndPriority()) {
				this.consumerRow2Length++; //add the thread to the condition row
				this.consumerRow2.await();
				this.consumerRow2Length--; //try to remove the thread from the row if policy and priority are respected
			}
			this.consumer2Number++; //add a consumer of type 1 to the resource

			
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 *  
	 * @return true if both the policy and priority conditions of the consumer 1 are respected.
	 */
	private boolean respectResource1PolicyAndPriority() {
		boolean policy = true; //for example consumer1Number must not be the same of CAPACITY
		boolean priority = true; //for example consumer2Number must be 0
		return policy && priority;
	}
	
	/**
	 *  
	 * @return true if both the policy and priority conditions of the consumer 2 are respected.
	 */
	private boolean respectResource2PolicyAndPriority() {
		boolean policy = true; //for example consumer2Number must not be the same of CAPACITY
		boolean priority = true; //for example nothing!
		return policy && priority;
	}
	
	//release the resource to the THREAD WITH THE HIGHEST PRIORITY
	public void releaseConsumer1() {
		this.lock.lock();
		
		if(this.consumerRow1Length>0) //release the resource to other consumer1 
			this.consumerRow1.signal();
		else if(this.consumerRow2Length>0) //release the resource to consumer2 "CAMBIO DI MODO"
			this.consumerRow2.signalAll();
		
		this.lock.unlock();
	}
	
	public void releaseConsumer2() {
		this.lock.lock();
		
		if(this.consumerRow2Length>0) //release the resource to other consumer2
			this.consumerRow2.signal();
		else if(this.consumerRow1Length>0) //release the resource to consumer1 "CAMBIO DI MODO"
			this.consumerRow1.signalAll();
		
		this.lock.unlock();
	}
	

}

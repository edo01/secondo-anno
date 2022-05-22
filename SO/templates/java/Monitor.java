package monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jdk.internal.loader.Resource;

/*
 * TODO:
 * - una classe log: avere un buon log è fondamentale per capire se il proprio programma funziona (differenziare per chi fa il log)
 * - classi fornitore e utilizzatore
 * 
 */

public class Monitor {
	
	//lock for entry methods
	private Lock lock;
	
	//condition variables
	private Condition consumerQueue1; 
	private Condition consumerQueue2;
	
	//capacity
	public static final int CAPACITY = 10;
	
	//row length
	private int consumerQueue1Length;
	private int consumerQueue2Length;
	
	//counter for knowing who is using the resource 
	private int consumer1Number;
	private int consumer2Number;
	
	//resource
	private int resource;
	
	//status
	private int status; 
	private static final int INIT=1;
	private static final int ACQUIRE=2;
	private static final int RELEASE=3;
	
	
	public ResourceHandler() {
		
		//check parameters first
		
		this.lock = new ReentrantLock();
		
		this.consumerQueue1 = lock.newCondition();
		this.consumerQueue2 = lock.newCondition();
		
		//set row length to zero
		this.consumerQueue1Length = 0;
		this.consumerQueue2Length = 0;
		
		//set the number of consumer which are using the resource to zero
		this.consumer1Number=0;
		this.consumer2Number=0;
		
		//init resource
		this.resource=20;
		
		//init status
		this.status=this.INIT;
		
		log("init");
	
	}
	
	//Se è necessario assegnare delle variabili
	public void enterConsumer1() throws InterruptedException{
		this.lock.lock();
		try {
			while(!this.respectResource1PolicyAndPriority()) {
				this.consumerQueue1Length++; //add the thread to the condition row
				this.log("wait");
				this.consumerQueue1.await();
				this.consumerQueue1Length--; //try to remove the thread from the row if policy and priority are respected
			}
			log("acquire");
			this.consumer1Number++; //add a consumer of type 1 to the resource

		}finally {
			this.lock.unlock();
		}
	}
	
	public void enterConsumer2() throws InterruptedException{
		this.lock.lock();
		try {
			while(!this.respectResource2PolicyAndPriority()) {
				this.consumerQueue2Length++; //add the thread to the condition row
				this.log("wait");
				this.consumerQueue2.await();
				this.consumerQueue2Length--; //try to remove the thread from the row if policy and priority are respected
			}
			this.consumer2Number++; //add a consumer of type 1 to the resource
			log("acquire");

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
		boolean priority = true; //for example consumerQueue2Length must be 0
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
		
		if(this.consumerQueue1Length>0) //release the resource to other consumer1 
			this.consumerQueue1.signal(); // quando liberi una risorsa chiedeti sempre: quante risorse sto liberando?
		else if(this.consumerQueue2Length>0) //release the resource to consumer2 "CAMBIO DI MODO"
			this.consumerQueue2.signalAll();
		
		this.consumer1Number--;
		this.resource--;

		log("release");

		this.lock.unlock();
	}
	
	public void releaseConsumer2() {
		this.lock.lock();
		
		if(this.consumerQueue2Length>0) //release the resource to other consumer2
			this.consumerQueue2.signal(); // quando liberi una risorsa chiedeti sempre: quante risorse sto liberando?
		else if(this.consumerQueue1Length>0) //release the resource to consumer1 "CAMBIO DI MODO"
			this.consumerQueue1.signalAll();
		
		this.consumer1Number++;
		this.resource--;
		
		log("release");

		this.lock.unlock();
	}
	
	public void produce(int n) {
		this.lock();
	
		this.resource+=n;
		
		if(this.consumerQueue1Length>0)
			this.consumerQueue1.signalAll(); //Se più thread possono accedere dopo questo incremento, signalAll
		else if(this.consumerQueue2Length>0)
			this.consumerQueue2.signalAll();
		
		log("produce");

		this.unlock();
	}

	
	
	private void log(String status) {
		String log ="[LOG:"+ status + "]"; // add monitor here
		System.out.println(log);
	}

}

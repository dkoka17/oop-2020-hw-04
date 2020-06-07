// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;

public class Bank {
	private static BlockingQueue<Transaction> queue;
	private static Map<Integer,Account> accountsMap ;
	private static  CountDownLatch countDownLatch;
	private final static Transaction nullTr = new Transaction(-1, -1, -1);


	private static class Worker extends Thread{
		private BlockingQueue<Transaction> queue;
		private Map<Integer,Account> accountsMap ;
		private  CountDownLatch countDownLatch;

		public Worker(BlockingQueue<Transaction> queue,Map<Integer,Account> accountsMap,CountDownLatch countDownLatch) {
			this.queue = queue;
			this.accountsMap = accountsMap;
			this.countDownLatch = countDownLatch;
		}

		public void run(){
			while (true){
				try {
					Transaction cur = queue.take();
					if(cur.equals(nullTr)){
						countDownLatch.countDown();
						return;
					}else {
						int amount = cur.getAmount();
						int from = cur.getFrom();
						int  to = cur.getTo();
						accountsMap.get(from).minusMoney(amount);
						accountsMap.get(to).addMoney(amount);
					}
				} catch (InterruptedException e) {
				}
			}

		}
	}
	public static final int ACCOUNTS = 20;	 // number of accounts
	/*
	 Reads transaction data (from/to/amt) from a file for processing.
	 (provided code)
	 */
	public static void readFile(String file,int numWorkers) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Use stream tokenizer to get successive words from file
			StreamTokenizer tokenizer = new StreamTokenizer(reader);
			
			while (true) {
				int read = tokenizer.nextToken();
				if (read == StreamTokenizer.TT_EOF){
					for(int i=0; i<numWorkers; i++){
						queue.put(nullTr);
					}
					break;  // detect EOF
				}
				int from = (int)tokenizer.nval;
				
				tokenizer.nextToken();
				int to = (int)tokenizer.nval;
				
				tokenizer.nextToken();
				int amount = (int)tokenizer.nval;
				// Use the from/to/amount
				Transaction newTrans = new Transaction(from,to,amount);
				queue.put(newTrans);
			}

		}
		catch (Exception e) {
			throw new RuntimeException();
		}
	}

	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	public static void processFile(String file, int numWorkers) {
		try{
			queue = new SynchronousQueue<>();
			accountsMap = new HashMap<Integer,Account>();
			countDownLatch = new CountDownLatch(numWorkers);
			ArrayList<Worker> threads = new ArrayList<Worker>();
			int balance = 1000;
			for(int i=0; i<ACCOUNTS; i++){
				Account acc = new Account(i,balance);
				accountsMap.put(i,acc);
			}
			CountDownLatch count = new CountDownLatch(numWorkers);
			for(int i=0; i<numWorkers; i++){
				Worker worker = new Worker(queue,accountsMap,countDownLatch);
				worker.start();
			}
			readFile(file,numWorkers);
			countDownLatch.await();

			printRes();
		}  catch (Exception e) {
			throw new RuntimeException();
		}
	}
	private static void printRes(){
		for(Map.Entry<Integer, Account> ac : accountsMap.entrySet()){
			Account acoun = ac.getValue();
			System.out.println(acoun.toString());
		}
	}
	/*
	 Looks at commandline args and calls Bank processing.
	*/
	public static void main(String[] args) {
		// deal with command-lines args
		if (args.length == 0) {
			System.out.println("Args: transaction-file [num-workers [limit]]");
			return;
		}

		String file = args[0];

		int numWorkers = 1;
		if (args.length >= 2) {
			numWorkers = Integer.parseInt(args[1]);
		}
		processFile(file,numWorkers);

	}
}


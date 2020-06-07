// Account.java

/*
 Simple, thread-safe Account class encapsulates
 a balance and a transaction count.
*/
public class Account {
	private int id;
	private int balance;
	private int transactions;
	
	// It may work out to be handy for the account to
	// have a pointer to its Bank.
	// (a suggestion, not a requirement)
	private Bank bank;  


	public Account( int id, int balance) {
		this.id = id;
		this.balance = balance;
		transactions = 0;
	}


	public synchronized void addMoney(int money){
		this.balance+=money;
		transactions+=1;
	}

	public synchronized void minusMoney(int money){
		this.balance-=money;
		transactions+=1;
	}

	@Override
	public String toString() {
		return ("acct:" + id +" bal:" + balance + " trans:" + transactions);
	}
}

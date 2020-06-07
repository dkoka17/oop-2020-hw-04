import java.util.Objects;

// Transaction.java
/*
 (provided code)
 Transaction is just a dumb struct to hold
 one transaction. Supports toString.
*/
public class Transaction {
	public int from;
	public int to;
	public int amount;
	
   	public Transaction(int from, int to, int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public int getAmount(){
   		return this.amount;
	}
	public int getFrom(){
		return this.from;
	}
	public int getTo(){
		return this.to;
	}

	@Override
	public boolean equals(Object o) {
		Transaction that = (Transaction) o;
		return from == that.from &&
				to == that.to &&
				amount == that.amount;
	}


}

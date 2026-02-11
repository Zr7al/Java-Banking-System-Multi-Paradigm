package bankapp;
/*
 * OOP Paradigm:
 *  - Encapsulation: Private data, public methods (access only via getters/setters)
 *  - Inheritance: Base class for SavingsAccount and CheckingAccount
 *  - Polymorphism: Subclasses override methods
 *  - Method Overloading: deposit() method overloaded
 */

// Encapsulation: All attributes are private and accessed only through getters/setters.
public class BankAccount {
	private String owner;
	private double balance;

	private double[] txChanges;
	private String[] txNotes;// transaction notes
	private int txIndex;// number of transactions recorded
	private static final int MAX_TRANSACTIONS = 100;

	/** Default constructor: zero balance */
	public BankAccount(String owner) {
		this(owner, 0.0);
	}

	/** Main constructor */
	public BankAccount(String owner, double initialBalance) {
		this.owner = owner;
		this.balance = initialBalance;
		this.txChanges = new double[MAX_TRANSACTIONS];
		this.txNotes = new String[MAX_TRANSACTIONS];
		this.txIndex = 0;
		recordTransaction(0.0, "[INFO] Account created, balance=" + initialBalance);
	}

	// Overloaded deposit: Allows deposit without a note (Method Overloading)
	public void deposit(double amount) {
		deposit(amount, "no note");
	}

	/** Deposits money and adds to transaction history */
	public void deposit(double amount, String note) {
		if (amount > 0) {
			balance += amount;
			recordTransaction(+amount, "[DEPOSIT] +" + amount + " | " + note);
		} else {
			recordTransaction(amount, "[ERROR] deposit failed: " + amount);
		}
	}

	// Overloaded withdraw: Allows withdraw without a note (Optional, for symmetry)
	public void withdraw(double amount) {
		withdraw(amount, "no note");
	}

	/** Withdraws money if possible and logs it */
	public void withdraw(double amount, String note) {
		if (amount > 0 && balance >= amount) {
			balance -= amount;
			recordTransaction(-amount, "[WITHDRAW] -" + amount + " | " + note);
		} else {
			recordTransaction(-amount, "[ERROR] withdrawal failed: " + amount);
		}
	}

	/** Apply a monthly fee (overridden by subclasses) */
	public void applyMonthlyFee() {
		// No default fee in base
	}

	/** record only the note, no amount storage */
	protected void addToHistory(String entry) {
		if (txIndex < txNotes.length) {
			txNotes[txIndex] = entry;
		}
	}

	/** record the actual change + note, then increment index */
	private void recordTransaction(double change, String entry) {
		if (txIndex < txChanges.length) {
			txChanges[txIndex] = change;
			txNotes[txIndex] = entry;
			txIndex++;
		}
	}

	/**
	 * Selection Sort Algorithm for Transactions (used in all 3 paradigms)
	 * Pseudocode: START FOR i from 0 to txIndex-2 minIndex = i FOR j from i+1 to
	 * txIndex-1 IF txChanges[j] < txChanges[minIndex] THEN minIndex = j END IF NEXT
	 * j IF minIndex != i THEN swap txChanges[i], txChanges[minIndex] swap
	 * txNotes[i], txNotes[minIndex] END IF NEXT i END
	 */
	protected void sortTransactions() {
		for (int i = 0; i < txIndex - 1; i++) {
			int maxIdx = i;
			for (int j = i + 1; j < txIndex; j++) {
				if (Math.abs(txChanges[j]) > Math.abs(txChanges[maxIdx])) {
					maxIdx = j;
				}
			}
			if (maxIdx != i) {
				double tmpAmt = txChanges[i];
				txChanges[i] = txChanges[maxIdx];
				txChanges[maxIdx] = tmpAmt;
				String tmpNote = txNotes[i];
				txNotes[i] = txNotes[maxIdx];
				txNotes[maxIdx] = tmpNote;
			}
		}
	}

	/** Prints transaction history and current balance */
	public void printHistory() {
		System.out.println("== Transactions for " + owner + " ==");
		for (int i = 0; i < txIndex; i++) {
			System.out.println(txNotes[i]);
		}
		System.out.println("Current balance: " + getBalance());
		System.out.println("================================");
	}

	/** Prints sorted transaction history and current balance */
	public void printSortedTransactions() {
		sortTransactions();
		System.out.println("== Sorted transactions for " + owner + " ==");
		for (int i = 0; i < txIndex; i++) {
			// no ternary – use if/else
			String sign;
			if (txChanges[i] >= 0) {
				sign = "+";
			} else {
				sign = "";
			}
			System.out.println(sign + String.format("%.2f", txChanges[i]) + " JD  |  " + txNotes[i]);
		}
		System.out.println("Current balance: " + getBalance());
		System.out.println("================================");
	}

	// Encapsulation: Private attributes accessed only via public getter/setter
	// methods.
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	// Optionally: add getters for transaction history if needed in subclasses
}

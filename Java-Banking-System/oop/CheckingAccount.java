package bankapp;

/*
 * OOP Paradigm:
 *  - Inheritance: extends BankAccount
 *  - Encapsulation: overdraft logic inside subclass
 *  - Polymorphism: overrides withdraw, applyMonthlyFee, toString
 */
public class CheckingAccount extends BankAccount {
	private static final double OVERDRAFT_LIMIT = 50.0;
	private static final double MONTHLY_FEE = 5.0;

	public CheckingAccount(String owner, double initialBalance) {
		super(owner, initialBalance);
		addToHistory("[INFO] Checking account created");
	}

	public CheckingAccount(String owner) {
		super(owner);
		addToHistory("[INFO] Checking account created with default balance");
	}

	@Override
	public void withdraw(double amount, String note) {
		if (amount > 0 && (getBalance() + OVERDRAFT_LIMIT) >= amount) {
			setBalance(getBalance() - amount);
			addToHistory("[WITHDRAW] -" + amount + " | " + note);
		} else {
			addToHistory("[ERROR] overdraft withdrawal failed: " + amount);
		}
	}

	@Override
	public void applyMonthlyFee() {
		withdraw(MONTHLY_FEE, "[MONTHLY FEE]");
	}

	@Override
	public String toString() {
		return "CheckingAccount[" + getOwner() + ", balance=" + getBalance() + "]";
	}
}

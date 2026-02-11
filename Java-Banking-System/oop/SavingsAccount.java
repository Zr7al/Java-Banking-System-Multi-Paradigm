package bankapp;

/*
 * OOP Paradigm:
 *  - Inheritance: extends BankAccount
 *  - Encapsulation: interest logic inside subclass
 *  - Polymorphism: overrides applyMonthlyFee, toString
 */
public class SavingsAccount extends BankAccount {
	private static final double INTEREST_RATE = 0.10;

	public SavingsAccount(String owner, double initialBalance) {
		super(owner, initialBalance);
		addToHistory("[INFO] Savings account created");
	}

	public SavingsAccount(String owner) {
		super(owner);
		addToHistory("[INFO] Savings account created with default balance");
	}

	// Applies monthly interest for given number of months
	public void applyMonthlyInterest(int months) {
		if (months > 0) {
			double interest = getBalance() * INTEREST_RATE * months;
			setBalance(getBalance() + interest);
			addToHistory("[INTEREST] +" + interest + " for " + months + " month(s)");
		} else {
			addToHistory("[ERROR] interest months invalid: " + months);
		}
	}

	@Override
	public void withdraw(double amount, String note) {
		addToHistory("[ERROR] Withdrawals blocked for savings");
	}

	@Override
	public void applyMonthlyFee() {
		applyMonthlyInterest(1);
	}

	@Override
	public String toString() {
		return "SavingsAccount[" + getOwner() + ", balance=" + getBalance() + "]";
	}
}

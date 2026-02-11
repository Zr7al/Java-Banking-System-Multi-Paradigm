package Final;

public class BankProcedural {
	// ====== CONSTANTS ======
	static final double MONTHLY_FEE = 5.0;
	static final double INTEREST_RATE = 0.10;
	static final double OVERDRAFT_LIMIT = 50.0;
	static final int MAX_TRANSACTIONS = 100;

	// ====== GLOBAL DATA ======
	// Savings account
	static String savingsName = "Zaid";
	static double savingsBalance = 200;
	static String[] savingsHistory = new String[MAX_TRANSACTIONS];
	static int savingsIndex = 0;
	static double[] savingsAmounts = new double[MAX_TRANSACTIONS];
	static int savingsAmountIndex = 0;

	// Checking account
	static String checkingName = "Zain";
	static double checkingBalance = 50;
	static boolean isFrozen = false;
	static String[] checkingHistory = new String[MAX_TRANSACTIONS];
	static int checkingIndex = 0;
	static double[] checkingAmounts = new double[MAX_TRANSACTIONS];
	static int checkingAmountIndex = 0;

	public static void main(String[] args) {
		// SAVINGS SCENARIO
		savingsAdd("[INFO] Account created: " + savingsName + " with 200.0 JD");
		depositToSavings(50);
		depositToSavings(-20);
		applyInterestToSavings();
		applyMonthlyInterestToSavings(2);
		withdrawFromSavings(100);
		applyMonthlyFeeToSavings();
		printSavingsHistory();
		printSortedTransactions(savingsAmounts, savingsHistory, savingsAmountIndex, savingsName);

		// CHECKING SCENARIO
		checkingAdd("[INFO] Account created: " + checkingName + " with 50.0 JD");
		depositToChecking(0);
		withdrawFromChecking(30);
		withdrawFromChecking(40);
		withdrawFromChecking(5);
		applyMonthlyFeeToChecking();
		depositToChecking(100);
		withdrawFromChecking(10);
		applyMonthlyFeeToChecking();
		withdrawFromChecking(200);
		printCheckingHistory();
		printSortedTransactions(checkingAmounts, checkingHistory, checkingAmountIndex, checkingName);
	}

	// ====== SAVINGS ACCOUNT FUNCTIONS ======

	static void depositToSavings(double amount) {
		if (amount <= 0) {
			savingsAdd("[ERROR] Invalid deposit: " + amount);
		} else {
			savingsBalance += amount;
			savingsAdd(String.format("[ACTION] Deposited: +%.2f JD -> Balance: %.2f JD", amount, savingsBalance));
			addSavingsAmount(amount, "[ACTION] Deposit");
		}
	}

	static void withdrawFromSavings(double amount) {
		savingsAdd("[SECURITY] Withdrawal blocked. Savings do not allow withdrawals.");
	}

	static void applyInterestToSavings() {
		double interest = savingsBalance * INTEREST_RATE;
		savingsBalance += interest;
		savingsAdd(String.format("[ACTION] Interest: +%.2f JD -> Balance: %.2f JD", interest, savingsBalance));
		addSavingsAmount(interest, "[ACTION] Interest");
	}

	static void applyMonthlyInterestToSavings(int months) {
		if (months <= 0) {
			savingsAdd("[ERROR] Invalid month value for compound interest: " + months);
			return;
		}
		double start = savingsBalance; //save the og balance
		double end = start * Math.pow(1 + INTEREST_RATE, months); //apply interest for n months //Future Value = Present Value * (1 + r)^n
		double interest = end - start; //extract only the interest gained
		savingsBalance += interest; //add the interest to your balance
		savingsAdd(String.format("[ACTION] Compound Interest: +%.2f JD over %d months -> Balance: %.2f JD", interest,months, savingsBalance));
		addSavingsAmount(interest, "[ACTION] Compound Interest");
	}

	static void applyMonthlyFeeToSavings() 
	{
		if (savingsBalance >= MONTHLY_FEE) 
		{
			savingsBalance -= MONTHLY_FEE;
			savingsAdd(String.format("[SYSTEM] Monthly Fee: -%.2f JD -> Balance: %.2f JD", MONTHLY_FEE, savingsBalance));
			addSavingsAmount(-MONTHLY_FEE, "[SYSTEM] Monthly Fee"); //the - sign means we are taking money out of the account
		} 
		else 
		{
			savingsAdd("[SYSTEM] Monthly fee failed. Insufficient funds.");
		}
	}

	static void savingsAdd(String msg) 
	{
		if (savingsIndex < savingsHistory.length) 
		{
		savingsHistory[savingsIndex++] = msg; //adding the msg to the history array at the current array index after entering msg it will be shifted
		} 
		else 
		{
		savingsHistory[savingsHistory.length - 1] = "[ERROR] Savings history full";
		}
	}

	static void addSavingsAmount(double amount, String note) 
	{
		if (savingsAmountIndex < savingsAmounts.length) //checks for space
		{
			savingsAmounts[savingsAmountIndex] = amount;
			if (savingsAmountIndex < savingsHistory.length)
			savingsHistory[savingsAmountIndex] += " [" + note + "]";
			savingsAmountIndex++;
		}
	}

	static void printSavingsHistory() 
	{
		System.out.println("= " + savingsName.toUpperCase() + " TRANSACTION HISTORY =");
		for (int i = 0; i < savingsIndex; i++) 
		{
			if (savingsHistory[i] != null)
			System.out.println("- " + savingsHistory[i]);
		}
		System.out.println("--------------------");
	}

	// ====== CHECKING ACCOUNT FUNCTIONS ======

	static void depositToChecking(double amount) 
	{
		if (amount <= 0) 
		{
			checkingAdd(String.format("[ERROR] Invalid deposit: %.2f", amount));
		} 
		else
		{
			checkingBalance += amount;
			checkingAdd(String.format("[ACTION] Deposit: +%.2f JD -> Balance: %.2f JD", amount, checkingBalance));
			addCheckingAmount(amount, "[ACTION] Deposit");
			if (isFrozen && checkingBalance > 0) 
			{
				isFrozen = false;
				checkingAdd("[SYSTEM] Account unfrozen. Balance positive.");
			}
		}
	}

	static void withdrawFromChecking(double amount) 
	{
		if (isFrozen) 
		{
			checkingAdd("[SECURITY] Withdrawal blocked. Account is frozen.");
			return;
		}
		if (amount <= checkingBalance) 
		{
			checkingBalance -= amount;
			checkingAdd(String.format("[ACTION] Withdraw: -%.2f JD -> Balance: %.2f JD", amount, checkingBalance));
			addCheckingAmount(-amount, "[ACTION] Withdraw");
		}
		else if (amount <= checkingBalance + OVERDRAFT_LIMIT) 
		{
			checkingBalance -= amount;
			checkingAdd(String.format("[SECURITY] Overdraft: -%.2f JD -> Balance: %.2f JD", amount, checkingBalance));
			addCheckingAmount(-amount, "[SECURITY] Overdraft");
		}
		else 
		{
			checkingAdd("[SECURITY] Withdrawal blocked. Exceeds overdraft limit.");
			return;
		}
		if (checkingBalance < -OVERDRAFT_LIMIT) {
			isFrozen = true;
			checkingAdd("[SECURITY] Account frozen. Overdraft limit exceeded.");
		}
	}

	static void applyMonthlyFeeToChecking() 
	{
		if (checkingBalance >= MONTHLY_FEE) 
		{
			checkingBalance -= MONTHLY_FEE;
			checkingAdd(String.format("[SYSTEM] Monthly Fee: -%.2f JD -> Balance: %.2f JD", MONTHLY_FEE, checkingBalance));
			addCheckingAmount(-MONTHLY_FEE, "[SYSTEM] Monthly Fee");
		} 
		else 
		{
			checkingAdd("[SYSTEM] Monthly fee failed. Insufficient funds.");
		}
	}

	static void checkingAdd(String msg)
	{
		if (checkingIndex < checkingHistory.length) 
		{
			checkingHistory[checkingIndex++] = msg;
		} 
		else
		{
			checkingHistory[checkingHistory.length - 1] = "[ERROR] Checking history full";
		}
	}

	static void addCheckingAmount(double amount, String note) 
	{
		if (checkingAmountIndex < checkingAmounts.length) 
		{
			checkingAmounts[checkingAmountIndex] = amount;
			if (checkingAmountIndex < checkingHistory.length)
			checkingHistory[checkingAmountIndex] += " [" + note + "]";
			checkingAmountIndex++;
		}
	}

	static void printCheckingHistory() 
	{
		System.out.println("= " + checkingName.toUpperCase() + " TRANSACTION HISTORY =");
		for (int i = 0; i < checkingIndex; i++) 
		{
			if (checkingHistory[i] != null)
			System.out.println("- " + checkingHistory[i]);
		}
		System.out.println("--------------------");
	}

	// ====== SELECTION SORT & PRINT ======

	/**
	 * Clones, selection-sorts by absolute value (largest first), then prints each
	 * transaction.
	 */
	static void printSortedTransactions(double[] amounts, String[] notes, int n, String owner) 
	{
		// Clone
		double[] sortedAmounts = new double[n];
		String[] sortedNotes = new String[n];
		for (int i = 0; i < n; i++) 
		{
			sortedAmounts[i] = amounts[i];
			sortedNotes[i] = notes[i];
		}

		// Selection sort (absolute value descending)
		for (int i = 0; i < n - 1; i++) 
		{
			int maxIdx = i;//assume i holds the largest 
			for (int j = i + 1; j < n; j++) 
			{//go thru the array
				if (Math.abs(sortedAmounts[j]) > Math.abs(sortedAmounts[maxIdx])) 
				{
					maxIdx = j;//if a bigger number was found
				}
			}
			// swap amounts
			double tmpAmt = sortedAmounts[i];
			sortedAmounts[i] = sortedAmounts[maxIdx];
			sortedAmounts[maxIdx] = tmpAmt;
			// swap notes
			String tmpNote = sortedNotes[i];
			sortedNotes[i] = sortedNotes[maxIdx];
			sortedNotes[maxIdx] = tmpNote;
		}

		// Print
		System.out.println("= " + owner.toUpperCase() + " SORTED TRANSACTION HISTORY =");
		for (int i = 0; i < n; i++) 
		{
			String sign;
			if (sortedAmounts[i] >= 0)
				sign = "+";
			else
				sign = "";
			System.out.printf("[%d] %s%.2f JD", i + 1, sign, sortedAmounts[i]);
			System.out.println(", Note: " + sortedNotes[i]);
		}
		System.out.println("--------------------");
	}
}

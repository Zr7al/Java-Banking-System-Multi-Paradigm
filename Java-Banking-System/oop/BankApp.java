package bankapp;

import java.util.Scanner;

/*
 * Demonstrates:
 *  - OOP flow using classes/objects
 *  - Reflection on OOP vs Procedural
 */
public class BankApp {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		SavingsAccount savings = new SavingsAccount("Zaid", 200);

		CheckingAccount checking = new CheckingAccount("Zain", 50);

		System.out.println("= TRANSFER DEMO =");
		System.out.print("Enter amount to transfer from SAVINGS TO CHECKING: ");
		double amt1 = scanner.nextDouble();
		savings.withdraw(amt1, "transfer to checking");
		checking.deposit(amt1, "transfer from savings");

		System.out.print("Enter amount to transfer from CHECKING TO SAVING: ");
		double amt2 = scanner.nextDouble();
		checking.withdraw(amt2, "transfer to savings");
		savings.deposit(amt2, "transfer from checking");

		// show raw histories
		savings.printHistory();
		checking.printHistory();

		// apply one month fee/interest
		savings.applyMonthlyFee();
		checking.applyMonthlyFee();

		// show sorted histories
		savings.printSortedTransactions();
		checking.printSortedTransactions();

		scanner.close();

		/*
		 * OOP Reflection: - OOP organizes related data and behaviors in logical units
		 * called classes, which helps manage complexity. - Inheritance avoids code
		 * duplication by letting subclasses use (and modify) base class logic. -
		 * Encapsulation hides sensitive data (like balance) so outside code can't
		 * corrupt it. - Polymorphism lets us use BankAccount references for both
		 * Checking and Savings, and the right method runs automatically. - This
		 * approach makes adding features (like a new account type) much easier than
		 * procedural. - OOP can be overkill for small scripts, and inheritance bugs can
		 * be hard to trace. - Overall, OOP is ideal for larger or expanding systems,
		 * but procedural is easier for quick, small tasks.
		 */
	}
}

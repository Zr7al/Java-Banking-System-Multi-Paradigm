package Final;

import javax.swing.*; //btns labels
import java.awt.*; //color font
import java.awt.event.*; //action event and listeners

/*
  ATMGui - Event-Driven Java ATM
 
  SELECTION SORT - Pseudocode:
  For i from 0 to n-2:
    Assume the ith element is the minimum
    For j from i+1 to n-1:
      If the ith element is less than the current minimum:
        Update the index of the minimum element
    Swap the minimum element found with the ith element
    Swap the notes too so amounts and notes stay in sync
 
  Selection sort is suitable here because transaction lists are small/fixed-size.
 
 */
@SuppressWarnings("serial")
public class ATMGui extends JFrame {

	// State constants
	private static final int STATE_WAITING = 0; //waiting for card
	private static final int STATE_PIN_ENTRY = 1;//user is entering PIN
	private static final int STATE_MAIN_MENU = 2;//pin correct show options
	private static final int STATE_DEPOSIT = 3;//enter deposit amount
	private static final int STATE_WITHDRAWAL = 4;//enter withdrawal amount

	// Account constants 
	private static final String VALID_PIN = "0429";
	private static final double INITIAL_SAVINGS_BAL = 200.0;
	private static final double INITIAL_CHECKING_BAL = 50.0;
	private static final double OVERDRAFT_LIMIT = 50.0;
	private static final double INTEREST_RATE = 0.10;
	private static final double MONTHLY_FEE_AMOUNT = 5.0;
	private static final int MAX_TRANSACTIONS = 100;

	// State variables
	private int currentState = STATE_WAITING;
	private boolean cardInserted = false; //true when card is pressed
	private boolean checkingFrozen = false;//becomes true when checking balance goes below 50
	private double lastDispensedAmount = 0.0;//shows how much cash they can collect when they click on the cash slot

	// Transaction histories and notes
	private String[] savingsAccountHistory = new String[MAX_TRANSACTIONS];
	private String[] checkingAccountHistory = new String[MAX_TRANSACTIONS];
	private int savingsHistoryCount = 0, checkingHistoryCount = 0; //Counts how many history messages are saved so far
	private double[] savingsTransactionAmounts = new double[MAX_TRANSACTIONS];
	private String[] savingsTransactionNotes = new String[MAX_TRANSACTIONS];//type of transaction
	private int savingsTransactionCount = 0;//items you've added
	private double[] checkingTransactionAmounts = new double[MAX_TRANSACTIONS];
	private String[] checkingTransactionNotes = new String[MAX_TRANSACTIONS];//type of transaction
	private int checkingTransactionCount = 0;//items you've added
	// Balances
	private double savingsBalance = INITIAL_SAVINGS_BAL;
	private double checkingBalance = INITIAL_CHECKING_BAL;
	// Input buffers
	private StringBuilder pinInput = new StringBuilder();
	private StringBuilder amountInput = new StringBuilder();

	// UI components
	private JLabel screenLabel;//blue screen
	private JButton cardButton;//credit card
	private JButton[][] keypadButtons = new JButton[4][4];
	private JButton depositButton, withdrawButton, balanceButton, historyButton, interestButton, feeButton, sortButton,
			transferButton;
	private JRadioButton savingsRadio, checkingRadio;
	private JPanel cashSlotPanel;
	private JLabel cashSlotLabel;

	
	  //Constructor to initialize the ATM GUI and set up all components and listeners
	 
	public ATMGui() 
	{
		super("ATM Machine");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setSize(1000, 700);

		JPanel mainPanel = new JPanel(null);
		mainPanel.setBackground(new Color(240, 244, 252));
		setContentPane(mainPanel);

		// CONTROL PANEL
		JPanel controlPanel = new JPanel(null);
		controlPanel.setBounds(35, 0, 300, 700);
		mainPanel.add(controlPanel);

		cardButton = new JButton("Insert Card");
		cardButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
		cardButton.setBackground(Color.red.darker());
		cardButton.setForeground(Color.WHITE);
		cardButton.setBounds(0, 20, 250, 60);
		cardButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (!cardInserted) 
				{
					cardInserted = true;
					cardButton.setEnabled(false);
					currentState = STATE_PIN_ENTRY;
					pinInput.setLength(0);
					screenLabel.setText("Enter your PIN");
				}
			}
		});
		controlPanel.add(cardButton);

		savingsRadio = new JRadioButton("Savings", true);
		checkingRadio = new JRadioButton("Checking");
		savingsRadio.setBounds(0, 100, 120, 30);
		checkingRadio.setBounds(130, 100, 120, 30);
		ButtonGroup bg = new ButtonGroup();
		bg.add(savingsRadio);
		bg.add(checkingRadio);
		controlPanel.add(savingsRadio);
		controlPanel.add(checkingRadio);

		depositButton = new JButton("Deposit");
		withdrawButton = new JButton("Withdraw");
		balanceButton = new JButton("Balance");
		historyButton = new JButton("History");
		interestButton = new JButton("Apply Interest");
		feeButton = new JButton("Apply Monthly Fee");
		sortButton = new JButton("Sort Transactions");
		transferButton = new JButton("Transfer");

		int y = 150;//starting height
		for (JButton b : new JButton[] 
		{depositButton,withdrawButton,balanceButton,historyButton,interestButton,feeButton,sortButton,transferButton }) 
		{
			b.setBounds(0, y, 220, 46);
			controlPanel.add(b);
			y += 60;
		}
		enableSideButtons(false);//only true when user enters the pin

		// ATM PANEL
		JPanel atmPanel = new JPanel(null);
		atmPanel.setBounds(350, 0, 650, 700);
		mainPanel.add(atmPanel);

		JPanel screenPanel = new JPanel(null);
		screenPanel.setBounds(0, 20, 450, 150);
		screenPanel.setBackground(new Color(60, 95, 175));//background screen
		atmPanel.add(screenPanel);

		screenLabel = new JLabel("Insert your card", SwingConstants.CENTER);//text inside the screen
		screenLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
		screenLabel.setForeground(Color.WHITE);
		screenLabel.setBounds(0, 0, 450, 150);
		screenPanel.add(screenLabel);
			
		// Cash slot
		cashSlotPanel = new JPanel(null);
		cashSlotPanel.setBounds(0, 200, 450, 70);
		cashSlotPanel.setBackground(new Color(100, 100, 100));
		cashSlotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		atmPanel.add(cashSlotPanel);

		cashSlotLabel = new JLabel("$", SwingConstants.CENTER);
		cashSlotLabel.setFont(new Font("Arial", Font.BOLD, 36));
		cashSlotLabel.setForeground(Color.WHITE);
		cashSlotLabel.setBackground(Color.GREEN.darker());
		cashSlotLabel.setOpaque(true);
		cashSlotLabel.setBounds(190, 10, 100, 50);
		cashSlotLabel.setVisible(false);
		cashSlotPanel.add(cashSlotLabel);

		cashSlotLabel.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (cashSlotLabel.isVisible()) 
				{
					JOptionPane.showMessageDialog(ATMGui.this, "You collected " + lastDispensedAmount +" JD","Collected", JOptionPane.INFORMATION_MESSAGE);
					cashSlotLabel.setVisible(false);
				}
			}
		});

		// Keypad
		JPanel keypadPanel = new JPanel(null);
		keypadPanel.setBounds(0, 300, 450, 300);
		atmPanel.add(keypadPanel);

		String[] keys = { "1", "2", "3", "EXIT", 
						  "4", "5", "6", "CLEAR", 
						  "7", "8", "9", "ENTER", 
						  "", "0", "", 		"" 
						};
		int idx = 0;
		for (int row = 0; row < 4; row++) 
		{
			int x = 0;
			for (int col = 0; col < 4; col++) 
			{
				final String label = keys[idx++];
				JButton btn = new JButton(label);
				
				int width;
				if (label.equals("ENTER") || label.equals("EXIT") || label.equals("CLEAR")) 
				{
					width = 120;
				}
				else 
				{
					width = 60;
				}
				btn.setBounds(x, row * 57, width, 45);

				if (label.equals("EXIT")) 
				{
					btn.setBackground(Color.RED.darker());
				} 
				else if (label.equals("CLEAR"))
				{
					btn.setBackground(Color.ORANGE);
				} 
				else if (label.equals("ENTER"))
				{
					btn.setBackground(Color.GREEN.darker());
				}

				if (!label.isEmpty()) 
				{
					btn.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e) 
						{
							handleKeypress(label);
						}
					});
				} 
				else 
				{
					btn.setEnabled(false);
				}
				keypadButtons[row][col] = btn;
				keypadPanel.add(btn);
				x = x + width + 12;
			}
		}

		// SIDE BUTTON LISTENERS
		depositButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				startTransaction(STATE_DEPOSIT);
			}
		});
		withdrawButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				startTransaction(STATE_WITHDRAWAL);
			}
		});
		balanceButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				displayBalance();
			}
		});
		historyButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				displayHistory();
			}
		});
		interestButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				applyInterest();
			}
		});
		feeButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				applyFee();
			}
		});
		sortButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				sortTransactions();
			}
		});
		transferButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				String inp = JOptionPane.showInputDialog(ATMGui.this, "Enter amount to transfer:", "Transfer",JOptionPane.PLAIN_MESSAGE);
				if (inp != null && inp.trim().length() > 0) 
				{
					try 
					{
						double amt = Double.parseDouble(inp);
						performTransfer(amt);
					} 
					catch (NumberFormatException ex) 
					{
						showErrorMessage("Invalid amount");
					}
				}
			}
		});
		setVisible(true);
		/*
		 * Event-Driven Reflection: - User interface is decoupled from business logic;
		 * each button, mouse, or keyboard action is an event. - Application control
		 * flow is asynchronous (based on user actions, not linear like procedural). -
		 * Compared to OOP: harder to debug, but UI is much more interactive and
		 * modular. - Compared to procedural: flow is not fixed; easier to add features
		 * but harder to trace state. - Event-driven is best for interactive apps like
		 * ATMs, GUIs, games, etc.
		 */
	}

	private void handleKeypress(String key) 
	{
		if (key.equals("EXIT"))
		{
			reset();
			return;
		}
		if (key.equals("CLEAR")) 
		{
			if (currentState == STATE_PIN_ENTRY)
			{
				pinInput.setLength(0);
				screenLabel.setText("PIN: ");
			} 
			else 
			{
				amountInput.setLength(0);
				screenLabel.setText("Amount: ");
			}
			return;
		}
		if (key.equals("ENTER"))
		{
			if (currentState == STATE_PIN_ENTRY) 
			{
				if (pinInput.toString().equals(VALID_PIN)) 
				{
					currentState = STATE_MAIN_MENU;
					screenLabel.setText("Select operation");
					enableSideButtons(true);
				} 
				else 
				{
					screenLabel.setText("Wrong PIN");
					pinInput.setLength(0);
				}
			} 
			else if (currentState == STATE_DEPOSIT || currentState == STATE_WITHDRAWAL)
			{
				processAmt();
			}
			return;
		}
		if (currentState == STATE_PIN_ENTRY && key.matches("\\d") && pinInput.length() < 4) 
		{
			pinInput.append(key);
			screenLabel.setText("PIN: " + new String(new char[pinInput.length()]).replace('\0', '*'));
		}
		else if ((currentState == STATE_DEPOSIT || currentState == STATE_WITHDRAWAL) && key.matches("\\d")) 
		{
			amountInput.append(key);
			screenLabel.setText("Amount: " + amountInput.toString());
		}
	}

	private void reset() 
	{
		currentState = STATE_WAITING;
		cardInserted = false;
		pinInput.setLength(0);
		screenLabel.setText("Insert your card");
		cardButton.setEnabled(true);
		enableSideButtons(false);
		cashSlotLabel.setVisible(false);
	}

	private void startTransaction(int type) 
	{
		currentState = type;
		amountInput.setLength(0);
		cashSlotLabel.setVisible(false);
		if (type == STATE_DEPOSIT) 
		{
			screenLabel.setText("Enter deposit");
		} 
		else
		{
			screenLabel.setText("Enter withdrawal");
		}
	}

	private void processAmt() 
	{
		int amt = Integer.parseInt(amountInput.toString());
		
		if (amt <= 0)
		{
			showErrorMessage("Amount must be positive");
			amountInput.setLength(0);
			return;
		}
		if (currentState == STATE_WITHDRAWAL) 
		{
			if (savingsRadio.isSelected()) 
			{
				showErrorMessage("Cannot withdraw from savings");
			}
			else if (checkingFrozen) 
			{
				showErrorMessage("Account frozen");
			}
			else if (checkingBalance - amt >= -OVERDRAFT_LIMIT) 
			{ 
				checkingBalance -= amt;
				addCheckingTransaction(-amt, "Withdraw");
				addToCheckingHistory(String.format("[ACTION] Withdraw -%.2f JD Balance: %.2f", (double) amt, checkingBalance));
				lastDispensedAmount = amt;
				cashSlotLabel.setVisible(true);
			} 
			else
			{
				showErrorMessage("Exceeds overdraft limit");
			}
		} 
		else //deposit logic
		{
			if (savingsRadio.isSelected())
			{
				savingsBalance += amt;
				addSavingsTransaction(amt, "Deposit");
				addToSavingsHistory(String.format("[ACTION] Deposit +%.2f JD Balance: %.2f", (double) amt, savingsBalance));
			}
			else
			{
				checkingBalance += amt;
				addCheckingTransaction(amt, "Deposit");
				addToCheckingHistory(String.format("[ACTION] Deposit +%.2f JD Balance: %.2f", (double) amt, checkingBalance));
				if (checkingFrozen && checkingBalance >= 0) 
				{
					checkingFrozen = false;
					addToCheckingHistory("[SYSTEM] Unfrozen on deposit");
				}
			}
		}
		amountInput.setLength(0);
		screenLabel.setText("Select operation");
		displayBalance();
	}

	private void performTransfer(double amt) 
	{
		if (amt <= 0) 
		{
			showErrorMessage("Amount must be positive");
			return;
		}
		if (savingsRadio.isSelected()) 
		{
			if (amt <= savingsBalance)
			{
				savingsBalance -= amt;
				checkingBalance += amt;
				addSavingsTransaction(-amt, "Transfer out");
				addToSavingsHistory(String.format("[TRANSFER OUT] -%.2f JD → CHECKING | Balance: %.2f", amt, savingsBalance));
				addCheckingTransaction(amt, "Transfer in");
				addToCheckingHistory(String.format("[TRANSFER IN] +%.2f JD ← SAVINGS | Balance: %.2f", amt, checkingBalance));
				if (checkingFrozen && checkingBalance >= 0)
				{
					checkingFrozen = false;
					addToCheckingHistory("[SYSTEM] Unfrozen on transfer in");
				}
				showInformationMessage(String.format("Transferred %.2f JD", amt));
			}
			else
			{
				showErrorMessage("Insufficient savings");
			}
		}
		else 
		{
			if (checkingBalance - amt >= -OVERDRAFT_LIMIT) 
			{
				checkingBalance -= amt;
				savingsBalance += amt;
				
				addCheckingTransaction(-amt, "Transfer out");
				addToCheckingHistory(String.format("[TRANSFER OUT] -%.2f JD → SAVINGS Balance: %.2f", amt, checkingBalance));
				
				if (checkingBalance < 0) 
				{
					checkingFrozen = true;
					addToCheckingHistory("[SECURITY] Frozen due to overdraft");
				}
				
				addSavingsTransaction(amt, "Transfer in");
				addToSavingsHistory(String.format("[TRANSFER IN] +%.2f JD ← CHECKING Balance: %.2f", amt, savingsBalance));
				
				showInformationMessage(String.format("Transferred %.2f JD", amt));
			} 
			else 
			{
				showErrorMessage("Overdraft limit exceeded");
			}
		}
	}

	private void addSavingsTransaction(double a, String n)
	{
		if (savingsTransactionCount < savingsTransactionAmounts.length)
		{
			savingsTransactionAmounts[savingsTransactionCount] = a; //could be positive or negative +20/-20
			savingsTransactionNotes[savingsTransactionCount] = n;
			savingsTransactionCount++;
		}
	}

	private void addCheckingTransaction(double a, String n) 
	{
		if (checkingTransactionCount < checkingTransactionAmounts.length)
		{
			checkingTransactionAmounts[checkingTransactionCount] = a;
			checkingTransactionNotes[checkingTransactionCount] = n;
			checkingTransactionCount++;
		}
	}

	private void addToSavingsHistory(String e) 
	{
		if (savingsHistoryCount < savingsAccountHistory.length) 
		{
			savingsAccountHistory[savingsHistoryCount++] = e;
	//saves the message e into the history array at the current index then increase the counter by 1
		}
	}

	private void addToCheckingHistory(String e) 
	{
		if (checkingHistoryCount < checkingAccountHistory.length) 
		{
			checkingAccountHistory[checkingHistoryCount++] = e;
		}
	}

	private void displayBalance()
	{
		if (savingsRadio.isSelected()) 
		{
			screenLabel.setText("Savings Balance: " + savingsBalance + " JD");
		}
		else
		{
			screenLabel.setText("Checking Balance: " + checkingBalance + " JD");
		}
	}

	private void displayHistory() 
	{
		StringBuilder sb = new StringBuilder();
		if (savingsRadio.isSelected())
		{
			for (int i = 0; i < savingsHistoryCount; i++) 
			{
				sb.append(savingsAccountHistory[i]).append("\n");
			}
			if (savingsHistoryCount == 0) 
			{
				sb.append("No SAVINGS transactions\n");
			}
			sb.append("--------------------\n");
			JOptionPane.showMessageDialog(this, sb.toString(), "Savings History", JOptionPane.INFORMATION_MESSAGE);
		} 
		else 
		{
			for (int i = 0; i < checkingHistoryCount; i++)
			{
				sb.append(checkingAccountHistory[i]).append("\n");
			}
			if (checkingHistoryCount == 0)
			{
				sb.append("No CHECKING transactions\n");
			}
			sb.append("--------------------\n");
			JOptionPane.showMessageDialog(this, sb.toString(), "Checking History", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void applyInterest() 
	{
		if (savingsRadio.isSelected()) 
		{
			double i = savingsBalance * INTEREST_RATE;
			savingsBalance += i;
			addToSavingsHistory(String.format("[ACTION] Interest +%.2f JD Bal: %.2f", i, savingsBalance));
			addSavingsTransaction(i, "Interest");
			showInformationMessage("Interest applied");
		} 
		else
		{
			showErrorMessage("Interest only on SAVINGS");
		}
	}

	private void applyFee()
	{
		if (!savingsRadio.isSelected()) 
		{
			if (checkingFrozen) 
			{
				showErrorMessage("Account frozen");
			}
			else if (checkingBalance >= MONTHLY_FEE_AMOUNT) 
			{
				checkingBalance -= MONTHLY_FEE_AMOUNT;
				addToCheckingHistory(String.format("[SYSTEM] Fee -%.2f JD Bal: %.2f", MONTHLY_FEE_AMOUNT, checkingBalance));
				addCheckingTransaction(-MONTHLY_FEE_AMOUNT, "Monthly Fee");
				if (checkingBalance < -OVERDRAFT_LIMIT)
				{
					checkingFrozen = true;
					addToCheckingHistory("[SECURITY] Frozen - overdraft limit");
					showErrorMessage("Fee caused overdraft - account frozen");
				} 
				else 
				{
					showInformationMessage("Fee applied");
				}
			} 
			else 
			{
				showErrorMessage("Insufficient for fee");
			}
		}
		else 
		{
			showErrorMessage("Fee only on CHECKING");
		}
	}

	private void sortTransactions() {
		/*
		  SELECTION SORT - Pseudocode: For i from 0 to n-2: Assume the ith element is
		  the minimum For j from i+1 to n-1: If the jth element is less than the
		  current minimum: Update the index of the fminimum element Swap the minimum
		  element found with the ith element Swap the notes too so amounts and notes
		  stay in sync
		 */
		if (savingsRadio.isSelected()) 
		{
			if (savingsTransactionCount < 2) 
			{
				showErrorMessage("Not enough SAVINGS transactions");
			}
			else 
			{
				for (int i = 0; i < savingsTransactionCount - 1; i++) 
				{
					int maxIdx = i;
					for (int j = i + 1; j < savingsTransactionCount; j++) 
					{
						if (Math.abs(savingsTransactionAmounts[j]) > Math.abs(savingsTransactionAmounts[maxIdx])) 
						{
							maxIdx = j;
						}
					}
					double tempAmt = savingsTransactionAmounts[i];
					savingsTransactionAmounts[i] = savingsTransactionAmounts[maxIdx];
					savingsTransactionAmounts[maxIdx] = tempAmt;

					String tempNote = savingsTransactionNotes[i];
					savingsTransactionNotes[i] = savingsTransactionNotes[maxIdx];
					savingsTransactionNotes[maxIdx] = tempNote;
				}
				savingsHistoryCount = 0;
				for (int i = 0; i < savingsTransactionCount; i++)
				{
					String prefix;
					if (savingsTransactionAmounts[i] >= 0)
					{
						prefix = "+";
					} 
					else 
					{
						prefix = "";
					}
					addToSavingsHistory("[ACTION] " + savingsTransactionNotes[i] + ": " + prefix+ String.format("%.2f", savingsTransactionAmounts[i]) + " JD");
				}
				showInformationMessage("Savings transactions sorted by size");
			}
		} 
		else 
		{
			if
			(checkingTransactionCount < 2)
			{
				showErrorMessage("Not enough CHECKING transactions");
			} 
			else
			{
				for (int i = 0; i < checkingTransactionCount - 1; i++) 
				{
					int maxIdx = i;
					for (int j = i + 1; j < checkingTransactionCount; j++) 
					{
						if (Math.abs(checkingTransactionAmounts[j]) > Math.abs(checkingTransactionAmounts[maxIdx])) 
						{
							maxIdx = j;
						}
					}
					double tempAmt = checkingTransactionAmounts[i];
					checkingTransactionAmounts[i] = checkingTransactionAmounts[maxIdx];
					checkingTransactionAmounts[maxIdx] = tempAmt;

					String tempNote = checkingTransactionNotes[i];
					checkingTransactionNotes[i] = checkingTransactionNotes[maxIdx];
					checkingTransactionNotes[maxIdx] = tempNote;
				}
				checkingHistoryCount = 0;
				for (int i = 0; i < checkingTransactionCount; i++) 
				{
					String prefix;
					if (checkingTransactionAmounts[i] >= 0) 
					{
						prefix = "+";
					} 
					else 
					{
						prefix = "";
					}
					addToCheckingHistory("[ACTION] " + checkingTransactionNotes[i] + ": " + prefix+ String.format("%.2f", checkingTransactionAmounts[i]) + " JD");
				}
				showInformationMessage("Checking transactions sorted by size");
			}
		}
	}
	private void enableSideButtons(boolean on) 
	{
		depositButton.setEnabled(on);
		withdrawButton.setEnabled(on);
		balanceButton.setEnabled(on);
		historyButton.setEnabled(on);
		interestButton.setEnabled(on);
		feeButton.setEnabled(on);
		sortButton.setEnabled(on);
		transferButton.setEnabled(on);
	}
	private void showErrorMessage(String msg) 
	{
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private void showInformationMessage(String msg)
	{
		JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
	}
	public static void main(String[] args) 
	{
		new ATMGui();
	}
}
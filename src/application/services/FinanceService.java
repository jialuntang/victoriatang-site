package application.services;

import java.util.ArrayList;
import java.util.List;

import application.Account;
import application.data.DataManager;
import application.model.Transaction;
import application.model.TransactionStatus;

public class FinanceService {
    private final DataManager dataManager;
    private Account currentAccount;

    public FinanceService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public boolean login(String username, String password) {
        if (dataManager.authenticateUser(username, password)) {
            currentAccount = dataManager.getAccountByUsername(username);
            return true;
        }
        currentAccount = null;
        return false;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public Account getAccount() {
        return currentAccount;
    }

    public boolean createUser(String username, String password) {
        if (dataManager.getAccountByUsername(username) != null) {
            return false;
        }
        Account account = dataManager.createAccount(username, password);
        currentAccount = account;
        return true;
    }

    public boolean transferMoney(Account recipient, double amount, String description) {
        Account sender = getCurrentAccount();
        if (sender == null || recipient == null || amount <= 0 || sender.getBalance() < amount) {
            return false;
        }

        // Create pending transaction
        Transaction transaction = dataManager.createTransaction(sender, recipient, amount, description);

        // Update balances
        dataManager.updateAccountBalance(sender, sender.getBalance() - amount);
        dataManager.updateAccountBalance(recipient, recipient.getBalance() + amount);

        // Mark transaction as completed
        dataManager.updateTransactionStatus(transaction, TransactionStatus.COMPLETED);
        return true;
    }


    public List<Transaction> getRecentTransactions(int limit) {
        Account account = getCurrentAccount();
        return account != null ? dataManager.getRecentTransactions(account, limit) : new ArrayList<>();
    }

    public List<Transaction> getPendingTransactions() {
        Account account = getCurrentAccount();
        return account != null ? dataManager.getPendingTransactions(account) : new ArrayList<>();
    }

    public double getTotalIncoming() {
        Account account = getCurrentAccount();
        return account != null ? dataManager.getTotalIncoming(account) : 0.0;
    }

    public double getTotalOutgoing() {
        Account account = getCurrentAccount();
        return account != null ? dataManager.getTotalOutgoing(account) : 0.0;
    }
} 
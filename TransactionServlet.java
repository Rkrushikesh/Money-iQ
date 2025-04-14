package bank;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class TransactionServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation"); // 'transaction' or 'balanceInquiry'
        int accountId = Integer.parseInt(request.getParameter("accountId"));
        int enteredPin = Integer.parseInt(request.getParameter("pin"));

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Verify PIN
            PreparedStatement pinStmt = connection.prepareStatement("SELECT PIN, Balance FROM BankAccount WHERE AccountID = ?");
            pinStmt.setInt(1, accountId);
            ResultSet pinResult = pinStmt.executeQuery();

            if (!pinResult.next() || pinResult.getInt("PIN") != enteredPin) {
                response.getWriter().println("Invalid PIN. Operation denied.");
                return;
            }

            double balance = pinResult.getDouble("Balance"); // Fetch balance for potential use

            if ("balanceInquiry".equalsIgnoreCase(operation)) {
                // Show balance
                response.getWriter().println("Your current balance is: " + balance);
                return;
            } else if ("transaction".equalsIgnoreCase(operation)) {
                // Perform Transaction
                int toAccountId = Integer.parseInt(request.getParameter("toAccount"));
                double amount = Double.parseDouble(request.getParameter("amount"));

                connection.setAutoCommit(false); // Start transaction

                try {
                    // Debit from the source account
                    PreparedStatement debitStmt = connection.prepareStatement("UPDATE BankAccount SET Balance = Balance - ? WHERE AccountID = ?");
                    debitStmt.setDouble(1, amount);
                    debitStmt.setInt(2, accountId);
                    debitStmt.executeUpdate();

                    // Log the debit transaction
                    PreparedStatement debitLogStmt = connection.prepareStatement(
                        "INSERT INTO Transaction (AccountID, TransactionType, CounterpartyAccountID, TransactionAmount) VALUES (?, ?, ?, ?)"
                    );
                    debitLogStmt.setInt(1, accountId);
                    debitLogStmt.setString(2, "Debit");
                    debitLogStmt.setInt(3, toAccountId);
                    debitLogStmt.setDouble(4, amount);
                    debitLogStmt.executeUpdate();

                    // Credit to the target account
                    PreparedStatement creditStmt = connection.prepareStatement("UPDATE BankAccount SET Balance = Balance + ? WHERE AccountID = ?");
                    creditStmt.setDouble(1, amount);
                    creditStmt.setInt(2, toAccountId);
                    creditStmt.executeUpdate();

                    // Log the credit transaction
                    PreparedStatement creditLogStmt = connection.prepareStatement(
                        "INSERT INTO Transaction (AccountID, TransactionType, CounterpartyAccountID, TransactionAmount) VALUES (?, ?, ?, ?)"
                    );
                    creditLogStmt.setInt(1, toAccountId);
                    creditLogStmt.setString(2, "Credit");
                    creditLogStmt.setInt(3, accountId);
                    creditLogStmt.setDouble(4, amount);
                    creditLogStmt.executeUpdate();

                    connection.commit(); // Commit transaction
                    response.getWriter().println("Transaction Successful!");
                } catch (Exception e) {
                    connection.rollback(); // Rollback on error
                    throw new ServletException("Transaction failed: " + e.getMessage(), e);
                }
            } else {
                response.getWriter().println("Invalid operation.");
            }
        } catch (SQLException e) {
            throw new ServletException("Database connection error: " + e.getMessage(), e);
        }
    }
}

package bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TransactionServlet")
public class TransitionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String senderAccount = request.getParameter("senderAccount");
        String receiverAccount = request.getParameter("receiverAccount");
        String amountStr = request.getParameter("amount");
        String pin = request.getParameter("pin");

        if (senderAccount == null || receiverAccount == null || amountStr == null || pin == null ||
            senderAccount.trim().isEmpty() || receiverAccount.trim().isEmpty() || amountStr.trim().isEmpty() || pin.trim().isEmpty()) {
            response.getWriter().println("Error: All fields are required!");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "root");
            con.setAutoCommit(false); // Ensuring Atomicity

            // Verify PIN
            String pinQuery = "SELECT pin FROM bank_accounts WHERE account_number = ?";
            PreparedStatement pinStmt = con.prepareStatement(pinQuery);
            pinStmt.setString(1, senderAccount);
            ResultSet pinRs = pinStmt.executeQuery();

            if (!pinRs.next() || !pinRs.getString("pin").equals(pin)) {
                response.getWriter().println("Error: Invalid PIN!");
                con.rollback();
                return;
            }
            pinStmt.close();

            // Check Sender's Balance
            String balanceQuery = "SELECT initial_deposit FROM bank_accounts WHERE account_number = ?";
            PreparedStatement balanceStmt = con.prepareStatement(balanceQuery);
            balanceStmt.setString(1, senderAccount);
            ResultSet balanceRs = balanceStmt.executeQuery();

            if (!balanceRs.next() || balanceRs.getDouble("initial_deposit") < amount) {
                response.getWriter().println("Error: Insufficient Funds!");
                con.rollback();
                return;
            }
            balanceStmt.close();

            // Debit Sender
            String debitQuery = "UPDATE bank_accounts SET initial_deposit = initial_deposit - ? WHERE account_number = ?";
            PreparedStatement debitStmt = con.prepareStatement(debitQuery);
            debitStmt.setDouble(1, amount);
            debitStmt.setString(2, senderAccount);
            debitStmt.executeUpdate();
            debitStmt.close();

            // Credit Receiver
            String creditQuery = "UPDATE bank_accounts SET initial_deposit = initial_deposit + ? WHERE account_number = ?";
            PreparedStatement creditStmt = con.prepareStatement(creditQuery);
            creditStmt.setDouble(1, amount);
            creditStmt.setString(2, receiverAccount);
            creditStmt.executeUpdate();
            creditStmt.close();

            // Insert into transactions table
            String transactionQuery = "INSERT INTO transactions (sender_account, receiver_account, amount, status, remarks) VALUES (?, ?, ?, 'SUCCESS', 'Transaction completed successfully')";
            PreparedStatement transactionStmt = con.prepareStatement(transactionQuery);
            transactionStmt.setString(1, senderAccount);
            transactionStmt.setString(2, receiverAccount);
            transactionStmt.setDouble(3, amount);
            transactionStmt.executeUpdate();
            transactionStmt.close();

            con.commit(); // Ensuring Durability
            response.getWriter().println("Transaction Successful!");

        } catch (Exception e) {
            try {
                if (con != null) con.rollback(); // Rollback ensures Atomicity in case of failure
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            response.getWriter().println("Transaction Failed: " + e.getMessage());

            // Log failed transaction
            try {
                if (con != null) {
                    String failQuery = "INSERT INTO transactions (sender_account, receiver_account, amount, status, remarks) VALUES (?, ?, ?, 'FAILED', ?)";
                    PreparedStatement failStmt = con.prepareStatement(failQuery);
                    failStmt.setString(1, senderAccount);
                    failStmt.setString(2, receiverAccount);
                    failStmt.setDouble(3, amount);
                    failStmt.setString(4, e.getMessage());
                    failStmt.executeUpdate();
                    failStmt.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

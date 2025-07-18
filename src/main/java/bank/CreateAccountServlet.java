package bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CreateAccountServlet")
public class CreateAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set request encoding to handle special characters
        request.setCharacterEncoding("UTF-8");

        // Fetch user input from UI
        String name = request.getParameter("name");
        String accountType = request.getParameter("accountType");
        String initialDepositStr = request.getParameter("initialDeposit");
        String pin = "" + new Random().nextInt(200000);

        // Validate input
        if (name == null || accountType == null || initialDepositStr == null ||
            name.trim().isEmpty() || accountType.trim().isEmpty() || initialDepositStr.trim().isEmpty()) {
            response.getWriter().println("Error: All fields are required!");
            return;
        }

        double initialDeposit = Double.parseDouble(initialDepositStr);	
//        int pin = Integer.parseInt

        // Generate a random account number (10-digit)
        String accountNumber = generateAccountNumber();

        // Generate an IFSC Code (Static Prefix + Random Suffix)
        String ifscCode = "BANK12345" + new Random().nextInt(99); // BANK12345XX

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database Connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "root");

            // SQL Query to insert bank account details
            String query = "INSERT INTO bank_accounts (account_number, ifsc_code, name, account_type, initial_deposit , pin) VALUES (?, ?, ?, ?, ?,?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, accountNumber);
            stmt.setString(2, ifscCode);
            stmt.setString(3, name);
            stmt.setString(4, accountType);
            stmt.setDouble(5, initialDeposit);
            stmt.setString(6, pin);

            // Execute Update
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                response.getWriter().println("Account Created Successfully!<br>");
                response.getWriter().println("Account Number: " + accountNumber + "<br>");
                response.getWriter().println("IFSC Code: " + ifscCode);
//                response.getWriter().println("pin Code: " + pin);
            } else {
                response.getWriter().println("Error: Could not create the account.");
            }

            // Close Connection
            stmt.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Database Error: " + e.getMessage());
        }
    }
 
     
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");

        String accountNumber = request.getParameter("accountNumber");

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            response.getWriter().println("Error: Please provide an account number.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "root");

            // Retrieve stored PIN for the given account number
            String query = "SELECT pin FROM bank_accounts WHERE account_number = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, accountNumber);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                response.getWriter().println("Your PIN is: " + rs.getString("pin"));
            } else {
                response.getWriter().println("Error: Account not found!");
            }

            rs.close();
            stmt.close();
            con.close();

        } catch (Exception e) {
            response.getWriter().println("Database Error: " + e.getMessage());
        }
    }
    
    
    
    // Method to generate a unique 10-digit account number
    private String generateAccountNumber() {
        Random rand = new Random();
        return "AC" + (1000000000 + rand.nextInt(900000000)); // Generates ACXXXXXXXXXX
    }
}

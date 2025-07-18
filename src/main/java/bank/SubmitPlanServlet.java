package bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SubmitPlanServlet")
public class SubmitPlanServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Ensure correct character encoding for text inputs
        request.setCharacterEncoding("UTF-8");

        // Retrieve text from the textarea
        String planType = request.getParameter("planType");

        // Debugging: Print received parameter to console
        System.out.println("Received Plan Type: " + planType);

        // Validate input before inserting into the database
        if (planType == null || planType.trim().isEmpty()) {
            response.getWriter().println("Error: Plan Type is missing or empty!");
            return; // Stop execution if value is null
        }

        try {
            // Load MySQL Driver (Ensure you have MySQL connector dependency)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "root");

            // SQL Query to store plan details
            String query = "INSERT INTO plan (plan_type) VALUES (?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, planType);

            // Execute Update
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                response.getWriter().println("Plan submitted successfully!");
            } else {
                response.getWriter().println("Error: Could not submit the plan.");
            }

            // Close resources
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Database Error: " + e.getMessage());
        }
    }
}
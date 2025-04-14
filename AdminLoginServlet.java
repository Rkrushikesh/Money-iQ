package bank;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import java.io.IOException;
import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
        String email = request.getParameter("email");
        String password = request.getParameter("password");
          
        
        try( Connection connection = DatabaseConnection.getConnection()) {
            // Connect to the database
     
            // Query to check credentials
            String sql = "SELECT password, email FROM adminlogindata WHERE email = ? AND password = ?";
     
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password); // Use hashing for real-world applications
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Valid user, create a session
                HttpSession session = request.getSession();
                session.setAttribute("email", email);
                session.setAttribute("isLoggedIn", true);
//                session.setAttribute("password" , password);

                // Redirect to the home page
                response.sendRedirect("AdminDashBoard.html");
            } else {
                // Invalid credentials, redirect to login with an error
            	
            	
                response.sendRedirect("signUp.html?error=invalid");
                response.getWriter().write("You Dont have Account SignUP please ");
            }

            rs.close();
            stmt.close();
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
          
    }
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the user's session (if it exists)
        HttpSession session = request.getSession(false);

        // Check login status
        if (session == null || session.getAttribute("isLoggedIn") == null) {
            // User is not logged in
            response.setContentType("text/html");
            response.getWriter().write("<html><body>");
            response.getWriter().write("<h3>Please log in to view your DashBoard .</h3>");
            response.getWriter().write("<a href='LoginAdmin.html'>Go to Admin Login</a>");
            response.getWriter().write("</body></html>");
        } else {
            // User is logged in, show account details
           
            
            response.sendRedirect("AdminDashBoard.html");

        }
    
    
    }
    
}



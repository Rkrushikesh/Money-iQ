package bank;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AccountServlet")
public class AccountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the user's session (if it exists)
        HttpSession session = request.getSession(false);

        // Check login status
        if (session == null || session.getAttribute("isLoggedIn") == null) {
            // User is not logged in
            response.setContentType("text/html");
            response.getWriter().write("<html><body>");
            response.getWriter().write("<h3>Please log in to view your account details.</h3>");
            response.getWriter().write("<a href='login.html'>Go to Login</a>");
            response.getWriter().write("</body></html>");
        } else {
            // User is logged in, show account details
        	 response.sendRedirect("Accounts.html");
    
        }
    }
}
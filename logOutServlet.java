package bank;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/logOutServlet")

public class logOutServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request , HttpServletResponse response )
	throws ServletException , IOException {
		
		HttpSession session = request.getSession(false);



		        if (session != null) {
		            session.invalidate(); // Logout: invalidate session
		        }

		        // Redirect to login or home page
		        response.sendRedirect("login.html");
		    }
		
	}
	
 
	
	
	
	 



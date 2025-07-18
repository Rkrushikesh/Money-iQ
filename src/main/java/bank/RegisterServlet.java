package bank;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.print.DocFlavor.INPUT_STREAM;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;  
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form parameters
   
//        Part filePart = request.getPart("photo");
//        InputStream inputStream = null;
        String fullName = request.getParameter("fullName");
        String dob = request.getParameter("dob");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String postalCode = request.getParameter("postalCode");
        String country = request.getParameter("country");
        String phoneNumber = request.getParameter("phone");
        String emailAddress = request.getParameter("email");
        String identificationType = request.getParameter("idType");
        String identificationNumber = request.getParameter("idNumber");
        String panCardNumber = request.getParameter("pan");
        String accountType = request.getParameter("accountType");
        String initialDepositAmount = request.getParameter("initialDeposit");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String securityQuestion = request.getParameter("securityQuestion");
        String securityAnswer = request.getParameter("securityAnswer");
        
        
        
        
        
//        for(int i = 0 ;i<fullName.length(); i++) {
//     	   
//            if(!(fullName.charAt(i) > 'A' && fullName.charAt(i) < 'Z' || fullName.charAt(i) > 'a' && fullName.charAt(i) < 'z') )
//            {
//         	   System.out.println("wrong Credential ");
//         	   PrintWriter out = response.getWriter();
//         	   out.print("Invalid Name use only character ");
//         			   return;
//             }
//            
//            
//            }
//            if(filePart != null) {
//            	inputStream  = filePart.getInputStream();
//            	
//            	
//            }
//    
       try (Connection connection = DatabaseConnection.getConnection()) {
           // Insert data into the database
           String sql = "INSERT INTO Users (full_name ,dob, address, city, state_province, postal_code, country, "
                   + "phone_number, email_address, identification_type, identification_number, pan_card_number, "
                   + "account_type, initial_deposit_amount, username, password, security_question, security_answer) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

           PreparedStatement preparedStatement = connection.prepareStatement(sql);
        
//           preparedStatement.setString(1, fullName);
//           if(inputStream != null) {
//        	   preparedStatement.setBlob(2,inputStream);
//        	   
//           }else {
//        	   preparedStatement.setNull(2, java.sql.Types.BLOB);
//           }
           
           preparedStatement.setString(1,fullName);
           preparedStatement.setString(2, dob);
           preparedStatement.setString(3, address);
           preparedStatement.setString(4, city);
           preparedStatement.setString(5, state);
           preparedStatement.setString(6, postalCode);
           preparedStatement.setString(7, country);
           preparedStatement.setString(8, phoneNumber);
           preparedStatement.setString(9, emailAddress);
           preparedStatement.setString(10, identificationType);
           preparedStatement.setString(11, identificationNumber);
           preparedStatement.setString(12, panCardNumber);
           preparedStatement.setString(13, accountType);
           preparedStatement.setString(14, initialDepositAmount);
           preparedStatement.setString(15, username);
           preparedStatement.setString(16, password);
           preparedStatement.setString(17, securityQuestion);
           preparedStatement.setString(18, securityAnswer);

           preparedStatement.executeUpdate();

           
//           ResultSet resultSet = preparedStatement.executeQuery();
//           if (resultSet.next()) {
//               byte[] imgData = resultSet.getBytes("profile_photo");
//
//               if (imgData != null) {
//                   response.setContentType("image/jpeg"); // Ensure correct content type
//                   response.setContentLength(imgData.length);
//                   response.getOutputStream().write(imgData);
//               } 
           
           
           response.setContentType("text/html");
           PrintWriter out = response.getWriter();
           out.println("<h2>Account Created Successfully!</h2>");
           response.sendRedirect("login.html");
           
       } catch (SQLException e) {
           throw new ServletException("Database connection error", e);
       }
        
      
    }
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
               
                
                response.sendRedirect("Register.html");

            }
        }
    }
    
    


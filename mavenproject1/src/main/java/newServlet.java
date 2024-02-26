/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.lookups.v1.PhoneNumber;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
/*import com.twilio.type.PhoneNumber;*/
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sara
 */
public class newServlet extends HttpServlet {

    private static final String ACCOUNT_SID = "AC00a5a55bf7bce79117818a1dc21f24b9";
    private static final String AUTH_TOKEN = "9f5385520427ba30386f23fde8911ee4";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter pen = response.getWriter();
        String toNumber = request.getParameter("toNumber");
        String message = request.getParameter("message");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Send SMS using Twilio
        try {
            Message.creator(
                    new com.twilio.type.PhoneNumber(toNumber),
                    new com.twilio.type.PhoneNumber("+18155915841"), // Twilio phone number
                    message)
                    .create();
        } catch (Exception e) {
            e.printStackTrace();
            pen.println("Failed to send message: " + e.getMessage());
            return; // Abort further processing if message sending fails
        }

        String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/webdev";
        String dbUsername = "postgres";
        String dbPassword = "2801";
        try {
            Class.forName("org.postgresql.Driver");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(newServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (
          Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO sms_records (to_number, from_number, message, timestamp) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, toNumber);
                pstmt.setString(2, "+18155915841");
                pstmt.setString(3, message);
                pstmt.setTimestamp(4, timestamp);
                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("A new message was inserted successfully.");
                } else {
                    System.out.println("Failed to insert the message.");
                }

                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/plain");
            response.getWriter().write("Error processing the message." + e.getMessage());
            return;
        }

    }

}

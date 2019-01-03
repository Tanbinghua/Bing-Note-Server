package com.note;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import com.info.ResInfo;

@WebServlet(name = "Signin")
public class Signin extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(405, "Method Not Allowed");
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        ServletContext application = this.getServletContext();
        String dbhost = application.getInitParameter("dbhost");
        String dbuser = application.getInitParameter("dbuser");
        String dbpass = application.getInitParameter("dbpass");
        String driverClassName = application.getInitParameter("driverClassName");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);
        if (session.getAttribute("userEmail") != null) {
            ResInfo info = new ResInfo(304, "You haven't log out!", false);
            out.println(info.getResJson());
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null) {
            ResInfo info = new ResInfo(304, "email or password can't be null!", false);
            out.println(info.getResJson());
            return;
        }

        try {
            Class.forName(driverClassName);
            dbhost = dbhost + "?useUnicode=true&characterEncoding=utf-8";
            Connection conn = DriverManager.getConnection(dbhost, dbuser, dbpass);

            String sql = "select id from note_user where email = ? and password = password(?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                session.setAttribute("userEmail", email);

                ResInfo info = new ResInfo(200, "Success to login!", true);
                out.println(info.getResJson());

                ps.close();
                rs.close();
                conn.close();
            } else {
                ResInfo info = new ResInfo(304, "User not exits!", false);
                out.println(info.getResJson());
            }
        } catch (Exception e) {
            response.sendError(500, "Something wrong in database!");
            e.printStackTrace();
        }
    }
}

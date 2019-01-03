package com.note;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import com.info.ResInfo;

@WebServlet(name = "Signup")
public class Signup extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(405, "Method Not Allowed!");
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
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
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String code = request.getParameter("code");

        if (email == null || name == null || password == null || code == null) {
            ResInfo info = new ResInfo(304, "email, name or password can't be null!", false);
            out.println(info.getResJson());
            return;
        }

//        if (!code.equals(session.getAttribute("code"))) {
//            ResInfo info = new ResInfo(304, "Verification code is wrong!", false);
//            out.println(info.getResJson());
//            return;
//        }
//        session.removeAttribute("code");

        try {
            Class.forName(driverClassName);
            dbhost = dbhost + "?useUnicode=true&characterEncoding=utf-8";
            Connection conn = DriverManager.getConnection(dbhost, dbuser, dbpass);

            String sql = "select email from note_user where email = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rsUser = ps.executeQuery();
            if (rsUser.next()) {
                ResInfo info = new ResInfo(304, "User has exits!", false);
                out.println(info.getResJson());
            } else {
                String sqlUser = "insert into note_user (name, email, password) values (?, ?, password(?));";
                PreparedStatement psCreate = conn.prepareStatement(sqlUser);
                psCreate.setString(1, name);
                psCreate.setString(2, email);
                psCreate.setString(3, password);
                psCreate.executeUpdate();
                psCreate.close();
                ResInfo info = new ResInfo(200, "Success to create user!", true);
                out.println(info.getResJson());
            }

            rsUser.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            response.sendError(500, "Something wrong in database!");
            e.printStackTrace();
        }
    }
}

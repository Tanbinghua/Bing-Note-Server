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

@WebServlet(name = "SetInfo")
public class SetInfo extends HttpServlet {
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
        if (session.getAttribute("userEmail") == null) {
            ResInfo info = new ResInfo(403, "You haven't log in!", false);
            out.println(info.getResJson());
            return;
        }

        String email = (String) session.getAttribute("userEmail");
        String theme = request.getParameter("theme");
        String language = request.getParameter("language");
        String avatar = request.getParameter("avatar");

        try {
            Class.forName(driverClassName);
            dbhost = dbhost + "?useUnicode=true&characterEncoding=utf-8";
            Connection conn = DriverManager.getConnection(dbhost, dbuser, dbpass);

            if (theme != null) {
                String sql = "update note_user set theme = ? where email = ?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, theme);
                ps.setString(2, email);
                ps.executeUpdate();
                ps.close();
            } else if (language != null) {
                String sql = "update note_user set language = ? where email = ?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, language);
                ps.setString(2, email);
                ps.executeUpdate();
                ps.close();
            } else if (avatar != null) {
                String sql = "update note_user set avatar = ? where email = ?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, avatar);
                ps.setString(2, email);
                ps.executeUpdate();
                ps.close();
            }

            ResInfo info = new ResInfo(200, "Success to update msg!", true);
            out.println(info.getResJson());

            conn.close();
        } catch (Exception e) {
            response.sendError(500, "Something wrong in database!");
            e.printStackTrace();
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(405, "Method Not Allowed!");
    }
}

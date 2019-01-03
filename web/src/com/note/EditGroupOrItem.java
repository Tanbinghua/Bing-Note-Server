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

@WebServlet(name = "EditGroupOrItem")
public class EditGroupOrItem extends HttpServlet {
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
        if (session.getAttribute("userEmail") == null) {
            ResInfo info = new ResInfo(403, "You haven't log in!", false);
            out.println(info.getResJson());
            return;
        }

        String type = request.getParameter("type");
        String title = request.getParameter("title");
        String id = request.getParameter("id");

        if (type == null || title == null || id == null) {
            ResInfo info = new ResInfo(304, "type, title or id can't be null!", false);
            out.println(info.getResJson());
            return;
        }

        try {
            Class.forName(driverClassName);
            dbhost = dbhost + "?useUnicode=true&characterEncoding=utf-8";
            Connection conn = DriverManager.getConnection(dbhost, dbuser, dbpass);

            String sql = "";
            if (type.equals("group")) {
                sql = "update note_group set title = ?, update_time = now() where id = ?;";
            } else if (type.equals("item")) {
                sql = "update note_item set title = ?, update_time = now() where id = ?;";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, id);
            ps.executeUpdate();
            ps.close();

            ResInfo info = new ResInfo(200, "Success to update!", true);
            out.println(info.getResJson());

            conn.close();
        } catch (Exception e) {
            response.sendError(500, "Something wrong in database!");
            e.printStackTrace();
        }
    }
}

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
import net.sf.json.JSONObject;

@WebServlet(name = "GetInfo")
public class GetInfo extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
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
            ResInfo info = new ResInfo(403, "You haven't log in", false);
            out.println(info.getResJson());
            return;
        }

        String email = (String) session.getAttribute("userEmail");

        try {
            Class.forName(driverClassName);
            dbhost = dbhost + "?useUnicode=true&characterEncoding=utf-8";
            Connection conn = DriverManager.getConnection(dbhost, dbuser, dbpass);

            String sql = "select id, name, email, avatar, theme, language, create_time from note_user where email = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JSONObject resJson = new JSONObject();
                JSONObject info = new JSONObject();
                info.put("id", rs.getString("id"));
                info.put("name", rs.getString("name"));
                info.put("email", rs.getString("email"));
                info.put("avatar", rs.getString("avatar"));
                info.put("theme", rs.getString("theme"));
                info.put("language", rs.getString("language"));
                info.put("create_time", rs.getString("create_time"));

                resJson.put("code", 200);
                resJson.put("msg", "success");
                resJson.put("success", true);
                resJson.put("data", info);

                out.println(resJson);

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
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(405, "Method Not Allowed!");
    }
}

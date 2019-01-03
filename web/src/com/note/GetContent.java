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

@WebServlet(name = "GetContent")
public class GetContent extends HttpServlet {
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

        String id = request.getParameter("id");
        if (id == null) {
            ResInfo info = new ResInfo(304, "Id required!", false);
            out.println(info.getResJson());
            return;
        }

        try {
            Class.forName(driverClassName);
            dbhost = dbhost + "?useUnicode=true&characterEncoding=utf-8";
            Connection conn = DriverManager.getConnection(dbhost, dbuser, dbpass);

            String sql = "select id, title, type, content, create_time, update_time from note_item where id = ? and type = 'content';";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JSONObject resJson = new JSONObject();

                JSONObject data = new JSONObject();
                data.put("id", rs.getString("id"));
                data.put("title", rs.getString("title"));
                data.put("type", rs.getString("type"));
                data.put("content", rs.getString("content"));
                data.put("create_time", rs.getString("create_time"));
                data.put("update_time", rs.getString("update_time"));

                resJson.put("code", 200);
                resJson.put("data", data);
                resJson.put("msg", "Success to get content!");
                resJson.put("success", true);

                out.println(resJson);
            } else {
                ResInfo info = new ResInfo(404, "Content not found!", false);
                out.println(info.getResJson());
            }

            conn.close();
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

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

@WebServlet(name = "NewGroupOrItem")
public class NewGroupOrItem extends HttpServlet {
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
        String user_id = request.getParameter("user_id");
        String group_id = request.getParameter("group_id");

        if (type == null || title == null || user_id == null) {
            ResInfo info = new ResInfo(304, "email, name or password can't be null!", false);
            out.println(info.getResJson());
            return;
        }

        try {
            Class.forName(driverClassName);
            dbhost = dbhost + "?useUnicode=true&characterEncoding=utf-8";
            Connection conn = DriverManager.getConnection(dbhost, dbuser, dbpass);

            String resSql = "";
            if (type.equals("group")) {
                String sql = "insert into note_group (title, user_id) values (?, ?);";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, title);
                ps.setString(2, user_id);
                ps.executeUpdate();
                ps.close();
                resSql ="select id, title, type, create_time, update_time from note_group where create_time <= now() order by create_time desc limit 1;";
            } else if (type.equals("item")) {
                String sql = "insert into note_item (title, user_id, group_id) values (?, ?, ?);";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, title);
                ps.setString(2, user_id);
                ps.setString(3, group_id);
                ps.executeUpdate();
                ps.close();
                resSql ="select id, title, type, create_time, update_time from note_item where create_time <= now() order by create_time desc limit 1;";
            }

            Statement st = conn.createStatement();
            ResultSet itemRs = st.executeQuery(resSql);
            JSONObject resJson = new JSONObject();
            JSONObject data = new JSONObject();
            if (itemRs.next()) {
                data.put("id", itemRs.getString("id"));
                data.put("title", itemRs.getString("title"));
                data.put("type", itemRs.getString("type"));
                data.put("create_time", itemRs.getString("create_time"));
                data.put("update_time", itemRs.getString("update_time"));
            }
            st.close();
            itemRs.close();
            resJson.put("code", 200);
            resJson.put("data", data);
            resJson.put("msg", "Success to add!");
            resJson.put("success", true);
            out.println(resJson);

            conn.close();
        } catch (Exception e) {
            response.sendError(500, "Something wrong in database!");
            e.printStackTrace();
        }
    }
}

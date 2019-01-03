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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet(name = "GetSide")
public class GetSide extends HttpServlet {
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

            String sql = "select id from note_user where email = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String groupSql ="select id, title, type, create_time, update_time from note_group where user_id = ? and type <> 'trash';";
                PreparedStatement groupPs = conn.prepareStatement(groupSql);
                groupPs.setString(1, rs.getString("id"));
                ResultSet groupRs = groupPs.executeQuery();
                JSONObject resJson = new JSONObject();
                JSONArray data = new JSONArray();
                while (groupRs.next()) {
                    JSONObject group = new JSONObject();
                    group.put("id", groupRs.getString("id"));
                    group.put("title", groupRs.getString("title"));
                    group.put("type", groupRs.getString("type"));
                    group.put("create_time", groupRs.getString("create_time"));
                    group.put("update_time", groupRs.getString("update_time"));

                    String itemSql = "select id, title, type, create_time, update_time from note_item where group_id = ? and type <> 'trash';";
                    PreparedStatement itemPs = conn.prepareStatement(itemSql);
                    itemPs.setString(1, groupRs.getString("id"));
                    ResultSet itemRs = itemPs.executeQuery();
                    JSONArray array = new JSONArray();
                    while (itemRs.next()) {
                        JSONObject item = new JSONObject();
                        item.put("id", itemRs.getString("id"));
                        item.put("title", itemRs.getString("title"));
                        item.put("type", itemRs.getString("type"));
                        item.put("create_time", itemRs.getString("create_time"));
                        item.put("update_time", itemRs.getString("update_time"));
                        array.add(item);
                    }
                    itemPs.close();
                    itemRs.close();

                    group.put("item", array);
                    data.add(group);
                }
                resJson.put("code", 200);
                resJson.put("data", data);
                resJson.put("msg", "Success to get side!");
                resJson.put("success", true);

                out.println(resJson);
                groupPs.close();
                groupRs.close();
            } else {
                ResInfo info = new ResInfo(304, "User not exits!", false);
                out.println(info.getResJson());
            }

            rs.close();
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

package com.note;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import com.info.ResInfo;

@WebServlet(name = "SignOut")
public class SignOut extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");

        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);
        if (session.getAttribute("userEmail") == null) {
            ResInfo info = new ResInfo(403, "You haven't log in", false);
            out.println(info.getResJson());
            return;
        }

        session.removeAttribute("userEmail");
        session.invalidate();
        ResInfo info = new ResInfo(200, "Log out!", true);
        out.println(info.getResJson());
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(405, "Method Not Allowed!");
    }
}

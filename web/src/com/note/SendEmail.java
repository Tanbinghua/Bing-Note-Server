package com.note;

import com.info.ResInfo;
import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/sendEmail")
public class SendEmail extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(405, "Method Not Allowed!");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();

        ServletContext application = this.getServletContext();
        String emailPass = application.getInitParameter("emailPass");
        String emailAccount = application.getInitParameter("emailAccount");

        HttpSession ss = request.getSession(true);
        if (ss.getAttribute("userEmail") != null) {
            ResInfo info = new ResInfo(304, "You haven't log out!", false);
            out.println(info.getResJson());
            return;
        }

        String receiverEmail = request.getParameter("email");

        if (receiverEmail == null) {
            ResInfo info = new ResInfo(304, "Email can't be null!", false);
            out.println(info.getResJson());
            return;
        }

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        String myEmailSMTPHost = "smtp.163.com";
        props.setProperty("mail.smtp.host", myEmailSMTPHost);
        props.setProperty("mail.smtp.auth", "true");

        // 根据配置创建会话，用于邮件和服务器交互
        Session session = Session.getInstance(props);
        try {
            String random = RandomStringUtils.randomAlphanumeric(5);
            ss.setAttribute("code", random);

            // 创建一封邮件对象
            MimeMessage message =createMimeMessage(session, emailAccount, receiverEmail, random);
            // 根据session 获取邮件传输对象
            Transport transport = session.getTransport();
            // 使用 邮箱账号和密码连接邮件服务器
            transport.connect(emailAccount, emailPass);
            // 发送邮件，发到所有的收件地址
            transport.sendMessage(message, message.getAllRecipients());
            // 关闭连接
            transport.close();
            ResInfo info = new ResInfo(200, "Success to send email!", true);
            out.println(info.getResJson());
        } catch (Exception e) {
            ResInfo info = new ResInfo(304, "Failed to send email!", false);
            out.println(info.getResJson());
            e.printStackTrace();
        }
    }
    private static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String random) throws Exception{
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sendMail,"Binghua","utf-8"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail,"World","utf-8"));
        message.setSubject("BING-NOTE","utf-8");
        message.setContent("<p>Welcome to <strong>Bing-note</strong></p>\n" +
                "<p><strong>" + random + "</strong> is your verification code!</p>\n" +
                "<p>Please enter the verification on the page.</p>", "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }
}

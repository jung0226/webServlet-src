package com.bitcamp.home;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login.do ")
public class Login extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//로그인 폼
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter pw = response.getWriter();
		pw.println("<html><head><title>로그인 폼</title></head><body>");
		pw.println("<form method='post' action='/webServlet/login.do' >");
		pw.println("아이디: <input type='text' name='userid'/><br/>");
		pw.println("비밀번호 : <input type='password' name='userpwd'><br/>");
		pw.println("<input type='submit' value='로그인'/>");
		pw.println("</form></body></html>");
		
		pw.close();
		
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		//로그인하기
		String userid= req.getParameter("userid");
		String userpwd = req.getParameter("userpwd");
		
		Connection con =null;
		PreparedStatement pstmt = null;
		ResultSet rs=null;
		try {
			//1. 드라이브 로딩
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			//2. DB연결
			con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "scott", "tiger");
			//3. PreparedStatement생성
			String sql = "select userid, username from register where userid=? and userpwd=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, userid);
			pstmt.setString(2, userpwd);
			
			rs=pstmt.executeQuery();
			
			res.setContentType("text/html; charset=UTF-8");
			PrintWriter pw = res.getWriter();
			pw.println("<html><head><title>로그인</title></head><body>");			
			if(rs.next()) {
				//로그인 됨->홈페이지
				HttpSession session= req.getSession();
				session.setAttribute("sesUserid", rs.getString(1));
				session.setAttribute("sesUsername", rs.getString(2));
				
				pw.println("<script>");
				pw.println("alert('로그인 성공 하였습니다. 홈페이지로 이동합니다.');");
				pw.println("location.href='/webServlet/test.do'");
			}else {
				//로그인 안 됨->로그인 폼
				pw.println("<script>");
				pw.println("alert('로그인 실패하였습니다. 다시 시도하세요.')");
				pw.println("history.back()");
			}
			pw.println("</script>");
			pw.println("</body></html>");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs!=null)rs.close();
				if(pstmt!=null)pstmt.close();
				if(con!=null)con.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}

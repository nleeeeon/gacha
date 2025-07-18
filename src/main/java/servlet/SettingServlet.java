package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class Setting
 */
@WebServlet("/Setting")
public class SettingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String SignificantStr = request.getParameter("Significant");
		String digitsStr = request.getParameter("digits");
		String decimalStr = request.getParameter("decimal");
		
		boolean inputError = false;
		String errorMsg = "";
		try {
            int Significant = Integer.parseInt(SignificantStr);
            int digits = Integer.parseInt(digitsStr);
            int decimal = Integer.parseInt(decimalStr);
            session.setAttribute("Significant", Significant);
            session.setAttribute("digits", digits);
            session.setAttribute("decimal",decimal);
            if(Significant < digits) {
            	inputError = true;
                errorMsg = "”結果の有効数字の表示桁数”が”計算する際の有効桁数”より大きい数字になってます。";
            }
        } catch(Exception e) {
            inputError = true;
            errorMsg = "入力に誤りがあります。";
            
        }
		request.setAttribute("inputError", inputError);
		request.setAttribute("errorMsg", errorMsg);
		request.getRequestDispatcher("setting.jsp").forward(request, response);
		return;
	}

}

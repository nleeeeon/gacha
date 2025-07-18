package servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ガチャ.FindExpectedValue;
import ガチャ.SignificantFigures;

/**
 * Servlet implementation class ExpectedServlet
 */
@WebServlet("/expected")
public class ExpectedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static MathContext MC = new MathContext(0); // 有効数字

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		
		String[] probabilityStrs = request.getParameterValues("probability");
        String[] thresholdStrs = request.getParameterValues("threshold");
        String quantityStr = request.getParameter("quantity");
                
        session.setAttribute("probabilityStrs", probabilityStrs);
        session.setAttribute("thresholdStrs", thresholdStrs);
        session.setAttribute("quantityStr", quantityStr);
        // 入力チェック・計算
        boolean inputError = false;
        String errorMsg = "";
        BigDecimal[] pArr = null;
        int[] reqArr = null;
        BigDecimal sumP = BigDecimal.ZERO;
        int quantity = 0;    // 当てるアイテム数
        BigDecimal select = BigDecimal.ZERO;   // あてる確率
        BigInteger currentCnt = BigInteger.ZERO;//動的の回数
        try {
            quantity = Integer.parseInt(quantityStr);
            int s = (int)session.getAttribute("Significant");
            MC = new MathContext(s, RoundingMode.HALF_UP);
        } catch(Exception e) { }
        boolean isSubmitted = (quantityStr != null 
                && probabilityStrs != null && thresholdStrs != null);
        request.setAttribute("isSubmitted",isSubmitted);
        if(!isSubmitted) {
        	request.getRequestDispatcher("expected.jsp").forward(request, response);
        	return;
        }
        
        pArr = new BigDecimal[quantity];
        reqArr = new int[quantity];
        if(probabilityStrs.length < quantity || thresholdStrs.length < quantity) {
            inputError = true;
            errorMsg = "入力項目が不足しています。すべての項目を入力してください。";
        } else {
            pArr = new BigDecimal[quantity];
            reqArr = new int[quantity];
            for (int i = 0; i < quantity; i++) {
                try {
                    pArr[i] = new BigDecimal (probabilityStrs[i]).divide(BigDecimal.valueOf(100));
                    if(pArr[i].compareTo(BigDecimal.ZERO) <= 0) {
                    	inputError = true;
                        errorMsg = "確率が0%のものがあります。";
                        request.getRequestDispatcher("expected.jsp").forward(request, response);
                        return;
                    }
                    reqArr[i] = Integer.parseInt(thresholdStrs[i]);
                    currentCnt = currentCnt.add(BigInteger.valueOf(reqArr[i]));
                    sumP = sumP.add(pArr[i]);
                } catch(Exception e) {
                    inputError = true;
                    errorMsg = "確率または必要回数の入力に誤りがあります。";
                    break;
                }
            }
            if(select.compareTo(BigDecimal.ONE) >= 0) {
            	inputError = true;
                errorMsg = "指定した確率が100%以上になっています。再度入力してください";
               
            }
            if(sumP.compareTo(BigDecimal.ONE) == 1) {
                inputError = true;
                errorMsg = "全アイテムの確率の合計が1を超えています。再度入力してください。";
            }
            if(!inputError) {
            	FindExpectedValue expect = new FindExpectedValue(MC);
                List<Integer> req = new ArrayList<>();
                for(int i = 0;i < reqArr.length;i++)req.add(reqArr[i]);
                BigDecimal ExpectedValue = expect.calculate(
                		new ArrayList<>(Arrays.asList(pArr)),
                		req,sumP,BigDecimal.ONE);//ここは期待値を求めます
                int n = (int)session.getAttribute("decimal");
                String ExpectedValueStr = SignificantFigures.roundDecimal(ExpectedValue, n);//ここで小数点以下の表示の桁を調整します
                request.setAttribute("ExpectedValue",ExpectedValueStr);
            }else {
            	request.setAttribute("inputError",true);
            	request.setAttribute("errorMsg",errorMsg);
            	
            }
        }
        
    	// JSPへフォワード
                request.getRequestDispatcher("expected.jsp")
                       .forward(request, response);
            	return;
		
		
		
	}

}

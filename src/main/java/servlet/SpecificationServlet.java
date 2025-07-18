package servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ガチャ.InclusionExclusionCalculator;
import ガチャ.SignificantFigures;


@WebServlet("/SpecificationServlet")
public class SpecificationServlet extends HttpServlet {
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
        String selectStr = request.getParameter("select");
                
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
            select = new BigDecimal(selectStr);
            StringBuilder str2 = new StringBuilder();
            str2.append(select).append("%");
            request.setAttribute("select", str2);
            select = select.movePointLeft(2);
            MC = new MathContext(s, RoundingMode.HALF_UP);
        } catch(Exception e) { }
        boolean isSubmitted = (selectStr != null && quantityStr != null 
                && probabilityStrs != null && thresholdStrs != null);
        request.setAttribute("isSubmitted",isSubmitted);
        if(!isSubmitted) {
        	request.getRequestDispatcher("specification.jsp").forward(request, response);
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
                        request.getRequestDispatcher("specification.jsp").forward(request, response);
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
            	InclusionExclusionCalculator calc = new InclusionExclusionCalculator(currentCnt, pArr, reqArr, MC);
		
            	BigDecimal current = calc.probabilityAllAtLeast();
				if(current.compareTo(select) > 0){//ここの処理は最低限の試行回数で指定した確率を超えたときようの処理です
					
				}
	            BigInteger before = new BigInteger("0");
	            //ここで二倍二倍していって指定した確率を超えるところまで続ける
	            //開始が当てるものの個数から２倍からになっちゃって綺麗じゃないけどそのままで続行で
	            do {
	            	before = currentCnt;
	            		currentCnt = currentCnt.multiply(BigInteger.TWO);
	            		calc.nChange(currentCnt);//ここで回数をセット。
	            		current = calc.probabilityAllAtLeast();
	            		
	            }while(select.compareTo(current) > 0);
	
	            //確率を超えたところと超えてないところで二分探索で探していく
				BigInteger l = before;
				BigInteger r = currentCnt;
				BigInteger ans = new BigInteger("0");
	            while(l.compareTo(r) <= 0) {//最終的なansの＋１の回数はselectの確率以上になる
	        		BigInteger mid = l.add(r).divide(BigInteger.TWO);
					calc.nChange(mid);
					current = calc.probabilityAllAtLeast();
					if(current.compareTo(select) >= 0){
						r = mid.subtract(BigInteger.ONE);
						
					}else{
						l = mid.add(BigInteger.ONE);
						ans = mid;
						
						
					}
	        	}
                request.setAttribute("ans",ans);
                request.setAttribute("ans+1",ans.add(BigInteger.ONE));
            	calc.nChange(ans);
            	BigDecimal previous = calc.probabilityAllAtLeast();
            	previous = previous.movePointRight(2);
            	int n = (int)session.getAttribute("decimal");
                String previousStr = SignificantFigures.roundToSignificantFigures(previous, n);//ここで表示する桁を調整します
            	
                calc.nChange(ans.add(BigInteger.ONE));
            	BigDecimal after = calc.probabilityAllAtLeast();
                after = after.movePointRight(2);
                String afterStr = SignificantFigures.roundToSignificantFigures(after, n);//ここで表示する桁を調整します
                
                StringBuilder previousAns = new StringBuilder();
                StringBuilder afterAns = new StringBuilder();
                previousAns.append(previousStr).append("%");
                afterAns.append(afterStr).append("%");
                request.setAttribute("previousAns", previousAns);
                request.setAttribute("afterAns", afterAns);
            }else {
            	request.setAttribute("inputError",true);
            	request.setAttribute("errorMsg",errorMsg);
            	
            }
        }
        
    	// JSPへフォワード
                request.getRequestDispatcher("specification.jsp")
                       .forward(request, response);
            	return;
        
        
        
	}

}

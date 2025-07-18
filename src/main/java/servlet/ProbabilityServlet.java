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

@WebServlet("/probability")
public class ProbabilityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static MathContext MC = new MathContext(0); // 有効数字

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	// 入力パラメータ受け取り
    	
        String countStr = request.getParameter("count");
        String quantityStr = request.getParameter("quantity");
        String[] probabilityStrs = request.getParameterValues("probability");
        String[] thresholdStrs = request.getParameterValues("threshold");
        // グラフ表示用の開始・終了試行回数（オプション）
        String rangeStartStr = request.getParameter("rangeStart");
        String rangeEndStr = request.getParameter("rangeEnd");
        
        request.setAttribute("countStr", countStr);
        session.setAttribute("quantityStr", quantityStr);
        session.setAttribute("probabilityStrs", probabilityStrs);
        session.setAttribute("thresholdStrs", thresholdStrs);
        request.setAttribute("rangeStart", rangeStartStr);
        request.setAttribute("rangeEnd", rangeEndStr);
        boolean isSubmitted = (countStr != null && quantityStr != null 
                                && probabilityStrs != null && thresholdStrs != null);

        BigInteger count = BigInteger.ZERO;
        int quantity = 0;    // 当てるアイテム数
        if(isSubmitted) {
            try {
            	count = new BigInteger(countStr);       // 指定試行回数（結果表示用）
                quantity = Integer.parseInt(quantityStr);
                int s = (int)session.getAttribute("Significant");
                MC = new MathContext(s, RoundingMode.HALF_UP);
            } catch(Exception e) { }
        }
        
        // グラフ用範囲（任意）
        BigInteger rangeStart = BigInteger.ZERO;
        BigInteger rangeEnd = BigInteger.ZERO;
        boolean showGraph = false;
        if(rangeStartStr != null && rangeEndStr != null) {
            try {
                rangeStart = new BigInteger(rangeStartStr);
                rangeEnd = new BigInteger(rangeEndStr);
                if(rangeStart.compareTo(BigInteger.ZERO) >= 0 && rangeEnd.compareTo(rangeStart) == 1) {
                    showGraph = true;
                    
                }
            } catch(Exception e) { }
        }
        request.setAttribute("showGraph", showGraph);
        
        // 入力チェック・計算
        boolean inputError = false;
        String errorMsg = "";
        BigDecimal overallProbability = null;
        BigDecimal[] pArr = null;
        int[] reqArr = null;
        BigDecimal sumP = BigDecimal.ZERO;
        
        if(isSubmitted && count.compareTo(BigInteger.ZERO) == 1 && quantity > 0) {
            if(probabilityStrs.length < quantity || thresholdStrs.length < quantity) {
                inputError = true;
                errorMsg = "入力項目が不足しています。すべての項目を入力してください。";
            } else {
                pArr = new BigDecimal[quantity];
                reqArr = new int[quantity];
                for (int i = 0; i < quantity; i++) {
                    try {
                        pArr[i] = new BigDecimal (probabilityStrs[i]).divide(BigDecimal.valueOf(100));
                        reqArr[i] = Integer.parseInt(thresholdStrs[i]);
                        sumP = sumP.add(pArr[i]);
                    } catch(Exception e) {
                        inputError = true;
                        errorMsg = "確率または必要回数の入力に誤りがあります。";
                        break;
                    }
                }
                if(sumP.compareTo(BigDecimal.ONE) == 1) {
                    inputError = true;
                    errorMsg = "全アイテムの確率の合計が1を超えています。再度入力してください。";
                }
                if(!inputError) {
                	InclusionExclusionCalculator calc = new InclusionExclusionCalculator(count, pArr, reqArr, MC);
                    
                    overallProbability = calc.probabilityAllAtLeast();
                    overallProbability = overallProbability.movePointRight(2);//ここで確率を求めます
                    int n = (int)session.getAttribute("digits");
                    String overallProbabilityStr = SignificantFigures.roundToSignificantFigures(overallProbability, n);//ここで表示する桁を調整します
                    StringBuilder ans = new StringBuilder();
                    ans.append(overallProbabilityStr).append("%");
                    request.setAttribute("overallProbability", ans);
                }else {
                	request.setAttribute("inputError",true);
                	System.out.println(errorMsg);
                	request.setAttribute("errorMsg",errorMsg);
                	request.setAttribute("isSubmitted", isSubmitted);
                	// JSPへフォワード
                    request.getRequestDispatcher("index.jsp")
                           .forward(request, response);
                	return;
                }
            }
        }
        
        
        
        
        //------ここからはグラフの壁画用--------//
        String rangeStepStr = request.getParameter("rangeStep");
        BigInteger rangeStep = BigInteger.ONE; // デフォルト1回ずつ
        try {
            if(rangeStepStr != null) {
                rangeStep = new BigInteger(rangeStepStr);
                if (rangeStep.compareTo(BigInteger.ZERO) <= 0) rangeStep = BigInteger.ONE; // 無効値対策
            }
        } catch(Exception e) {
            rangeStep = BigInteger.ONE;
        }
     // グラフ用の確率推移（指定範囲内でステップずつ計算）
        InclusionExclusionCalculator calc = new InclusionExclusionCalculator(count, pArr, reqArr, MC);
        if(isSubmitted && !inputError && showGraph && pArr != null && reqArr != null) {
            StringBuilder labelSB = new StringBuilder();
            StringBuilder dataSB = new StringBuilder();
            
            for(BigInteger r = rangeStart; r.compareTo(rangeEnd) <= 0; r = r.add(rangeStep)){
                
            	calc.nChange(r);
                
                BigDecimal prob = calc.probabilityAllAtLeast();
                labelSB.append("\"").append(r).append("\"").append(",");
                dataSB.append(prob).append(",");
            }
            if(labelSB.length() > 0) labelSB.setLength(labelSB.length()-1);
            if(dataSB.length() > 0) dataSB.setLength(dataSB.length()-1);
            String graphLabels = labelSB.toString();
            String graphData = dataSB.toString();
            request.setAttribute("graphLabels", graphLabels);
            request.setAttribute("graphData", graphData);
        }
        request.setAttribute("isSubmitted", isSubmitted);
        
        // JSPへフォワード
        request.getRequestDispatcher("index.jsp")
               .forward(request, response);
    }
}

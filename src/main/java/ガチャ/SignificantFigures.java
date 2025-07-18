package ガチャ;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class SignificantFigures {
	public static String roundToSignificantFigures(BigDecimal bd, int n) {
        if (bd.signum() == 0) {
            return "0"; // 0はそのまま返す
        }
        // MathContextで有効数字n桁を指定して丸める
        MathContext mc = new MathContext(n, RoundingMode.HALF_UP);
        return bd.round(mc).toPlainString();
    }
	
	
	public static String roundDecimal(BigDecimal bd, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("scaleは0以上にしてください");
        }
     // 四捨五入
        BigDecimal rounded = bd.setScale(n, RoundingMode.HALF_UP);

        // 末尾のゼロや不要な小数点を消す
        return rounded.stripTrailingZeros().toPlainString();
    }
	
}

package ガチャ;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
public class FindExpectedValue {
	private MathContext mc = new MathContext(50); // 有効数字
	public BigDecimal calculate(List<BigDecimal> p,//それぞれのアイテムの確率
					            List<Integer> k,//それぞれのアイテムの最低当選確率
					            BigDecimal CurrentCrobability,//現在の深さのアイテムの総確率
					            BigDecimal CurrentCr//現在の深さの確率
								) {
		if(CurrentCrobability.compareTo(BigDecimal.ZERO) <= 0)return BigDecimal.ZERO;
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal count= BigDecimal.ONE.divide(CurrentCrobability,mc);
		sum = sum.add(count.multiply(CurrentCr),mc);//期待値を足す
		for(int i = 0;i < k.size();i++) {
			k.set(i,k.get(i)-1);
			BigDecimal pp = p.get(i).divide(CurrentCrobability,mc).multiply(CurrentCr,mc);
			if(k.get(i) <= 0) {
				BigDecimal backp = p.get(i);
				CurrentCrobability = CurrentCrobability.subtract(p.get(i),mc);
				k.remove(i);
				p.remove(i);
				sum = sum.add(calculate(p,k,CurrentCrobability,pp),mc);
				k.add(i,1);
				p.add(i,backp);
				CurrentCrobability = CurrentCrobability.add(p.get(i),mc);
			}else {
				sum = sum.add(calculate(p,k,CurrentCrobability,pp),mc);
				k.set(i,k.get(i)+1);
			}
		
		}
		return sum;
	}
	public FindExpectedValue(MathContext mc) {//有効数字を設定
		this.mc = mc;
	}
}

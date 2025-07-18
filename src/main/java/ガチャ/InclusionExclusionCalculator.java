package ガチャ;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 包除原理 + 多項分布を BigInteger / BigDecimal で正確に評価するクラス。
 */
public class InclusionExclusionCalculator {

    private BigInteger n;                  // 試行回数
    private final BigDecimal[] p;         // 各アイテムの確率
    private final int[] k;                // 最低当選回数
    private final MathContext mc;         // 精度設定

    public InclusionExclusionCalculator(BigInteger n,
                                        BigDecimal[] p,
                                        int[] k,
                                        MathContext mc) {
        this.n   = n;
        this.p   = p;
        this.k   = k;
        this.mc  = mc;
    }
    
    public void nChange(BigInteger n) {
    	this.n = n;
    }

    /**
     * P(∀i, X_i ≥ k_i) を返す。
     */
    public BigDecimal probabilityAllAtLeast() {
        int m = p.length;
        int subsets = 1 << m;
        BigDecimal total = BigDecimal.ZERO;
        for (int mask = 0; mask < subsets; mask++) {
            int bits = Integer.bitCount(mask);
            BigDecimal jointFail = jointFailureProbability(mask);
            total = total.add(((bits & 1) == 0 ? jointFail : jointFail.negate()), mc);
        }
        return total;
    }

    /**
     * subset mask に含まれるアイテムがすべて "未達" (X_i < k_i) となる確率。
     */
    private BigDecimal jointFailureProbability(int mask) {
        int m = p.length;
        List<Integer> idx = new ArrayList<>();
        BigDecimal pSum = BigDecimal.ZERO;
        for (int i = 0; i < m; i++) {
            if ((mask & (1 << i)) != 0) {
                idx.add(i);
                pSum = pSum.add(p[i], mc);
            }
        }
        BigDecimal q = BigDecimal.ONE.subtract(pSum, mc);
        return enumerate(idx, 0, new int[idx.size()], 0, q);
    }

    private BigDecimal enumerate(List<Integer> idx, int pos, int[] counts, int used, BigDecimal q) {
        if (pos == idx.size()) {
        	BigInteger remaining = n.subtract(BigInteger.valueOf(used));
            if (remaining.compareTo(BigInteger.ZERO) == -1) return BigDecimal.ZERO;
            
            // 多項係数
            BigDecimal coefBD = comb(n, counts);
            

            // 確率部分
            BigDecimal prob = pow(q, remaining);
            
            for (int i = 0; i < idx.size(); i++) {
                prob = prob.multiply(pow(p[idx.get(i)], BigInteger.valueOf(counts[i])), mc);
            }
            return coefBD.multiply(prob, mc);
        }

        int item = idx.get(pos);
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (int c = 0; c < k[item] && n.compareTo(BigInteger.valueOf(used+c)) >= 0; c++) {
            counts[pos] = c;
            subtotal = subtotal.add(enumerate(idx, pos + 1, counts, used + c, q), mc);
        }
        return subtotal;
    }

    private BigDecimal pow(BigDecimal base, BigInteger exp) {

	    if (exp.compareTo(BigInteger.ZERO) == 0) return BigDecimal.ONE;   // 0 乗 = 1
	    BigDecimal result = BigDecimal.ONE;
	    BigDecimal acc    = base;                       // 繰り上げ用
	    BigInteger e      = exp;                        // 破壊的に使う
	    while (e.compareTo(BigInteger.ZERO) == 1) {
	    	if (e.testBit(0)) {                         // 下位ビットが 1 なら
	    		result = result.multiply(acc, mc);
	        }
	        acc = acc.multiply(acc, mc);                // 基数を 2 乗
	        if (acc.compareTo(new BigDecimal("1E-40")) <= 0) return BigDecimal.ZERO;

	        e   = e.shiftRight(1);                      // 1 ビット右シフト
	    }
	    return result;

    }
    private BigDecimal comb(BigInteger n, int[] counts) {
        BigInteger result = BigInteger.ONE;
        for(int k:counts) {
	        for (int i = 1; i <= k; i++) {
	        	
	            result = result.multiply(n.subtract(BigInteger.valueOf(i-1)));
	            result = result.divide(BigInteger.valueOf(i));
	        }
	        n = n.subtract(BigInteger.valueOf(k));
        }
        return new BigDecimal(result,mc);
    }

}
package var.service;

import java.util.ArrayList;
import java.util.List;

public class VaRCalculator {
    /**
     * Calculates Historical Value at Risk (VaR) using nearest-rank method.
     * PnL is sorted ascending and the (1 - confidence) quantile is returned.
     * Returns a PnL value (typically negative), not absolute loss.
     * Example: confidence=0.99 with 260 observations -> 3rd worst outcome.
     */
    public static double  calculate(List<Double>pnl, double confidence){

        confidenceValidation(confidence);
        pnlValidation(pnl);

        List<Double> sorted = new ArrayList<>(pnl);
        sorted.sort(null);
        int indexVaR= (int) Math.ceil(sorted.size()*(1-confidence));
        indexVaR=Math.min(Math.max(indexVaR, 1), sorted.size());
        return sorted.get(indexVaR-1);
    }

    private static void confidenceValidation(double confidence){
        if (!Double.isFinite(confidence)) {
            throw new IllegalArgumentException("Confidence must be a finite number.");
        }
        if (confidence <= 0 || confidence >= 1) {
            throw new IllegalArgumentException("Confidence must be in (0, 1).");
        }
    }
    private static void pnlValidation(List<Double> pnl){
        if (pnl == null ||pnl.isEmpty()) {
            throw new IllegalArgumentException("PnL must not be null or empty.");
        }

        for (int i=0; i<pnl.size();i++){
            if (pnl.get(i) == null) {
                throw new IllegalArgumentException("PnL contains null at index "+ i);
            }
            if (!Double.isFinite(pnl.get(i))) {
                throw new IllegalArgumentException("PnL must be a finite number at index "+ i);
            }
        }
    }
}

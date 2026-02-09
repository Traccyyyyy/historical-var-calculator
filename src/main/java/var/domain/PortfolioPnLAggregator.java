package var.domain;

import var.io.CSVDataLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PortfolioPnLAggregator {
    /**
     * Aggregates weighted daily PnL across trades.
     * Requires all trades to have equal length and total weight = 1.
     * Uses CSVDataLoader to fetch trade-level PnL by tradeId.
     */
    public static List<Double> calculatePortfolioDailyPnL(
            CSVDataLoader loader,
            Map<String, Double> tradesInPortfolio
    ) {
        if (loader == null) throw new IllegalArgumentException("loader cannot be null");
        if (tradesInPortfolio == null || (tradesInPortfolio.isEmpty())) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        List<Double> portfolioDailyPnL = new ArrayList<>();
        double sumWeight = 0.00;

        for (Map.Entry<String, Double> entry : tradesInPortfolio.entrySet()) {
            String tradeId = entry.getKey();
            Double weight = getValidatedWeight(tradeId, entry.getValue());

            List<Double> tradePnL = loader.getTradeDailyPnL(tradeId);
            int n = tradePnL.size();
            sumWeight += weight;

            if (portfolioDailyPnL.isEmpty()) {
                initialisePortfolioPnL(portfolioDailyPnL, n);
            }
            if (portfolioDailyPnL.size() != n) {
                throw new IllegalArgumentException(String.format("PnL data length in trades are not equal. Expected %d but got %d in %s.", portfolioDailyPnL.size(), n, tradeId));
            }
            for (int i = 0; i < n; i++) {
                portfolioDailyPnL.set(i, portfolioDailyPnL.get(i) + (tradePnL.get(i) * weight));
            }
        }

        if (Math.abs(sumWeight - 1.0) > 1e-6) {
            throw new IllegalArgumentException("Total portfolio weight must equal 1.0, but was " + sumWeight);
        }
            return portfolioDailyPnL;
    }

    private static Double getValidatedWeight(String tradeId, Double weight) {
        if (tradeId == null || tradeId.isBlank()) {
            throw new IllegalArgumentException("tradeId cannot be null/blank");
        }
        if (weight == null) {
            throw new IllegalArgumentException("Weight is missing for trade " +tradeId);
        }
        if (!Double.isFinite(weight)) {
            throw new IllegalArgumentException("Weight must be a finite number for trade " + tradeId);
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be >= 0 for trade " + tradeId);
        }
        return weight;
    }

    private static void initialisePortfolioPnL(List<Double> portfolioDailyPnL, int n) {
        for (int i = 0; i < n; i++) {
            portfolioDailyPnL.add(0.0);
        }
    }
}

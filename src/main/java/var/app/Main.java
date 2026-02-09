package var.app;

import var.domain.PortfolioPnLAggregator;
import var.io.CSVDataLoader;
import var.service.VaRCalculator;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args){

        double confidence = 0.95;
        Map<String, Double> portfolio = new HashMap<>();
        portfolio.put("trade_1", 0.5);
        portfolio.put("trade_2", 0.25);
        portfolio.put("trade_3", 0.25);

        CSVDataLoader loader = new CSVDataLoader();
        loader.loadTradesFromDirectory(Paths.get("data"));

        List<Double> dailyPnL = loader.getTradeDailyPnL("trade_1");
        double singleVaR = VaRCalculator.calculate(dailyPnL, confidence);
        System.out.printf("Single-trade VaR: %.2f%n", singleVaR);

        List<Double> portfolioPnL = PortfolioPnLAggregator.calculatePortfolioDailyPnL(loader,portfolio);
        double portfolioVaR = VaRCalculator.calculate(portfolioPnL, confidence);
        System.out.printf("Portfolio VaR: %.2f%n", portfolioVaR);
    }
}



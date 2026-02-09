package var.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class CSVDataLoader {

    private final Map<String, List<Double>> tradesPool = new HashMap<>();

    public void loadTradesFromDirectory(Path dataDir) {
        if (dataDir == null) {
            throw new IllegalArgumentException("dataDir cannot be null");
        }
        if (!Files.isDirectory(dataDir)) {
            throw new IllegalArgumentException("Not a directory: " + dataDir);
        }
        try (Stream<Path> paths = Files.list(dataDir)) {
            paths
                    .filter(p -> p.toString().endsWith(".csv"))
                    .sorted()
                    .forEach(this::loadSingleTradeCsv);
        } catch (IOException e) {
            throw new RuntimeException("Failed to list directory: " + dataDir, e);
        }
    }

    private void loadSingleTradeCsv(Path csvPath) {
        String fileName = csvPath.getFileName().toString();
        String tradeId = fileName.substring(0, fileName.length() - ".csv".length());
        List<Double> pnlList = parseCsv(csvPath);
        tradesPool.put(tradeId, pnlList);
    }

    private List<Double> parseCsv(Path csvPath) {
        List<Double> pnlList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;
            line = reader.readLine();
            if (line == null) {
                throw new IllegalArgumentException("Empty CSV file: " + csvPath);
            }
            int rowIndex = 1;
            while ((line = reader.readLine()) != null) {
                rowIndex++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    double pnl = Double.parseDouble(line);
                    if (!Double.isFinite(pnl)) {
                        throw new IllegalArgumentException(
                                "PnL must be finite at row " + rowIndex + " in " + csvPath
                        );
                    }
                    pnlList.add(pnl);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Invalid PnL value at row " + rowIndex + " in " + csvPath,
                            e
                    );
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV: " + csvPath, e);
        }
        if (pnlList.isEmpty()) {
            throw new IllegalArgumentException("No PnL data found in " + csvPath);
        }
        return pnlList;
    }

    public List<Double> getTradeDailyPnL(String tradeId) {

        if (tradeId == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        List<Double> dailyPnl = tradesPool.get(tradeId);

        if (dailyPnl == null || tradeId.isBlank()) {
            throw new NoSuchElementException("Cannot find trade " + tradeId);
        } else if (dailyPnl.isEmpty()) {
            throw new IllegalStateException("Daily PnL is empty for " + tradeId);
        }
        return new ArrayList<>(dailyPnl);
    }
}
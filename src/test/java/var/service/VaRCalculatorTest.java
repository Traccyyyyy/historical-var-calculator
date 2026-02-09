package var.service;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VaRCalculatorTest {
    @Test
    void calculate_happyPath() {
        List<Double> pnl = List.of(
                -10.0, 5.0, -3.0, 2.0, -20.0,
                1.0, -1.0, 4.0, -7.0, 6.0
        );
        double result = VaRCalculator.calculate(pnl, 0.9);
        assertEquals(-20.0, result);
    }

    @Test
    void calculate_invalidConfidence_shouldThrowException() {
        List<Double> pnl = List.of(
                -10.0, 5.0, -3.0, 2.0, -20.0,
                1.0, -1.0, 4.0, -7.0, 6.0
        );
        assertThrows(IllegalArgumentException.class,()
                ->VaRCalculator.calculate(pnl,1.3));
    }
    @Test
    void calculate_confidenceAtBoundary_shouldThrowException() {
        List<Double> pnl = List.of(-1.0, -2.0, -3.0);
        assertThrows(IllegalArgumentException.class,
                () -> VaRCalculator.calculate(pnl, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> VaRCalculator.calculate(pnl, 0.0));
    }
    @Test
    void calculate_nullValueInPnl_shouldThrowException() {
        List<Double> pnl = Arrays.asList(1.0, null, 3.0);
        assertThrows(IllegalArgumentException.class,
                () -> VaRCalculator.calculate(pnl, 0.99));
    }
    @Test
    void calculate_emptyPnl_shouldThrowException() {
        List<Double> pnl=List.of();
        assertThrows(IllegalArgumentException.class,()
                ->VaRCalculator.calculate(pnl,0.99));
    }
    @Test
    void calculate_invalidValuePnl_shouldThrowException() {
        List<Double> pnl=List.of(Double.NaN);
        assertThrows(IllegalArgumentException.class,()
                ->VaRCalculator.calculate(pnl,0.99));
    }
}
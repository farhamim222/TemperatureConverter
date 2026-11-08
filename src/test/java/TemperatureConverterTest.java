
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemperatureConverterTest {

    private final TemperatureConverter converter =
            new TemperatureConverter();

    @Test
    void testZeroCelsius() {
        double result = converter.celsiusToFahrenheit(0);
        assertEquals(32.0, result, 0.001);
    }

    @Test
    void testBoilingPoint() {
        double result = converter.celsiusToFahrenheit(100);
        assertEquals(212.0, result, 0.001);
    }

    @Test
    void testFreezingEquivalent() {
        double result = converter.celsiusToFahrenheit(-40);
        assertEquals(-40.0, result, 0.001);
    }

    @Test
    void testRoomTemperature() {
        double result = converter.celsiusToFahrenheit(25);
        assertEquals(77.0, result, 0.001);
    }

    @Test
    void testNegativeTemperature() {
        double result = converter.celsiusToFahrenheit(-10);
        assertEquals(14.0, result, 0.001);
    }
}

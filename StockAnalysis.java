import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StockAnalysis {

    private static final Logger logger = Logger.getLogger(StockAnalysis.class.getName());
    private static final ZoneId EST_ZONE_ID = ZoneId.of("America/New_York");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static List<String> dailyData = new ArrayList<>();
    private static List<String> weeklyData = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the stock symbol (e.g., NVDA): ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        ZonedDateTime now = ZonedDateTime.now(EST_ZONE_ID);
        analyzeStock(symbol, now);

        generateDailyReport(symbol);
        if (isEndOfWeek()) {
            generateEndOfWeekReport(symbol);
        }
    }

    // Method to analyze stock price patterns for the day
    public static void analyzeStock(String symbol, ZonedDateTime now) {
        // Perform quantitative analysis and collect data
        double currentPrice = getCurrentStockPrice(symbol);
        double previousDayClose = getPreviousDayClosePrice(symbol);
        double priceChange = currentPrice - previousDayClose;
        double priceChangePercent = (priceChange / previousDayClose) * 100.0;

        // Estimate next day's price
        double nextDayPrice = estimateNextDayPrice(currentPrice, priceChange);

        // Store data for daily and weekly reports
        String timestamp = now.format(DATE_FORMATTER);
        String data = String.format("%s,%.2f,%.2f,%.2f,%.2f%%", timestamp, currentPrice, priceChange, nextDayPrice, priceChangePercent);
        dailyData.add(data);
        weeklyData.add(data); // Add to weekly data as well

        // Print to console (can be replaced with logging or further processing)
        System.out.printf("Analysis for %s on %s:\n", symbol, timestamp);
        System.out.printf("Current Price: $%.2f\n", currentPrice);
        System.out.printf("Price Change: $%.2f (%.2f%%)\n", priceChange, priceChangePercent);
        System.out.printf("Estimated Next Day's Price: $%.2f\n", nextDayPrice);
    }

    // Method to generate a daily report
    public static void generateDailyReport(String symbol) {
        String desktopPath = "C:\\Users\\thenx\\Desktop\\Stock reports";
        String reportFileName = desktopPath + "\\daily_" + symbol.toLowerCase() + "_report.csv";
        try (FileWriter writer = new FileWriter(reportFileName)) {
            writer.write("Date,Current Price,Price Change,Next Day's Price,Change Percent\n");
            for (String data : dailyData) {
                writer.write(data + "\n");
            }
            System.out.println("Daily report generated: " + reportFileName);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write daily report for " + symbol, e);
        }
    }

    // Method to generate an end-of-week report
    public static void generateEndOfWeekReport(String symbol) {
        String desktopPath = "C:\\Users\\thenx\\Desktop\\Stock reports";
        String reportFileName = desktopPath + "\\end_of_week_" + symbol.toLowerCase() + "_report.txt";
        try (FileWriter writer = new FileWriter(reportFileName)) {
            writer.write("End-of-Week Report for " + symbol + " Stock\n");
            writer.write("==========================================\n");
            writer.write("Weekly Summary:\n");

            // Perform weekly analysis
            if (weeklyData.size() > 1) {
                double startPrice = Double.parseDouble(weeklyData.get(0).split(",")[1]);
                double endPrice = Double.parseDouble(weeklyData.get(weeklyData.size() - 1).split(",")[1]);

                writer.write("Start Price: $" + startPrice + "\n");
                writer.write("End Price: $" + endPrice + "\n");

                // Estimate next week's price based on historical weekly data
                double nextWeekPrice = estimateNextWeekPrice(startPrice, endPrice);
                writer.write("Estimated Next Week's Price: $" + nextWeekPrice + "\n");

                if (endPrice > startPrice) {
                    writer.write("Weekly Trend: Upward\n");
                } else if (endPrice < startPrice) {
                    writer.write("Weekly Trend: Downward\n");
                } else {
                    writer.write("Weekly Trend: No significant change\n");
                }

                // Additional analysis and predictions can be added here based on historical data
                // For example, calculating average prices, volatility, trading volume changes, etc.

            } else {
                writer.write("Insufficient data for weekly analysis.\n");
            }

            System.out.println("End-of-week report generated: " + reportFileName);
        } catch (IOException | NumberFormatException | IndexOutOfBoundsException e) {
            logger.log(Level.SEVERE, "Failed to write end-of-week report for " + symbol, e);
        }
    }

    // Method to check if it's the end of the trading week (Friday)
    public static boolean isEndOfWeek() {
        ZonedDateTime now = ZonedDateTime.now(EST_ZONE_ID);
        return now.getDayOfWeek().getValue() == 5; // 5 represents Friday
    }

    // Method to fetch current stock price from Yahoo Finance
    public static double getCurrentStockPrice(String symbol) {
        // Implement logic to fetch current stock price from Yahoo Finance
        try {
            String yahooFinanceUrl = "https://finance.yahoo.com/quote/" + symbol;
            Document doc = Jsoup.connect(yahooFinanceUrl).get();

            // Extracting current price from the HTML
            Element priceElement = doc.selectFirst("span[data-reactid='32']");
            if (priceElement != null) {
                String priceText = priceElement.text().replaceAll(",", "");
                return Double.parseDouble(priceText);
            } else {
                logger.log(Level.SEVERE, "Failed to find the current price element for " + symbol);
                return 0.0; // Return 0.0 or handle error case appropriately
            }

        } catch (IOException | NumberFormatException e) {
            logger.log(Level.SEVERE, "Failed to fetch current price for " + symbol, e);
            return -1.0; // Return -1.0 to indicate symbol not found or handle error case appropriately
        }
    }

    // Method to fetch previous day's closing price from Yahoo Finance
    public static double getPreviousDayClosePrice(String symbol) {
        // Implement logic to fetch previous day's closing price from Yahoo Finance
        try {
            String yahooFinanceUrl = "https://finance.yahoo.com/quote/" + symbol;
            Document doc = Jsoup.connect(yahooFinanceUrl).get();

            // Extracting previous day's close price from the HTML
            Element closePriceElement = doc.selectFirst("td[data-test='PREV_CLOSE-value']");
            if (closePriceElement != null) {
                String closePriceText = closePriceElement.text().replaceAll(",", "");
                return Double.parseDouble(closePriceText);
            } else {
                logger.log(Level.SEVERE, "Failed to find the previous day's close price element for " + symbol);
                return 0.0; // Return 0.0 or handle error case appropriately
            }

        } catch (IOException | NumberFormatException e) {
            logger.log(Level.SEVERE, "Failed to fetch previous day's close price for " + symbol, e);
            return -1.0; // Return -1.0 to indicate symbol not found or handle error case appropriately
        }
    }

    // Method to estimate next day's stock price based on current price and price change
    public static double estimateNextDayPrice(double currentPrice, double priceChange) {
        // Implement more advanced estimation algorithms here
        return currentPrice + priceChange; // Simple estimation for demonstration
    }

    // Method to estimate next week's stock price based on historical data
    public static double estimateNextWeekPrice(double startPrice, double endPrice) {
        // Implement more advanced estimation algorithms here
        return (startPrice + endPrice) / 2.0; // Simple average for demonstration
    }
}

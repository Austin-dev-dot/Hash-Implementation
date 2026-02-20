import java.util.*;

class RealTimeAnalytics {
    private HashMap<String, Integer> pageViews;
    private HashMap<String, HashSet<String>> uniqueVisitors;
    private HashMap<String, Integer> trafficSources;
    private int totalEvents;

    RealTimeAnalytics() {
        pageViews = new HashMap<>();
        uniqueVisitors = new HashMap<>();
        trafficSources = new HashMap<>();
        totalEvents = 0;
    }

    void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
        totalEvents++;
    }

    List<String> getTopPages(int n) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(pageViews.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            int unique = uniqueVisitors.containsKey(entry.getKey()) ?
                         uniqueVisitors.get(entry.getKey()).size() : 0;
            result.add(entry.getKey() + " - " + entry.getValue() + " views (" + unique + " unique)");
        }
        return result;
    }

    Map<String, String> getTrafficSourceBreakdown() {
        Map<String, String> breakdown = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(trafficSources.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        for (Map.Entry<String, Integer> entry : sorted) {
            double percentage = (entry.getValue() * 100.0) / totalEvents;
            breakdown.put(entry.getKey(), String.format("%.1f%%", percentage));
        }
        return breakdown;
    }

    void printDashboard() {
        System.out.println("=== Dashboard ===");
        System.out.println("Top Pages:");
        List<String> topPages = getTopPages(10);
        for (int i = 0; i < topPages.size(); i++)
            System.out.println("  " + (i + 1) + ". " + topPages.get(i));
        System.out.println("Traffic Sources:");
        for (Map.Entry<String, String> entry : getTrafficSourceBreakdown().entrySet())
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
    }

    public static void main(String[] args) {
        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.processEvent("/article/breaking-news", "user_123", "google");
        analytics.processEvent("/article/breaking-news", "user_456", "facebook");
        analytics.processEvent("/article/breaking-news", "user_123", "google");
        analytics.processEvent("/sports/championship", "user_789", "direct");
        analytics.processEvent("/sports/championship", "user_101", "google");
        analytics.processEvent("/tech/new-release", "user_202", "facebook");
        analytics.processEvent("/tech/new-release", "user_303", "direct");
        analytics.processEvent("/tech/new-release", "user_404", "google");
        analytics.processEvent("/tech/new-release", "user_505", "google");
        analytics.processEvent("/article/breaking-news", "user_606", "direct");

        analytics.printDashboard();
    }
}

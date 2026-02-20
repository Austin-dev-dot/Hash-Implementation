import java.util.*;

class MultiLevelCache {
    private LinkedHashMap<String, String> l1Cache;
    private HashMap<String, String> l2Cache;
    private HashMap<String, String> l3Database;
    private HashMap<String, Integer> accessCount;
    private int l1Capacity;
    private int l2Capacity;
    private int promotionThreshold;
    private int l1Hits, l1Misses, l2Hits, l2Misses, l3Hits;

    MultiLevelCache(int l1Capacity, int l2Capacity, int promotionThreshold) {
        this.l1Capacity = l1Capacity;
        this.l2Capacity = l2Capacity;
        this.promotionThreshold = promotionThreshold;
        this.accessCount = new HashMap<>();
        this.l1Hits = 0;
        this.l1Misses = 0;
        this.l2Hits = 0;
        this.l2Misses = 0;
        this.l3Hits = 0;

        this.l1Cache = new LinkedHashMap<String, String>(l1Capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > l1Capacity;
            }
        };

        this.l2Cache = new HashMap<>();
        this.l3Database = new HashMap<>();
    }

    void addToDatabase(String videoId, String videoData) {
        l3Database.put(videoId, videoData);
    }

    String getVideo(String videoId) {
        StringBuilder log = new StringBuilder();
        accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);

        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            log.append("L1 Cache HIT (0.5ms)");
            return log.toString();
        }
        l1Misses++;
        log.append("L1 Cache MISS -> ");

        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            log.append("L2 Cache HIT (5ms)");
            if (accessCount.get(videoId) >= promotionThreshold) {
                l1Cache.put(videoId, l2Cache.get(videoId));
                log.append(" -> Promoted to L1");
            }
            return log.toString();
        }
        l2Misses++;
        log.append("L2 Cache MISS -> ");

        if (l3Database.containsKey(videoId)) {
            l3Hits++;
            String data = l3Database.get(videoId);
            l2Cache.put(videoId, data);
            if (l2Cache.size() > l2Capacity) {
                String first = l2Cache.keySet().iterator().next();
                l2Cache.remove(first);
            }
            log.append("L3 Database HIT (150ms) -> Added to L2 (access count: " + accessCount.get(videoId) + ")");
            return log.toString();
        }

        log.append("NOT FOUND");
        return log.toString();
    }

    void invalidate(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
    }

    String getStatistics() {
        int l1Total = l1Hits + l1Misses;
        int l2Total = l2Hits + l2Misses;
        int overallHits = l1Hits + l2Hits + l3Hits;
        int overallTotal = l1Total;

        double l1HitRate = l1Total > 0 ? (l1Hits * 100.0 / l1Total) : 0;
        double l2HitRate = l2Total > 0 ? (l2Hits * 100.0 / l2Total) : 0;
        double l3HitRate = overallTotal > 0 ? (l3Hits * 100.0 / overallTotal) : 0;
        double overallHitRate = overallTotal > 0 ? (overallHits * 100.0 / overallTotal) : 0;

        return "L1: Hit Rate " + String.format("%.0f", l1HitRate) + "%, Avg Time: 0.5ms\n" +
               "L2: Hit Rate " + String.format("%.0f", l2HitRate) + "%, Avg Time: 5ms\n" +
               "L3: Hit Rate " + String.format("%.0f", l3HitRate) + "%, Avg Time: 150ms\n" +
               "Overall: Hit Rate " + String.format("%.0f", overallHitRate) + "%";
    }

    public static void main(String[] args) {
        MultiLevelCache cache = new MultiLevelCache(3, 10, 2);

        cache.addToDatabase("video_123", "Movie A Data");
        cache.addToDatabase("video_456", "Movie B Data");
        cache.addToDatabase("video_789", "Movie C Data");
        cache.addToDatabase("video_999", "Movie D Data");

        System.out.println("getVideo(video_123): " + cache.getVideo("video_123"));
        System.out.println("getVideo(video_123): " + cache.getVideo("video_123"));
        System.out.println("getVideo(video_123): " + cache.getVideo("video_123"));
        System.out.println("getVideo(video_456): " + cache.getVideo("video_456"));
        System.out.println("getVideo(video_999): " + cache.getVideo("video_999"));
        System.out.println();
        System.out.println(cache.getStatistics());
    }
}

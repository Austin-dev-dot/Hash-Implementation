import java.util.*;

class DNSCache {
    private HashMap<String, DNSEntry> cache;
    private int maxSize;
    private int hits;
    private int misses;

    static class DNSEntry {
        String domain;
        String ipAddress;
        long timestamp;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.timestamp = System.currentTimeMillis();
            this.expiryTime = this.timestamp + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    DNSCache(int maxSize) {
        this.cache = new HashMap<>();
        this.maxSize = maxSize;
        this.hits = 0;
        this.misses = 0;
    }

    String resolve(String domain) {
        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);
            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT -> " + entry.ipAddress;
            } else {
                cache.remove(domain);
                misses++;
                String ip = queryUpstream(domain);
                cache.put(domain, new DNSEntry(domain, ip, 300));
                return "Cache EXPIRED -> Query upstream -> " + ip;
            }
        }
        misses++;
        String ip = queryUpstream(domain);
        if (cache.size() >= maxSize)
            evictOldest();
        cache.put(domain, new DNSEntry(domain, ip, 300));
        return "Cache MISS -> Query upstream -> " + ip;
    }

    private String queryUpstream(String domain) {
        int hash = Math.abs(domain.hashCode());
        return (hash % 256) + "." + ((hash / 256) % 256) + "." +
               ((hash / 65536) % 256) + "." + ((hash / 16777216) % 256);
    }

    private void evictOldest() {
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        for (Map.Entry<String, DNSEntry> entry : cache.entrySet()) {
            if (entry.getValue().timestamp < oldestTime) {
                oldestTime = entry.getValue().timestamp;
                oldestKey = entry.getKey();
            }
        }
        if (oldestKey != null)
            cache.remove(oldestKey);
    }

    void removeExpiredEntries() {
        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().isExpired())
                it.remove();
        }
    }

    String getCacheStats() {
        int total = hits + misses;
        double hitRate = total > 0 ? (hits * 100.0 / total) : 0;
        return "Hit Rate: " + String.format("%.1f", hitRate) + "%, " +
               "Hits: " + hits + ", Misses: " + misses;
    }

    public static void main(String[] args) {
        DNSCache dnsCache = new DNSCache(100);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("facebook.com"));
        System.out.println(dnsCache.resolve("twitter.com"));
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.getCacheStats());
    }
}

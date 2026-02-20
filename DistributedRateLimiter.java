import java.util.*;

class DistributedRateLimiter {
    private HashMap<String, TokenBucket> clients;
    private int maxTokens;
    private int refillRate;

    static class TokenBucket {
        int tokens;
        long lastRefillTime;
        int maxTokens;
        int refillRate;

        TokenBucket(int maxTokens, int refillRate) {
            this.tokens = maxTokens;
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.lastRefillTime = System.currentTimeMillis();
        }

        void refill() {
            long now = System.currentTimeMillis();
            long elapsed = (now - lastRefillTime) / 1000;
            int tokensToAdd = (int) (elapsed * refillRate / 3600);
            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }

        boolean tryConsume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        long getRetryAfter() {
            return (long) ((1.0 / refillRate) * 3600);
        }
    }

    DistributedRateLimiter(int maxTokens, int refillRate) {
        this.clients = new HashMap<>();
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
    }

    String checkRateLimit(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) {
            bucket = new TokenBucket(maxTokens, refillRate);
            clients.put(clientId, bucket);
        }
        if (bucket.tryConsume()) {
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after " + bucket.getRetryAfter() + "s)";
        }
    }

    String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null)
            return "No data for client: " + clientId;
        int used = bucket.maxTokens - bucket.tokens;
        return "{used: " + used + ", limit: " + bucket.maxTokens + "}";
    }

    public static void main(String[] args) {
        DistributedRateLimiter limiter = new DistributedRateLimiter(5, 5);

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}

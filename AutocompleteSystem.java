import java.util.*;

class AutocompleteSystem {
    private HashMap<String, Integer> queryFrequency;
    private HashMap<String, List<String>> prefixCache;

    AutocompleteSystem() {
        queryFrequency = new HashMap<>();
        prefixCache = new HashMap<>();
    }

    void addQuery(String query) {
        query = query.toLowerCase();
        queryFrequency.put(query, queryFrequency.getOrDefault(query, 0) + 1);
        prefixCache.clear();
    }

    List<String> search(String prefix, int topK) {
        prefix = prefix.toLowerCase();
        if (prefixCache.containsKey(prefix))
            return prefixCache.get(prefix);

        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : queryFrequency.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                minHeap.offer(entry);
                if (minHeap.size() > topK)
                    minHeap.poll();
            }
        }

        LinkedList<String> result = new LinkedList<>();
        while (!minHeap.isEmpty()) {
            Map.Entry<String, Integer> entry = minHeap.poll();
            result.addFirst(entry.getKey() + " (" + entry.getValue() + " searches)");
        }

        prefixCache.put(prefix, result);
        return result;
    }

    void updateFrequency(String query) {
        query = query.toLowerCase();
        int freq = queryFrequency.getOrDefault(query, 0) + 1;
        queryFrequency.put(query, freq);
        prefixCache.clear();
        System.out.println("Updated: \"" + query + "\" -> Frequency: " + freq);
    }

    public static void main(String[] args) {
        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("java tutorial");
        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java 21 features");
        system.addQuery("python tutorial");
        system.addQuery("python tutorial");

        System.out.println("Search 'jav':");
        List<String> results = system.search("jav", 5);
        for (int i = 0; i < results.size(); i++)
            System.out.println("  " + (i + 1) + ". " + results.get(i));

        System.out.println("\nSearch 'py':");
        results = system.search("py", 5);
        for (int i = 0; i < results.size(); i++)
            System.out.println("  " + (i + 1) + ". " + results.get(i));

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
    }
}

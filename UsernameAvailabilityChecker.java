import java.util.*;

class UsernameAvailabilityChecker {
    private HashMap<String, Integer> usernames;
    private HashMap<String, Integer> attemptFrequency;

    UsernameAvailabilityChecker() {
        usernames = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }

    void registerUser(String username, int userId) {
        usernames.put(username, userId);
    }

    boolean checkAvailability(String username) {
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        return !usernames.containsKey(username);
    }

    List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String candidate = username + i;
            if (!usernames.containsKey(candidate))
                suggestions.add(candidate);
        }
        String dotVersion = username.replace("_", ".");
        if (!usernames.containsKey(dotVersion))
            suggestions.add(dotVersion);
        return suggestions;
    }

    String getMostAttempted() {
        String mostAttempted = "";
        int maxAttempts = 0;
        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }
        return mostAttempted + " (" + maxAttempts + " attempts)";
    }

    public static void main(String[] args) {
        UsernameAvailabilityChecker checker = new UsernameAvailabilityChecker();
        checker.registerUser("john_doe", 1);
        checker.registerUser("admin", 2);
        checker.registerUser("jane_smith", 3);

        System.out.println("john_doe available: " + checker.checkAvailability("john_doe"));
        System.out.println("new_user available: " + checker.checkAvailability("new_user"));
        System.out.println("Suggestions for john_doe: " + checker.suggestAlternatives("john_doe"));

        for (int i = 0; i < 100; i++)
            checker.checkAvailability("admin");

        System.out.println("Most attempted: " + checker.getMostAttempted());
    }
}

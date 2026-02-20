import java.util.*;

class FlashSaleInventoryManager {
    private HashMap<String, Integer> stock;
    private LinkedHashMap<String, LinkedList<Integer>> waitingList;

    FlashSaleInventoryManager() {
        stock = new HashMap<>();
        waitingList = new LinkedHashMap<>();
    }

    void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedList<>());
    }

    int checkStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    synchronized String purchaseItem(String productId, int userId) {
        int currentStock = stock.getOrDefault(productId, 0);
        if (currentStock > 0) {
            stock.put(productId, currentStock - 1);
            return "Success, " + (currentStock - 1) + " units remaining";
        } else {
            LinkedList<Integer> list = waitingList.get(productId);
            if (list == null) {
                list = new LinkedList<>();
                waitingList.put(productId, list);
            }
            list.add(userId);
            return "Added to waiting list, position #" + list.size();
        }
    }

    List<Integer> getWaitingList(String productId) {
        return waitingList.getOrDefault(productId, new LinkedList<>());
    }

    public static void main(String[] args) {
        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();
        manager.addProduct("IPHONE15_256GB", 3);

        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB") + " units");
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 11111));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 88888));
        System.out.println("Waiting list: " + manager.getWaitingList("IPHONE15_256GB"));
    }
}

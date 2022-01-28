package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String ORDERS_CSV = "orders.csv";
    private static final String ORDER_ITEMS_CSV = "order_items.csv";
    private static final String PRODUCTS_CSV = "products.csv";

    private static final Map<String, LocalDate> idDateMap = new HashMap<>();
    private static final Map<String, Integer> productToQuantityMap = new HashMap<>();
    private static String PRODUCT_NAME ;
    private static Integer MAX_PRICE = 0;


    public static void main(String[] args) throws URISyntaxException, IOException {
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        extractOrders();
        extractProductAndAmount();
        extractProductAndPrice();
        System.out.println(PRODUCT_NAME);
    }

    private static void extractProductAndPrice() throws URISyntaxException, IOException {
        URL res = Main.class.getClassLoader().getResource(PRODUCTS_CSV);
        File file = Paths.get(res.toURI()).toFile();

        BufferedReader csvReader = new BufferedReader(new FileReader(file));
        String row;
        boolean isLabel = true;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if (isLabel || !ifWeNeedOrder(data[1])) {
                isLabel = false;
                continue;
            }
            if (!ifWeNeedProduct(data[0])) {
                String productId = data[0];
                int temp_m = productToQuantityMap.get(productId) * Integer.parseInt(data[2]);
                if (temp_m > MAX_PRICE) {

                    MAX_PRICE = temp_m;
                    PRODUCT_NAME = data[1];
                }
            }
        }
        csvReader.close();
    }

    private static boolean ifWeNeedProduct(String productId) {
        return productToQuantityMap.get(productId) == null;
    }
    
    

    private static void extractProductAndAmount() throws URISyntaxException, IOException {
        URL res = Main.class.getClassLoader().getResource(ORDER_ITEMS_CSV);
        File file = Paths.get(res.toURI()).toFile();

        BufferedReader csvReader = new BufferedReader(new FileReader(file));
        String row;
        boolean isLabel = true;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if (isLabel || !ifWeNeedOrder(data[1])) {
                isLabel = false;
                continue;
            }
            if (!ifWeNeedOrder(data[0])) {
                String productId = data[1];
                int alreadyAdded = 0;
                if (productToQuantityMap.get(productId) != null) {
                    alreadyAdded = productToQuantityMap.get(productId);
                }
                productToQuantityMap.put(productId,Integer.parseInt(data[2]) + alreadyAdded);
            }
        }
        csvReader.close();
    }

    private static boolean ifWeNeedOrder(String orderId) {
        return idDateMap.get(orderId) == null;
    }

    private static void extractOrders() throws URISyntaxException, IOException {
        URL res = Main.class.getClassLoader().getResource(ORDERS_CSV);
        File file = Paths.get(res.toURI()).toFile();

        BufferedReader csvReader = new BufferedReader(new FileReader(file));
        String row;
        boolean isLabel = true;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if (isLabel) {
                isLabel = false;
                continue;
            }
            LocalDate date = getDateFromString(data[1]);
            if (!ifWeNeedDate(date)) {
                continue;
            }
            idDateMap.put(data[0], date);
        }
        csvReader.close();
    }

    private static boolean ifWeNeedDate(LocalDate date) {
        return LocalDate.parse("2021-01-21").equals(date);
    }

    private static LocalDate getDateFromString(String str) {
        int index = str.indexOf("T");
        String substring = str.substring(0, index);
        return LocalDate.parse(substring);
    }

}

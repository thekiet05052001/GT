import java.util.HashMap;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

class Final {
    public static Pair<HashMap<String, HashMap<String, HashMap<String, Double>>>, HashMap<String, Double>> generateWeight(
            String data) {// HashMap<String, HashMap<String, HashMap<String,
        // Object>>>
        // generateWeight(String data) {
        String[] lines = data.split("\n");
        HashMap<String, HashMap<String, HashMap<String, Double>>> transactWeightMap = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
        HashMap<String, Double> memoized = new HashMap<String, Double>();
        for (int i = 0; i < lines.length; i++) {
            HashMap<String, HashMap<String, Double>> transWeightMap = new HashMap<String, HashMap<String, Double>>();
            String[] currentLine = lines[i].split(" ");
            for (int j = 1; j < currentLine.length; j++) {
                if (memoized.containsKey(currentLine[j])) {
                    HashMap<String, Double> tempMap = new HashMap<String, Double>();
                    tempMap.put("weight", memoized.get(currentLine[j]));
                    if (transWeightMap.containsKey(currentLine[j])) {
                        tempMap.put("item_count", transWeightMap.get(currentLine[j]).get("item_count") + 1);
                    } else {
                        tempMap.put("item_count", (double) 1);
                    }
                    transWeightMap.put(currentLine[j], tempMap);
                } else {
                    Double weight = new Random().nextDouble();
                    HashMap<String, Double> tempMap = new HashMap<String, Double>();
                    tempMap.put("weight", weight);
                    tempMap.put("item_count", (double) 1);
                    transWeightMap.put(currentLine[j], tempMap);
                    memoized.put(currentLine[j], (Double) tempMap.get("weight"));
                }
            }
            transactWeightMap.put("T" + (i + 1), transWeightMap);
        }
        return new OrderedPair<>(transactWeightMap, memoized);
    }

    public static HashMap<String, Double> calculateSingleWS(HashMap<String, Double> item_weight_map,
            HashMap<String, HashMap<String, HashMap<String, Double>>> weighted_db,
            HashMap<String, Double> weight_dict,
            Double all_trans_weight) {
        HashMap<String, Double> singleWSMap = new HashMap<String, Double>();
        weighted_db.forEach((transaction, tv) -> {
            weighted_db.get(transaction).forEach((item, iv) -> {
                if (singleWSMap.containsKey(item)) {
                    singleWSMap.put(item, singleWSMap.get(item) + iv.get("weight"));
                } else {
                    singleWSMap.put(item, iv.get("weight"));
                }
            });
            ;
        });
        return singleWSMap;
    }

    public static Pair<HashMap<String, Double>, Double> calculate_trasnact_weight(
            HashMap<String, HashMap<String, HashMap<String, Double>>> weighted_db) {
        HashMap<String, Double> weight_dict = new HashMap<String, Double>();
        Double all_trans_weight = 0.0;
        for (String transaction : weighted_db.keySet()) {
            Double total_weight = 0.0;
            for (String item : weighted_db.get(transaction).keySet()) {
                total_weight += weighted_db.get(transaction).get(item).get("weight")
                        * weighted_db.get(transaction).get(item).get("item_count");
            }
            Double transaction_items = (double) weighted_db.get(transaction).size();
            Double transaction_weight = total_weight / transaction_items;
            weight_dict.put(transaction, transaction_weight);
            all_trans_weight += transaction_weight;
        }
        return new OrderedPair<HashMap<String, Double>, Double>(weight_dict, all_trans_weight);
    }

    public static void main(String[] args) {
        String sampleData = "1 5 5 3 4\n 2 3 4 5\n 4 9 8 4 5";
        List<String> sampleDataList = new ArrayList<String>();
        try (
                BufferedReader reader = new BufferedReader(new FileReader("./Resources/Data/retail.dat.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sampleDataList.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        }
        sampleData = String.join("\n", sampleDataList);
        long startTime = System.nanoTime();
        // HashMap<String, HashMap<String, HashMap<String, Object>>> transactWeightMap =
        // generateWeight(sampleData);
        Pair<HashMap<String, HashMap<String, HashMap<String, Double>>>, HashMap<String, Double>> tWMapAndMemoizedPair = generateWeight(
                sampleData);
        long endTime = System.nanoTime();
        // System.out.println(tWMapAndMemoizedPair.getKey().containsKey("T1"));
        System.out.println(endTime - startTime / 1_000_000_000.);
        // List<HashMap<String, Double>> testList = new ArrayList<HashMap<String,
        // Double>>();
        // HashMap<String, Double> testMap1 = new HashMap<String, Double>();
        // HashMap<String, Double> testMap2 = new HashMap<String, Double>();
        // testMap1.put("1", 123.0);
        // testMap2.put("9", 123.0);
        // testList.add(testMap1);
        // testList.add(testMap2);
        // HashMap<String, List<HashMap<String, Double>>> testData = new HashMap<String,
        // List<HashMap<String, Double>>>();
        // testData.put("T1", testList);

        // testData.forEach((k, v) -> {
        // System.out.println(k);
        // for (int i = 0; i < v.size(); i++) {
        // System.out.println(v.get(i).containsKey("9"));
        // }
        // });
        // System.out.println(endTime - startTime / 1_000_000_000.);
        // System.out.println(transactWeightMap);

    }
}
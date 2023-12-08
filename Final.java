import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map.Entry;

class Final {
    public static Pair<HashMap<String, HashMap<String, HashMap<String, Double>>>, HashMap<String, Double>> generateWeight(
            String data) {
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
                    Double temp_ws = weight_dict.get(transaction) / all_trans_weight;
                    singleWSMap.put(item, temp_ws);
                } else {
                    singleWSMap.put(item, 0.0);
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

    public static void insertTree(WNTree tree, List<String> transaction, Double transaction_weight) {
        WNNode current_node = tree.root;
        for (String item : transaction) {
            if (current_node.isItemInList(item)) {
                current_node.updateChildWeight(item, transaction_weight);
                current_node = current_node.getChild(item);
            } else {
                current_node.new_child = new WNNode();
                current_node.new_child.item_name = item;
                current_node.new_child.weight = transaction_weight;
                current_node.children.add(current_node.new_child);
                current_node = current_node.new_child;
            }
        }
    }

    public static String unionString(String s1, String s2) {
        String res = s1;
        for (int i = 0; i < s2.length(); i++) {
            if (!res.contains(s2.substring(i, i + 1))) {
                res += s2.substring(i, i + 1);
            }
        }
        return res;
    }

    public static Entry<String, List<PPWCode>> intersectWL(Entry<String, List<PPWCode>> wl1,
            Entry<String, List<PPWCode>> wl2) {
        HashMap<String, List<PPWCode>> intersected_wl = new HashMap<String, List<PPWCode>>();
        String intersected_name = unionString(wl1.getKey(), wl2.getKey());
        for (PPWCode ppwc1 : wl1.getValue()) {
            for (PPWCode ppwc2 : wl2.getValue()) {
                if (ppwc1.Pre > ppwc2.Pre && ppwc1.Post < ppwc2.Post) {
                    PPWCode temp_ppwc = new PPWCode(ppwc2.Pre, ppwc2.Post, ppwc1.weight + ppwc2.weight);
                    if (intersected_wl.containsKey(intersected_name)) {
                        for (PPWCode ppwc : intersected_wl.get(intersected_name)) {
                            if (ppwc.Pre == temp_ppwc.Pre && ppwc.Post == temp_ppwc.Post) {
                                ppwc.weight += temp_ppwc.weight;
                            }
                        }
                    } else {
                        intersected_wl.put(intersected_name, new ArrayList<PPWCode>());
                        intersected_wl.get(intersected_name).add(temp_ppwc);
                    }
                }
            }
        }
        if (intersected_wl.entrySet().iterator().hasNext()) {
            return intersected_wl.entrySet().iterator().next();
        } else {
            return null;
        }
    }

    public static int getMax(List<PPWCode> list) {
        int max = list.get(0).Post;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).Post > max) {
                max = list.get(i).Post;
            }
        }
        return max;
    }

    public static int getMin(List<PPWCode> list) {
        int min = list.get(0).Pre;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).Pre < min) {
                min = list.get(i).Pre;
            }
        }
        return min;
    }

    public static Double calculateWNLWS(Entry<String, List<PPWCode>> wl, Double sumtw) {
        Double wnlws = 0.0;
        for (PPWCode ppwc : wl.getValue()) {
            wnlws += ppwc.weight;
        }
        return wnlws / sumtw;
    }

    public static void Find_FWCI(List<Entry<String, List<PPWCode>>> w_list, Double sumtw, List<String> fwci,
            Double minws) {
        for (int i = w_list.size() - 1; i >= 0; i--) {
            List<Entry<String, List<PPWCode>>> inext = new ArrayList<Entry<String, List<PPWCode>>>();
            for (int j = i - 1; j >= 0; j--) {
                if (is_an_ancestor(w_list.get(i), w_list.get(j))) {
                    Entry<String, List<PPWCode>> intersect = intersectWL(w_list.get(i), w_list.get(j));
                    for (Entry<String, List<PPWCode>> next : inext) {
                        Entry<String, List<PPWCode>> intersect_temp = intersectWL(intersect, next);
                        inext.add(intersect_temp);
                    }
                    if (calculateWNLWS(w_list.get(i), sumtw) == calculateWNLWS(w_list.get(j), sumtw)) {
                        w_list.remove(j);
                        i--;
                    }
                } else {
                    Entry<String, List<PPWCode>> intersect_ij = intersectWL(w_list.get(i), w_list.get(j));
                    if (intersect_ij == null) {
                        continue;
                    }
                    if (calculateWNLWS(intersect_ij, sumtw) > minws && !fwci.contains(intersect_ij.getKey())) {
                        inext.add(intersect_ij);
                    }
                }
            }
            Find_FWCI(inext, sumtw, fwci, minws);
            fwci.add(w_list.get(i).getKey());
        }
    }

    public static boolean is_an_ancestor(Entry<String, List<PPWCode>> ancestor,
            Entry<String, List<PPWCode>> descendant) {
        if (ancestor.getValue().size() == 0 || descendant.getValue().size() == 0) {
            return false;
        }
        int a_smallest_pre = getMin(ancestor.getValue());
        int a_largest_post = getMax(ancestor.getValue());
        int d_smallest_pre = getMin(descendant.getValue());
        int d_largest_post = getMax(descendant.getValue());
        return a_smallest_pre < d_smallest_pre && a_largest_post > d_largest_post;
    }

    public static void main(String[] args) {
        final Double minws = 0.3;
        String sampleData = "1 5 5 3 4\n 2 3 4 5\n 4 9 8 4 5";
        List<String> sampleDataList = new ArrayList<String>();
        try (
                BufferedReader reader = new BufferedReader(new FileReader("retail-mini.dat.txt"))) {
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
        // Generate weight and item list with weights
        Pair<HashMap<String, HashMap<String, HashMap<String, Double>>>, HashMap<String, Double>> tWMapAndMemoizedPair = generateWeight(
                sampleData);
        long endTime = System.nanoTime();
        HashMap<String, Double> item_weights = tWMapAndMemoizedPair.getValue();

        // Calculate transaction weight
        Pair<HashMap<String, Double>, Double> tWAndATWPair = calculate_trasnact_weight(tWMapAndMemoizedPair.getKey());

        HashMap<String, Double> transaction_weights = tWAndATWPair.getKey();

        // Calculate items weighted support
        HashMap<String, Double> item_wss_temp = calculateSingleWS(item_weights, tWMapAndMemoizedPair.getKey(),
                tWAndATWPair.getKey(), tWAndATWPair.getValue());

        HashMap<String, Double> item_wss = new HashMap<String, Double>();
        item_wss_temp.forEach((k, v) -> {
            if (v < minws) {
                item_wss.put(k, v);
            }
        });

        WNTree tree = new WNTree();
        for (String transaction : tWMapAndMemoizedPair.getKey().keySet()) {
            HashMap<String, HashMap<String, Double>> transaction_item_weight = tWMapAndMemoizedPair.getKey()
                    .get(transaction);
            ArrayList<String> processed_transaction = new ArrayList<String>();
            Map<String, Double> temp_items = new HashMap<String, Double>();

            for (String item : transaction_item_weight.keySet()) {
                if (item_wss.containsKey(item)) {
                    temp_items.put(item, transaction_item_weight.get(item).get("weight"));
                }
                List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
                        temp_items.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return Double.compare(o2.getValue(), o1.getValue());
                    }
                });

                list.forEach((v) -> {
                    if (!processed_transaction.contains(v.getKey())) {
                        processed_transaction.add(v.getKey());
                    }
                });

            }
            insertTree(tree, processed_transaction, transaction_weights.get(transaction));
        }

        // Traverse preorder and postorder
        tree.traversePreorder(tree.root, 1);
        tree.traversePostOrder(tree.root, 0);

        // Generate WNList
        HashMap<String, List<PPWCode>> w_map = new HashMap<String, List<PPWCode>>();
        tree.root.generateWList(w_map);
        // w_map.forEach((k, v) -> {
        // v.forEach((vt) -> {
        // System.out.println("I:" + k + " Pr:" + vt.Pre + " Po:" + vt.Post + " W:" +
        // vt.weight);
        // });
        // });
        ArrayList<Entry<String, List<PPWCode>>> w_list = new ArrayList<Entry<String, List<PPWCode>>>(w_map.entrySet());
        List<String> fwci = new ArrayList<String>();
        startTime = System.nanoTime();
        Find_FWCI(w_list, tWAndATWPair.getValue(), fwci, minws);
        endTime = System.nanoTime();

        System.out.println(fwci);
        System.out.println("Time to find FWCI: " + (endTime - startTime) / 1000000 + "ms");
        // for (int i = 0; i < w_list.size(); i++) {
        // for (int j = i + 1; j < w_list.size(); j++) {
        // Entry<String, List<PPWCode>> intersected_wl = intersectWL(w_list.get(i),
        // w_list.get(j));
        // if (intersected_wl == null) {
        // continue;
        // }
        // intersected_wl.getValue().forEach((v) -> {
        // System.out.println(
        // "I:" + intersected_wl.getKey() + " Pr:" + v.Pre + " Po:" + v.Post + " W:" +
        // v.weight);
        // });
        // }
        // }
    }
}
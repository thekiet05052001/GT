import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class WNNode {
    String item_name;
    Double weight;
    int Pre;
    int Post;
    WNNode new_child;
    List<WNNode> children;

    public WNNode() {
        this.item_name = "";
        this.weight = 0.0;
        this.Pre = 0;
        this.Post = 0;
        this.new_child = null;
        this.children = new ArrayList<WNNode>();
    }

    public boolean isItemInList(String item) {
        if (this.children == null) {
            return false;
        }
        for (WNNode child : this.children) {
            if (child.item_name == item) {
                return true;
            }
        }
        return false;
    }

    public void updateChildWeight(String item, Double transaction_weight) {
        for (WNNode child : this.children) {
            if (child.item_name == item) {
                child.weight += transaction_weight;
            }
        }
    }

    public WNNode getChild(String item) {
        for (WNNode child : this.children) {
            if (child.item_name == item) {
                return child;
            }
        }
        return null;
    }

    public void generateWList(HashMap<String, List<PPWCode>> wnlist) {
        for (WNNode child : this.children) {
            if (!wnlist.containsKey(child.item_name)) {
                wnlist.put(child.item_name, new ArrayList<PPWCode>());
            }
            wnlist.get(child.item_name).add(new PPWCode(child.Pre, child.Post, child.weight));
            child.generateWList(wnlist);
            // HashMap<String, List<PPWCode>> temp_w = new HashMap<String, List<PPWCode>>();
            // List<PPWCode> temp_ppwc_list = new ArrayList<PPWCode>();
            // temp_ppwc_list.add(new PPWCode(child.Pre, child.Post, child.weight));
            // temp_w.put(child.item_name, temp_ppwc_list);
            // wnlist.add(temp_w);
            // child.generateWList(wnlist);
        }
    }
}

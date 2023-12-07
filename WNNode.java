import java.util.ArrayList;
import java.util.List;

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
}

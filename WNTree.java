import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class WNTree {
    WNNode root;
    List<WNNode> preorder_list;
    List<WNNode> postorder_list;

    public WNTree() {
        this.root = new WNNode();
        this.preorder_list = new ArrayList<>();
        this.postorder_list = new ArrayList<>();
    }

    public void printTree(WNNode node, int level) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        System.out.println(node.item_name + " " + node.weight);
        for (WNNode child : node.children) {
            printTree(child, level + 1);
        }

    }

    public void traversePreorder(WNNode node, int level) {
        if (node == null) {
            return;
        }
        this.preorder_list.add(node);
        node.Pre = this.preorder_list.size();
        for (WNNode child : node.children) {
            traversePreorder(child, level + 1);
        }
    }

    public void traversePostOrder(WNNode node, int level) {
        if (node == null) {
            return;
        }
        for (WNNode child : node.children) {
            traversePostOrder(child, level);
        }
        this.postorder_list.add(node);
        node.Post = this.postorder_list.size();
    }

    public List<HashMap<String, PPWCode>> generateWList() {
        List<HashMap<String, PPWCode>> w_list = new ArrayList<HashMap<String, PPWCode>>();
        if (this.preorder_list == null) {
            return w_list;
        }
        for (int i = 0; i < this.preorder_list.size(); i++) {
            WNNode node = this.preorder_list.get(i);
            if (node.children.size() == 0) {
                continue;
            }
            HashMap<String, PPWCode> w = new HashMap<String, PPWCode>();
            for (WNNode child : node.children) {
                w.put(child.item_name, new PPWCode(child.Pre, child.Post, child.weight));
            }
            w_list.add(w);
        }
        return w_list;
    }
}

import java.util.List;

public class WNNode {
    String item_name;
    Double weight;
    WNNode Pre;
    WNNode Post;
    WNNode new_child;
    List<WNNode> children;

    public WNNode() {
        this.item_name = null;
        this.weight = 0.0;
        this.Pre = null;
        this.Post = null;
        this.new_child = null;
        this.children = null;
    }
}

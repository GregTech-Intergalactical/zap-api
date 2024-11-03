package tesseract.newgraph;

import java.util.List;

// keep track on which node is being looked for across the recursive functions
public class NodeList<T> {

    List<INode<T>> nodes;
    int counter = 0;

    public NodeList(List<INode<T>> nodes) {
        this.nodes = nodes;
    }

    INode<T> getNextNode() {
        if (++counter < nodes.size()) return nodes.get(counter);
        else return null;
    }

    INode<T> getNode() {
        if (counter < nodes.size()) return nodes.get(counter);
        else return null;
    }
}

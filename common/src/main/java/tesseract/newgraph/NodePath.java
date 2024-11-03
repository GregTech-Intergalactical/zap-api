package tesseract.newgraph;


import tesseract.api.IConnectable;

// to contain all info about the path between nodes
public class NodePath<T extends IConnectable> {

    protected T[] mPipes;
    public Lock lock = new Lock();

    public NodePath(T[] pipes) {
        this.mPipes = pipes;
        processPipes();
    }

    protected void processPipes() {
        for (T tPipe : mPipes) {
            tPipe.setNodePath(this);
        }
    }

    public void clearPath() {
        for (T mPipe : mPipes) {
            mPipe.setNodePath(null);
        }
    }

    public void reloadLocks() {
        for (T pipe : mPipes) {
            pipe.reloadLocks();
        }
    }
}

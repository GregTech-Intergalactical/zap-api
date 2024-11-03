package tesseract.newgraph;

import gregtech.api.graphs.consumers.ConsumerNode;
import gregtech.api.graphs.paths.PowerNodePath;
import net.minecraft.core.Direction;

/*
 * look for and power node that need power how this works a node only contains nodes that has a higher value then it
 * self except for 1 which is the return node this node also contains the highest known node value of its network this
 * network only includes nodes that have a higher value then it self so it does not know the highest known value that
 * the return node knows with these rules we can know for the target node to be in the network of a node, the target
 * node must have a value no less than the node we are looking and no greater than the highest value that node knows
 * this way we don't have to go over the entire network to look for it we also hold a list of all consumers so we can
 * check before looking if that consumer actually needs power and only look for nodes that actually need power
 */
public class NodeUtils {

    // check if the looked for node is next to or get the next node that is closer to it
    public static <T> long powerNode(INode<T> aCurrentNode, INode<T> aPreviousNode, NodeList<T> aConsumers, long aVoltage) {
        long tAmpsUsed = 0;
        INode<T> tConsumer = aConsumers.getNode();
        int tLoopProtection = 0;
        while (tConsumer != null) {
            int tTargetNodeValue = tConsumer.getNodeValue();
            // if the target node has a value less then the current node
            if (tTargetNodeValue < aCurrentNode.getNodeValue() || tTargetNodeValue > aCurrentNode.getHighestNodeValue()) {
                for (Direction side : Direction.values()) {
                    final INode<T> tNextNode = aCurrentNode.getNeighborNode(side);
                    if (tNextNode != null && tNextNode.getNodeValue() < aCurrentNode.getNodeValue()) {
                        if (tNextNode.getNodeValue() == tConsumer.getNodeValue()) {
                            tAmpsUsed += processNodeInject(aCurrentNode, tConsumer, side, aMaxAmps - tAmpsUsed, aVoltage);
                            tConsumer =  aConsumers.getNextNode();
                        } else {
                            if (aPreviousNode == tNextNode) return tAmpsUsed;
                            tAmpsUsed += processNextNode(
                                aCurrentNode,
                                tNextNode,
                                aConsumers,
                                side,
                                aMaxAmps - tAmpsUsed,
                                aVoltage);
                            tConsumer =  aConsumers.getNode();
                        }
                        break;
                    }
                }
            } else {
                // if the target node has a node value greater then current node value
                for (int sideOrdinal = 5; sideOrdinal > -1; sideOrdinal--) {
                    Direction side = Direction.from3DDataValue(sideOrdinal);
                    final INode<T> tNextNode = aCurrentNode.getNeighborNode(side);
                    if (tNextNode == null) continue;
                    if (tNextNode.getNodeValue() > aCurrentNode.getNodeValue() && tNextNode.getNodeValue() < tTargetNodeValue) {
                        if (tNextNode == aPreviousNode) return tAmpsUsed;
                        tAmpsUsed += processNextNodeAbove(
                            aCurrentNode,
                            tNextNode,
                            aConsumers,
                            side,
                            aMaxAmps - tAmpsUsed,
                            aVoltage);
                        tConsumer = aConsumers.getNode();
                        break;
                    } else if (tNextNode.getNodeValue() == tTargetNodeValue) {
                        tAmpsUsed += processNodeInject(aCurrentNode, tConsumer, sideOrdinal, aMaxAmps - tAmpsUsed, aVoltage);
                        tConsumer = aConsumers.getNextNode();
                        break;
                    }
                }
            }
            if (aMaxAmps - tAmpsUsed <= 0) {
                return tAmpsUsed;
            }
            if (tLoopProtection++ > 20) {
                throw new NullPointerException("infinite loop in powering nodes ");
            }
        }
        return tAmpsUsed;
    }

    // checking if target node is next to it or has a higher value then current node value
    // these functions are different to either go down or up the stack
    protected static <T> long powerNodeAbove(INode<T> aCurrentNode, INode<T> aPreviousNode, NodeList<T> aConsumers, long aVoltage,
        long aMaxAmps) {
        long tAmpsUsed = 0;
        int tLoopProtection = 0;
        INode<T> tConsumer = aConsumers.getNode();
        while (tConsumer != null) {
            int tTargetNodeValue = tConsumer.getNodeValue();
            if (tTargetNodeValue > aCurrentNode.getHighestNodeValue() || tTargetNodeValue < aCurrentNode.getNodeValue()) {
                return tAmpsUsed;
            } else {
                for (int sideOrdinal = 5; sideOrdinal > -1; sideOrdinal--) {
                    Direction side = Direction.from3DDataValue(sideOrdinal);
                    final INode<T> tNextNode = aCurrentNode.getNeighborNode(side);
                    if (tNextNode == null) continue;
                    if (tNextNode.getNodeValue() > aCurrentNode.getNodeValue() && tNextNode.getNodeValue() < tTargetNodeValue) {
                        if (tNextNode == aPreviousNode) return tAmpsUsed;
                        tAmpsUsed += processNextNodeAbove(
                            aCurrentNode,
                            tNextNode,
                            aConsumers,
                            side,
                            aMaxAmps - tAmpsUsed,
                            aVoltage);
                        tConsumer = aConsumers.getNode();
                        break;
                    } else if (tNextNode.getNodeValue() == tTargetNodeValue) {
                        tAmpsUsed += processNodeInject(aCurrentNode, tConsumer, side, aMaxAmps - tAmpsUsed, aVoltage);
                        tConsumer = aConsumers.getNextNode();
                        break;
                    }
                }
            }
            if (aMaxAmps - tAmpsUsed <= 0) {
                return tAmpsUsed;
            }
            if (tLoopProtection++ > 20) {
                throw new NullPointerException("infinite loop in powering nodes ");
            }
        }
        return tAmpsUsed;
    }

    protected static <T> long processNextNode(INode<T> aCurrentNode, INode<T> aNextNode, NodeList<T> aConsumers, Direction ordinalSide,
        long aMaxAmps, long aVoltage) {
        if (aCurrentNode.getLock(ordinalSide).isLocked()) {
            aConsumers.getNextNode();
            return 0;
        }
        final NodePath<?> tPath = aCurrentNode.mNodePaths[ordinalSide];
        final NodePath<?> tSelfPath = aCurrentNode.mSelfPath;
        long tVoltLoss = 0;
        if (tSelfPath != null) {
            tVoltLoss += tSelfPath.getLoss();
            tSelfPath.applyVoltage(aVoltage, false);
        }
        tPath.applyVoltage(aVoltage - tVoltLoss, true);
        tVoltLoss += tPath.getLoss();
        long tAmps = powerNode(aNextNode, aCurrentNode, aConsumers, aVoltage - tVoltLoss, aMaxAmps);
        tPath.addAmps(tAmps);
        if (tSelfPath != null) tSelfPath.addAmps(tAmps);
        return tAmps;
    }

    protected static long processNextNodeAbove(Node aCurrentNode, Node aNextNode, NodeList aConsumers, int ordinalSide,
        long aMaxAmps, long aVoltage) {
        if (aCurrentNode.locks[ordinalSide].isLocked()) {
            aConsumers.getNextNode();
            return 0;
        }
        final PowerNodePath tPath = (PowerNodePath) aCurrentNode.mNodePaths[ordinalSide];
        final PowerNodePath tSelfPath = (PowerNodePath) aCurrentNode.mSelfPath;
        long tVoltLoss = 0;
        if (tSelfPath != null) {
            tVoltLoss += tSelfPath.getLoss();
            tSelfPath.applyVoltage(aVoltage, false);
        }
        tPath.applyVoltage(aVoltage - tVoltLoss, true);
        tVoltLoss += tPath.getLoss();
        long tAmps = powerNodeAbove(aNextNode, aCurrentNode, aConsumers, aVoltage - tVoltLoss, aMaxAmps);
        tPath.addAmps(tAmps);
        if (tSelfPath != null) tSelfPath.addAmps(tAmps);
        return tAmps;
    }

    protected static long processNodeInject(Node aCurrentNode, ConsumerNode aConsumer, int ordinalSide, long aMaxAmps,
        long aVoltage) {
        if (aCurrentNode.locks[ordinalSide].isLocked()) return 0;
        final PowerNodePath tPath = (PowerNodePath) aCurrentNode.mNodePaths[ordinalSide];
        final PowerNodePath tSelfPath = (PowerNodePath) aCurrentNode.mSelfPath;
        long tVoltLoss = 0;
        if (tSelfPath != null) {
            tVoltLoss += tSelfPath.getLoss();
            tSelfPath.applyVoltage(aVoltage, false);
        }
        tPath.applyVoltage(aVoltage - tVoltLoss, true);
        tVoltLoss += tPath.getLoss();
        long tAmps = aConsumer.injectEnergy(aVoltage - tVoltLoss, aMaxAmps);
        tPath.addAmps(tAmps);
        if (tSelfPath != null) tSelfPath.addAmps(tAmps);
        return tAmps;
    }
}

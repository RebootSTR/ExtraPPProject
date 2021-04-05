/**
 * @author Aydar Rafikov
 */
public class Node<T> implements NodeInterface<T>{

    private T value;

    private NodeInterface<T> next;
    private NodeInterface<T> previous;

    public Node(T value) {
        setValue(value);
    }

    public Node(T value, NodeInterface<T> previous, NodeInterface<T> next) {
        this(value);
        link(previous, this);
        link(this, next);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public T setValue(T value) {
        this.value = value;
        return this.value;
    }

    @Override
    public NodeInterface<T> getNext() {
        return next;
    }

    @Override
    public NodeInterface<T> getPrevious() {
        return previous;
    }

    @Override
    public void setNext(NodeInterface<T> node) {
        next = node;
    }

    @Override
    public void setPrevious(NodeInterface<T> node) {
        previous = node;
    }

    @Override
    public void link(NodeInterface<T> prev, NodeInterface<T> next) {
        if (prev != null) {
            prev.setNext(next);
        }
        if (next != null) {
            next.setPrevious(prev);
        }
    }

    @Override
    public void removeNext() {
        next = null;
    }

    @Override
    public void removePrevious() {
        previous = null;
    }
}

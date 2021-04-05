/**
 * @author Aydar Rafikov
 */
public interface NodeInterface<T> {

    T getValue();
    T setValue(T value);

    NodeInterface<T> getNext();
    NodeInterface<T> getPrevious();

    void setNext(NodeInterface<T> node);
    void setPrevious(NodeInterface<T> node);

    void link(NodeInterface<T> prev, NodeInterface<T> next);

    void removeNext();
    void removePrevious();
}

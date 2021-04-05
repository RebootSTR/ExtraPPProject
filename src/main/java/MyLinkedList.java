import java.util.*;
import java.util.function.BiFunction;

/**
 * @author Aydar Rafikov
 */
public class MyLinkedList<T> implements List<T> {

    private NodeInterface<T> startNode;
    private NodeInterface<T> endNode;

    private int count;

    /**
     * Получение размера списка.
     */
    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean contains(Object o) {
        return getNodeByObject(o) == null;
    }

    @Override
    public Iterator<T> iterator() {
        return new MyLinkedListIterator(0);
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size()];
        NodeInterface<T> current = startNode;
        for (int i = 0; i < size(); i++, current = current.getNext()) {
            array[i] = current;
        }
        return array;
    }

    @Override
    public <E> E[] toArray(E[] a) {
        if (a.length < size())
            a = (E[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size());
        int i = 0;
        Object[] result = a;
        for (NodeInterface<T> x = startNode; x != null; x = x.getNext()) {
            result[i++] = x.getValue();
        }

        if (a.length > size())
            a[size()] = null;

        return a;
    }

    /**
     * Вставка в конец списка О(1).
     */
    @Override
    public boolean add(T t) {
        NodeInterface<T> node = new Node<>(t, endNode, null);
        if (isEmpty()) {
            startNode = node;
        }
        endNode = node;
        count++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        NodeInterface<T> current = getNodeByObject(o);
        if (current == null) {
            return false;
        }
        removeNode(current);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (isEmpty()) {
            return false;
        }
        NodeInterface<T> current = startNode;
        for (int i = 0; i < size(); i++, current = current.getNext()) {
            if (!c.contains(current.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T element : c) {
            this.add(element);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        NodeInterface<T> current = getNode(index);
        for (T element : c) {
            insertByNode(element, current);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object element : c) {
            NodeInterface<T> node = getNodeByObject(element);
            if (node != null) {
                result = true;
                removeNode(node);
            }
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Iterator<NodeInterface<T>> iterator = nodeIterator();
        while (iterator.hasNext()) {
            NodeInterface<T> node = iterator.next();
            if (c.contains(node.getValue())) {
                removeNode(node);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        if (size() > 2) {
            NodeInterface<T> cursor = startNode.getNext();
            for (int i = 0; i < size() - 2; i++) {
                cursor.getPrevious().removePrevious();
                cursor.getPrevious().removeNext();
                cursor = cursor.getNext();
            }
            cursor.removePrevious();
            cursor.removeNext();
        }
        startNode = null;
        endNode = null;
        count = 0;
    }

    @Override
    public T get(int index) {
        return getNode(index).getValue();
    }

    @Override
    public T set(int index, T element) {
        return getNode(index).setValue(element);
    }

    /**
     * Вставка в любое место списка О(1) - О(n/2).
     */
    @Override
    public void add(int index, T element) {
        if (index == size()) {
            add(element);
        } else {
            NodeInterface<T> oldNode = getNode(index);
            insertByNode(element, oldNode);
        }
    }

    /**
     * Удаление элемента из списка О(1) - О(n/2).
     */
    @Override
    public T remove(int index) {
        NodeInterface<T> node = getNode(index);
        return removeNode(node);
    }

    @Override
    public int indexOf(Object o) {
        if (size() == 0) {
            return -1;
        }
        NodeInterface<T> current = startNode;
        for (int i = 0; i < size(); i++) {
            if (current.getValue().equals(o)) {
                return i;
            }
            current = current.getNext();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (size() == 0) {
            return -1;
        }
        NodeInterface<T> current = startNode;
        for (int i = size()-1; i >= size(); i--) {
            if (current.getValue().equals(o)) {
                return i;
            }
            current = current.getPrevious();
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new MyLinkedListIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        List<T> result = new MyLinkedList<>();
        NodeInterface<T> node = getNode(fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            result.add(node.getValue());
            node = node.getNext();
        }
        return result;
    }

    /**
     * Вставка в начало списка О(1)
     * @param value
     */
    public void addFirst(T value) {
        add(0, value);
    }

    /**
     * Удаление из начала списка О(1).
     */
    public void removeFirst() {
        remove(0);
    }

    /**
     * Удаление из конца списка О(1).
     */
    public void removeLast() {
        remove(size() - 1);
    }

    /**
     * Получить минимальный элемент коллекции.
     */
    public T getMinimal(Comparator<? super T> comparator) {
        return getMinOrMax((first, second) -> {
            if (comparator.compare(first, second) > 0) {
                return second;
            } else {
                return first;
            }
        });
    }

    /**
     * Получить максимальный элемент коллекции.
     */
    public T getMaximum(Comparator<? super T> comparator) {
        return getMinOrMax((first, second) -> {
            if (comparator.compare(first, second) < 0) {
                return second;
            } else {
                return first;
            }
        });
    }

    private T getMinOrMax(BiFunction<T, T, T> findFunc){
        if (size() == 0) {
            return null;
        }
        T result = startNode.getValue();
        for (T current : this) {
            result = findFunc.apply(result, current);
        }
        return result;
    }

    private Iterator<NodeInterface<T>> nodeIterator() {
        return new Iterator<NodeInterface<T>>() {
            private final NodeInterface<T> node = startNode;

            @Override
            public boolean hasNext() {
                return node.getNext() != null;
            }

            @Override
            public NodeInterface<T> next() {
                return node.getNext();
            }
        };
    }

    private void insertByNode(T element, NodeInterface<T> oldNode) {
        NodeInterface<T> newNode = new Node<>(element, oldNode.getPrevious(), oldNode);
        oldNode.setPrevious(newNode);
        count++;
        if (oldNode == startNode) {
            startNode = newNode;
        }
    }

    /**
     * Получение узла по индексу О(1) - О(n/2).
     */
    private NodeInterface<T> getNode(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        NodeInterface<T> current;
        if (index < size() / 2) {
            current = startNode;
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        } else {
            current = endNode;
            for (int i = size()-1; i > index; i--) {
                current = current.getPrevious();
            }
        }
        return current;
    }



    /**
     * Удаление узла из списка по адресу, О(1).
     * @param node
     * @return
     */
    private T removeNode(NodeInterface<T> node) {
        T element = node.getValue();
        if (size() == 1) {
            startNode = null;
            endNode = null;
            node.setNext(null);
            node.setPrevious(null);
        } else if (node == startNode) {
            startNode.getNext().setPrevious(null);
            startNode = node.getNext();
        } else if (node == endNode) {
            endNode.getPrevious().setNext(null);
            endNode = node.getPrevious();
        } else {
            node.getPrevious().setNext(node.getNext());
            node.getNext().setPrevious(node.getPrevious());
        }
        count--;
        return element;
    }

    private NodeInterface<T> getNodeByObject(Object o) {
        if (size() == 0) {
            return null;
        }
        NodeInterface<T> current = startNode;
        for (int i = 0; i < size(); i++) {
            if (current.getValue().equals(o)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    @Override
    public void sort(Comparator<? super T> c) {
        qSort(c);
    }

    /**
     * Быстрая сортировка.
     * @param comparator Компаратор для сравнение элементов T
     */
    private void qSort(Comparator<? super T> comparator) {
        qSort(startNode, 0, endNode, size() - 1, comparator);
    }

    private void qSort(NodeInterface<T> from, int fromInd, NodeInterface<T> to, int toInd, Comparator<? super T> comparator) {
        NodeInterface<T> stay = from;
        int stayInd = fromInd;
        NodeInterface<T> run = to;
        int runInd = toInd;
        while (true) {
            while ((stay.getValue().equals(run.getValue()) ||
                    (stayInd < runInd == comparator.compare(stay.getValue(), run.getValue()) < 0)) &&
                    stayInd != runInd) {
                if (stayInd > runInd) {
                    runInd++;
                    run = run.getNext();
                } else {
                    runInd--;
                    run = run.getPrevious();
                }
            }
            if (stayInd == runInd) {
                if (stayInd != fromInd && stayInd != 0) {
                    qSort(from, fromInd, stay.getPrevious(), stayInd - 1, comparator);
                }
                if (stayInd != toInd && stayInd != size() - 1) {
                    qSort(stay.getNext(), stayInd+1, to, toInd, comparator);
                }
                break;
            }
            // swap nodes
            T tmpT = stay.getValue();
            stay.setValue(run.getValue());
            run.setValue(tmpT);
            // swap stay and run
            NodeInterface<T> tmpNode = stay;
            stay = run;
            run = tmpNode;
            // swap stayInd and runInd
            int tmpInt = stayInd;
            stayInd = runInd;
            runInd = tmpInt;

        }
    }

    class MyLinkedListIterator implements ListIterator<T> {

        public MyLinkedListIterator(int index) {
            this.index = index - 1;
            if (this.index == -1) {
                node = null;
            } else {
                node = getNode(this.index);
            }
        }

        private NodeInterface<T> node;
        private int index;
        private boolean previousIsThis = false;

        @Override
        public boolean hasNext() {
            return !((index == -1 && isEmpty()) || nextIndex() == size());
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No element next in MyLinkedList");
            }
            if (node == null) {
                node = startNode;
            } else {
                node = node.getNext();
            }
            index++;
            return node.getValue();
        }

        @Override
        public boolean hasPrevious() {
            return !(index == -1 || previousIndex() == -1);
        }

        @Override
        public T previous() {
            if (previousIsThis) {
                previousIsThis = false;
                return node.getValue();
            }
            if (!hasPrevious()) {
                throw new NoSuchElementException("No element next in MyLinkedList");
            }
            node = node.getPrevious();
            index--;
            return node.getValue();
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            if (previousIsThis) {
                return index;
            }
            return index - 1;
        }

        @Override
        public void remove() {
            if (index == -1) {
                throw new IllegalArgumentException("Nothing to remove");
            }
            removeNode(node);
            if (index == size()) {
                index--;
            }
        }

        @Override
        public void set(T t) {
            if (index == -1) {
                throw new IllegalArgumentException("Nothing to set");
            }
            node.setValue(t);
        }

        @Override
        public void add(T t) {
            MyLinkedList.this.add(nextIndex(), t);
            node = node.getNext();
            index++;
            previousIsThis = true;
        }
    }
}

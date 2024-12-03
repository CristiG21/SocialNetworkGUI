package ubb.scs.map.utils.paging;

import java.util.Collection;

public class Page<E> {
    private final Collection<E> elementsOnPage;
    private final int totalNumberOfElements;

    public Page(Collection<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public Collection<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}

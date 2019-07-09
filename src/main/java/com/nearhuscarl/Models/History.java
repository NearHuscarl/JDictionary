package com.nearhuscarl.Models;

import java.util.ArrayList;

public class History<T> {
    private int currentIndex = -1;
    private ArrayList<T> collection = new ArrayList<>();

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public ArrayList<T> getCollection() {
        return collection;
    }

    public void setCollection(ArrayList<T> collection) {
        this.collection = collection;
    }

    public T current() {
        if (currentIndex == -1 || collection.size() == 0) {
            return null;
        }
        return collection.get(currentIndex);
    }

    public boolean isFirst() {
        return currentIndex == 0;
    }

    public boolean isLast() {
        return currentIndex == collection.size() - 1;
    }

    public int count() {
        return collection.size();
    }

    public void add(T item) {
        currentIndex++;

        if (currentIndex <= collection.size() - 1)
        {
            collection.add(currentIndex, item);
        }
        else
        {
            collection.add(item);
        }

        for (int i = currentIndex + 1; i <= collection.size() - 1;) {
            collection.remove(i);
        }
    }

    public T previous() {
        if (currentIndex > 0) {
            return collection.get(--currentIndex);
        }

        return collection.get(0);
    }


    public T next() {
        if (currentIndex < collection.size() - 1) {
            return collection.get(++currentIndex);
        }

        return collection.get(collection.size() - 1);
    }

    public void trim(int limit) {
        if (collection.size() > limit)
        {
            int oldWordCount = collection.size() - limit;
            for (int i = 0; i < oldWordCount; i++) {
                collection.remove(0);
                if (currentIndex >= collection.size()) {
                    currentIndex--;
                }
            }
        }
    }
}

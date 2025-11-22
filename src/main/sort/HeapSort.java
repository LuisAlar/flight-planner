package main.sort;

import java.util.Comparator;
import java.util.List;

public final class HeapSort {

    public static <T> void sort(List<T> list, Comparator<T> cmp) {
        int n = list.size();

        // build max-heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, cmp);
        }

        // one by one extract from heap
        for (int i = n - 1; i > 0; i--) {
            swap(list, 0, i);          // move current root to end
            heapify(list, i, 0, cmp);  // heapify reduced heap
        }
    }

    private static <T> void heapify(List<T> list, int heapSize, int i, Comparator<T> cmp) {
        int largest = i;
        int left  = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < heapSize && cmp.compare(list.get(left), list.get(largest)) > 0) {
            largest = left;
        }
        if (right < heapSize && cmp.compare(list.get(right), list.get(largest)) > 0) {
            largest = right;
        }
        if (largest != i) {
            swap(list, i, largest);
            heapify(list, heapSize, largest, cmp);
        }
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }

    private HeapSort() {}
}

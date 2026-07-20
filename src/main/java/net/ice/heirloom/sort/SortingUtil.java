package net.ice.heirloom.sort;

import java.util.List;

public class SortingUtil {

	public static <T extends Sortable> List<T> insertionSort(List<T> list) {
		for (int i = 1; i < list.size(); i++) {
			T key = list.get(i);
			int j = i - 1;

			while(j >= 0 && list.get(j).sortValue() > key.sortValue()) {
				list.set(j + 1, list.get(j));
				j = j - 1;
			}

			list.set(j + 1, key);
		}

		return list;
	}

	public static <T extends Sortable> List<T> reverseInsertionSort(List<T> list) {
		for (int i = 1; i < list.size(); i++) {
			T key = list.get(i);
			int j = i - 1;

			while(j >= 0 && list.get(j).sortValue() < key.sortValue()) {
				list.set(j + 1, list.get(j));
				j = j - 1;
			}

			list.set(j + 1, key);
		}

		return list;
	}
}


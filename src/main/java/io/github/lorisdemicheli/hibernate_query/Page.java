package io.github.lorisdemicheli.hibernate_query;

import java.util.ArrayList;
import java.util.Collection;

public class Page<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	private int totalPage, currentPage;
	private long totalElements, currentElements;

	public Page(Collection<? extends E> c, int totalPage, int currentPage, long totalElements) {
		super(c);
		this.totalPage = totalPage;
		this.currentPage = currentPage;
		this.totalElements = totalElements;
		this.currentElements = c.size();
	}

	public int getTotalPage() {
		return totalPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public long getCurrentElements() {
		return currentElements;
	}

	@Override
	public String toString() {
		return "totalPage: " + totalPage + 
			", currentPage: " + currentPage + 
			", totalElements: " + totalElements +
			", currentElements: " + currentElements +
			",values " + super.toString();
	}
	
}

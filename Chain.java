package server;

public class Chain<T> extends Link<T> {
	private int size = 0;
	
	private Link<T> current = this;
	
	public Chain() {
		next = this;
		previous = this;
		content = null;
	}

	/** 
	 * Adds an object to a new link in the chain. 
	 * 
	 * @param content
	 */
	public void add(T content) {
		this.add(content, 0);
	}

	/**
	 * Adds an object to a new link with a specified index in the chain. 
	 * 
	 * @param content
	 * @param index
	 */
	public void add(T content, int index) {
		if (index > size || index < 0) {
			index = size;
		}
		Link<T> preceding = this.getLink(0, index);
		Link<T> newLink = new Link<T>();
		newLink.content = content;
		newLink.next = preceding.next;
		newLink.previous = preceding;
		newLink.next.previous = newLink;
		newLink.previous.next = newLink;
		size++;
	}

	/**
	 * Returns the object of a link in the chain specified by its index. 
	 * 
	 * @param index
	 * @return
	 */
	public T get(int index) {
		if (index >= size || index < 0) {
			return null;
		}
		return next.getLink(0, index).content;
	}

	/**
	 * Removes a link and its content from the chain specified by its index. 
	 * 
	 * @param index
	 * @return
	 */
	public T remove(int index) {
		if (index >= size || index < 0) {
			return null;
		}
		return remove(next.getLink(0, index));
	}

	/**
	 * Returns the index of a link containing a specified object. 
	 * 
	 * @param object
	 * @return
	 */
	public int indexOf(T object) {
		return next.findLink(0, object);
	}

	/**
	 * Returns the current size of the chain. 
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns the index of the current link. 
	 * 
	 * @return
	 */
	public int currentIndex() {
		return indexOf(current.content);
	}

	/**
	 * Returns the object of the current link. 
	 * 
	 * @return
	 */
	public T current() {
		return current.content;
	}

	/**
	 * Returns "true" if there is a next link. 
	 * 
	 * @return
	 */
	public boolean hasNext() {
		if (current.next == this) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the current link to the next link. 
	 * 
	 * @return
	 */
	public T next() {
		current = current.next;
		if (current == this) {
			current = next;
		}
		return current.content;
	}

	/**
	 * Removes the current link.
	 */
	public void remove() {
		if (current == this) {
			return;
		}
		remove(current);
	}
	
	public Object[] getArray() {
		Object[] returnArray = new Object[size()];
		for (int index = 0; index < size(); index++) {
			returnArray[index] = get(index);
		}
		return returnArray;
	}
	
	private T remove(Link<T> oldLink) {
		if (current == oldLink) {
			current = current.previous;
		}
		oldLink.next.previous = oldLink.previous;
		oldLink.previous.next = oldLink.next;
		size--;
		return oldLink.content;
	}
	
	@Override
	protected Link<T> getLink(T object) {
		return null;
	}
	
	@Override
	protected int findLink(int counter, T object) {
		return -1;
	}
}

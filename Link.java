package server;

class Link<T> {
	protected T content;
	
	protected Link<T> next;
	protected Link<T> previous;
	
	protected Link<T> getLink(int counter, int index) {
		if (counter == index) {
			return this;
		}
		return next.getLink(counter + 1, index);
	}
	
	protected Link<T> getLink(T object) {
		if (this.content == object) {
			return this;
		}
		return next.getLink(object);
	}
	
	protected int findLink(int counter, T object) {
		if (this.content == object) {
			return counter;
		}
		return next.findLink(counter + 1, object);
	}
}

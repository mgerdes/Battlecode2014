package MicromaniaIsBad;

public class Queue {
	int tail, head, maxLength;
	int q[];
	
	Queue(int maxLength) {
		tail = 0;
		head = 0;
		this.maxLength = maxLength;
		q = new int[maxLength];
	}
	
	public void enqueue(int x) {
		q[tail] = x;
		tail++;
		if (tail == maxLength) {
			tail = 0;
		}
	}
	
	public int dequeue() {
		int x = q[head];
		head++;
		if (head == maxLength) {
			head = 0;
		}
		return x;
	}
	
	public boolean isEmpty() {
		return tail == head;
	}
}

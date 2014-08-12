package MicromaniaIsBad;

import java.util.*;

public class ArrayList {
	
	int maxSize, head;
	int a[];
	
	ArrayList(int maxSize) {
		this.maxSize = maxSize;
		this.head = 0;
		this.a = new int[maxSize];
	}

	public void add(int x) {
		a[head++] = x;
	}
	
	public void remove(int x) {
		for (int i = 0; i < head; i++) {
			if (a[i] == x) {
				if (i < head - 1) {
					pushback(i + 1);
				}
				head--;
			}
		}
	}

	public boolean contains(int x) {
		for (int i = 0; i < head; i++) {
			if (a[i] == x) return true;
		}
		return false;
	}

	public void clear() {
		head = 0;
	}
	
	public void pushback(int index) {
		for (int i = index; i < head; i++) {
			a[i - 1] = a[i];
		}
	}
	
	@Override
	public String toString() {
		String s = "[";
		for (int i = 0; i < head; i++) {
			s += a[i];
			if (i < head - 1) {
				s += ", ";
			}
		}
		s += "]";
		return s;
	}

	
}

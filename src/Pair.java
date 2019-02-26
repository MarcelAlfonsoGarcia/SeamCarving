public class Pair {
	int energy;
	int next;

	public Pair(int energy, int next) {
		this.energy = energy;
		this.next = next;
	}
	
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public int getNext() {
		return next;
	}

	public int getEnergy() {
		return energy;
	}
}

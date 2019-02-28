import java.awt.Color;

public class Trio {
	int energy;
	int next;
	Color color;
	
	public Trio(Color color, int energy, int next) {
		this.energy = energy;
		this.next = next;
		this.color = color;
	}
	
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void setNext(int next) {
		this.next = next;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public int getNext() {
		return next;
	}

	public int getEnergy() {
		return energy;
	}
	
	public Color getColor() {
		return color;
	}
}

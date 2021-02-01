package main.java.io.github.General;

/** Position d'une case dans un plan. */
public class Position {
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int recevoirX() {return x;}
	public int recevoirY() {return y;}
	
	/** Renvoie la somme des deux positions. */
	public Position plus(Position p) {
		return new Position(x+p.recevoirX(), y+p.recevoirY());
	}
	
	/** Renvoie la position suivante selon la direction donnée. */
	public Position plus(Direction d) {
		return plus(d.versPosition());
	}
	
	/** Renvoie la position à laquelle on a retranché p. */
	public Position moins(Position p) {
		return new Position(x-p.recevoirX(), y-p.recevoirY());
	}
	
	/** Renvoie vrai si les deux positions sont égales. */
	public boolean egale(Position p) {
		return x == p.recevoirX() && y == p.recevoirY();
	}
	
	@Override
	public boolean equals(Object o) {
	    return o.getClass() == Position.class && egale((Position) o);
	}
	
	public String toString() {
		return "("+x+","+y+")";
	}
	
	private final int x, y;
}


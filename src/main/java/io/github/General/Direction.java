package main.java.io.github.General;

/** Direction horizontale ou verticale orientée. */
public enum Direction {
	Haut(new Position(0,-1)),
	Bas(new Position(0,1)),
	Gauche(new Position(-1,0)),
	Droite(new Position(1,0));
	
	private Direction(Position position) {
		this.position = position;
	}
	
	public Direction oppose() {
		switch(this) {
		case Haut: return Bas;
		case Bas:  return Haut;
		case Gauche: return Droite;
		case Droite: return Gauche;
		default:   return null;
		}
	}

	public Position versPosition() {
		return new Position(position.recevoirX(), position.recevoirY());
	}
	
	public static Direction directionDe(Position position) {
		for(Direction d : Direction.values())
			if(d.versPosition().egale(position))
				return d;
		throw new IllegalArgumentException("Position donnée : "+position.recevoirX()+","+position.recevoirY());
	}
	
	private final Position position;
}



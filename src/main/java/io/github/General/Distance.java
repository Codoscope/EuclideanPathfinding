package main.java.io.github.General;

/** Classe gérant les distances relatives aux déplacements horizontaux et verticaux et supportant
 *  l'existence de la distance infinie (éventuellement un obstacle).
 */
public class Distance {
	private Distance() {
		d = -1;
	}
	
	/** Construit une distance à partir d'un entier positif.
	 *  S'il est négatif, déclenche une exception de type IllegalArgumentException.
	 */
	public Distance(long d) {
		if(d<0) throw new IllegalArgumentException("Une distance ne peut pas être négative");
		this.d = d;
	}
	
	/** Teste si la distance est plus petite qu'une autre au sens large. */
	public boolean estPlusPetitQue(Distance distance) {
		return distance == INFINI || d >= 0 && d <= distance.d;
	}
	
	/** Teste si la distance n'est pas plus grande qu'une constante fois une autre distance,
	 *  sachant que la constante est plus grande mais proche de racine de 2 (à 5*10^-6 près).
	 */
	public boolean seMajorePar(Distance distance) {
		if(distance == INFINI)
			return true;
		else if(this == INFINI)
			return false;
		else
			return 408*d < 577*distance.d;
		// 577/408 est une approximation de la racine de 2
		// inégalité stricte car 577/408 excède la racine de 2
	}
	
	/** Si l'argument n'est pas l'infini, renvoie vrai si et seulement si la distance correspondant
	 * à l'objet est égale à celle fournie en argument moins 1.
	 */
	public boolean estLeDecrementeDe(Distance distance) {
		if(distance == INFINI) throw new IllegalArgumentException("On ne fait pas de décrément de l'infini");
		return d >= 0 && d == distance.d - 1;
	}
	
	/** Si la distance n'est pas infinie, renvoie la valeur du type primitif long correspondante,
	 *  sinon, déclenche une exception de type ClassCastException.
	 */
	public long valeur() {
		if(d<0) throw new ClassCastException("L'infini ne peut être converti en long");
		return d;
	}
	
	/** Renvoie une distance augmentée de 1. */
	public Distance increment() {
		if(this == INFINI)
			return INFINI;
		else
			return new Distance(d+1);
	}
	
	/** Renvoie la somme des deux distances. */
	public Distance plus(Distance distance) {
		if(this == INFINI || distance == INFINI)
			return INFINI;
		else
			return new Distance(d+distance.d);
	}
	
	public boolean equals(Object o) {
	    if(o.getClass() != Distance.class) {
	        return false;
	    }
	    Distance dist = (Distance) o;

	    if(this == INFINI ^ dist == INFINI) {
	        return false;
	    }

	    return dist.d == d;
	}

	@Override
	public String toString() {
	    return "" + d;
	}

	public static final Distance INFINI = new Distance();
	private final long d;
}
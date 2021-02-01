package main.java.io.github.General;

/** Tableau où chaque case contient une référence vers la suivante et la précédente. */
public class DoubleMaillon<T> {
	/** Crée un double-maillon de contenu nul. */
	public DoubleMaillon() {
		contenu = null;
	}
	
	/** Crée un double-maillon dont le contenu est la valeur fournie en argument. */
	public DoubleMaillon(T contenu) {
		this.contenu = contenu;
	}
	
	/** Crée un double-maillon correspondant au tableau donné.
	 *  @param t : Tableau de taille strictement positive. */
	public DoubleMaillon(T[] t) {
		this(t, 0, null);
	}
	
	/** Crée un double-maillon correspondant au tableau donné commençant à l'indice i.
	 *  @param i : endroit où le tableau commence (le reste est ignoré).
	 *  @param precedent : le double-maillon qui précède celui que l'on crée. */
	private DoubleMaillon(T[] t, int i, DoubleMaillon<T> precedent) {
		// System.out.println("i = "+i);
		contenu = t[i];
		this.precedent = precedent;
		
		if(i+1 < t.length)
			suivant = new DoubleMaillon<T>(t, i+1, this);
	}
	
	/** Renvoie la valeur correspondant au maillon. */
	public T valeur() {
		return contenu;
	}
	
	/** Modifie la valeur correspondant au maillon. */
	public void valeur(T t) {
		contenu = t;
	}
	
	/** Accroche à ce maillon un maillon suivant. Si le maillon est nul, le remplace. 
	 *  Renvoie le maillon obtenu. Met automatiquement ce maillon comme étant le
	 *  précédent du suivant. */
	public DoubleMaillon<T> suivant(DoubleMaillon<T> suivant) {
		if(suivant == null) {
			return this.suivant = null;
		} else if(contenu != null) {
			this.suivant = suivant;
			suivant.precedent = this;
			return suivant;
		} else {
			contenu = suivant.valeur();
			return this;
		}
	}
	
	/** Renvoie le maillon suivant. */
	public DoubleMaillon<T> suivant() {
		return suivant;
	}
	
	/** Renvoie le maillon précédent. */
	public DoubleMaillon<T> precedent() {
		return precedent;
	}
	
	/** Renvoie la longueur du double-maillon. Tourne en boucle s'il est infini. */
	public int taille() {
		if(suivant != null)
			return 1+suivant.taille();
		else
			return 1;
	}
	
	/** Renvoie le tableau correspondant.
	 *  @param t : Tableau de la bonne longueur destiné à contenir le résultat. */
	public T[] toArray(T[] t) {
		DoubleMaillon<T> actuel = this;
		
		for(int i=0 ; i<t.length ; i++) {
			t[i] = actuel.contenu;
			actuel = actuel.suivant();
		}
		
		return t;
	}
	
	public String toString() {
		String resultat = contenu.toString();
		if(suivant != null) resultat += ", "+suivant.toString();
		return resultat;
	}
	
	private T contenu; // La valeur donnée par le maillon lui-même.
	private DoubleMaillon<T> precedent, suivant; // Maillons suivant et précédent dans la "liste".
}
package main.java.io.github.General;


/** Tableau où chaque case contient une référence vers la suivante. */
public class Maillon<T> {
	public Maillon() {
		tete = null;
		queue = null;
	}
	
	public Maillon(T tete) {
		this.tete = tete;
		queue = null;
	}
	
	public Maillon(T tete, Maillon<T> c) {
		this.tete = tete;
		queue = c;
	}
	
	/** Renvoie le premier élément. */
	public T tete() {
		return tete;
	}
	
	/** Renvoie le deuxième maillon (qui s'accompagne donc du reste de la liste). */
	public Maillon<T> queue() {
		return queue;
	}
	
	/** Change de maillon suivant (et donc de tous les autres maillons qui suivent).
	 *  Si la tête est nulle, le maillon devient celui indiqué. */
	public void modifierQueue(Maillon<T> c) {
		if(tete != null)
			queue = c;
		else {
			tete = c.tete();
			queue = c.queue();
		}
	}
	
	private T tete;
	private Maillon<T> queue;
}

package main.java.io.github.General;

/** Tableau où on peut retirer le premier élément et ajouter un élément à la fin. */
public class Chaine<T> {
	public Chaine() {
		taille = 0;
	}
	
	public Chaine(T t) {
		this();
		ajouter(t);
	}
	
	/** Renvoie le premier élément de la chaîne (null s'il n'y en a pas). */
	public T tete() {
		if(maillon != null)
			return maillon.tete();
		else
			return null;
	}
	
	/** Retire le premier élément de la chaîne. */
	public void decrocherTete() {
		if(maillon != null) {
			taille--;
			maillon = maillon.queue();
			if(maillon == null) fin = null;
		}
	}
	
	/** Ajoute un élément à la fin de la chaîne. */
	public void ajouter(T t) {
		if(fin != null) {
			fin.modifierQueue(new Maillon<T>(t));
			fin = fin.queue();
		} else {
			maillon = new Maillon<T>(t);
			fin = maillon;
		}
		taille++;
	}
	
	/** Renvoie le maillon qui correspond à la chaîne. */
	public Maillon<T> maillon() {
		return maillon;
	}
	
	/** Renvoie la liste qui correspond à la chaîne. */
	public T[] liste() {
		@SuppressWarnings("unchecked")
		T[] liste = (T[]) new Object[taille];
		Maillon<T> m = maillon;
		for(int i=0 ; i<taille ; i++) {
			liste[i] = m.tete();
			m = m.queue();
		}
		return liste;
	}
	
	private Maillon<T> maillon, fin;
	private int taille;
}
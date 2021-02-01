package main.java.io.github.General;

/** Correspond à une liste que l'on parcourt dans un sens ou dans un autre.
 *  Il s'agit de l'ensemble de cases qui relie deux noeuds.
 *  À la construction, on commence au début (ou à la fin). */
public class Branche<T> {
	public Branche(DoubleMaillon<T> doubleMaillon, Sens sens) {
		debut = actuel = doubleMaillon;
		this.sens = sens;
	}
	
	/** Avance d'un cran dans la branche et renvoie la case qui est enlevée. */
	public T suivant() {
		if(actuel == null) return null;
		
		T retour = actuel.valeur();
		
		if(sens == Sens.DIRECT)
			actuel = actuel.suivant();
		else
			actuel = actuel.precedent();
		
		return retour;
	}
	
	/** Remet le mécanisme de parcours à 0 pour pouvoir à nouveau parcourir la branche. */
	public void reinitialiser() {
		actuel = debut;
	}
	
	private DoubleMaillon<T> debut, actuel;
	private Sens sens;
}
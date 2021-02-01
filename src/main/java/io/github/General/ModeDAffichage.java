package main.java.io.github.General;


/** Mode d'affichage de la carte pour le panneau. */
public enum ModeDAffichage {
	NORMAL("Normal"),		// Affichage de la carte dans ses plus fins détails.
	GRILLE("Grid");		// Affichage de cette même carte mais avec la grille qui permet la pixelisation.
	//PIXELISE("Pixelisé");	// Affichage de la carte pixelisée. TODO
	
	private ModeDAffichage(String nom) {
		this.nom = nom;
	}
	
	public String texteAssocie() {
		return nom;
	}
	
	private final String nom;
}

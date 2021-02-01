package main.java.io.github.General;
import java.util.ArrayList;


/** Tableau à double entrée de type T. */
public class Plan<T> {
	public Plan(Object[][] plan, T valeurParDefaut) {
		this.plan = plan;
		yTaille = plan.length;
		xTaille = plan[0].length;
		coinSuperieurGauche = new Position(0,0);
		coinInferieurDroit = new Position(xTaille, yTaille);
		this.valeurParDefaut = valeurParDefaut;
	}
	
	public Plan(int yTaille, int xTaille, T valeurParDefaut) {
		plan = new Object[yTaille][xTaille];
		for(int i=0 ; i<yTaille ; i++)
			for(int j=0 ; j<xTaille ; j++)
				plan[i][j] = valeurParDefaut;
		this.yTaille = yTaille;
		this.xTaille = xTaille;
		coinSuperieurGauche = new Position(0,0);
		coinInferieurDroit = new Position(xTaille, yTaille);
		this.valeurParDefaut = valeurParDefaut;
	}
	
	public void modifierValeurParDefaut(T valeurParDefaut) {
		this.valeurParDefaut = valeurParDefaut;
	}
	
	/** Construit un plan où seule une zone rectangulaire définie par deux coins prend des valeurs différentes
	 *  de celle par défaut. Seule cette zone interne est réellement stockée.
	 *  @param yTaille : Largeur virtuelle.
	 *  @param xTaille : Hauteur virtuelle.
	 *  @param valeurParDefaut : Valeur donnée pour les cases non remplies.
	 *  @param coinSuperieurGauche : coin supérieur gauche de la zone interne.
	 *  @param coinInferieurDroit : coin inférieur droit dela zone interne.
	 */
	public Plan(int yTaille, int xTaille, T valeurParDefaut, Position coinSuperieurGauche, Position coinInferieurDroit) {
		this(yTaille - coinSuperieurGauche.recevoirY() + coinInferieurDroit.recevoirY(),
			 xTaille - coinInferieurDroit.recevoirX() + coinSuperieurGauche.recevoirX(),
			 valeurParDefaut
		);
		this.coinSuperieurGauche = coinSuperieurGauche;
		this.coinInferieurDroit = coinInferieurDroit;
		this.valeurParDefaut = valeurParDefaut;
	}
	
	public T recevoirElement(int y, int x) {
		return recevoirElement(new Position(x, y));
	}
	
	@SuppressWarnings("unchecked")
	public T recevoirElement(Position position) {
		if(coinSuperieurGauche.recevoirX() <= position.recevoirX()
				&& position.recevoirX() < coinInferieurDroit.recevoirX()
				&& coinSuperieurGauche.recevoirY() <= position.recevoirY()
				&& position.recevoirY() < coinInferieurDroit.recevoirY()) {

			T element = (T) plan[position.recevoirY()+coinSuperieurGauche.recevoirY()]
					[position.recevoirX()+coinSuperieurGauche.recevoirX()];
                        return element != null ? element : valeurParDefaut;
		} else {
			return valeurParDefaut;
		}
	}
	
	public void modifierElement(int y, int x, T e) {
		modifierElement(new Position(x, y), e);
	}
	
	public void modifierElement(Position position, T e) {
		plan[position.recevoirY()+coinSuperieurGauche.recevoirX()]//FIXME
			[position.recevoirX()+coinSuperieurGauche.recevoirY()] = e;
	}
	
	public boolean contient(Position p) {
		boolean retour = 0 <= p.recevoirX() && p.recevoirX() < xTaille
				&& 0 <= p.recevoirY() && p.recevoirY() < yTaille;
		return retour ;
	}
	
	/** Donne les voisins d'une case (haut, bas, gauche, droite) appartenant à la carte. */
	public Position[] voisinsDe(Position position) {
		ArrayList<Position> resultat = new ArrayList<Position>();
		
		Position[] voisinsPotentiels = {
			new Position(-1, 0),
			new Position(0, 1),
			new Position(1, 0),
			new Position(0, -1),
			new Position(0, 0)
		};
		
		for(final Position v:voisinsPotentiels) {
			Position positionAAjouter = position.plus(v) ;
			if(contient(positionAAjouter)) {
				resultat.add(positionAAjouter);
			}
		}
		
		return resultat.toArray(new Position[resultat.size()]);
	}
	
	public Plan<T> copier() {
		/*System.out.println("("+coinSuperieurGauche.recevoirX()+","+coinSuperieurGauche.recevoirX()
						   +"), ("+coinInferieurDroit.recevoirX()+","+coinInferieurDroit.recevoirY()+")");
		System.out.println(""+xTaille+","+yTaille);*/
		Object[][] nouveauPlan = new Object[yTaille][xTaille];
		for(int y=0 ; y<yTaille ; y++)
			for(int x=0 ; x<xTaille ; x++)
				nouveauPlan[y][x] = recevoirElement(new Position(x, y));
		
		return new Plan<T>(nouveauPlan, valeurParDefaut);
	}
	
	@Override
	public boolean equals(Object o) {
	    if(o.getClass() != Plan.class) {
	        return false;
	    }

	    Plan rawPlan = (Plan) o;

	    if(!(rawPlan.recevoirHauteur() == rawPlan.recevoirHauteur()
	            && rawPlan.recevoirLargeur() == recevoirLargeur())) {
	        return false;
	    }

	    for(int y = 0; y < recevoirHauteur(); ++y) {
	        for(int x = 0; x < recevoirLargeur(); ++x) {
	            if(!recevoirElement(y, x).equals(rawPlan.recevoirElement(y, x))) {
	                return false;
	            }
	        }
	    }

	    return true;
	}

	public int recevoirLargeur() {return xTaille;}
	public int recevoirHauteur() {return yTaille;}
	
	private Object[][] plan;
	private int yTaille, xTaille;
	
	private Position coinSuperieurGauche, coinInferieurDroit;
	private T valeurParDefaut;
}
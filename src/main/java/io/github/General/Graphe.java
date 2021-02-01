package main.java.io.github.General;
import java.util.ArrayList;


class Graphe {
	@SuppressWarnings("unchecked") // Pour la conversion en Maillon<Noeud>[].
	public Graphe(Plan<Distance> zone, Position depart, Position arrivee) {
		this.zone = zone;
		this.depart = depart;
		this.arrivee = arrivee;
		
		long tempsDepart = System.currentTimeMillis();
		
		// Parcours de la zone "topologique" à partir du départ pour créer les noeuds :
		{
			ArrayList<Noeud> noeuds = new ArrayList<Noeud>();
			Noeud noeud = new Noeud(depart);
			noeud.indice(0);
			noeuds.add(noeud);
			parcourir(noeuds, noeud);
			this.noeuds = noeuds.toArray(new Noeud[noeuds.size()]);
		}
		
		tempsCalculGraphe = System.currentTimeMillis() - tempsDepart;
		
		// Calcul des parcours possibles :
		boolean[] passage = new boolean[noeuds.length]; // Détermine les noeuds où l'on est passé.
		for(int i=0 ; i<passage.length ; i++) passage[i] = false;
		ArrayList<Maillon<Direction>> parcoursPossibles = parcoursPossibles(passage, noeuds[0]);
		this.parcoursPossibles = (Maillon<Direction>[]) parcoursPossibles.toArray(new Maillon[parcoursPossibles.size()]);
		
		tempsCalculParcours = System.currentTimeMillis() - tempsDepart - tempsCalculGraphe;
	}
	
	public long tempsCalculGraphe() {
		return tempsCalculGraphe;
	}
	
	public long tempsCalculParcours() {
		return tempsCalculParcours;
	}
	
	/** Renvoie le nombre de possibilités de parcours à travers le graphe. */
	public int nombreDeParcours() {
		return parcoursPossibles.length;
	}
	
	/** Initialise un parcours possible particulier.
	 * @param numeroDuParcours : numéro du parcours compris entre 0 et nombreDeParcours()-1. */
	public void chargerParcours(int numeroDuParcours) {
		// Le maillon indique la suite de directions à prendre sur chaque noeud :
		maillonDuParcoursActuel = parcoursPossibles[numeroDuParcours];
		// Initialement, il n'y a aucun parcours à effectuer pour arriver sur un noeud :
		brancheDuParcoursActuel = new Branche<Position>(null, Sens.DIRECT);
		// Le premier noeud est le départ.
		prochainNoeudDuParcoursActuel = noeuds[0];
	}
	
	/** Donne la position suivante du parcours initialisé avec chargerParcours(...). */
	public Position positionSuivante() {
		if(prochainNoeudDuParcoursActuel == null) {
			// Il n'y a plus rien à parcourir : l'arrivée a déjà été atteinte.
			return null;
			
		} else {
			Position position = brancheDuParcoursActuel.suivant();
			
			if(position == null) {
				// Nous sommes sur un noeud. La position qu'il faut renvoyer est :
				position = prochainNoeudDuParcoursActuel.position();
				
				if(maillonDuParcoursActuel != null) { // S'il y a une direction suivante :
					// La direction à prendre est :
					Direction direction = maillonDuParcoursActuel.tete();
					// Passage à la direction d'après pour la prochaine fois :
					maillonDuParcoursActuel = maillonDuParcoursActuel.queue();
					// Initialisation de la branche qu'il va falloir suivre jusqu'au prochain noeud :
					brancheDuParcoursActuel = prochainNoeudDuParcoursActuel.brancheSelon(direction);
					brancheDuParcoursActuel.reinitialiser();
					// Passage au noeud suivant pour quand la branche sera terminée :
					prochainNoeudDuParcoursActuel = prochainNoeudDuParcoursActuel.voisinSelon(direction);
				} else {
					// S'il n'y a pas de direction suivante :
					prochainNoeudDuParcoursActuel = null;
				}
			}
			return position;
		}
	}
	
	/**
	 * Calcule les parcours possibles à partir du noeud donné.
	 * @param passage : Liste qui indique si l'on est déjà passé par le noeud (true) ou non (false).
	 * @param noeud : Noeud à partir duquel on commence le parcours.
	 * @return Liste des possibilités une chacune d'entre elles est représentée par un maillon.
	 */
	private ArrayList<Maillon<Direction>> parcoursPossibles(boolean[] passage, Noeud noeud) {
		Noeud noeudArrivee = new Noeud(arrivee);
		ArrayList<Maillon<Direction>> retour = new ArrayList<Maillon<Direction>>();
		
		// Si nous sommes sur l'arrivée, c'est la dernière et unique étape possible.
		// Il n'y a pas de direction supplémentaire à prendre.
		if(noeud.equals(noeudArrivee)) {
			retour.add(null);
			return retour;
		}
		
		// Sinon, on commence par indiquer que nous sommes passés sur ce noeud :
		passage[noeud.indice()] = true;
		
		for(Direction direction : noeud.embranchements()) {
			Noeud voisin = noeud.voisinSelon(direction);
			if(!passage[voisin.indice()]) {
				// Pour chaque direction qui n'amène pas sur un voisin par lequel
				// on est déjà passé, on tente une suite du parcours à partir de
				// ce voisin :
				ArrayList<Maillon<Direction>> suiteDuParcours = parcoursPossibles(passage, voisin);
				for(int i=0 ; i<suiteDuParcours.size() ; i++){
					// On ajoute alors à gauche de chacun des parcours possibles à partir
					// du voisin la direction courante "direction" :
					Maillon<Direction> maillonAAjouter = new Maillon<Direction>(direction);
					maillonAAjouter.modifierQueue(suiteDuParcours.get(i));
					retour.add(maillonAAjouter);
				}
			}
		}
		
		passage[noeud.indice()] = false;
		return retour;
	}
	
	/** Méthode récursive pour créer les noeuds.
	 * @param noeuds : les noeuds qui ont déjà été créés.
	 * @param noeud : le noeud actuel dont on demande de poursuivre le parcours.
	 */
	private void parcourir(ArrayList<Noeud> noeuds, Noeud noeud) {
		// System.out.println(">> Parcours "+noeud.toString());
		if(noeud.equals(new Noeud(arrivee))) {
			// System.out.println("<< Fin parcours "+noeud.toString());
			return; // Il n'y a rien à poursuivre à partir de l'arrivée.
		}
		// Pour chaque direction possible :
		for(Direction directionDepart : noeud.directions()) {
			// Si la direction est en fait déjà explorée, on essaye la suivante :
			if(noeud.voisinSelon(directionDepart) != null) continue;
			// System.out.print(directionDepart + " ");
			Direction direction = directionDepart; // Direction actuelle.
			Position position = noeud.position(); // Position actuelle.
			// Trajet que l'on va effectuer :
			DoubleMaillon<Position> debut = new DoubleMaillon<Position>(), fin = debut;
			
			// Tant qu'il n'y a pas de noeud.
			chercheNoeud: while(true) {
				// On ajoute à la branche la position suivante :
				fin = fin.suivant(new DoubleMaillon<Position>(position = position.plus(direction)));
				// System.out.print(position.toString() + " ");
				// On sort si la position suivante est l'arrivée ou le départ (considérés comme des noeuds) :
				if(position.egale(arrivee) || position.egale(depart)) break;
				Direction directionSuivante = null; // Direction suivante.
				
				// Pour chaque direction autour de la position courante,
				for(Direction directionPossible : Direction.values()) {
					Position essaiDePosition = position.plus(directionPossible);
					
					// Si celle-là donne une case possible et différente de la précédente,
					if(directionPossible != direction.oppose()
							&& zone.recevoirElement(essaiDePosition) != Distance.INFINI)
						if(directionSuivante == null)
							// Si aucune case possible n'avait encore été trouvée,
							// directionSuivante forme une possibilité.
							directionSuivante = directionPossible;
						else
							// Sinon, la position courante est sur un noeud et on sort du while :
							break chercheNoeud;
				}
				direction = directionSuivante; // On change de direction.
				// On considère une impasse (parfois l'arrivée) comme étant un noeud.
				if(direction == null) break;
			}
			
			Noeud noeudDArrivee = new Noeud(fin.valeur());// Le noeud auquel on aboutit.
			int indice = noeuds.indexOf(noeudDArrivee);
			if(indice == -1) {			   // Si le noeud n'avait pas encore été trouvé,
				noeuds.add(noeudDArrivee); // on l'ajoute à la liste.
				noeudDArrivee.indice(noeuds.size() - 1);
				// On indique au noeud où il se trouve dans la liste des noeuds.
			} else {
				noeudDArrivee = noeuds.get(indice);
				// Sinon, on reprend l'ancien noeud qui existe déjà.
			}
			
			// On ne met pas le noeud d'arrivée dans le morceau de parcours :
			if(fin.precedent() == null)
				fin.valeur(null);
			else
				(fin = fin.precedent()).suivant(null);
			
			// System.out.println(" " + directionDepart + " " + direction.oppose());
			
			// Les noeuds de début et de fin sont mis en tant que voisins :
			noeud.ajouterVoisin(directionDepart, noeudDArrivee, new Branche<Position>(debut, Sens.DIRECT));
			noeudDArrivee.ajouterVoisin(direction.oppose(), noeud, new Branche<Position>(fin, Sens.INDIRECT));
			
			// On poursuit le parcours à partir du noeud que l'on vient d'atteindre.
			parcourir(noeuds, noeudDArrivee);
		}
		// System.out.println("<< Fin parcours "+noeud.toString());
		return;
	}
	
	private final Plan<Distance> zone; // Zone "topologique" dans laquelle on se déplace.
	private final Position depart, arrivee;
	private final Noeud[] noeuds; // Ensemble des noeuds qui constituent le graphe. Commence par le départ.
	private final Maillon<Direction>[] parcoursPossibles; // Parcours possibles du graphe.
	private final long tempsCalculGraphe, tempsCalculParcours;
	
	// Champs utilisés pour le parcours d'un trajet précis :
	private Maillon<Direction> maillonDuParcoursActuel;
	private Branche<Position> brancheDuParcoursActuel;
	private Noeud prochainNoeudDuParcoursActuel;
	
	/** Point d'embranchement d'un graphe qui se relie dans les directions verticales
	 *  et horizontales à d'autres noeuds. Ce sont en fait les points d'intersection
	 *  de la zone "topologique" calculée par la classe Itineraire. */
	private class Noeud {
		@SuppressWarnings("unchecked") // Pour new Branche[...].
		/** @param position : position du noeud dans le plan. */
		public Noeud(Position position) {
			this.position = position;
			voisins = new Noeud[Direction.values().length];
			branches = new Branche[Direction.values().length];
			int nombreDeDirectionsPossibles = 0;
			Chaine<Direction> directionsPossibles = new Chaine<Direction>();
			
			// Pour chaque direction, si la case correspondante est dans la zone "topologique", on l'ajoute.
			for(Direction direction : Direction.values())
				if(zone.recevoirElement(position.plus(direction)) != Distance.INFINI) {
					directionsPossibles.ajouter(direction);
					nombreDeDirectionsPossibles++;
				}
			// Conversion de la chaîne en une liste :
			Maillon<Direction> maillonDirectionsPossibles = directionsPossibles.maillon();
			this.directionsPossibles = new Direction[nombreDeDirectionsPossibles];
			for(int i=0 ; i<nombreDeDirectionsPossibles ; i++) {
				this.directionsPossibles[i] = maillonDirectionsPossibles.tete();
				maillonDirectionsPossibles = maillonDirectionsPossibles.queue();
			}
		}
		
		public void indice(int i) {
			this.indice = i;
		}
		
		public int indice() {
			return indice;
		}
		
		/** Renvoie la position du noeud dans le plan. */
		public Position position() {
			return position;
		}
		
		/** Renvoie les directions de parcours possibles (absence d'obstacle dans la zone "topologique"). */
		public Direction[] directions() {
			return directionsPossibles;	
		}
		
		/** Ajoute un accès vers un noeud.
		 * @param direction : direction à prendre pour accéder au noeud voisin.
		 * @param voisin : le noeud voisin que l'on ajoute.
		 * @param branche : la branche pour accéder au voisin, noeuds exclus.
		 */
		public void ajouterVoisin(Direction direction, Noeud voisin, Branche<Position> branche) {
			if(voisins[direction.ordinal()] == null) nombreDeVoisins++;
			voisins[direction.ordinal()] = voisin;
			branches[direction.ordinal()] = branche;
		}
		
		/** Renvoie les directions possibles à partir du noeud courant.
		 *  On remarque que les directions sont dans l'ordre de l'énumération. */
		public Direction[] embranchements() {
			Direction[] embranchements = new Direction[nombreDeVoisins];
			for(int i=0, k=0 ; i<voisins.length ; i++)
				if(voisins[i] != null)
					embranchements[k++] = Direction.values()[i];
			return embranchements;
		}
		
		/** Renvoie les voisins du noeud. */
		public Noeud[] voisins() {
			Noeud[] voisins = new Noeud[nombreDeVoisins];
			// System.out.println(voisins.length);
			for(int i=0, k=0 ; i<this.voisins.length ; i++)
				if(this.voisins[i] != null)
					voisins[k++] = this.voisins[i];
			return voisins;
		}
		
		/** Renvoie le voisin auquel on accède par la direction donnée, null s'il n'y en a pas (encore). */
		public Noeud voisinSelon(Direction direction) {
			return voisins[direction.ordinal()];
		}
		
		/** Renvoie vrai si les deux noeuds donnent la même position. */
		public boolean equals(Object noeud) {
			return position.egale(((Noeud)noeud).position());
		}
		
		
		public String toString() {
			Noeud[] voisinsloc = voisins();
			String infos = "Noeud n°"+indice+" ";
			if(voisins != null) {
				infos += "de voisins "+nombreDeVoisins+" (";
				// System.out.println(indice + ", " + position + ", " + nombreDeVoisins);
				for(Noeud voisin : voisinsloc) {
					//try {
						infos += " "+voisin.position();
					//} catch(java.lang.NullPointerException e) {
					//	System.out.println("");
					//}
				}
			}
			infos += " ) et de position "+position;
			return infos;
		}
		
		/** Renvoie la branche qui découle de la direction fournie que l'on prend à partir du noeud. */
		public Branche<Position> brancheSelon(Direction direction) {
			return branches[direction.ordinal()];
		}
		
		private final Noeud[] voisins; // Les index correspondent à des directions.
		private final Branche<Position>[] branches; // Les branches pour accéder à chacun des voisins.
		private short nombreDeVoisins = 0; // Nombre de voisins stockés.
		private final Position position; // Position du noeud dans la carte.
		private final Direction[] directionsPossibles; // Directions de déplacement possibles en suivant la zone "topologique".
		private int indice;
	}	
}
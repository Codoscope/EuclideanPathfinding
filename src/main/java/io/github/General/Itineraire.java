package main.java.io.github.General;
import java.util.ArrayList;

/** Classe pour calculer un itinéraire. */
public class Itineraire {
	
	/** @param carte : carte des obstacles. */
	public Itineraire(Plan<Boolean> carte, Position depart, Position arrivee) {
		this.carte = carte;
		this.depart = depart;
		this.arrivee = arrivee;
	}
	
	/** Donne les distances au départ relativement au trajet vers l'arrivée. */
	private Plan<Distance> calculerDistancesNorme1(Position depart, Position arrivee, boolean optionElargitZone) {
		//System.out.println("==> calculerDistancesNorme1(...)");
		Plan<Distance> planDesDistances = new Plan<Distance>(carte.recevoirHauteur(), carte.recevoirLargeur(), Distance.INFINI);
		//System.out.println("Dimensions planDesDistances : (x,y) =("+planDesDistances.recevoirHauteur()+","+planDesDistances.recevoirLargeur()+")");
		planDesDistances.modifierElement(depart.recevoirY(), depart.recevoirX(), new Distance(0));
		Chaine<Position> aTraiter = new Chaine<Position>(depart);
		
		// Tant que l'arrivée n'est pas atteinte, nous allons passer à l'instant t suivant pour explorer.
		do {
			// Remplir les cases accessibles à partir des précédentes.
			
			Position tete = aTraiter.tete();
			Position[] voisins = planDesDistances.voisinsDe(tete);
			
			for(final Position p : voisins) {
				Distance d = planDesDistances.recevoirElement(p);
				// Si la case n'est pas déjà traitée et qu'elle ne correspond pas à un obstacle.
				if(d == Distance.INFINI && !carte.recevoirElement(p)) {
					aTraiter.ajouter(p);
					planDesDistances.modifierElement(p, planDesDistances.recevoirElement(tete).increment());
				}
			}
			
			aTraiter.decrocherTete();
			
			//System.out.println("Tour boucle norme1 ; tete = "+tete);
			
		} while(// tant qu'on peut explorer
				 aTraiter.tete() != null
				 && (
					    // et tant qu'on n'a pas atteint l'arrivée
					    planDesDistances.recevoirElement(arrivee) == Distance.INFINI
					    // ou bien qu'on n'a pas suffisamment élargi la zone si l'option correspondante est activée
					    || (optionElargitZone
							    && planDesDistances.recevoirElement(aTraiter.tete())
							       .seMajorePar(planDesDistances.recevoirElement(arrivee)))
				    )
				);
		
		
		return planDesDistances;
	}
	
	public Plan<Distance> calculerZonePourLeCalcul() {
		if(zonePourLeCalcul == null) {
			long temps = System.currentTimeMillis();
			zonePourLeCalcul = calculerDistancesNorme1(depart, arrivee, false);
			tempsZonePourLeCalcul = System.currentTimeMillis() - temps;
			infosModifiees();
		}
		return zonePourLeCalcul.copier();
	}
	
	/** Calcule le plus court chemin en ne permettant que les déplacements haut, bas, gauche et droit. */
	public Plan<Distance> calculerPlusCourtCheminOrthogonal() {
		if(plusCourtCheminOrthogonal == null) {
			long temps = System.currentTimeMillis();
			Plan<Distance> planDesDistances = calculerZonePourLeCalcul();
			Plan<Distance> territoire = new Plan<Distance>(planDesDistances.recevoirHauteur(),
													 planDesDistances.recevoirLargeur(),
													 Distance.INFINI);
			Distance d = planDesDistances.recevoirElement(arrivee);
			territoire.modifierElement(arrivee, d);
			Position position = arrivee;
			
			while(d.valeur() > 0) {
				for(final Position p : territoire.voisinsDe(position)) {
					Distance dSuivante = planDesDistances.recevoirElement(p);
					if(dSuivante.estLeDecrementeDe(d)) {
						d = dSuivante;
						territoire.modifierElement(p, d);
						position = p;
						break;
					}
				}
			}
			
			plusCourtCheminOrthogonal = territoire;
			tempsPlusCourtCheminOrthogonal = System.currentTimeMillis() - temps;
			infosModifiees();
		}
		
		return plusCourtCheminOrthogonal.copier();
	}
	
	
	/** Calcule la zone où se trouve le chemin le plus court pour un parcours case par case. */
	public Plan<Distance> calculerZone() {
		if(zone == null) {
			Plan<Distance> planDesDistances = calculerZonePourLeCalcul();
			long temps = System.currentTimeMillis();
			Plan<Distance> territoire = new Plan<Distance>(planDesDistances.recevoirHauteur(),
					 								 planDesDistances.recevoirLargeur(),
					 								 Distance.INFINI);
			
			//explorerZone(territoire, planDesDistances, arrivee);
			
			Chaine<Position> aTraiter = new Chaine<Position>(arrivee);
			territoire.modifierElement(arrivee, planDesDistances.recevoirElement(arrivee));
			
			do {
				Position tete = aTraiter.tete();
				Distance d = territoire.recevoirElement(tete);
				for(final Position p : territoire.voisinsDe(tete))
					// Si le voisin p n'a pas été traité et doit l'être, on ajoute sa distance au territoire
					// et on l'ajoute à la Chaine aTraiter.
					if(territoire.recevoirElement(p) == Distance.INFINI
							&& planDesDistances.recevoirElement(p).estLeDecrementeDe(d)) {
						aTraiter.ajouter(p);
						territoire.modifierElement(p, planDesDistances.recevoirElement(p));
					}
				aTraiter.decrocherTete();
			} while(aTraiter.tete() != null);
			
			zone = territoire;
			tempsZone = System.currentTimeMillis() - temps;
			infosModifiees();
		}
		
		return zone.copier();
	}
	
	/** Calcule la zone où se trouve le chemin le plus court au sens de la norme pour un parcours réel. */
	public Plan<Distance> calculerZoneElargie() {
		if(zoneElargie == null) {
			long temps = System.currentTimeMillis();
			Plan<Distance> distancesAuDepart  = calculerDistancesNorme1(depart, arrivee, true);
			Plan<Distance> distancesALArrivee = calculerDistancesNorme1(arrivee, depart, true);
			
			Plan<Distance> territoire = new Plan<Distance>(carte.recevoirHauteur(),
															carte.recevoirLargeur(),
															Distance.INFINI);
			
			//explorerZoneElargie(territoire, planDesDistancesAuDepart, planDesDistancesALArrivee, arrivee);
			
			Chaine<Position> aTraiter = new Chaine<Position>(arrivee); 
			territoire.modifierElement(arrivee, distancesAuDepart.recevoirElement(arrivee));
			Distance distanceMaximale = distancesAuDepart.recevoirElement(arrivee);
			
			do {
				Position tete = aTraiter.tete();
				
				for(final Position p : territoire.voisinsDe(tete)) {
					// Si le voisin p n'a pas été traité et peut l'être, on lui stocke sa distance et on l'ajoute à la chaîne.
					if(territoire.recevoirElement(p) == Distance.INFINI
							&& distancesAuDepart.recevoirElement(p).plus(distancesALArrivee.recevoirElement(p)).seMajorePar(distanceMaximale)) {
						territoire.modifierElement(p, distancesAuDepart.recevoirElement(p));
						aTraiter.ajouter(p);
					}
				}
				
				aTraiter.decrocherTete();
				
				//System.out.println("Tour boucle zoneElargie ; tete = "+tete);
			} while(aTraiter.tete() != null); // Tant qu'il reste des cases à traiter.
			
			zoneElargie = territoire;
			tempsZoneElargie = System.currentTimeMillis() - temps;
			infosModifiees();
		}
		
		return zoneElargie.copier();
	}

	/** Donne un équivalent topologique de la situation par gonflement des obstacles. */
	public Plan<Distance> calculerZoneTopologique() {
		//System.out.println("Zone topologique");
		if(zoneTopologique == null) {
			//System.out.println("Calcul");
			Plan<Distance> zone = calculerZoneElargie();
			long temps = System.currentTimeMillis();
			Chaine<Position> aTraiter = new Chaine<Position>();
			for(int x=-1 ; x<=zone.recevoirLargeur() ; x++)
				for(int y=-1 ; y<=zone.recevoirHauteur() ; y++){
					Position position = new Position(x, y);
					if(zone.recevoirElement(position) == Distance.INFINI) {
						for(Position p : zone.voisinsDe(position)){
							if(zone.recevoirElement(p) != Distance.INFINI) {
								// La case de position "position" n'est pas inatteignable.
								aTraiter.ajouter(position);
								break;
							}
						}
					}
				}
			
			// Tant qu'il y a à traiter pour gonfler les obstacles.
			while(aTraiter.tete() != null) {
				Position tete = aTraiter.tete();
				// Pour toutes les directions de gonflement
				for(Position p : zone.voisinsDe(tete)) {
					// S'il n'y a pas déjà un obstacle et que ce n'est ni l'arrivée, ni le départ :
					if(zone.recevoirElement(p) != Distance.INFINI
							&& !p.egale(depart) && !p.egale(arrivee)) {
						Position direction = p.moins(tete),
								 gauche = new Position(-direction.recevoirY(), direction.recevoirX()),
								 droite = new Position(direction.recevoirY(), -direction.recevoirX());
						// On considère la zone de six cases en face de "tete" selon "direction" :
						Position f2 = direction.plus(p),
								 g1 = p.plus(gauche),
								 d1 = p.plus(droite),
								 g2 = direction.plus(g1),
								 d2 = direction.plus(d1);
						/*
						 * ???? g1 g2
						 * tete p  f2
						 * ???? d1 d2
						 */
						
						boolean vf2 = zone.recevoirElement(f2) == Distance.INFINI,
								 vg1 = zone.recevoirElement(g1) == Distance.INFINI,
								 vd1 = zone.recevoirElement(d1) == Distance.INFINI,
								 vg2 = zone.recevoirElement(g2) == Distance.INFINI,
								 vd2 = zone.recevoirElement(d2) == Distance.INFINI;
						
						// Si les conditions permettent d'ajouter un obstacle
						if(vd1 && (vg1 || !vg2) || vg1 && !vd2 || !vg2 && !vf2 && !vd2
								|| vd1 && vf2 || vg1 && vf2) {
							zone.modifierElement(p, Distance.INFINI);
							aTraiter.ajouter(p);
						}
					}
				}
				aTraiter.decrocherTete();
			}
			
			zoneTopologique = zone;
			tempsZoneTopologique = System.currentTimeMillis() - temps;
			infosModifiees();
		}
		return zoneTopologique.copier();
	}
	
	/** Calcule le graphe correspondant au trajet et renvoie le nombre de parcours possibles. */
	public int calculerGraphe() {
		if(graphe == null) {
			graphe = new Graphe(calculerZoneTopologique(), depart, arrivee);
			nombreDeParcours = graphe.nombreDeParcours();
			tempsCalculGraphe = graphe.tempsCalculGraphe();
			tempsCalculParcours = graphe.tempsCalculParcours();
			
			infosModifiees();
		}
		return nombreDeParcours;
	}
	
	/** Modifie le numéro du parcours actuellement visible. */
	public void modifierNumeroParcours(int i) {
		numeroDuParcours = i;
	}
	
	/** Renvoie le parcours actuellement visible sous forme d'un tableau de positions. */
	public Position[] calculerParcours() {
		calculerGraphe();
		graphe.chargerParcours(numeroDuParcours);
		ArrayList<Position> trajet = new ArrayList<Position>();
		// On met toutes les positions une à une dans la liste à taille variable :
		for(Position position ; (position = graphe.positionSuivante()) != null ; trajet.add(position));
		
		return trajet.toArray(new Position[trajet.size()]);
	}
	
	private void infosModifiees() {
		//System.out.println("Infos modifiées");
		if(ecouteurInfos != null) ecouteurInfos.nouvellesInfos(performances());
	}
	
	
	private String performances() {
		return "calcul zone pour le calcul : "+tempsZonePourLeCalcul+"ms\n"
				+ "calcul plus court chemin orthogonal : "+tempsPlusCourtCheminOrthogonal+"ms\n"
				+ "calcul zone : "+tempsZone+"ms + zone pour le calcul\n"
				+ "calcul zone élargie : "+tempsZoneElargie+"ms\n"
				+ "calcul zone topologique : "+tempsZoneTopologique+"ms + zone élargie\n"
				+ "calcul graphe : "+tempsCalculGraphe+"ms \n"
				+ "calcul des parcours : "+tempsCalculParcours+"ms \n"
				+ "nombre de parcours : "+nombreDeParcours+"\n";
	}
	
	public void ajouterEcouteurInfos(EcouteurInfos e) {
		ecouteurInfos = e;
	}
	
	private Plan<Distance> zonePourLeCalcul, plusCourtCheminOrthogonal, zone,
							zoneElargie, zoneTopologique;
	private long tempsZonePourLeCalcul, tempsPlusCourtCheminOrthogonal, tempsZone,
				  tempsZoneElargie, tempsZoneTopologique,
				  tempsCalculGraphe, tempsCalculParcours;
	private int nombreDeParcours, numeroDuParcours;
	private Graphe graphe;
	private final Plan<Boolean> carte;
	private final Position depart, arrivee;
	private EcouteurInfos ecouteurInfos;
}


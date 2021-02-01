package main.java.io.github.General;
import java.util.ArrayList;


/** Modélise la corde à tendre pour avoir le trajet le plus court.
 */
public class Corde {
	/**
	 * Crée une corde à tendre pour avoir le trajet le plus court.
	 * @param carte Carte réelle.
	 * @param trajet Trajet que la corde va suivre ; les coordonnées correspondent aux cases.
	 */
	public Corde(Plan<Boolean> carte, Position[] trajet) {
		this.carte = carte;
		this.trajet = trajet;
		//System.out.println("Taille du trajet : " + trajet.length);
	}
	

	
	/** Fournit une première approche de la corde tendue. */
	public Segment[] premiereApproche() {
		if(premiereApproche != null) return premiereApproche;
		
		ArrayList<Segment> resultat = new ArrayList<Segment>();
		Segment segmentActuel = new Segment(trajet[0].recevoirX(), trajet[0].recevoirY(), carte);
		Segment essaiDeSegment;
		
		// Principe : on avance selon le trajet en accrochant une corde rigide derrière soi.
		// Dès que celle-ci touche un obstacle, on la termine là où on est et on poursuit
		// avec une nouvelle.
		for(int i=0 ; i<trajet.length ; i++) {
			essaiDeSegment = new Segment(segmentActuel, trajet[i].recevoirX(), trajet[i].recevoirY(), carte);
			// System.out.println("Essai de segment : "+essaiDeSegment.toString());
			if(essaiDeSegment.toucheUnObstacle()) {
				resultat.add(segmentActuel);
				segmentActuel = new Segment(trajet[i-1].recevoirX(), trajet[i-1].recevoirY(),
								trajet[i].recevoirX(),  trajet[i].recevoirY(), carte);
				// System.out.println("  touche un obstacle => segment actuel : "+segmentActuel.toString());
			} else if(i == trajet.length - 1) {
			        resultat.add(essaiDeSegment);
			        
			} else {
				segmentActuel = essaiDeSegment;
			}
		}
		
		premiereApproche = resultat.toArray(new Segment[resultat.size()]);
		//System.out.println("Taille de la première approche : "+premiereApproche.length);
		//for(Segment s : premiereApproche)
		//	System.out.println(s.toString());
		return premiereApproche;
	}
	
	public static DoubleMaillon<Segment> smooth(DoubleMaillon<Segment> lissage, Plan<Boolean> carte) {
	           DoubleMaillon<Segment> premier = lissage;
	                
	                while(premier.suivant() != null) {
	                        
	                        DoubleMaillon<Segment> deuxieme = premier.suivant();
	                        double angle = Math.abs(AngleUtils.angle(premier.valeur().fin(), premier.valeur().debut(), deuxieme.valeur().fin()));
	                        //System.out.println("Angle = "+angle+", " + (Math.abs(angle) > 15));
	                        
	                        //if((angle < 170d || angle > 190d)
	                        if(Math.abs(angle) > 15 && angle != 180
	                                        //&& premier.valeur().taille() > 5
	                                        //&& deuxieme.valeur().taille() > 5
	                                        ) {
	                                // Si l'angle est suffisamment pointu, on juge nécessaire de l'aplanir :
	                                
	                                //new DebugSegment("", carte, lissage, premier.valeur(), deuxieme.valeur());
	                                
	                                
	                                //System.out.println("   prem = "+premier.valeur()
	                                //              + ", deu = "+deuxieme.valeur());
	                                
	                                // Le segment qui coupe la trajectoire commence sa course sur le segment suivant :
	                                DoubleMaillon<Segment> segmentDepart = premier,
	                                // Et il termine sa course sur le segment suivant :
	                                                                           segmentArrivee = deuxieme;
	                                
	                                DoubleMaillon<Segment> meilleurSegmentDepart = segmentDepart,
	                                                                           meilleurSegmentArrivee = segmentArrivee;
	                                
	                                // La suite de pixels correspondant aux segments de ci-dessus :
	                                Position[] pixelsSegmentDepart = segmentDepart.valeur().bresenham(),
	                                                   pixelsSegmentArrivee = segmentArrivee.valeur().bresenham();
	                                
	                                // Les endroits où l'on en est dans le parcours des segments (on commence au niveau du coin) :
	                                int indiceDepart = pixelsSegmentDepart.length - 1, indiceArrivee = 0;
	                        //System.out.println("   " + pixelsSegmentDepart.length + " ; " + pixelsSegmentArrivee.length);
	                                
	                                Segment meilleureCoupe = null, essaiDeCoupe = null;
	                                
	                                // Jusqu'à ce qu'on ne parvienne pas à faire de meilleure coupe ou que l'on est arrivé
	                                // au bout de l'itineraire :
	                                
	                                do {
	                                        
	                                //System.out.println("   " + indiceDepart + "," + indiceArrivee);
	                                        
	                                        indiceDepart--;         // Le départ commence plus tôt.
	                                        indiceArrivee++;        // L'arrivée commence plus tard.
	                                        
	                                        // L'essai de coupe plus profonde que la précédente :
	                                        essaiDeCoupe = new Segment(pixelsSegmentDepart[indiceDepart],
	                                                                                                pixelsSegmentArrivee[indiceArrivee],
	                                                                                                carte); // TODO inflate
	                                        
	                                        // Si l'essai se fait avec succès, on remplace.
	                                        if(!essaiDeCoupe.toucheUnObstacle()) {
	                                                meilleureCoupe = essaiDeCoupe;
	                                                meilleurSegmentDepart = segmentDepart;
	                                                meilleurSegmentArrivee = segmentArrivee;
	                                        } else {
	                                                //new DebugSegment("Essai raté", carte, lissage, essaiDeCoupe, null);
	                                                break;
	                                        }
	                                        
	                                        // Le segment de départ est remplacé par le précédent si on en atteint le bout.
	                                        if(indiceDepart <= 0) {
	                                                indiceDepart++;
	                                                //System.out.println("   inc");
	                                                if(segmentDepart.precedent() != null) {
	                                                        segmentDepart = segmentDepart.precedent();
	                                                        pixelsSegmentDepart = segmentDepart.valeur().bresenham();
	                                                        indiceDepart = pixelsSegmentDepart.length;
	                                                        //System.out.println("   nouv depart");
	                                                } else {
	                                                        break;
	                                                }
	                                                //System.out.println("   " + pixelsSegmentDepart.length + " ; " + pixelsSegmentArrivee.length);
	                                        }
	                                        
	                                        // Idem pour le segment d'arrivée.
	                                        if(indiceArrivee >= pixelsSegmentArrivee.length - 1) {
	                                                indiceArrivee--;
	                                                //System.out.println("   dec");
	                                                if(segmentArrivee.suivant() != null) {
	                                                        segmentArrivee = segmentArrivee.suivant();
	                                                        pixelsSegmentArrivee = segmentArrivee.valeur().bresenham();
	                                                        indiceArrivee = -1;
	                                                        //System.out.println("   nouv arrivee");
	                                                } else {
	                                                        break;
	                                                }
	                                                //System.out.println("   " + pixelsSegmentDepart.length + " ; " + pixelsSegmentArrivee.length);
	                                        }
	                                        
	                                        
	                                } while(true);
	                                
	                                if(meilleureCoupe != null && meilleureCoupe.taille() > 0) {
	                                        
	                                        segmentDepart = meilleurSegmentDepart;
	                                        segmentArrivee = meilleurSegmentArrivee;
	                                        
	                                        //new DebugSegment("Bientôt", carte, lissage, meilleureCoupe, null);
	                                        
	                                        DoubleMaillon<Segment> nouveauSegmentDepart =
	                                                        new DoubleMaillon<Segment>(new Segment(segmentDepart.valeur(),
	                                                                                                                meilleureCoupe.x1(),
	                                                                                                                meilleureCoupe.y1(),
	                                                                                                                carte)); // TODO inflate
	                                        
	                                        DoubleMaillon<Segment> segmentIntermediaire = new DoubleMaillon<Segment>(meilleureCoupe);
	                                        
	                                        DoubleMaillon<Segment> nouveauSegmentArrivee =
	                                                        new DoubleMaillon<Segment>(new Segment(
	                                                                        meilleureCoupe.x2(),
	                                                                        meilleureCoupe.y2(),
	                                                                        segmentArrivee.valeur().x2(),
	                                                                        segmentArrivee.valeur().y2(),
	                                                                        carte // TODO inflate
	                                                        )
	                                        );
	                                        
	                                        nouveauSegmentDepart.suivant(segmentIntermediaire);
	                                        segmentIntermediaire.suivant(nouveauSegmentArrivee);
	                                        
	                                        double nouvelleTaille = nouveauSegmentDepart.valeur().taille()
	                                                + segmentIntermediaire.valeur().taille()
	                                                + nouveauSegmentArrivee.valeur().taille();
	                                        
	                                        double ancienneTaille = segmentArrivee.valeur().taille();
	                                        for(DoubleMaillon<Segment> s = segmentDepart; s != segmentArrivee; s = s.suivant()) {
	                                            ancienneTaille += s.valeur().taille();
	                                        }
	                                        
	                                        if(nouvelleTaille < ancienneTaille) {
	                                            
	                                                System.out.print("Remplace ");
	                                                for(DoubleMaillon<Segment> s = segmentDepart; ; s = s.suivant()) {
	                                                    System.out.print(s.valeur().toString() + " ");
	                                                    if(s == segmentArrivee) { break; }
	                                                }
	                                                System.out.println(" de taille " + ancienneTaille);
	                                                System.out.println(" par " + nouveauSegmentDepart.valeur() + " "
	                                                        + segmentIntermediaire.valeur() + " " + nouveauSegmentArrivee.valeur()
	                                                        + " de taille " + nouvelleTaille);
        	                                        
        	                                        if(segmentDepart.precedent() == null) {
        	                                                lissage = nouveauSegmentDepart;
        	                                        } else {
        	                                                segmentDepart.precedent().suivant(nouveauSegmentDepart);
        	                                        }
        	                                        
        	                                        nouveauSegmentArrivee.suivant(segmentArrivee.suivant());
        	                                        
        	                                        //System.out.println("départ : "+nouveauSegmentDepart+", arrivée : "+nouveauSegmentArrivee);
        	                                        
        	                                        premier = nouveauSegmentDepart;
        	                                        
        	                                        if(nouveauSegmentDepart.valeur().taille() == 0) {
        	                                                if(nouveauSegmentDepart.precedent() == null) {
        	                                                        lissage = nouveauSegmentDepart.suivant();
        	                                                        //System.out.println("départ 0 précédent nul");
        	                                                }
        	                                                else {
        	                                                        nouveauSegmentDepart.precedent().suivant(nouveauSegmentDepart.suivant());
        	                                                        //System.out.println("départ 0 précédent existant");
        	                                                }
        	                                                
        	                                                premier = segmentIntermediaire;
        	                                        }
        	                                        
        	                                        if(nouveauSegmentArrivee.valeur().taille() == 0) {
        	                                                nouveauSegmentArrivee.precedent().suivant(nouveauSegmentArrivee.suivant());
        	                                                //System.out.println("arrivée 0");
        	                                        }
        	                                        
	                                        } else {
	                                            premier = premier.suivant();
	                                        }
	                                        
	                                        //new DebugSegment("Modification", carte, lissage, meilleureCoupe, null);
	                                        
	                                } else {
	                                        premier = premier.suivant();
	                                        //System.out.println("Suivant");
	                                }
	                                
	                        } else {
	                                // Si l'angle n'est pas suffisamment pointu, on passe au suivant :
	                                premier = premier.suivant();
	                                //System.out.println("Suivant");
	                        }
	                        
	                        //System.out.println(lissage);
	                }
                    return lissage;
	                
	}
	
	/** Lisse la suite de segments donnée par la première approche. */
	public Segment[] lissage() {
		if(this.lissage != null) return this.lissage;
		DoubleMaillon<Segment> lissage = new DoubleMaillon<Segment>(premiereApproche());
		lissage = smooth(lissage, carte);
		return lissage.toArray(new Segment[lissage.taille()]);
	}
	
	private final Plan<Boolean> carte;
	private final Position[] trajet;
	private Segment[] premiereApproche, lissage;
}
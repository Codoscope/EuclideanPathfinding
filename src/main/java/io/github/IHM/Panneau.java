package main.java.io.github.IHM;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.io.github.General.Corde;
import main.java.io.github.General.Distance;
import main.java.io.github.General.EcouteurInfos;
import main.java.io.github.General.Fonction;
import main.java.io.github.General.Itineraire;
import main.java.io.github.General.ModeDAffichage;
import main.java.io.github.General.Plan;
import main.java.io.github.General.Position;
import main.java.io.github.General.Segment;
import main.java.io.github.General.TypeDeTrajet;



class Panneau extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Panneau où s'affiche la carte des obstacles ainsi que les graphismes sur les trajectoires.
	 * @param carteReelle : carte des obstacles. true = un obstacle, false = pas d'obstacle.
	 * @param saisieNumeroDuTrajet : champ de texte à utiliser pour sélectionner un trajet dans la zone "topologique".
	 */
	public Panneau(Plan<Boolean> carteReelle, JTextField saisieNumeroDuTrajet) {
		
		this.saisieNumeroDuTrajet = saisieNumeroDuTrajet;
		saisieNumeroDuTrajet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculerTrajet();
			}
		});
		
		fonctionDepart = new Fonction() {
			public void action(Position p) {
				//System.out.println("Clic avec depart activé");
				if(Panneau.this.carte.contient(p)) {
					depart = p;
					conditionsModifiees();
					calculerTrajet();
				}
			}
		};
		
		fonctionCourante = fonctionDepart;
		
		fonctionArrivee = new Fonction() {
			public void action(Position p) {
				//System.out.println("Clic avec depart activé");
				if(Panneau.this.carte.contient(p)) {
					arrivee = p;
					conditionsModifiees();
					calculerTrajet();
				}
			}
		};
		
		fonctionObstacle = new Fonction() {
			public void action(Position p) {
				if(Panneau.this.carte.contient(p)) {
					Panneau.this.carte.modifierElement(p, !Panneau.this.carte.recevoirElement(p)); // TODO use tailleCase
					Panneau.this.carteReelle.modifierElement(p, !Panneau.this.carteReelle.recevoirElement(p));
					conditionsModifiees();
					calculerTrajet();
				}
			}
		};
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fonctionCourante.action(new Position(e.getX()/(taillePixel*tailleCase), e.getY()/(taillePixel*tailleCase)));
			}
		});
		modeDAffichage = ModeDAffichage.NORMAL;
		this.effetsDeCouleurs = false;
		modifierCarte(carteReelle);
		
		//System.out.println("<== new Panneau");
	}
	
	public void modifierCarte(Plan<Boolean> carte) {
		taillePixel = Math.max(1, Math.min(800/carte.recevoirLargeur(), 500/carte.recevoirHauteur()));
		setPreferredSize(new Dimension(carte.recevoirLargeur()*taillePixel, carte.recevoirHauteur()*taillePixel));
		this.carteReelle = carte;
		
		this.carte = new Plan<Boolean>(1+(carteReelle.recevoirHauteur()/tailleCase),
								  1+(carteReelle.recevoirLargeur()/tailleCase),
								  new Boolean(false));
		this.carte.modifierValeurParDefaut(new Boolean(true));
		
		for(int x=0 ; x<this.carte.recevoirLargeur() ; x++)
			for(int y=0 ; y<this.carte.recevoirHauteur() ; y++)
				// Pour chaque case de la carte pixelisée, on veut voir s'il y a un obstacle dedans.
				// S'il y a effectivement un obstacle, on noircit la case et on passe à la suite.
				parcourtInterieurCase:
					for(int xInt=0 ; xInt<tailleCase ; xInt++)
						for(int yInt=0 ; yInt<tailleCase ; yInt++)
							if(carteReelle.recevoirElement(yInt + tailleCase*y, xInt + tailleCase*x)) {
								// Il y a un point noir dans la case donc on la noircit en entier.
								this.carte.modifierElement(y, x, true);
								break parcourtInterieurCase;
							}
		
		depart = arrivee = null;
		zone = null;
		itineraire = null;
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		//System.out.println("==> Panneau.paintComponent(...)");
		super.paintComponent(g);
		int tailleCarre = taillePixel*tailleCase; // taille d'un carré pixelisé
		
		// ****************
		// Tracé de la zone :
		// ****************
		
		g.setFont(new Font("Courier", Font.PLAIN, taillePixel-8));
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, tailleCarre*carte.recevoirLargeur(),
						 tailleCarre*carte.recevoirHauteur());
		
		if(zone != null && typeDeTrajet != TypeDeTrajet.UniquementCordeLisse) {
			//System.out.println("Zone non nulle");
			for(int y=0 ; y<carte.recevoirHauteur() ; y++)
				for(int x=0 ; x<carte.recevoirLargeur() ; x++)
					if(zone.recevoirElement(y, x) != Distance.INFINI){
						int couleur = (int)zone.recevoirElement(y, x).valeur();
						if(effetsDeCouleurs)
							g.setColor(new Color(20+(23*couleur)%150,20+(13*couleur)%50,20+(7*couleur)%98));
						else
							g.setColor(Color.BLUE);
						g.fillRect(tailleCarre*x, tailleCarre*y, tailleCarre, tailleCarre);
						g.setColor(Color.WHITE);
						String texte = ""+zone.recevoirElement(y, x).valeur();
						int nombreDeChiffres = texte.length();
						if(tailleCarre > 2+4*nombreDeChiffres) {
							g.setFont(new Font("Courier", Font.PLAIN, 3*(tailleCarre-2)/(2*2/*nombreDeChiffres*/)));
							g.drawString(texte, tailleCarre*x+1, tailleCarre*(y+1)-1);
						}
					}
		}
		
		if(trajet != null && typeDeTrajet != TypeDeTrajet.UniquementCordeLisse) {
			//System.out.println("Trajet non nul");
			g.setColor(Color.GREEN);
			for(final Position p : trajet) {
				g.fillRect(tailleCarre*p.recevoirX(), tailleCarre*p.recevoirY(), tailleCarre, tailleCarre);
				//System.out.println("Test position "+p.recevoirX()+","+p.recevoirY());
			}
		}
		
		// *****************
		// Tracé de la carte :
		// *****************
		
		{
			int hauteur = tailleCarre*carte.recevoirHauteur(),
				largeur = tailleCarre*carte.recevoirLargeur();
			
			switch(modeDAffichage) {
				
			case GRILLE:
				g.setColor(Color.BLACK);
				// Affichage des barres verticales
				for(int x=0 ; x<=largeur ; x+=tailleCarre)
					g.drawLine(x, 0, x, hauteur);
				// Affichage des barres horizontales
				for(int y=0 ; y<=hauteur ; y+=tailleCarre)
					g.drawLine(0, y, largeur, y);
				
			case NORMAL:
				for(int y=0 ; y<carteReelle.recevoirHauteur() ; y++)
					for(int x=0 ; x<carteReelle.recevoirLargeur() ; x++)
						if(((Boolean)carteReelle.recevoirElement(y, x)).booleanValue()) {
							g.setColor(Color.BLACK);
							g.fillRect(taillePixel*x, taillePixel*y, taillePixel, taillePixel);
						}
				
				break;
			
			/* TODO
			case PIXELISE:
				for(int y=0 ; y<carte.recevoirHauteur() ; y++) {
					for(int x=0 ; x<carte.recevoirLargeur() ; x++) {
						if(((Boolean)carte.recevoirElement(y, x)).booleanValue()) {
							g.setColor(Color.BLACK);
							g.fillRect(tailleCarre*x, tailleCarre*y, tailleCarre, tailleCarre);
						} else {
							// g.setColor(Color.WHITE);
						}
					}
				}
				break;
			*/
			}
		}
		
		// *******************************
		// Tracé du départ et de l'arrivée.
		// *******************************
		
		if(depart != null) {
			g.setColor(Color.RED);
			g.fillRect(tailleCarre*depart.recevoirX(), tailleCarre*depart.recevoirY(), tailleCarre, tailleCarre);
		}
		
		if(arrivee != null) {
			g.setColor(Color.GREEN);
			g.fillRect(tailleCarre*arrivee.recevoirX(), tailleCarre*arrivee.recevoirY(), tailleCarre, tailleCarre);
		}
		
		// *****************
		// Tracé de la corde.
		// *****************
		
		if(segments != null) {
			g.setColor(Color.MAGENTA);
			for(Segment segment : segments) {
			        int offset = taillePixel/2;
				g.drawLine(taillePixel*segment.x1() + offset, taillePixel*segment.y1() + offset,
						   taillePixel*segment.x2() + offset, taillePixel*segment.y2() + offset);
			}
		}
		
		//System.out.println("<== Panneau.paintComponent");
	}
	
	public void enModificationDuDepart() {
		fonctionCourante = fonctionDepart;
	}
	
	public void enModificationDeLArrivee() {
		fonctionCourante = fonctionArrivee;
	}
	
	public void enModificationDeLaCarte() {
		fonctionCourante = fonctionObstacle;
	}
	
	public void calculerTrajet() {
		if(depart != null && arrivee != null) {
			if(itineraire == null) {
				itineraire = new Itineraire(carte, depart, arrivee);
				itineraire.ajouterEcouteurInfos(ecouteurInfos);
			}
			
			if(typeDeTrajet != null) {
			    
		                resetPaths();
			    
				switch(typeDeTrajet) {
				
				case ZonePourLeCalcul:
					zone = itineraire.calculerZonePourLeCalcul();
					break;
				case PlusCourtCheminOrthogonal:
					zone = itineraire.calculerPlusCourtCheminOrthogonal();
					break;
				case Zone:
					 zone = itineraire.calculerZone();
					 break;
				case ZoneElargie:
					zone = itineraire.calculerZoneElargie();
					break;
				case ZoneTopologique:
					zone = itineraire.calculerZoneTopologique();
					break;
				case Graphe:
					zone = itineraire.calculerZoneTopologique();
					itineraire.modifierNumeroParcours(-1+Integer.parseInt(
						saisieNumeroDuTrajet.getText()
					));
					trajet = itineraire.calculerParcours();
					break;
				case Corde:
					zone = itineraire.calculerZoneTopologique();
					itineraire.modifierNumeroParcours(-1+Integer.parseInt(
						saisieNumeroDuTrajet.getText()
					));
					trajet = itineraire.calculerParcours();
					segments = new Corde(carteReelle, trajet).premiereApproche();
					break;
				case CordeLisse:
				case UniquementCordeLisse:
					zone = itineraire.calculerZoneTopologique();
					itineraire.modifierNumeroParcours(-1+Integer.parseInt(
						saisieNumeroDuTrajet.getText()
					));
					trajet = itineraire.calculerParcours();
					segments = new Corde(carteReelle, trajet).lissage();
					break;
				}
			}
		}
		System.out.println("Repaint");
		repaint();
	}
	
	public void augmenterTailleCase() {
		taillePixel++;
		mettreAJourTaille();
		repaint();
	}
	
	public void diminuerTailleCase() {
		if(taillePixel > 1) {
			taillePixel--;
			mettreAJourTaille();
			repaint();
		}
	}
	
	private void mettreAJourTaille() {
		setPreferredSize(new Dimension(taillePixel*carteReelle.recevoirLargeur(), taillePixel*carteReelle.recevoirHauteur()));
	}
	
	public void modifierTypeDeTrajet(TypeDeTrajet typeDeTrajet) {
		this.typeDeTrajet = typeDeTrajet;
		calculerTrajet();
	}
	
	public void modifierModeDAffichage(ModeDAffichage modeDAffichage) {
		this.modeDAffichage = modeDAffichage;
		repaint();
	}
	
	public void activerEffetsDeCouleurs(boolean b) {
		this.effetsDeCouleurs = b;
		repaint();
	}
	
	public void ajouterEcouteurInfos(EcouteurInfos e) {
		ecouteurInfos = e;
		if(itineraire != null) itineraire.ajouterEcouteurInfos(e);
	}
	
	private void resetPaths() {
	           zone = null;
	           trajet = null;
	           segments = null;
	}
	
	/** À appeler si le départ ou l'arrivée a été déplacé(e) pour supprimer les calculs effectués. */
	public void conditionsModifiees() {
		resetPaths();
                itineraire = null;
		saisieNumeroDuTrajet.setText("1");
	}
	
	private Plan<Boolean> carteReelle; // Carte de départ avec beaucoup de pixels.
	private Plan<Boolean> carte; // La carte des obstacles pixelisée.
	
	private int taillePixel; // La taille d'un pixel.
	private int tailleCase = 1; // Pour la pixelisation de l'image. De l'ordre de grandeur du mobile.
	
	private Position depart, arrivee; // Les positions d'arrivée et de départ, initialement à null.
	private Fonction fonctionDepart, fonctionArrivee, fonctionCourante, fonctionObstacle;
	// ^^^^^ Fonctions à appeler lors d'un clic de souris.
	
	private Position[] trajet; // Le trajet couramment calculé, initialement à null.
	private Segment[] segments; // Les segments composant la corde correspondant au trajet couramment calculé.
	private Plan<Distance> zone; // La zone de recherche du plus court trajet.
	
	private TypeDeTrajet typeDeTrajet;
	private ModeDAffichage modeDAffichage;
	
	private boolean effetsDeCouleurs;
	private Itineraire itineraire;
	private EcouteurInfos ecouteurInfos;
	
	private final JTextField saisieNumeroDuTrajet; // Champ de saisie du numéro de trajet à afficher.
}

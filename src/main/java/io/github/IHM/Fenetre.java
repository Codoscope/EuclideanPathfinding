package main.java.io.github.IHM;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.PixelGrabber;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
//import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.java.io.github.General.EcouteurInfos;
import main.java.io.github.General.ModeDAffichage;
import main.java.io.github.General.Plan;
import main.java.io.github.General.TypeDeTrajet;



public class Fenetre extends JFrame {
	private static final long serialVersionUID = 1L;

	public Fenetre(final String nomImage) {
		setTitle("Euclidean pathfinder");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contenu = getContentPane();
		
		final JTextField saisieNumeroDuTrajet = new JTextField("1", 3);
		
		panneau = new Panneau(fabriquerCarte(), saisieNumeroDuTrajet);
		contenu.add(new JScrollPane(panneau));
		
		JMenuBar barre = new JMenuBar();
		
		// Affichage des performances de l'algorithme et du nombre de trajets :
		final JTextArea texte = new JTextArea();
		panneau.ajouterEcouteurInfos(new EcouteurInfos() {
			public void nouvellesInfos(String infos) {
				texte.setText(infos);
			}
		});
		contenu.add(texte, "South");
		
		JButton modifierCarte = new JButton("Open a map");
		modifierCarte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*String aOuvrir = (String) JOptionPane.showInputDialog(Fenetre.this, "Entrez le nom de l'image à ouvrir :",
															 "Ouverture d'une carte", JOptionPane.QUESTION_MESSAGE,
															 null, null, "../");*/
				
				JFileChooser jfc = new JFileChooser(nomImage);
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setMultiSelectionEnabled(false);
				//jfc.setSelectedFile(new File(nomImage));
				jfc.showOpenDialog(null);
				File fichierOuvrir = jfc.getSelectedFile();
				if(fichierOuvrir != null) {
					panneau.modifierCarte(fabriquerCarte(fichierOuvrir.getAbsolutePath()));
				}
				/*
				if(aOuvrir != null) {
					panneau.modifierCarte(fabriquerCarte(aOuvrir));
					texte.setText("");
				}*/
			}
		});
		barre.add(modifierCarte);
		
		barre.add(saisieNumeroDuTrajet);
		
		final JButton trajetPrecedent = new JButton("<");
		trajetPrecedent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saisieNumeroDuTrajet.setText(
						"" + (-1 + Integer.parseInt(saisieNumeroDuTrajet.getText()))
				);
				saisieNumeroDuTrajet.getActionListeners()[0].actionPerformed(
						new ActionEvent(trajetPrecedent, getDefaultCloseOperation(), "")
				);
			}
		});
		barre.add(trajetPrecedent);
		
		final JButton trajetSuivant = new JButton(">");
		trajetSuivant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saisieNumeroDuTrajet.setText(
						"" + (1 + Integer.parseInt(saisieNumeroDuTrajet.getText()))
				);
				saisieNumeroDuTrajet.getActionListeners()[0].actionPerformed(
						new ActionEvent(trajetSuivant, getDefaultCloseOperation(), "")
				);
			}
		});
		barre.add(trajetSuivant);
		
		JButton depart = new JButton("Select the departure");
		depart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("Action sur bouton depart");
				panneau.enModificationDuDepart();
			}
		});
		barre.add(depart);
		
		JButton arrivee = new JButton("Select the arrival");
		arrivee.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("Action sur bouton arrivée");
				panneau.enModificationDeLArrivee();
			}
		});
		barre.add(arrivee);
		
		JButton obstacles = new JButton("Switch obstacles");
		obstacles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panneau.enModificationDeLaCarte();
			}
		});
		barre.add(obstacles);
		
		{
			JMenu menuTypeDeTrajet = new JMenu("Task to perform");
			barre.add(menuTypeDeTrajet);
			
			ButtonGroup groupe = new ButtonGroup();
			for(final TypeDeTrajet type : TypeDeTrajet.values()) {
				JRadioButton boutonRadio = new JRadioButton(type.texteAssocie(), false);
				boutonRadio.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent action) {
						panneau.modifierTypeDeTrajet(type);
					}
				});
				menuTypeDeTrajet.add(boutonRadio);
				groupe.add(boutonRadio);
			}
		}
		
		{
			JMenu menuModeDAffichage = new JMenu("Display mode");
			barre.add(menuModeDAffichage);
			
			ButtonGroup groupe = new ButtonGroup();
			for(final ModeDAffichage mode : ModeDAffichage.values()) {
				JRadioButton boutonRadio = new JRadioButton(mode.texteAssocie(), false);
				boutonRadio.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent action) {
						panneau.modifierModeDAffichage(mode);
					}
				});
				menuModeDAffichage.add(boutonRadio);
				groupe.add(boutonRadio);
			}
		}
		
		JButton augmCase = new JButton("+");
		augmCase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panneau.augmenterTailleCase();
				revalidate();
			}
		});
		barre.add(augmCase);
		
		JButton dimiCase = new JButton("-");
		dimiCase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panneau.diminuerTailleCase();
				revalidate();
			}
		});
		barre.add(dimiCase);
		
		final JCheckBox couleur = new JCheckBox("Color iso-lines");
		couleur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panneau.activerEffetsDeCouleurs(couleur.isSelected());
			}
		});
		couleur.setSelected(false);
		barre.add(couleur);
		
		contenu.add(barre, "North");
	}
	
	private Plan<Boolean> fabriquerCarte(String aOuvrir) {
		int[][] environnement = ouvrirImage(aOuvrir);
		int hauteur = environnement.length;
		int largeur = environnement[0].length;
		Boolean[][] carteTableau = new Boolean[hauteur][largeur];
		for(int y=0 ; y<hauteur ; y++)
			for(int x=0 ; x<largeur ; x++)
				carteTableau[y][x] = new Boolean(environnement[y][x] != -1);
		
		return new Plan<Boolean>(carteTableau, new Boolean(true));
	}
	
	private Plan<Boolean> fabriquerCarte() {
	    int hauteur = 600;
	    int largeur = 800;
	    Boolean[][] carteTableau = new Boolean[hauteur][largeur];
            for(int y=0 ; y<hauteur ; y++)
                    for(int x=0 ; x<largeur ; x++)
                            carteTableau[y][x] = new Boolean(false);
            
            return new Plan<Boolean>(carteTableau, new Boolean(true));
	}
	
	private static int[][] ouvrirImage(String nom) {
		Image image = (new javax.swing.ImageIcon(nom)).getImage();
		final int largeur = image.getWidth(null),
				   hauteur = image.getHeight(null);
		int[] pixels = new int[largeur*hauteur];
		PixelGrabber pg = new PixelGrabber(image, 0, 0, largeur, hauteur, pixels, 0, largeur);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("Interruption de l'attente des pixels");
		}
		
		int[][] tableau = new int[hauteur][largeur];
		for(int y=0 ; y<hauteur ; y++)
			for(int x=0 ; x<largeur ; x++)
				tableau[y][x] = pixels[largeur*y+x];
		
		return tableau;
	}
	
	private Panneau panneau;
}

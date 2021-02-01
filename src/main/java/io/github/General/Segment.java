package main.java.io.github.General;

import java.util.ArrayList;

/** Caractérise une portion de corde qui s'étend sur des cases de la carte. */
public class Segment {
        /**
         * @param x1 : Abscisse du départ.
         * @param y1 : Ordonnée du départ.
         * @param x2 : Abscisse de l'arrivée.
         * @param y2 : Ordonnée de l'arrivée.
         */
        public Segment(int x1, int y1, int x2, int y2, Plan<Boolean> carte) {
                this.carte = carte;
                this.x1 = x1; this.y1 = y1;
                this.x2 = x2; this.y2 = y2;
        }
        
        /**
         * 
         * @param depart Position du départ.
         * @param arrivee Position de l'arrivée.
         */
        public Segment(Position depart, Position arrivee, Plan<Boolean> carte) {
                this(depart.recevoirX(), depart.recevoirY(), arrivee.recevoirX(), arrivee.recevoirY(), carte);
        }
        
        /** Départ = arrivée = (x, y). Les coordonnées correspondent aux cases.
         * @param x Abscisse du départ et de l'arrivée.
         * @param y Ordonnée du départ et de l'arrivée.
         */
        public Segment(int x, int y, Plan<Boolean> carte) {
                this(x, y, x, y, carte);
        }
        
        /**
         * @param segment Donne les coordonnées du départ.
         * @param x2 Abscisse de l'arrivée.
         * @param y2 Ordonnée de l'arrivée.
         */
        public Segment(Segment segment, int x2, int y2, Plan<Boolean> carte) {
                this(segment.x1(), segment.y1(), x2, y2, carte);
        }
        
        /** Renvoie les cases de la matrice pixelisée correspondant au segment. */
        public Position[] bresenham() {
                if(renvoieBresenham != null) return renvoieBresenham;
                
                ArrayList<Position> resultat = new ArrayList<Position>();
                
                int x1=this.x1, x2=this.x2, y1=this.y1, y2=this.y2;
                
                int dx, dy;
                
                if((dx = x2 - x1) != 0) {
                        if(dx > 0) {
                                if((dy = y2 - y1) != 0) {
                                        if(dy > 0) {
                                                // vecteur oblique dans le 1er quadran
                                                
                                                if(dx >= dy) {
                                                        // vecteur diagonal ou oblique proche de l'horizontale, dans le 1er octant
                                                        int e = dx; // positif
                                                        dx  *= 2; dy *= 2;
                                                        
                                                        while(true) { // déplacements horizontaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(++x1 == x2) break;
                                                                if((e -= dy) < 0) {
                                                                        y1++;
                                                                        e += dx;
                                                                }
                                                        }
                                                } else {
                                                        // vecteur oblique proche de la verticale, dans le 2nd octant
                                                        int e = dy; // positif
                                                        dy *= 2; dx *= 2;
                                                        while(true) { // déplacements verticaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(++y1 == y2) break;
                                                                if((e -= dx) < 0) {
                                                                        x1++;
                                                                        e += dy;
                                                                }
                                                        }
                                                }
                                        } else { // dy < 0 (et dx > 0)
                                                // vecteur oblique dans le 4e cadran
                                                
                                                if(dx >= -dy) {
                                                        // vecteur diagonal ou oblique proche de l’horizontale, dans le 8e octant
                                                        int e = dx; // positif
                                                        dx *= 2; dy *= 2;
                                                        while(true) { // déplacements horizontaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(++x1 == x2) break;
                                                                if((e += dy) < 0) {
                                                                        y1--;
                                                                        e += dx;
                                                                }
                                                        }
                                                } else { // vecteur oblique proche de la verticale, dans le 7e octant
                                                        int e = dy; // négatif
                                                        dy *= 2; dx *= 2;
                                                        while(true) { // déplacements verticaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(--y1 == y2) break;
                                                                if((e += dx) > 0) {
                                                                        x1++;
                                                                        e += dy;
                                                                }
                                                        }
                                                }
                                        }
                                } else { // dy = 0 (et dx > 0)
                                        
                                        // vecteur horizontal vers la droite
                                        do {
                                                resultat.add(new Position(x1, y1));
                                        } while (++x1 < x2);
                                }
                        } else { // dx < 0
                                if((dy = y2 - y1) != 0) {
                                        if(dy > 0) {
                                                // vecteur oblique dans le 2nd quadran
                                                
                                                if(-dx >= dy) {
                                                        // vecteur diagonal ou oblique proche de l'horizontale, dans le 4e octant
                                                        int e = dx; // négatif
                                                        dx *= 2; dy *= 2;
                                                        while(true) { // déplacements horizontaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(--x1 == x2) break;
                                                                if((e += dy) >= 0) {
                                                                        y1++; // déplacements diagonal
                                                                        e += dx;
                                                                }
                                                        }
                                                } else {
                                                        // vecteur oblique proche de la verticale, dans le 3e octant
                                                        int e = dy; // positif
                                                        dx *= 2; dy *= 2;
                                                        while(true) { // déplacements verticaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(++y1 == y2) break;
                                                                if((e += dx) <= 0) {
                                                                        x1--; // déplacement diagonal
                                                                        e += dy;
                                                                }
                                                        }
                                                }
                                        } else { // dy < 0 (et dx < 0)
                                                // vecteur oblique dans le 3e cadran
                                                
                                                if(dx <= dy) {
                                                        // vecteur diagonal ou oblique proche de l’horizontale, dans le 5e octant
                                                        int e = dx; // négatif
                                                        dx *= 2; dy *= 2;
                                                        while(true) { // déplacements horizontaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(--x1 == x2) break;
                                                                if((e -= dy) >= 0) {
                                                                        y1--; // déplacement diagonal
                                                                        e += dx;
                                                                }
                                                        }
                                                } else {
                                                        // vecteur oblique proche de la verticale, dans le 6e octant
                                                        int e = dy; // négatif
                                                        dy *= 2; dx *= 2;
                                                        while(true) { // déplacements verticaux
                                                                resultat.add(new Position(x1, y1));
                                                                if(--y1 == y2) break;
                                                                if((e -= dx) >= 0) {
                                                                        x1--;
                                                                        e += dy;
                                                                }
                                                        }
                                                }
                                        }
                                } else { // dy = 0 (et dx < 0)
                                        
                                        // vecteur horizontal vers la gauche
                                        do {
                                                resultat.add(new Position(x1, y1));
                                        } while(--x1 > x2);
                                        
                                }
                        }
                } else { // dx = 0
                        if((dy = y2 - y1) != 0) {
                                if(dy > 0) {
                                        
                                        // vecteur vertical croissant
                                        do {
                                                resultat.add(new Position(x1, y1));
                                        } while(++y1 < y2);
                                        
                                } else { // dy < 0 (et dx = 0)
                                        
                                        // vecteur vertical décroissant
                                        do {
                                                resultat.add(new Position(x1, y1));
                                        } while(--y1 > y2);
                                }
                        }
                }
                
                resultat.add(new Position(x2, y2));
                
                int longueur = resultat.size();
                return renvoieBresenham = resultat.toArray(new Position[longueur]);
        }               
        
        /**
         * 
         * @return Vrai si le segment touche un obstacle.
         */
        public boolean toucheUnObstacle() {
                bresenham();
                for(Position p : renvoieBresenham) {
                        if((boolean) carte.recevoirElement(p)) {
                                return true;
                        }
                }
                return false;
        }
        
        /**
         * @return La longueur du segment.
         */
        public double taille() {
                if(taille == 0)
                        taille = Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
                return taille;
        }
        
        @Override
        public String toString() {
                return "("+x1+","+y1+","+x2+","+y2+") taille "+Math.round(taille())+" ";
        }
        
        @Override
        public boolean equals(Object o) {
            if(!(o.getClass() == Segment.class)) {
                return false;
            }
            
            Segment s = (Segment) o;
            
            return x1 == s.x1 && x2 == s.x2 && y1 == s.y1 && y2 == s.y2; 
        }
        
        public int x1() {return x1;}
        public int x2() {return x2;}
        public int y1() {return y1;}
        public int y2() {return y2;}
        
        public Position debut() {return new Position(x1, y1);}
        public Position fin()   {return new Position(x2, y2);}
        
        private final int x1, y1, x2, y2;
        private final Plan<Boolean> carte;
        
        private double taille;
        private Position[] renvoieBresenham;
}
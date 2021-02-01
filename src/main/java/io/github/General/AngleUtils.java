package main.java.io.github.General;

public class AngleUtils {
    public static double angle(Position centre, Position point1, Position point2) {
            
            double degrees = Math.toDegrees(-Math.atan2(point1.recevoirX() - centre.recevoirX(),
                                                            point1.recevoirY() - centre.recevoirY())+
                                                       Math.atan2(point2.recevoirX()- centre.recevoirX(),
                                                                       point2.recevoirY()- centre.recevoirY()));
            if(degrees > 180) {
                return 360 - degrees;
            } else if(degrees <= -180) {
                return degrees + 360;
            } else {
                return degrees;
            }
    }
}

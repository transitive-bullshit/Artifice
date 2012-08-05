package fisch;
/*    Static constants and methods accessible anywhere
 * within my projects.
 * 
 * @usage: import static fisch.Utilities.*;
 * 
 * @author Travis Fischer (tfischer)
 * @date 12/25/2006
 */
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Utilities {
    public static final float     PI = (float)(Math.PI);
    public static final float TWO_PI = (float)(2 * Math.PI);
    
    /** Returns randomly true or false **/
    public static final boolean randomBoolean() {
        return (Math.random() > 0.5);
    }

    /** Returns a random float between zero and one inclusive **/
    public static final float random() {
        return (float)Math.random();
    }
    
    /** Returns a random float between -bound and bound **/
    public static final float randomSigned(float bound) {
        return bound - 2 * bound * random();
    }

    /** Returns a random positive or negative sign **/
    public static final int randomSign() {
        return (randomBoolean() ? 1 : -1);
    }
    
    /** Returns a random float between low and high inclusive **/
    public static final float random(float low, float high) {
        return low + (float) (Math.random() * (high - low));
    }
    
    /** Returns a random integer between low and high inclusive **/
    public static final int random(int low, int high) {
        return low + (int) (Math.random() * (high - low + 1));
    }
    
    /** Enable/Disable Antialiasing for the given Graphics2D **/
    public static final void setAntialiasing(Graphics2D brush, boolean enabled) {
        Object renderingHint = (enabled ? 
            RenderingHints.VALUE_ANTIALIAS_ON : 
            RenderingHints.VALUE_ANTIALIAS_OFF);
        
        brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING, renderingHint);
    }
}

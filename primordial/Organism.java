package primordial;
import static fisch.Utilities.*;

import gfx.Ellipse;
import javax.swing.JPanel;
import java.awt.*;

public interface Organism {
    /* Should move the organism and perform anything
     * necessary for it stay alive
     * 
     * @returns true if organism is still alive; false otherwise
     */
    public boolean update();
    
    /** Paints the Organism graphically using the specified Graphics2D **/
    public void paint(Graphics2D brush);
    
    public void setVariation(Variation variation);
}

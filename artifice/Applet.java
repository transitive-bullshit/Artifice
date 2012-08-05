package artifice;

import static artifice.ArtificeConstants.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author Travis Fischer
 * 
 */
public class Applet extends java.applet.Applet {
    
    public void init() {
        this.add(new MainPanel());
        
//        this.setVisible(true);
    }
}

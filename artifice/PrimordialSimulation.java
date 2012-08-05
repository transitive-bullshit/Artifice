package artifice;
import static artifice.ArtificeConstants.*;
import primordial.*;

import java.awt.image.*;
import java.awt.*;

/**
 * @author Travis Fischer
 *
 * Concept and code base by J. Tarbell
 * @see http://www.complexification.net/
 */
public class PrimordialSimulation extends Simulation {
    private static final int PRIMORDIAL_DELAY = 40;
    private Organism[] _organisms;
    private int _noOrganisms;
    private Variation _variation;
    private boolean _singleCelled, _flagella, _octopi;
    
    public PrimordialSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB, PRIMORDIAL_DELAY);
        
        _noOrganisms = 25;//25;//18
        _variation   = Variation.PRIMORDIAL_DEFAULT;
        _singleCelled = false;
        _flagella     = true;
        _octopi       = true;
    }
    
    public void setVariation(Variation variation) {
        if (_variation != variation) {
            _variation = variation;
            
            if (_variation == Variation.PRIMORDIAL_DEFAULT)
                this.setDelay(PRIMORDIAL_DELAY);
            else this.setDelay(0);
            
            for(int i = 0; i < _organisms.length; i++) {
                if (_organisms[i] != null)
                    _organisms[i].setVariation(variation);
            }
        }
    }
    
    public void update() {
        for(int i = 0; i < _organisms.length; i++) {
            if (_organisms[i] != null)
                _organisms[i].update();
        }
        
        this.repaint();
    }
    
    // Initializes and starts the Simulation
    public final void reset() {
        _organisms = new Organism[_noOrganisms];
        
        // Ugly, but it works for the purposes of this Simulation
        int chanceOfSingle     = 18;
        int chanceOfFlagellum  = 92 - chanceOfSingle;
        int chanceOfOctopus    = 100 - chanceOfFlagellum;
        
        if (!_singleCelled) {
            chanceOfSingle     = -1000;
            chanceOfFlagellum += 9;
            chanceOfOctopus   += 9;
        }
        
        if (!_flagella) {
            chanceOfFlagellum  = -1000;
            chanceOfSingle    += 64;
            chanceOfOctopus   += 10;
        }
        
        if (!_octopi) {
            chanceOfOctopus    = -1000;
            chanceOfSingle    += 3;
            chanceOfFlagellum += 7; 
        }
        
        if (chanceOfSingle < 0) chanceOfSingle = 0;
        if (chanceOfFlagellum < 0) chanceOfFlagellum = 0;
        if (chanceOfOctopus < 0) chanceOfOctopus = 0;
        
//        int flag = chanceOfFlagellum;
        chanceOfFlagellum += chanceOfSingle;
        chanceOfOctopus += chanceOfFlagellum;
        
        int noOctopi = 0;
        
        for(int i = 0; i < _organisms.length; i++) {
            Organism o = null;
            int rand = random(0, 99);
            
            if (rand < chanceOfSingle)
                o = new Cell(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
            else if (rand < chanceOfFlagellum)
                o = new Flagellum(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
            else if (rand < chanceOfOctopus)
                o = new Octopus(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
            else if (_flagella)
                o = new Flagellum(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
            else {
                boolean rand2 = (_singleCelled && _octopi) ? randomBoolean() : true;
                
                if (_singleCelled && rand2)
                    o = new Cell(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
                else if (_octopi && rand2)
                    o = new Octopus(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
            }
//            if (rand < 18)
//                o = new Cell(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
//            else if (rand < 92)
//                o = new Flagellum(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
//            else o = new Octopus(_dp, PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, _variation);
            
            if (o != null && (o instanceof Octopus) && ++noOctopi > 3)
                o = null;
            
            _organisms[i] = o;
        }
    }
    
    public void setSingleCelled(boolean enabled) {
        System.out.println(enabled);
        if (_singleCelled != enabled) {
            _singleCelled = enabled;
            this.updateOrganisms();
        }
    }

    public void setFlagella(boolean enabled) {
        if (_flagella != enabled) {
            _flagella = enabled;
            this.updateOrganisms();
        }
    }

    public void setOctopi(boolean enabled) {
        if (_octopi != enabled) {
            _octopi = enabled;
            this.updateOrganisms();
        }
    }
    
    public void updateOrganisms() {
        this.restart();
    }
    
    public void paintSimulation(Graphics2D brush) {
        if (_variation == Variation.PRIMORDIAL_SNAKESKIN) {
            if (_offscreen != null)
              brush.drawImage(_offscreen, 0, 0, _dp);
            
            brush = (Graphics2D)_offscreen.getGraphics();
        }
//        else {
//            brush.setColor(Color.BLACK);
//            brush.fillRect(0, 0, _dp.getWidth(), _dp.getHeight());
//        }
        
        for(int i = 0; i < _organisms.length; i++) {
            if (_organisms[i] != null)
                _organisms[i].paint(brush);
        }
    }
}

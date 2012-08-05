package primordial;
import static artifice.ArtificeConstants.*;

import gfx.Ellipse;
import javax.swing.JPanel;

import java.awt.image.*;
import java.awt.*;
import java.util.Vector;

public class Octopus extends Cell {
    // Random number of tentacles attached to _head Cell
    private Flagellum[] _flagella;
    
    public Octopus(JPanel container) {
        this(container, Variation.PRIMORDIAL_DEFAULT);
    }
    
    public Octopus(JPanel container, Variation variation) {
        this(container, 0, 0, variation);
    }
    
    public Octopus(JPanel container, float x, float y, Variation variation) {
        super(container, x, y, variation);
        
        // Average length of tentacles (number of Cells in a Flagellum)
        int averageLength = random(16, 30);
        
        // Initialize the (parent) root cell
        this.setSize((float)(averageLength * averageLength) / 20);
        this.setSpeedBounds(averageLength);
        this.setFillColor(new Color(164, 0, 164, 48));
        
        // Create and initialize all of the Tentacles
        _flagella = new Flagellum[random(7, 24)];
        
        // smaller girth = more compact organisms
        // larger girth  = more loosely connected organisms
        // All tentacles will have a similar girth, providing some degree of 
        // uniformity to the Octopus
        float connectedness = 1.85f + 1.8f * random();
        
        for(int i = 0; i < _flagella.length; i++) {
            _flagella[i] = new Flagellum(container, x, y, averageLength, 
                    connectedness, variation, this);
        }
    }
    
    public boolean update() {
        // Update position and orientation of Head Cell
        super.update();
        
        for(int i = 0; i < _flagella.length; i++) {
            _flagella[i].update();//, _theta + (float)(Math.PI * i));
        }
        
        return true;
    }
    

    public void setVariation(Variation variation) {
        for(int i = 0; i < _flagella.length; i++) {
            _flagella[i].setVariation(variation);
        }
    }
    
    public void paint(Graphics2D brush) {
        // Paint the head Cell first
        super.paint(brush);
        
        for(int i = 0; i < _flagella.length; i++) {
            _flagella[i].paint(brush);
        }
    }
}

package primordial;
/**
 * @author Travis Fischer
 * @date   12/25/2006
 * 
 *   Models a multi-celled Flagella
 */
import static fisch.Utilities.*;
import javax.swing.JPanel;
import java.awt.*;

public class Flagellum extends Cell {
    // Makes the Flagellum rhythmically undulate
    protected float _muscularFrequency, _noMuscles, _muscleRange;
    
    // Constituting the body of the Flagellum
    protected Cell[] _cells;
    
    public Flagellum(JPanel container) {
        this(container, 0, 0);
    }
    
    public Flagellum(JPanel container, float x, float y) {
        this(container, x, y, Variation.PRIMORDIAL_DEFAULT);
    }
    
    public Flagellum(JPanel container, float x, float y, Variation variation) {
        this(container, x, y, random(16, 30), 1.85f + 1.8f * random(), 
                variation, null);
    }

    public Flagellum(JPanel container, int length, Variation variation) {
        this(container, 0, 0, length, 1.85f + 1.8f * random(), variation, null);
    }
    
    public Flagellum(JPanel container, int length, float connectedness,  
            Variation variation, Cell head) {
        this(container, 0, 0, length, connectedness, variation, head);
    }
    
    public Flagellum(JPanel container, float x, float y, int length, 
            Variation variation) {
        this(container, x, y, length, 1.85f + 1.8f * random(), variation, null);
    }
    
    public Flagellum(JPanel container, float x, float y, int length, 
            float connectedness, Variation variation, Cell head) {
        super(container, x, y, (float)(length * length) / 20);
        
        // smaller girth = more compact organisms
        // larger girth  = more loosely connected organisms
//        float connectedness = 1.85f + 1.8f * random();
        
        // How often organism will undulate
        _muscularFrequency = 0.1f + 0.4f * random();
        _noMuscles = 0;
        
        // How prevalent the undulation will be (greater = more undulation)
        _muscleRange = (float)((8 - random(0, 16)) * Math.PI / 180);
        
        length += random(-3, 3);
        if (length < 16) length = 16;
        else if (length > 30) length = 30;
        _cells = new Cell[length];//random(16, 30)];
        
        this.setSpeedBounds(length);
        
        // _cells[0] will point to the Head of the Flagellum
        if (head != null)
            _cells[0] = head;
        else _cells[0] = this;
        
        for(int i = 1; i < _cells.length; i++) {
            int j = (_cells.length - i);
            float size  = (float)(j * j) / 20;
            float girth = size * connectedness / (j * 0.2f);
            
            _cells[i] = new Cell(container, x, y, size, girth);
        }
        
        this.setVariation(variation);
    }
    
    public boolean update() {
        // Update position and orientation of root Cell
        // which may or may not be the Head Cell 
        // (ex: it's not the Head for the Octopus)
        super.update();
        
        // Randomly pulse 2nd 'muscle' node
        _noMuscles += _muscularFrequency;
        double muscleTheta = _theta + _muscleRange * Math.sin(_noMuscles);
        
        // Make 2nd 'muscle' node follow head node (_cells[0] is the head)
        _cells[1]._x = _cells[0]._x - (float)(_cells[0].getSize() * Math.cos(muscleTheta));
        _cells[1]._y = _cells[0]._y - (float)(_cells[0].getSize() * Math.sin(muscleTheta));
        
        // Apply kinetic forces throughout body
        for(int i = 2; i < _cells.length; i++) {
            float dx = _cells[i]._x - _cells[i - 2]._x;
            float dy = _cells[i]._y - _cells[i - 2]._y;
            
            float dist  = (float)Math.sqrt(dx * dx + dy * dy);
            float girth = _cells[i].getGirth();
            
            _cells[i].setLocation(_cells[i - 1]._x + (dx * girth) / dist, 
                    _cells[i - 1]._y + (dy * girth) / dist);
        }
        
        return true;
    }
    
    public void setVariation(Variation variation) {
        super.setVariation(variation);
        
        if (variation == Variation.PRIMORDIAL_DEFAULT) {
            for(int i = 1; i < _cells.length; i++)
                _cells[i].setVariation(variation);
            
            // Make head of organism light purple
            _cells[1].setFillColor(new Color(164, 0, 164, 18));
        } else if (variation == Variation.PRIMORDIAL_SNAKESKIN) {
            for(int i = 1; i < _cells.length; i++)
                _cells[i].setVariation(variation);
            
            // Make head of organism light blue
            _cells[1].setFillColor(new Color(0x88, 0xAC, 0xAC, 18));
        }
    }
    
    public void paint(Graphics2D brush) {
        for(int i = 1; i < _cells.length; i++)
            _cells[i].paint(brush);
    }
}

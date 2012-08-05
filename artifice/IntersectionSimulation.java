package artifice;
/* IntersectionSimulation.java
 * 
 * Displays the aggregate intersections of Circles
 * 
 * (the following concept comes from Casey Reas http://reas.com/ )
 * A surface filled with 100 medium to small sized circles. 
 * Each circle has a different size and direction, 
 * but moves at the same slow rate. Display:
 *  A. The instantaneous intersections of the circles.
 *>>B. The aggregate intersections of the circles.<<
 * 
 * @see http://reas.com/iperimage.php?section=works&work=preprocess_s&images=4&id=1&bgcolor=FFFFFF
 * @see http://www.complexification.net/gallery/machines/interAggregate/index.php
 * 
 * @author Travis Fischer (tfischer)
 * @version January 6, 2006
 */
import static artifice.ArtificeConstants.*;

import java.awt.image.*;
import java.awt.*;

public class IntersectionSimulation extends Simulation {
    private boolean _randomRadius;
    private Circle[] _circles;
    private int _noCircles;
    private int _variation;
    private int _movement;
    private int _radius;
    
    public IntersectionSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer);
        
//        ArtificeConstants.extractColorsFromImage(dp, "Images/intersectionPalette.png");
        
        _noCircles = 100;
        _circles   = new Circle[_noCircles];
        _radius    = DEFAULT_CIRCLE_RADIUS;
        _randomRadius = true;
        _variation = 0;
        _movement  = 0;
    }
    
    public final void reset() {
        for(int i = 0; i < _circles.length; i++) {
            int x = random(0, SIMULATION_WIDTH - 1);
            int y = random(0, SIMULATION_HEIGHT - 1);
            int radius = _radius;
            if (_randomRadius)
                radius = random(MIN_CIRCLE_RADIUS, MAX_CIRCLE_RADIUS);
            
            _circles[i] = new Circle(x, y, radius, 
                    randomColor(INTERSECTION_PALETTE, random(2, 30)), _movement);
        }
        
        // Clear the offscreen buffer to white
        Graphics2D brush = (Graphics2D)_offscreen.getGraphics();
        brush.setColor(Color.WHITE);
        brush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
    }
    
    public void update() {
        for(int i = 0; i < _circles.length; i++)
            _circles[i].update();
        
        if (_variation != 2) {
            // O(N^2)  TODO:  Optimize to O(NlogN)
            for(int i = 0; i < _circles.length - 1; i++) {
                int diameter = _circles[i].getDiameter();
                for(int j = i + 1; j < _circles.length; j++) {
                    int c = _circles[i].getColor();
                    
                    if (_variation != 1) {
                        Point[] intersections = _circles[i].getIntersection(_circles[j]);
                        if (intersections != null && intersections.length == 2) {
                            int difX = intersections[1].x - intersections[0].x;
                            int difY = intersections[1].y - intersections[0].y;
                            int dist = (int)Math.sqrt(difX * difX + difY * difY);
                            int diameter2 = _circles[j].getDiameter(); 
                            // want the smaller of the two diameters
                            int d = (diameter > diameter2 ? diameter2 : diameter);
                            
                            int alpha;
                            if (_variation == 0)
                                alpha = 32 - ((dist << 5) / d);
                            else alpha = 128 - (int)((dist << 8) / d);
                            
                            if (alpha > 255) alpha = 255;
                            else if (alpha < 0) alpha = 255 + alpha;
                            
                            if (_variation == 0)
                                c = (c & 0x00FFFFFF) | (alpha << 24);
                            else c = 0x18000000 | (alpha << 16) | (alpha << 8) | alpha;
                            
                            // draw a line between the two points of intersection
                            this.drawClippedLine(intersections[0].x, intersections[0].y, 
                                    intersections[1].x, intersections[1].y, c);
                        }
                    } else if (_circles[i].intersects(_circles[j])) {
                        // draw a line connecting the centers of the two 
                        // intersecting circles (_variation == 1)
                        this.drawClippedLine((int)_circles[i].getX(), 
                                (int)_circles[i].getY(), (int)_circles[j].getX(), 
                                (int)_circles[j].getY(), c);
                    }
                }
            }
        }
        
        this.repaint();
    }
    
    public void setRadius(int radius) {
        if (_randomRadius || _radius != radius) {
            _randomRadius = false;
            _radius = radius;
            
            this.resetRadii();
        }
    }
    
    public void setRandomRadius() {
        if (!_randomRadius) {
            _randomRadius = true;
            
            this.resetRadii();
        }
    }
    
    public void resetRadii() {
        if (_randomRadius) {
            for(int i = 0; i < _circles.length; i++)
                _circles[i].setRadius(random(MIN_CIRCLE_RADIUS, MAX_CIRCLE_RADIUS));
        } else {
            for(int i = 0; i < _circles.length; i++)
                _circles[i].setRadius(_radius);
        }
    }
    
    public void setVariation(int variation) {
        if (_variation != variation)
            _variation = variation;
    }
    
    public void setMovement(int movement) {
        if (_movement != movement) {
            _movement = movement;
            
            for(int i = 0; i < _circles.length; i++)
                _circles[i].setMovement(_movement);
        }
    }
    
    public void paintSimulation(Graphics2D brush) {
        if (_variation != 2 && _offscreen != null) {
            brush.drawImage(_offscreen, 0, 0, _dp);
        } else if (_variation == 2) {
            setAntialiasing(brush, true);
            brush.setStroke(new BasicStroke(1));
            brush.setColor(new Color(0, 0, 0, 100));
            
            for(int i = 0; i < _circles.length; i++)
                _circles[i].paint(brush);
            
            brush.setColor(Color.BLACK);
            int size = 4;
            int half = (size >> 1);
            
            // Draw the intersections between all circles
            for(int i = 0; i < _circles.length - 1; i++) {
                for(int j = i + 1; j < _circles.length; j++) {
                    Point[] intersections = _circles[i].getIntersection(_circles[j]);
                    
                    if (intersections != null) {
                        // draw a line connecting the two points of intersection
                        if (intersections.length == 2)
                            brush.drawLine(intersections[0].x, intersections[0].y, 
                                intersections[1].x, intersections[1].y);
                        
                        // draw the actual point(s) of intersection
                        for(int a = 0; a < intersections.length; a++) {
                            brush.fillOval(intersections[a].x - half, 
                                    intersections[a].y - half, size, size);
                        }
                    }
                }
            }
        }
    }
    
    /* RGB triples constituting a list of predefined colors
     * taken from "Images/intersectionPalette.png"
     * 
     * @see method Simulation.randomColor
     */
    private static final int INTERSECTION_PALETTE[] = {
        255,255,255, 255,255,255, 255,255,255, 
        37, 29, 23, 51, 45, 39, 57, 49, 41, 71, 57, 42, 90, 74, 52, 
        100, 76, 55, 90, 85, 73, 104, 97, 77, 116, 104, 88, 123, 122, 119, 
        132, 120, 106, 147, 121, 83, 140, 129, 107, 161, 141, 117, 154, 139, 119, 
        141, 144, 139, 154, 148, 141, 176, 158, 146, 182, 168, 141, 187, 176, 140, 
        187, 179, 157, 189, 184, 165, 195, 184, 170, 202, 190, 173, 218, 209, 188, 
        213, 211, 196, 211, 218, 202, 220, 220, 219, 221, 221, 224, 43, 36, 25, 
        117, 108, 101, 137, 119, 89, 171, 158, 146, 181, 168, 145, 186, 179, 163, 
        220, 205, 186, 48, 31, 15, 48, 30, 20, 49, 46, 49, 58, 56, 52, 
        69, 59, 53, 102, 83, 46, 92, 90, 88, 99, 98, 88, 109, 112, 107, 
        125, 125, 129, 128, 110, 98, 133, 125, 119, 141, 137, 120, 167, 147, 109, 
        141, 145, 146, 153, 150, 149, 163, 158, 161, 179, 170, 155, 186, 180, 171, 
        189, 184, 173, 195, 188, 181, 204, 190, 178, 226, 196, 172, 220, 211, 195, 
        221, 217, 212, 222, 224, 221, 81, 62, 43, 102, 86, 56, 117, 100, 75, 
        126, 128, 123, 169, 152, 118, 147, 141, 135, 163, 158, 171, 170, 166, 163, 
        172, 169, 162, 183, 180, 177, 187, 183, 178, 211, 193, 172, 226, 204, 181, 
        217, 211, 203, 94, 96, 93, 150, 145, 123, 163, 159, 176, 167, 163, 169, 
        208, 189, 168, 223, 207, 196, 222, 225, 226, 34, 30, 32, 42, 40, 38, 
        80, 59, 49, 113, 79, 57, 122, 114, 102, 125, 128, 129, 145, 125, 106, 
        153, 137, 108, 177, 150, 106, 147, 141, 144, 169, 160, 141, 171, 171, 176, 
        187, 188, 187, 208, 189, 178, 211, 197, 181, 226, 210, 186, 225, 212, 195, 
        69, 64, 55, 114, 86, 58, 125, 113, 82, 172, 173, 174, 189, 192, 190, 
        202, 189, 184, 211, 196, 185, 232, 213, 186, 220, 216, 202, 42, 33, 13, 
        61, 61, 65, 67, 62, 65, 98, 79, 65, 124, 113, 74, 129, 101, 73, 
        131, 101, 60, 181, 154, 119, 165, 155, 145, 174, 176, 173, 194, 189, 193, 
        204, 196, 187, 242, 217, 186, 230, 216, 196, 226, 218, 203, 62, 64, 45, 
        73, 65, 40, 109, 97, 60, 135, 112, 61, 158, 146, 109, 173, 160, 118, 
        166, 163, 153, 196, 193, 158, 203, 195, 170, 212, 205, 196, 211, 211, 211, 
        226, 219, 212, 53, 40, 25, 83, 69, 26, 158, 160, 155, 196, 194, 142, 
        202, 199, 197, 206, 205, 205, 226, 222, 216, 52, 35, 11, 61, 64, 61, 
        118, 98, 60, 102, 87, 73, 105, 102, 101, 128, 93, 71, 148, 116, 76, 
        161, 125, 81, 177, 157, 134, 157, 156, 161, 158, 160, 160, 177, 172, 163, 
        190, 169, 142, 193, 170, 140, 205, 195, 159, 205, 205, 208, 213, 214, 216, 
        226, 222, 225, 92, 80, 56, 163, 141, 103, 178, 172, 169, 190, 170, 147, 
        211, 197, 156, 62, 65, 66, 76, 74, 69, 108, 109, 113, 166, 135, 77, 
        180, 161, 107, 178, 174, 177, 187, 172, 155, 194, 174, 151, 212, 200, 168, 
        206, 208, 203, 214, 216, 212, 233, 225, 204, 56, 48, 29, 66, 46, 35, 
        77, 77, 80, 101, 92, 86, 133, 109, 86, 168, 139, 90, 184, 162, 122, 
        179, 176, 165, 194, 177, 144, 196, 178, 141, 204, 196, 180, 205, 208, 208, 
        214, 216, 216, 231, 225, 216, 65, 44, 28, 84, 67, 41, 72, 52, 28, 
        77, 80, 76, 109, 113, 114, 134, 116, 72, 140, 129, 77, 174, 145, 94, 
        192, 159, 124, 160, 142, 131, 166, 154, 137, 180, 178, 172, 194, 179, 156, 
        212, 209, 203, 217, 213, 211, 234, 223, 205, 240, 227, 204, 140, 129, 87, 
        176, 145, 93, 193, 166, 121, 202, 182, 150, 211, 205, 204, 235, 225, 212, 
        241, 229, 212, 44, 45, 49, 84, 54, 30, 89, 80, 39, 84, 76, 68, 
        77, 81, 82, 179, 143, 92, 182, 184, 172, 206, 184, 147, 211, 185, 143, 
        211, 206, 209, 218, 214, 217, 236, 227, 218, 242, 235, 220, 45, 48, 45, 
        103, 93, 96, 147, 130, 79, 178, 163, 140, 205, 185, 155, 210, 187, 153, 
        211, 202, 186, 237, 232, 219, 139, 137, 135, 201, 182, 158, 212, 202, 182, 
        45, 49, 50, 64, 44, 53, 98, 73, 45, 82, 78, 80, 119, 94, 100, 
        150, 131, 85, 140, 140, 145, 187, 174, 164, 193, 174, 165, 217, 208, 175, 
        215, 208, 183, 229, 228, 226, 233, 231, 229,   
    };
}

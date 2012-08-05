package artifice;
import static artifice.ArtificeConstants.*;

import java.awt.image.*;
import java.awt.*;

public class Crack {
    public static final int[][] _setCracks = new int[SIMULATION_HEIGHT + 1][SIMULATION_WIDTH + 1];
    private static float _chanceOfCurved = 0;
    
    // Containing SubstratePanel
    private static BufferedImage _container;
//    private static WritableRaster _raster;
    
    // Position
    private float _x, _initialX;
    private float _y, _initialY;
    
    // Direction specified in degrees
    private int _theta;
    private float _dx, _dy;
    
    private float _sandRand, _scale, _radiusScale;
    private boolean _curved;
    
    private int _color, _length;
    
    public static void setCanvas(BufferedImage canvas) {
        _container = canvas;
//        _raster = canvas.getRaster();
    }
    
    public static void setChanceOfCurved(float percentChance) {
        _chanceOfCurved = (percentChance / 100);
    }
    
    // Initializes a completely randomized Crack
    public Crack(SubstrateSimulation container) {
        this(random(0, _container.getWidth()), random(0, _container.getHeight()), 
             random(0, 359));
    }
    
    // Initializes a given Crack with a random Color
    public Crack(float x, float y, int theta) {
        
        // Initialize geometric data
        _initialX  = x;
        _initialY  = y;
        _x         = x;
        _y         = y;
        _length    = 0;
        
        _sandRand  = (float)(Math.random() * 0.1); // 0 <= _sandRand <= 0.1
        
        _curved = (_chanceOfCurved > Math.random());
        if (_curved) {
            // Make particle's path elliptical instead of circular
            _scale  = (float)(Math.PI * Math.random() / 4);
            
            // Make particle curve slowly/gradually
            _radiusScale = (float)(Math.PI / ((180 - 50 + random(0, 100)) << 3));
            _dx     = 0.4f * (1 - 2 * random(0, 1));
            _dy     = 0.4f * (1 - 2 * random(0, 1));
            _theta  = random(-2000000, -1000);
        } else { // Crack is a line so it's Velocity <_dx, _dy> will be constant
            _theta     = (theta + 2 - random(0, 4)) % 360;
            _dx        = (float)(0.42f * Math.cos(_theta * (Math.PI / 180)));
            _dy        = (float)(0.42f * Math.sin(_theta * (Math.PI / 180)));
        }
        
        // Initialize Crack to have a pseudo-random color within the predefined color scheme
        int offset = 3 * random(0, (SUBSTRATE_COLORS.length - 1) / 3);
        _color = ((SUBSTRATE_COLORS[offset] & 0xFF) << 16) | 
                 ((SUBSTRATE_COLORS[offset + 1] & 0xFF) << 8) | 
                  (SUBSTRATE_COLORS[offset + 2] & 0xFF);
    }
    
    public boolean update() {
        float dX = _dx, dY = _dy;
        
        // Update velocity of curved particles
        if (_curved) {
            // _length acts as time
            float angle = _length * _radiusScale;
            
            dX = _dx * (float)Math.cos(angle);
            dY = _dy * (float)Math.sin(angle + _scale);
        }
        
        // Update particle's position
        _x += dX;
        _y += dY;

        int realX = Math.round(_x), realY = Math.round(_y);
        
        // Make line "fuzzy" to prevent the uniform 
        // aliases which are otherwise produced
        int x = (int)(_x + FUZZ - Math.random() * TWICE_FUZZ);
        int y = (int)(_y + FUZZ - Math.random() * TWICE_FUZZ);
        
        // Check if it's time for this particle to die
        if (!this.isValid(x, y, _length) || !this.isValid(realX, realY, _length++))
            return false;
        
        this.paintSand(x, y, dX, dY);
        _setCracks[y][x] = _theta;
        
        _container.setRGB(x, y, 0xff000000);
        
//        _raster.setPixel(x, y, new int[] { 0, 0, 0, 0xff });
        
        _setCracks[realY][realX] = _theta;
//      _container.setRGB(realX, realY, 0xff000000);
        
        return true;
    }
    
    // Blends a src ARGB pixel onto a destination RGB pixel
    private static void setPixel(int x, int y, int color) {
//        int[] array = new int[4]; // Rasterized test version
//        int oldColor[] = _raster.getPixel(x, y, array);
//      _raster.setPixel(x, y, new int[] { r, g, b, alpha });
        int oldColor = _container.getRGB(x, y);
        int alpha = (color >>> 24);
        
        // Blend the two colors together, weighting their relative contributions
        // according to the new color's alpha value
        int k = (255 - alpha); // / 256
        int r = ((alpha * ((color & 0x00FF0000) >>> 16) + 
                k * ((oldColor & 0x00FF0000) >> 16)) << 8) & 0x00FF0000;
        int g =  (alpha * ((color & 0x0000FF00) >>>  8) + 
                k * ((oldColor & 0x0000FF00) >>  8)) & 0x0000FF00;
        int b =  (alpha *  (color & 0x000000FF)         + 
                k * ((oldColor & 0x000000FF)      )) >> 8;
        
        _container.setRGB(x, y, (r | g | b));
    }
    

    // Blends a src ARGB pixel onto a destination ARGB pixel
//    private static void setPixel(int x, int y, int color) {
////        int[] array = new int[4];
////        int oldColor[] = _raster.getPixel(x, y, array);
//        int oldColor = _container.getRGB(x, y);
//        int oldAlpha = (oldColor >>> 24);
//        
//        int alpha = (color >>> 24);
//        int r     = ((color & 0x00FF0000) >>> 16);
//        int g     = ((color & 0x0000FF00) >>>  8);
//        int b     =  (color & 0x000000FF);
//        
//        // Only blend if the destination has some color
//        if (oldAlpha != 0) {
//            int k = (oldAlpha - ((alpha * oldAlpha) >>> 8)); // / 256
//            int a = cap(alpha + k);
//            r = cap((alpha * r + k * ((oldColor & 0x00FF0000) >> 16)) / a);
//            g = cap((alpha * g + k * ((oldColor & 0x0000FF00) >>  8)) / a);
//            b = cap((alpha * b + k * ((oldColor & 0x000000FF)      )) / a);
////            r = cap((alpha * r + k * oldColor[0]/*((oldColor & 0x00FF0000) >> 16)*/) / a);
////            g = cap((alpha * g + k * oldColor[1]/*((oldColor & 0x0000FF00) >>  8)*/) / a);
////            b = cap((alpha * b + k * oldColor[2]/*((oldColor & 0x000000FF)      )*/) / a);
//            alpha = a;
//        }
//        
//        int newColor = (alpha << 24) | (r << 16) | (g << 8) | b;
//        
//        _container.setRGB(x, y, newColor);
////        _raster.setPixel(x, y, new int[] { r, g, b, alpha });
//    }
    
    public void paintSand(int oldX, int oldY, float dX, float dY) {
        float sandX  = oldX,   sandY  = oldY;
        float sandDx = dY / 2, sandDy = dX / 2;
        int length = -3;
        
        do {
            // Sand moves perpendicular to Crack
            sandX += sandDx;
            sandY -= sandDy;
        } while(isValid((int)sandX, (int)sandY, length++));
        
        sandX = (sandX - sandDx - _x);
        sandY = (sandY + sandDy - _y);
        
        // Modulate length of sand
        float modulation = 0.01f + (float)(Math.abs(sandX)) * (1.0f / (50*SIMULATION_WIDTH)) 
            + (Math.abs(sandY)) * (1.0f / (50*SIMULATION_HEIGHT));
        
        _sandRand += modulation - Math.random() * modulation * 2; // 0.02
        
        // Cap length of sand
        if (_sandRand < 0.3f)
            _sandRand = 0.3f;
        if (_sandRand > 1)
            _sandRand = 1;
        
        int noGrains = 128;
        
        // Paint sand (pixels w/ varying levels of transparency)
        float wAdd = _sandRand / (noGrains - 1);
        float w    = 0;
        
        float startX = _x + 0.15f - random() * 0.3f;
        float startY = _y + 0.15f - random() * 0.3f;
        
        for(int i = 0; i < noGrains; i++, w += wAdd) {
            float sinSinW = (float)Math.sin(Math.sin(w));
            int x = Math.round(startX + sandX * sinSinW);
            int y = Math.round(startY + sandY * sinSinW);
            
            if (x != oldX || y != oldY) {
//                int alpha = (int)(64 * ((double)(noGrains - i) / noGrains));
                int alpha = cap((int)(256 * (0.1f - ((float)i) / (noGrains << 4))));
                
                if (x > 0 && y > 0 && x < SIMULATION_WIDTH && y < SIMULATION_HEIGHT)
                    this.setPixel(x, y, (alpha << 24) | _color);
                
                oldX = x;
                oldY = y;
            }
        }
    }
 
    private static final int cap(int color) {
        return (color >= 255 ? 255 : (color <= 0 ? 0 : color));
    }
    
    protected boolean isValid(int x, int y, int length) {
        return (x > 0 && x < SIMULATION_WIDTH - 1 && 
                y > 0 && y < SIMULATION_HEIGHT - 1 &&  
                (length <= 1 || _setCracks[y][x] > 361 || _setCracks[y][x] == _theta));
    }
    
    public Crack spawnNewCrack(int spawnType) {
        boolean randBoolean = randomBoolean();
        int newTheta = _theta;
        
        float newX = _initialX, newY = _initialY;
        if (_curved) {
            int t = random(0, _length);
            
            float angle = 0;
            for(int i = 0; i < t; i++, angle += _radiusScale) {
                
                newX += _dx * (float)Math.cos(angle);
                newY += _dy * (float)Math.sin(angle + _scale);
            }
            
            // Get angle of curve's tangent vector at spawn point
            newTheta = (int)(angle * (180 / Math.PI));
        } else {
            float t = (float)(Math.random() * _length);
            
            newX += t * _dx;
            newY += t * _dy;
        }
        
        if (spawnType == SPAWN_LINEAR)
            newTheta += (randBoolean ? 90 : 270);
        else newTheta += (randBoolean ? random(5, 174) : random(185, 354));
        
        return new Crack(newX, newY, newTheta);
    }
    
    public int getLength() {
        return _length;
    }
}

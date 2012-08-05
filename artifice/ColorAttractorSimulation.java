package artifice;
/* ColorAttractorSimulation.java
 * 
 * Peter De Jong Attractors
 * 
 * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/peterdejong/
 * @author Travis Fischer (tfischer)
 * @date January 13, 2006
 */
import static artifice.ArtificeConstants.*;
import java.awt.*;

public class ColorAttractorSimulation extends Simulation {
    private int _defaultHorizontalScale;
    private int _defaultVerticalScale;
    
    private Attractor _attractor;
    private boolean _done;
    
    public ColorAttractorSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer);
        
//        ArtificeConstants.extractColorsFromImage(_dp, "Images/hairPalette.png");
        
        _attractor = null;
    }
    
    public final void reset() {
        _defaultHorizontalScale = _offscreen.getWidth()  >> 2;
        _defaultVerticalScale   = _offscreen.getHeight() >> 2;
        
        _done = false;
        
        // Clear the offscreen buffer to white
        Graphics2D brush = (Graphics2D)_offscreen.getGraphics();
        brush.setColor(Color.WHITE);
        brush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        _attractor = new Attractor();
    }
    
    public void update() {
        if (_done) { this.repaint(); return; }
        for(int i = 0; i < 100000; i++)
            _attractor.update();
        
//        _attractor.modifyParameters();
        
        double p = _attractor._maxHits;
        
        //K = (3 / 4)(1 ? t2) 
        for(int i = 0; i < _offscreen.getHeight(); i++) {
            for(int j = 0; j < _offscreen.getWidth(); j++) {
                float brightness = (float)Math.pow(_attractor._hits[i][j], 2) / _attractor._maxHits;
//                System.err.println(brightness);
                if (brightness > 1) brightness = 1;
                int color = (new Color(brightness, brightness, brightness)).getRGB();
//                int color = Color.HSBtoRGB(_attractor._hues[i][j], 
//                    _attractor._saturations[i][j], brightness);
                
//                System.out.println(color);
                
                //hit_count^power / max_hit_count^power * 255
                _attractor.setPixel(j, i, 0xff000000 | color);
            }
        }
        _done = true;
        
        this.repaint();
    }
    
    private class Attractor implements Updateable {
        // Particle Position
        protected float _x, _y;
        
        // Attraction parameters
        protected float _a, _b, _c, _d;
        
        protected int _horizontalScale, _verticalScale;
        
        public Attractor() {
            this(randomSigned(3f), 
                 randomSigned(3f), 
                 randomSigned(3f), 
                 randomSigned(3f));
        }
        
        public Attractor(float a, float b, float c, float d) {
            this(a, b, c, d, _defaultHorizontalScale, _defaultVerticalScale);
        }
        
        public Attractor(float a, float b, float c, float d, 
                int horizontalScale, int verticalScale) {
            super();
            
            _a = a;
            _b = b;
            _c = c;
            _d = d;
            
            //System.out.println(_a + ", " + _b + ", " + _c + ", " + _d);
            
            _x = 0;
            _y = 0;
            
            _horizontalScale = horizontalScale;
            _verticalScale   = verticalScale;
            
            _maxHits = 0;
            _hits = new int[_offscreen.getHeight()][_offscreen.getWidth()];
            _hues = new float[_offscreen.getHeight()][_offscreen.getWidth()];
            _saturations = new float[_offscreen.getHeight()][_offscreen.getWidth()];
            for(int i = 0; i < _hits.length; i++) {
                for(int j = 0; j < _hits[i].length; j++) {
                    _hits[i][j] = 0;
                    _hues[i][j] = 0;
                    _saturations[i][j] = 0;
                }
            }
        }
        
        public void modifyParameters() {
            _a += randomSigned(0.05f);
            _b += randomSigned(0.05f);
            _c += randomSigned(0.05f);
            _d += randomSigned(0.05f);
        }

        private float _dX, _dY;
        public int[][] _hits;
        public float[][] _hues;
        public float[][] _saturations;
        public int _maxHits;
        
        public boolean update() {
            float x = _x, y = _y;
            
            // Xn+1 = sin(a * Yn) - cos(b * Xn)
            // Yn+1 = sin(c * Xn) - cos(d * Yn)
            _x = (float)(Math.sin(_a * _y) - Math.cos(_b *  x));
            _y = (float)(Math.sin(_c * x)  - Math.cos(_d * _y));
            
            
            float dX = _x - x, dY = _y - y;
            
            float hue = (float)Math.atan2(dY, dX) / PI;
            float saturation = (float)Math.atan2(dY - _dY, dX - _dX) / PI;
            
            //hit_count^power / max_hit_count^power * 255
            int screenX = PARTICLE_ORIGIN_X + Math.round(_x * _horizontalScale);
            int screenY = PARTICLE_ORIGIN_Y + Math.round(_y * _verticalScale);
            
            if (isValid(screenX, screenY)) {
//                int color = Color.HSBtoRGB(hue, saturation, ++_hits[screenY][screenX]);
                
                int no = _hits[screenY][screenX];
                _hues[screenY][screenX] = (no * _hues[screenY][screenX] + hue) / (no + 1);
                _saturations[screenY][screenX] = (no * _saturations[screenY][screenX] + saturation) / (no + 1);
                if (++_hits[screenY][screenX] > _maxHits)
                    _maxHits = _hits[screenY][screenX];
                
//                setPixel(screenX, screenY, color);
            }
            
            _dX = dX; _dY = dY;
            
            return true;
        }
        
        // Blends a src ARGB pixel onto a destination RGB pixel
        public void setPixel(int x, int y, int color) {
            int oldColor = _offscreen.getRGB(x, y);
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
            
            _offscreen.setRGB(x, y, (r | g | b));
        }
    }
}
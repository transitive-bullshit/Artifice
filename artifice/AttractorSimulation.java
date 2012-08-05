package artifice;
/* AttractorSimulation.java
 * 
 * Peter De Jong, Clifford, and Ikeda Attractors
 * as well as the Julia star fractal and Lissajous Curve
 * 
 * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/peterdejong/
 * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/clifford/
 * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/starjulia/
 * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/ifs_leaf_b/
 * @see http://www.xahlee.org/SpecialPlaneCurves_dir/Lissajous_dir/lissajous.html
 * @see http://en.wikipedia.org/wiki/Lissajous_figure
 * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/ikeda/
 * @see http://www.splintered.co.uk/experiments/archives/ikeda_attractor_sketchboard/
 * @author Travis Fischer (tfischer)
 * @date January 8-13, 2006
 */
import static artifice.ArtificeConstants.*;
import java.awt.*;

//TODO: color based on velocity and/or curvature (use dot product)

public class AttractorSimulation extends Simulation {
    private static final int BLACK = 0x30000000;
    private static final int WHITE = 0x30ffffff;
    private int _defaultHorizontalScale;
    private int _defaultVerticalScale;
    
    private Attractor _attractor;
    private int _count;
    private int _color;
    // Options set vial AttractorControl
    private int _attractorType;
    private int _variation;
    
    public AttractorSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer);
        
//        ArtificeConstants.extractColorsFromImage(_dp, "Images/hairPalette.png");
        
        _attractor = null;
        _attractorType = 0; // DeJong Attractor
        _variation = 0; // Black on White
    }
    
    public final void reset() {
        _defaultHorizontalScale = _offscreen.getWidth()  >> 2;
        _defaultVerticalScale   = _offscreen.getHeight() >> 2;
        
        _count = 0;
        _color = (_variation == 0 ? WHITE : BLACK);
        
        // Clear the offscreen buffer to white
        Graphics2D brush = (Graphics2D)_offscreen.getGraphics();
        brush.setColor(_variation == 0 ? Color.BLACK : Color.WHITE);
        brush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        this.resetAttractor();
    }

    // Attractor Factory which creates the type of Attractor 
    // currently selected by the user
    private final void resetAttractor() {
        switch(_attractorType) {
        case 0:
            _attractor = new DeJongAttractor();
            break;
        case 1:
            _attractor = new RingAttractor();
            break;
        case 2:
            _attractor = new CliffordAttractor();
            break;
        case 3:
            _attractor = new LissajousCurve();
            break;
        case 4:
            _attractor = new IkedaAttractor();
            break;
        case 5:
            _attractor = new JuliaFractal();
            // _attractor = new LorenzAttractor();
            break;
        case 6:
            _attractor = new HenonPhase();
            break;
        default:
            break;
        }
    }
    
    public void update() {
        for(int i = 0; i < 1000; i++)
            _attractor.update();
        
        _attractor.modifyParameters();
        if (_count++ > 1000) {
            _count = 0;
            
            if (_color == WHITE)
                _color = BLACK;
            else _color = WHITE;
        }
        
        this.repaint();
    }
    
    public void setVariation(int variation) {
        if (_variation != variation) {
            _variation = variation;
            
            this.restart();
        }
    }
    
    public void setAttractorType(int type) {
//        if (_attractorType != type) {
        _attractorType = type;
        
        this.resetAttractor();
//        }
    }
    
    private abstract class Attractor implements Updateable {
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
        }
        
        public void modifyParameters() {
            _a += randomSigned(0.05f);
            _b += randomSigned(0.05f);
            _c += randomSigned(0.05f);
            _d += randomSigned(0.05f);
        }
        
        public abstract void move();
        
        public boolean update() {
//            int x = PARTICLE_ORIGIN_X + Math.round(_x * _horizontalScale);
//            int y = PARTICLE_ORIGIN_Y + Math.round(_y * _verticalScale);
            
            this.move();
            
            // System.out.println('(' + x + ", " + y + ")  ->  (" +_x + ", " + _y + ")" + "   ");
            
            int screenX = PARTICLE_ORIGIN_X + Math.round(_x * _horizontalScale);
            int screenY = PARTICLE_ORIGIN_Y + Math.round(_y * _verticalScale);
            
            if (isValid(screenX, screenY)) {
//                int color = randomColor(ATTRACTOR_PALETTE, random(10, 40)).getRGB();
                setPixel(screenX, screenY, _color);
                
//                drawClippedLine(x, y, screenX, screenY, _color);
            }
            
            return true;
        }
        
        // Blends a src ARGB pixel onto a destination RGB pixel
        protected void setPixel(int x, int y, int color) {
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
    
    private class DeJongAttractor extends Attractor {
        public DeJongAttractor() {
            this((randomBoolean() ? -1 : 1) * (1.4f + randomSigned(.6f)), 
                 (randomBoolean() ? -1 : 1) * (-2.3f + randomSigned(0.3f)), 
                 (randomBoolean() ? -1 : 1) * (2.6f + randomSigned(0.6f)), 
                 (randomBoolean() ? -1 : 1) * (-2.1f + randomSigned(1f)));
            
//            this(-1.85f, 1.48f, -1.55f, -1.87f);
        }
        
        public DeJongAttractor(float a, float b, float c, float d) {
            super(a, b, c, d);
        }
                
        public void move() {
            float x = _x;
            
            // Xn+1 = sin(a * Yn) - cos(b * Xn)
            // Yn+1 = sin(c * Xn) - cos(d * Yn)
            _x = (float)(Math.sin(_a * _y) - Math.cos(_b *  x));
            _y = (float)(Math.sin(_c * x)  - Math.cos(_d * _y));
        }
    }
    
    private class CliffordAttractor extends Attractor {
        public CliffordAttractor() {
            this(-1.35f + randomSigned(0.35f), 
                 randomBoolean() ? -1f + randomSigned(0.8f) : 1.7f + randomSigned(0.4f),  
                 (randomBoolean() ? -1 : 1) * random(0.5f, 1.8f), 
                 1.2f + randomSigned(0.5f));
            
            /* Known Configurations which produce interesting results:
             * -1.4f, 1.6f, 1.0f, 0.7
             * a = 1.1, b = -1.0, c = 1.0, d = 1.5
             * a = 1.6, b = -0.6, c = -1.2, d = 1.6
             * a = 1.7, b = 1.7, c = 0.06, d = 1.2
             * a = 1.3, b = 1.7, c = 0.5, d = 1.4
             * a = 1.5, b = -1.8, c = 1.6, d = 0.9
             * 
             * Known bad Configurations
             * -1.6346651, -0.29710782, 1.8077382, 1.2984563
             * -1.1098732, -1.6683505, -0.8322704, 1.6945715
             * -1.4157772, -1.1488563, 0.59418416, 1.0296066
             * 
             * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/clifford/
             */
        }
        
        public CliffordAttractor(float a, float b, float c, float d) {
            super(a, b, c, d);
        }
        
        public void move() {
            float x = _x;//, y = _y;
            
            // Xn+1 = sin(a * Yn) + c * cos(a * Xn)
            // Yn+1 = sin(b * Xn) + d * cos(b * Yn)
            _x = (float)(Math.sin(_a * _y) + _c * Math.cos(_a *  x));
            _y = (float)(Math.sin(_b * x)  + _d * Math.cos(_b * _y));
        }
    }
    
    private class RingAttractor extends Attractor {
        public RingAttractor() {
            this(1.4f + randomSigned(0.5f), 
                 1.56f + randomSigned(0.5f), 
                 1.4f + randomSigned(0.5f),  
                 -6.56f + randomSigned(0.8f));
            
            /* Known Configurations which produce interesting results:
             * a = 1.40, b = 1.56, c = 1.40, d = -6.56
             * 
             * @see http://local.wasp.uwa.edu.au/~pbourke/fractals/peterdejong/
             * @credits go to Johnny Svensson, whom I believe to be the discoverer of 
             * this incredible attractor
             */
        }
        
        public RingAttractor(float a, float b, float c, float d) {
            super(a, b, c, d, _defaultHorizontalScale >> 2, _offscreen.getHeight() / 6);
        }
        
        public void move() {
            float aMulX = _a * _x, bMulY = _b * _y;
            
            // Xn+1 = d * sin(a * Xn) - sin(b * Yn)
            // Yn+1 = c * cos(a * Xn) + cos(b * Yn)
            _x = (float)(_d * Math.sin(aMulX) - Math.sin(bMulY));
            _y = (float)(_c * Math.cos(aMulX) + Math.cos(bMulY));
        }
    }
    
    // TODO: Incorporate Lorenz Attractor
    // LorenzAttractor is really a 3D attractor and doesn't produce 
    // too interesting of results in 2D
//    private class LorenzAttractor extends Attractor {
//        protected float _z;
//        
//        public LorenzAttractor() {
//            this(10f, 28f, 8f / 3f, 0.01f);
////            this(28f, 46.92f, 4f, 0.01f);
//        }
//        
//        // @see http://local.wasp.uwa.edu.au/~pbourke/fractals/lorenz/
//        public LorenzAttractor(float a, float b, float c, float d) {
//            super(a, b, c, d);
//            
//            _x = 0.01f;
//            _z = 0;
//        }
//        
//        public void modifyParameters() {
////            _a += randomSigned(0.001f);
////            _b += randomSigned(0.001f);
////            _c += randomSigned(0.001f);
////            _d += randomSigned(0.001f);
//        }
//        
//        public boolean update() {
//            this.move();
////            
////            float eyeX = 1, eyeY = 1, eyeZ = 1;
////            Matrix toOrigin = new Matrix(new float[] { 1,0,0,0,
////                    0,1,0,0, 0,0,1,0, -eyeX,-eyeY,-eyeZ,1 });
////            
////            
//            int screenX = PARTICLE_ORIGIN_X + Math.round(_x * _horizontalScale);
//            int screenY = PARTICLE_ORIGIN_Y + Math.round(_y * _verticalScale);
//            
////            System.out.println(new Point(_horizontalScale, _verticalScale));
//            
//            if (isValid(screenX, screenY)) {
////                int color = randomColor(ATTRACTOR_PALETTE, random(10, 40)).getRGB();
//                setPixel(screenX, screenY, _color);//0x30000000);
//            }
//            
//            return true;
//        }
//        
//        public void move() {
//            float x = _x, y = _y;
//            
//            _x += _d * _a * (y - x);
//            _y += _d * (x * (_b - _z) - y);
//            _z += _d * (x * y - _c * _z);
//        }
//        
//    }
    
    private class JuliaFractal extends Attractor {
        private int _type;
        private float _w;
        
        public JuliaFractal() {
            this(randomSign() * random(2f, 4.1f), 
                    randomSign() * random(0.8f, 1.2f), 
                    randomSign() * random(0.6f, 1.8f), 
                    random(0, 2));
        }
        
        // 
        // Fractals derived from the Julia Set
        // @see http://local.wasp.uwa.edu.au/~pbourke/fractals/starjulia/
        // 
        public JuliaFractal(float a, float b, float c, int type) {
            super(a, b, c, 0);
            
            _w = 0;
            _type = type;
        }
        
        public void modifyParameters() {
            _type = (_type + random(1, 2)) % 3;
            
            _a += randomSigned(0.05f);
            _b += randomSigned(0.033f);
            _c += randomSigned(0.01f);
            
            if (_type == 0) {
                _a = randomSign() * random(3.9f, 4.1f);//3 to 6
                _c = randomSign() * random(0.9f, 1.1f);//1 to 6
                
                _w = 0;
                _x = _y = 0;
            } else if (_type == 2) {
                _c = randomSign() * (random(0.5f, 1.3f));
                _a = randomSign() * (random(1.7f, 3f));
            }
            
//            System.out.println(_a + ", " + _b + ", " + _c);
        }
        
        public void move() {
            float xa = -_a * _x, ya = -_a * _y;// _a's default is 4
            
            switch(_type) {
            case 0:// my favorite with _a = 4 and _c = 1
                xa += (float)Math.cos(_a * _w);
                ya += (float)Math.sin(_a * _w);
                break;
            case 1:
                xa += 1;
                break;
            case 2:
                xa += (float)Math.cos(_a * _w);
                ya += (float)Math.sin(_a * _w);
            default:
                break;
            }
            
            _w = _c * (float)Math.atan2(ya, xa);
            float r = (float)(Math.sqrt(Math.sqrt(xa * xa + ya * ya)) / 2);
            float w = _w / 2;
            boolean rand = randomBoolean();
            
            switch(_type) {
            case 0:
                if (rand) {
                    _x = (float)(-Math.cos(_w) - r * Math.cos(w));
                    _y = (float)(-Math.sin(_w) + r * Math.sin(w));
                } else {
                    _x = (float)(-Math.cos(_w) + r * Math.cos(w));
                    _y = (float)(-Math.sin(_w) - r * Math.sin(w));
                }
                
                break;
            case 1:
                float s = _b * (randomBoolean() ? 1 : -1);
                
                if (rand) {
                    _x = (float)(-s * Math.cos(_w) - r * Math.cos(w));
                    _y = (float)( s * Math.sin(_w) - r * Math.sin(w));
                } else {
                    _x = (float)(-s * Math.cos(_w) + r * Math.cos(w));
                    _y = (float)( s * Math.sin(_w) + r * Math.sin(w));
                }
                
                break;
            case 2:
                if (rand) {
                    _x = (float)(-Math.cos(2 * _w) - r * Math.cos(w));
                    _y = (float)(-Math.sin(2 * _w) + r * Math.sin(w));
                } else {
                    _x = (float)(-Math.cos(2 * _w) + r * Math.cos(w));
                    _y = (float)(-Math.sin(2 * _w) - r * Math.sin(w));
                }
                
            default:
                break;
            }
        }
    }
    
    private class HenonPhase extends Attractor {
        private float _sinA, _cosA;
        private float _initialX, _initialY;
        
        public HenonPhase() {
            this((random() < 0.2f ? 22 + randomSigned(0.3f) : randomSign() * (1 + random(0, 9f))));
        }
        
        /*bad:
         * -0.048124313, 1.0878032, 0.91356254
         * 6.12084, 1.0261008, 1.112512
         * 6.11166, 0.86204344, 1.1016762
         * -0.59308434, 0.88863134, 1.1883903
         * 
         * good:
         * -2.8871279, 1.0300877, 1.1677693
         * 8.764784, 0.91266435, 1.0157443
         * -9.570294, 1.1077266, 1.2420424
         * 3.2044935, 0.8193875, 0.9158557
         * 1.58428, 0.82720274, 0.8488602
         * 
         */
        
        // 
        // @see http://local.wasp.uwa.edu.au/~pbourke/fractals/ifs_leaf_b/
        // 
        public HenonPhase(float a) {
            super(a, 0, 0, 0);
            
            _initialX = random(0.8f, 1.2f);
            _initialY = random(0.8f, 1.2f);
            this.resetA(_a);
            
//            System.out.println(_a + ", " + _initialX + ", " + _initialY);
        }
        
        public void modifyParameters() {
            this.resetA(_a + randomSigned(0.03f));
        }
        
        private final void resetA(float a) {
            _a = a;
            
            _sinA = (float)Math.sin(_a);
            _cosA = (float)Math.cos(_a);
            
            _initialX += randomSigned(0.05f);
            _initialY += randomSigned(0.05f);
            
            // cap initial (_x, _y) coordinates
            if (_initialX > 1.6f) _initialX = 1.6f;
            else if (_initialX < 0.2f) _initialX = 0.2f;
            if (_initialY > 1.6f) _initialY = 1.6f;
            else if (_initialY < 0.2f) _initialY = 0.2f;
            
            _x = _initialX;//random(0.5f, 1.5f);
            _y = _initialY;//random(0.5f, 1.5f);
        }
        
        public void move() {
            float c = (_y - _x * _x);
            
            _y = _x * _sinA + c * _cosA;
            _x = _x * _cosA - c * _sinA;
            
            if (Math.abs(_x) < 0.0001 && Math.abs(_y) < 0.0001) {
                _initialX = _initialY = 1;
                this.resetA((random() < 0.2f ? 22 + randomSigned(0.3f) : randomSign() * (1 + random(0, 9f))));
//                _x = 1; _y = 1;
            }
            
//            float x = _x;
//            _x = 1 - _a * _x * _x + _y;
//            _y = _b * _x;
            
//            System.out.println(_x + ", " + _y + ", ");// + _sinA + ", " + _cosA);
        }
    }
    
    private class LissajousCurve extends Attractor {
        private static final float A_CAP = 2f, B_CAP = 5f, C_CAP = 3.5f;
        private float _t;
        
        public LissajousCurve() {
            this(random(0, A_CAP), randomSigned(B_CAP), randomSigned(C_CAP));
        }
        
        /* Bad parameters:
         * 2.8622706, -2.0685349, -4.0589314         * 6.117867, -3.148118, -5.984028
         * 5.074573, 3.5127888, -4.8420057         * 4.04583, 5.31549, -2.5136757
         * 6.135536, -3.5415783, -4.653157         * 4.61873, 1.6976175, 4.156188
         * 5.3419013, 2.1378202, -0.43924427         * 3.8877316, -3.010521, -2.805832
         * 3.731795, 3.5843904, -5.37484         * 3.8175635, 2.706264, -0.5287199
         * 3.4080632, 4.7388544, -2.4954052         * 5.3633637, -4.651043, -0.53087234
         * 2.0599196, -3.3465157, 5.611248         * 4.7257466, -3.5212517, -1.1070862
         * 2.8620574, -3.4428558, 3.8715298         * 4.1085835, 0.44753456, 5.2037005
         * 
         * Interesting parameters:
         * 0.29932573, -5.482336, 0.60173464         * 2.4392607, 0.8175721, 0.72027254
         * 1.3793149, 4.6680546, 1.060638         * 2.2764971, 2.0359392, -1.0377064
         * 3.3186874, 0.59753466, -1.7613988         * 1.9280308, -2.1237345, -3.010827
         * 0.36387554, -5.1283045, -4.001934         * 1.0401568, 5.03418, -1.596767
         * 0.3260226, 5.307534, 1.480936         * 0.3310106, -5.5870285, 1.3381624
         * 1.6485546, 3.3853707, 3.7178857**         * 2.166757, -1.1383238, -1.447855**
         * 1.3987412, -4.2901573, 3.2854204**         * 1.9692127, 2.1873913, 1.2635322
         * 0.38653, 4.4918203, 2.9098964*         * 1.0598902, 0.17450523, -2.6572037diff
         * 0.7932027, -5.761816, 1.6989374         * 1.8419552, -5.0638685, 4.256047  limit
         */
        
        // 
        // @see http://www.xahlee.org/SpecialPlaneCurves_dir/Lissajous_dir/lissajous.html
        // @see http://en.wikipedia.org/wiki/Lissajous_figure
        // 
        public LissajousCurve(float a, float b, float c) {
            super(a, b, c, 0, _offscreen.getWidth() >> 2, _offscreen.getHeight() >> 1);
            
            _t = 0;
            System.out.println(_a + ", " + b + ", " + c);
        }
        
        public void modifyParameters() {
            _a += randomSigned(0.05f);
            _b += randomSigned(0.05f);
            _c += randomSigned(0.05f);
            
            if (Math.abs(_a) > A_CAP)
                _a = Math.signum(_a) * A_CAP;
            if (Math.abs(_b) > B_CAP)
                _b = Math.signum(_b) * B_CAP;
            if (Math.abs(_c) > C_CAP)
                _c = Math.signum(_c) * C_CAP;
        }
        
        public void move() {
            _t += 0.005f; // 0.001f too small and 0.01f seems too large
            
            _x = (float)(_a * Math.sin(_b * _t + _c));
            _y = (float)(Math.sin(_t));
        }
    }
    
    // TODO: Work on Ikeda Attractor
    private class IkedaAttractor extends Attractor {
        private static final float RAND_MIN = 0, RAND_MAX = 0.3f;
        private static final float RHO_MIN = 0.5f, RHO_MAX = 1.25f;
        private static final float C_MIN = 7, C_MAX = 16;
        private static final float A_MIN = 0.25f, A_MAX = 0.6f; // 0.4f default
        private static final float B_MIN = 0.75f, B_MAX = 0.9f; // 0.85f default
        private float _x2, _y2, _c2, _rand2;
        
        private float _rho, _rand, _rho2;
        private int _sign, _sign2;
        
        public IkedaAttractor() {
            this(random(A_MIN, A_MAX), 
                 random(B_MIN, B_MAX), 
                 random(C_MIN, C_MAX), 
                 random(RAND_MIN, RAND_MAX), 
                 randomSign() * random(RHO_MIN, RHO_MAX));
        }
        
        // 
        // Two Opposing Ikeda Attractors
        // 
        // @see http://local.wasp.uwa.edu.au/~pbourke/fractals/ikeda/
        // @see http://www.splintered.co.uk/experiments/archives/ikeda_attractor_sketchboard/
        // 
        public IkedaAttractor(float a, float b, float c, float rand, float rho) {
            super(a, b, c, rand);
            
            _rand = rand;
            _rho  = rho;
            
            _x = 0.1f;
            _y = 0.1f;
            _x2 = random();
            _y2 = random();
            
            _c2 = random(C_MIN, C_MAX);
            _rand2 = random(RAND_MIN, RAND_MAX);
            _rho2  = -Math.signum(_rho) * random(RHO_MIN, RHO_MAX);
            
            _sign  = randomSign();
            _sign2 = randomSign();
            
//            _dRho = randomSign() * random(0.0001f, 0.01f);
//            System.out.println(_a + ", " + _b + ", " + _c + ", " + _rand + ", " + _rho);
        }
        
        public void modifyParameters() {
            _a += randomSigned(0.005f);
            _b += randomSigned(0.005f);
            _c += randomSigned(0.05f);
            _c2 += randomSigned(0.05f);
            
            _rand += randomSigned(0.005f);
            _rand2 += randomSigned(0.005f);
            
            _rho  += randomSigned(0.03f);
            _rho2 += randomSigned(0.03f);
            
//            _dRho += randomSigned(0.0001f);
//            _dRho = this.capSigned(_dRho, 0.0001f, 0.01f);
//            
//            _rho += _dRho;
            
            _x  = random();
            _y  = random();
            _x2 = random();
            _y2 = random();
            
            _a     = this.capSigned(_a, A_MIN, A_MAX);
            _b     = this.capSigned(_b, B_MIN, B_MAX);
            _c     = this.capSigned(_c, C_MIN, C_MAX);
            _c2    = this.capSigned(_c2, C_MIN, C_MAX);
            _rho   = this.capSigned(_rho, RHO_MIN, RHO_MAX);
            _rho2  = this.capSigned(_rho2, RHO_MIN, RHO_MAX);
            _rand  = this.capSigned(_rand, RAND_MIN, RAND_MAX);
            _rand2 = this.capSigned(_rand2, RAND_MIN, RAND_MAX);
            
            _sign = randomSign();
            _sign2 = randomSign();
        }
        
        // Cap randomness to predetermined 'interesting values'
        public float capSigned(float num, float min, float max) {
            if (Math.abs(num) < min)
                return Math.signum(num) * min;
            else if (Math.abs(num) > max)
                return Math.signum(num) * max;
            
            return num;
        }
        
        public boolean update() {
            this.move();
            
            int screenX = PARTICLE_ORIGIN_X + Math.round(_x * _horizontalScale);
            int screenY = PARTICLE_ORIGIN_Y + Math.round(_y * _verticalScale);
            
            if (isValid(screenX, screenY))
                setPixel(screenX, screenY, _color);
            
            screenX = PARTICLE_ORIGIN_X + Math.round(_x2 * _horizontalScale);
            screenY = PARTICLE_ORIGIN_Y + Math.round(_y2 * _verticalScale);
            
            if (isValid(screenX, screenY))
                setPixel(screenX, screenY, _color);
            
            return true;
        }
        
        public void move() {
            float temp = _a - _c / (1 + _rand * 3 + _x * _x + _y * _y);
            float sinTemp = (float)Math.sin(temp);
            float cosTemp = (float)Math.cos(temp);
            
            float x = _x;
            
            _x = _rho  + _b * (x * cosTemp + _sign * _y * sinTemp);
            _y = _rand + _b * (x * sinTemp - _sign * _y * cosTemp);
            
            temp = _a - _c2 / (1 + _rand2 * 3 + _x2 * _x2 + _y2 * _y2);
            sinTemp = (float)Math.sin(temp);
            cosTemp = (float)Math.cos(temp);
            
            x = _x2;
            
            _x2 = _rho2  + _b * (x * cosTemp + _sign2 * _y2 * sinTemp);
            _y2 = _rand2 + _b * (x * sinTemp - _sign2 * _y2 * cosTemp);
        }
    }
    
//    
//    private class MapleLeaf extends Attractor {
//        private float _e, _f;
//        private int _stage;
//        
//        public MapleLeaf() {
//            this(random(0, 3));
//        }
//        
//        // 
//        // @see http://local.wasp.uwa.edu.au/~pbourke/fractals/ifs_leaf_b/
//        // 
//        public MapleLeaf(int stage) {
//            super(0, 0, 0, 0, 30, 30);
//            
//            this.initializeStage(stage);
//        }
//        
//        public final void initializeStage(int stage) {
//            _stage = stage;
//            
//            float[] a = new float[] { 0.14f, 0.43f, 0.45f, 0.49f };
//            float[] b = new float[] { 0.01f, 0.52f, -0.49f, 0f };
//            float[] c = new float[] { 0.0f, -0.45f, 0.47f, 0f };
//            float[] d = new float[] { 0.51f, 0.5f, 0.47f, 0.51f };
//            float[] e = new float[] { -0.08f, 1.49f, -1.62f, 0.02f };
//            float[] f = new float[] { -1.31f, -0.75f, -0.74f, 1.62f };
//            
//            _a = a[_stage]; _b = b[_stage]; _c = c[_stage];
//            _d = d[_stage]; _e = e[_stage]; _f = f[_stage];
//        }
//        
//        public void modifyParameters() {
////            this.initializeStage((_stage + 1) % 4);
//        }
//        
//        public void move() {
//            float x = _x;
//            
////            System.out.println(_a + ",  " + _b + ", " + _c + ", " + _d + ", " + _e + ", " + _f);
//            
//            _x = _a * _x + _b * _y + _e;
//            _y = _c *  x + _d * _y + _f;
//        }
//    }
    
//    private static final int ATTRACTOR_PALETTE[] = {
//        115, 73, 50, 124, 166, 164, 178, 56, 73, 144, 115, 108, 206, 56, 73, 
//        95, 227, 216, 199, 105, 146, 234, 121, 201, 215, 166, 84, 154, 167, 166, 
//        199, 171, 180, 220, 184, 230, 201, 195, 170, 247, 198, 166, 216, 216, 215, 
//        206, 233, 244, 243, 217, 199, 113, 99, 55, 160, 207, 109, 175, 50, 104, 
//        174, 83, 78, 203, 52, 109, 203, 114, 111, 198, 120, 134, 181, 130, 59, 
//        212, 166, 118, 177, 147, 141, 200, 182, 168, 247, 150, 199, 201, 197, 185, 
//        244, 201, 182, 199, 204, 224, 205, 242, 235, 245, 215, 215, 83, 79, 78, 
//        147, 26, 38, 146, 74, 21, 176, 87, 107, 240, 55, 71, 240, 77, 83, 
//        199, 121, 149, 151, 131, 88, 241, 144, 80, 181, 150, 165, 200, 185, 183, 
//        251, 151, 230, 203, 209, 187, 247, 215, 168, 204, 218, 226, 204, 243, 246, 
//        230, 202, 227, 88, 89, 100, 140, 50, 17, 111, 50, 142, 175, 111, 81, 
//        209, 76, 21, 238, 88, 105, 214, 105, 136, 148, 133, 117, 238, 144, 110, 
//        181, 167, 148, 213, 169, 166, 232, 168, 198, 215, 197, 169, 247, 216, 182, 
//        202, 218, 241, 219, 234, 234, 235, 203, 242, 91, 97, 88, 142, 51, 44, 
//        153, 100, 24, 176, 114, 109, 206, 85, 46, 234, 117, 81, 214, 107, 149, 
//        180, 132, 86, 251, 166, 84, 182, 176, 171, 214, 170, 181, 233, 166, 216, 
//        214, 198, 185, 251, 232, 150, 217, 203, 227, 219, 235, 242, 228, 219, 228, 
//        91, 102, 103, 175, 11, 17, 149, 101, 54, 205, 12, 16, 204, 104, 23, 
//        241, 112, 112, 213, 120, 135, 177, 140, 114, 239, 172, 114, 190, 152, 193, 
//        214, 183, 168, 230, 186, 198, 217, 212, 170, 246, 234, 178, 217, 220, 226, 
//        218, 241, 236, 232, 219, 242, 113, 83, 76, 178, 24, 40, 174, 76, 21, 
//        206, 25, 41, 207, 109, 53, 145, 120, 133, 213, 120, 149, 184, 163, 122, 
//        215, 197, 116, 185, 184, 196, 213, 185, 183, 232, 185, 213, 217, 212, 185, 
//        202, 200, 198, 217, 221, 241, 219, 243, 244, 249, 199, 231, 113, 85, 101, 
//        214, 200, 44, 173, 81, 47, 229, 76, 22, 176, 85, 137, 208, 115, 169, 
//        211, 133, 56, 250, 198, 88, 186, 194, 184, 237, 144, 144, 249, 166, 198, 
//        220, 228, 179, 203, 203, 210, 204, 227, 218, 228, 201, 199, 250, 202, 246, 
//        116, 101, 85, 174, 50, 47, 180, 101, 24, 205, 45, 49, 230, 85, 44, 
//        177, 116, 137, 234, 86, 139, 243, 142, 29, 246, 202, 115, 186, 198, 198, 
//        243, 147, 174, 251, 169, 213, 244, 207, 147, 203, 211, 202, 218, 225, 202, 
//        229, 202, 213, 245, 216, 233, 115, 111, 108, 155, 21, 76, 177, 106, 54, 
//        232, 15, 22, 236, 115, 14, 183, 116, 164, 230, 92, 171, 245, 132, 54, 
//        250, 230, 121, 204, 148, 141, 239, 178, 144, 248, 183, 196, 230, 199, 168, 
//        204, 214, 214, 217, 227, 219, 229, 215, 200, 250, 216, 248, 122, 121, 129, 
//        143, 53, 73, 143, 84, 75, 237, 24, 41, 239, 117, 50, 207, 86, 142, 
//        245, 118, 138, 249, 163, 55, 148, 143, 140, 205, 150, 168, 234, 178, 174, 
//        249, 184, 216, 229, 200, 183, 214, 201, 199, 221, 244, 200, 227, 216, 215, 
//        241, 239, 210, 121, 131, 121, 147, 57, 106, 147, 85, 105, 227, 60, 5, 
//        206, 79, 80, 219, 86, 166, 245, 118, 169, 210, 139, 84, 154, 155, 162, 
//        207, 172, 146, 211, 149, 197, 247, 184, 234, 232, 213, 169, 215, 203, 212, 
//        220, 242, 218, 246, 198, 202, 226, 232, 233, 123, 135, 134, 171, 15, 83, 
//        146, 106, 82, 239, 45, 52, 206, 83, 107, 199, 105, 135, 239, 125, 194, 
//        206, 144, 112, 153, 162, 152, 199, 169, 166, 207, 185, 197, 215, 197, 151, 
//        230, 214, 184, 216, 212, 201, 205, 229, 229, 247, 199, 214, 227, 235, 243, 
//    };

//  private class Matrix {
//      private float[] _data;
//      
//      public Matrix(float[] data) {
//          super();
//          
//          _data = data;
//      }
//      
//      public Matrix mul(Matrix m2) {
//          float[] data = new float[16];
//          float[] data2 = m2._data;
//          
//          for(int a = 0; a < 16; a++) {
//              // Dot product: row(
//              int row = (a & 3), col = (a & ~3);
//              
//              data[a]  = _data[a] * data2[col++];
//              data[a] += _data[a + 4] * data2[col++];
//              data[a] += _data[a + 8] * data2[col++];
//              data[a] += _data[a + 12] * data2[col];
//          }
//          
//          return new Matrix(data);
//      }
//  }
}

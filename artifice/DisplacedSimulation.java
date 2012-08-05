package artifice;
/* DisplacedSimulation.java
 * 
 * Kinda Models waves sorta (not a physically accurate representation)
 *   The main problem with this simulation is that waves propogate through 
 *   each other without really being affected.
 * 
 * @author Travis Fischer (tfischer)
 * @version Dec 27, 2006
 */
import static artifice.ArtificeConstants.*;

import gfx.ImageUtils;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;

public class DisplacedSimulation extends Simulation {
    static private BufferedImage _background = null;
//    private MemoryImageSource _source;
    private int _rippleRadius;
//    private int[] _ripple;
    private short _strength;
//    private Image _off;
    private int[] _back;
    private boolean _automaticWaves;
    private short[][] _heightMap;
    private short[] _currentMap;
    private short[] _oldMap;
    private String _backgroundPath;
    
    public DisplacedSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB);
        
        _rippleRadius = 3; // 2 to 3 works well
        _strength     = 4; // 3 to 9 works well
        _automaticWaves = true;
        _backgroundPath = "";
        
        if (_background == null)
            this.loadBackground(DISPLACED_DEFAULT_BACKGROUNDS[0]);
    }
    
    public final void reset() {
        _heightMap = new short[2][SIMULATION_WIDTH * SIMULATION_HEIGHT];
        _currentMap = _heightMap[0];
        _oldMap = _heightMap[1];
        
        if (!_automaticWaves)
            this.showBackground();
        
//        _ripple = new int[SIMULATION_WIDTH * SIMULATION_HEIGHT];
//        _source = new MemoryImageSource(SIMULATION_WIDTH, SIMULATION_HEIGHT, 
//                _ripple, 0, SIMULATION_WIDTH);
//        
//        _source.setAnimated(true);
//        _source.setFullBufferUpdates(true);
//        
//        _off = _dp.getToolkit().createImage(_source);
    }
    
    public void update() {
        short[] temp = _currentMap;
        _currentMap = _oldMap;
        _oldMap = temp;
        
        int index = 0;
        
//        if (random() > 0.2f)//0.68f)
        if (_automaticWaves)
            this.disturb(random(1, SIMULATION_WIDTH - 2), random(1, SIMULATION_HEIGHT - 2));
        
        for(int j = 0; j < SIMULATION_HEIGHT; j++) {
            int a = (j <= 0 ? 0 : SIMULATION_WIDTH);
            int b = (j >= SIMULATION_HEIGHT - 1 ? 0 : SIMULATION_WIDTH);
            for(int i = 0; i < SIMULATION_WIDTH; i++, index++) {
                short old = _currentMap[index];
                
                // simple box filter for the heights around (i, j)
//                short avg = 0;
//                int radius = 1;
//                for(int b = -radius; b <= radius; b++) {
//                    int index = oldIndex + b * SIMULATION_WIDTH - radius;
//                    for(int a = -radius; a <= radius; a++, index++) {
//                        if (index >= 0 && index < SIMULATION_HEIGHT*SIMULATION_WIDTH)
//                            avg += _oldMap[index];
//                    }
//                }
//                radius = 1 + (radius << 1);
//                avg = (short)((avg << 1) / (radius * radius) - old);
                
                short avg = (short)(((_oldMap[index - a] + _oldMap[index + b] + 
                        _oldMap[index - (i <= 0 ? 0 : 1)] + 
                        _oldMap[index + (i >= SIMULATION_WIDTH - 1 ? 0 : 1)]) >> 1) - 
                        old);
                
//                short avg = (short)(((_old[j][a] + _old[j][b] + 
//                        _old[(j <= 1 ? 0 : j - 1)][i] + 
//                        _old[(j >= SIMULATION_HEIGHT - 2 ? SIMULATION_HEIGHT - 1 : j + 1)][i]) >> 1) - 
//                        _current[j][i]);
                
                // dampen the strength of the wave over time
                avg -= (avg >> _strength);
                
                if (old != avg) {
                    _currentMap[index] = avg;
//                    _current[j][i] = avg;
                    
                    int x = i, y = j;
                    
                    if (avg > 0 && avg < 1024) {
                        avg = (short)(1024 - avg);
                        
                        // reflect (i, j) incides based on current magnitude of wave
                        x = (i * avg) >> 10;
                        y = (j * avg) >> 10;
                        
                        // Boundary checks
                        x = (x <= 0 ? 0 : (x >= SIMULATION_WIDTH  ? SIMULATION_WIDTH  - 1 : x));
                        y = (y <= 0 ? 0 : (y >= SIMULATION_HEIGHT ? SIMULATION_HEIGHT - 1 : y));
                    }
                    
                    _offscreen.setRGB(i, j, _back[y * SIMULATION_WIDTH + x]);
                    //_background.getRGB(x, y));
                }
            }
        }
        
        this.repaint();
    }
    
    public final void loadBackgroundAbsolute(String path) {
        if (_backgroundPath != path) {
            _backgroundPath = path;

            this.setBackground(ImageUtils.getBufferedImageAbsolute(_dp, path));
        }
    }
    
    public final void loadBackground(String path) {
        if (_backgroundPath != path) {
            _backgroundPath = path;
            
            this.setBackground(ImageUtils.getBufferedImage(_dp, path));
        }
    }
    
    private boolean setBackground(BufferedImage background) {
        if (background == null)
            return false;
        
        _background = background;
        
        if (_background.getWidth() != SIMULATION_WIDTH || 
            _background.getHeight() != SIMULATION_HEIGHT) {
            // Ensure background Image is of correct size
            // and if not, scale it appropriately
            
            BufferedImage temp = new BufferedImage(SIMULATION_WIDTH, 
                    SIMULATION_HEIGHT, BufferedImage.TYPE_INT_RGB); 
            Graphics2D tempBrush = (Graphics2D) temp.getGraphics();
            tempBrush.setColor(Color.BLACK);
            tempBrush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
            // stretch to fit
            tempBrush.drawImage(_background, 0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT, _dp);
            
            _background = temp;
        }
        
        _back = new int[_background.getWidth() * _background.getHeight()];
        for(int a = 0, j = 0; j < _background.getHeight(); j++) {
            for(int i = 0; i < _background.getWidth(); i++) {
                _back[a++] = _background.getRGB(i, j);
            }
        }
        
        this.restart();
        return true;
    }
    
    public void showBackground() {
        Graphics2D offBrush = (Graphics2D) _offscreen.getGraphics();
        offBrush.drawImage(_background, 0, 0, _dp);
    }
    
    public void disturb(MouseEvent e) {
        this.disturb(e.getX(), e.getY());
    }
    
    public void disturb(int x, int y) {
        _rippleRadius = random(1, 3);
//        System.out.println("Ripple: " + _rippleRadius);
        
        for(int j = -_rippleRadius; j <= _rippleRadius; j++) {
            short val = (short)(8 - _rippleRadius - Math.abs(j));
            
            int yOff = y + j;
            if (yOff >= 0 && yOff < SIMULATION_HEIGHT) {
                yOff *= SIMULATION_WIDTH;
                
                for(int i = -_rippleRadius; i <= _rippleRadius; i++) {
                    val += (i <= 0 ? 1 : -1);
                    
                    int xOff = x + i;
                    if (xOff >= 0 && xOff < SIMULATION_WIDTH)
                        _oldMap[yOff + xOff] = (short)(1 << val);
//                    System.out.println(i + ", " + j + ",  " + val);
                }
            }
        }

//        for(int j = y - _rippleRadius; j < y + _rippleRadius; j++) {
//            if (j >= 0 && j < SIMULATION_HEIGHT) {
//                int yOff = j * SIMULATION_WIDTH;
//                
//                for(int i = x - _rippleRadius; i < x + _rippleRadius; i++) {
//                    if (i >= 0 && i < SIMULATION_WIDTH)
//                        _oldMap[yOff + i] += 512;
//                }
//            }
//        }
    }
    
    public void mouseClicked(MouseEvent e) {
        this.disturb(e);
    }
    
    public void mouseDragged(MouseEvent e) {
        this.disturb(e);
    }
    
    public void setWaveStrength(int strength) {
        _strength = (short)strength;
    }
    
    public void setAutomaticWaves(boolean enabled) {
        _automaticWaves = enabled;
    }
}

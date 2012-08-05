package artifice;
/* HilbertSystem.java
 * 
 * Recursive space-filling L-System represented graphically
 * 
 * @see http://en.wikipedia.org/wiki/Hilbert_curve
 * 
 * @author Travis Fischer (tfischer)
 * @version Dec 31, 2006
 */
import static artifice.ArtificeConstants.*;

import java.awt.image.*;
import java.awt.*;

public class HilbertSystem extends LSystem {
    public static final int VARIATION_CIRCLE = 0;
    public static final int VARIATION_RECT   = 1;
    public static final int COLOR_DEFAULT    = 0;
    public static final int COLOR_NONE       = 1;
    private static final Color STROKE_COLOR  = new Color(0, 0, 0, 48);
    private static final Stroke STROKE_STROKE = new BasicStroke(1);
    private int _x, _y;
    private double _theta;
    private int _width, _height;
    private String _result;
    private Graphics2D _brush;
    private int _horizLength, _vertLength;
    private double _angle;
    private int _depth;
    private int _variation, _color;
    
    public HilbertSystem() {
        super("LR", "L");
        
        _angle  = Math.PI / 2; // 90 degree turns
        _variation = HilbertSystem.VARIATION_CIRCLE;
        _color     = HilbertSystem.COLOR_DEFAULT;
    }
    
    public String processRule(char rule) {
        String replacement = "";
        
        if (rule == 'L')
            replacement = "+RF-LFL-FR+";
        else if (rule == 'R')
            replacement = "-LF+RFR+FL-";
        
        return replacement;
    }
    
    public void setVariation(int variation) {
        _variation = variation;
    }
    
    public int getVaration() {
        return _variation;
    }
    
    public void setColor(int color) {
        _color = color;
    }
    public int getColor() {
        return _color;
    }
    
    public void generate(BufferedImage canvas, int depth) {
        _width  = canvas.getWidth();
        _height = canvas.getHeight();
        
        _theta = 0;
        _x = 0; _y = _height - 1;
        
        _depth  = depth;
        _result = this.processSystem(depth);
        _brush = (Graphics2D) canvas.getGraphics();
        
        _brush.setColor(Color.BLACK);
        _brush.setStroke(new BasicStroke(1));
        _brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
//        System.out.println(_result);
        
        _horizLength = _width;
        _vertLength  = _height;
        for(int i = 0; i < depth; i++) {
            _horizLength >>= 1;
            _vertLength  >>= 1;
        }
        
//        for(int i = 0; i < _result.length(); i++) {
//            this.update(i);
//        }
    }
    
    public Color randomColor(int alpha) {
        if (_color == HilbertSystem.COLOR_DEFAULT) {
            int[] palette = HILBERT_PALETTE;//SUBSTRATE_COLORS;
            
            // Select a random color from within a predefined color palette
            int offset = 3 * random(0, (palette.length - 1) / 3);
            return new Color(palette[offset],  
                    palette[offset + 1], 
                    palette[offset + 2], alpha);
        }
        
        return new Color(0, 0, 0, random(10, 150));
    }
    
    public boolean update(int i) {
        if (i >= _result.length())
            return false;

        char cur = _result.charAt(i);
        
        switch(cur) {
        case 'F':
            int newX = (int)Math.round(_x + _horizLength * Math.cos(_theta));
            int newY = (int)Math.round(_y + _vertLength  * Math.sin(_theta));
            
            _brush.setColor(STROKE_COLOR);
            _brush.setStroke(STROKE_STROKE);
            _brush.drawLine(_x, _y, newX, newY);
            
            int size = random(3, _horizLength + 3), halfSize = (size >> 1);
            
            _brush.setStroke(new BasicStroke(7 - _depth));
            _brush.setColor(this.randomColor(random(10, 150)));
            
            if (_variation == HilbertSystem.VARIATION_RECT) {
                if (randomBoolean())
                    _brush.drawRect(_x - halfSize, _y - halfSize, size, size);
                else _brush.fillRect(_x - halfSize, _y - halfSize, size, size);
            } else if (_variation == HilbertSystem.VARIATION_CIRCLE) {
                _brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (randomBoolean())
                    _brush.drawOval(_x - halfSize, _y - halfSize, size, size);
                else _brush.fillOval(_x - halfSize, _y - halfSize, size, size);
                
                _brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            
            _x = newX;
            _y = newY;
            break;
        case '-':
            _theta += _angle;
            break;
        case '+':
            _theta -= _angle;
            break;
        default:
            break;
        }
        
        return true;
    }
    
    private static final int[] HILBERT_PALETTE = {
//        11, 16, 12, 
//        17, 69, 45, 
        71, 49, 19,         108, 63, 16, 
        99, 100, 68,         71, 125, 105, 
        82, 138, 113,        115, 148, 131, 
        108, 150, 148,         125, 148, 128, 
        174, 15, 68,         175, 77, 71, 
        195, 62, 69,         205, 91, 71, 
        176, 144, 94,         190, 170, 82, 
        204, 161, 75,         140, 157, 138, 
        157, 179, 154,         169, 182, 167, 
        188, 209, 195,         231, 193, 152, 
        20, 18, 34,         23, 88, 57, 
        66, 60, 46,         102, 70, 45, 
        96, 103, 89,         86, 124, 90, 
        102, 141, 72,         123, 152, 99, 
        118, 142, 138,         126, 150, 143, 
        171, 54, 71,         186, 71, 97, 
        217, 26, 74,         210, 78, 89, 
        185, 129, 91,         188, 143, 79, 
        181, 149, 100,         135, 156, 152, 
        141, 171, 167,         167, 184, 177, 
        206, 159, 141,         241, 194, 170, 
        16, 42, 20,         44, 68, 23, 
        97, 28, 17,         118, 75, 24, 
        115, 93, 55,         110, 110, 64, 
        113, 143, 82,         116, 154, 129, 
        117, 144, 152,         124, 152, 150, 
        145, 77, 14,         169, 115, 80, 
        218, 72, 69,         237, 74, 68, 
        202, 115, 90,         186, 144, 108, 
        185, 145, 116,         147, 147, 142, 
        143, 170, 175,         181, 171, 164, 
        214, 156, 161,         205, 208, 196, 
        19, 45, 39,         47, 72, 47, 
        99, 22, 41,         112, 74, 47, 
        109, 94, 78,         97, 109, 88, 
        85, 131, 100,         92, 138, 133, 
        121, 125, 117,         140, 39, 35, 
        141, 80, 44,         171, 120, 99, 
        200, 89, 42,         228, 97, 55, 
        208, 135, 82,         193, 160, 89, 
        187, 155, 108,         148, 149, 152, 
        145, 177, 167,         181, 182, 167, 
        210, 169, 144,         209, 217, 218, 
        39, 16, 24,         56, 90, 52, 
        101, 57, 21,         86, 77, 52, 
        96, 100, 75,         95, 119, 96, 
        79, 131, 127,         88, 135, 148, 
        121, 122, 129,         141, 14, 40, 
        148, 99, 24,         207, 25, 24, 
        206, 64, 40,         234, 105, 80, 
        211, 144, 108,         192, 165, 113, 
        189, 160, 119,         149, 157, 145, 
        147, 177, 177,         181, 188, 176, 
        201, 183, 165,         216, 223, 211, 
        43, 11, 37,         31, 80, 63, 
        94, 61, 49,         83, 79, 82, 
        94, 104, 99,         107, 124, 94, 
        98, 134, 107,         91, 135, 143, 
        110, 121, 131,         140, 51, 19, 
        144, 107, 44,         205, 9, 40, 
        205, 80, 28,         175, 116, 92, 
        163, 158, 124,         194, 171, 103, 
        202, 151, 104,         150, 158, 150, 
        150, 174, 171,         163, 190, 187, 
        223, 176, 149,         215, 223, 219, 
        43, 41, 26,         26, 95, 70, 
        64, 64, 71,         77, 89, 75, 
        70, 98, 107,         85, 113, 114, 
        98, 132, 120,         94, 134, 156, 
        114, 119, 150,         145, 57, 40, 
        168, 87, 18,         207, 42, 20, 
        207, 94, 37,         192, 119, 105, 
        192, 131, 82,         212, 145, 64, 
        197, 153, 122,         139, 159, 166, 
        152, 174, 178,         170, 188, 192, 
        234, 182, 165,         246, 207, 195, 
        44, 49, 40,         46, 81, 72, 
        96, 28, 69,         82, 77, 87, 
        63, 105, 119,         80, 120, 126, 
        97, 138, 124,         95, 144, 145, 
        118, 121, 137,         169, 27, 25, 
        174, 81, 42,         207, 50, 37, 
        228, 76, 25,         187, 113, 55, 
        183, 132, 57,         215, 147, 63, 
        199, 162, 107,         141, 164, 150, 
        150, 179, 170,         159, 191, 160, 
        241, 189, 187,         231, 222, 212, 
        28, 51, 67,         51, 80, 98, 
        104, 55, 73,         84, 86, 101, 
        66, 99, 124,         82, 122, 120, 
        97, 147, 126,         99, 144, 153, 
        126, 114, 140,         175, 15, 40, 
        175, 103, 23,         238, 21, 17, 
        241, 64, 35,         202, 111, 34, 
        200, 146, 29,         222, 164, 62, 
        196, 166, 121,         138, 168, 158, 
        152, 183, 178,         158, 192, 175, 
        203, 194, 160,         228, 231, 221, 
        26, 58, 90,         49, 95, 88, 
        77, 76, 46,         74, 100, 86, 
        73, 112, 125,         88, 130, 127, 
        97, 144, 133,         97, 136, 163, 
        125, 118, 145,         174, 46, 25, 
        175, 114, 39,         244, 14, 32, 
        242, 79, 27,         203, 120, 38, 
        200, 150, 45,         216, 176, 73, 
        189, 182, 122,         144, 171, 148, 
        164, 165, 147,         172, 195, 155, 
        204, 195, 156,         229, 231, 222, 
        43, 31, 70,         53, 104, 97, 
        70, 89, 58,         72, 109, 94, 
        83, 118, 141,         103, 126, 134, 
        108, 136, 121,         104, 141, 147, 
        127, 131, 141,         170, 56, 43, 
        147, 89, 65,         244, 42, 20, 
        239, 94, 35,         185, 126, 65, 
        202, 141, 49,         232, 168, 61, 
        197, 184, 118,         155, 166, 141, 
        170, 172, 149,         183, 196, 172, 
        220, 194, 133,         230, 231, 220, 
        44, 47, 69,         70, 44, 44, 
        74, 86, 36,         80, 105, 84, 
        93, 116, 130,         109, 127, 130, 
        112, 136, 127,         102, 144, 146, 
        116, 131, 128,         150, 30, 63, 
        144, 104, 77,         245, 48, 34, 
        220, 78, 57,         170, 120, 91, 
        198, 138, 68,         205, 146, 76, 
        156, 158, 132,         156, 167, 150, 
        167, 175, 162,         175, 196, 187, 
        213, 193, 147,         230, 231, 221, 
        21, 63, 36,         72, 15, 38, 
        76, 84, 46,         82, 105, 94, 
        71, 118, 105,         91, 135, 104, 
        113, 144, 120,         104, 149, 141, 
        114, 138, 129,         142, 53, 70, 
        141, 117, 98,         211, 26, 63, 
        203, 67, 86,         164, 135, 94, 
        200, 161, 57,         199, 150, 78, 
        143, 153, 141,         156, 176, 147, 
        167, 176, 179,         184, 200, 192, 
        199, 200, 179,         229, 232, 223, 
    };
}

package artifice;
/* HilbertSystem.java
 * 
 * Models a Lindenmayer System, which may be used to simulate a variety of 
 * effects in Nature, such as plant growth and fractals.
 * 
 * @see http://en.wikipedia.org/wiki/Lindenmayer_System
 * 
 * @author Travis Fischer (tfischer)
 * @version Dec 31, 2006
 */
import java.awt.image.*;

public abstract class LSystem {
//    private String _constants;
    
    private String _axiom;
    // Any chars which are not in _rules are considered to be constants
    private String _rules;
    
    public LSystem(String rules, String axiom) {
        super();
        
        _rules     = rules;
        _axiom     = axiom;
    }
    
    public String processSystem(int maxDepth) {
        return this.process(_axiom, maxDepth);
    }
    
    private final String process(String state, int depth) {
        if (depth <= 0)
            return state;
        
        String nextState = "";
        
        for(int i = 0; i < state.length(); i++) {
            char cur = state.charAt(i);
            
//            if (_constants.indexOf(cur) > 0) {
//                nextState += cur;
            if (_rules.indexOf(cur) >= 0) {
                nextState += this.processRule(cur);
            } else { 
                nextState += cur;
                
//                this.processConstant(cur, depth);
            }
        }
        
//        System.out.println(depth + ")  " + nextState);
        
        return this.process(nextState, depth - 1);
    }
    
//    public abstract void processConstant(int constant, int depth);
    public abstract String processRule(char rule);
}

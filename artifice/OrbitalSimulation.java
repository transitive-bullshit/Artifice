package artifice;
import static artifice.ArtificeConstants.*;

import java.util.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.awt.*;

/**
 * @author Travis Fischer
 *
 * Concept and code base by J. Tarbell
 * @see http://www.complexification.net/
 */
public class OrbitalSimulation extends Simulation {
    private static final int NO_DEFAULT_PARTICLES = 100;
    private Particle[] _particles;
    private int _noParticles;
    private int _originRadius;
    
    public OrbitalSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB);
        
        _noParticles  = NO_DEFAULT_PARTICLES;
        _originRadius = 70;
    }
    
    public void update() {
        for(int i = 0; i < _noParticles; i++)
            _particles[i].update();
        
        this.repaint();
    }
    
    // Initializes and starts the Simulation
    public void reset() {
        _timer.stop();
        
        Particle.setCanvas(_offscreen);
        _particles = new Particle[_noParticles];
        
        for(int i = 0; i < SIMULATION_HEIGHT; i++) {
            for(int j = 0; j < SIMULATION_WIDTH; j++) {
                // Clear the screen to white
                _offscreen.setRGB(j,i, 0x00ffffff);
            }
        }
        
        // Create and initialize default cracks
        for(int i = 0; i < _noParticles; i++) {
           Particle particle = new Particle(i);
            
            _particles[i] = particle;
        }
//        
//        int[] pid = new int[_noParticles];
//        for(int i = 0; i < _noParticles; i++)
//            pid[i] = i;
//        
//        for(int i = 0; i < _noParticles; i++) {
//            int swap = random(0, _noParticles - 1);
//            
//            pid[i]    ^= pid[swap];
//            pid[swap] ^= pid[i];
//            pid[i]    ^= pid[swap];
//        }
        
//        boolean[] pid = new boolean[_noParticles];
//        for(int i = 0; i < _noParticles; i++)
//            pid[i] = true;
//        for(int i = _noParticles / 10; i-- > 0;)
//            pid[random(0, _noParticles - 1)] = false;
        
        float theta = TWO_PI * random();
        float thetaAdd = (random() * 4 + TWO_PI) / _noParticles;
        
        // Initialize all Particles
        for(int i = 0; i < _noParticles; i++, theta += thetaAdd) {
            float x = PARTICLE_ORIGIN_X + _originRadius * (float)Math.sin(theta);
            float y = PARTICLE_ORIGIN_Y + _originRadius * (float)Math.cos(theta);
            
            // Choose a random particle for _particles[i] to orbit around
            int pid1 = ((i > _noParticles / 10) ? random(0, i - 1) : i);
            
            _particles[i].initialize(x, y, _particles[pid1]);//[i]]);
        }
    }
}

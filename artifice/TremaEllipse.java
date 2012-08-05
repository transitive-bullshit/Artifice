package artifice;

import gfx.Ellipse;

public class TremaEllipse extends TremaSimulation {
    public TremaEllipse(DrawingPanel dp, SimulationTimer timer) {
        super(new Ellipse(dp), dp, timer);
    }
}
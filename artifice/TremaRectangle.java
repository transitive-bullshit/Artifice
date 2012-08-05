package artifice;

public class TremaRectangle extends TremaSimulation {
    public TremaRectangle(DrawingPanel dp, SimulationTimer timer) {
        super(new gfx.Rectangle(dp), dp, timer);
    }
}

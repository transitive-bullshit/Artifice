package gfx;

/*
 * The only purpose of this class is so that you have something to test your
 * shape with. It's use is 100% optional. When you run cs015_runDemo gfx, this
 * is what you see. Instructions for its use are below.
 */

public class App {

    public App(String s) {
        super();

        /*
         * Pass in one of your Shape subclasses here. You might be wondering
         * where your shapes will get their container from. For a container,
         * cs015.prj.Shape.ViewerFrame has a protected drawing panel. It is
         * named _dp. Since this class is a subclass of
         * cs015.prj.Shape.ViewerFrame, you have access to the _dp instance
         * variable from within this class. Thus, to use the ViewerFrame to test
         * your Shape, all you need is this:
         * 
         * gfx.Ellipse ellipse = new gfx.Ellipse(_dp);
         * 
         * Where Ellipse is one of your Shape subclasses.
         * 
         * Then you need to call:
         * 
         * this.setShape(ellipse);
         * 
         * and you should be all set! Just compile everything and run as:
         * 
         * java gfx.App
         * 
         */

        // instantiate an instance of your Shape subclass here
        /* Rectangle */
        // gfx.Ellipse e = new gfx.Ellipse(_dp);
        // gfx.Rectangle e = new gfx.Rectangle(_dp);
        // gfx.ImageShape e = new ImageShape(_dp, "witch01.gif");
        // this.setShape(e);
        // this.setShape(new Rectangle(_dp));
        /** Gradient Paint Example */
        // e.setPaint(new
        // java.awt.GradientPaint(0F,0F,java.awt.Color.black,(float)e.getWidth(),0F,java.awt.Color.white));

        // this.setShape(new gfx.TextShape(_dp, "This is a test", 50, 50));

        /** Texture Paint Example */
        // e.setPaint(ImageUtils.getTexturePaint(_dp, "witch01.gif"));
        // e.setPaint(new java.awt.TexturePaint(ImageUtils.getBufferedImage(_dp,
        // "witch01.gif"),
        // new java.awt.geom.Rectangle2D.Double(0, 0, e.getWidth(),
        // e.getHeight())));

        // BufferedImage =
        // e.setPaint(new java.awt.TexturePaint(new BufferedImage("test.jpg",
        // _dp),

        /* Ellipse */
        // this.setShape(new Ellipse(_dp));
        /* Arc */
        // this.setShape(new Arc(_dp));

        // uncomment this line and pass in your shape
        // this.setShape(<your shape subclass here>);

    }

    // Still don't need to worry about this yet. It just starts the App.
    public static void main(String[] argv) {

        App a = new App("Shape Viewer");

    }

}

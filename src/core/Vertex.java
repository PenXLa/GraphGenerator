package core;

public class Vertex implements GraphComponent, Cloneable {
    public int ui_x, ui_y;
    public String name;
    public int inx;
    public boolean selected = false;

    public Vertex(){}

    public Vertex(int ui_x, int ui_y, int inx) {
        this.ui_x = ui_x;
        this.ui_y = ui_y;
        this.inx = inx;
    }

    @Override
    protected Vertex clone() {
        Vertex v = new Vertex();
        v.ui_x = ui_x;
        v.ui_y = ui_y;
        v.name = name;
        v.inx = inx;
        v.selected = selected;
        return v;
    }
}

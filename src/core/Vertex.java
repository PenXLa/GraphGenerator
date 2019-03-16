package core;

public class Vertex implements GraphComponent {
    public int ui_x, ui_y;
    public String name;
    public int inx;

    public Vertex(){}

    public Vertex(int ui_x, int ui_y, int inx) {
        this.ui_x = ui_x;
        this.ui_y = ui_y;
        this.inx = inx;
    }
}

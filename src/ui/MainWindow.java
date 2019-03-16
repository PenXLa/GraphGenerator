package ui;

import core.Edge;
import core.Graph;
import core.GraphComponent;
import core.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.Iterator;

public class MainWindow extends JFrame {
    public DrawingPanel drawingArea = new DrawingPanel();

    public MainWindow() {
        this.setTitle("Graph Test Data Generator        --Powered By PXL");
        this.setLayout(new BorderLayout());
        this.add(drawingArea);

        DAMouseListener listener = new DAMouseListener(drawingArea);
        drawingArea.addMouseListener(listener);
        drawingArea.addMouseMotionListener(listener);
        DAKey daklistener = new DAKey(drawingArea);
        this.addKeyListener(daklistener);

        JPanel toolbox = new JPanel();
        this.add(toolbox, BorderLayout.SOUTH);


        toolbox.add(new JLabel("数据格式："));
        JComboBox dataStyle = new JComboBox();
        dataStyle.addItem("按边生成");
        dataStyle.addItem("DFS");
        dataStyle.addItem("BFS");
        dataStyle.addItem("(二叉树)先序遍历");
        dataStyle.addItem("(二叉树)中序遍历");
        dataStyle.addItem("(二叉树)后序遍历");
        toolbox.add(dataStyle);


        JButton generate = new JButton("生成测试数据");
        toolbox.add(generate);
        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                String res="";
                if (dataStyle.getSelectedIndex()==0) {
                    res = Graph.generateGraph();
                } else {
                    int root = Integer.parseInt(JOptionPane.showInputDialog(null, "输入根节点"));

                    if (dataStyle.getSelectedIndex()==1) {
                        res = "暂不支持";
                    } else if (dataStyle.getSelectedIndex()==2) {
                        res = Graph.BFS(root);
                    } else {
                        res = Graph.binaryEnumGraph(root, dataStyle.getSelectedIndex());
                    }
                }
                Transferable tText = new StringSelection(res);
                clip.setContents(tText, null);
            }
        });

        JButton remove = new JButton("删除");
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                daklistener.remove();
            }
        });
        toolbox.add(remove);

        JButton clear = new JButton("清空");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Graph.edges.clear();
                Graph.vertices.clear();
                drawingArea.updateUI();
            }
        });
        toolbox.add(clear);


        JButton help = new JButton("帮助");
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "添加顶点：\t左键单击空白\n\n" +
                                                                "添加边：左键拖动顶点\n\n" +
                                                                "删除：\t选中，然后按下删除按钮\n\n" +
                                                                "移动顶点：\t右键拖动顶点\n\n" +
                                                                "修改权值/编号:\t双击\n\n" +
                                                                "测试数据的格式：\n" +
                                                                "顶点数 边数\n" +
                                                                "出发点 目标点 权值\n\n点击生成测试数据后结果保存在剪贴板中      \n\n\n\n\n" +
                                                                "程序由PXL制作","帮助", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        toolbox.add(help);
    }

    public static void main(String[] args) {
        MainWindow frame = new MainWindow();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public static GraphComponent pointCheck(int x, int y) {
        Iterator<Vertex> iter = Graph.vertices.descendingIterator();

        while(iter.hasNext()) {
            Vertex v = iter.next();
            int rx = x - v.ui_x, ry = y - v.ui_y;
            if (rx*rx + ry*ry < Graph.uiRadius * Graph.uiRadius) {
                return v;
            }
        }


        final double mouseRadius = 10;//边离鼠标距离为2时就算选中
        for (Edge e:Graph.edges) {
            double len=Graph.distance(e.vs, e.ve);
            double vector1x = e.vs.ui_x - x;
            double vector1y = e.vs.ui_y - y;
            double vector2x = e.ve.ui_x - x;
            double vector2y = e.ve.ui_y - y;

            double dis = (Math.abs(vector1x*vector2y - vector2x*vector1y))/len;
            if (dis <= mouseRadius) return e;

        }
        return null;
    }



}







class DrawingPanel extends JPanel {


    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g2d = (Graphics2D)graphics;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font(null, Font.PLAIN, 20));
        g2d.setStroke(new BasicStroke(2f));


        //画边
        for (Edge e: Graph.edges) {
            g2d.setColor((e==Graph.selected)?Color.BLUE:Color.BLACK);
            g2d.drawLine(e.vs.ui_x, e.vs.ui_y, e.ve.ui_x, e.ve.ui_y);

            //*****
            final int arrowLen = 10;//箭头长度
            int arrowHeadX = (int)(e.ve.ui_x-(e.ve.ui_x-e.vs.ui_x)*Graph.uiRadius/Graph.distance(e.vs,e.ve));
            int arrowHeadY = (int)(e.ve.ui_y-(e.ve.ui_y-e.vs.ui_y)*Graph.uiRadius/Graph.distance(e.vs,e.ve));
            double angle = Math.atan2(e.ve.ui_y - e.vs.ui_y, e.ve.ui_x - e.vs.ui_x);
            int aX1 = (int)(-arrowLen*Math.cos(angle+.4)+arrowHeadX);
            int aY1 = (int)(-arrowLen*Math.sin(angle+.4)+arrowHeadY);
            int aX2 = (int)(-arrowLen*Math.cos(angle-.4)+arrowHeadX);
            int aY2 = (int)(-arrowLen*Math.sin(angle-.4)+arrowHeadY);
            g2d.drawLine(arrowHeadX, arrowHeadY, aX1, aY1);
            g2d.drawLine(arrowHeadX, arrowHeadY, aX2, aY2);
            //*****


            g2d.setColor(Color.RED);
            g2d.drawString(e.weight+"", (e.vs.ui_x+e.ve.ui_x)/2, (e.vs.ui_y+e.ve.ui_y)/2);
        }


        //画预览边
        g2d.setColor(Color.BLACK);
        if (Graph.ui_hasNewEdge) g2d.drawLine(Graph.ui_newEdgeHeadV.ui_x, Graph.ui_newEdgeHeadV.ui_y, Graph.ui_newEdgeX, Graph.ui_newEdgeY);

        //画顶点
        for (Vertex v: Graph.vertices) {
            g2d.setColor((v==Graph.selected)?Color.ORANGE:Color.WHITE);
            g2d.fillOval(v.ui_x - Graph.uiRadius, v.ui_y - Graph.uiRadius, Graph.uiRadius * 2, Graph.uiRadius * 2);

            g2d.setColor(Color.BLACK);
            g2d.drawOval(v.ui_x - Graph.uiRadius, v.ui_y - Graph.uiRadius, Graph.uiRadius * 2, Graph.uiRadius * 2);

            drawStr(v.inx+"", v.ui_x, v.ui_y, g2d);

        }
    }


    private void drawStr(String str, int x, int y, Graphics2D g2d) {
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(str);
        int h = fm.getHeight();
        g2d.drawString(str,x - w/2, y+h/4);
    }
}







class DAMouseListener implements MouseListener, MouseMotionListener {
    private Vertex v;
    private Edge edge;
    private DrawingPanel da;
    private int button;//sb MouseDragged不能查e.getButton。。。只能在这写个变量

    public DAMouseListener(DrawingPanel da) {
        this.da = da;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (v!=null) {
            if (button == MouseEvent.BUTTON1){
                Graph.ui_hasNewEdge = true;
                Graph.ui_newEdgeX = e.getX();
                Graph.ui_newEdgeY = e.getY();
            } else {
                v.ui_x = e.getX();
                v.ui_y = e.getY();
            }
            da.updateUI();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        button = e.getButton();

        GraphComponent gc = MainWindow.pointCheck(e.getX(), e.getY());//获取点击到的组建
        if (gc instanceof Vertex) v = (Vertex)gc;//如果是顶点，存起来
        else v=null;//不是就把顶点设为null

        if (gc instanceof Edge) edge = (Edge)gc;//如果是边，存起来
        else edge=null;//不是就把边设为null

        if (e.getButton()==1) {
            if (gc==null) {
                Graph.vertices.add(new Vertex(e.getX(), e.getY(), Graph.vertices.size()));
                da.updateUI();
            } else {
                if (v!=null) {
                    Graph.ui_newEdgeHeadV=v;
                    Graph.selected = v;
                }
                if (edge!=null) {
                    Graph.selected = edge;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        GraphComponent gc = MainWindow.pointCheck(e.getX(), e.getY());//获取点击到的组件
        if (gc instanceof Vertex && v!=null && e.getButton()==MouseEvent.BUTTON1) {
            Vertex ve = (Vertex)gc;
            boolean flag = false;//防止重复
            for (Edge edge:Graph.edges) {
                if (edge.vs==v && edge.ve==ve) {//防止重复
                    flag = true;
                    break;
                }
            }
            if (!flag && v!=gc) Graph.edges.add(new Edge(v, (Vertex)gc, 1));
        }
        Graph.ui_hasNewEdge = false;
        da.updateUI();
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount()==2 ) {
            if (v!=null) {
                int inx = Integer.parseInt(JOptionPane.showInputDialog(null, "输入顶点编号"));
                v.inx = inx;
            }
            if (edge!=null) {
                int w = Integer.parseInt(JOptionPane.showInputDialog(null, "输入权值"));
                edge.weight = w;
            }

            da.updateUI();

        }
    }



    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}

class DAKey extends KeyAdapter {
    DrawingPanel da;

    public DAKey(DrawingPanel da) {
        this.da = da;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                remove();
                break;
        }
    }

    public void remove() {
        if (Graph.selected!=null) {
            if (Graph.selected instanceof Edge) {
                Graph.edges.remove(Graph.selected);
            } else {

                Iterator<Edge> iter = Graph.edges.iterator();
                while (iter.hasNext()) {
                    Edge e = iter.next();
                    if (e.vs==Graph.selected || e.ve==Graph.selected) {
                        iter.remove();
                    }
                }
                Graph.vertices.remove(Graph.selected);
            }
            da.updateUI();
        }
    }

}
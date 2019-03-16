package core;

import java.util.LinkedList;
import java.util.Queue;

public class Graph {
    public static final int uiRadius = 20;//顶点半径
    public static Vertex ui_newEdgeHeadV;
    public static int ui_newEdgeX;//用于画新边的预览
    public static int ui_newEdgeY;//用于画新边的预览
    public static boolean ui_hasNewEdge = false;//用于画新边的预览
    public static GraphComponent selected;
    public static LinkedList<Vertex> vertices = new LinkedList<Vertex>();
    public static LinkedList<Edge> edges = new LinkedList<Edge>();
    public static double distance(Vertex v1, Vertex v2) {
        int t1 = v1.ui_x-v2.ui_x;
        int t2 = v1.ui_y-v2.ui_y;
        return Math.sqrt(t1*t1+t2*t2);
    }


    public static String generateGraph() {
        StringBuffer sb = new StringBuffer(vertices.size()+ " " + edges.size() + "\n");
        for (Edge e:edges) {
            sb.append(e.vs.inx + " " + e.ve.inx + " " + e.weight + "\n");
        }
        return sb.toString();
    }

    //二叉树搜索
    //method=3表示先序，=4表示中序，=5表示后序
    private static StringBuffer enumAns;
    public static String binaryEnumGraph(int root, int method) {
        enumAns = new StringBuffer(vertices.size() + "\n");
        Vertex rt = null;
        for (Vertex v : vertices) if (v.inx == root) {rt = v;break;}
        enum_dfs(rt, method);

        return  enumAns.toString();
    }

    private static void enum_dfs(Vertex v, int method) {
        if (v==null) return;
        Vertex v1 = null;
        Vertex v2 = null;
        for (Edge e : edges) {
            if (e.vs==v) {
                if (v1==null) v1 = e.ve;//第一次检查到子节点
                else {
                    //第二次检查到子节点，根据x值判断是左节点还是右节点
                    if (e.ve.ui_x<v1.ui_x) {
                        v2 = v1;
                        v1 = e.ve;
                    } else {
                        v2 = e.ve;
                    }
                    break;
                }
            }
        }

        switch (method){
            case 3:
                enumAns.append(v.inx+" ");
                enum_dfs(v1, method);
                enum_dfs(v2, method);
                break;
            case 4:
                enum_dfs(v1, method);
                enumAns.append(v.inx+" ");
                enum_dfs(v2, method);
                break;
            case 5:
                enum_dfs(v1, method);
                enum_dfs(v2, method);
                enumAns.append(v.inx+" ");
                break;
        }
    }

    public static String BFS(int root) {
        enumAns = new StringBuffer(vertices.size()+"\n");
        Vertex rt = null;
        for (Vertex v : vertices) if (v.inx == root) {rt = v;break;}

        Queue<Vertex> q = new LinkedList<>();
        q.offer(rt);
        while(!q.isEmpty()) {
            Vertex v = q.poll();
            enumAns.append(v.inx + " ");
            for (Edge e : edges) if (e.vs==v) q.offer(e.ve);
        }
        return enumAns.toString();
    }



}

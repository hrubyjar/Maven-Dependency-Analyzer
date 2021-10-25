package cz.zcu.kiv.utils;

import cz.zcu.kiv.offscreen.api.BaseEdge;

import java.util.List;

public class Edge extends BaseEdge {
    private List<SubEdgeInfo> subedgeInfo;

    public Edge(int id, int from, int to, String text, List<SubEdgeInfo> subedgeInfo) {
        super(id, from, to, text);
        this.subedgeInfo = subedgeInfo;
    }

    public List<SubEdgeInfo> getSubedgeInfo() {
        return this.subedgeInfo;
    }

    public void setSubEdgeInfo(List<SubEdgeInfo> subedgeInfo) {
        this.subedgeInfo = subedgeInfo;
    }
}
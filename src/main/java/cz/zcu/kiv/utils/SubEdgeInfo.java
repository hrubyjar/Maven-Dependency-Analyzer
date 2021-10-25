package cz.zcu.kiv.utils;

import org.json.JSONArray;

import java.util.List;

public class SubEdgeInfo {
    private int archetype;
    private int id;
    private List<String[]> attributes;
    private String edgeSymbol;
    private JSONArray incompatibilities;

    public SubEdgeInfo(int id, int archetype, String edgeSymbol, List<String[]> attributes, JSONArray incompatibilities) {
        this.id = id;
        this.archetype = archetype;
        this.edgeSymbol = edgeSymbol;
        this.attributes = attributes;
        this.incompatibilities = incompatibilities;
    }

    public int getId() {
        return this.id;
    }

    public int getArchetype() {
        return this.archetype;
    }

    public String getEdgeSymbol() {
        return edgeSymbol;
    }

    public List<String[]> getAttributes() {
        return attributes;
    }

    public JSONArray getIncompatibilities() {
        return this.incompatibilities;
    }
}

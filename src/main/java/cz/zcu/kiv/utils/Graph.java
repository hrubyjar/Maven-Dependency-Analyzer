package cz.zcu.kiv.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.zcu.kiv.offscreen.api.*;
import org.apache.log4j.Logger;

public class Graph {
    private Set<Vertex> vertices = new HashSet();
    private List<cz.zcu.kiv.utils.Edge> edges = new LinkedList();
    private List<VertexArchetype> vertexArchetypes;
    private List<EdgeArchetype> edgeArchetypes;
    private List<AttributeType> attributeTypes;
    private Map<String, List<String>> possibleEnumValues;
    private List<Group> groups;
    private List<SideBar> sideBar;
    private String highlightedVertex;
    private String highlightedEdge;
    private final Logger logger = Logger.getLogger(cz.zcu.kiv.utils.Graph.class);

    public Graph() {
    }

    public List<Edge> getEdges() {
        this.logger.trace("ENTRY");
        this.logger.trace("EXIT");
        return this.edges;
    }

    public Set<Vertex> getVertices() {
        this.logger.trace("ENTRY");
        this.logger.trace("EXIT");
        return this.vertices;
    }

    public List<VertexArchetype> getVertexArchetypes() {
        return this.vertexArchetypes;
    }

    public List<EdgeArchetype> getEdgeArchetypes() {
        return this.edgeArchetypes;
    }

    public List<AttributeType> getAttributeTypes() {
        return this.attributeTypes;
    }

    public Map<String, List<String>> getPossibleEnumValues() {
        return this.possibleEnumValues;
    }

    public List<Group> getGroups() {
        return this.groups;
    }

    public List<SideBar> getSideBar() {
        return this.sideBar;
    }

    public String getHighlightedVertex() {
        return this.highlightedVertex;
    }

    public String getHighlightedEdge() {
        return this.highlightedEdge;
    }

    public void setVertexArchetypes(List<VertexArchetype> vertexArchetypes) {
        this.vertexArchetypes = vertexArchetypes;
    }

    public void setEdgeArchetypes(List<EdgeArchetype> edgeArchetypes) {
        this.edgeArchetypes = edgeArchetypes;
    }

    public void setAttributeTypes(List<AttributeType> attributeTypes) {
        this.attributeTypes = attributeTypes;
    }

    public void setPossibleEnumValues(Map<Integer, List<String>> possibleEnumValues) {
        this.possibleEnumValues = new HashMap();
        Iterator var2 = possibleEnumValues.keySet().iterator();

        while(var2.hasNext()) {
            Integer index = (Integer)var2.next();
            this.possibleEnumValues.put("" + index, possibleEnumValues.get(index));
        }

    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setSideBar(List<SideBar> sideBar) {
        this.sideBar = sideBar;
    }

    public void setHighlightedVertex(String highlightedVertex) {
        this.highlightedVertex = highlightedVertex;
    }

    public void setHighlightedEdge(String highlightedEdge) {
        this.highlightedEdge = highlightedEdge;
    }
}


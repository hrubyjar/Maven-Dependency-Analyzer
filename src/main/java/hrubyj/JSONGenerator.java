package hrubyj;

import com.verifa.jacc.ccu.ApiCmpStateResult;
import com.verifa.jacc.cmp.JComparator;
import com.verifa.jacc.javatypes.*;
import com.verifa.jacc.typescmp.CmpResultNode;
import cz.zcu.kiv.offscreen.api.*;
import com.verifa.jacc.ccu.ApiInterCompatibilityResult;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class JSONGenerator {

    private Graph graph;
    private int level;
    private String compInfoJSON;
    private String compInfoInnerJSON;
    private String compInfoJSONNF;
    private Set<String> origins;
    private Properties jaccMessages;

    private static final String NOT_FOUND = "NOT_FOUND";

    public void generateJSON(ApiInterCompatibilityResult comparisonResult, String appFiles, String libFiles, String jsonName) throws Exception {
        this.origins = comparisonResult.getOriginsImportingIncompatibilities();
        this.graph = new Graph();
        this.jaccMessages = new Properties();
        try (InputStream inputStream = JComparator.class.getResourceAsStream("messages.properties")) {
            jaccMessages.load(inputStream);
        } catch (IOException e) {
            System.err.println("Error loading JaCC properties!");
            e.printStackTrace();
        }

        File[] files = ArrayUtils.addAll(new File(appFiles).listFiles(), new File(libFiles).listFiles());
        generateVertices(files);
        createEdges(comparisonResult);
        JSONObject jsonGraph;
        try {
            System.out.println("Converting graph to JSON!");
            jsonGraph = new JSONObject(this.graph);
        } catch (Exception e) {
            jsonGraph = null;
            System.err.println("Error creating JSON! ");
            e.printStackTrace();
        }

        File json = new File("json_files/" + jsonName + ".json");
        if (jsonGraph != null) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(json))) {
                bufferedWriter.write(jsonGraph.toString());
                bufferedWriter.flush();
            }
        }
    }

    /**
     * This method creates the vertices and adds them to the graph.
     */
    private void generateVertices(File[] files) {
        List<VertexArchetype> vertexArchetypes = new ArrayList<>();
        vertexArchetypes.add(new VertexArchetype("Jar file", "", ""));
        this.graph.setVertexArchetypes(vertexArchetypes);

        Vertex vertex;
        int id = 0;
        String name = "";
        int archetypeIndex = 0;
        String text = "";
        List<String[]> attributes = new ArrayList<>();

        for (File file : files) {
            name = file.getName();
            vertex = new Vertex(id, name, archetypeIndex, text, attributes);
            this.graph.getVertices().add(vertex);
            id++;
        }
    }

    /**
     * This method creates the edges and adds them to the graph.
     */
    private void createEdges(ApiInterCompatibilityResult comparisonResult) throws Exception {
        EdgeArchetype edgeArchetype = new EdgeArchetype("Incompatibility Cause", "");
        AttributeType attributeType = new AttributeType("Incompatibility", AttributeDataType.STRING, "");
        this.graph.setEdgeArchetypes(new ArrayList<>(Collections.singleton(edgeArchetype)));
        this.graph.setAttributeTypes(new ArrayList<>(Collections.singleton(attributeType)));
        SubedgeInfo subedgeInfo;
        Set<ApiCmpStateResult> apiCmpResults;
        Set<JClass> incompatibleClasses;
        Edge edge;
        int id = 0;

        // compatibility checking START
        try {
            this.compInfoJSON = "";
            this.compInfoInnerJSON = "";
            this.compInfoJSONNF = "";
            String NFClassName = "";
            String firstOrigin = "";
            String secondOrigin = "";
            String firstOriginNF = "";
            String secondOriginNF = "";
            List<String> NFClasses = new ArrayList<>();
            for (String origin : this.origins) {
                incompatibleClasses = comparisonResult.getClassesImportingIncompatibilities(origin);
                System.out.println("Processing origin " + origin);
                for (JClass incompatibleClass : incompatibleClasses) {
                    apiCmpResults = comparisonResult.getIncompatibleResults(incompatibleClass, origin);
                    for (ApiCmpStateResult apiCmpResult : apiCmpResults) {
                        if (apiCmpResult.getResult() != null && apiCmpResult.getResult().getSecondObject() != null) {
                            List<CmpResultNode> children = apiCmpResult.getResult().getChildren();

                            this.level = 0;
                            this.compInfoInnerJSON += "";
                            this.findCompatibilityCause(children, incompatibleClass.getName(), apiCmpResult.getResult().getSecondObject().getName(), "");
                            this.compInfoInnerJSON += ",";
                            if (firstOrigin.equals("") && secondOrigin.equals("")) {
                                firstOrigin = apiCmpResult.getResult().getFirstObject().getOrigin();
                                secondOrigin = apiCmpResult.getResult().getSecondObject().getOrigin();
                            }
                        } else {
                            if (apiCmpResult.getResult() != null) {
                                NFClassName = apiCmpResult.getResult().getFirstObject().getName();
                                if (!NFClassName.equals("") && !NFClasses.contains(NFClassName)) {
                                    NFClasses.add(NFClassName);
                                    this.compInfoJSONNF += "{theClass: \"" + NFClassName + "\", incomps: [ ";
                                    this.compInfoJSONNF += "]},";
                                }

                                if (firstOriginNF.equals("") && secondOriginNF.equals("")) {
                                    firstOriginNF = apiCmpResult.getResult().getFirstObject().getOrigin();
                                    secondOriginNF = NOT_FOUND;
                                    Vertex vertex = new Vertex(id, NOT_FOUND, 0, "", new ArrayList<>());
                                    this.graph.getVertices().add(vertex);
                                }
                            }
                        }

                    }

                    if (!this.compInfoInnerJSON.equals(",") && !this.compInfoInnerJSON.equals("")) {
                        this.compInfoJSON += "{theClass: \"" + incompatibleClass.getName() + "\", incomps: [ ";
                        this.compInfoJSON += this.compInfoInnerJSON;
                        this.compInfoJSON += "]},";
                    }
                    this.compInfoInnerJSON = "";

                }
                NFClasses.clear();

                if (!firstOrigin.equals("")) {
                    String[] attribute = {"Incompatibility",  getCompatibilityInfo(new JSONObject(this.compInfoJSON)) };
                    subedgeInfo = new SubedgeInfo(id, 0,  new ArrayList<>(Collections.singleton(attribute)));
                    edge = new Edge(id, getVertexId(firstOrigin), getVertexId(secondOrigin), "", new ArrayList<>(Collections.singleton(subedgeInfo)));
                    System.out.println("Edge " + edge.getId() + " created: " + edge.getFrom() + " -> " + edge.getTo());
                    this.graph.getEdges().add(edge);
                    id++;
                }

                if (!firstOriginNF.equals("")) {
                    String[] attribute = {"Incompatibility", getCompatibilityInfo(new JSONObject(this.compInfoJSONNF)) };
                    subedgeInfo = new SubedgeInfo(id, 0,  new ArrayList<>(Collections.singleton(attribute)));
                    edge = new Edge(id, getVertexId(secondOriginNF), getVertexId(firstOriginNF), "", new ArrayList<>(Collections.singleton(subedgeInfo)));
                    System.out.println("Edge " + edge.getId() + " created: " + edge.getFrom() + " -> " + edge.getTo());
                    this.graph.getEdges().add(edge);
                    id++;
                }

                firstOrigin = "";
                secondOrigin = "";
                firstOriginNF = "";
                secondOriginNF = "";
                this.compInfoJSON = "";
                this.compInfoJSONNF = "";

            }
        } catch (Exception e) {
            System.err.println("ERROR createEdges()");
            System.err.println(e.toString());
            throw new Exception(e);
        }
        System.out.println("Creating edges done!");
    }


    private int getVertexId(String file) {
        for (Vertex vertex : this.graph.getVertices()) {
            if (file.contains(vertex.getName())) {
                return vertex.getId();
            }
        }
        throw new IllegalStateException("Vertex " + file + " not found!");
    }


    public void findCompatibilityCause(List<CmpResultNode> children, String className, String jarName, String corrStrategy) {
        for (CmpResultNode child : children) {
            Object o = child.getResult().getFirstObject();
            this.compInfoInnerJSON += "{desc: {level: \"" + String.valueOf(this.level);

            if (child.isIncompatibilityCause()) {

                if (o instanceof HasName) {
                    this.compInfoInnerJSON += "\", name: \"" + this.jaccMessages.getProperty(child.getContentCode()) + ": " + ((HasName) o).getName();

                    this.compInfoInnerJSON
                            += (!child.getResult().getInherentDiff().name().equals("DEL") ? "\", objectNameFirst: \"" + ((HasName) o).getName() : "")
                            + (!child.getResult().getInherentDiff().name().equals("DEL") ? "\", objectNameSecond: \"" + ((HasName) child.getResult().getSecondObject()).getName() : "");
                } else {
                    if (o instanceof JMethod) {
                        this.compInfoInnerJSON += "\", name: \"M " + getCompleteMethodName(o);
                    } else {
                        this.compInfoInnerJSON += "\", name: \"" + this.jaccMessages.getProperty(child.getContentCode());
                    }

                    this.compInfoInnerJSON
                            += (!child.getResult().getInherentDiff().name().equals("DEL") ? "\", objectNameFirst: \"" + o.toString() : "")
                            + (!child.getResult().getInherentDiff().name().equals("DEL") ? "\", objectNameSecond: \"" + child.getResult().getSecondObject().toString() : "");

                }

                this.compInfoInnerJSON += "\", className: \"" + className;
                this.compInfoInnerJSON += "\", jarName: \"" + jarName;

                String incompName = this.getIncompatibilityName(child, corrStrategy);
                if (incompName.equals("")) {
                    this.compInfoInnerJSON += "\", incompName: \"Incompatible " + this.jaccMessages.getProperty(child.getContentCode()) + " -> " + corrStrategy; //child.getResult().getStrategy().name();
                } else {
                    this.compInfoInnerJSON += "\", incompName: \"" + incompName + (child.getResult().getInherentDiff().name().equals("DEL") ? " is missing -> " + child.getResult().getStrategy().name() : "");
                }

                this.compInfoInnerJSON += "\", isIncompCause: \"" + String.valueOf(child.isIncompatibilityCause())
                        + "\", strategy: \"" + child.getResult().getStrategy().name()
                        + "\", difference: \"" + child.getResult().getInherentDiff().name() + "\"}, subtree:";

            } else {
                if (o instanceof JMethod) {
                    this.compInfoInnerJSON += "\", name: \"<span class='entity'>M</span> " + getCompleteMethodName(o);
                } else if (o instanceof JField) {
                    this.compInfoInnerJSON += "\", name: \"<span class='entity'>F</span> " + getShortName2(o.toString());

                } else if (o instanceof HasName) {
                    this.compInfoInnerJSON += "\", name: \"" + this.jaccMessages.getProperty(child.getContentCode()) + ": " + ((HasName) o).getName();
                } else {
                    this.compInfoInnerJSON += "\", name: \"" + this.jaccMessages.getProperty(child.getContentCode());
                }

                this.compInfoInnerJSON += "\", isIncompCause: \"" + String.valueOf(child.isIncompatibilityCause()) + "\"}, subtree: ";

            }
            if (!child.getResult().childrenCompatible()) {
                String strategy = "";
                if (this.level == 1) {
                    strategy = child.getResult().getStrategy().name();
                }
                this.level++;
                this.compInfoInnerJSON += "[";
                // tady se podivat na child.getResult()
                this.findCompatibilityCause(child.getResult().getChildren(), className, jarName, strategy);
                this.level--;
                this.compInfoInnerJSON += "],";
            } else {
                this.compInfoInnerJSON += "[],";
            }
            this.compInfoInnerJSON += "},";
        }

    }

    private String getShortName(String longName) {
        return longName.substring(longName.lastIndexOf('.') + 1);
    }

    private String getShortName2(String longName) {
        return longName.substring(longName.lastIndexOf(':') + 1);
    }

    private String getCompleteMethodName(Object o) {
        String methodName = "";
        methodName += getShortName(((JMethod) o).getReturnType().getName());
        methodName += " " + ((JMethod) o).getName();
        methodName += " (";
        for (JType type : ((JMethod) o).getParameterTypes()) {
            methodName += getShortName(type.getName()) + ", ";
        }
        methodName = methodName.replaceAll(", $", "");
        methodName += ")";
        return methodName;
    }

    private String getIncompatibilityName(CmpResultNode child, String corrStrategy) {
        Object o = child.getResult().getFirstObject();

        String incompName = "";
        switch (child.getContentCode()) {
            case "cmp.child.method.return.type":
                incompName = this.jaccMessages.getProperty(child.getContentCode()) + " different -> " + corrStrategy;
                break;
            case "cmp.child.method.param.type": {
                if (o instanceof HasName) {
                    incompName = "Parameter " + getShortName(((HasName) o).getName()) + " different -> " + corrStrategy;
                } else {
                    incompName = "Parameter " + getShortName(o.toString()) + " different -> " + corrStrategy;
                }
            }
            break;
            case "cmp.child.method.invocation":
                incompName = "Invoke Virtual" + " -> " + child.getResult().getStrategy().name();
                ;
                break;
            case "cmp.child.method":
                incompName = "<span class='entity'>M</span> " + getCompleteMethodName(o);
                break;
            case "cmp.child.constructor":
                incompName = "<span class='entity'>C</span> " + getCompleteMethodName(o);
                break;
            case "cmp.child.field":
                incompName = "<span class='entity'>F</span> " + getCompleteMethodName(o);
                break;
            case "cmp.child.modifier":
                incompName = "<span class='entity'>P</span> " + o.toString() + " -> " + corrStrategy;
                break;
            default:
                incompName = "";
                break;
        }

        return incompName;
    }


    String compatibilityTooltip = "";

    /**
     * Return HTML for incompatibility tooltip
     */
    private String getCompatibilityInfo(JSONObject data) {

        compatibilityTooltip = "";
        for (int i = 0; i < data.length(); i++) {
            String Class = data.getString("theClass");
            compatibilityTooltip += "<li><strong>" + Class + "</strong><ul>";
            JSONArray incomps = data.getJSONArray("incomps");
            for (int j = 0; j < incomps.length(); j++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = incomps.getJSONObject(j);
                } catch (JSONException e) {
                    System.out.println("Not JSON object!");
                }
                if (jsonObject != null) {
                    parseCompatibilityInfo(jsonObject);
                }
            }
            compatibilityTooltip += "</ul></li>";
        }
        compatibilityTooltip += "";
        return compatibilityTooltip;
    }

    /**
     * Traverses incompatibility JSON object and creates HTML nested list
     */
    private void parseCompatibilityInfo(JSONObject data) {
        JSONObject desc = data.getJSONObject("desc");
        JSONArray subtree = data.getJSONArray("subtree");

        if(desc.getString("isIncompCause") == "true") {
            compatibilityTooltip += "<li><strong class=\"incomp\">" + desc.getString("incompName") + "</strong>";
            compatibilityTooltip += "<ul class=\"compatibility-list\">";

            if (desc.getString("difference") != "DEL") {
                compatibilityTooltip += "<li><span><img src=\"images/efp_qtip/provided.png\"> <span class=\"second\">" + desc.getString("objectNameSecond") + "</span></span></li>";
                compatibilityTooltip += "<li><span><img src=\"images/efp_qtip/required.png\"> <span class=\"first\">" + desc.getString("objectNameFirst") + "</span></span></li>";
            }
            compatibilityTooltip += "</ul>";
        } else {
            if (desc.getInt("level") > 0) {
                compatibilityTooltip += "<li><strong>" + desc.getString("name") + "</strong>";
            }
        }

        if (subtree.length() > 0) {
            if (desc.getInt("level") > 0) {
                compatibilityTooltip += "<ul class=\"compatibility-list\">";
            }
            for (int i = 0; i < subtree.length(); i++) {
                JSONObject sub = subtree.getJSONObject(i);
                if (sub.getJSONArray("subtree").length() > 0 || sub.getJSONObject("desc").getString("isIncompCause").equals("true")) {
                    parseCompatibilityInfo(sub);
                }
            }
            if (desc.getInt("level") > 0) {
                compatibilityTooltip += "</ul>";
            }
        }
        if (desc.getInt("level") > 0) {
            compatibilityTooltip += "</li>";
        }
    }


}

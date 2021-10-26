package cz.zcu.kiv.utils;

import com.mysql.cj.xdevapi.JsonArray;
import com.verifa.jacc.ccu.ApiCmpStateResult;
import com.verifa.jacc.cmp.JComparator;
import com.verifa.jacc.javatypes.*;
import com.verifa.jacc.typescmp.CmpResult;
import com.verifa.jacc.typescmp.CmpResultNode;
import com.verifa.jacc.ccu.ApiInterCompatibilityResult;
import com.verifa.jacc.typescmp.CorrectionStrategy;
import cz.zcu.kiv.offscreen.api.*;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Generates JSON file for IMiGEr tool.
 */
public class JSONGenerator {

    /** graph structure */
    private Graph graph;
    /** level of depth */
    private int level;
    /** origins */
    private Set<String> origins;
    /** JaCC messages */
    private Properties jaccMessages;
    /** directory for JSON save */
    private Path jsonDirectory;

    private static final String NOT_FOUND = "NOT_FOUND";

    /**
     * Constructor
     *
     * @throws IOException
     */
    public JSONGenerator() throws IOException {
        this.jsonDirectory = Files.createTempDirectory("json");
    }

    /**
     * Generates JSON graph.
     *
     * @param comparisonResult analysis result
     * @param appFiles path to app files
     * @param libFiles path to lib files
     * @param jsonName future JSON filename
     * @return JSON file
     * @throws IOException
     */
    public File generateJSON(ApiInterCompatibilityResult comparisonResult, String appFiles, String libFiles, String jsonName) throws IOException {
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
            jsonGraph.getJSONArray("attributeTypes").getJSONObject(0).put("dataType", "string");
        } catch (Exception e) {
            jsonGraph = null;
            System.err.println("Error creating JSON! ");
            e.printStackTrace();
        }

        File json = new File(jsonDirectory + File.separator + jsonName + ".json");
        if (jsonGraph != null) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(json))) {
                bufferedWriter.write(jsonGraph.toString());
                bufferedWriter.flush();
            }
        }
        return json;
    }

    /**
     * Generates vertices of the graph.
     *
     * @param files project files
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
        vertex = new Vertex(id, NOT_FOUND, archetypeIndex, text, attributes);
        this.graph.getVertices().add(vertex);
    }

    /**
     * Generates edges of the graph.
     *
     * @param comparisonResult analysis result
     */
    private void createEdges(ApiInterCompatibilityResult comparisonResult)  {
        EdgeArchetype edgeArchetype = new EdgeArchetype("Incompatibility details", "");
        AttributeType attributeType = new AttributeType("Cause", AttributeDataType.STRING, "");
        this.graph.setEdgeArchetypes(new ArrayList<>(Collections.singleton(edgeArchetype)));
        this.graph.setAttributeTypes(new ArrayList<>(Collections.singleton(attributeType)));
        SubEdgeInfo subedgeInfo;
        int id = 0;

        try {
            for (String origin : this.origins) {
                JSONArray incompatibleInfoJSON = new JSONArray();
                JSONArray notFoundInfoJSON = new JSONArray();
                String firstOrigin = "";
                String secondOrigin = "";
                String firstOriginNF = "";
                String secondOriginNF = "";

                System.out.println("Processing origin " + origin);
                Set<JClass> incompatibleClasses = comparisonResult.getClassesImportingIncompatibilities(origin);
                for (JClass incompatibleClass : incompatibleClasses) {
                    JSONArray incompatibilitiesJson = new JSONArray();
                    JSONArray missingClassesJson = new JSONArray();

                    // cyklus pres samotne nekompatibility
                    Set<ApiCmpStateResult> apiCmpResults = comparisonResult.getIncompatibleResults(incompatibleClass, origin);
                    for (ApiCmpStateResult apiCmpResult : apiCmpResults) {
                        this.level = 0;
                        CmpResult<JClass> cmpResult = apiCmpResult.getResult();
                        if (cmpResult != null) {
                            if (cmpResult.getSecondObject() != null) {
                                List<CmpResultNode> children = cmpResult.getChildren();
                                for (CmpResultNode child : children) {
                                    incompatibilitiesJson.put(findIncompatibilityCause(child, ""));
                                }

                                if (firstOrigin.equals("") && secondOrigin.equals("")) {
                                    firstOrigin = cmpResult.getFirstObject().getOrigin();
                                    secondOrigin = cmpResult.getSecondObject().getOrigin();
                                }

                            } else {
                                List<CmpResultNode> children = cmpResult.getChildren();
                                for (CmpResultNode child : children) {
                                    missingClassesJson.put(findIncompatibilityCause(child, ""));
                                }

                                if (firstOriginNF.equals("") && secondOriginNF.equals("")) {
                                    firstOriginNF = cmpResult.getFirstObject().getOrigin();
                                    secondOriginNF = NOT_FOUND;
                                }
                            }
                        } else {
                            firstOriginNF = getOriginName(origin);
                            secondOriginNF = NOT_FOUND;
                            JSONObject descJson = new JSONObject();
                            descJson.put("isIncompCause", true);
                            descJson.put("incompName", "Class " + apiCmpResult.getImportClass().getName() + " not found!");
                            descJson.put("difference", "DEL");
                            descJson.put("level", 0);
                            JSONObject causeJson = new JSONObject();
                            causeJson.put("desc", descJson);
                            causeJson.put("subtree", new JsonArray());
                            missingClassesJson.put(causeJson);
                        }
                    }

                    // incompatible class
                    JSONObject incompatibleClassJson = new JSONObject();
                    incompatibleClassJson.put("theClass", getOriginName(origin));
                    incompatibleClassJson.put("causedBy", incompatibleClass.getName());
                    incompatibleClassJson.put("incomps", incompatibilitiesJson);

                    incompatibleInfoJSON.put(incompatibleClassJson);

                    // not found class
                    JSONObject notFoundClassJson = new JSONObject();
                    notFoundClassJson.put("theClass", getOriginName(origin));
                    notFoundClassJson.put("causedBy", incompatibleClass.getName());
                    notFoundClassJson.put("incomps", missingClassesJson);

                    notFoundInfoJSON.put(notFoundClassJson);
                }

                if (!firstOrigin.equals("")) {
                    subedgeInfo = new SubEdgeInfo(id, 0, "lollipop", new ArrayList<String[]>(),  incompatibleInfoJSON);
                    Edge edge = new Edge(id, getVertexId(firstOrigin), getVertexId(secondOrigin), "", new ArrayList<>(Collections.singleton(subedgeInfo)));
                    this.graph.getEdges().add(edge);
                    id++;
                }

                if (!firstOriginNF.equals("")) {
                    subedgeInfo = new SubEdgeInfo(id, 0, "lollipop", new ArrayList<String[]>(), notFoundInfoJSON);
                    Edge edge = new Edge(id, getVertexId(secondOriginNF), getVertexId(firstOriginNF), "", new ArrayList<>(Collections.singleton(subedgeInfo)));
                    this.graph.getEdges().add(edge);
                    id++;
                }
            }
        } catch (IllegalStateException e) {
            System.err.println("Error while creating edges!");
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
        System.out.println("Creating edges done!");
    }

    /**
     * Returns vertex ID.
     *
     * @param file file
     * @return ID
     */
    private int getVertexId(String file) {
        for (Vertex vertex : this.graph.getVertices()) {
            if (file.contains(vertex.getName())) {
                return vertex.getId();
            }
        }
        throw new IllegalStateException("Vertex " + file + " not found!");
    }

    /**
     * Returns origin name.
     *
     * @param origin origin
     * @return name
     */
    private String getOriginName(String origin) {
        for (int i = origin.length()-1; i >= 0; i--) {
            if (origin.charAt(i) == File.separatorChar) {
                return origin.substring(i+1);
            }
        }
        return "Invalid Name";
    }


    /**
     * Finds incompatibility cause.
     *
     * @param cmpResultNode resultNode
     * @param corrStrategy correction strategy
     * @return incompatibility cause
     */
    public JSONObject findIncompatibilityCause(CmpResultNode cmpResultNode, String corrStrategy) {
        Object o = cmpResultNode.getResult().getFirstObject();

        // cause description
        JSONObject descriptionJson = new JSONObject();
        descriptionJson.put("level", this.level);

        if (o instanceof JMethod) {
            descriptionJson.put("name", "M " + getMethodDeclaration(o));
        } else if (o instanceof JField) {
            descriptionJson.put("name", "F " + getFieldDeclaration(o));
        } else if (o instanceof HasName) {
            descriptionJson.put("name", this.jaccMessages.getProperty(cmpResultNode.getContentCode()) + ": " + ((HasName) o).getName());
        } else {
            descriptionJson.put("name", this.jaccMessages.getProperty(cmpResultNode.getContentCode()));
        }

        descriptionJson.put("contentCode", cmpResultNode.getContentCode());
        descriptionJson.put("propertyName", this.jaccMessages.getProperty(cmpResultNode.getContentCode()));
        descriptionJson.put("isIncompCause", cmpResultNode.isIncompatibilityCause());

        if (o instanceof JPackage) {
            descriptionJson.put("type", "package");
            descriptionJson.put("details", getPackageDetails((JPackage) o));
        } else if (o instanceof JClass) {
            descriptionJson.put("type", "class");
            descriptionJson.put("details", getClassDetails((JClass) o));
        } else if (o instanceof JMethod) {
            descriptionJson.put("type", "method");
            descriptionJson.put("details", getMethodDetails((JMethod) o));
        } else if (o instanceof JField) {
            descriptionJson.put("type", "field");
            descriptionJson.put("details", getFieldDetails((JField) o));
        }

        // incompatibility details
        if (cmpResultNode.isIncompatibilityCause()) {
            if (!cmpResultNode.getResult().getInherentDiff().name().equals("DEL")) {
                if (o instanceof HasName) {
                    descriptionJson.put("objectNameFirst", ((HasName) o).getName());
                    descriptionJson.put("objectNameSecond", ((HasName) cmpResultNode.getResult().getSecondObject()).getName());
                } else {
                    descriptionJson.put("objectNameFirst", o.toString());
                    descriptionJson.put("objectNameSecond", cmpResultNode.getResult().getSecondObject().toString());
                }
            }

            String incompName = this.getIncompatibilityName(cmpResultNode, corrStrategy);
            if (incompName.equals("")) {
                descriptionJson.put("incompName", "Incompatible " + this.jaccMessages.getProperty(cmpResultNode.getContentCode()) + " -> " + corrStrategy); //child.getResult().getStrategy().name();
            } else {
                descriptionJson.put("incompName", incompName + (cmpResultNode.getResult().getInherentDiff().name().equals("DEL") ? " is missing -> " + cmpResultNode.getResult().getStrategy().name() : ""));
            }

            descriptionJson.put("strategy", cmpResultNode.getResult().getStrategy().name());
            descriptionJson.put("difference", cmpResultNode.getResult().getInherentDiff().name());
        }

        // subtree
        JSONArray subtreeJson = new JSONArray();
        if (!cmpResultNode.getResult().childrenCompatible()) {
            this.level++;

            List<CmpResultNode> children = cmpResultNode.getResult().getChildren();
            for (CmpResultNode child : children) {
                CorrectionStrategy strategy = this.level == 1 ? cmpResultNode.getResult().getStrategy() : child.getResult().getStrategy();
                subtreeJson.put(findIncompatibilityCause(child, strategy.name()));
            }

            this.level--;
        }

        JSONObject causeJson = new JSONObject();
        causeJson.put("desc", descriptionJson);
        causeJson.put("subtree", subtreeJson);

        return causeJson;
    }

    /**
     * Returns package details.
     *
     * @param pacckage package
     * @return details
     */
    private JSONObject getPackageDetails(JPackage pacckage) {
        JSONObject detailsJson = new JSONObject();
        detailsJson.put("name", pacckage.getName());

        return detailsJson;
    }

    /**
     * Returns class details.
     *
     * @param classs class
     * @return details
     */
    private JSONObject getClassDetails(JClass classs) {
        JSONObject detailsJson = new JSONObject();
        detailsJson.put("name", classs.getShortName());
        detailsJson.put("package", classs.getPackage().getName());
        detailsJson.put("enum", classs.isEnum());
        detailsJson.put("interface", classs.isInterface());
        detailsJson.put("annotation", classs.isAnnotation());
        detailsJson.put("abstract", classs.getModifiers().isAbstract());
        detailsJson.put("final", classs.getModifiers().isFinal());
        detailsJson.put("static", classs.getModifiers().isStatic());

        return detailsJson;
    }

    /**
     * Returns method details.
     *
     * @param method method
     * @return details
     */
    private JSONObject getMethodDetails(JMethod method) {
        JSONObject detailsJson = new JSONObject();
        detailsJson.put("name", method.getName());
        detailsJson.put("returnType", method.getReturnType().getName());
        detailsJson.put("constructor", method.isConstructor());

        List<JType> exceptionTypes = method.getExceptionTypes();
        JSONArray exceptionTypeNames = new JSONArray();
        for (JType type : exceptionTypes) {
            exceptionTypeNames.put(type.getName());
        }
        detailsJson.put("exceptions", exceptionTypeNames);
        List<JType> parameterTypes = method.getParameterTypes();
        JSONArray parameterTypeNames = new JSONArray();
        for (JType type : parameterTypes) {
            parameterTypeNames.put(type.getName());
        }
        detailsJson.put("paramTypes", parameterTypeNames);
        detailsJson.put("abstract", method.getModifiers().isAbstract());
        detailsJson.put("final", method.getModifiers().isFinal());
        detailsJson.put("static", method.getModifiers().isStatic());
        detailsJson.put("synchronized", method.getModifiers().isSynchronized());

        return detailsJson;
    }

    /**
     * Returns field details.
     *
     * @param field field
     * @return details
     */
    private JSONObject getFieldDetails(JField field) {
        JSONObject detailsJson = new JSONObject();
        detailsJson.put("name", field.getName());
        detailsJson.put("type", field.getType().getName());
        detailsJson.put("initialValue", field.getInitialValue());
        detailsJson.put("abstract", field.getModifiers().isAbstract());
        detailsJson.put("final", field.getModifiers().isFinal());
        detailsJson.put("static", field.getModifiers().isStatic());

        return detailsJson;
    }

    /**
     * Returns short name.
     *
     * @param longName long name
     * @return short name
     */
    private String getShortName(String longName) {
        return longName.substring(longName.lastIndexOf('.') + 1);
    }

    /**
     * Returns incompatibility name.
     *
     * @param child child node
     * @param corrStrategy strategy
     * @return incompatibility name
     */
    private String getIncompatibilityName(CmpResultNode child, String corrStrategy) {
        Object o = child.getResult().getFirstObject();

        String incompName;
        switch (child.getContentCode()) {
            case "cmp.child.class":
                if (o instanceof HasName) {
                    incompName = "Class " + ((HasName) o).getName();
                } else {
                    incompName = "Class " + o.toString();
                }
                break;
            case "cmp.child.method.return.type":
                incompName = this.jaccMessages.getProperty(child.getContentCode()) + " different -> " + corrStrategy;
                break;
            case "cmp.child.method.param.type":
                if (o instanceof HasName) {
                    incompName = "Parameter " + getShortName(((HasName) o).getName()) + " different -> " + corrStrategy;
                } else {
                    incompName = "Parameter " + getShortName(o.toString()) + " different -> " + corrStrategy;
                }
                break;
            case "cmp.child.method.invocation":
                incompName = "Invoke Virtual" + " -> " + child.getResult().getStrategy().name();
                break;
            case "cmp.child.method":
                incompName = "<span class='entity'>M</span> " + getMethodDeclaration(o);
                break;
            case "cmp.child.constructor":
                incompName = "<span class='entity'>C</span> " + getMethodDeclaration(o);
                break;
            case "cmp.child.field":
                incompName = "<span class='entity'>F</span> " + getFieldDeclaration(o);
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

    /**
     * Returns method declaration.
     *
     * @param o method
     * @return method declaration
     */
    private String getMethodDeclaration(Object o) {
        String methodName = "";
        methodName += getShortName(((JMethod) o).getReturnType().getName());
        methodName += " " + ((JMethod) o).getName();
        methodName += "(";
        for (JType type : ((JMethod) o).getParameterTypes()) {
            methodName += getShortName(type.getName()) + ", ";
        }
        methodName = methodName.replaceAll(", $", "");
        methodName += ")";
        return methodName;
    }

    /**
     * Returns field declaration.
     *
     * @param o field
     * @return field declaration
     */
    private String getFieldDeclaration(Object o) {
        String fieldDeclaration = "";
        fieldDeclaration += getShortName(((JField) o).getType().getName());
        fieldDeclaration += " " + ((JField) o).getName();
        return fieldDeclaration;
    }

}

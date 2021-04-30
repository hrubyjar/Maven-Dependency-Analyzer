package hrubyj;

import java.io.*;

public class StatsHandler {

    private final int VALUE_INDEX = 1;
    private final String VALUE_SEPARATOR = ";";
    private Integer incompatible = 0;
    private Integer redundant = 0;
    private Integer must_remove = 0;
    private Integer compatible = 0;
    private Integer C1 = 0;
    private Integer C2 = 0;
    private Integer C3 = 0;
    private Integer M1 = 0;
    private Integer M2 = 0;
    private Integer F1 = 0;
    private Integer F2 = 0;
    private Integer MOD = 0;
    private Integer MM1 = 0;
    private Integer F7 = 0;

    public void saveToGlobalStats(String filename) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            incompatible += Integer.parseInt(getValue(bufferedReader.readLine()));
            redundant += Integer.parseInt(getValue(bufferedReader.readLine()));
            must_remove += Integer.parseInt(getValue(bufferedReader.readLine()));
            compatible += Integer.parseInt(getValue(bufferedReader.readLine()));
            C1 += Integer.parseInt(getValue(bufferedReader.readLine()));
            C2 += Integer.parseInt(getValue(bufferedReader.readLine()));
            C3 += Integer.parseInt(getValue(bufferedReader.readLine()));
            M1 += Integer.parseInt(getValue(bufferedReader.readLine()));
            M2 += Integer.parseInt(getValue(bufferedReader.readLine()));
            F1 += Integer.parseInt(getValue(bufferedReader.readLine()));
            F2 += Integer.parseInt(getValue(bufferedReader.readLine()));
            MOD += Integer.parseInt(getValue(bufferedReader.readLine()));
            MM1 += Integer.parseInt(getValue(bufferedReader.readLine()));
            F7 += Integer.parseInt(getValue(bufferedReader.readLine()));
        } catch (IOException e) {
            System.out.println("Error reading stats! " + e);
        }
    }

    private String getValue(String line) {
        return line.split(VALUE_SEPARATOR)[VALUE_INDEX];
    }

    public Integer getIncompatible() {
        return incompatible;
    }

    public Integer getRedundant() {
        return redundant;
    }

    public Integer getMust_remove() {
        return must_remove;
    }

    public Integer getCompatible() {
        return compatible;
    }

    public Integer getC1() {
        return C1;
    }

    public Integer getC2() {
        return C2;
    }

    public Integer getC3() {
        return C3;
    }

    public Integer getM1() {
        return M1;
    }

    public Integer getM2() {
        return M2;
    }

    public Integer getF1() {
        return F1;
    }

    public Integer getF2() {
        return F2;
    }

    public Integer getMOD() {
        return MOD;
    }

    public Integer getMM1() {
        return MM1;
    }

    public Integer getF7() {
        return F7;
    }
}

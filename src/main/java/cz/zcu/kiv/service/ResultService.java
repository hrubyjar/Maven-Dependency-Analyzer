package cz.zcu.kiv.service;

import cz.zcu.kiv.service.vo.ResultVO;

import java.io.*;

/**
 * Process results of the analysis.
 */
public class ResultService {

    /**
     * Loads results from TXT file.
     *
     * @param resultPath path to result
     * @return result
     */
    public ResultVO loadResult(String resultPath) {
        ResultVO result = new ResultVO();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(resultPath))) {
            result.setIncompatible(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setRedundant(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setMust_remove(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setCompatible(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setC1(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setC2(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setC3(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setM1(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setM2(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setF1(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setF2(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setMOD(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setMM1(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setF7(Integer.parseInt(getValue(bufferedReader.readLine())));
            result.setScore(new ScoreService().countScore(result));
        } catch (IOException e) {
            System.out.println("Error reading stats! " + e);
        }
        return result;
    }

    /**
     * Returns value on the line.
     *
     * @param line line
     * @return value
     */
    private String getValue(String line) {
        int VALUE_INDEX = 1;
        String VALUE_SEPARATOR = ";";
        return line.split(VALUE_SEPARATOR)[VALUE_INDEX];
    }

}

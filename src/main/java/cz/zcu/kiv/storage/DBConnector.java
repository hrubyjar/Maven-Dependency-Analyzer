package cz.zcu.kiv.storage;

import cz.zcu.kiv.service.vo.ResultVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;

/**
 * MySql Database connector
 */
public class DBConnector {

    /**
     * Stores the result in the database.
     *
     * @param result analysis result
     * @param json json graph
     * @param html_report html report
     */
    public void insertNewResult(ResultVO result, String json, String html_report) {
        String insertSQL = "insert into results (repository_name, incompatible, compatible, redundant, must_remove," +
                " c1, c2, c3, m1, m2, f1, f2, modd, mm1, f7, score, json_graph, html_report)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/results", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            File file = new File(html_report);
            FileInputStream input = new FileInputStream(file);
            File jsonFile = new File(json);
            FileInputStream jsonStream = new FileInputStream(jsonFile);

            pstmt.setString(1, result.getRepositoryName());
            pstmt.setInt(2, result.getIncompatible());
            pstmt.setInt(3, result.getCompatible());
            pstmt.setInt(4, result.getRedundant());
            pstmt.setInt(5, result.getMust_remove());
            pstmt.setInt(6, result.getC1());
            pstmt.setInt(7, result.getC2());
            pstmt.setInt(8, result.getC3());
            pstmt.setInt(9, result.getM1());
            pstmt.setInt(10, result.getM2());
            pstmt.setInt(11, result.getF1());
            pstmt.setInt(12, result.getF2());
            pstmt.setInt(13, result.getMOD());
            pstmt.setInt(14, result.getMM1());
            pstmt.setInt(15, result.getF7());
            pstmt.setInt(16, result.getScore());
            pstmt.setBlob(17, jsonStream);
            pstmt.setBlob(18, input);

            System.out.println("Storing the result in the database.");
            pstmt.execute();

        } catch (SQLException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


}

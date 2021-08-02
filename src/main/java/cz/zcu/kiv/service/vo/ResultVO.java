package cz.zcu.kiv.service.vo;

/**
 * Value Object for result of the analysis
 */
public class ResultVO {

    private String repositoryName;
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
    private Integer score = 0;

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Integer getIncompatible() {
        return incompatible;
    }

    public void setIncompatible(Integer incompatible) {
        this.incompatible = incompatible;
    }

    public Integer getRedundant() {
        return redundant;
    }

    public void setRedundant(Integer redundant) {
        this.redundant = redundant;
    }

    public Integer getMust_remove() {
        return must_remove;
    }

    public void setMust_remove(Integer must_remove) {
        this.must_remove = must_remove;
    }

    public Integer getCompatible() {
        return compatible;
    }

    public void setCompatible(Integer compatible) {
        this.compatible = compatible;
    }

    public Integer getC1() {
        return C1;
    }

    public void setC1(Integer c1) {
        C1 = c1;
    }

    public Integer getC2() {
        return C2;
    }

    public void setC2(Integer c2) {
        C2 = c2;
    }

    public Integer getC3() {
        return C3;
    }

    public void setC3(Integer c3) {
        C3 = c3;
    }

    public Integer getM1() {
        return M1;
    }

    public void setM1(Integer m1) {
        M1 = m1;
    }

    public Integer getM2() {
        return M2;
    }

    public void setM2(Integer m2) {
        M2 = m2;
    }

    public Integer getF1() {
        return F1;
    }

    public void setF1(Integer f1) {
        F1 = f1;
    }

    public Integer getF2() {
        return F2;
    }

    public void setF2(Integer f2) {
        F2 = f2;
    }

    public Integer getMOD() {
        return MOD;
    }

    public void setMOD(Integer MOD) {
        this.MOD = MOD;
    }

    public Integer getMM1() {
        return MM1;
    }

    public void setMM1(Integer MM1) {
        this.MM1 = MM1;
    }

    public Integer getF7() {
        return F7;
    }

    public void setF7(Integer f7) {
        F7 = f7;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}

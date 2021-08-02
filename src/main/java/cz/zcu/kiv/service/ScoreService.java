package cz.zcu.kiv.service;

import cz.zcu.kiv.service.vo.ResultVO;

/**
 * Counts score of the repository according to the results of the analysis.
 */
public class ScoreService {

    /** C1 - Missing Classes */
    private final int C1 = 1;
    /** C2 - Incompatible Classes */
    private final int C2 = 2;
    /** C3 - Interface/Class */
    private final int C3 = 10;
    /** M1 - Missing Methods */
    private final int M1 = 10;
    /** M2 - Incompatible Methods */
    private final int M2 = 10;
    /** F1 - Missing Fields */
    private final int F1 = 5;
    /** F2 - Incompatible Fields */
    private final int F2 = 10;
    /** MOD - Modifiers */
    private final int MOD = 2;
    /** M.M1 - Non-Static/Static Methods */
    private final int MM1 = 10;
    /** F7 - Non-Static/Static Fields */
    private final int F7 = 5;


    /**
     * Counts score of the repository.
     *
     * @param result analysis result
     * @return score
     */
    public int countScore(ResultVO result) {
        return (result.getC1() * C1) + (result.getC2() * C2) + (result.getC3() * C3)+ (result.getM1() * M1)
                + (result.getM2() * M2)+ (result.getF1() * F1)+ (result.getF2() * F2)+ (result.getF7() * F7)+
                (result.getMOD() * MOD)+ (result.getMM1() * MM1);
    }


}

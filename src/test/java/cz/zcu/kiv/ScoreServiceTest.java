package cz.zcu.kiv;

import cz.zcu.kiv.service.ScoreService;
import cz.zcu.kiv.service.vo.ResultVO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ScoreServiceTest {


    @Test
    public void testScore() {
        ScoreService fileSearcher = new ScoreService();

        ResultVO resultVO = new ResultVO();
        resultVO.setC1(1);
        resultVO.setC2(2);
        resultVO.setC3(4);
        resultVO.setM1(3);
        resultVO.setM2(1);
        resultVO.setMM1(2);
        resultVO.setMOD(1);
        resultVO.setF1(5);
        resultVO.setF2(2);
        resultVO.setF7(2);

        int score = fileSearcher.countScore(resultVO);

        assertEquals(162, score);
    }

}

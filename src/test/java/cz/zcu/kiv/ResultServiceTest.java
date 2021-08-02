package cz.zcu.kiv;

import cz.zcu.kiv.service.ResultService;
import cz.zcu.kiv.service.ScoreService;
import cz.zcu.kiv.service.vo.ResultVO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ResultServiceTest {


    @Test
    public void testResult() {
        ResultVO resultVO = new ResultService().loadResult("src/test/java/cz/zcu/kiv/data/test.txt");

        assertEquals(new Integer(2), resultVO.getIncompatible());
        assertEquals(new Integer(3), resultVO.getRedundant());
        assertEquals(new Integer(4), resultVO.getMust_remove());
        assertEquals(new Integer(1), resultVO.getCompatible());

        assertEquals(new Integer(5), resultVO.getC1());
        assertEquals(new Integer(4), resultVO.getC2());
        assertEquals(new Integer(3), resultVO.getC3());
        assertEquals(new Integer(2), resultVO.getM1());
        assertEquals(new Integer(1), resultVO.getM2());
        assertEquals(new Integer(1), resultVO.getF1());
        assertEquals(new Integer(2), resultVO.getF2());
        assertEquals(new Integer(3), resultVO.getMOD());
        assertEquals(new Integer(4), resultVO.getMM1());
        assertEquals(new Integer(5), resultVO.getF7());
    }

}

package cz.zcu.kiv;

import cz.zcu.kiv.utils.FileSearcher;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class FileSearcherTest {


    @Test
    public void testSearch() {
        FileSearcher fileSearcher = new FileSearcher("src/test/java/cz/zcu/kiv/data/");
        List<File> appFiles = fileSearcher.findAppFiles();
        List<File> libFiles = fileSearcher.findLibFiles();

        assertTrue(appFiles.size() == 5);
        assertTrue(libFiles.size() == 2);
    }

}

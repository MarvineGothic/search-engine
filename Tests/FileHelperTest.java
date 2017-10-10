import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileHelperTest {

    /**
     * Test if the FileHelper skips sites that does not contain a title or any words
     */
    @Test
    void fileHelperIncompleteSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "incompleteSites.txt";
        List<Website> sites = FileHelper.parseFile(path);
        assertEquals(sites.size(), 3);
    }

    /**
     * Test if the FileHelper skips
     */
    @Test
    void fileHelperDuplicateSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "duplicateSites.txt";
        List<Website> sites = FileHelper.parseFile(path);
        assertEquals(sites, null);
    }


}
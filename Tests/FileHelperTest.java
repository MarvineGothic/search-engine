import Main.FileHelper;
import Main.Website;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class FileHelperTest {

    /**
     * Test if the Main.FileHelper skips sites that does not contain a title or any words
     */
    @Test
    void fileHelperIncompleteSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "incompleteSites.txt";
        List<Website> sites = FileHelper.parseFile(path);
        assertEquals(sites != null, true);
        assertEquals(sites.size(), 3);
    }

    /**
     * Test if the Main.FileHelper returns null when trying to load files with duplicate websites
     */
    @Test
    void fileHelperDuplicateSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "duplicateSites.txt";
        List<Website> sites = FileHelper.parseFile(path);
        assertEquals(sites, null);
    }

    /**
     * Test if the Main.FileHelper returns null (error) if a webpage has more more words on a line
     */
    @Test
    void fileHelperSingleWordsTest() {
        String path;
        List<Website> sites;

        // We allow for multiple word pr line for the title
        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiTitleLineSites.txt";
        sites = FileHelper.parseFile(path);
        assertNotEquals(sites, null);

        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiUrlLineSites.txt";
        sites = FileHelper.parseFile(path);
        assertEquals(sites, null);

        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiWordLineSites.txt";
        sites = FileHelper.parseFile(path);
        assertEquals(sites, null);

    }

}
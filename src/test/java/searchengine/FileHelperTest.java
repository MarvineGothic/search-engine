package searchengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileHelperTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(null);
        System.setErr(null);
    }

    /**
     * Test if parseFile throws an error message when given a faulty file directory
     */
    @Test
    void fileHelperFaultyDirectoryTest(){
        String[] faultyDirectories = {"C:\\", "C:\\123", "C:\\abc", "C:\\890.txt", "C:\\zyx.txt", "æøå", "123", "test with spaces" +
                ".,/()<"};
        for (String faultyDirectory:faultyDirectories)
        {
            FileHelper.parseFile(faultyDirectory);
            String expectedOutput = "Couldn't load the given file\r\n";
            assertEquals(expectedOutput, outContent.toString(), "Assertion error for "+faultyDirectory);
            outContent.reset();
        }
    }

    /**
     * Test if the FileHelper's parseFile method skips sites that does not contain a title or words.
     */
    @Test
    void fileHelperIncompleteSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "incompleteSites.txt";
        List<Website> sites = FileHelper.parseFile(path);
        ArrayList<String> siteUrls = new ArrayList<>();
        for (Website site:sites) {
            siteUrls.add(site.getUrl());
        }
        assertEquals(siteUrls.toString(), "[https://site1.com/, https://site2.com/, https://site6.com/]");
    }

    /**
     * Test if the FileHelper throws an error message when trying to parse a duplicate website, and skips the duplicate
     */
    @Test
    void fileHelperDuplicateSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "duplicateSites.txt";
        List<Website> sites = FileHelper.parseFile(path);
        String expectedOutput = "ERROR: Duplicate site when parsing file: https://site1.com/\r\n";
        assertEquals(expectedOutput, outContent.toString());
        ArrayList<String> siteUrls = new ArrayList<>();
        for (Website site:sites) {
            siteUrls.add(site.getUrl());
        }
        assertEquals(siteUrls.toString(), "[https://site1.com/, https://site2.com/, https://site3.com/]");
    }


    /**
     * Test if the FileHelper returns an error if a website has more than one words in an url or word line, but allows
     * several words in the title line
     */
    @Test
    void fileHelperSingleWordsTest() {
        String path;

        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiTitleLineSites.txt";
        FileHelper.parseFile(path);

        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiUrlLineSites.txt";
        FileHelper.parseFile(path);

        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiWordLineSites.txt";
        FileHelper.parseFile(path);

        String expectedOutput = "ERROR: parseFile with multiple words in the URL: *PAGE:https://site3.com/ Title\r\n";
        expectedOutput += "ERROR: parseFile with multiple words on the same line: word1 word2\r\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}
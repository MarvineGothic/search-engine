package searchengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileHelperTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }


    /**
     * Test if the Searchengine skips sites that does not contain a title or any words
     */
    @Test
    void fileHelperIncompleteSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "incompleteSites.txt";
        List<Website> sites = FileHelper.parseFile(path);

        assertEquals(sites != null, true);
        assertEquals(sites.size(), 3);
    }

    /**
     * Test if the Searchengine returns null when trying to load files with duplicate websites
     */
    @Test
    void testOutput() {
        System.out.println("testoutput");
        assertEquals("testoutput\r\n", outContent.toString());
    }

    /**
     * Test if the Searchengine returns null when trying to load files with duplicate websites
     */
    @Test
    void fileHelperDuplicateSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "duplicateSites.txt";

        FileHelper.parseFile(path);
        String expectedOutput = "ERROR: Duplicate site when parsing file: https://site1.com/\r\n";
        assertEquals(expectedOutput, outContent.toString());
    }


    /**
     * Test if the Searchengine returns null (error) if a webpage has more more words on a line
     */
    @Test
    void fileHelperSingleWordsTest() {
        String path;
        List<Website> sites;

        // We allow for multiple word pr line for the title
        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiTitleLineSites.txt";
        FileHelper.parseFile(path);

        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiUrlLineSites.txt";
        FileHelper.parseFile(path);

        path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "multiWordLineSites.txt";
        FileHelper.parseFile(path);

        String expectedOutput = "ERROR: parseFile with multiple words int the URL: *PAGE:https://site3.com/ Title\r\n";
        expectedOutput += "ERROR: parseFile with multiple words on the same line: Word1 Word2\r\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}
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
        FileHelper.parseFile("This is very much a faulty file directory!");
        String expectedOutput = "Couldn't load the given file\r\n";
        assertEquals(expectedOutput, outContent.toString());
    }

    /**
     * Test if the FileHelper's parseFile method skips sites that does not contain a title or any words.
     * Case 1a: Check if the method skips null value site lists
     * Case 2a: Check if the method skips empty site lists
     */
    @Test
    void fileHelperIncompleteSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "incompleteSites.txt";
        List<Website> sites = FileHelper.parseFile(path);
        assertEquals(true, sites != null, "Case 1a failed");
        assertEquals(3, sites.size(), "Case 2a failed");
    }

    /**
     * Test if the FileHelper returns null when trying to load files with duplicate websites
     */
    @Test
    void fileHelperDuplicateSitesTest() {
        String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "duplicateSites.txt";

        FileHelper.parseFile(path);
        String expectedOutput = "ERROR: Duplicate site when parsing file: https://site1.com/\r\n";
        assertEquals(expectedOutput, outContent.toString());
    }


    /**
     * Test if the FileHelper returns null (error) if a website has more more words on a line
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

        String expectedOutput = "ERROR: parseFile with multiple words int the URL: *PAGE:https://site3.com/ Title\r\n";
        expectedOutput += "ERROR: parseFile with multiple words on the same line: word1 word2\r\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}
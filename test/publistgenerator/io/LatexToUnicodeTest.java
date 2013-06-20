/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class LatexToUnicodeTest {

    public LatexToUnicodeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of convertToUnicode method, of class LatexToUnicode.
     */
    @Test
    public void testConvertToUnicode() {
        System.out.println("convertToUnicode");

        HashMap<String, String> expected = new LinkedHashMap<>();

        // Simple ones
        expected.put("\\`{o}", "ò");
        expected.put("\\'{o}", "ó");
        expected.put("\\\"{o}", "ö");
        expected.put("\\.{o}", "ȯ");
        expected.put("\\^{o}", "ô");
        expected.put("\\H{o}", "ő");
        expected.put("\\~{o}", "õ");
        expected.put("\\={o}", "ō");

        // In-text
        expected.put("Test\\`otest\\'otest\\^otest\\\"otest\\~otest\\=otest\\.otest",
                "Testòtestótestôtestötestõtestōtestȯtest");
        expected.put("Test\\`{o}test\\'{o}test\\^{o}test\\\"{o}test\\~{o}test\\={o}test\\.{o}test",
                "Testòtestótestôtestötestõtestōtestȯtest");
        expected.put("Test\\u{o}test\\v{s}test\\H{o}test\\c{c}test",
                "Testŏtestštestőtestçtest");

        // Don't touch things in math-mode
        expected.put("$\\'{o}$", "$\\'{o}$");
        expected.put("\\(\\'{o}\\)", "\\(\\'{o}\\)");
        expected.put("Test$\\'{o}$test", "Test$\\'{o}$test");
        expected.put("Test$\\'{o}$test\\'{o}test", "Test$\\'{o}$testótest");
        expected.put("Test\\(\\'{o}\\)test", "Test\\(\\'{o}\\)test");
        expected.put("Test\\(\\'{o}\\)test\\'{o}test", "Test\\(\\'{o}\\)testótest");
        expected.put("Test\\(\\'{o}\\)test$\\'{o}$test", "Test\\(\\'{o}\\)test$\\'{o}$test");

        // Other
        expected.put("Alejandro L{\\'o}pez-Ortiz", "Alejandro L{ó}pez-Ortiz");
        expected.put("<span class=\"author\">A. L{\\'o}pez-Ortiz</span>, <span class=\"author\">P. Morin</span>, and <span class=\"author\">J. Munro</span>",
                     "<span class=\"author\">A. L{ó}pez-Ortiz</span>, <span class=\"author\">P. Morin</span>, and <span class=\"author\">J. Munro</span>");
        expected.put("Discrete {\\&} Computational Geometry", "Discrete {&} Computational Geometry");
        
        for (String input : expected.keySet()) {
            String expectedResult = expected.get(input);
            String result = LatexToUnicode.convertToUnicode(input);

            assertEquals(expectedResult, result);
        }
    }
}
/*
 * Copyright 2014-2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy.algo;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import publy.Console;
import publy.data.bibitem.BibItem;
import publy.data.settings.Settings;

public class PostProcessorTest {

    public PostProcessorTest() {
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
     * Test of applyCrossref method, of class PostProcessor.
     */
    @Test
    public void testApplyCrossref() {
        System.out.println("applyCrossref");

        Settings settings = new Settings();
        settings.getConsoleSettings().setShowWarnings(false);
        Console.setSettings(settings.getConsoleSettings());
        Console.setHeadless(true);

        BibItem item1 = new BibItem("inproceedings", "Kang2011");
        item1.put("author", "Kang, Hongwen and Hebert, Martial and Kanade, Takeo");
        item1.put("title", "Discovering object instances from scenes of Daily Living");
        item1.put("crossref", "iccv-2011");
        item1.put("pages", "762--769");

        BibItem item2 = new BibItem("proceedings", "iccv-2011");
        item2.put("booktitle", "International Conference on Computer Vision (ICCV)");
        item2.put("year", "2011");

        PublicationPostProcessor.postProcess(settings, new ArrayList<>(Arrays.asList(item1, item2)));

        assertEquals("International Conference on Computer Vision (ICCV)", item1.get("booktitle"));
        assertEquals("2011", item1.get("year"));

        BibItem item3 = new BibItem("inproceedings", "no-gnats");
        item3.put("author", "Rocky Gneisser");
        item3.put("title", "No Gnats Are Taken for Granite");
        item3.put("crossref", "gg-proceedings");
        item3.put("pages", "133-139");

        BibItem item4 = new BibItem("proceedings", "gg-proceedings");
        item4.put("title", "The Gnats and Gnus 1988 Proceedings");
        item4.put("editor", "Gerald Ford and Jimmy Carter");
        item4.put("booktitle", "The Gnats and Gnus 1988 Proceedings");
        item4.put("year", "2011");

        PublicationPostProcessor.postProcess(settings, new ArrayList<>(Arrays.asList(item3, item4)));

        assertEquals("No Gnats Are Taken for Granite", item3.get("title"));
        assertEquals("Gerald Ford and Jimmy Carter", item3.get("editor"));
        assertEquals("The Gnats and Gnus 1988 Proceedings", item3.get("booktitle"));
        assertEquals("2011", item3.get("year"));
    }

    /**
     * Test of detectArxiv method, of class PostProcessor.
     */
    @Test
    public void testDetectArxiv() {
        System.out.println("detectArxiv");

        // No arxiv
        BibItem item1 = new BibItem("article", "test");
        item1.put("author", "Thor, Au");
        item1.put("title", "Title");
        item1.put("journal", "Journal of Examples");
        item1.put("year", "2013");
        item1.put("--testArxiv", "");
        item1.put("--testClass", "");

        // Just arxiv
        BibItem item2 = new BibItem("article", "test");
        item2.put("author", "Thor, Au");
        item2.put("title", "Title");
        item2.put("journal", "Journal of Examples");
        item2.put("year", "2013");
        item2.put("arxiv", "1234.1234");
        item2.put("--testArxiv", "1234.1234");
        item2.put("--testClass", "");

        // arxiv and primary class
        BibItem item3 = new BibItem("article", "test");
        item3.put("author", "Thor, Au");
        item3.put("title", "Title");
        item3.put("journal", "Journal of Examples");
        item3.put("year", "2013");
        item3.put("arxiv", "1234.1234");
        item3.put("primaryclass", "cs.cg");
        item3.put("--testArxiv", "1234.1234");
        item3.put("--testClass", "cs.cg");

        // Other arxiv versions
        BibItem item4 = new BibItem("article", "test");
        item4.put("author", "Thor, Au");
        item4.put("title", "Title");
        item4.put("journal", "Journal of Examples");
        item4.put("year", "2013");
        item4.put("arxiv", "1234.1234v2");
        item4.put("--testArxiv", "1234.1234v2");
        item4.put("--testClass", "");

        BibItem item5 = new BibItem("article", "test");
        item5.put("author", "Thor, Au");
        item5.put("title", "Title");
        item5.put("journal", "Journal of Examples");
        item5.put("year", "2013");
        item5.put("arxiv", "cs.cg/1234.1234");
        item5.put("--testArxiv", "1234.1234");
        item5.put("--testClass", "cs.cg");

        BibItem item6 = new BibItem("article", "test");
        item6.put("author", "Thor, Au");
        item6.put("title", "Title");
        item6.put("journal", "Journal of Examples");
        item6.put("year", "2013");
        item6.put("arxiv", "1234.1234 [cs.cg]");
        item6.put("--testArxiv", "1234.1234");
        item6.put("--testClass", "cs.cg");

        BibItem item7 = new BibItem("article", "test");
        item7.put("author", "Thor, Au");
        item7.put("title", "Title");
        item7.put("journal", "Journal of Examples");
        item7.put("year", "2013");
        item7.put("arxiv", "1234.1234v5 [math]");
        item7.put("--testArxiv", "1234.1234v5");
        item7.put("--testClass", "math");

        // Other fields
        BibItem item8 = new BibItem("article", "test");
        item8.put("author", "Thor, Au");
        item8.put("title", "Title");
        item8.put("journal", "Journal of Examples");
        item8.put("year", "2013");
        item8.put("eprint", "math/0307200v3");
        item8.put("--testArxiv", "0307200v3");
        item8.put("--testClass", "math");

        BibItem item9 = new BibItem("article", "test");
        item9.put("author", "Thor, Au");
        item9.put("title", "Title");
        item9.put("journal", "Journal of Examples");
        item9.put("year", "2013");
        item9.put("archiveprefix", "arXiv");
        item9.put("eprint", "0707.3168");
        item9.put("primaryclass", "hep-th");
        item9.put("--testArxiv", "0707.3168");
        item9.put("--testClass", "hep-th");

        BibItem item10 = new BibItem("article", "test");
        item10.put("author", "Thor, Au");
        item10.put("title", "Title");
        item10.put("journal", "Journal of Examples");
        item10.put("year", "2013");
        item10.put("archiveprefix", "Snarxiv"); // NOT arxiv
        item10.put("eprint", "0707.3168");
        item10.put("primaryclass", "hep-th");
        item10.put("--testArxiv", "");
        item10.put("--testClass", "hep-th");

        BibItem item11 = new BibItem("article", "test");
        item11.put("author", "Thor, Au");
        item11.put("title", "Title");
        item11.put("journal", "Journal of Examples");
        item11.put("year", "2013");
        item11.put("ee", "http://arxiv.org/abs/1110.6473");
        item11.put("--testArxiv", "1110.6473");
        item11.put("--testClass", "");

        BibItem item12 = new BibItem("article", "test");
        item12.put("author", "Thor, Au");
        item12.put("title", "Title");
        item12.put("journal", "Journal of Examples");
        item12.put("year", "2013");
        item12.put("url", "http://arxiv.org/abs/1110.6473");
        item12.put("--testArxiv", "1110.6473");
        item12.put("--testClass", "");

        BibItem item13 = new BibItem("article", "test");
        item13.put("author", "Thor, Au");
        item13.put("title", "Title");
        item13.put("journal", "Journal of Examples");
        item13.put("year", "2013");
        item13.put("eprint", "http://arxiv.org/abs/1110.6473");
        item13.put("--testArxiv", "1110.6473");
        item13.put("--testClass", "");

        BibItem item14 = new BibItem("article", "test");
        item14.put("author", "Thor, Au");
        item14.put("title", "Title");
        item14.put("journal", "Journal of Examples");
        item14.put("year", "2013");
        item14.put("eprint", "http://test.org/abs/1110.6473"); // Not arxiv
        item14.put("--testArxiv", "");
        item14.put("--testClass", "");

        BibItem[] items = new BibItem[]{item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item14};
        Settings settings = new Settings();
        settings.getConsoleSettings().setShowWarnings(false);
        Console.setSettings(settings.getConsoleSettings());

        for (BibItem item : items) {
            PublicationPostProcessor.postProcess(settings, Arrays.asList(item));

            assertEquals("arXiv mismatch: " + item, item.get("--testArxiv"), (item.get("arxiv") == null ? "" : item.get("arxiv")));
            assertEquals("class mismatch: " + item, item.get("--testClass"), (item.get("primaryclass") == null ? "" : item.get("primaryclass")));
        }
    }

    /**
     * Test of processAliases method, of class PostProcessor.
     */
    @Test
    public void testProcessAliases() throws InterruptedException {
        System.out.println("processAliases");

        // Just journal
        BibItem item1 = new BibItem("article", "test");
        item1.put("author", "Thor, Au");
        item1.put("title", "Title");
        item1.put("journal", "Journal of Examples");
        item1.put("year", "2013");
        item1.put("--expectedJournal", "Journal of Examples");

        // Both
        BibItem item2 = new BibItem("article", "test");
        item2.put("author", "Thor, Au");
        item2.put("title", "Title");
        item2.put("journal", "Journal of Examples");
        item2.put("journaltitle", "Journal of Misfires");
        item2.put("year", "2013");
        item2.put("--expectedJournal", "Journal of Examples");

        // Empty journal
        BibItem item3 = new BibItem("article", "test");
        item3.put("author", "Thor, Au");
        item3.put("title", "Title");
        item3.put("journal", "");
        item3.put("journaltitle", "Journal of Misfires");
        item3.put("year", "2013");
        item3.put("--expectedJournal", "Journal of Misfires");

        // No journal
        BibItem item4 = new BibItem("article", "test");
        item4.put("author", "Thor, Au");
        item4.put("title", "Title");
        item4.put("journaltitle", "Journal of Misfires");
        item4.put("year", "2013");
        item4.put("--expectedJournal", "Journal of Misfires");

        BibItem items[] = new BibItem[]{item1, item2, item3, item4};
        Settings settings = new Settings();
        settings.getConsoleSettings().setShowWarnings(false);
        Console.setSettings(settings.getConsoleSettings());

        for (BibItem item : items) {
            PublicationPostProcessor.postProcess(settings, Arrays.asList(item));

            if (item.get("--expectedJournal") != null) {
                assertEquals("journal mismatch: " + item, item.get("--expectedJournal"), (item.get("journal") == null ? "" : item.get("journal")));
            }
        }
    }

}

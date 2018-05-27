/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import publy.data.Section;
import publy.data.bibitem.BibItem;
import publy.data.settings.GeneralSettings;
import publy.data.settings.Settings;
import publy.Constants;
import publy.io.PublicationListWriter;

/**
 *
 *
 */
public class PlainPublicationListWriter extends PublicationListWriter {

    private PlainBibItemWriter itemWriter;
    private int count;

    public PlainPublicationListWriter(Settings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(List<Section> sections, BufferedWriter out) throws IOException {
        itemWriter = new PlainBibItemWriter(out, settings);
        count = getInitialCount(sections);

        // Write the body
        for (Section s : sections) {
            writeSection(s, out, 0);
        }
        
        // Credit line and last modified
        out.write("Generated by Publy " + Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION + ".  Last modified on "  + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");
        out.newLine();
    }

    private void writeSection(Section section, BufferedWriter out, int nestingLevel) throws IOException {
        // Reset the count if necessary
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.WITHIN_CATEGORIES) {
            if (settings.getGeneralSettings().isReverseNumbering()) {
                count = section.getItems().size(); // This is correct; sub-sections hsould have their own count
            } else {
                count = 1;
            }
        }

        // Write the title
        indent(out, 2 * nestingLevel);
        out.write(section.getName() + ".");
        out.newLine();
        out.newLine();

        // Write the publications
        itemWriter.setIgnoredFields(new HashSet<>(section.getIgnoredFields()));
        itemWriter.setIndentationLevel(2 * nestingLevel + 3);

        for (BibItem item : section.getItems()) {
            indent(out, 2 * nestingLevel);

            // Write the appropriate number
            if (settings.getGeneralSettings().getNumbering() != GeneralSettings.Numbering.NO_NUMBERS) {
                out.write(count + ". ");
                itemWriter.setIndentationLevel(2 * nestingLevel + (count + ". ").length());

                if (settings.getGeneralSettings().isReverseNumbering()) {
                    count--;
                } else {
                    count++;
                }
            } else {
                // Indent the first line
                out.write("   ");
            }

            itemWriter.write(item);
            out.newLine();
        }

        // Write the sub-sections
        for (Section subsection : section.getSubsections()) {
            writeSection(subsection, out, nestingLevel + 1);
        }

        out.newLine();
    }

    private void indent(BufferedWriter out, int nestingLevel) throws IOException {
        for (int i = 0; i < nestingLevel; i++) {
            out.write(" ");
        }
    }
}

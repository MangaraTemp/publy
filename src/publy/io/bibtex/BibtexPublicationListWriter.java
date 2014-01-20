/*
 * Copyright 2013-2014 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.bibtex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;
import publy.data.settings.GeneralSettings;
import publy.data.settings.Settings;
import publy.gui.UIConstants;
import publy.io.PublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class BibtexPublicationListWriter extends PublicationListWriter {

    private int count;
    private BibtexBibItemWriter itemWriter;

    public BibtexPublicationListWriter(Settings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(List<OutputCategory> categories, BufferedWriter out) throws IOException {
        itemWriter = new BibtexBibItemWriter(out, settings);

        // Initialize the count
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.GLOBAL) {
            if (settings.getGeneralSettings().isReverseNumbering()) {
                count = 0;

                for (OutputCategory c : categories) {
                    count += c.getItems().size();
                }
            } else {
                count = 1;
            }
        }

        // Write the body
        for (OutputCategory c : categories) {
            writeCategory(c, out);
        }

        // Credit line and last modified
        out.write("-- Generated by Publy " + UIConstants.MAJOR_VERSION + "." + UIConstants.MINOR_VERSION + ".  Last modified on " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");
        out.newLine();
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        // Reset the count if necessary
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.LOCAL) {
            if (settings.getGeneralSettings().isReverseNumbering()) {
                count = c.getItems().size();
            } else {
                count = 1;
            }
        }

        out.write("-- " + c.getName() + ".");
        out.newLine();
        out.newLine();

        for (BibItem item : c.getItems()) {
            // Write the appropriate number
            if (settings.getGeneralSettings().getNumbering() != GeneralSettings.Numbering.NONE) {
                out.write("-- " + count + ".");
                out.newLine();

                if (settings.getGeneralSettings().isReverseNumbering()) {
                    count--;
                } else {
                    count++;
                }
            }

            itemWriter.write(item);

            out.newLine();
        }

        out.newLine();

        // Reset the count if necessary
        if (settings.getGeneralSettings().getNumbering() == GeneralSettings.Numbering.LOCAL) {
            count = 0;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publy.io.plain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import publy.data.bibitem.BibItem;
import publy.data.category.OutputCategory;
import publy.data.settings.FormatSettings;
import publy.gui.UIConstants;
import publy.io.PublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class PlainPublicationListWriter extends PublicationListWriter {

    private PlainBibItemWriter itemWriter;
    private int count;

    public PlainPublicationListWriter(FormatSettings settings) {
        super(settings);
    }

    @Override
    protected void writePublicationList(BufferedWriter out) throws IOException {
        itemWriter = new PlainBibItemWriter(out, getSettings());

        // Initialize the count
        if (getSettings().getNumbering() == FormatSettings.Numbering.GLOBAL) {
            if (getSettings().isReverseNumbering()) {
                count = 0;

                for (OutputCategory c : getCategories()) {
                    count += c.getItems().size();
                }
            } else {
                count = 1;
            }
        }

        // Write the body
        for (OutputCategory c : getCategories()) {
            writeCategory(c, out);
        }
        
        // Credit line and last modified
        out.write("Generated by Publy " + UIConstants.MAJOR_VERSION + "." + UIConstants.MINOR_VERSION + ".  Last modified on "  + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");
        out.newLine();
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        // Reset the count if necessary
        if (getSettings().getNumbering() == FormatSettings.Numbering.LOCAL) {
            if (getSettings().isReverseNumbering()) {
                count = c.getItems().size();
            } else {
                count = 1;
            }
        }

        out.write(c.getName() + ".");
        out.newLine();
        out.newLine();

        for (BibItem item : c.getItems()) {
            // Write the appropriate number
            if (getSettings().getNumbering() != FormatSettings.Numbering.NONE) {
                out.write(count + ".");
                out.newLine();

                if (getSettings().isReverseNumbering()) {
                    count--;
                } else {
                    count++;
                }
            }

            itemWriter.write(item);
            out.newLine();
        }

        out.newLine();
    }
}

/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import publy.Console;
import publy.data.PublicationType;
import publy.data.Author;
import publy.data.bibitem.BibItem;
import publy.data.bibitem.Type;
import publy.data.category.OutputCategory;
import publy.data.settings.HTMLSettings;
import publy.data.settings.Settings;
import publy.io.BibItemWriter;
import publy.io.bibtex.BibtexBibItemWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLBibItemWriter extends BibItemWriter {

    private static final String indent = "          ";
    private BibtexBibItemWriter bibtexWriter;
    private List<OutputCategory> categories;

    public HTMLBibItemWriter(BufferedWriter out, List<OutputCategory> categories, Settings settings) {
        super(out, settings);
        bibtexWriter = new BibtexBibItemWriter(out, settings);
        this.categories = categories;
    }

    @Override
    public void write(BibItem item) throws IOException {
        writeTitleAndAuthorsHTML(item);

        if (item.anyNonEmpty("status")) {
            writeStatus(item);
        } else {
            out.write(indent);

            switch (item.getType()) {
                case ARTICLE:
                    writeArticle(item);
                    break;
                case BOOK:
                    writeBook(item);
                    break;
                case INBOOK:
                    writeInBook(item);
                    break;
                case BOOKLET:
                    writeBooklet(item);
                    break;
                case INCOLLECTION:
                    writeInCollection(item);
                    break;
                case MANUAL:
                    writeManual(item);
                    break;
                case MISC:
                    writeMisc(item);
                    break;
                case ONLINE:
                    writeOnline(item);
                    break;
                case PATENT:
                    writePatent(item);
                    break;
                case PROCEEDINGS:
                    writeProceedings(item);
                    break;
                case INPROCEEDINGS:
                    writeInProceedings(item);
                    break;
                case REPORT:
                    writeReport(item);
                    break;
                case THESIS:
                    writeThesis(item);
                    break;
                case UNPUBLISHED:
                    writeUnpublished(item);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unexpected publication type: " + item.getType());
            }

            if (!((item.getType() == Type.PROCEEDINGS || item.getType() == Type.INPROCEEDINGS) && item.anyNonEmpty("address"))) {
                output("<span class=\"date\">", formatDate(item), "</span>.<br>", true);
            }
        }

        // Write note (unpublished uses note as the publication info)
        if (item.getType() != Type.UNPUBLISHED) {
            output(indent + "<span class=\"note\">", item.get("note"), ".</span><br>", true);
        }

        writeLinks(item);
    }

    protected void writeArticle(BibItem item) throws IOException {
        output("<span class=\"journal\">", item.get("journal"), "</span>, ");

        if (item.anyNonEmpty("volume", "number")) {
            output("<span class=\"volume\">", item.get("volume"), "</span>");
            output("<span class=\"number\">(", item.get("number"), ")</span>");
            output(":<span class=\"pages\">", formatPages(item, false), "</span>");
            out.write(", ");
        } else {
            output("<span class=\"pages\">", formatPages(item, true), "</span>, ");
        }
    }

    protected void writeBook(BibItem item) throws IOException {
        writeVolume(item, true, ". ");
        writePublisherAndEdition(item);
    }

    protected void writeInBook(BibItem item) throws IOException {
        if (item.anyNonEmpty("volume") && item.anyNonEmpty("chapter", "pages")) {
            writeVolume(item, true, "");

            if (item.anyNonEmpty("chapter")) {
                out.write(", ");
                writeChapter(item, false);
            }

            output(", <span class=\"pages\">", formatPages(item, true), "</span>");
            out.write(". ");
        } else {
            writeVolume(item, true, ". ");
        }

        writePublisherAndEdition(item);
    }

    protected void writeBooklet(BibItem item) throws IOException {
        output("<span class=\"howpublished\">", item.get("howpublished"), "</span>, ");
        output("<span class=\"address\">", item.get("address"), "</span>, ");
    }

    protected void writeInCollection(BibItem item) throws IOException {
        out.write("In ");

        if (item.anyNonEmpty("editor")) {
            output("<span class=\"editor\">", formatAuthors(item, true, Author.NameOutputType.LINKED_HTML), ", </span>");
        }

        output("<span class=\"booktitle\">", item.get("booktitle"), "</span>");

        if (item.anyNonEmpty("volume", "series", "number")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        if (item.anyNonEmpty("chapter")) {
            out.write(", ");
            writeChapter(item, false);
        }

        output(", <span class=\"pages\">", formatPages(item, true), "</span>");
        out.write(". ");

        if (item.anyNonEmpty("publisher", "address", "edition")) {
            writePublisherAndEdition(item);
        }
    }

    protected void writeManual(BibItem item) throws IOException {
        output("<span class=\"organization\">", item.get("organization"), "</span>, ");
        output("<span class=\"address\">", item.get("address"), "</span>, ");

        String edition = item.get("edition");

        if (edition != null && !edition.isEmpty()) {
            if (item.anyNonEmpty("organization", "address")) {
                output("<span class=\"edition\">", toLowerCase(edition), " edition</span>, ");
            } else {
                output("<span class=\"edition\">", toTitleCase(edition), " edition</span>, ");
            }
        }
    }

    protected void writeMisc(BibItem item) throws IOException {
        output(item.get("howpublished"), ", ");
        output(item.get("address"), ", ");
    }

    protected void writeOnline(BibItem item) throws IOException {
        // No publication information necessary
    }

    protected void writePatent(BibItem item) throws IOException {
        // TODO
    }

    protected void writeProceedings(BibItem item) throws IOException {
        String notesConnective;

        if (item.anyNonEmpty("address")) {
            notesConnective = ", ";
        } else {
            // There should be a period if anything follows notes
            if (item.anyNonEmpty("publisher") || (item.anyNonEmpty("organization") && item.anyNonEmpty("editor"))) {
                notesConnective = ". ";
            } else {
                notesConnective = ", ";
            }
        }

        writeVolume(item, true, notesConnective);

        if (item.anyNonEmpty("address")) {
            output("<span class=\"address\">", item.get("address"), "</span>, ");
            output("<span class=\"date\">", formatDate(item), "</span>.<br>", true);

            if (item.anyNonEmpty("publisher")) {
                if (item.anyNonEmpty("editor")) {
                    output("<span class=\"organization\">", item.get("organization"), "</span>, ");
                }
                output("<span class=\"publisher\">", item.get("publisher"), "</span>. ");
            } else if (item.anyNonEmpty("editor")) {
                output("<span class=\"organization\">", item.get("organization"), "</span>. ");
            }
        } else {
            if (item.anyNonEmpty("editor")) {
                output("<span class=\"organization\">", item.get("organization"), "</span>, ");
            }
            output("<span class=\"publisher\">", item.get("publisher"), "</span>, ");
        }
    }

    protected void writeInProceedings(BibItem item) throws IOException {
        out.write("In ");

        if (item.anyNonEmpty("editor")) {
            output("<span class=\"editor\">", formatAuthors(item, true, Author.NameOutputType.LINKED_HTML), ", </span>");
        }

        output("<span class=\"booktitle\">", item.get("booktitle"), "</span>");

        if (item.anyNonEmpty("volume", "number", "series")) {
            out.write(", ");
            writeVolume(item, false, "");
        }

        output(", <span class=\"pages\">", formatPages(item, true), "</span>");

        if (item.anyNonEmpty("address")) {
            output(", <span class=\"address\">", item.get("address"), "</span>, ");
            output("<span class=\"date\">", formatDate(item), "</span>.<br>", true);

            if (item.anyNonEmpty("publisher")) {
                output("<span class=\"organization\">", item.get("organization"), "</span>, ");
                output("<span class=\"publisher\">", item.get("publisher"), "</span>. ");
            } else {
                output("<span class=\"organization\">", item.get("organization"), "</span>. ");
            }
        } else if (item.anyNonEmpty("publisher", "organization")) {
            out.write(". ");
            output("<span class=\"organization\">", item.get("organization"), "</span>, ");
            output("<span class=\"publisher\">", item.get("publisher"), "</span>, ");
        } else {
            out.write(", ");
        }
    }

    protected void writeReport(BibItem item) throws IOException {
        output("<span class=\"type\">", toTitleCase(item.get("type")), "</span>");
        output(" <span class=\"number\">", item.get("number"), "</span>");
        out.write(", ");

        output("<span class=\"institution\">", item.get("institution"), "</span>, ");
        output("<span class=\"address\">", item.get("address"), "</span>, ");
    }

    protected void writeThesis(BibItem item) throws IOException {
        output("<span class=\"type\">", item.get("type"), "</span>, ");
        output("<span class=\"school\">", item.get("school"), "</span>, ");
        output("<span class=\"address\">", item.get("address"), "</span>, ");
    }

    protected void writeUnpublished(BibItem item) throws IOException {
        output("<span class=\"note\">", item.get("note"), "</span>, ");
    }

    protected void writeTitleAndAuthorsHTML(BibItem item) throws IOException {
        if (settings.getGeneralSettings().titleFirst()) {
            writeTitleAndAbstractHTML(item);
        }

        writeAuthors(item);

        if (!settings.getGeneralSettings().titleFirst()) {
            writeTitleAndAbstractHTML(item);
        }
    }

    protected void writeTitleAndAbstractHTML(BibItem item) throws IOException {
        out.write(indent);

        String title = formatTitle(item);

        if (item.getType() == Type.INBOOK && !item.anyNonEmpty("volume")) {
            if (item.anyNonEmpty("chapter")) {
                title += ", <span class=\"chapter\">";

                if (item.anyNonEmpty("type")) {
                    title += toLowerCase(item.get("type"));
                } else {
                    title += "chapter";
                }

                title += " " + item.get("chapter") + "</span>";
            }

            if (item.anyNonEmpty("pages")) {
                title += ", <span class=\"pages\">" + formatPages(item, true) + "</span>";
            }
        }

        // Title
        if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.ABSTRACT && includeAbstract(item)) {
            output("<h3 class=\"title abstract-toggle\">", title, "</h3>");
        } else if (settings.getHtmlSettings().getTitleTarget() == HTMLSettings.TitleLinkTarget.PAPER && includePaper(item)) {
            try {
                String href = (new URI(null, null, item.get("paper"), null)).toString();

                out.write("<a href=\"" + href + "\">");
                output("<h3 class=\"title\">", title, "</h3>");
                out.write("</a>");
                checkExistance(item.get("paper"), "paper", item);
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
                output("<h3 class=\"title\">", title, "</h3>");
            }
        } else {
            output("<h3 class=\"title\">", title, "</h3>");
        }

        // Add text if I presented this paper
        if ("yes".equals(item.get("presented"))) {
            output(" ", settings.getHtmlSettings().getPresentedText(), "");
        }

        // Abstract if included
        if (includeAbstract(item)) {
            // Show \ hide link for the abstract
            if (settings.getHtmlSettings().getTitleTarget() != HTMLSettings.TitleLinkTarget.ABSTRACT) {
                out.newLine();
                writeToggleLink("abstract", "Abstract");
            }

            out.write("<br>");
            out.newLine();

            // Actual abstract
            out.write(indent + "<div class=\"abstract-container\">");
            out.write("<div class=\"abstract\">");
            out.newLine();
            out.write(indent + "  <span class=\"abstractword\">Abstract: </span>");
            output(item.get("abstract"));
            out.newLine();
            out.write(indent + "</div></div>");
            out.newLine();
        } else {
            out.write("<br>");
            out.newLine();
        }
    }

    protected void writeAuthors(BibItem item) throws IOException {
        boolean useEditor;

        if (item.getType() == Type.PROCEEDINGS) {
            // Proceedings never prints the author
            if (item.anyNonEmpty("editor")) {
                useEditor = true;
            } else if (item.anyNonEmpty("organization")) {
                output(indent + "<span class=\"organization\">", item.get("organization"), "</span>.<br>", true);
                return;
            } else {
                Console.error("No editor or organization found for entry \"%s\".", item.getId());
                return;
            }
        } else {
            if (item.anyNonEmpty("author")) {
                useEditor = false;
            } else if (item.anyNonEmpty("editor")) {
                useEditor = true;
            } else {
                Console.error("No author information found for entry \"%s\".", item.getId());
                return;
            }
        }

        List<Author> authorList = (useEditor ? item.getEditors() : item.getAuthors());

        // Don't add an authors line if it's just me and I just want to list co-authors
        if (settings.getGeneralSettings().listAllAuthors() || authorList.size() > 1 || (authorList.size() == 1 && !authorList.get(0).isMe(settings.getGeneralSettings().getMyNames(), settings.getGeneralSettings().getNameDisplay(), settings.getGeneralSettings().reverseNames()))) {
            String authors = formatAuthors(item, useEditor, Author.NameOutputType.LINKED_HTML);

            if (authors.endsWith(".</span>") || authors.endsWith(".</a>")) {
                // Don't double up on periods (occurs when author names are abbreviated and reversed)
                output(indent, authors, "<br>", true);
            } else {
                output(indent, authors, ".<br>", true);
            }
        }
    }

    private void writeVolume(BibItem item, boolean capitalize, String connective) throws IOException {
        String volume = item.get("volume");
        String series = item.get("series");
        String number = item.get("number");

        if (volume != null && !volume.isEmpty()) {
            output("<span class=\"volume\">" + (capitalize ? "Volume " : "volume "), volume, "</span>");
            output(" of <span class=\"series\">", series, "</span>");
            out.write(connective);
        } else if (number != null && !number.isEmpty()) {
            output("<span class=\"number\">" + (capitalize ? "Number " : "number "), number, "</number>");
            output(" in <span class=\"series\">", series, "</span>");
            out.write(connective);
        } else {
            output("<span class=\"series\">", series, "</span>" + connective);
        }
    }

    protected void writePublisherAndEdition(BibItem item) throws IOException {
        output("<span class=\"publisher\">", item.get("publisher"), "</span>, ");
        output("<span class=\"address\">", item.get("address"), "</span>, ");

        String edition = item.get("edition");

        if (edition != null && !edition.isEmpty()) {
            if (item.anyNonEmpty("publisher", "address")) {
                output("<span class=\"edition\">", toLowerCase(edition), " edition</span>, ");
            } else {
                output("<span class=\"edition\">", toTitleCase(edition), " edition</span>, ");
            }
        }
    }

    protected void writeChapter(BibItem item, boolean capitalize) throws IOException {
        String type = item.get("type");
        String chapter = item.get("chapter");

        if (chapter != null && !chapter.isEmpty()) {
            out.write("<span class=\"chapter\">");

            if (type != null && !type.isEmpty()) {
                output(capitalize ? toTitleCase(type) : toLowerCase(type));
            } else {
                out.write(capitalize ? "Chapter" : "chapter");
            }

            output(" ", chapter, "");

            out.write("</span>");
        }
    }

    protected void writeStatus(BibItem item) throws IOException {
        String title;

        switch (item.getType()) {
            case ARTICLE:
                title = item.get("journal");
                break;
            case INPROCEEDINGS:
                title = item.get("booktitle");

                if (title.startsWith("Proceedings of ")) {
                    title = title.substring("Proceedings of ".length());
                }

                break;
            default:
                title = null;
                break;
        }

        out.write(indent);

        if (title == null) {
            switch (item.get("status")) {
                case "submitted":
                    output("Submitted for review.<br>", true);
                    break;
                case "accepted":
                    output("Accepted for publication.<br>", true);
                    break;
                case "acceptedrev":
                    output("Accepted for publication, pending minor revisions.<br>", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + item.get("status") + "\"");
            }
        } else {
            switch (item.get("status")) {
                case "submitted":
                    output("Submitted to <span class=\"booktitle\">", title, "</span>.<br>", true);
                    break;
                case "accepted":
                    output("Accepted to <span class=\"booktitle\">", title, "</span>.<br>", true);
                    break;
                case "acceptedrev":
                    output("Accepted, pending minor revisions, to <span class=\"booktitle\">", title, "</span>.<br>", true);
                    break;
                default:
                    throw new AssertionError("Item \"" + item.getId() + "\" has an unrecognized status: \"" + item.get("status") + "\"");
            }
        }
    }

    protected String formatPages(BibItem item, boolean verbose) {
        String pages = (verbose ? super.formatPages(item) : item.get("pages"));

        if (pages == null) {
            pages = "";
        }

        return pages.replaceAll("-+", "&ndash;");
    }

    private void writeLinks(BibItem item) throws IOException {
        if (!includeBibtex(item)) {
            writeLinks(item, false, false);
        } else {
            switch (item.getType()) {
                // Only include bibtex if there is an eprint reference
                case MISC:
                case ONLINE:
                case UNPUBLISHED:
                case PATENT:
                    if (item.anyNonEmpty("arxiv")) {
                        writeLinks(item, false, true);
                    } else {
                        writeLinks(item, false, false);
                    }
                    break;
                default:
                    if (PublicationType.ACCEPTED.matches(item)) {
                        writeLinks(item, true, false);
                    } else if (item.anyNonEmpty("arxiv")) {
                        writeLinks(item, false, true);
                    } else {
                        // Just in case.
                        writeLinks(item, false, false);
                    }
                    break;
            }
        }
    }

    private void writeLinks(BibItem item, boolean includeBibtex, boolean includeArxivBibtex) throws IOException {
        // Make sure the div is not written if there are no links
        boolean divOpened = false;

        // Paper link
        if (includePaper(item) && settings.getHtmlSettings().getTitleTarget() != HTMLSettings.TitleLinkTarget.PAPER) {
            try {
                String link = (new URI(null, null, item.get("paper"), null)).toString();
                String text;

                // Use the extension as link text, or "Paper" if no extension is found
                int dot = link.lastIndexOf('.');

                if (dot != -1) {
                    text = link.substring(dot + 1);

                    // Convert *.ps.gz to ps
                    if ("gz".equals(text) && link.lastIndexOf('.', dot - 1) != -1) {
                        text = link.substring(link.lastIndexOf('.', dot - 1) + 1, dot);
                    }
                } else {
                    text = "Paper";
                }

                writeLink(divOpened, link, text);
                divOpened = true;

                checkExistance(item.get("paper"), "paper", item);
            } catch (URISyntaxException ex) {
                Console.except(ex, "Paper link for entry \"%s\" is not formatted properly:", item.getId());
            }
        }

        // arXiv link
        if (item.anyNonEmpty("arxiv")) {
            writeLink(divOpened, "http://arxiv.org/abs/" + item.get("arxiv"), "arXiv");
            divOpened = true;
        }

        // DOI link
        if (item.anyNonEmpty("doi")) {
            String link = item.get("doi");

            // Add the general DOI part if necessary 
            if (!link.startsWith("http://dx.doi.org/")) {
                link = "http://dx.doi.org/" + link;
            }

            writeLink(divOpened, link, "DOI");
            divOpened = true;
        }

        // ISSN link
        if (item.anyNonEmpty("issn")) {
            String link = "http://www.worldcat.org/issn/" + item.get("issn");
            writeLink(divOpened, link, "ISSN");
            divOpened = true;
        }
        
        // ISBN link
        if (item.anyNonEmpty("isbn")) {
            String link = "http://www.worldcat.org/isbn/" + item.get("isbn");
            writeLink(divOpened, link, "ISBN");
            divOpened = true;
        }

        // URL link
        if (item.anyNonEmpty("url")) {
            String link = item.get("url");
            writeLink(divOpened, link, "URL");
            divOpened = true;
        }

        // Other user-specified links
        divOpened = writeCustomLink(divOpened, item, -1); // link

        for (int i = 0; i < 20; i++) {
            divOpened = writeCustomLink(divOpened, item, i); // link<i>
        }

        // Close links div
        if (divOpened) {
            out.write(indent + "</div>");
            out.newLine();
        }

        // BibTeX link
        if (includeBibtex || includeArxivBibtex) {
            // Show / hide link
            writeToggleLink("bibtex", "BibTeX");
            out.newLine();

            // Actual bibtex
            if (includeBibtex) {
                writeBibtexHTML(item);
            } else {
                writeArxivBibtexHTML(item);
            }
        }
    }

    private void writeLink(boolean divOpened, String link, String text) throws IOException {
        if (!divOpened) {
            out.write(indent + "<div class=\"links\">");
            out.newLine();
        }

        out.write(indent + "  <a href=\"" + link + "\">" + text + "</a>");
        out.newLine();
    }

    private boolean writeCustomLink(boolean divOpened, BibItem item, int i) throws IOException {
        String attribute = (i == -1 ? "link" : "link" + i);

        if (item.anyNonEmpty(attribute)) {
            String link = item.get(attribute);
            int divider = link.indexOf('|');

            if (divider == -1) {
                Console.error("No divider \"|\" found in %s of item \"%s\". Links should be formatted as%n  %s={<Link text>|<Link target>}", attribute, item.getId(), attribute);
            } else {
                String text = link.substring(0, divider);
                String target = link.substring(divider + 1);

                if (target.startsWith("#")) {
                    // Link to another paper, good as-is
                    checkIdExistance(target.substring(1), attribute, item);
                } else if (target.contains(":")) {
                    // Most file systems prohibit colons in file names, so
                    // it seems safe to assume that this indicates an
                    // absolute URI and as such, should be fine.
                } else {
                    // Most likely link to a file on disk. Encode correctly.
                    try {
                        checkExistance(target, attribute, item);
                        target = (new URI(null, null, target, null)).toString();
                    } catch (URISyntaxException ex) {
                        Console.except(ex, "Could not parse the target of %s of item \"%s\":", attribute, item.getId());
                    }
                }

                writeLink(divOpened, target, text);
                return true;
            }
        }

        return divOpened;
    }

    private void writeBibtexHTML(BibItem item) throws IOException {
        out.write(indent + "<div class=\"bibtex-container\">");
        out.newLine();
        out.write(indent + "  <pre class=\"bibtex\">");
        out.newLine();

        bibtexWriter.write(item);

        out.write("</pre>");
        out.newLine();

        out.write(indent + "</div>");
        out.newLine();
    }

    private void writeArxivBibtexHTML(BibItem item) throws IOException {
        out.write(indent + "<div class=\"bibtex-container\">");
        out.newLine();
        out.write(indent + "  <pre class=\"bibtex\">");
        out.newLine();

        // Item type
        out.write("@article{" + item.getId() + ",");
        out.newLine();

        // The first field should omit the connecting ",".
        boolean first = true;

        // Get the proper format for authors
        if (item.anyNonEmpty("author")) {
            out.write("  author={");

            for (int i = 0; i < item.getAuthors().size(); i++) {
                out.write(item.getAuthors().get(i).getName(Author.NameOutputType.LATEX));

                if (i < item.getAuthors().size() - 1) {
                    out.write(" and ");
                }
            }

            out.write("}");

            first = false;
        }

        for (String field : new String[]{"title", "year"}) {
            if (first) {
                first = false;
            } else {
                out.write(",");
                out.newLine();
            }

            out.write("  " + field + "={" + item.get(field) + "}");
        }

        out.write(",");
        out.newLine();
        out.write("  journal={CoRR},");
        out.newLine();
        out.write("  volume={abs/" + item.get("arxiv") + "},");
        out.newLine();
        out.write("  ee={http://arxiv.org/abs/" + item.get("arxiv") + "}");

        out.newLine(); // No comma after the last element
        out.write("}</pre>");
        out.newLine();

        out.write(indent + "</div>");
        out.newLine();
    }

    private void writeToggleLink(String type, String text) throws IOException {
        out.write(indent);
        out.write("<button class=\"" + type + "-toggle\">");
        out.write(text);
        out.write("</button>");
    }

    private boolean includeAbstract(BibItem item) {
        return item.anyNonEmpty("abstract") && settings.getHtmlSettings().getIncludeAbstract().matches(item);
    }

    private boolean includeBibtex(BibItem item) {
        return settings.getHtmlSettings().getIncludeBibtex().matches(item);
    }

    private boolean includePaper(BibItem item) {
        return item.anyNonEmpty("paper") && settings.getHtmlSettings().getIncludePaper().matches(item);
    }

    private void checkExistance(String path, String attr, BibItem item) {
        Path file = settings.getFileSettings().getTarget().resolveSibling(path);

        if (Files.notExists(file)) {
            Console.warn(Console.WarningType.MISSING_REFERENCE, "File \"%s\" (linked in attribute \"%s\" of publication \"%s\") cannot be found at \"%s\".", path, attr, item.getId(), file);
        }
    }

    /**
     * Checks whether a BibItem with the given id exists, gives a warning when it doesn't.
     * @param id 
     */
    private void checkIdExistance(String id, String attr, BibItem item) {
        for (OutputCategory cat : categories) {
            for (BibItem i : cat.getItems()) {
                if (i.getId().equals(id)) {
                    return;
                }
            }
        }
        
        Console.warn(Console.WarningType.MISSING_REFERENCE, "Publication \"%s\" (linked in attribute \"%s\" of publication \"%s\") is not in the final list.", id, attr, item.getId());
    }
}

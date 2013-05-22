/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import publistgenerator.Console;
import publistgenerator.data.Pair;
import publistgenerator.data.bibitem.Article;
import publistgenerator.data.bibitem.Author;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.bibitem.InCollection;
import publistgenerator.data.bibitem.InProceedings;
import publistgenerator.data.bibitem.InvitedTalk;
import publistgenerator.data.bibitem.MastersThesis;
import publistgenerator.data.bibitem.PhDThesis;
import publistgenerator.data.bibitem.Unpublished;
import publistgenerator.data.bibitem.Venue;

/**
 *
 * @author Sander
 */
public class BibTeXParser {

    private static final Pattern whiteSpace = Pattern.compile("\\s+"); // Regex that matches one or more whitespace characters
    // Patterns for author and abbreviation parsing
    private static final Pattern shortPattern = Pattern.compile("short=\"([^\"]*)\"");
    private static final Pattern fullPattern = Pattern.compile("full=\"([^\"]*)\"");
    private static final Pattern abbPattern = Pattern.compile("abbr=\"([^\"]*)\"");
    private static final Pattern namePattern = Pattern.compile(" name=\"([^\"]*)\"");
    private static final Pattern htmlPattern = Pattern.compile("htmlname=\"([^\"]*)\"");
    private static final Pattern latexPattern = Pattern.compile("latexname=\"([^\"]*)\"");
    private static final Pattern urlPattern = Pattern.compile("url=\"([^\"]*)\"");
    // Pattern for detecting an author link
    private static final Pattern authorPattern = Pattern.compile("<([^<>]*)>");
    // Pattern for detecting an abbreviation
    private static final Pattern abbrPattern = Pattern.compile("<<([^>]*)>>");

    private BibTeXParser() {
    }

    public static List<BibItem> parseFile(File file) throws IOException {
        List<BibItem> items = new ArrayList<>();
        HashMap<String, String> abbreviations = new HashMap<>();
        HashMap<String, Venue> venues = new HashMap<>();
        HashMap<String, Author> authors = new HashMap<>();

        parseFile(file, items, abbreviations, venues, authors);

        for (BibItem item : items) {
            setVenue(item, venues);
            expandAbbreviations(item, abbreviations, venues);
            replaceAuthors(item, authors);
        }
        
        Console.log("Publications list \"%s\" parsed successfully.", file.getName());

        return items;
    }

    private static void parseFile(File file, List<BibItem> items, Map<String, String> abbreviations, Map<String, Venue> venues, Map<String, Author> authors) throws IOException {
        HashSet<String> ids = new HashSet<>(); // Bibitem identifiers, used to check for duplicates

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            for (String l = in.readLine(); l != null; l = in.readLine()) {
                String line = l.trim();

                if (line.startsWith("@")) {
                    // A Bibitem
                    BibItem item = parseBibItem(collectItem(in, line, '{', '}'));

                    if (item != null) {
                        if (ids.contains(item.getId())) {
                            Console.error("Duplicate publication identifier: %s", item.getId());
                        } else {
                            if (item.checkMandatoryFields()) {
                                ids.add(item.getId());
                                items.add(item);
                            }
                        }
                    }
                } else if (line.startsWith("<")) {
                    // A custom tag
                    parseTag(collectItem(in, line, '<', '>'), abbreviations, venues, authors);
                }
            }
        }
    }

    private static String collectItem(BufferedReader in, String firstLine, char open, char close) throws IOException {
        int level = levelChange(firstLine, open, close);
        StringBuilder item = new StringBuilder(firstLine);

        while (level > 0) {
            String line = in.readLine();

            if (line == null) {
                throw new IOException("Unclosed item starting at line \"" + firstLine + "\".");
            } else {
                item.append(" ").append(line);
                level += levelChange(line, open, close);
            }
        }

        // Replace all sequences of whitespace with a single space
        return whiteSpace.matcher(item).replaceAll(" ");
    }

    private static int levelChange(String line, char open, char close) {
        int change = 0;

        for (char c : line.toCharArray()) {
            if (c == open) {
                change++;
            } else if (c == close) {
                change--;
            }
        }

        return change;
    }

    private static BibItem parseBibItem(String bibItem) {
        BibItem item = initializeBibItem(bibItem);

        if (item != null) {
            // Keep only the part between the outermost pair of braces
            String body = bibItem.substring(bibItem.indexOf('{') + 1, bibItem.lastIndexOf('}')).trim();

            // Parse the id
            int idEnd = body.indexOf(',');

            if (idEnd == -1) {
                // No attributes
                item.setId(body);
                body = "";
            } else {
                item.setId(body.substring(0, idEnd).trim());
                body = body.substring(idEnd + 1).trim();
            }

            // Parse the attributes
            int valueStart = body.indexOf('=');

            while (valueStart > 0) {
                // Parse the attribute name
                String name = body.substring(0, valueStart).trim().toLowerCase();
                body = body.substring(valueStart + 1).trim();

                // Parse the attribute value
                Pair<Integer, Integer> valuePos = getNextValuePosition(body);

                if (valuePos == null) {
                    Console.error("Mismatched delimiters in attribute \"%s\" of publication \"%s\".", name, item.getId());
                    break;
                } else {
                    item.put(name, body.substring(valuePos.getFirst(), valuePos.getSecond()).trim());
                    body = body.substring(valuePos.getSecond() + valuePos.getFirst()).trim(); // Skip a final delimiter if there are any (valuePos.getFirst == 1)
                    
                    if (body.startsWith(",")) {
                        body = body.substring(1).trim();
                    }
                }

                valueStart = body.indexOf('=');
            }
        }

        return item;
    }

    private static BibItem initializeBibItem(String bibItem) {
        String type = bibItem.substring(1, bibItem.indexOf('{')).trim().toLowerCase();

        switch (type) {
            case "inproceedings":
            case "conference":
                return new InProceedings();
            case "article":
                return new Article();
            case "mastersthesis":
                return new MastersThesis();
            case "phdthesis":
                return new PhDThesis();
            case "incollection":
                return new InCollection();
            case "unpublished":
                return new Unpublished();
            case "talk":
                return new InvitedTalk();
            default:
                Console.error("Unrecognized publication type \"%s\".", type);
                return null;
        }
    }

    private static Pair<Integer, Integer> getNextValuePosition(String body) {
        char firstChar = body.charAt(0);
        int valueStart, valueEnd;

        if (firstChar == '"') {
            // Quote-delimited value
            valueStart = 1;
            valueEnd = body.indexOf('"', 1);
        } else if (firstChar == '{') {
            // Brace-delimited value
            valueStart = 1;
            valueEnd = -1;

            int level = 0;
            int index = 0;

            for (char c : body.toCharArray()) {
                if (c == '{') {
                    level++;
                } else if (c == '}') {
                    level--;

                    if (level == 0) {
                        // Back at the initial level
                        valueEnd = index;
                        break;
                    }
                }

                index++;
            }
        } else {
            // No delimiters. Capture everything up to the next ',' or the end of the item
            valueStart = 0;
            valueEnd = body.indexOf(',');
            
            if (valueEnd == -1) {
                valueEnd = body.length();
            }
        }

        if (valueEnd == -1) {
            return null;
        } else {
            return new Pair<>(valueStart, valueEnd);
        }
    }

    private static void parseTag(String tag, Map<String, String> abbreviations, Map<String, Venue> venues, Map<String, Author> authors) {
        String type = tag.substring(1, tag.indexOf(' ', 2)).trim().toLowerCase();

        switch (type) {
            case "author":
                parseAuthor(tag, authors);
                break;
            case "abbr":
                parseAbbreviation(tag, abbreviations);
                break;
            case "conf":
                parseVenue(tag, true, venues);
                break;
            case "journal":
                parseVenue(tag, false, venues);
                break;
            default:
                Console.error("Unrecognized tag type \"%s\" at line \"%s\".", type, tag);
                break;
        }
    }

    private static void parseAuthor(String line, Map<String, Author> authors) {
        String shortName = null, latexName = null, htmlName = null, url = null;

        Matcher matcher = shortPattern.matcher(line);

        if (matcher.find()) {
            shortName = matcher.group(1);
        }

        matcher = namePattern.matcher(line);

        if (matcher.find()) {
            latexName = matcher.group(1);
            htmlName = matcher.group(1);
        }

        matcher = htmlPattern.matcher(line);

        if (matcher.find()) {
            htmlName = matcher.group(1);
        }

        matcher = latexPattern.matcher(line);

        if (matcher.find()) {
            latexName = matcher.group(1);
        }

        matcher = urlPattern.matcher(line);

        if (matcher.find()) {
            url = matcher.group(1);
        }

        if (shortName != null && htmlName != null && latexName != null) {
            Author author = new Author(shortName, latexName, htmlName);
            author.setUrl(url);

            authors.put(shortName, author);
        } else {
            Console.error("Author tag detected, but no full author information found:%n%s", line);
        }
    }

    private static void parseAbbreviation(String line, Map<String, String> abbreviations) {
        String abbr = null, full = null;
        Matcher matcher = shortPattern.matcher(line);

        if (matcher.find()) {
            abbr = matcher.group(1);
        }

        matcher = fullPattern.matcher(line);

        if (matcher.find()) {
            full = matcher.group(1);
        }

        if (abbr != null && full != null) {
            abbreviations.put(abbr, full);
        } else {
            Console.error("Abbreviation tag detected, but no full information found:%n%s", line);
        }
    }

    private static void parseVenue(String line, boolean conference, Map<String, Venue> venues) {
        String shortName = null, fullName = null, abbreviation = null;
        Matcher matcher = shortPattern.matcher(line);

        if (matcher.find()) {
            shortName = matcher.group(1);
        }

        matcher = fullPattern.matcher(line);

        if (matcher.find()) {
            fullName = matcher.group(1);
        }

        matcher = abbPattern.matcher(line);

        if (matcher.find()) {
            abbreviation = matcher.group(1);
        }

        if (shortName != null && fullName != null && abbreviation != null) {
            venues.put(shortName, new Venue(conference, abbreviation, fullName, shortName));
        } else {
            Console.error("%s tag detected, but no full information found:%n%s", (conference ? "Conference" : "Journal"), line);
        }
    }
    
    private static void replaceAuthors(BibItem item, Map<String, Author> authors) {
        // Replace authors
        String author = item.get("author");

        if (author != null && !author.isEmpty()) {
            String[] paperAuthors = author.split(" and ");

            for (String paperAuthor : paperAuthors) {
                Matcher matcher = authorPattern.matcher(paperAuthor);

                if (matcher.find()) {
                    Author a = authors.get(matcher.group(1));

                    if (a == null) {
                        Console.error("Author abbreviation \"%s\" is used, but never defined.", matcher.group(1));
                    } else {
                        item.getAuthors().add(a);
                    }
                } else {
                    item.getAuthors().add(new Author(paperAuthor));
                }
            }
        }
    }
    
    private static void expandAbbreviations(BibItem item, Map<String, String> abbreviations, Map<String, Venue> venues) {
        for (String field : item.getFields()) {
            String currentValue = item.get(field);

            if (currentValue != null && !currentValue.isEmpty()) {
                StringBuilder finalValue = new StringBuilder();
                Matcher matcher = abbrPattern.matcher(currentValue);
                int prevEnd = 0;

                while (matcher.find()) {
                    String abbreviation = matcher.group(1);
                    int start = matcher.start();
                    int end = matcher.end();

                    finalValue.append(currentValue.substring(prevEnd, start));

                    if (abbreviations.containsKey(abbreviation)) {
                        finalValue.append(abbreviations.get(abbreviation));
                    } else if (venues.containsKey(abbreviation)) {
                        finalValue.append(venues.get(abbreviation).getFullName());
                    } else {
                        Console.error("Abbreviation \"%s\" is used, but never defined.", matcher.group(1));
                    }

                    prevEnd = end;
                }

                finalValue.append(currentValue.substring(prevEnd, currentValue.length()));

                item.put(field, finalValue.toString());
            }
        }
    }

    private static void setVenue(BibItem item, Map<String, Venue> venues) {
        String venue = null;

        if (item.anyNonEmpty("booktitle")) {
            venue = item.get("booktitle");
        } else if (item.anyNonEmpty("journal")) {
            venue = item.get("journal");
        }

        if (venue != null) {
            Matcher matcher = abbrPattern.matcher(venue);

            while (matcher.find()) {
                String abbr = matcher.group(1);

                if (venues.containsKey(abbr)) {
                    item.setVenue(venues.get(abbr));
                    break;
                }
            }
        }
    }
}

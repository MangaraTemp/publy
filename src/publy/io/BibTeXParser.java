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
package publy.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import publy.Console;
import publy.data.Pair;
import publy.data.Author;
import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander
 */
public class BibTeXParser {

    private static final Pattern whiteSpace = Pattern.compile("\\s+"); // Regex that matches one or more whitespace characters
    // Patterns for author and abbreviation parsing
    private static final Pattern shortPattern = Pattern.compile("short=\"([^\"]*)\"");
    private static final Pattern fullPattern = Pattern.compile("full=\"([^\"]*)\"");
    private static final Pattern namePattern = Pattern.compile(" name=\"([^\"]*)\"");
    private static final Pattern htmlPattern = Pattern.compile("htmlname=\"([^\"]*)\"");
    private static final Pattern plainPattern = Pattern.compile("plaintextname=\"([^\"]*)\"");
    private static final Pattern urlPattern = Pattern.compile("url=\"([^\"]*)\"");
    private static final Pattern groupPattern = Pattern.compile("group=\"([^\"]*)\"");
    // Pattern for detecting an author link
    private static final Pattern authorPattern = Pattern.compile("<([^<>]*)>");
    // Pattern for detecting an abbreviation
    private static final Pattern abbrPattern = Pattern.compile("<<([^>]*)>>");

    private BibTeXParser() {
    }

    public static List<BibItem> parseFile(Path file) throws IOException {
        List<BibItem> items = new ArrayList<>();
        HashMap<String, String> abbreviations = new HashMap<>();
        HashMap<String, Author> authors = new HashMap<>();

        parseFile(file, items, abbreviations, authors);

        for (BibItem item : items) {
            expandAbbreviations(item, abbreviations);
            replaceAuthorsAndEditors(item, authors);
        }

        return items;
    }

    private static void parseFile(Path file, List<BibItem> items, Map<String, String> abbreviations, Map<String, Author> authors) throws IOException {
        HashSet<String> ids = new HashSet<>(); // Bibitem identifiers, used to check for duplicates

        try (BufferedReader in = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {
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
                    parseTag(collectItem(in, line, '<', '>'), abbreviations, authors);
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
        // Parse the type
        String type = bibItem.substring(1, bibItem.indexOf('{')).trim().toLowerCase();

        // Keep only the part between the outermost pair of braces
        String body = bibItem.substring(bibItem.indexOf('{') + 1, bibItem.lastIndexOf('}')).trim();

        // Parse the id
        String id;
        int idEnd = body.indexOf(',');

        if (idEnd == -1) {
            // No attributes
            id = body;
            body = "";
        } else {
            id = body.substring(0, idEnd).trim();
            body = body.substring(idEnd + 1).trim();
        }

        // Initialize the BibItem
        BibItem item = new BibItem(type, id);

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

        return item;
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

    private static void parseTag(String tag, Map<String, String> abbreviations, Map<String, Author> authors) {
        String type = tag.substring(1, tag.indexOf(' ', 2)).trim().toLowerCase();

        switch (type) {
            case "author":
                parseAuthor(tag, authors);
                break;
            case "abbr":
                parseAbbreviation(tag, abbreviations);
                break;
            default:
                Console.error("Unrecognized tag type \"%s\" at line \"%s\".", type, tag);
                break;
        }
    }

    private static void parseAuthor(String line, Map<String, Author> authors) {
        String shortName = null, name = null, plaintextName = null, htmlName = null, url = null, group = null;

        Matcher matcher = shortPattern.matcher(line);

        if (matcher.find()) {
            shortName = matcher.group(1);
        }

        matcher = namePattern.matcher(line);

        if (matcher.find()) {
            name = matcher.group(1);
        }

        matcher = htmlPattern.matcher(line);

        if (matcher.find()) {
            htmlName = matcher.group(1);
        }

        matcher = plainPattern.matcher(line);

        if (matcher.find()) {
            plaintextName = matcher.group(1);
        }

        matcher = urlPattern.matcher(line);

        if (matcher.find()) {
            url = matcher.group(1);
        }

        matcher = groupPattern.matcher(line);

        if (matcher.find()) {
            group = matcher.group(1);
        }

        if (shortName == null) {
            Console.error("Author tag is missing mandatory field \"short\":%n%s", line);
        } else if (name == null) {
            Console.error("Author tag is missing mandatory field \"name\":%n%s", line);
        } else {
            Author author = new Author(shortName, name);
            author.setUrl(url);
            author.setGroup(group);

            if (htmlName != null) {
                author.setName(Author.NameOutputType.HTML, htmlName);
            }

            if (plaintextName != null) {
                author.setName(Author.NameOutputType.PLAINTEXT, plaintextName);
            }

            authors.put(shortName, author);
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

    private static void replaceAuthorsAndEditors(BibItem item, Map<String, Author> authors) {
        replaceAuthors(item, authors, "author", item.getAuthors());
        replaceAuthors(item, authors, "editor", item.getEditors());
    }
    
    private static void replaceAuthors(BibItem item, Map<String, Author> authors, String field, List<Author> authorList) {
        String fieldValue = item.get(field);

        if (fieldValue != null && !fieldValue.isEmpty()) {
            String[] names = fieldValue.split(" [aA][nN][dD] "); // " and ", ignoring case

            for (String name : names) {
                Matcher matcher = authorPattern.matcher(name);

                if (matcher.find()) {
                    Author a = authors.get(matcher.group(1));

                    if (a == null) {
                        Console.error("Author abbreviation \"%s\" is used, but never defined.", matcher.group(1));
                    } else {
                        authorList.add(a);
                    }
                } else {
                    authorList.add(new Author(name));
                }
            }

            // Update the author field
            StringBuilder newFieldValue = new StringBuilder();
            boolean first = true;

            for (Author a : authorList) {
                if (first) {
                    first = false;
                } else {
                    newFieldValue.append(" and ");
                }

                newFieldValue.append(a.getName(Author.NameOutputType.LATEX));
            }

            item.put(field, newFieldValue.toString());
        }
    }

    private static void expandAbbreviations(BibItem item, Map<String, String> abbreviations) {
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
}

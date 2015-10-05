/*
 * Copyright 2015 Sander Verdonschot <sander.verdonschot at gmail.com>.
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
package publy.io.bibtexparser;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import publy.Console;

public class TagParser {

    private static final int[] SPECIAL_CHARACTERS = new int[]{
        '>', // End of tag
        '=', // Field-value separator
        '"', // Value delimiter "..."
    };
    private static final int MAX_RESET = 6000000; // ~12MB, Easily accomodates largest bib file I've found
    private static Tokenizer tokenizer;

    public static Tag parseTag(Reader in) throws IOException, ParseException {
        if (in.markSupported()) {
            in.mark(MAX_RESET);
        }

        tokenizer = new Tokenizer(in);
        tokenizer.setSpecialCharacters(SPECIAL_CHARACTERS);

        try {
            tokenizer.match(StreamTokenizer.TT_WORD);
            String type = tokenizer.getLastTokenAsString().toLowerCase();

            switch (type) {
                case "author":
                    return parseAuthor();
                case "abbr":
                    return parseAbbreviation();
                // Ignore valid HTML tags
                case "a":
                case "acronym":
                case "address":
                case "area":
                case "b":
                case "base":
                case "bdo":
                case "big":
                case "blockquote":
                case "body":
                case "br":
                case "button":
                case "caption":
                case "cite":
                case "code":
                case "col":
                case "colgroup":
                case "dd":
                case "del":
                case "dfn":
                case "div":
                case "dl":
                case "DOCTYPE":
                case "dt":
                case "em":
                case "fieldset":
                case "form":
                case "h1":
                case "h2":
                case "h3":
                case "h4":
                case "h5":
                case "h6":
                case "head":
                case "html":
                case "hr":
                case "i":
                case "img":
                case "input":
                case "ins":
                case "kbd":
                case "label":
                case "legend":
                case "li":
                case "link":
                case "map":
                case "meta":
                case "noscript":
                case "object":
                case "ol":
                case "optgroup":
                case "option":
                case "p":
                case "param":
                case "pre":
                case "q":
                case "samp":
                case "script":
                case "select":
                case "small":
                case "span":
                case "strong":
                case "style":
                case "sub":
                case "sup":
                case "table":
                case "tbody":
                case "td":
                case "textarea":
                case "tfoot":
                case "th":
                case "thead":
                case "title":
                case "tr":
                case "tt":
                case "ul":
                case "var":
                    Console.warn(Console.WarningType.OTHER, "Ignored HTML tag \"%s\" out of publication context.", type);
                    return null;
                default:
                    throw new ParseException(String.format("Unrecognized tag \"%s\".", type));
            }
        } catch (IOException | ParseException ex) {
            in.reset();
            throw ex;
        }
    }

    private static Tag parseAuthor() throws IOException, ParseException {
        Tag result = new Tag(Tag.Type.AUTHOR);

        parseFields(result);
        tokenizer.match('>');

        if (!result.values.containsKey("short")) {
            Console.error("Author tag is missing mandatory field \"short\".");
            return null;
        } else if (!result.values.containsKey("name")) {
            Console.error("Author tag is missing mandatory field \"name\".");
            return null;
        } else {
            return result;
        }
    }

    private static Tag parseAbbreviation() throws IOException, ParseException {
        Tag result = new Tag(Tag.Type.ABBREVIATION);

        parseFields(result);
        tokenizer.match('>');

        if (!result.values.containsKey("short")) {
            Console.error("Abbreviation tag is missing mandatory field \"short\".");
            return null;
        } else if (!result.values.containsKey("full")) {
            Console.error("Abbreviation tag is missing mandatory field \"full\".");
            return null;
        } else {
            return result;
        }
    }

    private static void parseFields(Tag result) throws IOException, ParseException {
        // name = " value "
        while (!tokenizer.nextTokenIs('>')) {
            tokenizer.match(StreamTokenizer.TT_WORD);
            String name = tokenizer.getLastTokenAsString();

            tokenizer.match('=');
            tokenizer.setWhiteSpaceMatters(true);
            tokenizer.match('"');

            StringBuilder value = new StringBuilder();

            while (!tokenizer.nextTokenIs('"')) {
                tokenizer.match(SPECIAL_CHARACTERS)
            }

            tokenizer.match(StreamTokenizer.TT_WORD);
            result.values.put(name, tokenizer.getLastTokenAsString());

            tokenizer.setWhiteSpaceMatters(false);
            tokenizer.match('"');
        }
    }
}

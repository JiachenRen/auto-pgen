package components;

import java.util.ArrayList;

import static components.ColoredPrinters.*;

/**
 * Created by Jiachen on 2/23/18.
 * Interpret raw results returned from search engine into appropriate java properties.
 */
public class Item {
    private String title;
    private String link;
    private String snippet;
    private String displayLink;

    private Item(String title, String link, String snippet, String displayLink) {
        this.title = title;
        this.link = link;
        this.snippet = snippet;
        this.displayLink = displayLink;
    }

    public static ArrayList<Item> extractItemsFrom(String rawContent) {
        return interpret(extractRawItems(rawContent));
    }

    private static ArrayList<String> extractRawItems(String input) {
        boldGreen.println("Extracting content from raw JSON string...");
        ArrayList<String> rawItems = new ArrayList<>();
        String keyword = "\"items\": ";
        int idx = input.indexOf("\"items\": ") + keyword.length();
        String buffer = "";
        int numBrackets = 0;
        boolean beginRead = false;
        for (String line : input.substring(idx + 1).split("\n")) {
            if (line.contains("{")) {
                numBrackets += 1;
                beginRead = true;
            } else if (line.contains("}")) {
                numBrackets -= 1;
            }
            if (numBrackets == 0 && beginRead) {
                rawItems.add(buffer);
                buffer = "";
            } else {
                buffer += line + "\n";
            }
        }
        return rawItems;
    }

    private static ArrayList<Item> interpret(ArrayList<String> rawItems) {
        boldGreen.println("Constructing items...");
        ArrayList<Item> items = new ArrayList<>();
        rawItems.forEach(rawItem -> {
            String[] lines = rawItem.split("\n");
            String title = "", link = "", snippet = "", displayLink = "";
            for (String line : lines) {
                if (line.contains("title")) {
                    title = extractContent(line);
                    boldBlack.print("[title] ");
                    normal.println(title);
                } else if (line.contains("link")) {
                    link = extractContent(line);
                    boldBlack.print("[link] ");
                    normal.println(link);
                } else if (line.contains("snippet")) {
                    snippet = extractContent(line).replace("\\n", "").replace("\\\"", "\"");
                    boldBlack.print("[snippet] ");
                    normal.println(snippet);
                } else if (line.contains("displayLink")) {
                    displayLink = extractContent(line);
                }
            }
            if (link.length() > 1) {
                items.add(new Item(title, link, snippet, displayLink));
            }
        });
        return items;
    }

    private static String extractContent(String line) {
        return line.substring(line.indexOf("\":") + 4, line.lastIndexOf("\""));
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getDisplayLink() {
        return displayLink;
    }

    public String toString() {
        return "title: " + getTitle() + "\n"
                + "snippet: " + getSnippet() + "\n"
                + "link: " + getLink() + "\n"
                + "displayLink: " + getDisplayLink() + "\n";
    }
}

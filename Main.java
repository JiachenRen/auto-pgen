import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        ArrayList<Item> items = Item.extractItemsFrom(Extractor.search("China"));
        System.out.println("Detailed: \n" + generateParagraph(items) + "\n");
        System.out.println("Simple: \n" + generateSimpleParagraph(items) + "\n");
        Dictionary d = Dictionary.getDefaultResourceInstance();
        System.out.println(d.lookupAllIndexWords("China").getIndexWordArray()[0].getSenses());
    }

    private static String getTestResponse() {
        return Extractor.read("./resources/example_response.txt");
    }

    private static String generateParagraph(ArrayList<Item> items) {
        String paragraph = "";
        for (Item item : items) {
            paragraph += item.getSnippet();
        }
        return paragraph;
    }

    private static String generateSimpleParagraph(ArrayList<Item> items) {
        String paragraph = "";
        for (Item item : items) {
            paragraph += getFirstSentence(item.getSnippet());
        }
        return paragraph;
    }

    private static String getFirstSentence(String line) {
        return line.contains(". ") ? line.substring(0, line.indexOf(". ") + 2) : line;
    }
}
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
//        ArrayList<Item> items = Item.extractItemsFrom(Extractor.search("China"));
//        System.out.println("Detailed: \n" + generateParagraph(items) + "\n");
//        System.out.println("Simple: \n" + generateSimpleParagraph(items) + "\n");

        Generator gen = new Generator();
        System.out.println(gen.generateSimpleParagraph(Item.extractItemsFrom(Extractor.search("urbanization")), 5));
    }

    private static String getTestResponse() {
        return Extractor.read("./resources/example_response.txt");
    }
}
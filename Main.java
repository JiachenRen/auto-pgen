import components.Extractor;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class Main {

    public static void main(String[] args) throws Exception {
//        ArrayList<components.Item> items = components.Item.extractItemsFrom(components.Extractor.search("China"));
//        System.out.println("Detailed: \n" + generateParagraph(items) + "\n");
//        System.out.println("Simple: \n" + generateSimpleParagraph(items) + "\n");

//        components.Generator gen = new components.Generator();
//        System.out.println(gen.generateSimpleParagraph(components.Item.extractItemsFrom(components.Extractor.search("urbanization")), 5));
//        System.out.println(components.Extractor.);
        getTestResponse();
    }

    private static String getTestResponse() {
        return Extractor.readFromResources("/example_response.txt");
    }
}
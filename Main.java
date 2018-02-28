import components.Extractor;
import components.Generator;
import components.Item;

import static components.ColoredPrinters.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String raw = getTestResponse();
        Generator gen = new Generator();
        gen.setIncludeSources(true);
        gen.setShuffleSentences(true);
        gen.setCrossContextWordSwapping(false);
        gen.setPosSpecificSynMapping(true);
        gen.setDebug(true);
        String paragraph = gen.generateSimpleParagraph(Item.extractItemsFrom(raw), 20);
        boldRed.println(paragraph);
    }

    private static String getTestResponse() {
        return Extractor.readFromResources("/example_response.txt");
    }
}
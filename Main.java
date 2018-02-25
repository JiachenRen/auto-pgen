public class Main {

    public static void main(String[] args) throws Exception {
//        ArrayList<Item> items = Item.extractItemsFrom(Extractor.search("China"));
//        System.out.println("Detailed: \n" + generateParagraph(items) + "\n");
//        System.out.println("Simple: \n" + generateSimpleParagraph(items) + "\n");

//        Generator gen = new Generator();
//        System.out.println(gen.generateSimpleParagraph(Item.extractItemsFrom(Extractor.search("urbanization")), 5));
//        System.out.println(Extractor.);
        getTestResponse();
    }

    private static String getTestResponse() {
        return Extractor.readFromResources("/example_response.txt");
    }
}
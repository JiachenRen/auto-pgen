import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jiachen on 2/25/18.
 * Commandline util for doing homework.
 */
public class CmdLine {
    private static Scanner scan = new Scanner(System.in);
    private static int minSentences = 2;
    private static int maxSentences = 7;
    private static boolean swapVerbs = true;
    private static boolean allowNounAsVerbs;
    private static boolean ignoreInfinitive;
    private static double swapRatio = 0.6;
    private static Generator gen;

    public static void main(String args[]) {
        l("Welcome to Auto Paragraph Generator by Jiachen Ren");
        l("Do you wish to customize the paragraph generator? [Y/N]");
        if (bool()) customize();
        l("Initializing automatic paragraph generator...");
        gen = new Generator(swapVerbs, swapRatio);
        gen.setAllowNounAsVerbs(allowNounAsVerbs);
        gen.setIgnoreInfinitive(ignoreInfinitive);
        l("Please enter topics (the program will accept any designated delimiters); enter [done] to finish.");
        String topicsRaw = "";
        while (true) {
            String line = scan.nextLine();
            if (line.toLowerCase().equals("done")) {
                break;
            }
            topicsRaw += line + "\n";
        }
        l("Subject area of concern (Simply press [ENTER] if you wish to do a generic search)? [US History/English... ect.]");
        String postfix = " " + scan.nextLine();
        l("Please enter delimiter: ");
        String delimiter = scan.nextLine();
        l("Please enter delimiter between topic and generated paragraph: ");
        String outputDelimiter = scan.nextLine();
        l("Working...");
        String topics[];
        if (!delimiter.equals("")) topics = topicsRaw.split(delimiter);
        else topics = new String[]{topicsRaw};
        for (String topic : topics) {
            String rawContent = Extractor.search(topic + (postfix.equals(" ") ? "" : postfix));
            ArrayList<Item> items = Item.extractItemsFrom(rawContent);
            int numSentences = (int) (minSentences + Math.random() * (maxSentences - minSentences + 1));
            String paragraph = gen.generateSimpleParagraph(items, numSentences);
            l(topic + outputDelimiter + "\n" + paragraph);
            l("\n");

        }
    }


    private static void customize() {
        l("Max number of sentences [1 - 20]: ");
        maxSentences = num(1, 10);
        l("Min number of sentences [1 - 20]: ");
        minSentences = num(1, 10);
        l("Do you wish to swap out some of the verbs? [Y/N]");
        if (bool()) {
            swapVerbs = true;
            l("Do you wish to allow the generator to swap out verbs that are potentially nouns? [Y/N]");
            allowNounAsVerbs = bool();
            l("Do you wish to ignore all verbs in infinitive form when swapping? [Y/N]");
            ignoreInfinitive = bool();
            l("Ratio of verbs to be swapped out [0.0 - 1.0]: ");
            swapRatio = decimal(0, 1);
        }
    }

    private static boolean bool() {
        while (true) {
            String response = scan.nextLine().toLowerCase();
            switch (response) {
                case "y":
                    return true;
                case "yes":
                    return true;
                case "n":
                    return false;
                case "no":
                    return false;
            }
            l("Please enter \"y\" for yes, \"no\" for no.");
        }
    }

    private static void l(String s) {
        System.out.println(s);
    }

    private static int num(int min, int max) {
        while (true) {
            String response = scan.nextLine().toLowerCase();
            try {
                int num = Integer.valueOf(response);
                if (num <= max && num >= min) return num;
                else l("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                l("Please enter a valid number.");
            }
        }
    }

    private static double decimal(double min, double max) {
        while (true) {
            String response = scan.nextLine().toLowerCase();
            try {
                double num = Double.valueOf(response);
                if (num <= max && num >= min) return num;
                else l("Please enter a decimal number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                l("Please enter a valid decimal number.");
            }
        }
    }

}

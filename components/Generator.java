package components;

import net.sf.extjwnl.data.*;

import java.util.*;
import java.util.stream.Collectors;

import static components.Idioma.*;
import static components.ColoredPrinters.*;


/**
 * Created by Jiachen on 2/24/18.
 * Generate a paragraph from the resources collected over the internet.
 */
public class Generator {

    private boolean crossContextWordSwapping;
    private boolean posSpecificSynMapping;
    private boolean swapWords;
    private boolean shuffleSentences;
    private boolean includeSources;
    private double swapRatio;
    private boolean debug = false;
    private static String SUPER_SCRIPT_DIGITS = "⁰¹²³⁴⁵⁶⁷⁸⁹";
    private static ArrayList<Character> endings;

    static {
        endings = new ArrayList<>();
        Collections.addAll(endings, '.', '!', '?');
    }

    public Generator() {
        this(true, 1);
    }

    public Generator(boolean swapWords, double swapRatio) {
        this.crossContextWordSwapping = false;
        this.swapWords = swapWords;
        this.swapRatio = swapRatio;
        posSpecificSynMapping = true;
    }

    private String toSuperscript(int num) {
        String output = "";
        for (Character c : Integer.toString(num).toCharArray()) {
            char coded = SUPER_SCRIPT_DIGITS.charAt(c - 48);
            output += Character.toString(coded);
        }
        return output;
    }

    public String generateSimpleParagraph(ArrayList<Item> items, int numSentences) {
        ArrayList<String> pool = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            ArrayList<String> sentences = getSentences(item.getSnippet());
            List<String> obfuscated = sentences.stream().map(sentence ->
                    replaceWords(sentence, swapRatio))
                    .collect(Collectors.toList());
            sentences.clear();
            sentences.addAll(obfuscated);
            final int num = i;
            if (includeSources) {
                boldGreen.println("Generating sources...");
                pool.addAll(sentences.stream()
                        .map(sentence -> sentence.endsWith(" ") ?
                                sentence.substring(0, sentence.length() - 1) + toSuperscript(num + 1) + " " :
                                sentence + toSuperscript(num + 1))
                        .collect(Collectors.toList()));
            } else pool.addAll(sentences);
        }
        if (shuffleSentences) shuffle(pool);
        final String[] paragraph = {""};
        for (int i = 0; i < pool.size(); i++) {
            String sentence = pool.get(i);
            paragraph[0] += sentence;
            if (i == numSentences - 1) break;
        }
        if (includeSources) {
            for (int i = 0; i < items.size(); i++) {
                paragraph[0] += "\n" + toSuperscript(i + 1) + items.get(i).getLink();
            }
        }
        return paragraph[0];
    }

    private void shuffle(ArrayList<String> arrList) {
        ArrayList<String> shuffled = new ArrayList<>();
        boldGreen.println("Shuffling sentences...");
        while (arrList.size() > 0) {
            int idx = (int) (Math.random() * arrList.size());
            shuffled.add(arrList.remove(idx));
        }
        arrList.addAll(shuffled);
    }

    private ArrayList<String> getSentences(String paragraph) {
        ArrayList<String> sentences = new ArrayList<>();
        String sentence = "";
        char[] charArray = paragraph.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i], next = ' ';
            if (i <= charArray.length - 2) {
                next = charArray[i + 1];
            }
            boolean isAbbreviation = (i > 0 && Character.toUpperCase(charArray[i - 1]) == charArray[i - 1]);
            if (!isAbbreviation && endings.contains(c) && (next == '\"' || next == ' ')) {
                boolean endOfSentence = true;
                for (String abbr : Exceptions.ABBREVIATIONS.list()) {
                    if (paragraph.substring(0, i).toLowerCase().endsWith(" " + abbr.toLowerCase()))
                        endOfSentence = false;
                }
                if (endOfSentence) {
                    sentence += c + "" + next;
                    if (isValidSentence(sentence)) {
                        boldGreen.print("[accepted] ");
                        boldBlack.println(sentence);
                        sentences.add(sentence);
                    } else {
                        boldRed.print("[rejected] ");
                        boldBlack.println(sentence);
                    }
                    sentence = "";
                    i += 1;
                    continue;
                }
            }
            sentence += c;
        }
        return sentences;
    }

    private boolean isValidSentence(String sentence) {
        if (sentence.split(" ").length < 4) {
            return false;
        }
        for (String pattern : Exceptions.IGNORED_PATTERNS.list()) {
            if (pattern.contains(" <ignore case>")) {
                pattern = pattern.replace(" <ignore case>", "");
                if (sentence.toLowerCase().contains(pattern.toLowerCase()))
                    return false;
            } else if (sentence.contains(pattern))
                return false;
        }
        return true;
    }

    private String replaceWords(String org, double ratio) {
        String words[] = org.split(" ");
        String output = "";
        for (String word : words) {
            if (word.length() == 0) continue;
            String punctuation = "", lastLetter = word.substring(word.length() - 1);
            if (lastLetter.matches("[!.,?:;\"']")) {
                punctuation = lastLetter;
                word = word.substring(0, word.length() - 1);
            }
            ArrayList<POS> poses = getPos(word);
            if (Math.random() < ratio && (poses.size() == 1 || (poses.size() > 0 && crossContextWordSwapping))) {
                switch (poses.get(0)) {
                    case VERB:
                        if (!Exceptions.IGNORED_VERBS.contains(infinitiveFormOf(POS.VERB, word))) {
                            word = getConjugatedSynVerb(word, posSpecificSynMapping);
                            if (debug) word = "<" + word + ">";
                        }
                        break;
                    case ADJECTIVE:
                        word = getSynAdjective(word, posSpecificSynMapping);
                        if (debug) word = "[" + word + "]";
                        break;
                    case ADVERB:
                        if (!Exceptions.IGNORED_ADVERBS.contains(word.toLowerCase())) {
                            word = getSynAdverb(word, posSpecificSynMapping);
                            if (debug) word = "(" + word + ")";
                        }
                        break;
                }
            }
            output += word + punctuation + " ";
        }
        return output;
    }

    public void setCrossContextWordSwapping(boolean b) {
        crossContextWordSwapping = b;
    }

    public void setShuffleSentences(boolean shuffleSentences) {
        this.shuffleSentences = shuffleSentences;
    }

    public void setIncludeSources(boolean includeSources) {
        this.includeSources = includeSources;
    }

    public void setDebug(boolean b) {
        debug = b;
    }

    public void setPosSpecificSynMapping(boolean posSpecificSynMapping) {
        this.posSpecificSynMapping = posSpecificSynMapping;
    }
}

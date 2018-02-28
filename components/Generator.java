package components;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;

import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;

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
    private boolean swapVerbs;
    private boolean shuffleSentences;
    private boolean includeSources;
    private double swapRatio;
    private boolean debug = false;
    private static String SUPER_SCRIPT_DIGITS = "⁰¹²³⁴⁵⁶⁷⁸⁹";
    private static ArrayList<String> ignoredVerbs;
    private static ArrayList<String> ignoredPatterns;
    private static ArrayList<String> abbreviations;
    private static ArrayList<Character> endings;

    static {
        String[] arr = Extractor.readFromResources("/ignored_verbs.txt").split("\n");
        ignoredVerbs = new ArrayList<>();
        Collections.addAll(ignoredVerbs, arr);

        arr = Extractor.readFromResources("/ignored_patterns.txt").split("\n");
        ignoredPatterns = new ArrayList<>();
        Collections.addAll(ignoredPatterns, arr);

        arr = Extractor.readFromResources("/abbreviations.txt").split("\n");
        abbreviations = new ArrayList<>();
        Collections.addAll(abbreviations, arr);


        endings = new ArrayList<>();
        Collections.addAll(endings, '.', '!', '?');
    }

    public Generator() {
        this(true, 1);
    }

    public Generator(boolean swapVerbs, double swapRatio) {
        this.crossContextWordSwapping = false;
        this.swapVerbs = swapVerbs;
        this.swapRatio = swapRatio;
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
            if (swapVerbs) sentence = replaceVerbs(sentence, swapRatio);
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
        boldGreen.println("Extracting sentences from sources...");
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
                for (String abbr : abbreviations) {
                    if (paragraph.substring(0, i).toLowerCase().endsWith(" " + abbr.toLowerCase()))
                        endOfSentence = false;
                }
                if (endOfSentence) {
                    sentence += c + "" + next;
                    if (isValidSentence(sentence))
                        sentences.add(sentence);
                    else {
                        boldRed.print("[filtered] ");
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
        for (String pattern : ignoredPatterns) {
            if (pattern.contains(" <ignore case>")) {
                pattern = pattern.replace(" <ignore case>", "");
                if (sentence.toLowerCase().contains(pattern.toLowerCase()))
                    return false;
            } else if (sentence.contains(pattern))
                return false;
        }
        return true;
    }


    private String replaceVerbs(String org, double ratio) {
        String words[] = org.split(" ");
        String output = "";
        for (String word : words) {
            if (word.length() == 0) continue;
            String punctuation = "", lastLetter = word.substring(word.length() - 1);
            if (lastLetter.matches("[!.,?:;\"']")) {
                punctuation = lastLetter;
                word = word.substring(0, word.length() - 1);
            }

            if (((crossContextWordSwapping && is(POS.VERB, word)) || isExclusively(POS.VERB, word)) && Math.random() < ratio) {
                VerbTense verbTense = getVerbTense(word);
                ArrayList<String> synonyms = null;
                try {
                    synonyms = getSynonyms(POS.VERB, word, true); //TODO: make exclusivity as an option.
                } catch (JWNLException e) {
                    e.printStackTrace();
                }
                String infinitiveForm = infinitiveFormOf(POS.VERB, word);
                if (!ignoredVerbs.contains(infinitiveForm) && verbTense != null && synonyms != null && synonyms.size() > 0) {
                    boldGreen.print("[original] ");
                    boldBlack.print(word + " ");
                    boldYellow.print("[tense] ");
                    boldBlack.print(verbTense + " ");
                    String selected = synonyms.get((int) (synonyms.size() * Math.random()));
                    boldBlue.print("[synonym] ");
                    boldBlack.print(selected + " ");
                    String postfix = "";
                    if (selected.contains(" ")) {
                        int idx = selected.indexOf(" ");
                        postfix = selected.substring(idx);
                        selected = selected.substring(0, idx);
                    }
                    WordElement wordElement = lexicon.getWord(selected, LexicalCategory.VERB);
                    switch (verbTense) {
                        case GERUND:
                            word = conjugate(wordElement, Form.PRESENT_PARTICIPLE);
                            break;
                        case PAST_PARTICIPLE:
                            word = conjugate(wordElement, Form.PAST_PARTICIPLE);
                            break;
                        case PAST:
                            word = conjugate(wordElement, Tense.PAST);
                            break;
                        case THIRD_PERSON_SINGULAR:
                            word = conjugate(wordElement, Tense.PRESENT);
                            break;
                        case INFINITIVE:
                            word = selected;
                            break;
                    }
                    word += postfix;
                    boldGreen.print("[conjugated] ");
                    boldBlack.println(word + " ");
                    if (debug) word = "<" + word + ">";
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
}

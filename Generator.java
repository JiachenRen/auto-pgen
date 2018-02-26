import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.dictionary.Dictionary;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by Jiachen on 2/24/18.
 * Generate a paragraph from the resources collected over the internet.
 */
public class Generator {
    private Dictionary dict;
    private boolean allowNounAsVerbs;
    private boolean ignoreInfinitive;
    private boolean swapVerbs;
    private boolean shuffleSentences;
    private boolean includeSources = true;
    private double swapRatio;
    private boolean debug = false;
    private static String SUPER_SCRIPT_DIGITS = "⁰¹²³⁴⁵⁶⁷⁸⁹";
    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);
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

    Generator() {
        this(true, 0.5);
    }

    Generator(boolean swapVerbs, double swapRatio) {
        try {
            this.dict = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        this.allowNounAsVerbs = false;
        this.ignoreInfinitive = false;
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
            if (includeSources) pool.addAll(sentences.stream()
                    .map(sentence -> sentence.endsWith(" ") ?
                            sentence.substring(0, sentence.length() - 1) + toSuperscript(num + 1) + " " :
                            sentence + toSuperscript(num + 1))
                    .collect(Collectors.toList()));
            else pool.addAll(sentences);
        }
        if (shuffleSentences) shuffle(pool);
        final String[] paragraph = {""};
        for (int i = 0; i < pool.size(); i++) {
            String sentence = pool.get(i);
            if (swapVerbs) sentence = replaceVerbs(sentence, swapRatio);
            paragraph[0] += sentence;
            if (i == numSentences - 1) break;
        }
        for (int i = 0; i < items.size(); i++) paragraph[0] += "\n" + toSuperscript(i + 1) + items.get(i).getLink();
        return paragraph[0];
    }

    private void shuffle(ArrayList<String> arrList) {
        ArrayList<String> shuffled = new ArrayList<>();
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
                for (String abbr : abbreviations) {
                    if (paragraph.substring(0, i).toLowerCase().endsWith(" " + abbr.toLowerCase()))
                        endOfSentence = false;
                }
                if (endOfSentence) {
                    sentence += c + "" + next;
                    if (isValidSentence(sentence))
                        sentences.add(sentence);
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

    private ArrayList<String> getSynonyms(POS pos, String word) throws JWNLException {
        ArrayList<String> synonyms = new ArrayList<>();
        IndexWord indexWord = dict.lookupIndexWord(pos, word);
        if (indexWord == null) return synonyms;
        String inf = this.infinitiveFormOf(pos, word);
        for (long i : indexWord.getSynsetOffsets()) {
            Synset set = dict.getSynsetAt(pos, i);
            for (Word word1 : set.getWords()) {
                String lemma = word1.getLemma();
                if (lemma.equals(inf))
                    continue;
                if (!synonyms.contains(lemma) && !lemma.contains(inf)) {
                    synonyms.add(lemma);
                }
            }
        }
        return synonyms;
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
            if (is(POS.VERB, word) && (allowNounAsVerbs || !is(POS.NOUN, word)) && Math.random() < ratio) {
                VerbTense verbTense = getVerbTense(word);
                ArrayList<String> synonyms = null;
                try {
                    synonyms = getSynonyms(POS.VERB, word);
                } catch (JWNLException e) {
                    e.printStackTrace();
                }
                String infinitiveForm = infinitiveFormOf(POS.VERB, word);
                if (!ignoredVerbs.contains(infinitiveForm) && verbTense != null && synonyms != null && synonyms.size() > 0) {
                    String selected = synonyms.get((int) (synonyms.size() * Math.random()));
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
                            if (!ignoreInfinitive)
                                word = selected;
                            break;
                    }
                    word += postfix;
                    if (debug) word = "<" + word + ">";
                }
            }
            output += word + punctuation + " ";
        }
        return output;
    }

    private boolean is(POS pos, String candidate) {
        try {
            return dict.lookupIndexWord(pos, candidate) != null;
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private VerbTense getVerbTense(String verb) {
        try {
            verb = verb.toLowerCase();
            IndexWord indexWord = dict.lookupIndexWord(POS.VERB, verb);
            if (indexWord == null) return null;
            if (indexWord.getLemma().equals(verb)) return VerbTense.INFINITIVE;
            WordElement word = lexicon.getWord(indexWord.getLemma(), LexicalCategory.VERB);
            if (this.conjugate(word, Tense.PRESENT).equals(verb)) {
                return VerbTense.THIRD_PERSON_SINGULAR;
            } else if (conjugate(word, Tense.PAST).equals(verb)) {
                return VerbTense.PAST;
            } else if (conjugate(word, Form.PAST_PARTICIPLE).equals(verb)) {
                return VerbTense.PAST_PARTICIPLE;
            } else if (conjugate(word, Form.PRESENT_PARTICIPLE).equals(verb)) {
                return VerbTense.GERUND;
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String conjugate(WordElement word, Tense tense) {
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.TENSE, tense);
        return realiser.realise(inflected).getRealisation();
    }

    private String conjugate(WordElement word, Form form) {
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.FORM, form);
        return realiser.realise(inflected).getRealisation();
    }

    private String infinitiveFormOf(POS pos, String word) {
        try {
            IndexWord inf = dict.lookupIndexWord(pos, word);
            if (inf != null) {
                return inf.getLemma();
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return "";
    }

    void setAllowNounAsVerbs(boolean b) {
        allowNounAsVerbs = b;
    }

    void setIgnoreInfinitive(boolean b) {
        ignoreInfinitive = b;
    }

    public void setShuffleSentences(boolean shuffleSentences) {
        this.shuffleSentences = shuffleSentences;
    }

    public void setIncludeSources(boolean includeSources) {
        this.includeSources = includeSources;
    }
}

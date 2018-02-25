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

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringJoiner;


/**
 * Created by Jiachen on 2/24/18.
 * Generate a paragraph from the resources collected over the internet.
 */
public class Generator {
    private ArrayList<Item> items;
    private Dictionary dict;
    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);
    private static ArrayList<String> ignoredVerbs = new ArrayList<>();

    static {
        String[] arr = Extractor.read("./resources/ignored_verbs.txt").split("\n");
        Collections.addAll(ignoredVerbs, arr);
    }

    Generator(ArrayList<Item> items) {
        this.items = items;
        try {
            this.dict = Dictionary.getDefaultResourceInstance();
            this.getSynonyms(POS.ADJECTIVE, "thoughtful").forEach(System.out::println);
            System.out.println(getVerbTense("stolen"));
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        String modified = replaceVerbs("Manifest Destiny refers to a period of violence", false, false, 1);
        System.out.println(modified);
// System.out.println(conjugate(lexicon.getWord("sware", LexicalCategory.VERB), Form.PRESENT_PARTICIPLE));
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

    //TODO: when returned synonym contains two words, conjugate the first one.
    private String replaceVerbs(String org, boolean allowNounAsVerbs, boolean ignoreInfinitive, double ratio) {
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
}

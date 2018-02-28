package components;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;

import static components.ColoredPrinters.*;

/**
 * Created by Jiachen on 2/26/18.
 * Manages SimpleNLG, CoreNLP, and WordNet functions
 */
public class Idioma {
    static Lexicon lexicon = Lexicon.getDefaultLexicon();
    //    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);
    private static Dictionary dict;

    static {
        try {
            dict = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }


    static String conjugate(WordElement word, Tense tense) {
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.TENSE, tense);
        return realiser.realise(inflected).getRealisation();
    }

    static String conjugate(WordElement word, Form form) {
        InflectedWordElement inflected = new InflectedWordElement(word);
        inflected.setFeature(Feature.FORM, form);
        return realiser.realise(inflected).getRealisation();
    }

    static String infinitiveFormOf(POS pos, String word) {
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

    static VerbTense getVerbTense(String verb) {
        try {
            verb = verb.toLowerCase();
            IndexWord indexWord = dict.lookupIndexWord(POS.VERB, verb);
            if (indexWord == null) return null;
            if (indexWord.getLemma().equals(verb)) return VerbTense.INFINITIVE;
            WordElement word = lexicon.getWord(indexWord.getLemma(), LexicalCategory.VERB);
            if (conjugate(word, Tense.PRESENT).equals(verb)) {
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

    public static boolean is(POS pos, String candidate) {
        try {
            return dict.lookupIndexWord(pos, candidate) != null;
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isExclusively(POS pos, String candidate) {
        if (!is(pos, candidate)) return false;
        for (POS pos1 : POS.getAllPOS()) {
            if (!pos.equals(pos1) && is(pos1, candidate)) return false;
        }
        return true;
    }

    /**
     * @param word the word to be looked up
     * @return an ArrayList containing parts of speech of the given word.
     */
    public static ArrayList<POS> getPos(String word) {
        ArrayList<POS> poses = new ArrayList<>();
        POS.getAllPOS().forEach(pos -> {
            if (is(pos, word)) poses.add(pos);
        });
        return poses;
    }

    public static String lookup(POS pos, String word) {
        try {
            return dict.lookupIndexWord(pos, word).getLemma();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param pos       part of speech of the word
     * @param word      word in which a synonym is going to be derived from
     * @param exclusive whether or not to return synonyms that have exclusively the designated part of speech
     * @return an ArrayList containing synonyms
     * @throws JWNLException some unknown WordNet internal error
     */
    static ArrayList<String> getSynonyms(POS pos, String word, boolean exclusive) throws JWNLException {
        ArrayList<String> synonyms = new ArrayList<>();
        IndexWord indexWord = dict.lookupIndexWord(pos, word);
        if (indexWord == null) return synonyms;
        String inf = Idioma.infinitiveFormOf(pos, word);
        for (long i : indexWord.getSynsetOffsets()) {
            Synset set = dict.getSynsetAt(pos, i);
            for (Word word1 : set.getWords()) {
                String lemma = word1.getLemma();
                if (lemma.equals(inf) || (exclusive && !isExclusively(pos, lemma)))
                    continue;
                if (!synonyms.contains(lemma) && !lemma.contains(inf)) {
                    synonyms.add(lemma);
                }
            }
        }
        return synonyms;
    }

    /**
     * TODO: don't forget to conjugate back! "largest" should be replaced with "most prominent", but now it returns "prominent"
     * @param adjective        the synonyms are going to be derived from this adjective
     * @param exclusiveResults whether or not to return synonyms that can only be used as adjectives
     * @return a randomly selected adjective
     */
    static String getSynAdjective(String adjective, boolean exclusiveResults) {
        try {
            ArrayList<String> synonyms = getSynonyms(POS.ADJECTIVE, adjective, exclusiveResults);
            if (synonyms.size() == 0) return adjective; //no synonyms found
            boldCyan.print("[adjective] ");
            boldBlack.print(adjective);
            String replacement = getRandom(synonyms);
            boldBlue.print(" [synonym] ");
            boldBlack.println(replacement + " ");
            return replacement;
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return adjective;
    }

    /**
     * @param adverb           the synonyms are going to be derived from this adverb
     * @param exclusiveResults whether or not to return synonyms that can only be used as adverbs
     * @return a randomly selected adverb
     */
    static String getSynAdverb(String adverb, boolean exclusiveResults) {
        try {
            ArrayList<String> synonyms = getSynonyms(POS.ADVERB, adverb, exclusiveResults);
            if (synonyms.size() == 0) return adverb; //no synonyms found
            boldCyan.print("[adverb] ");
            boldBlack.print(adverb);
            String replacement = getRandom(synonyms);
            boldBlue.print(" [synonym] ");
            boldBlack.println(replacement + " ");
            return replacement;
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return adverb;
    }

    private static String getRandom(ArrayList<String> pool) {
        return pool.get((int) (pool.size() * Math.random()));
    }

    /**
     * @param verb             the synonyms are going to be derived from this verb
     * @param exclusiveResults whether or not to return synonyms that can only be used as verbs
     * @return a randomly selected and conjugated verb that matches the original tense
     */
    static String getConjugatedSynVerb(String verb, boolean exclusiveResults) {
        VerbTense verbTense = getVerbTense(verb);
        ArrayList<String> synonyms = null;
        try {
            synonyms = getSynonyms(POS.VERB, verb, exclusiveResults);
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        if (verbTense != null && synonyms != null && synonyms.size() > 0) {
            boldCyan.print("[verb] ");
            boldBlack.print(verb + " ");
            boldYellow.print("[tense] ");
            boldBlack.print(verbTense + " ");
            String selected = getRandom(synonyms);
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
                    verb = conjugate(wordElement, Form.PRESENT_PARTICIPLE);
                    break;
                case PAST_PARTICIPLE:
                    verb = conjugate(wordElement, Form.PAST_PARTICIPLE);
                    break;
                case PAST:
                    verb = conjugate(wordElement, Tense.PAST);
                    break;
                case THIRD_PERSON_SINGULAR:
                    verb = conjugate(wordElement, Tense.PRESENT);
                    break;
                case INFINITIVE:
                    verb = selected;
                    break;
            }
            verb += postfix;
            boldGreen.print("[conjugated] ");
            boldBlack.println(verb + " ");
            return verb;
        } else return verb;
    }
}

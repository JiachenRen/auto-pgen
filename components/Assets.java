package components;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jiachen on 2/27/18.
 * Wrapper class that contains the assets to the generator's paraphrasing engine.
 */
public enum Assets {
    IGNORED_VERBS,
    IGNORED_PATTERNS,
    IGNORED_ADVERBS,
    ABBREVIATIONS;

    ArrayList<String> assets;

    Assets() {
        String fileName = "/" + this.toString().toLowerCase() + ".txt";
        String[] arr = Extractor.readFromResources(fileName).split("\n");
        assets = new ArrayList<>();
        Collections.addAll(assets, arr);
//        this.list().forEach(System.out::println);
    }

    boolean contains(String word) {
        return this.list().contains(word);
    }

    ArrayList<String> list() {
        return assets;
    }
}

import components.Extractor;
import components.Generator;
import components.Item;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

import static components.ColoredPrinters.boldGreen;
import static components.ColoredPrinters.boldYellow;
import static components.ColoredPrinters.magenta;

/**
 * Created by Jiachen on 3/14/18.
 * Auto Paragraph Generator Controller
 */
@SuppressWarnings("unused")
public class APGController {

    public TextField delimiter;
    public TextField subjectArea;
    public CheckBox sentenceShuffling;
    public CheckBox synReplacement;
    public CheckBox citeSources;
    public CheckBox crsCtxWordSwapping;
    public CheckBox posSpecSynMapping;
    public TextArea topics;
    public Button generate;
    public TextArea generatedPars;
    public ScrollBar minNumSentences;
    public ScrollBar maxNumSentences;
    public Label leftStatus;
    public Label minLabel;
    public Label maxLabel;

    private Generator generator;

    void initListeners(Generator generator) {
        this.generator = generator;
        l("Initializing event listeners... ");
        sentenceShuffling.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setShuffleSentences(newValue));
        synReplacement.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setSwapWords(newValue));
        citeSources.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setIncludeSources(newValue));
        crsCtxWordSwapping.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setCrossContextWordSwapping(newValue));
        posSpecSynMapping.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setPosSpecificSynMapping(newValue));
        minNumSentences.valueProperty().addListener((observable, oldValue, newValue) -> minLabel.setText(String.valueOf(minNumSentences.getValue())));
        maxNumSentences.valueProperty().addListener((observable, oldValue, newValue) -> maxLabel.setText(String.valueOf(maxNumSentences.getValue())));
    }

    public void generateButtonPressed(MouseEvent mouseEvent) {
        String topics[];
        String delimiter = this.delimiter.getText();
        String topicsRaw = this.topics.getText();
        if (!delimiter.equals("")) topics = topicsRaw.split(delimiter);
        else topics = new String[]{topicsRaw};
        String subjectArea = this.subjectArea.getText();
        int minSentences = (int) this.minNumSentences.getValue();
        int maxSentences = (int) this.maxNumSentences.getValue();
        generatedPars.setText("");

        String output = "";
        for (String t : topics) {
            final String[] topic = {t};
            Task<String> task = new Task<String>() {
                @Override
                public String call() throws Exception {
                    if (topic[0].endsWith("\n")) topic[0] = topic[0].substring(0, topic[0].length() - 1);
                    String rawContent = Extractor.search(topic[0] + (subjectArea.equals(" ") ? "" : subjectArea));
                    ArrayList<Item> items = Item.extractItemsFrom(rawContent);
                    int numSentences = (int) (minSentences + Math.random() * (maxSentences - minSentences + 1));
                    return generator.generateSimpleParagraph(items, numSentences);
                }
            };
            task.setOnSucceeded(event -> {
                leftStatus.setText("Working on \"" + topic[0] + "\" ...");
                String result = event.getSource().getValue().toString();
                String updatedContent = generatedPars.getText() + topic[0] + "\n" + result + "\n\n";
                generatedPars.setText(updatedContent);
            });
            new Thread(task).start();
        }
    }

    private void l(Object... os) {
        for (Object o : os) {
            p(o, "\n");
        }
    }


    private void p(Object... os) {
        for (Object o : os) {
            System.out.print(o);
        }
    }
}

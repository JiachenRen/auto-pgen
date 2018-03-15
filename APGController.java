import components.Extractor;
import components.Generator;
import components.Item;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

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
    public ProgressBar progressBar;
    public Label percentageLabel;

    private static int count;

    private Generator generator;

    void init(Generator generator) {
        this.generator = generator;
        this.progressBar.setVisible(false);
        this.percentageLabel.setVisible(false);

        sentenceShuffling.setSelected(generator.shuffleSentences());
        synReplacement.setSelected(generator.isSwapWords());
        citeSources.setSelected(generator.citesSources());
        crsCtxWordSwapping.setSelected(generator.performsCCWS());
        posSpecSynMapping.setSelected(generator.performsPSSM());

        l("Initializing event listeners... ");

        sentenceShuffling.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setShuffleSentences(newValue));
        synReplacement.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setSwapWords(newValue));
        citeSources.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setIncludeSources(newValue));
        crsCtxWordSwapping.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setCrossContextWordSwapping(newValue));
        posSpecSynMapping.selectedProperty().addListener((observable, oldValue, newValue) -> generator.setPosSpecificSynMapping(newValue));
        minNumSentences.valueProperty().addListener((observable, oldValue, newValue) -> minLabel.setText(String.valueOf((int) minNumSentences.getValue())));
        maxNumSentences.valueProperty().addListener((observable, oldValue, newValue) -> maxLabel.setText(String.valueOf((int) maxNumSentences.getValue())));
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
        progressBar.setVisible(true);
        percentageLabel.setVisible(true);
        count = 0;

        String output = "";
        for (String t : topics) {
            final String[] topic = {t};
            Task<String> task = new Task<String>() { //accelerated requests with multi-threading.
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
                leftStatus.setText("Working on " + (count + 1) + " of " + topics.length + ", " + topic[0] + " ...");
                double progress = (count + 1) / (double) topics.length;
                percentageLabel.setText(String.valueOf((int) (progress * 100)) + "%");
                progressBar.setProgress(progress);
                String result = event.getSource().getValue().toString();
                String updatedContent = generatedPars.getText() + topic[0] + "\n" + result + "\n\n";
                generatedPars.setText(updatedContent);
                if (count == topics.length - 1) {
                    leftStatus.setText("Done. ");
                    progressBar.setVisible(false);
                    percentageLabel.setVisible(false);
                }
                count++;
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

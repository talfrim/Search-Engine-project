import HandleParse.Parse;
import HandleReadFiles.QueryFileUtil;
import HandleSearch.DocDataHolders.DocumentDataToView;
import HandleSearch.DocDataHolders.QueryIDDocDataToView;
import HandleSearch.Searcher;
import IndexerAndDictionary.CountAndPointerDicValue;
import IndexerAndDictionary.Dictionary;
import IndexerAndDictionary.Indexer;
import OuputFiles.DictionaryFileHandler;
import OuputFiles.DocumentFile.DocumentFileHandler;
import OuputFiles.DocumentFile.DocumentFileObject;
import TermsAndDocs.TermCounterPair;
import TermsAndDocs.Terms.Term;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;


public class GUI extends Application implements EventHandler<ActionEvent> {

    Button startButton;
    Button inputPathBrowseButton;
    Button outputPathBrowseButton;
    Button resetButton;
    Button viewDictionaryButton;
    Button loadDictionaryToMemoryButton;
    //part 2
    Button searchQueryFromTextButton;
    Button queriesFileBrowseButton;
    Button searchUsingFileButton;
    Button resultsFilePathBrowseButton;


    TextField inputPathTextField;
    TextField outputPathTextField;
    //part 2
    TextField singleQueryTextField;
    TextField queriesFilePathTextFiled;
    TextField resultFileTextField;

    DirectoryChooser inputPathChooser;
    DirectoryChooser outputPathChooser;
    //part 2
    FileChooser queriesFileChooser;
    DirectoryChooser resultsFilePathChooser;

    CheckBox stemCheckBox;
    //part 2
    CheckBox semanticallySimilarCheckBox;
    CheckBox showEntitiesCheckBox;
    CheckBox showDateCheckBox;
    CheckBox writeResultsToFileCheckBox;


    String inputPath;
    String outputPath;

    Dictionary dictionary;

    //part 2
    Separator separator1;
    private String resultFileName = "results";


    @SuppressWarnings({"JoinDeclarationAndAssignmentJava", "Duplicates"})
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Search Engine");
        StackPane layout = new StackPane();
        VBox mainVBox = new VBox();
        HBox extraButtonsHBox;
        HBox inputHBox;
        HBox outputHBox;
        HBox singleQuerySearchHBox;
        HBox searchFromFileHBox;
        HBox resultFileHBox;
        HBox resultTableExtrasHBox;

        //directory choosers
        inputPathChooser = new DirectoryChooser();
        outputPathChooser = new DirectoryChooser();
        //part 2
        queriesFileChooser = new FileChooser();
        resultsFilePathChooser = new DirectoryChooser();

        //text fields
        inputPathTextField = new TextField();
        outputPathTextField = new TextField();
        //part 2
        singleQueryTextField = new TextField();
        singleQueryTextField.setText("Insert your query here");
        singleQueryTextField.setPrefSize(315, 40);
        queriesFilePathTextFiled = new TextField();
        queriesFilePathTextFiled.setText("Or choose a file...");
        resultFileTextField = new TextField();


        //buttons
        startButton = new Button("Start pre-processing");
        startButton.setMinHeight(40);
        startButton.setOnAction(this);
        inputPathBrowseButton = new Button("Browse input directory");
        inputPathBrowseButton.setOnAction(e -> {
            File inputLibrary = inputPathChooser.showDialog(primaryStage);
            inputPathTextField.setText(inputLibrary.getAbsolutePath());
        });
        outputPathBrowseButton = new Button("Browse output directory");
        outputPathBrowseButton.setOnAction(e -> {
            File outputLibrary = outputPathChooser.showDialog(primaryStage);
            outputPathTextField.setText(outputLibrary.getAbsolutePath());
        });
        resetButton = new Button("Reset");
        resetButton.setOnAction(this);
        viewDictionaryButton = new Button("View dictionary");
        viewDictionaryButton.setOnAction(this);
        loadDictionaryToMemoryButton = new Button("Load dictionary to memory");
        loadDictionaryToMemoryButton.setOnAction(this);
        //part 2
        searchQueryFromTextButton = new Button("RUN query");
        searchQueryFromTextButton.setMinHeight(40);
        searchQueryFromTextButton.setOnAction(this);
        queriesFileBrowseButton = new Button("Browse queries file");
        queriesFileBrowseButton.setOnAction(e -> {
            File queriesFile = queriesFileChooser.showOpenDialog(primaryStage);
            queriesFilePathTextFiled.setText(queriesFile.getAbsolutePath());
        });
        searchUsingFileButton = new Button("RUN on queries file");
        searchUsingFileButton.setOnAction(this);
        resultsFilePathBrowseButton = new Button("Browse result folder");
        resultsFilePathBrowseButton.setOnAction(e -> {
            File resultsPath = resultsFilePathChooser.showDialog(primaryStage);
            resultFileTextField.setText(resultsPath.getAbsolutePath());
        });


        //checkBox
        stemCheckBox = new CheckBox("Stemming");
        semanticallySimilarCheckBox = new CheckBox("Include sematically similar words");
        showEntitiesCheckBox = new CheckBox("Show top entities for results");
        showDateCheckBox = new CheckBox("Show document dates");
        writeResultsToFileCheckBox = new CheckBox("Write to result file");

        //separator
        separator1 = new Separator();
        separator1.setPrefHeight(5);

        //menu bar
        MenuBar menuBar = new MenuBar();
        Menu help = new Menu("Help");
        menuBar.getMenus().add(help);
        MenuItem readme = new MenuItem("Readme");
        help.getItems().add(readme);
        readme.setOnAction(e -> {
            AlertBox.display("Readme", ReadmeViewer.readmeStr);
        });


        //build scene
        mainVBox.getChildren().add(menuBar);
        inputHBox = new HBox(inputPathTextField, inputPathBrowseButton);
        outputHBox = new HBox(outputPathTextField, outputPathBrowseButton);
        mainVBox.getChildren().add(inputHBox);
        mainVBox.getChildren().add(outputHBox);
        mainVBox.getChildren().add(stemCheckBox);
        mainVBox.getChildren().add(startButton);
        extraButtonsHBox = new HBox(resetButton, viewDictionaryButton, loadDictionaryToMemoryButton);
        extraButtonsHBox.setSpacing(5);
        mainVBox.getChildren().add(extraButtonsHBox);
        //part 2
        mainVBox.getChildren().add(separator1);
        mainVBox.getChildren().add(semanticallySimilarCheckBox);
        resultTableExtrasHBox = new HBox(showEntitiesCheckBox, showDateCheckBox);
        resultTableExtrasHBox.setSpacing(15);
        mainVBox.getChildren().add(resultTableExtrasHBox);
        resultFileHBox = new HBox(resultFileTextField, resultsFilePathBrowseButton, writeResultsToFileCheckBox);
        resultFileHBox.setSpacing(5);
        mainVBox.getChildren().add(resultFileHBox);
        singleQuerySearchHBox = new HBox(singleQueryTextField, searchQueryFromTextButton);
        singleQuerySearchHBox.setSpacing(5);
        mainVBox.getChildren().add(singleQuerySearchHBox);
        searchFromFileHBox = new HBox(queriesFilePathTextFiled, queriesFileBrowseButton, searchUsingFileButton);
        searchFromFileHBox.setSpacing(5);
        mainVBox.getChildren().add(searchFromFileHBox);
        mainVBox.setSpacing(15);
        layout.getChildren().add(mainVBox);
        primaryStage.setScene(new Scene(layout, 400, 460));
        primaryStage.show();
    }

    /**
     * handles events in the gui
     *
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        boolean isWithStemming = stemCheckBox.isSelected();
        inputPath = inputPathTextField.getText();
        outputPath = outputPathTextField.getText();
        if (event.getSource() == startButton) {
            if (inputPath.equals("") || outputPath.equals(""))
                AlertBox.display("Alert", "Please choose paths and try again.");
            else
                startProgram(inputPath, outputPath, isWithStemming);
        }
        if (event.getSource() == resetButton) {
            reset(outputPath);
        }
        if (event.getSource() == viewDictionaryButton) {
            showSortedDictionary();
        }
        if (event.getSource() == loadDictionaryToMemoryButton) {
            loadDictionaryToMemory(outputPath);
        }
        //part 2
        if (event.getSource() == searchQueryFromTextButton) {
            if (singleQueryTextField.getText().equals("") || singleQueryTextField.getText().equals("Insert your query here"))
                AlertBox.display("", "Please write a query and try again!");
            else
                runSingleQuery(singleQueryTextField.getText(), semanticallySimilarCheckBox.isSelected());
        }
        if (event.getSource() == searchUsingFileButton) {
            if (queriesFilePathTextFiled.getText().equals("") || queriesFilePathTextFiled.getText().equals("Or choose a file..."))
                AlertBox.display("", "Please choose file and try again!");
            else
                runQueriesFromFile(queriesFilePathTextFiled.getText(), semanticallySimilarCheckBox.isSelected());
        }
    }

    @SuppressWarnings("Duplicates")
    private void runQueriesFromFile(String path, boolean similarWords) {
        long time1 = System.currentTimeMillis();
        try {
            HashMap<String, String> queriesHash = QueryFileUtil.extractQueries(path);
            ArrayList<String> queries = new ArrayList<>();
            ArrayList<String> queriesID = new ArrayList<>();
            for (Map.Entry<String, String> entry : queriesHash.entrySet()){
                queries.add(entry.getValue());
                queriesID.add(entry.getKey());
            }
            boolean writeToFile = writeResultsToFileCheckBox.isSelected();
            boolean entities = showEntitiesCheckBox.isSelected();
            if (dictionary == null) {
                try {
                    loadDictionaryToMemory(outputPath);
                } catch (Exception e) {
                    AlertBox.display("", "No dictionary file!");
                }
            }
            Searcher searcher = new Searcher(similarWords, stemCheckBox.isSelected(), dictionary, generateStopWords(), queries, entities);
            ArrayList<QueryIDDocDataToView> datas = new ArrayList<>();
            ArrayList<DocumentDataToView>[] queryAnswers = searcher.search();
            for (int i = 0; i < queryAnswers.length; i++) {
                for (DocumentDataToView docData : queryAnswers[i]) {
                    datas.add(new QueryIDDocDataToView(queriesID.get(i), docData.getDocNo(), docData.getDate(), docData.getEntities()));
                }
                System.out.println(i);
            }
            Collections.sort(datas, new Comparator<QueryIDDocDataToView>() {
                @Override
                public int compare(QueryIDDocDataToView o1, QueryIDDocDataToView o2) {
                    return o1.getQueryID().compareTo(o2.getQueryID());
                }
            });
            if (writeToFile) {
                path = resultFileTextField.getText() + "\\" + resultFileName + ".txt";
                try {
                    File toCreateFile = new File(path);
                    if (toCreateFile.exists())
                        toCreateFile.delete();
                    toCreateFile.createNewFile(); //create empty file
                    FileWriter fw = new FileWriter(path, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    for (int i = 0; i < datas.size(); i++) {
                        String queryId = datas.get(i).getQueryID();
                        if (queryId.charAt(queryId.length() - 1) == ' ')
                            queryId = queryId.substring(0, queryId.length() - 1);
                        bw.append(queryId + " " + 0 + " " + datas.get(i).getDocNo() + " " + "1" + " " + "1.1" + " " + "mt");
                        bw.newLine();
                    }
                    System.out.println("All Wriiten to file.");
                    bw.close();
                } catch (Exception e) {
                }
            }
            showResultsWithIds(datas);
            long time2 = System.currentTimeMillis();
            System.out.println("Total time: " + (time2-time1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    private void runSingleQuery(String query, boolean similarWords) {
        boolean writeToFile = writeResultsToFileCheckBox.isSelected();
        boolean entities = showEntitiesCheckBox.isSelected();
        if (dictionary == null) {
            try {
                loadDictionaryToMemory(outputPath);
            } catch (Exception e) {
                AlertBox.display("", "No dictionary file!");
            }
        }
        ArrayList<String> queryList = new ArrayList<>();
        queryList.add(query);
        Searcher searcher = new Searcher(similarWords, stemCheckBox.isSelected(), dictionary, generateStopWords(), queryList, entities);
        ArrayList<DocumentDataToView>[] answer = searcher.search();
        this.showResultsWithoutIds(answer[0]);
        if (writeToFile) {
            String qId = "000";
            String path = resultFileTextField.getText() + "\\" + resultFileName + ".txt";
            try {
                File toCreateFile = new File(path);
                if (toCreateFile.exists())
                    toCreateFile.delete();
                toCreateFile.createNewFile(); //create empty file
                FileWriter fw = new FileWriter(path, true);
                BufferedWriter bw = new BufferedWriter(fw);
                for (int i = 0; i < answer[0].size(); i++) {
                    bw.append(qId + " " + 0 + " " + answer[0].get(i).getDocNo() + " " + "1" + " " + "1.1" + " " + "mt" + "\n");
                    bw.newLine();
                }
                bw.close();
            } catch (Exception e) {
            }
        }

    }

    @SuppressWarnings("Duplicates")
    private void showResultsWithoutIds(ArrayList<DocumentDataToView> answer) {
        System.out.println("Showing results of single q");
        Stage stage = new Stage();
        TableView tableView = new TableView();

        TableColumn<String, DocumentDataToView> docNoCol = new TableColumn("DocNo");
        docNoCol.setCellValueFactory(new PropertyValueFactory<>("docNo"));
        TableColumn<String, DocumentDataToView> dateCol = new TableColumn("Doc Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<String, DocumentDataToView> entitiesCol = new TableColumn("Entities");
        entitiesCol.setCellValueFactory(new PropertyValueFactory<>("entities"));

        tableView.getColumns().add(docNoCol);
        if (showDateCheckBox.isSelected())
            tableView.getColumns().add(dateCol);
        if (showEntitiesCheckBox.isSelected())
            tableView.getColumns().add(entitiesCol);

        for (int i = 0; i < answer.size(); i++) {
            tableView.getItems().add(answer.get(i));
        }
        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    @SuppressWarnings("Duplicates")
    private void showResultsWithIds(ArrayList<QueryIDDocDataToView> answer) {
        Stage stage = new Stage();
        TableView tableView = new TableView();


        TableColumn<String, QueryIDDocDataToView> queryIdCol = new TableColumn("QueryId");
        queryIdCol.setCellValueFactory(new PropertyValueFactory<>("queryID"));
        TableColumn<String, QueryIDDocDataToView> docNoCol = new TableColumn("DocNo");
        docNoCol.setCellValueFactory(new PropertyValueFactory<>("docNo"));
        TableColumn<String, QueryIDDocDataToView> dateCol = new TableColumn("Doc Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<String, QueryIDDocDataToView> entitiesCol = new TableColumn("Entities");
        entitiesCol.setCellValueFactory(new PropertyValueFactory<>("entities"));

        tableView.getColumns().add(queryIdCol);
        tableView.getColumns().add(docNoCol);
        if (showDateCheckBox.isSelected())
            tableView.getColumns().add(dateCol);
        if (showEntitiesCheckBox.isSelected())
            tableView.getColumns().add(entitiesCol);

        for (int i = 0; i < answer.size(); i++) {
            tableView.getItems().add(answer.get(i));
        }
        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    private HashSet<String> generateStopWords() {
        try {
            return ProgramStarter.readStopWords(inputPath + "\\05 stop_words");
        } catch (Exception e) {
            AlertBox.display("", "Stop words file not found, default stop words file will be used");
            try {
                return ProgramStarter.readStopWords("data\\05 stop_words");
            } //default path
            catch (Exception ourE) {
                ourE.printStackTrace();
            }
        }
        return new HashSet<>();
    }

    private ArrayList<String> generateDocsFiles() {
        ArrayList<String> output = new ArrayList<>();
        String stemRelatedFolder = getStemRelatedFolderForDocFiles(stemCheckBox.isSelected());

        for (int i = 0; i < 6; i++) {
            String docFilePath = outputPath + "\\" + stemRelatedFolder + "\\DocsFiles\\docFile" + i;
            output.add(docFilePath);
        }
        return output;
    }

    private String getStemRelatedFolderForDocFiles(boolean selected) {
        if (selected)
            return "stemOur";
        return "noStemOur";
    }


    /**
     * loads doctionary from outputPath, according to stemming box
     *
     * @param outputPath
     */
    private void loadDictionaryToMemory(String outputPath) {
        DocumentFileHandler documentFileHandler = new DocumentFileHandler();
        DocumentFileObject documentFileObject = DocumentFileObject.getInstance();
        documentFileObject.setInstance(documentFileHandler.extractDocsData(generateDocsFiles()));
        System.out.println(documentFileObject.docsHolder.size());
        try {
            if (Indexer.dictionary.getDictionaryTable().size() == 0) {
                boolean isWithStemming = stemCheckBox.isSelected();
                DictionaryFileHandler dfh = new DictionaryFileHandler(new Dictionary());
                this.dictionary = dfh.readFromFile(outputPath, isWithStemming);
                System.out.println("loaded");
            } else {
                this.dictionary = Indexer.dictionary;
            }
        } catch (Exception e) {
            AlertBox.display("", "No dictionary in memory!");
        }
    }

    /**
     * showing sorted dictionary
     */
    private void showSortedDictionary() {
        Stage stage = new Stage();
        TableView tableView = new TableView();
        TableColumn<String, TermCounterPair> termCol = new TableColumn("Term");
        termCol.setCellValueFactory(new PropertyValueFactory<>("termStr"));
        TableColumn<Integer, TermCounterPair> countCol = new TableColumn("Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        tableView.getColumns().add(termCol);
        tableView.getColumns().add(countCol);

        for (Map.Entry<Term, CountAndPointerDicValue> entry : dictionary.dictionaryTable.entrySet()) {
            tableView.getItems().add(new TermCounterPair(entry.getKey().getData(), entry.getValue().getTotalCount()));
        }


        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * delete info from dictionary if files exists
     *
     * @param outputPath
     */
    private void reset(String outputPath) {
        File index = new File(outputPath);
        String[] entries = index.list();
        if (entries == null) {
            AlertBox.display("", "Nothing to delete!");
            return;
        }
        for (String s : entries) {
            File currentFile = new File(index.getPath(), s);
            if (!currentFile.exists()) {
                AlertBox.display("", "Nothing to delete!");
                return;
            }
            currentFile.delete();
        }
        index.delete();
        Parse.deleteStatics();
        Dictionary.deleteMutex();
        Indexer.deleteDictionary();
        dictionary = null;
    }

    /**
     * starts the processing
     *
     * @param inputPath
     * @param outputPath
     * @param toStemm
     */
    private void startProgram(String inputPath, String outputPath, boolean toStemm) {
        ProgramStarter.startProgram(inputPath, outputPath, toStemm);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



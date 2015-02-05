package testsuite;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the TestSuite
 */
public class TestSuiteController implements Initializable {

    private static final String GREEN = "-fx-background-color: #2ba721;";
    private static final String RED = "-fx-background-color: #cd3b3b;";

    @FXML
    private ListView<String> availableTests;

    @FXML
    private ListView<String> executedTests;

    @FXML
    private TextField dirField;

    @FXML
    private TextField exeField;

    @FXML
    private Button runTests;

    private final Model model;

    private final HashMap<String, Boolean> cellMap = new HashMap<>();

    public TestSuiteController() {
        this.model = new Model();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert availableTests != null;
        assert executedTests != null;
        assert dirField != null;

        availableTests.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        executedTests.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        executedTests.setCellFactory(param -> new ColorListCell());

        setupModel();
        setupDragAndDrop();
        setupRunButton();
    }

    private void setupRunButton() {
        runTests.setOnAction(event -> {
            try {
                for (String s : model.getExecutedTests()) {
                    String prcName = "";
                    prcName += model.getExecutable() + " ";
                    prcName += model.getDirectory() + s;
                    Process process = Runtime.getRuntime().exec(prcName);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        System.out.println(s + " [SUCCESSFUL]");
                    } else {
                        System.out.println(s + " [FAIL]");
                    }
                    cellMap.put(s, exitCode == 0);
                    updateExecutedTests();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateExecutedTests() {
        ObservableList<String> items = executedTests.getItems();
        executedTests.setItems(null);
        executedTests.setItems(items);
    }

    private void setupModel() {
        model.getAvailableTests().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasAdded())
                    for (String s : c.getAddedSubList())
                        availableTests.getItems().add(s);
                else if (c.wasRemoved())
                    for (String s : c.getRemoved())
                        availableTests.getItems().remove(s);
            }
        });
        model.getExecutedTests().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasAdded())
                    for (String s : c.getAddedSubList())
                        executedTests.getItems().add(s);
                else if (c.wasRemoved())
                    for (String s : c.getRemoved())
                        executedTests.getItems().remove(s);
            }
        });
        model.directoryProperty().addListener((observable, oldValue, newValue) -> {
            if (!model.getAvailableTests().isEmpty())
                model.getAvailableTests().clear();
            if (!model.getExecutedTests().isEmpty())
                model.getExecutedTests().clear();
            if (FileManager.directoryExists(newValue)) {
                File file = new File(newValue);
                String[] list = file.list((dir, name) -> name.endsWith(".java"));
                if (list != null)
                    model.getAvailableTests().addAll(list);
                dirField.setStyle(GREEN);
                model.setDirValid(true);
                updateRunButtonEnabled();
            } else {
                dirField.setStyle(RED);
                model.setDirValid(false);
                updateRunButtonEnabled();
            }
        });
        model.executableProperty().addListener((observable, oldValue, newValue) -> {
            if (FileManager.fileExists(newValue)) {
                exeField.setStyle(GREEN);
                model.setExeValid(true);
                updateRunButtonEnabled();
            } else {
                exeField.setStyle(RED);
                model.setExeValid(false);
                updateRunButtonEnabled();
            }
        });
        dirField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.setDirectory(newValue);
        });
        exeField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.setExecutable(newValue);
        });
    }

    private void updateRunButtonEnabled() {
        runTests.setDisable(!(model.isDirValid() && model.isExeValid()));
    }

    private void setupDragAndDrop() {
        availableTests.setOnDragDetected(event -> {
            Dragboard dragboard = availableTests.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("available->executed");
            dragboard.setContent(content);
            event.consume();
        });
        executedTests.setOnDragOver(event -> {
            if (event.getDragboard().hasString()
                    && event.getDragboard().getString().equals("available->executed")) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        executedTests.setOnDragDropped(event -> {
            /* copy selection first because listener mess up the update when working on the actual list */
            List<String> tmpList = new ArrayList<>(availableTests.getSelectionModel().getSelectedItems());
            model.getAvailableTests().removeAll(tmpList);
            model.getExecutedTests().addAll(tmpList);
        });
        executedTests.setOnDragDetected(event -> {
            Dragboard dragboard = executedTests.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("executed->available");
            dragboard.setContent(content);
            event.consume();
        });
        availableTests.setOnDragOver(event -> {
            if (event.getDragboard().hasString()
                    && event.getDragboard().getString().equals("executed->available")) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        availableTests.setOnDragDropped(event -> {
            List<String> tmpList = new ArrayList<>(executedTests.getSelectionModel().getSelectedItems());
            model.getExecutedTests().removeAll(tmpList);
            model.getAvailableTests().addAll(tmpList);
            tmpList.forEach(cellMap::remove);
        });
    }

    class ColorListCell extends ListCell<String> {
        public void colorGreen() {
            setStyle(GREEN);
        }
        public void colorRed() {
            setStyle(RED);
        }
        public void colorWhite() {
            setStyle("");
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                setText(item);
                if (cellMap.get(item) == null)
                    colorWhite();
                else if (cellMap.get(item))
                    colorGreen();
                else
                    colorRed();
            }
        }
    }
}

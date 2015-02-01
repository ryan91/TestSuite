package testsuite;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for the TestSuite (actually just a bean)
 */
public class Model {
    private ObservableList<String> availableTests;
    private ObservableList<String> executedTests;
    private SimpleStringProperty directory;
    private SimpleStringProperty executable;
    private boolean dirValid, exeValid;

    public Model() {
        availableTests = FXCollections.observableArrayList();
        executedTests = FXCollections.observableArrayList();
        directory = new SimpleStringProperty("");
        executable = new SimpleStringProperty("");
    }

    public ObservableList<String> getAvailableTests() {
        return availableTests;
    }

//    public void setAvailableTests(ObservableList<String> availableTests) {
//        this.availableTests = availableTests;
//    }

    public ObservableList<String> getExecutedTests() {
        return executedTests;
    }

//    public void setExecutedTests(ObservableList<String> executedTests) {
//        this.executedTests = executedTests;
//    }

    public String getDirectory() {
        return directory.get();
    }

    public SimpleStringProperty directoryProperty() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory.set(directory);
    }

    public String getExecutable() {
        return executable.get();
    }

    public SimpleStringProperty executableProperty() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable.set(executable);
    }

    public boolean isExeValid() {
        return exeValid;
    }

    public void setExeValid(boolean exeValid) {
        this.exeValid = exeValid;
    }

    public boolean isDirValid() {
        return dirValid;
    }

    public void setDirValid(boolean dirValid) {
        this.dirValid = dirValid;
    }
}

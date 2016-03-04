package com.keepfit.app.utils;

import java.util.List;

/**
 * Created by Edward on 3/4/2016.
 */
public class DataFolder {

    private String author;
    private int numberOfSteps;
    private String mode;
    private String orientation;
    private List<String> fileNames;
    private List<DataFile> files;

    public DataFolder(String path) {
        String folderName = path.split("/")[1];
        String[] fileValues = folderName.split("_");
        String author = fileValues[0];
        int numberOfSteps = Integer.parseInt(fileValues[1]);
        String mode = fileValues[2];
        String orientation = fileValues[3];

        this.author = author;
        this.numberOfSteps = numberOfSteps;
        this.mode = mode;
        this.orientation = orientation;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the numberOfSteps
     */
    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    /**
     * @param numberOfSteps the numberOfSteps to set
     */
    public void setNumberOfSteps(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public List<DataFile> getFiles() {
        return files;
    }

    public void setFiles(List<DataFile> files) {
        this.files = files;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
}

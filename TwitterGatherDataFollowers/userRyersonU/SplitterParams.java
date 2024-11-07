package TwitterGatherDataFollowers.userRyersonU;
import java.net.*;
import java.io.*;

public class SplitterParams {
    // filename of input file in resources
    private String fileName;

    private int parts;

    private String copyString;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public String getCopyString() {
        return copyString;
    }

    public void setCopyString(String copyString) {
        this.copyString = copyString;
    }
}
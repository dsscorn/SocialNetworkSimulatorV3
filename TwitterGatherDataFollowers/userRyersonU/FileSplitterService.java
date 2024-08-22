package TwitterGatherDataFollowers.userRyersonU;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;  
import java.nio.file.*;

public class FileSplitterService {
	static transient protected ControllerAgentGui myGui;
	static int nodeNumInt = Integer.parseInt((myGui.numNodesField.getText()));
	
    static String doc2vecDirName = "D:/Simulator-S-15-May-2020/Dataset/424k/"; 
    //public static final String OUT_FILE_NAME_PATTERN = doc2vecDirName + "out_set_doc2vec"+nodeNumInt+".txt";
	public static final String OUT_FILE_NAME_PATTERN = "D:/Simulator-S-15-May-2020/Dataset/424k/out_%s.txt";

    public void validate(SplitterParams theParams) {
        if (theParams.getParts() <= 0) {
            throw new RuntimeException("Invalid file parts parameter");
        }
        if (theParams.getFileName() == null || theParams.getFileName().equals("")) {
            throw new RuntimeException("Invalid file path");
        }
    }

    public void splitFile(SplitterParams theParams) throws IOException {
        validate(theParams);
        int outFileNum = 1;

        // string that if it's provided, we'll copy these lines that start
        // with this value (in first "column")
        String lineToCopy = theParams.getCopyString();
        boolean hasLineToCopy = lineToCopy != null && !lineToCopy.equals("");

        String fileName = theParams.getFileName();

        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        File file;
        try {
            file = new File(fileUrl.toURI());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String inPath = file.getAbsolutePath();

        try {
            List<String> copiedLines = new ArrayList<>();
            List<List<String>> fileContents = new ArrayList<>();


            // getting all the file contents
            List<String> allLines = Files.readAllLines(Paths.get(inPath));
            int linesPerFile = (allLines.size() / theParams.getParts()) + 1;

            int linesInCurrentFile = 0;
            List<String> contents = null;
            for (int i = 0; i < allLines.size(); i++) {
                if (contents == null) {
                    contents = new ArrayList<>();
                }

                // get line
                String line = allLines.get(i);

                // if has line to copy, copy line if necessary
                boolean dontSkip = true;
                if (hasLineToCopy) {
                    // we know it is delimited by tabs
                    String[] parts = line.split("\t");
                    if (parts.length <= 1) {
                        System.out.println("No idea what to do with this - just continue as before " + line);
                    }

                    // check if it's a line to copy
                    if (parts[0].equals(lineToCopy)) {
                        copiedLines.add(line);
                        linesPerFile--;
                        dontSkip = false;
                    }
                }

                if (dontSkip) {
                    contents.add(line);
                    linesInCurrentFile++;
                }

                if (linesInCurrentFile >= linesPerFile) {
                    System.out.println("Adding contents of length " + contents.size());
                    fileContents.add(contents);
                    linesInCurrentFile = 0;
                    contents = null;
                }
            }

            // write all the lines
            for (List<String> c : fileContents) {
                String outpath = inPath.replace(theParams.getFileName(),
                        String.format(OUT_FILE_NAME_PATTERN, outFileNum++)
                );
                try (FileWriter writer = new FileWriter(outpath)) {
                    // first write copied lines
                    for (String line : copiedLines) {
                        writer.write(line + "\n");
                    }

                    // then write other lines
                    for (String line : c) {
                        writer.write(line + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
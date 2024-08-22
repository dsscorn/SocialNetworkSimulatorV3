package TwitterGatherDataFollowers.userRyersonU;
import java.net.*;
import java.io.*;
import java.util.*;

public class App {
	
	static transient protected ControllerAgentGui myGui;
    static public ArrayList<String> usersRec = new ArrayList<String>();
	
    public static void main(String[] args) {
        
		int nodeNumInt = Integer.parseInt((myGui.numNodesField.getText()));
		usersRec = myGui.getUsersRec(); 
		String userREC = usersRec.get(0).toString();
		File fileFromGui = myGui.fileChooser.getSelectedFile();  // added by Sepide
	    String filePath = fileFromGui.getPath();
		
        FileSplitterService svc = new FileSplitterService();
        SplitterParams params = new SplitterParams();
        params.setParts(nodeNumInt);
        params.setFileName(filePath);
        params.setCopyString(userREC);

        try {
            svc.splitFile(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
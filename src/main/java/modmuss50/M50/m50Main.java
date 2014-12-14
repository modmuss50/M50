package modmuss50.M50;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class m50Main {

	public static File script;

	public static HashMap<Integer, String> gotos = new HashMap<Integer, String>();

	public static Boolean stopReading = false;


	public static void main(String[] args) throws IOException {
		System.out.println("Starting M50");
		File thisfolder = new File(".").getAbsoluteFile();
		script = new File(thisfolder, "script.m50");
		if(!script.exists()){
			System.out.println("No script file found!");
			System.exit(-1);
		}
		System.out.println("Will now try to run " + Integer.toString(countLines(script)) + " lines of script");
		System.out.println();
		System.out.println();

		loadPlaces();
		readFrom(1);

		System.out.println();
		System.out.println();
		System.out.println("Script finished");

	}

	public static void readFrom(int startLine) throws IOException {
		boolean proccess = false;
		int linenumber = 1;
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			if(stopReading){
				return;
			}
			if(linenumber == startLine){
				proccess = true;
			}
			if(proccess){
				if(!processLine(line, linenumber)){
				return;
				}
			}
			linenumber += 1;
		}
		br.close();
	}

	public static void loadPlaces() throws IOException {
		int linenumber = 1;
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			if(stopReading){
				return;
			}
			if(line.startsWith("place")){
				String name = line;
				gotos.put(linenumber, name.replaceAll("\"", "").replace("place:", ""));
			}
			linenumber += 1;
		}
		br.close();
	}



	public static boolean processLine (String line, int lineNumber) throws IOException {
		if(line.startsWith("print")){
			String message = line.replaceAll("\"", "").replace("print:", "");
			System.out.println(message);
		} else if(line.startsWith("goto")){
			String name = line.replaceAll("\"", "").replace("goto:", "");
			for(Map.Entry<Integer, String> entry : gotos.entrySet()){
				if(entry.getValue().equals(name)){
					readFrom(entry.getKey());
					stopReading = true;
				}
			}
		}
		return true;
	}


	public static int countLines(File filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}
}

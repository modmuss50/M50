package modmuss50.M50;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class m50Main {

	public static File script;

	public static HashMap<Integer, String> gotos = new HashMap<Integer, String>();
	//Fist string is the var name second is the data
	public static HashMap<String, String> strVars = new HashMap<String, String>();

	public static Boolean stopReading = false;

	public static void main(String[] args) throws IOException {
		System.out.println("Starting M50");
		File thisfolder = new File(".").getAbsoluteFile();
		script = new File(thisfolder, "script.m50");
		if (!script.exists()) {
			System.out.println("No script file found!");
			System.exit(-1);
		}
		System.out.println("Will now try to run " + Integer.toString(countLines(script)) + " lines of script");
		System.out.println();
		System.out.println();

		BufferedReader br = new BufferedReader(new FileReader(script));
		String scriptArgs;
		scriptArgs = br.readLine();
		if(!scriptArgs.startsWith("#m50")){
			System.out.println("this is not a script file");
			System.exit(-2);
		}
		br.close();
		if(!scriptArgs.contains("noPlaces")){
			//doing this allows us to go to places after the goto statement
			loadPlaces();
		}
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
			if (stopReading) {
				return;
			}
			if (linenumber == startLine) {
				proccess = true;
			}
			if (proccess) {
				if (!processLine(line, linenumber)) {
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
			if (stopReading) {
				return;
			}
			if (line.startsWith("place")) {
				String name = line;
				gotos.put(linenumber, name.replaceAll("\"", "").replace("place:", ""));
			}
			linenumber += 1;
		}
		br.close();
	}


	public static boolean processLine(String line, int lineNumber) throws IOException {
		if (line.startsWith("print")) {
			String[] vars = line.split(":");
			if(vars[1].contains("\"")){
				String message = line.replaceAll("\"", "").replace("print:", "");
				System.out.println(message);
			} else {
				for (Map.Entry<String, String> entry : strVars.entrySet()) {
					if(entry.getKey().equals(vars[1])){
						System.out.println(entry.getValue());
					}
				}
			}

		} else if (line.startsWith("goto")) {
			String name = line.replaceAll("\"", "").replace("goto:", "");
			for (Map.Entry<Integer, String> entry : gotos.entrySet()) {
				if (entry.getValue().equals(name)) {
					readFrom(entry.getKey());
					stopReading = true;
				}
			}
		} else if (line.startsWith("stop")){
			System.exit(-1);
		} else if(line.startsWith("input:")){
			String[] vars = line.split(":");
			Scanner reader = new Scanner(System.in);
			if(vars.length >= 3){
				System.out.println(vars[2]);
			} else {
				System.out.println("Enter input:");
			}
			strVars.put(vars[1], reader.next());
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

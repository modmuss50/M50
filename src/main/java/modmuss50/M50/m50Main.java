package modmuss50.M50;

import java.io.*;

public class m50Main {

	public static File script;

	public static void main(String[] args) throws IOException {
		System.out.println("Starting M50");
		File thisfolder = new File(".").getAbsoluteFile();
		script = new File(thisfolder, "script.m50");
		if(!script.exists()){
			System.out.println("No script file found!");
			System.exit(-1);
		}
		System.out.println("Will now try to run " + Integer.toString(countLines(script)) + " lines of script");


		int linenumber = 1;
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			processLine(line);
			linenumber += 1;
		}
		br.close();
	}


	public static void processLine (String line){
		if(line.startsWith("print")){
			String message = line.replaceAll("\"", "").replace("print:", "");
			System.out.println(message);
		}
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

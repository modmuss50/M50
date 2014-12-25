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
	//Fist string is the var name second is the data
	public static HashMap<String, Integer> intVars = new HashMap<String, Integer>();
	//Fist string is the var name second is the data
	public static HashMap<String, Boolean> booVars = new HashMap<String, Boolean>();

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
		if (!scriptArgs.startsWith("#m50")) {
			System.out.println("this is not a script file");
			System.exit(-2);
		}
		br.close();
		if (!scriptArgs.contains("noPlaces")) {
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
				String[] vars = line.split(":");
				String name = vars[1];
				Boolean canAdd = true;
				for (Map.Entry<Integer, String> entry : gotos.entrySet()) {
					if (entry.getValue().equals(name)) {
						String output = entry.getValue();
						System.out.println("A place exits with that name! :" + linenumber);
						canAdd = false;
					}
				}
				if (canAdd) {
					gotos.put(linenumber, name);
				}
			}
			linenumber += 1;
		}
		br.close();
	}

	public static boolean processLine(String line, int lineNumber) throws IOException {
		if (line.startsWith("//") || line.startsWith("-")) {
			//there is no need to do anything :)
			return true;
		} else if (line.startsWith("print")) {
			String[] vars = line.split(":");
			if (vars[1].contains("\"")) {
				String message = line.replaceAll("\"", "").replace("print:", "");
				System.out.println(message);
			} else {
				for (Map.Entry<String, String> entry : strVars.entrySet()) {
					if (entry.getKey().equals(vars[1])) {
						String output = entry.getValue();
						System.out.println(output);
					}
				}
				for (Map.Entry<String, Integer> entry : intVars.entrySet()) {
					if (entry.getKey().equals(vars[1])) {
						Integer output = entry.getValue();
						System.out.println(output);
					}
				}
				for (Map.Entry<String, Boolean> entry : booVars.entrySet()) {
					if (entry.getKey().equals(vars[1])) {
						Boolean output = entry.getValue();
						System.out.println(output);
					}
				}
			}
		} else if (line.startsWith("goto")) {
			String[] vars = line.split(":");
			String name = vars[1];
			for (Map.Entry<Integer, String> entry : gotos.entrySet()) {
				if (entry.getValue().equals(name)) {
					readFrom(entry.getKey());
					stopReading = true;
				}
			}
		} else if (line.startsWith("stop")) {
			System.exit(-1);
		} else if (line.startsWith("input:")) {
			String[] vars = line.split(":");
			Scanner reader = new Scanner(System.in);
			if (vars.length >= 3) {
				System.out.println(vars[2]);
			} else {
				System.out.println("Enter input:");
			}
			String input = reader.nextLine();
			strVars.put(vars[1], input);
		} else if (line.startsWith("var")) {
			String[] vars = line.split(":");
			if (vars[1].equals("str")) {
				for (Map.Entry<String, String> entry : strVars.entrySet()) {
					if (entry.getKey().equals(vars[2])) {
						System.out.println("A variable with that name exists! :" + lineNumber);
						return true;
					}
				}
				strVars.put(vars[2], vars[3]);
			}
			if (vars[1].equals("int")) {
				for (Map.Entry<String, Integer> entry : intVars.entrySet()) {
					if (entry.getKey().equals(vars[2])) {
						System.out.println("A variable with that name exists! :" + lineNumber);
						return true;
					}
				}
				intVars.put(vars[2], Integer.parseInt(vars[3]));
			}
			if (vars[1].equals("boo") || vars[1].equals("boolean")) {
				for (Map.Entry<String, Boolean> entry : booVars.entrySet()) {
					if (entry.getKey().equals(vars[2])) {
						System.out.println("A variable with that name exists! :" + lineNumber);
						return true;
					}
				}
				if (vars[3].equals("true")) {
					booVars.put(vars[2], true);
				} else if (vars[3].equals("false")) {
					booVars.put(vars[2], false);
				}
			}
		} else if (line.startsWith("if")) {
			String[] vars = line.split(":");
			if (vars.length <= 3) {
				System.out.println("Error at " + lineNumber);
			} else {
				if (vars[1].equals("str")) {
					String var1 = null;
					String var2 = null;
					if (vars[2].contains("\"")) {
						var1 = vars[2].replace("\"", "");
					} else {
						for (Map.Entry<String, String> entry : strVars.entrySet()) {
							if (entry.getKey().equals(vars[2])) {
								String output = entry.getValue();
								var1 = output;
							}
						}
					}
					if (vars[3].startsWith("\"")) {
						var2 = vars[3].replaceAll("\"", "");
					} else {
						for (Map.Entry<String, String> entry : strVars.entrySet()) {
							if (entry.getKey().equals(vars[3])) {
								String output = entry.getValue();
								var2 = output;
							}
						}
					}
					int endIf = 0;
					endIf = findEndIfWithElse(lineNumber);
					boolean hasElse = hasElse(lineNumber);
					if (endIf != 0) {
						if (vars[4].equals("=")) {
							if (var1.equals(var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum, endIf, true);
								}
							}
						} else if (vars[4].equals("!=")) {
							if (!var1.equals(var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						}
					} else {
						System.out.println("No end if found! at " + lineNumber);
					}

				} else if (vars[1].equals("int")) {
					int var1 = 0;
					int var2 = 0;
					for (Map.Entry<String, Integer> entry : intVars.entrySet()) {
						if (entry.getKey().equals(vars[2])) {
							int output = entry.getValue();
							var1 = output;
						}
					}
					if (var1 == 0) {
						var1 = Integer.parseInt(vars[2]);
					}

					for (Map.Entry<String, Integer> entry : intVars.entrySet()) {
						if (entry.getKey().equals(vars[3])) {
							int output = entry.getValue();
							var2 = output;
						}
					}
					if (var2 == 0) {
						var2 = Integer.parseInt(vars[3]);
					}
					int endIf = 0;
					endIf = findEndIfWithElse(lineNumber);
					boolean hasElse = hasElse(lineNumber);
					if (endIf != 0) {
						if (vars[4].equals("=")) {
							if (var1 == var2) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum, endIf, true);
								}
							}
						} else if (vars[4].equals("!=")) {
							if (var1 != var2) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals(">")) {
							if (var1 > var2) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals("<")) {
							if (var1 < var2) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals(">=")) {
							if (var1 >= var2) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals("<=")) {
							if (var1 <= var2) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals("!>")) {
							if (!(var1 > var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals("!<")) {
							if (!(var1 < var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals(">=!")) {
							if (!(var1 >= var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						} else if (vars[4].equals("<=!")) {
							if (!(var1 <= var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						}
					} else {
						System.out.println("No end if found! at " + lineNumber);
					}
				}if (vars[1].equals("boo") || vars[1].equals("boolean")) {
					boolean var1 = false;
					boolean var2 = false;

					if (vars[2].equals("true")) {
						var1 = true;
					} else if (vars[2].equals("false")) {
						var1 = false;
					} else{
						for (Map.Entry<String, Boolean> entry : booVars.entrySet()) {
							if (entry.getKey().equals(vars[2])) {
								Boolean output = entry.getValue();
								var1 = output;
							}
						}
					}

					if (vars[3].equals("true")) {
						var2 = true;
					} else if (vars[3].equals("false")) {
						var2 = false;
					} else{
						for (Map.Entry<String, Boolean> entry : booVars.entrySet()) {
							if (entry.getKey().equals(vars[3])) {
								Boolean output = entry.getValue();
								var2 = output;
							}
						}
					}
					int endIf = 0;
					endIf = findEndIfWithElse(lineNumber);
					boolean hasElse = hasElse(lineNumber);
					if (endIf != 0) {
						if (vars[4].equals("=")) {
							if (var1 = (var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum, endIf, true);
								}
							}
						} else if (vars[4].equals("!=")) {
							if (var1 != (var2)) {
								runIf(lineNumber + 1, endIf, false);
							} else {
								if (hasElse) {
									endIf = findEndIfWithoutElse(lineNumber);
									int elseNum = findElse(lineNumber);
									runIf(elseNum + 1, endIf, true);
								}
							}
						}
					} else {
						System.out.println("No end if found! at " + lineNumber);
					}

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

	public static int findEndIfWithElse(int startNum) throws IOException {
		int linenumber = 1;
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			if (linenumber >= startNum) {
				if (line.startsWith("endIf") || line.startsWith("else")) {
					return linenumber;
				}
			}
			linenumber += 1;
		}
		br.close();
		return 0;
	}

	public static int findEndIfWithoutElse(int startNum) throws IOException {
		int linenumber = 1;
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			if (linenumber >= startNum) {
				if (line.startsWith("endIf")) {
					return linenumber;
				}
			}
			linenumber += 1;
		}
		br.close();
		return 0;
	}

	public static int runIf(int startNum, int endNum, boolean isElse) throws IOException {
		int linenumber = 1;
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			if (linenumber >= startNum && linenumber <= endNum) {
				processLine(line.replace("-", ""), linenumber);
			}
			linenumber += 1;
		}
		br.close();
		return 0;
	}

	public static boolean hasElse(int startNum) throws IOException {
		int linenumber = 1;
		int lastNum = findEndIfWithElse(startNum);
		if (lastNum == 0) {
			return false;
		}
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			if (linenumber >= startNum && linenumber <= lastNum) {
				if (line.startsWith("else")) {
					return true;
				}
			}
			linenumber += 1;
		}
		br.close();
		return false;
	}

	public static int findElse(int startNum) throws IOException {
		int linenumber = 1;
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while ((line = br.readLine()) != null) {
			if (linenumber >= startNum) {
				if (line.startsWith("else")) {
					return linenumber;
				}
			}
			linenumber += 1;
		}
		br.close();
		return 0;
	}

}

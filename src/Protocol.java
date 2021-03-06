public class Protocol {
	final static int ab = 0;
	final static int register = 1;
	final static int wat = 2;
	final static int click = 3;
	final static int dndDown = 4;
	final static int dndUp = 5;
	final static int rclick = 6;
	final static int keyboard = 7;
	final static int launch = 8;
	final static int shortcut = 9;
	final static int commandLine = 10;
	final static int launchFromTaskBarList = 11;
	final static int getTaskBarIcons = 12;
	final static int setForegroundWindow = 13;
	final static int forward = 14;
	final static int overlay = 15;
	final static int centerClick = 16;
	final static int wheelDown = 17;
	final static int wheelUp = 18;

	public String output;
	public String outputA;
	public String outputB;
	public int outputPort;
	public String outputChar;
	public String outputToLounch;
	public String outputShortcut;
	public String outputCommandLine;
	public int outputForwardPort;
	public int outputOverlayNumber;
	public String[] overlayStrings;

	public int processInput(String input) {

		if (input.startsWith("ab:") && input.contains("lolParseMe")) {

			int lolpm = input.indexOf("lolParseMe");
			outputA = input.substring(3, lolpm);
			outputB = input.substring(lolpm + 10, input.length());
			return ab;
		} else if (input.startsWith("registerMe:")) {
			outputPort = Integer.parseInt(input.substring(11, input.length()));
			return register;
		} else if (input.startsWith("rclick:")) {

			return rclick;
		} else if (input.startsWith("click:")) {

			return click;
		} else if (input.startsWith("centerClick:")) {

			return centerClick;
		} else if (input.startsWith("dndDown:")) {

			return dndDown;
		} else if (input.startsWith("dndUp:")) {

			return dndUp;
		} else if (input.startsWith("keyboard:")) {
			String outputChars[] = input.split("::");
			outputChar = outputChars[1];
			return keyboard;
		} else if (input.startsWith("launch:")) {
			String outputStrings[] = input.split(":");
			outputToLounch = outputStrings[1];
			return launch;
		} else if (input.startsWith("shortcut:")) {
			String outputStrings[] = input.split("::");
			outputShortcut = outputStrings[1];
			return shortcut;
		} else if (input.startsWith("commandLine:")) {
			String outputStrings[] = input.split("::");
			outputCommandLine = input.substring(input.indexOf("::") + 2);

			//forward
			if (outputCommandLine.startsWith("forward::")) {
				outputCommandLine = outputCommandLine.substring(outputCommandLine.indexOf("::", 9) + 2);
				outputForwardPort = Integer.parseInt(outputStrings[2]);
				return forward;
				//overlay TODO перенести в отдельнный заголовок
			} else if (outputCommandLine.startsWith("overlay::")) {
				outputCommandLine = outputCommandLine.substring(outputCommandLine.indexOf("::", 9) + 2);
				outputOverlayNumber = Integer.parseInt(outputStrings[2]);
				overlayStrings = outputCommandLine.split(":");
				return overlay;
			} else {

				return commandLine;
			}

		} else if (input.startsWith("launchFromTaskBarList")) {

			return launchFromTaskBarList;
		} else if (input.startsWith("getTaskBarIcons")) {
			String outputStrings[] = input.split("::");
			output = outputStrings[1];
			return getTaskBarIcons;
		} else if (input.startsWith("setForegroundWindow")) {
			String outputStrings[] = input.split("::");
			output = outputStrings[1];
			return setForegroundWindow;
		} else if (input.startsWith("wheel")) {
			String outputStrings[] = input.split("::");
			output = outputStrings[1];
			if (output.equals("up")) {
				return wheelUp;
			} else {
				return wheelDown;
			}
		}


		return wat;
	}
}

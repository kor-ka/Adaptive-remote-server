

public class Protocol
{
final static int AB = 0;
final static int REGISTER = 1;
final static int BAD_INPUT = 2;

public String outputA;
public String outputB;
public int outputPort;
	
	public int processInput(String input){
		
		if(input.contains("ab:") ){
			String[] inputs = input.split(":");
			outputA = inputs[1];
			outputB = inputs[2];
			return AB;
		} else if(input.contains("registerMe:") ){
			outputPort =Integer.parseInt( input.substring(11, input.length()));
			return REGISTER;
		}
		
		return BAD_INPUT;
	}
}

import javax.print.attribute.standard.MediaSize.Other;

public class protocol
{
final static int ab =0;
final static int register =1;
final static int wat =2;

public String output;
public String outputA;
public String outputB;
public String outputIP;
	
	public int processInput(String input){
		
		if(input.contains("ab:") && input.contains("lolParseMe")){
			
			int lolpm = input.indexOf("lolParseMe");
			outputA = input.substring(3, lolpm);
			outputB = input.substring(lolpm+10, input.length());
			return ab;
		} else if(input.contains("registerMe:") ){
			outputIP = input.substring(11, input.length());
			return register;
		}
		
		return wat;
	}
}

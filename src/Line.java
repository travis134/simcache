
public class Line {
	private char command;
	private int address;
	public static final String[] COMMANDS = {"R", "W"};
	
	public Line(String content) throws NumberFormatException, LineException
	{
		command = '\0';
		//Make sure command is a legal value
		for(int i = 0; i < COMMANDS.length; i++)
		{
			if(content.charAt(0) == COMMANDS[i].charAt(0))
			{
				command = COMMANDS[i].charAt(0);
				break;
			}
		}
		if(command == '\0')
		{
			throw(new LineException("Unrecognized command ('" + content.charAt(0) + "')"));
		}
		
		//Make sure address is a legal value
		try
		{
			this.address = Integer.parseInt(content.substring(2), 16);
		}
		catch(NumberFormatException n)
		{
			throw(new LineException("Invalid address (" + content.substring(2) + ")\nNumberFormatException: " + n.getMessage()));
		}
	}
	
	public char getCommand()
	{
		return this.command;
	}
	
	public String getCommandString()
	{
		return Character.toString(this.command);
	}
	
	public int getAddress()
	{
		return this.address;
	}
	
	public String getAddressHex()
	{
		return Integer.toHexString(this.address);
	}
	
	public String getAddressOctal()
	{
		return Integer.toOctalString(this.address);
	}
	
	public String getAddressBinary()
	{
		return Integer.toBinaryString(this.address);
	}
	
	@Override
	public String toString()
	{
		return this.command + " " + this.getAddressHex();
	}
}

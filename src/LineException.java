
public class LineException extends Exception
{
	private int intError;
  
	LineException(int intErrNo)
	{
		intError = intErrNo;
	}

	LineException(String strMessage)
	{
		super(strMessage);
	}

	public String toString()
	{
		return "LineError["+intError+"]";
	}  
	
}
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Driver {
	
	static final int SUCCESS = 0;
	static final int INCORRECT_NUMBER_OF_ARGS_ERROR = 1;
	static final int INVALID_ARGS_ERROR = 2;
	static final int FILE_READ_ERROR = 3;
	static final int INVALID_LINE_ERROR = 4;
	public static final Integer[] ASSOCIATIVITIES = {1,2,4};
	public static final String[] REPLACEMENT_POLICIES = {"R","FIFO","LRU"};
	public static final String[] WRITE_POLICIES = {"wb", "wt"};
	
	public static void main(String [ ] args)
	{
		//Sample input
		args = new String[] {"C:\\Users\\travis\\Documents\\test.txt", "513", "21", "1","FIFO","wb"};
		
		//Make sure the user has supplied enough arguments
		if(args.length != 6)
		{
			System.out.println("Incorrect number of arguments error...");
			System.out.println("Irrecoverable failure, exiting...");
			System.out.println("Usage: simcache file_name cache_size block_size associativity replacement_policy write_policy");
			System.exit(INCORRECT_NUMBER_OF_ARGS_ERROR);
		}
		
		//Validate the arguments
		try
		{
			//cachSize: Parse first argument as integer
			int cacheSize = Integer.parseInt(args[1]);
			//Make sure cacheSize is a legal value
			if(cacheSize < 1)
			{
				System.out.println("Cache size (" + cacheSize + ") must be equal to or greater than 1...");
				cacheSize = 1;
				System.out.println("Setting cache size to: 1");
			}
			else if(Math.floor(Math.log(cacheSize) / Math.log(2)) != (Math.log(cacheSize) / Math.log(2)))
			{
				System.out.println("Cache size (" + cacheSize + ") must be base 2...");
				for(int i = 0; i < cacheSize * 2; i++)
				{
					if((i % 2) == 0)
					{
						if(Math.floor(Math.log(cacheSize + (i/2)) / Math.log(2)) == (Math.log(cacheSize + (i/2)) / Math.log(2)))
						{
							cacheSize = cacheSize + (i/2);
							break;
						}
					}
					else
					{
						if(Math.floor(Math.log(cacheSize - i) / Math.log(2)) == (Math.log(cacheSize - i) / Math.log(2)))
						{
							cacheSize = cacheSize - i;
							break;
						}
					}
				}
				System.out.println("Setting cache size to: " + cacheSize);
			}
			
			//blockSize: Parse second argument as integer
			int blockSize = Integer.parseInt(args[2]);
			//Make sure blockSize is a legal value with respect to cacheSize
			if(blockSize > cacheSize)
			{
				System.out.println("Block size (" + blockSize + ") must be less than or equal to cache size (" + cacheSize + ")...");
				blockSize = cacheSize;
				System.out.println("Setting block size to: " + blockSize);
			}
			else if(blockSize < 1)
			{
				System.out.println("Block size (" + blockSize + ") must be equal to or greater than 1...");
				blockSize = 1;
				System.out.println("Setting block size to: 1");	
			}
			else if((cacheSize % blockSize) != 0)
			{
				System.out.println("Block size (" + blockSize + ") must be a factor of cache size (" + cacheSize + ")...");
				for(int i = 0; i < cacheSize * 2; i++)
				{
					if((i % 2) == 0)
					{
						if(((blockSize + (i/2)) <= cacheSize) && ((cacheSize % (blockSize + (i/2))) == 0))
						{
							blockSize = blockSize + (i/2);
							break;
						}
					}
					else
					{
						if(((blockSize - i) >= 1) && ((cacheSize % (blockSize - i)) == 0))
						{
							blockSize = blockSize - i;
							break;
						}
					}
				}
				System.out.println("Setting block size to: " + blockSize);
			}
			
			//associativityIn: Parse third argument as integer
			int associativityIn = Integer.parseInt(args[3]);
			int associativity = -1;
			//Make sure associativity is a legal value
			for(int i = 0; i < ASSOCIATIVITIES.length; i++)
			{
				if(associativityIn == ASSOCIATIVITIES[i])
				{
					associativity = ASSOCIATIVITIES[i];
					break;
				}
			}
			if(associativity == -1)
			{
				System.out.println("Associativity (" + associativityIn + ") must be " + arrayToString(ASSOCIATIVITIES, ",","or") + "...");
				associativity = 0;
				System.out.println("Setting associativity to: " + ASSOCIATIVITIES[associativity]);	
			}			
		}
		catch(NumberFormatException n)
		{
			System.out.println("Invalid arguments error...");
			System.out.println(n.getMessage());
			System.out.println("Irrecoverable failure, exiting...");
			System.out.println("cache_size, block_size, and associativity must be integers.");
			System.exit(INVALID_ARGS_ERROR);
		}
		
		//Validate string arguments
		String replacementPolicyString = args[4];
		String writePolicyString = args[5];
		int replacementPolicy = -1;
		int writePolicy = -1;
		for(int i = 0; i < REPLACEMENT_POLICIES.length; i++)
		{
			if(replacementPolicyString.equalsIgnoreCase(REPLACEMENT_POLICIES[i]))
			{
				replacementPolicy = i;
				break;
			}
		}
		if(replacementPolicy == -1)
		{
			System.out.println("Replacement Policy (" + replacementPolicyString + ") must be " + arrayToString(REPLACEMENT_POLICIES, ",","or") + "...");
			replacementPolicy = 0;
			System.out.println("Setting replacement policy to: " + REPLACEMENT_POLICIES[replacementPolicy]);	
		}
		
		for(int i = 0; i < WRITE_POLICIES.length; i++)
		{
			if(writePolicyString.equalsIgnoreCase(WRITE_POLICIES[i]))
			{
				writePolicy = i;
				break;
			}
		}
		if(writePolicy == -1)
		{
			System.out.println("Write Policy (" + writePolicyString + ") must be " + arrayToString(WRITE_POLICIES, ",","or") + "...");
			writePolicy = 0;
			System.out.println("Setting replacement policy to: " + WRITE_POLICIES[writePolicy]);	
		}
		
		String fileName = args[0];
		
		//Open the input file and read contents line by line
		List<Line> inputLines = new ArrayList<Line>();
		try
		{
			FileInputStream fileInputStream = new FileInputStream(fileName);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			int lineNumber = 0;
			String content;
			while ((content = bufferedReader.readLine()) != null)
			{
				lineNumber++;
				try
				{
					inputLines.add(new Line(content));
				}
				catch(LineException l)
				{
					System.out.println("Invalid line error at line " + lineNumber + "...");
					System.out.println(l.getMessage());
					System.out.println("Irrecoverable failure, exiting...");
					System.exit(INVALID_LINE_ERROR);
				}
			}
			System.out.println("Finished reading " + inputLines.size() + " lines of input...");
			dataInputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("File read error...");
			System.out.println(e.getMessage());
			System.out.println("Irrecoverable failure, exiting...");
			System.exit(FILE_READ_ERROR);
		}
		
		for(int i = 0; i < inputLines.size(); i++)
		{
			System.out.println(inputLines.get(i).toString());
		}
		
		System.exit(SUCCESS);
	}
	
	//Turn an array, {1,2,3} into "1, 2, or 3"
	public static String arrayToString(Object[] array, String seperator, String lastSeperator)
	{
		String outputString = new String();
		if(array.length == 1)
		{
			outputString = array[0].toString();
		}
		else if(array.length == 2)
		{
			outputString = array[0].toString() + " " + lastSeperator + " " + array[1].toString();
		}
		else
		{
			for(int i = 0; i < array.length; i++)
			{
				if(i != 0 && i != array.length - 1)
				{
					outputString += seperator + " ";
				}
				else if(i == array.length - 1)
				{
					outputString += seperator + " " + lastSeperator + " ";
				}
				outputString += array[i].toString();
			}
		}
		return outputString;
	}
}

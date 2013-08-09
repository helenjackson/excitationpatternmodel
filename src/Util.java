
public class Util {
	
	//formats numbers for filenames
	public static String fmtNum(double d)
	{
		return String.valueOf(d).replaceAll("\\.0+$", "");
	}
	
	
	
	// returns the max value in an array
	public static double max(double[] array) {
	    double maximum = array[0];   // start with the first value
	    for (int i=1; i<array.length; i++)
	    {
	        if (array[i] > maximum)
	        {
	            maximum = array[i];   // new maximum
	        }
	    }
	    return maximum;
	}
	
	
	// returns the max value in an array
	public static double min(double[] array) {
	    double minimum = array[0];   // start with the first value
	    for (int i=1; i<array.length; i++)
	    {
	        if (array[i] < minimum)
	        {
	        	minimum = array[i];   // new maximum
	        }
	    }
	    return minimum;
	}
}

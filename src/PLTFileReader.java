import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PLTFileReader {

	public static ArrayList<Float> readFreqs(ModelParameters p, String iFilename) throws IOException
	{
		ArrayList<Float> lFreqs = new ArrayList<Float>();
		File inputFile = new File(PathUtils.combine(""+p.getSimulationFilepath(),iFilename+"_ex.out"));

		FileReader lFr = new FileReader(inputFile);      
		BufferedReader lBr = new BufferedReader(lFr);

		String lStr;
		Pattern lPattern = Pattern.compile(" *((-|)\\d+\\.\\d+) +((-|)\\d+\\.\\d+) *");              
		while((lStr = lBr.readLine()) != null)
		{
			Matcher lMatcher = lPattern.matcher(lStr);
			boolean lMatch = lMatcher.matches();
			if(lMatch)
			{
				//System.out.println("Matches regex");
				float lFirst = Float.parseFloat(lMatcher.group(1));				
				if ((lFirst > ((p.getN()-1)*p.getF0()) && (lFirst < ((p.getN()+p.getNComps()-1+1)*p.getF0()))))
				{				
					lFreqs.add(lFirst);				
				}
			}
		} 
		
		lBr.close();
		lFr.close();
		return lFreqs;
	}

	public static ArrayList<Float> readLevels(ModelParameters p, String iFilename) throws IOException
	{
		ArrayList<Float> lLevels = new ArrayList<Float>();
		File inputFile = new File(PathUtils.combine(""+p.getSimulationFilepath(),iFilename+"_ex.out"));

		FileReader lFr = new FileReader(inputFile);      
		BufferedReader lBr = new BufferedReader(lFr);

		String lStr;
		Pattern lPattern = Pattern.compile(" *((-|)\\d+\\.\\d+) +((-|)\\d+\\.\\d+) *");              
		while((lStr = lBr.readLine()) != null)
		{
			Matcher lMatcher = lPattern.matcher(lStr);
			boolean lMatch = lMatcher.matches();
			if(lMatch)
			{
				//System.out.println("Matches regex");
				float lFirst = Float.parseFloat(lMatcher.group(1));
				float lSecond = Float.parseFloat(lMatcher.group(3));
				
				if ((lFirst > ((p.getN()-1)*p.getF0()) && (lFirst < ((p.getN()+p.getNComps()-1+1)*p.getF0()))))
				{				
					lLevels.add(lSecond);//lSecond taken out		
					//System.out.println(""+lLevels.get(lLevels.size()-1));
				}
			}
		} 
		lBr.close();
		lFr.close();
		return lLevels;
	}
}

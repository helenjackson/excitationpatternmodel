import java.io.IOException;
import java.util.ArrayList;

public class TemplateCalculator {

	public static double[] run(ModelParameters iP, SimParameters sp, boolean iIsTemplate) throws IOException
	{		
		
		//make something to hold info about tones
		ArrayList<ArrayList<Float>> EPDiffTemplateList = new ArrayList<ArrayList<Float>>();

		for (int lTrialNum=1; lTrialNum<sp.getNumTemplateTrials()+1; lTrialNum++)
		{			
			//get the difference between the H and I pattern for this trial (only interval 1 needed)
			ArrayList<Float> EPDiff = new ArrayList<Float>();
			double[] lEPDiff1 = EPDiffGetter.getEPDiff(iP, sp, lTrialNum, 1, iIsTemplate);
			for (int li=0; li<lEPDiff1.length; li++)
			{
				EPDiff.add((Float)(float)lEPDiff1[li]);
				//System.out.println(""+EPDiff.get(li));
			}

			
			//put it in EPDiffTemplateList
			EPDiffTemplateList.add(EPDiff);
		}


		//average the two diffs and convert the ArrayList<Float> to a double[]
		double[] EPDiffTemplate = new double[EPDiffTemplateList.get(0).size()];
		for (int li=0; li<EPDiffTemplateList.get(0).size(); li++)
		{
			EPDiffTemplate[li] = (((EPDiffTemplateList.get(0)).get(li))+((EPDiffTemplateList.get(1)).get(li)))/2;
		}

/*
		//print out
		System.out.println("\n\nTemplate:");
		for (int li=0; li<EPDiffTemplate.length; li++)
		{
			System.out.println(""+EPDiffTemplate[li]);
		}
*/
		return EPDiffTemplate;
	}
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class Simulator {
	/* For each trial, this function gets and compares the correlations between two intervals
	 */	
	private static double[] mTemplate;

	public static double[] getTemplate()
	{
		return mTemplate;
	}

	public static void run(ModelParameters p, SimParameters sp, double[] iEPTemplate, boolean iIsTemplate) throws IOException
	{
		mTemplate = iEPTemplate;

		File lResults = new File(PathUtils.combine(p.getSimulationFilepath(),"results_"+p.getFilenameBase()+"_"+sp.getInternalNoise()+"N_"+sp.getNumTrials()+"trials_"+sp.getIsSharp()+"_run2.txt"));
		
		FileWriter lResultsFW = new FileWriter(lResults);
		BufferedWriter lResultsBW = new BufferedWriter(lResultsFW);


		//make counters to count how many times each interval is picked
		int hihiCount = 0;
		int hhhhCount = 0;
		int sameCount = 0;


		// make something to hold largest difference in dB between averaged intervals
		double largestDiff = 0;
		
		//loop for each trial starting one after number of template trials
		for (int lTrialNum=sp.getFirstTrial()+sp.getNumTemplateTrials(); lTrialNum<sp.getFirstTrial()+sp.getNumTrials()+sp.getNumTemplateTrials(); lTrialNum++)
		{
			//print out interval and trial number
			System.out.println("\n\n\nTRIAL "+lTrialNum+" INTERVAL 1");
			

			double[] lEPDiff1 = EPDiffGetter.getEPDiff(p, sp, lTrialNum, 1, iIsTemplate);
			
			if(Util.max(lEPDiff1) > largestDiff)
			{
				largestDiff = Util.max(lEPDiff1);
			}

			/*		System.out.println("\n\nlEPDiff1:");
			for (int li=0; li<lEPDiff1.length; li++)
			{
				System.out.println(""+lEPDiff1[li]);
			}
			 */

			double[] xCorr1 = CrossCorrelationCalculator.calculateCrossCorrelation(lTrialNum, lEPDiff1, iEPTemplate);		


			//interval 2
			System.out.println("\n\n\nTRIAL "+lTrialNum+" INTERVAL 2");
			

			double[] lEPDiff2 = EPDiffGetter.getEPDiff(p, sp, lTrialNum, 2, iIsTemplate);
			//System.out.println("Trial "+lTrialNum+", interval 2: largest diff = "+Util.max(lEPDiff2)+" dB");

			/*			System.out.println("\n\nlEPDiff2:");
			for (int li=0; li<lEPDiff2.length; li++)
			{
				System.out.println(""+lEPDiff2[li]);
			}
			 */			
			double[] xCorr2 = CrossCorrelationCalculator.calculateCrossCorrelation(lTrialNum, lEPDiff2, iEPTemplate);


			//calculate factor for normalising xcorrs
			//cross correlate each signal with itself and do maths on y intercepts of each function
			System.out.println("\n\n\nDOING CROSS CORRELATION...");
			double lFactor = XCorrFactorCalculator.getFactor(lTrialNum, lEPDiff1, lEPDiff2);


			//normalise both cross corrs using factor just calculated
			double[] xCorr1_norm = XCorrNormaliser.normalise(xCorr1, lFactor);
			double[] xCorr2_norm = XCorrNormaliser.normalise(xCorr2, lFactor);


			//print out for checking
			/*			System.out.println("\n\nxCorr1_norm:");
			for (int li=0; li<xCorr1_norm.length; li++)
			{
				System.out.println(""+xCorr1_norm[li]);
			}
			System.out.println("\n\nxCorr2_norm:");
			for (int li=0; li<xCorr2_norm.length; li++)
			{
				System.out.println(""+xCorr2_norm[li]);
			}
			 */		

			//find largest peak in each one
			double lLargestPeak1 = Util.max(xCorr1_norm);
			double lLargestPeak2 = Util.max(xCorr2_norm);


			//print out stuff for checking
 			System.out.println("\nLargest peak for interval 1 = "+lLargestPeak1);
			System.out.println("Largest peak for interval 2 = "+lLargestPeak2);
			 

			//pick largest peak 
			if (lLargestPeak1 > lLargestPeak2)
			{
				System.out.println("\n\nINTERVAL 1 (HIHI) PICKED");

				hihiCount++;
				System.out.println("\n\nHIHI picked "+hihiCount+" times out of "+(hihiCount+hhhhCount+sameCount)+" trials so far");
			}
			else if (lLargestPeak2 > lLargestPeak1)
			{
				System.out.println("\n\nINTERVAL 2 (HHHH) PICKED");

				hhhhCount++;
				System.out.println("\n\nHIHI picked "+hihiCount+" times out of "+(hihiCount+hhhhCount+sameCount)+" trials so far");
			}
			else
			{
				System.out.println("\nIDENTICAL");

				sameCount++;
				System.out.println("\nHIHI picked "+hihiCount+" times out of "+(hihiCount+hhhhCount+sameCount)+" trials so far");
			}
		}

//		System.out.println("\n\n\n\nLargest diff = "+largestDiff+" dB,");

		
		lResultsBW.write("The HIHI interval was picked "+hihiCount+" times out of "+sp.getNumTrials()+", and the correlations were the same "+sameCount+" times.");
		lResultsBW.newLine();
		lResultsBW.newLine();
		lResultsBW.write("Filters were sharp: "+sp.getIsSharp()+"\n" +
				"F0 was "+Util.fmtNum(p.getF0())+" Hz\n" +
						"N was "+p.getN()+"\n" +
								"delta f was "+Util.fmtNum(p.getShift())+"F0\n" +
										"nComps was "+p.getNComps()+"\n" +
												"phase was "+p.getPhase()+"\n" +
														"amplitude perturbation limit was "+Util.fmtNum(p.getAmpPert())+"dB\n" +
																"internal noise limit was "+sp.getInternalNoise()+" dB\n" +
																		"signal level was "+Util.fmtNum(p.getSignalLevel())+" dB SPL\n" +
																				"TEN level was "+p.getRelTEN());
		lResultsBW.close();
		lResultsFW.close();
	}
}

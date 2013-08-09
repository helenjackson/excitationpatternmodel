
public class XCorrFactorCalculator {

	public static double getFactor(int iTrialNum, double[] iEPDiff1, double[] iEPDiff2)
	{
		double[] ep1_1 = CrossCorrelationCalculator.calculateCrossCorrelation(iTrialNum, iEPDiff1, iEPDiff1);
		double[] ep2_2 = CrossCorrelationCalculator.calculateCrossCorrelation(iTrialNum, iEPDiff2, iEPDiff2);

		
		//factor = largest value in each function
		double diff1Largest = Util.max(ep1_1);
		double diff2Largest = Util.max(ep2_2);
		
		
		//calculate the normalisation factor: sqrt(x*y)
		double lFactor = Math.pow((diff1Largest*diff2Largest),0.5);
		System.out.println("\ndiff1Largest = "+diff1Largest+"\ndiff2Largest = "+diff2Largest+"\nfactor = "+lFactor);
		
		return lFactor;
	}
}


public class XCorrNormaliser {

	public static double[] normalise(double[] iXCorr, double iFactor)
	{
		double[] xCorr_norm = new double[iXCorr.length];
		
		for (int li=0; li<iXCorr.length; li++)
		{
			xCorr_norm[li] = iXCorr[li]/iFactor;
		}
		
		return xCorr_norm;
	}
}

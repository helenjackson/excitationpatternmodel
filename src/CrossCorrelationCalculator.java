
public class CrossCorrelationCalculator
{

	public static double[] calculateCrossCorrelation(int iTrialNum, double[] iEPDiff, double[] iEPTemplate)
	{
/*
		print everything out for checking
*/
		
/*	System.out.println("\n\n\n\nTRIAL NUMBER "+iTrialNum);

		 System.out.println("\n\niEPDiff entering xCorr function:");
		 
		for (int li=0; li<iEPDiff.length; li++)
		{
			System.out.println(""+iEPDiff[li]);
		}


		System.out.println("\n\niEPTemplate entering xCorr function:");
		for (int li=0; li<iEPTemplate.length; li++)
		{
			System.out.println(""+iEPTemplate[li]);
		}
*/


		int lengthSig = iEPDiff.length;
		double[] result = new double[(lengthSig*2)-1];

		for(int delay = -lengthSig + 1; delay<lengthSig; delay++)
		{
			//Calculate the numerator
			double sxy = 0;
			for(int i=0; i<lengthSig; i++)
			{
				int j = i + delay;
				if((j < 0) || (j >= lengthSig))  //The series are not wrapped, so the value is ignored
				{
					continue;
				}
				else
				{
					sxy += (iEPDiff[i] * iEPTemplate[j]);
					
					//System.out.println("\n\ndelay = "+delay+", i = "+i+", j = "+j);
					//System.out.println("sxy = "+sxy);
				}
			}

			//Calculate the correlation series at "delay"
			result[delay + lengthSig - 1] = sxy;
		}


/*		System.out.println("\n\nresult of xCorr function:");
		for (int li=0; li<result.length; li++)
		{
			System.out.println(""+result[li]);
		}
*/
		return result;
	}

}

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class EPDiffGetter {
	/* for a given trial number and interval, this function selects and analyses the appropriate files and writes
	 * a plt file containing the averaged ex pattern. It returns the difference the ex patterns in the interval
	 * as a double[].
	 */

	public static double[] getEPDiff(ModelParameters iP, SimParameters iSP, int iTrialNum, int iInterval, boolean iIsTemplate) throws IOException
	{
		//get the filenames of the tones to be used in this interval
		ToneSelecter ts = new ToneSelecter(iP.getFilenameBase());
		String[] lToneNames = ts.selectTones(iTrialNum, iInterval);


		// fft and excit all the tones - each ends up in [name]_ex.out and [name]_fft.out
		for (int li=0; li<lToneNames.length; li++)
		{
			boolean isLinux =false;
			
			if (((File.separator).compareTo("/")) == 0)
			{
				isLinux = true;
			}
			//System.out.println("\n\n\n\nisLinux = "+isLinux);
			
			String[] args = new String[] {"exfft_hz", "-i", ""+lToneNames[li]+".raw", "-n", "8192", "-s", "704", "-f", "48000", "-m", "95", "-F", "8192", "-x"};
			if (isLinux)
			{
				args[0] = "./"+args[0];
			}
			CommandLineWriter.runCommandNoIn(args, ""+lToneNames[li]+"_fft.out");
			
			
			String[] args2;
			if (iSP.getIsSharp())
			{
				args2 = new String[] {"plt_excit_sharp_Hz", "-p", "-q"};

			}
			else
			{
				args2 = new String[] {"plt_excit_Hz", "-p", "-q"};
			}

			if (isLinux)
			{
				args2[0] = "./"+args2[0];
			}
			CommandLineWriter.runCommand(args2, ""+lToneNames[li]+"_fft.out", ""+lToneNames[li]+"_ex.out");
		}


		//read in the freqs and levels for the part of the excitation pattern we're interested in (n-1 to n+1)
		ArrayList<Float> lFreqs = PLTFileReader.readFreqs(iP, lToneNames[0]);
		ArrayList<ArrayList<Float>> lLevels = new ArrayList<ArrayList<Float>>();
		try
		{
			for (int li=0; li<lToneNames.length; li++)
			{
				ArrayList<Float> lPattern;
				lPattern = PLTFileReader.readLevels(iP, lToneNames[li]);

				//System.out.println("\n\n\nPerturbation limit: "+iSP.getInternalNoise());
				// add internal noise here
				Random r = new Random();
				for (int count=0; count<lPattern.size(); count++)
				{	
					//System.out.println("\nlPattern[count] before internal noise: "+lPattern.get(count));
					Float lNoise = new Float(r.nextGaussian()*iSP.getInternalNoise());
					//System.out.println("lNoise: "+lNoise);

					lPattern.set(count, (lPattern.get(count)+lNoise));
					//System.out.println("\nlPattern[count] after internal noise: "+lPattern.get(count));
					//System.out.println(""+lPattern.get(count));
				}
				lLevels.add(lPattern);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}


		//now the 4 ex levels are in lLevels, and the frequencies to which these levels correspond are in lFreqs		
/*
 		System.out.println("\n\nFREQUENCIES");

		for (int li=0; li<lFreqs.size(); li++)
		{
			System.out.println(""+lFreqs.get(li));
		}

		System.out.println("\n\nlLevels.get(0)).size() = "+(lLevels.get(0)).size());

		System.out.println("\n\nEX PATTERN 1");
		for (int li=0; li<(lLevels.get(0)).size(); li++)
		{
			ArrayList<Float> tempLevels = lLevels.get(0);
			System.out.println(""+tempLevels.get(li));
		}

		System.out.println("\n\nEX PATTERN 2");
		for (int li=0; li<(lLevels.get(1)).size(); li++)
		{
			ArrayList<Float> tempLevels = lLevels.get(1);
			System.out.println(""+tempLevels.get(li));
		}

		System.out.println("\n\nEX PATTERN 3");
		for (int li=0; li<(lLevels.get(2)).size(); li++)
		{
			ArrayList<Float> tempLevels = lLevels.get(2);
			System.out.println(""+tempLevels.get(li));
		}

		System.out.println("\n\nEX PATTERN 4");
		for (int li=0; li<(lLevels.get(3)).size(); li++)
		{
			ArrayList<Float> tempLevels = lLevels.get(3);
			System.out.println(""+tempLevels.get(li));
		}
*/

		//average tones 1 and 3, and 2 and 4, calling them lEP1 and lEP2 respectively
		double[] lEP1 = new double[lFreqs.size()];
		double[] lEP2 = new double[lFreqs.size()];
		for (int li=0; li<lFreqs.size(); li++)
		{
			lEP1[li] = (((lLevels.get(0)).get(li))+((lLevels.get(2)).get(li)))/2;
			lEP2[li] = (((lLevels.get(1)).get(li))+((lLevels.get(3)).get(li)))/2;
		}

/*
		System.out.println("\n\nlEP1:");
		for (int li=0; li<lEP1.length; li++)
		{
			System.out.println(""+lEP1[li]);
		}

		System.out.println("\n\nlEP2");
		for (int li=0; li<lEP2.length; li++)
		{
			System.out.println(""+lEP2[li]);
		}
*/

		//System.out.println("\n\nlEPDiff:");

		//calculate the difference between lEP1 and lEP2 - this is lEPdiff
		double[] lEPDiff = new double[lFreqs.size()];;
		for (int li=0; li<lFreqs.size(); li++)
		{
			lEPDiff[li] = lEP1[li] - lEP2[li];
			///System.out.println(""+lEPDiff[li]);
		}

		return lEPDiff;
	}
}

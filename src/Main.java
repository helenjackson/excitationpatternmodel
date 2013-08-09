import java.io.File;
import java.util.ArrayList;

public class Main {

	public static void main (String[] args)
	{
		// these are all the parameters that control how the simulation runs (internal noise, number of trials etc)
		ArrayList<SimParameters> SimConditions = new ArrayList<SimParameters>();

		boolean isSharp = Boolean.parseBoolean(args[0]);
		float intNoise = Float.parseFloat(args[1]);
		int nTrials = Integer.parseInt(args[2]);
		double f0 = Double.parseDouble(args[3]);
		int freqLowest = Integer.parseInt(args[4]);
		int nComps = Integer.parseInt(args[5]);
		double shift = Double.parseDouble(args[6]);
		double ampPert = Double.parseDouble(args[7]);
		double level = Double.parseDouble(args[8]);
		double TENlevel = Double.parseDouble(args[9]);


		// set the simulation and signal parameters for each run wanted
		//(internalNoise, firstTrial, numTrials, client, f0, freqLowest, level, signalDuration, isi, shift, TENlevel, ear, nComps, phase, perturbation)
		SimConditions.add(new SimParameters(isSharp, intNoise, 1, nTrials, new ModelParameters("hmw", f0, freqLowest, level, 0.2, 0.3, shift, TENlevel, "left", nComps, "c", ampPert)));


		try {
			for (int li=0; li<SimConditions.size(); li++)
			{
				SimParameters sp = SimConditions.get(li);
				ModelParameters p = sp.getModelParameters();

				//TEMPLATE		
				System.out.println("\n\nTEMPLATE");
				// make signals with given parameters (end up in Simulation\tfs1_stimuli_n*)
				System.out.println("\nGenerating signals...\n");
				SignalGenerator.generateSignals(p, sp, true);


				// call a WavReader to convert them to.raw files (end up in root)	
				System.out.println("\nConverting signals to .raw...");
				RawWriterOrganiser.convertToRaw(p, sp, true);


				//calculate the template for this run - average of EPdiff1 (HIHI interval diff) from first two trials
				System.out.println("\n\nCalculating template...");
				double[] lEPTemplate = TemplateCalculator.run(p, sp, true);


				//Actual simulation
				System.out.println("\n\nSIMULATION");

				// make signals with given parameters (end up in Simulation\tfs1_stimuli_n*)
				System.out.println("\nGenerating signals...\n");
				SignalGenerator.generateSignals(p, sp, false);


				// call a WavReader to convert them to.raw files (end up in root)	
				System.out.println("\nConverting signals to .raw...");
				RawWriterOrganiser.convertToRaw(p, sp, false);


				// run the simulation
				System.out.println("\nRunning simulation...shift is "+p.getShift()+"F0...");
				Simulator.run(p, sp, lEPTemplate, false);


				//move results file to /results
				String resultsPathPre = PathUtils.combine(p.getSimulationFilepath(),"results_"+p.getFilenameBase()+"_"+sp.getInternalNoise()+"N_"+sp.getNumTrials()+"trials_"+sp.getIsSharp()+"_run2.txt");

				File lResults = new File(resultsPathPre);
				String resultsPathPost = PathUtils.combine((PathUtils.combine(p.getSimulationFilepath(),"results")), "results_"+p.getFilenameBase()+"_"+sp.getInternalNoise()+"N_"+sp.getNumTrials()+"trials_"+sp.getIsSharp()+"_run2.txt");
				boolean fileMoved = lResults.renameTo(new File(resultsPathPost));

				if (!fileMoved)
				{
					System.out.println("\n\nFile "+resultsPathPre+" not moved to "+resultsPathPost);
				}

				// success!
				System.out.println("\n\nEverything has worked!\n\n");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	static public boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					if (! files[i].delete())
					{
						throw new RuntimeException("Failed to delete directory "+files[i].getAbsolutePath());
					}
				}
			}
		}
		return(path.delete());
	}
}

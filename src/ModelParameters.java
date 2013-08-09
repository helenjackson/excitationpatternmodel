import java.io.File;
import java.net.URISyntaxException;

public class ModelParameters {

	private String client;
	private double f0;
	private int freqLowest;
	private double signalLevel;
	private double stimDur;
	private double ISI;
	private double startFreqDev;
	private double TENLevel;
	private String ear;
	private int nComps;
	private String phase;
	private double ampPert;
	private double shift;
	
	public static String exePath = getExePath();
	
	private static String getExePath()  {
		try {
			return new Util().getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static String installPath = new File(exePath).getParent();
	public static String TFS1Filepath = installPath+"\\TFS1"; // where the .exe is
	public static String simulationFolderPath = installPath;

	public ModelParameters(String iClient, double iF0, int iFreqLowest, double iSignalLevel, double iStimDur, double iISI, double iStartFreqDev, double iTENLevel, String iEar, int iNComps, String iPhase, double iAmpPert)
	{
		client = iClient;
		f0 = iF0;
		freqLowest = iFreqLowest;
		signalLevel = iSignalLevel;
		stimDur = iStimDur;
		ISI = iISI;
		startFreqDev = iStartFreqDev;
		TENLevel = iTENLevel;
		ear = iEar;
		nComps = iNComps;
		phase = iPhase;
		ampPert = iAmpPert;
		
		shift = startFreqDev/f0;
	}

	public String getFilenameBase() 
	{ return ""+Util.fmtNum(f0)+"n"+getN()+"_"+nComps+phase+"_"+getShift()+"f0_"+Util.fmtNum(ampPert)+"dB_"+Util.fmtNum(signalLevel)+"SPL_TEN"+ Util.fmtNum(getRelTEN()); 
	}
	
	public String getClient() { return client; }

	public double getF0() { return f0; }

	public int getFreqLowest() { return freqLowest; }
	
	public double getSignalLevel() { return signalLevel; }

	public double getStimDur() { return stimDur; }

	public double getISI() { return ISI; }

	public double getStartFreqDev() { return startFreqDev; }

	public double getTENLevel() { return TENLevel; }

	public String getEar() 
	{
		return ear;
	}
	
	public double getRelTEN()
	{
		return TENLevel - signalLevel;
	}

	public int getNComps() 
	{
		return nComps;
	}

	public String getPhase() 
	{
		return phase;
	}

	public double getAmpPert() 
	{
		return ampPert;
	}

	public int getN() 
	{
		return (int)(Math.round(freqLowest/f0));
	}

	public double getShift() 
	{
		return shift;
	}
	
	public void setShift(double iShift)
	{
		this.shift = iShift;
	}
	
	public String getTFS1Filepath() 
	{
		return TFS1Filepath;
	}
	
	public String getSimulationFilepath() 
	{
		return simulationFolderPath;
	}

}

/*

Parameters p = new Parameters();
p.loadFromFile();

p.getHarmonicRank();

 */

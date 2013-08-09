import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Random;

public class TFS1_class {
	private static final String ComponentPhases = "R"; // only needs to change here
	private static final StimulusType stimulusType = StimulusType.SHAPED; // TODO Check this is a constant
	private static final int NumberOfCoefficients = 512;
	private static final int HeadphonesEmits = 95;
	private static final int CurrentVoltage = 1;
	private static final int Voltage = 1;
	private int NumberOfNoiseSamples = 873000;

	// these appear to be unused TODO check
	// private final int SignalInterval = 1;
	// private double Signal_RMS;
	// private int LengthOfTheSignal;

	private final double UZero = Voltage
			* Math.pow(10, ((-1) * HeadphonesEmits / 20));
	private double[] Noise = new double[NumberOfNoiseSamples];
	private static final double InnerISI = 0.1; // FIXME HAXX FILE MAY CHANGE
												// THIS :S :S :S
	private double[] Coeffs = new double[NumberOfCoefficients];
	private String Ear; // ZARG ZARG ZARG FIXME
	private double NoiseConstant;
	private final static Random random = new Random();;

	public double[] ReadNoiseFile(String NoiseType) {
		byte[] u = new byte[2 * NumberOfNoiseSamples + 44];
		double[] sig = new double[NumberOfNoiseSamples];

		int i = 0;
		int z = 0;
		if (!NoiseType.equals("NONE")) {
			if (NoiseType.equals("TEN_SPL")) {
				try {
					FileInputStream is = new FileInputStream(new File(
							"TEN_SPL.bin"));

					int bytesToRead = u.length;
					int bytesReadSoFar = 0;

					while (bytesReadSoFar < bytesToRead) {
						int bytesReadHere = is.read(u, bytesReadSoFar, u.length
								- bytesReadSoFar);
						bytesReadSoFar += bytesReadHere;
					}

					is.close();
				} catch (Exception e) {
					System.err.println("READING NOISE IST GEBORKEN :(");
					e.printStackTrace();
				}

				NoiseConstant = -15.9;
				// -15.1
			}

			// NOTE: I use this to get _correct behaviour
			// the vb doesn't quite do that, but for all the inputs in _this_
			// noise file it does!
			ByteBuffer buffer = ByteBuffer.wrap(u, 44, u.length - 44);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			ShortBuffer sbuffer = buffer.asShortBuffer();

			for (i = 0; i < sig.length; i++) {
				sig[i] = sbuffer.get();
			}
		} else {
			for (i = 44; i <= u.length - 1; i += 2) {
				sig[z] = 0;
			}
			NoiseConstant = 0;
		}
		return sig;
	}

	public void doSomethingInsane(String iFilename,
			double FundamentalFrequency, int LowestComponentInPassband,
			int NoOfComponentsWithinPassband, double FrequencyDeviation,
			double PerturbationLimit, int SignalLevel, int NoiseLevelPer1kERB,
			int SamplingRate, boolean MaskingInOtherEar, double sig_dur,
			double isi, String NoiseType)

	{
		// HAXXX
		int CentreFrequency = (int) (LowestComponentInPassband + ((NoOfComponentsWithinPassband - 1) / 2)
				* FundamentalFrequency);
		final double sig_dur_total = 4 * sig_dur + 3 * InnerISI;
		int NumberOfComponents = Conversion.Int(CentreFrequency
				/ FundamentalFrequency + 40);
		if (NumberOfComponents * FundamentalFrequency > 19000) {
			NumberOfComponents = (int) Math
					.floor((19000) / FundamentalFrequency);
		}

		Shaping(NoOfComponentsWithinPassband, FundamentalFrequency,
				CentreFrequency, SamplingRate);

		MakeNoneSIStimulus(FrequencyDeviation, SamplingRate, sig_dur_total,
				sig_dur, PerturbationLimit, FundamentalFrequency,
				NumberOfComponents, SignalLevel, CurrentVoltage);

		Noise = ReadNoiseFile(NoiseType);

		int NumberOfPlayedNoiseSamples = (int) (SamplingRate * (2
				* sig_dur_total + isi + 0.6));
		double[] PN = new double[NumberOfPlayedNoiseSamples + 1];
		int DD = 0;
		int i = 0;
		int n_points = 0;
		int StartPointXX = 0;
		double[] temp_sig = null;
		double[] SignalOut = new double[NumberOfPlayedNoiseSamples + 1];

		n_points = (int) (SamplingRate * (2 * sig_dur_total + isi));
		DD = Conversion.Int(SamplingRate * (sig_dur_total + isi));
		double[] signal = new double[n_points + 3];
		double[] SignalToMeasure = new double[Conversion.Int(SamplingRate
				* sig_dur_total) + 1];
		StartPointXX = (int) (0.3 * SamplingRate);

		double[] NonIntervalSignal;
		temp_sig = MakeSIStimulus(FrequencyDeviation, SamplingRate,
				sig_dur_total, sig_dur, PerturbationLimit,
				FundamentalFrequency, NumberOfComponents, SignalLevel,
				CurrentVoltage);
		// double Signal_RMS = RMS(temp_sig);
		for (i = 1; i <= temp_sig.length - 1; i++) {
			signal[i] = temp_sig[i];
		}

		NonIntervalSignal = MakeNoneSIStimulus(0, SamplingRate, sig_dur_total,
				sig_dur, PerturbationLimit, FundamentalFrequency,
				NumberOfComponents, SignalLevel, CurrentVoltage);
		for (i = DD; i <= DD + NonIntervalSignal.length - 1; i++) {
			signal[i] = NonIntervalSignal[i - DD];
		}

		if (!NoiseType.equals("NONE")) {
			SignalOut = GetPlayedNoise((int) SamplingRate, sig_dur_total, isi); // TODO
																				// SamplingRate
																				// haxx???

			SignalOut = Normalize_RMS(SignalOut, LevelToRMS(NoiseLevelPer1kERB
					- NoiseConstant - 20 * Math.log10(CurrentVoltage) - 4));

			for (i = StartPointXX; i <= StartPointXX + signal.length - 1; i++) {
				SignalOut[i] = SignalOut[i] + signal[i - StartPointXX];
			}
			SignalOut = Hanning(SamplingRate, SignalOut);

			if (MaskingInOtherEar == true) {
				// TFSNG.GeneratePinkNoise(NumberOfPlayedNoiseSamples)
				// PN = Hanning(SamplingRate, TFSNG.UUUU)
				// TFSNG.psd(PN, 2 * 32768)
			} else {
				for (i = 1; i <= NumberOfPlayedNoiseSamples; i++) {
					PN[i] = 0;
				}
			}
			SaveSignalToWaveFile(SignalOut, PN, iFilename, SamplingRate);
		} else {
			for (i = 1; i <= Conversion.Int(SamplingRate * sig_dur); i++) {
				SignalToMeasure[i] = signal[i];
			}
			SaveSignalToWaveFile(signal, signal, iFilename, SamplingRate);
		}
		// double LengthOfTheSignal = (SignalOut.length - 1);

	}

	private void Shaping(int NoOfComponentsWithinPassband,
			double FundamentalFrequency, double CentreFrequency,
			int SamplingRate) {
		if (stimulusType == StimulusType.SHAPED) {
			Coeffs = FilterCoefficientCalculations(
					NoOfComponentsWithinPassband, FundamentalFrequency,
					CentreFrequency, SamplingRate);
		}
	}

	// TODO Check all the off by ones etc :(
	public double[] FilterCoefficientCalculations(
			int NoOfComponentsWithinPassband, double FundamentalFrequency,
			double CentreFrequency, int SamplingRate) {
		int i = 0;
		// int N = 2 * NumberOfCoefficients + 1;
		double[] Amplitudes = new double[NumberOfCoefficients + 1];
		double[] REiDftInput = new double[2 * NumberOfCoefficients];
		double[] IMiDftInput = new double[2 * NumberOfCoefficients];
		double[] iDftInput = new double[4 * NumberOfCoefficients - 1];
		double[] iDftOutput;
		double[] rad = new double[NumberOfCoefficients];
		double[] ReHH = new double[NumberOfCoefficients + 1];
		double[] ImHH = new double[NumberOfCoefficients + 1];
		double[] ConjReHH = new double[NumberOfCoefficients];
		double[] ConjImHH = new double[NumberOfCoefficients];
		double[] Coefficients = new double[NumberOfCoefficients + 1];
		double f1 = 0;
		double f2 = 0;
		double f3 = 0;
		double f4 = 0;
		double dt = 0;
		double spacing = 0;
		double ALPHA = 0;
		ALPHA = NoOfComponentsWithinPassband / 2;
		// MsgBox(NoOfComponentsWithinPassband)
		f1 = CentreFrequency + ALPHA * FundamentalFrequency;
		f2 = 2 * f1;
		f3 = CentreFrequency - ALPHA * FundamentalFrequency;
		f4 = 0.5 * f3;

		spacing = (SamplingRate / 2) / NumberOfCoefficients;

		for (i = 0; i < NumberOfCoefficients; i++) {
			if (i * spacing > ALPHA * FundamentalFrequency + CentreFrequency) {
				Amplitudes[i] = -30 * (Log2(i * spacing) - Log2(f1))
						/ (Log2(f2) - Log2(f1));
			}
			if (i * spacing < CentreFrequency - ALPHA * FundamentalFrequency) {
				Amplitudes[i] = 30 * (Log2(i * spacing) - Log2(f3))
						/ (Log2(f3) - Log2(f4));
			}
		}
		for (i = 0; i < NumberOfCoefficients; i++) {
			Amplitudes[i] = Math.pow(10, (Amplitudes[i] / 20));
		}
		dt = 0.5 * (NumberOfCoefficients);
		// obliczam liniow¹ fazê
		for (i = 0; i < NumberOfCoefficients; i++) {
			rad[i] = dt * Math.PI * i / NumberOfCoefficients;
		}
		// rzeczywista i urojona DODATNIA czesc widma
		for (i = 0; i < NumberOfCoefficients; i++) {
			ReHH[i] = Amplitudes[i] * Math.cos(rad[i]);
			ImHH[i] = Amplitudes[i] * Math.sin(rad[i]) * (-1);
		}
		// Obliczam widma sprzezone
		for (i = 0; i < NumberOfCoefficients - 1; i++) {
			ConjReHH[i] = ReHH[NumberOfCoefficients - i];
			ConjImHH[i] = ImHH[NumberOfCoefficients - i] * (-1);
		}
		// £¹czê widma "normalne" i sprzê¿one AMplitudowe
		for (i = 0; i < NumberOfCoefficients; i++) {
			REiDftInput[i] = ReHH[i];
			IMiDftInput[i] = ImHH[i];
		}
		// £¹czê widma "normalne" i sprzê¿one AMplitudowe
		for (i = NumberOfCoefficients; i < 2 * NumberOfCoefficients; i++) {
			REiDftInput[i] = ConjReHH[i - NumberOfCoefficients];
			IMiDftInput[i] = ConjImHH[i - NumberOfCoefficients];
		}
		for (i = 0; i < 2 * NumberOfCoefficients; i++) {
			iDftInput[i] = REiDftInput[i];
		}
		for (i = 2 * NumberOfCoefficients; i <= 4 * NumberOfCoefficients - 2; i++) {
			iDftInput[i] = IMiDftInput[i - 2 * NumberOfCoefficients + 1];
		}
		iDftOutput = iDFT(iDftInput);

		for (i = 1; i <= NumberOfCoefficients; i++) {
			Coefficients[i] = iDftOutput[i]
					* 0.5
					* (1 - Math.cos(2 * Math.PI * i
							/ (NumberOfCoefficients - 1)));
		}
		// SaveBin("c:\z5.bin", Coefficients)
		// SaveBin("c:\z6.bin", Amplitudes)
		return Coefficients;
	}

	public double[] iDFT(double[] x) {
		int NN = x.length - 1;
		int N = NN / 2;
		double[] WeRe = new double[N + 1];
		double[] WeIm = new double[N + 1];
		double[] ResRe = new double[N + 1];
		double[] ResIm = new double[N + 1];
		double[] Wy = new double[2 * N + 1];
		int i = 0;
		int k = 0;
		int p = 0;
		double Theta = 0;
		double CosTh = 0;
		double SinTh = 0;
		for (i = 1; i <= N; i++) {
			WeRe[i] = x[i];
			WeIm[i] = x[i + N];
		}
		// Macierz wejœciowa jest podzielona
		double norm = 1.0 / N;
		for (k = 1; k <= N; k++) {
			for (p = 1; p <= N; p++) {
				Theta = 2 * Math.PI * (k - 1) * (p - 1) / N;
				CosTh = Math.cos(Theta);
				SinTh = Math.sin(Theta);
				ResRe[k] = ResRe[k] + norm
						* (WeRe[p] * CosTh - WeIm[p] * SinTh);
				ResIm[k] = ResIm[k] + norm
						* (WeRe[p] * SinTh + WeIm[p] * CosTh);
			}
		}
		for (i = 1; i <= N; i++) {
			Wy[i] = ResRe[i];
			Wy[i + N] = ResIm[i];
		}
		return Wy;
	}

	private double Log2(double d) {
		return (Math.log(d) / Math.log(2));
	}

	private double[] Hanning(double SamplingRate, double[] xxx) {
		int i = 0;
		int NN = 0;

		int N = 1320;
		NN = xxx.length - 1;
		double[] DDD = new double[NN + 1];
		for (i = 1; i <= N / 2; i++) {
			DDD[i] = 0.5 * (1 - Math.cos((2 * Math.PI * i) / (N - 1)));
		}
		for (i = N / 2 + 1; i <= NN - N / 2; i++) {
			DDD[i] = 1;
		}
		for (i = NN - N / 2 + 1; i <= NN; i++) {
			DDD[i] = 0.5 * (1 - Math
					.cos((2 * Math.PI * (i - NN + N)) / (N - 1)));
		}
		for (i = 1; i <= NN; i++) {
			xxx[i] = xxx[i] * DDD[i];
		}
		return xxx;
	}

	private double[] Normalize_RMS(double[] xxx, double N_RMS) {
		double temp1_d = 0;
		int pp = 0;
		temp1_d = RMS(xxx);
		for (pp = 0; pp <= xxx.length - 1; pp++) {
			xxx[pp] = xxx[pp] * (N_RMS / temp1_d);
		}
		return xxx;
	}

	private double LevelToRMS(double BBB) {
		double QQ = 0;
		QQ = UZero * Math.pow(10, (BBB / 20));
		return QQ;
	}

	public void SaveSignalToWaveFile(double[] SSSS, double[] TTTT,
			String filename, int SamplingRate) {
		int i = 0;

		try {
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(filename));
			BufferedOutputStream w = fos;

			// the Header of the WAV file
			w.write(Convert.ToChar("R"));
			// 0
			w.write(Convert.ToChar("I"));
			// 1
			w.write(Convert.ToChar("F"));
			// 2
			w.write(Convert.ToChar("F"));
			// 3
			w.write(Convert.ToInt32LittleEndian((SSSS.length - 1) * 4 + 44 - 8));
			// 4-7
			w.write(Convert.ToChar("W"));
			// 8
			w.write(Convert.ToChar("A"));
			// 9
			w.write(Convert.ToChar("V"));
			// 10
			w.write(Convert.ToChar("E"));
			// 11
			w.write(Convert.ToChar("f"));
			// 12
			w.write(Convert.ToChar("m"));
			// 13
			w.write(Convert.ToChar("t"));
			// 14
			w.write(Convert.ToChar(" "));
			// 15
			w.write(Convert.ToInt32LittleEndian(16));
			// 16-19 zawsze to sasmo 4 bajty
			w.write(Convert.ToInt16LittleEndian(1));
			w.write(Convert.ToInt16LittleEndian(2));

			w.write(Convert.ToInt32LittleEndian(SamplingRate));
			// 24-27
			w.write(Convert.ToInt32LittleEndian(4 * SamplingRate));
			// 28-31 liczba bitow na sekunde
			w.write(Convert.ToInt16LittleEndian(4));
			// 32-33
			w.write(Convert.ToInt16LittleEndian(16));
			// 34-35
			w.write(Convert.ToChar("d"));
			// 36
			w.write(Convert.ToChar("a"));
			// 37
			w.write(Convert.ToChar("t"));
			// 38
			w.write(Convert.ToChar("a"));
			// 39
			w.write(Convert.ToInt32LittleEndian((SSSS.length - 1) * 4));
			// 40-43 (dl. sygn. w Bytach!!)
			// end of the header***********************
			// now data

			boolean forceBreak = true;
			if (filename.equals("#.wav")) {
				if (forceBreak) {
					throw new IOException(
							"If this code is used, it may have been broken, string== is not the same as in .Net");
				}
				if ("l".equalsIgnoreCase(Ear)) {
					for (i = 0; i <= SSSS.length - 2; i++) {
						w.write(Convert.ToInt32LittleEndian(32767 * SSSS[i]));
						w.write(Convert.ToInt32LittleEndian(32767 * TTTT[i]));
					}
				}
				if ("r".equalsIgnoreCase(Ear)) {
					for (i = 0; i <= SSSS.length - 2; i++) {
						w.write(Convert.ToInt32LittleEndian(32767 * TTTT[i]));
						w.write(Convert.ToInt32LittleEndian(32767 * SSSS[i]));
					}
				}
			} else {
				for (i = 0; i <= SSSS.length - 2; i++) {
					w.write(Convert.ToInt16LittleEndian(32767 * SSSS[i]));
					w.write(Convert.ToInt16LittleEndian(32767 * TTTT[i]));
				}
			}
			w.close();
		} catch (IOException e) {
			System.err.println("ALLES IST GEBORKEN WRITING DIE FILE :(((");
			e.printStackTrace();
		}

	}

	private double[] GetPlayedNoise(int SamplingRate, double sig_dur_total,
			double isi) {
		int NumberOfPlayedNoiseSamples = 0;
		NumberOfPlayedNoiseSamples = Conversion.Int(SamplingRate
				* (2 * sig_dur_total + isi + 0.6));
		double[] PlayedNoise = new double[NumberOfPlayedNoiseSamples + 1];
		int StartPoint = 0;
		int i = 0;
		StartPoint = Conversion.Int(random.nextDouble()
				* (NumberOfNoiseSamples - NumberOfPlayedNoiseSamples - 100));
		for (i = 1; i <= NumberOfPlayedNoiseSamples; i++) {
			PlayedNoise[i] = Noise[StartPoint + i];
		}
		return PlayedNoise;
	}

	private double[] MakeNoneSIStimulus(double Deviation, double SamplingRate,
			double sig_dur_total, double sig_dur, double PerturbationLimit,
			double FundamentalFrequency, int NumberOfComponents,
			int SignalLevel, double CurrentVoltage) {
		int npts = Conversion.Int(SamplingRate * sig_dur_total);
		int Snpts = Conversion.Int(sig_dur * SamplingRate);
		double[] PartOfSig;
		int SnptsX = Conversion.Int(SamplingRate * (sig_dur + InnerISI));
		double[] PartXOfSig = new double[SnptsX + 1];
		int z = Conversion.Int(SamplingRate * InnerISI);
		double[] sig = new double[npts + 1];

		int i = 0;
		int j = 0;
		PartOfSig = SinglePieceGeneration(0, PerturbationLimit, SamplingRate,
				FundamentalFrequency, sig_dur, NumberOfComponents, SignalLevel,
				CurrentVoltage);
		for (i = 1; i <= Snpts; i++) {
			sig[i] = PartOfSig[i];
		}

		for (j = 1; j <= 3; j++) {
			PartOfSig = SinglePieceGeneration(0, PerturbationLimit,
					SamplingRate, FundamentalFrequency, sig_dur,
					NumberOfComponents, SignalLevel, CurrentVoltage);
			for (i = 1; i <= Snpts; i++) {
				PartXOfSig[i + z] = PartOfSig[i];
			}
			for (i = 1 * j; i <= SnptsX; i++) {
				sig[Snpts + i + (j - 1) * SnptsX - 1] = PartXOfSig[i];
			}
		}
		return sig;
	}

	private double[] SinglePieceGeneration(double Deviation, double PetLim,
			double SamplingRate, double FundamentalFrequency, double sig_dur,
			int NumberOfComponents, double SignalLevel, double CurrentVoltage) {
		int i = 0;
		int comp = 0;
		int npts = 0;
		double rand_temp = 0;
		double MM = 0;
		double FF = 0;
		double Sdev = 0;
		double VV = 0;
		double lPeturb = 0;

		MM = Convert.ToDouble(SamplingRate);
		FF = Convert.ToDouble(FundamentalFrequency);
		Sdev = Convert.ToDouble(Deviation);
		npts = Conversion.Int(sig_dur * MM);
		double[] temp_sig = new double[npts + 1];

		for (comp = 1; comp <= NumberOfComponents; comp++) {
			Random r = random;
			lPeturb = ((PetLim * r.nextDouble()) * 2) - PetLim;

			if (ComponentPhases == "R" | ComponentPhases == "r") {
				rand_temp = Math.PI * 2 * r.nextDouble();
			}
			/*
			 * if (ComponentPhases == "C" | ComponentPhases == "c") { rand_temp
			 * = Math.PI / 2; }
			 */
			for (i = 1; i <= npts; i++) {
				VV = i / MM;

				// PetLim is input by user to be 0, 3 or 5 dB for each component
				// Rnd() returns x between 0 and 1, so multiply by PetLim to get
				// x between 0 and PetLim
				// Then multiply by 2 to get x between 0 and 2*PetLim
				// Then minus PetLim to get x between -Petlim and +Petlim

				// To increase level by x dB, multiply full-scale signal by
				// 10^(x/20)
				temp_sig[i] = temp_sig[i]
						+ (Math.pow(10, (lPeturb / 20)))
						* (Math.sin(2 * Math.PI * (FF * comp + Sdev) * VV
								+ rand_temp));
			}
		}
		Hanning(SamplingRate, temp_sig);
		if (stimulusType == StimulusType.SHAPED) {
			temp_sig = Filtering(temp_sig);
			Hanning(SamplingRate, temp_sig);
		}
		Hanning(SamplingRate, temp_sig);
		Normalize_RMS(temp_sig,
				LevelToRMS(SignalLevel - 20 * Math.log10(CurrentVoltage) - 4));
		return temp_sig;
	}

	private double[] Filtering(double[] sig) {
		double[] out = new double[sig.length];
		double[] temp = new double[sig.length - 1 + (Coeffs.length - 1) + 1];
		int i = 0;
		int j = 0;
		for (i = 1; i <= sig.length - 1; i++) {
			temp[i + Coeffs.length - 1] = sig[i];
		}
		for (j = 1; j <= sig.length - 1; j++) {
			for (i = 1; i <= Coeffs.length - 1; i++) {
				out[j] = out[j] + temp[-i + j + Coeffs.length - 1] * Coeffs[i];
			}
		}
		return out;
	}

	private double RMS(double[] xxx) {
		double temp1_d = 0;
		int pp = 0;
		temp1_d = 0;
		for (pp = 0; pp <= xxx.length - 1; pp++) {
			temp1_d = temp1_d + xxx[pp] * xxx[pp];
		}
		temp1_d = Math.sqrt(temp1_d / (xxx.length - 1));
		return temp1_d;
	}

	private double[] MakeSIStimulus(double Deviation, double SamplingRate,
			double sig_dur_total, double sig_dur, double PerturbationLimit,
			double FundamentalFrequency, int NumberOfComponents,
			int SignalLevel, double CurrentVoltage) {
		int npts = Conversion.Int(SamplingRate * sig_dur_total);
		int Snpts = Conversion.Int(sig_dur * SamplingRate);
		double[] PartOfSig = new double[Snpts + 1];
		int SnptsX = Conversion.Int(SamplingRate * (sig_dur + InnerISI));
		double[] PartXOfSig = new double[SnptsX + 1];
		int z = Conversion.Int(SamplingRate * InnerISI);
		double[] sig = new double[npts + 1];
		// ca³y sygnal
		int i = 0;
		int j = 0;

		PartOfSig = SinglePieceGeneration(0, PerturbationLimit, SamplingRate,
				FundamentalFrequency, sig_dur, NumberOfComponents, SignalLevel,
				CurrentVoltage);
		for (i = 1; i <= Snpts; i++) {
			sig[i] = PartOfSig[i];
		}

		for (j = 1; j <= 3; j++) {
			if (j == 2) {
				PartOfSig = SinglePieceGeneration(0, PerturbationLimit,
						SamplingRate, FundamentalFrequency, sig_dur,
						NumberOfComponents, SignalLevel, CurrentVoltage);
			} else {
				PartOfSig = SinglePieceGeneration(Deviation, PerturbationLimit,
						SamplingRate, FundamentalFrequency, sig_dur,
						NumberOfComponents, SignalLevel, CurrentVoltage);
			}
			for (i = 1; i <= Snpts; i++) {
				PartXOfSig[i + z] = PartOfSig[i];
			}
			for (i = 1 * j; i <= SnptsX; i++) {
				sig[Snpts + i + (j - 1) * SnptsX - 1] = PartXOfSig[i];
			}
		}
		return sig;
	}
}

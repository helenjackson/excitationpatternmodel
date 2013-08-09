import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class RawWriter {

	public static void writeSection(String iPathIn, String iPathOut, int iFirstSampleWanted, int iNumSamplesWanted) throws IOException, Exception
	{

			File inFile = new File(iPathIn);
			File outFile = new File(iPathOut);
			
			if ((outFile.exists() == false) || (outFile.length() < 1))
			{
				FileOutputStream fileOutStream = new FileOutputStream(outFile, false);

				AudioInputStream audioInputStream = 
					AudioSystem.getAudioInputStream(inFile);
				int bytesPerFrame = 
					audioInputStream.getFormat().getFrameSize();

				int sampleCount = 0; //to locate us in the file
				
				
				int bytesToSkip = (iFirstSampleWanted-1) * bytesPerFrame;
				int bytesSkipped =  0;
				while ((bytesSkipped += audioInputStream.skip(bytesToSkip - bytesSkipped)) < bytesToSkip)
				{
					
				}
				sampleCount += iFirstSampleWanted -1;
				
				
				//System.out.println("bytesPerFrame = "+bytesPerFrame);
				// THERE ARE 4 BYTES PER FRAME

				// Set a buffer size of 1 frame (4 bytes)
				int bufferSize = 1 * bytesPerFrame; 
				byte[] bufferContents = new byte[bufferSize];
				int numBytesRead = 0;
				

				// Read bufferSize bytes from the file.
				while ((numBytesRead = 
					audioInputStream.read(bufferContents)) != -1) 
				{	
					if (numBytesRead != bytesPerFrame)
					{
						throw new AssertionError("Didn't read enough data, only "+numBytesRead);
					}
					
					sampleCount++; // increment counter

					if (sampleCount < iFirstSampleWanted)
					{
						throw new AssertionError("Should have skipped this");
					}
					
					// If we're in the part of the signal we want
					if ((sampleCount < iFirstSampleWanted+iNumSamplesWanted))
					{
						//System.out.println("Sample count = "+sampleCount);

						/*
					print out contents of buffer
					System.out.println("bufferContents[0] = "+bufferContents[0]);			
					System.out.println("bufferContents[1] = "+bufferContents[1]);			
					System.out.println("bufferContents[2] = "+bufferContents[2]);			
					System.out.println("bufferContents[3] = "+bufferContents[3]);			
						 */

						// write the desired part of bufferContents array to outFile via fileOutStream
						// need alternate pairs of bytes starting with iFirstSampleWanted
						// bufferContents contains 4 bytes (duhhhh)
						byte[] bytesToWrite = new byte[2];
						bytesToWrite[0] = bufferContents[0];
						bytesToWrite[1] = bufferContents[1];

						fileOutStream.write(bytesToWrite);
					}
					else
					{
						break;
					}
					
				}
				fileOutStream.close();//close stream
			}
			else
			{
				//System.out.println(".raw file already exists!");
			}	
	}
}

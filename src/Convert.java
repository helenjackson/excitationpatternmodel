

import java.util.Arrays;

public class Convert
{

	public static double ToDouble(double inDouble)
	{
		return inDouble;
	}

	public static byte[] ToChar(String string)
	{
		return string.getBytes();
	}

	public static int ToInt32(Integer i)
	{
		assert (Integer.bitCount(i) < 32);

		return i;
	}

	public static byte[] ToInt32LittleEndian(double d)
	{
		int zorf = (int) d;

		byte[] zjasdlfhsdf = (new byte[] { (byte) ((zorf & 0xff) >>> 0), (byte) ((zorf & 0xff00) >>> 8), (byte) ((zorf & 0xff0000) >>> 16),
				(byte) ((zorf & 0xff000000) >>> 24) });

		if (zjasdlfhsdf.length != 4) {
			zjasdlfhsdf = "zzzzzzzzzzzzzzzzzzzzzz".getBytes();
		}

		return zjasdlfhsdf;
	}

	public static byte[] ToInt16LittleEndian(double d)
	{
		byte[] toInt32LittleEndian = ToInt32LittleEndian(d);
		return Arrays.copyOf(toInt32LittleEndian, 2);
	}

}

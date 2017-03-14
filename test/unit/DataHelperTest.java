package unit;

import org.junit.Test;
import webchatinterface.helpers.DataHelper;
import static org.junit.Assert.*;

public class DataHelperTest
{
	@Test
	public void testFormatBytes()
	{
		assertEquals("Incorrect String returned", "0.00B", DataHelper.formatBytes(0, 2));
		assertEquals("Incorrect String returned", "512.00B", DataHelper.formatBytes(512, 2));
		assertEquals("Incorrect String returned", "1023.00B", DataHelper.formatBytes(1024-1, 2));
		assertEquals("Incorrect String returned", "1.00kB", DataHelper.formatBytes(1024, 2));
		assertEquals("Incorrect String returned", "1023.99kB", DataHelper.formatBytes(1024*1024-1, 2));
		assertEquals("Incorrect String returned", "1.00MB", DataHelper.formatBytes(1024*1024, 2));
		assertEquals("Incorrect String returned", "1023.99MB", DataHelper.formatBytes(1024*1024*1024-1, 2));
		assertEquals("Incorrect String returned", "1.00GB", DataHelper.formatBytes(1024*1024*1024, 2));

		assertEquals("Incorrect String returned", "512B", DataHelper.formatBytes(512, 0));
		assertEquals("Incorrect String returned", "512.0B", DataHelper.formatBytes(512, 1));
		assertEquals("Incorrect String returned", "512B", DataHelper.formatBytes(512, -1));
	}
}
package net.chiisana.builddit;

import org.junit.Assert;
import org.junit.Test;

import net.chiisana.builddit.generator.PlotHelper;

public class ModulusTest {
	@Test
	public void NegativeModulusTest() {
		int size = 21;
		Assert.assertEquals(0, 0 % size);
		Assert.assertEquals(20, PlotHelper.modulus(-1, size));
		Assert.assertEquals(19, PlotHelper.modulus(-2, size));
	}
}

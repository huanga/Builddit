package net.chiisana.builddit.task;

import net.chiisana.builddit.Builddit;

public class DelayedInitTask implements Runnable {
	public void run() {
		Builddit.getInstance().initDelayed();
	}
}

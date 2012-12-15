package org.jenkinsci.plugins.blink1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BallColor;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import org.kohsuke.stapler.DataBoundConstructor;

public class Blink1Notifier extends Notifier {
	@DataBoundConstructor
	public Blink1Notifier() {
	}

	private static final String COLOR_CODE_BLUE = "0000FF";
	private static final String COLOR_CODE_YELLOW = "FFFF00";
	private static final String COLOR_CODE_RED = "FF0000";
	
	private static final double DELAY = 0.5;

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
		listener.getLogger().println("Blink1Notifier.perform");
		BallColor color = build.getResult().color;
		String colorCode = "FFFFFF";
		if (BallColor.BLUE==color)
			colorCode = COLOR_CODE_BLUE;
		else if (BallColor.YELLOW==color)
			colorCode = COLOR_CODE_YELLOW;
		else if (BallColor.RED==color)
			colorCode = COLOR_CODE_RED;
		blink(listener, colorCode);
		return true;
	}

	private void blink(BuildListener listener, String colorCode) {
		String urlStr = "http://localhost:8934/blink1/fadeToRGB?rgb=%23" + colorCode + "&time=" + DELAY;
		URL url;
		try {
			URLConnection conn;
			url = new URL(urlStr);
			conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				listener.getLogger().println(inputLine);
			}
			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl)super.getDescriptor();
	}
	
	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public DescriptorImpl() {
			load();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		public String getDisplayName() {
			return "Blink1Notifier";
		}
	}
}
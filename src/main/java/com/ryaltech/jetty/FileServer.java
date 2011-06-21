package com.ryaltech.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.FileFilter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.regex.Pattern;

public class FileServer {
	private int port;
	private String rootDir;

	public FileServer(int port, String rootDir) {
		super();
		this.port = port;
		this.rootDir = rootDir;
	}

	public void start() throws Exception {

		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		server.addConnector(connector);

		System.out.println("Web root is: "
				+ new File(rootDir).getAbsolutePath());
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(false);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });

		resource_handler.setResourceBase(rootDir);

		HandlerList handlers = new HandlerList();

		handlers.setHandlers(new Handler[] { createWebContexts(rootDir),
				/*resource_handler,*/ new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		
		server.join();

	}

	public static void main(String[] args) throws Exception {
		int port;
		String webRoot = ".";
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception ex) {
			help();
			System.exit(-1);
			return;
		}
		try {
			webRoot = args[1];
		} catch (Exception ex) {
		}
		new FileServer(port, webRoot).start();

	}

	private Handler createWebContexts(String rootDir) {

		File rootDirFile = new File(rootDir);
		List<File> warFiles = enumerateWars(rootDirFile);
		URI baseDirURI = rootDirFile.toURI();
		
		HandlerList hl = new HandlerList();
		if (warFiles != null) {
			for (File warFile : warFiles) {
				String mountpoint = "/"
					+ baseDirURI.relativize(warFile.toURI())
					.getPath();
				WebAppContext ctx = new WebAppContext(
						warFile.getAbsolutePath(),mountpoint);
				hl.addHandler(ctx);
				System.out.println(String.format("War: %s can be accessed under %s",warFile.getAbsolutePath(), mountpoint));
			}
		}
		return hl;
	}

	private List<File> enumerateWars(File rootDir) {

		List<File> allWarFiles = new ArrayList<File>();
		File[] warFiles = rootDir.listFiles(new FileFilter() {
			final Pattern pattern = Pattern.compile(".*\\.war");

			public boolean accept(File pathname) {
				return (!pathname.isDirectory() && pattern.matcher(
						pathname.getName()).matches());
			}
		});
		if (allWarFiles != null)
			allWarFiles.addAll(Arrays.asList(warFiles));
		File[] subDirs = rootDir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		if (subDirs != null) {
			for (File subDir : subDirs) {
				allWarFiles.addAll(enumerateWars(subDir));

			}
		}
		return allWarFiles;

	}

	private static void help() {
		System.out
				.println("Usage: java -jar jetty-static-web[version].jar <web server port> [web content dir(defaults to work dir)]");

	}
}
package com.ryaltech.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import java.io.File;

public class FileServer
{
    public static void main(String[] args) throws Exception
    {
    	int port;
		String webRoot = ".";
    	try{
    		port = Integer.parseInt(args[0]);
    	}catch(Exception ex){
    		help();
    		System.exit(-1);
    		return;
    	}
		try{
    		webRoot = args[1];
    	}catch(Exception ex){		
		}
    		
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);
 
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(false);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
 
        resource_handler.setResourceBase(webRoot);
 
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        server.setHandler(handlers);
 
        server.start();
		System.out.println("Web root is: "+new File(webRoot).getAbsolutePath());
        server.join();
    }

	private static void help() {
		System.out.println("Usage: java -jar jetty-static-web[version].jar <web server port> [web content dir(defaults to work dir)]");

		
	}
}
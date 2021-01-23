package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

@Slf4j
public class ServletServer {

    public static void main(String[] args) {
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);
            tomcat.getConnector();

            Context context = tomcat.addWebapp("", new File("servlet/src/main/webapp").getAbsolutePath());
            WebResourceRoot resources = new StandardRoot(context);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File("servlet/build/classes").getAbsolutePath(), "/"));
            context.setResources(resources);

            tomcat.start();
            tomcat.getServer().wait();
        } catch (Throwable e) {
            log.error("Start server failed: {}", e.getMessage(), e);
        }
    }
}

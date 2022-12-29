package app.server;

import app.server.http.HttpServer;

public class Tomcat {
    public static void main(String[] args) throws Exception {
        HttpServer.start();
    }
}

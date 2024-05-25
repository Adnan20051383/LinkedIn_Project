package org.example;

import com.adnan.server.dataAccess.UserDataAccess;
import com.adnan.server.handlers.*;
import com.adnan.server.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/users", new UserHandler());
        server.createContext("/bios", new BioHandler());
        server.createContext("/posts", new PostHandler());
        server.createContext("/session", new SessionHandler());
        server.createContext("/educations", new EducationHandler());
        server.createContext("/contacts", new ContactsHandler());
        server.createContext("/skills", new SkillHandler());
        server.start();
    }

    }
package com.varun.threading.performanceOptimization.throughput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This is a HTTP server that acts as a search engine.
 * <p>
 * We will fire something like : localhost:8000/search="word"
 * <p>
 * it will return the total count of that word in the text file : body:8667
 */
public class ThroughputHttpServer {

    private static final String INPUT_FILE = "./resources/war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 2;

    public static void main(String[] args) throws IOException {

        String bookStr = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(bookStr);

    }

    public static void startServer(String bookText) throws IOException {
        // we keep the queue of the http server as ZER0.
        // So that all requests are queued in the task queue of thread pool
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/search", new WordCountHandler(bookText));
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {
        private String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String wordToSearch = keyValue[1];

            if (!action.equals("word")) {
                httpExchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(wordToSearch);
            byte[] response = Long.toString(count).getBytes();
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response);
            // this will dispatch the response
            os.close();
        }

        private long countWord(String wordToSearch) {
            int count = 0;
            int index = 0;

            while (index >= 0) {
                index = text.indexOf(wordToSearch, index);
                if (index >= 0) {
                    count++;
                    index++;
                }
            }
            return count;
        }
    }

}

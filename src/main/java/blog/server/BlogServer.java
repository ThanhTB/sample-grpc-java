package blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 50053;

        MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:admin123@cluster0.0supo.mongodb.net/movie_db?retryWrites=true&w=majority");

        Server server = ServerBuilder
                .forPort(port)
                .addService(new BlogServiceImpl(mongoClient))
                .build();

        server.start();
        System.out.println("Server started");
        System.out.println("Server listening on port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Server stopped");
        }));

        server.awaitTermination();
    }
}

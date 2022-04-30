package greeting.client;

import com.proto.greeting.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClientTsl {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Needed one argument to work");
            return;
        }

        ChannelCredentials credentials = TlsChannelCredentials.newBuilder().trustManager(
                new File("ssl/ca.crt")
        ).build();

        ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 50051, credentials).build();

        switch (args[0]) {
            case "greet":
                doGreet(channel);
            default:
                System.out.println("Keyword invalid: " + args[0]);
        }


        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Thanh").build());

        System.out.println("Greeting: " + response.getResult());
    }
}

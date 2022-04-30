package greeting.client;

import com.proto.greeting.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Needed one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet":
                doGreet(channel);
                break;
            case "greet_many_times":
                doGreetManyTimes(channel);
                break;
            case "long_greet":
                doLongGreet(channel);
                break;
            case "greet_every_one":
                doGreetEveryOne(channel);
                break;
            case "sqrt":
                doSqrt(channel);
                break;
            case "deadline":
                doGreetWithDeadline(channel);
                break;
            default:
                System.out.println("Keyword invalid: " + args[0]);
        }


        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void doGreetWithDeadline(ManagedChannel channel) {
        System.out.println("Enter doDeadline");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        GreetingResponse response = stub.withDeadline(Deadline.after(3, TimeUnit.SECONDS)).greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Thanh").build());
        System.out.println("Greeting within deadline: " + response.getResult());

        try {
            response = stub.withDeadline(Deadline.after(100, TimeUnit.MICROSECONDS)).greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Thanh").build());
            System.out.println("Greeting deadline exceeded: " + response.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded");
            } else {
                System.out.println("Got an exception in greetWithDeadline");
                e.printStackTrace();
            }
        }
    }

    private static void doSqrt(ManagedChannel channel) {
        System.out.println("Enter doSqrt");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        try {
            SqrtResponse response = stub.sqrt(SqrtRequest.newBuilder().setNumber(-1).build());
            System.out.println("Sqrt: " + response.getResult());

        } catch (Exception e) {
            System.out.println("Got an Exception for sqrt");
            e.printStackTrace();
        }
    }

    private static void doGreetEveryOne(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doGreetEveryOne");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetingRequest> streams = stub.longGreet(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse response) {
                System.out.println(response.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList("Thanh", "Thanh 1", "Thanh 2").forEach(name -> streams.onNext(GreetingRequest.newBuilder().setFirstName(name).build()));
        streams.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doLongGreet(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doLongGreet");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        List<String> names = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        Collections.addAll(names, "Thanh", "Thanh 1", "Thanh 2");

        StreamObserver<GreetingRequest> streams = stub.longGreet(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse response) {
                System.out.println(response.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for (String name: names) {
            streams.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        }

        streams.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doGreetManyTimes(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        stub
            .greetManyTimes(GreetingRequest.newBuilder().setFirstName("Thanh").build())
            .forEachRemaining(response -> {
                System.out.println(response.getResult());
            });

    }

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Thanh").build());

        System.out.println("Greeting: " + response.getResult());
    }
}

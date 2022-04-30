package calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Needed one argument to work");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();

        switch (args[0]) {
            case "sum":
                doSum(channel);
                break;
            case "primes":
                doPrimes(channel);
                break;
            case "avg":
                doAvg(channel);
                break;
            case "max":
                doMax(channel);
                break;
            default:
                System.out.println("Keyword invalid: " + args[0]);
        }


        System.out.println("Shutting down");
        channel.shutdown();


    }

    private static void doMax(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doMax");
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<MaxRequest> streams = stub.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse response) {
                System.out.println(response.getMax());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList(1, 12, 3, 64, 5, 6, 7, 8, 19, 10).forEach(number -> streams.onNext(MaxRequest.newBuilder().setNumber(number).build()));
        streams.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doAvg(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doPrimes");
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AvgRequest> streams = stub.avg(new StreamObserver<AvgResponse>() {
            @Override
            public void onNext(AvgResponse value) {
                System.out.println("Avg: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).forEach(number -> streams.onNext(AvgRequest.newBuilder().setNumber(number).build()));
        streams.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doPrimes(ManagedChannel channel) {
        System.out.println("Enter doPrimes");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.primes(PrimeRequest.newBuilder().setNumber(100).build()).forEachRemaining(response -> {
            System.out.println(response);
        });
    }

    private static void doSum(ManagedChannel channel) {
        System.out.println("Enter doSum");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse sum = stub.sum(SumRequest.newBuilder().setFirstNumber(10).setSecondNumber(20).build());
        System.out.println("Calculator: " + sum.getResult());
    }
}

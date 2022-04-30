package greeting.server;

import com.proto.greeting.*;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver
                .onNext(GreetingResponse
                                .newBuilder()
                                .setResult("Hello " + request.getFirstName())
                                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        GreetingResponse response = GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build();

        for (int index = 0; index < 10; index++) {
            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> longGreet(StreamObserver<GreetingResponse> responseObserver) {
        StringBuilder sb = new StringBuilder();

        return new StreamObserver<GreetingRequest>() {
            @Override
            public void onNext(GreetingRequest request) {
                sb.append("Hello, ");
                sb.append(request.getFirstName());
                sb.append("!\n");
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult(sb.toString()).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetingRequest> greetEveryOne(StreamObserver<GreetingResponse> responseObserver) {
        return new StreamObserver<GreetingRequest>() {
            @Override
            public void onNext(GreetingRequest request) {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello, " + request.getFirstName() + "!\n").build());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void sqrt(SqrtRequest request, StreamObserver<SqrtResponse> responseObserver) {
        int number = request.getNumber();
        if (number < 0) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("The number being sent cannot be negative")
                            .augmentDescription("Number: " + number)
                            .asRuntimeException()
            );

            return;
        }

        responseObserver.onNext(SqrtResponse.newBuilder().setResult(Math.sqrt(number)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void greetWithDeadline(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        Context context = Context.current();

        try {
            for (int index = 0; index < 3; index++) {
                if (context.isCancelled()) {
                    return;
                }

                Thread.sleep(100);
            }

            responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello, " + request.getFirstName()).build());
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            responseObserver.onError(e);
        }
    }
}

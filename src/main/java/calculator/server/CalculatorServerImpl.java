package calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        responseObserver.onNext(SumResponse.newBuilder().setResult(request.getFirstNumber() + request.getSecondNumber()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void primes(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver) {
        int number = request.getNumber();
        int divisor = 2;

        while (number > 1) {
            if (number % divisor == 0) {
                number /= divisor;
                responseObserver.onNext(PrimeResponse.newBuilder().setPrimeFactor(number).build());
            } else {
                ++divisor;
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AvgRequest> avg(StreamObserver<AvgResponse> responseObserver) {
        return new StreamObserver<AvgRequest>() {
            int sum = 0;
            int count = 0;

            @Override
            public void onNext(AvgRequest request) {
                sum += request.getNumber();
                ++count;
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(AvgResponse.newBuilder().setResult((double) sum / count).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<MaxRequest>() {
            int max = 0;
            @Override
            public void onNext(MaxRequest request) {
                if (request.getNumber() > max) {
                    max = request.getNumber();
                    responseObserver.onNext(MaxResponse.newBuilder().setMax(max).build());
                }
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
}

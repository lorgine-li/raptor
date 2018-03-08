package com.ppdai.framework.raptor.benchmark.client;

import com.ppdai.framework.raptor.filter.refer.ReferAccessLogFilter;
import com.ppdai.framework.raptor.filter.refer.ReferMetricsFilter;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.serialize.ProtobufBinSerialization;
import com.ppdai.framework.raptor.serialize.ProtobufJsonSerialization;
import com.ppdai.framework.raptor.serialize.Serialization;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ClientBenchmark {

    private Serialization jsonSerialization;
    private Serialization binSerialization;

    private Simple jsonProxy;
    private Simple binProxy;

    private byte[] jsonData;
    private byte[] binData;

    @Setup
    public void setup() {
        Helloworld.HelloReply helloReply = Helloworld.HelloReply.newBuilder().setMessage("Hello ppdai").build();
        String url = "http://localhost:8080";

        jsonSerialization = new ProtobufJsonSerialization();
        jsonData = jsonSerialization.serialize(helloReply);
        MockClient jsonClient = MockClient.builder()
                .content(jsonData).build();
        jsonProxy = ReferProxyBuilder.newBuilder()
                .addFilter(new ReferMetricsFilter())
                .addFilter(new ReferAccessLogFilter())
                .serialization(jsonSerialization)
                .client(jsonClient)
                .build(Simple.class, URL.valueOf(url));

        //
        binSerialization = new ProtobufBinSerialization();
        binData = binSerialization.serialize(helloReply);
        MockClient binClient = MockClient.builder()
                .content(binData).build();
        binProxy = ReferProxyBuilder.newBuilder()
                .addFilter(new ReferMetricsFilter())
                .addFilter(new ReferAccessLogFilter())
                .serialization(binSerialization)
                .client(binClient)
                .build(Simple.class, URL.valueOf(url));
    }

    @Benchmark
    public void testJson(Blackhole bh) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = jsonProxy.sayHello(helloRequest);
        bh.consume(helloReply);
    }


    @Benchmark
    public void testBin(Blackhole bh) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = binProxy.sayHello(helloRequest);
        bh.consume(helloReply);
    }

    @Benchmark
    public void jsonSerialization(Blackhole bh) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        jsonSerialization.serialize(helloRequest);
        Helloworld.HelloReply helloReply = jsonSerialization.deserialize(jsonData, Helloworld.HelloReply.class);
        bh.consume(helloReply);
    }

    @Benchmark
    public void binSerialization(Blackhole bh) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        binSerialization.serialize(helloRequest);
        Helloworld.HelloReply helloReply = binSerialization.deserialize(binData, Helloworld.HelloReply.class);
        bh.consume(helloReply);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ClientBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

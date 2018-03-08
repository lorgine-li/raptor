package com.ppdai.framework.raptor.benchmark.client;

import com.ppdai.framework.raptor.filter.refer.ReferAccessLogFilter;
import com.ppdai.framework.raptor.filter.refer.ReferMetricsFilter;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.URL;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ClientBenchmark {

    private Simple proxy;

    @Setup
    public void setup() {
        String url = "http://localhost:8080";
        MockClient client = new MockClient();
        String responseMessage = "{\n" +
                "  \"message\": \"Hello ppdai. 965476759\"\n" +
                "}";
        client.setContent(responseMessage.getBytes());
        client.init();

        proxy = ReferProxyBuilder.newBuilder()
                .addFilter(new ReferMetricsFilter())
                .addFilter(new ReferAccessLogFilter())
                .client(client)
                .build(Simple.class, URL.valueOf(url));
    }

    @Benchmark
    public void testClient() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
        System.out.println(helloReply);
    }

    public static void main(String[] args) {
        ClientBenchmark benchmark = new ClientBenchmark();
        benchmark.setup();
        benchmark.testClient();
    }
}

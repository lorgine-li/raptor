package com.ppdai.framework.raptor.benchmark.client;

import com.ppdai.framework.raptor.refer.client.AbstractHttpClient;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.*;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockClient extends AbstractHttpClient {

    private ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
    private StatusLine statusLine = new BasicStatusLine(pv, 200, "OK");

    @Getter
    @Setter
    private ContentType responseContentType = ContentType.APPLICATION_JSON;

    @Getter
    @Setter
    private byte[] content = new byte[0];

    @Getter
    @Setter
    private List<Header> headers = new ArrayList<>();


    public MockClient() {
        headers.add(new BasicHeader("date", LocalDate.now().toString()));
        headers.add(new BasicHeader("transfer-encoding", "chunked"));
        headers.add(new BasicHeader("x-host-server", "127.0.0.1"));
    }

    @Override
    protected HttpResponse doSendRequest(HttpPost httpPost, URL serviceUrl) throws IOException {
        HttpResponse httpResponse = new BasicHttpResponse(statusLine);
        HttpEntity entity = EntityBuilder.create()
                .setContentType(ContentType.APPLICATION_JSON).setBinary(content).build();
        httpResponse.setEntity(entity);

        return httpResponse;
    }

    @Override
    public void destroy() {

    }
}

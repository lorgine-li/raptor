package com.ppdai.framework.raptor.benchmark.client;

import com.ppdai.framework.raptor.refer.client.AbstractHttpClient;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;

@Builder
@AllArgsConstructor
public class MockClient extends AbstractHttpClient {

    private byte[] content;

    @Override
    protected HttpResponse doSendRequest(HttpPost httpPost, URL serviceUrl) throws IOException {
        ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
        StatusLine statusLine = new BasicStatusLine(pv, 200, "OK");
        HttpResponse httpResponse = new BasicHttpResponse(statusLine);
        HttpEntity entity = EntityBuilder.create().setBinary(content).build();
        httpResponse.setEntity(entity);

        return httpResponse;
    }

    @Override
    public void destroy() {

    }
}

package com.jahnelgroup.cartographer.core.http.apache;

import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class ApacheHttpClient implements ElasticsearchHttpClient {

    @Override
    public HttpResponse exchange(HttpRequest elasticSearchHttpRequest) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpRequestBase httpRequest = createHttpRequest(URI.create(elasticSearchHttpRequest.url()),
                elasticSearchHttpRequest.method());

        // Content (if it supports a body)
        if( httpRequest instanceof HttpEntityEnclosingRequestBase){
            ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(new StringEntity(elasticSearchHttpRequest.content(),
                    ContentType.APPLICATION_JSON));
        }

        CloseableHttpResponse httpResp = httpclient.execute(httpRequest);

        String response = null;
        try {
            org.apache.http.HttpEntity entity = httpResp.getEntity();
            response = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            httpResp.close();
        }

        return new HttpResponse(httpResp.getStatusLine().getStatusCode(), response);
    }

    private HttpRequestBase createHttpRequest(URI url, HttpMethod httpMethod){
        switch(httpMethod){
            case GET:
                return new HttpGet(url);
            case PUT:
                return new HttpPut(url);
            case DELETE:
                return new HttpDelete(url);
            case POST:
                return new HttpPost(url);
        }
        throw new UnsupportedOperationException(String.format("Unsupported httpMethod={}", httpMethod));
    }
}

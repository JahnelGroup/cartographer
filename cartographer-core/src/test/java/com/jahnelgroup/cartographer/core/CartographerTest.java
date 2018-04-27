package com.jahnelgroup.cartographer.core;

import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpRequest;
import static com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpResponse;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CartographerTest {

    @Mock
    ElasticsearchHttpClient mockHttpClient;

    @Test
    public void test() throws Exception{
        Cartographer cartographer = new Cartographer();
        cartographer.setHttpClient(mockHttpClient);

        Mockito.when(mockHttpClient.exchange(any(HttpRequest.class)))
                .thenAnswer((Answer<HttpResponse>) invocationOnMock -> {
            HttpRequest request = (HttpRequest) invocationOnMock.getArguments()[0];
            return new HttpResponse(200, null);
        });

        cartographer.migrate();
    }

}

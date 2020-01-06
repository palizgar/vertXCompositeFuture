package com.hatef.demo.vertxapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class HttpClientVerticle extends AbstractVerticle {

  private WebResource webResource;
  private HttpResponse<Buffer> httpResponse;
  private long fetchDurationInMilliseconds = 0;

  public HttpClientVerticle(WebResource webResource) {
    this.webResource = webResource;
  }

  @Override
  public void start(Promise<Void> promise) throws Exception {

    //  url: the endpoint to which we send the request
    String url = webResource.getUrl();

    //  request: holds the request object
    HttpRequest<Buffer> request;

    //  set the system timer to record the time for fetch procedure
    long startTime = System.currentTimeMillis();

    //  creating request object from vertx instance
    request = WebClient.create(vertx).getAbs(url);

    //  sending request to url
    request.send(
        httpResponseAsyncResult -> {

          //  CASE 1: received the response successfully

          if (httpResponseAsyncResult.succeeded()) {

            //  calculating the fetch duration (the time frame between sending a request and
            // receiving its response)
            fetchDurationInMilliseconds = System.currentTimeMillis() - startTime;

            //  register the response object
            httpResponse = httpResponseAsyncResult.result();

            //  associate response body size with webResource
            int size = httpResponse.body().getBytes().length;
            webResource.setBodySize(size);

            //            System.out.println("promise completed successfully");
            promise.complete();

            //  CASE 2: no response is received
          } else {
            //            System.out.println("failed to complete the promise");
            promise.fail(httpResponseAsyncResult.cause());
          }
        });
    //  acknowledge the caller: 'future is completed (either successfully/failed )'
  }

  // === === === === === === === === === === === === === ===
  //  Client getters and setters

  public HttpResponse<Buffer> getHttpResponse() {
    return this.httpResponse;
  }

  public WebResource getWebResource() {
    return this.webResource;
  }

  public void setWebResource(WebResource webResource) {
    this.webResource = webResource;
  }

  public long getFetchDurationInMilliseconds() {
    return this.fetchDurationInMilliseconds;
  }
}

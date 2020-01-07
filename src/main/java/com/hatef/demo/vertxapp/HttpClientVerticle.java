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
        result -> {

          //  CASE 1: received the response successfully
          if (result.succeeded()) {

            //  calculating the fetch duration (the time frame between sending a request and
            // receiving its response)
            fetchDurationInMilliseconds = System.currentTimeMillis() - startTime;

            //  register the response object
            httpResponse = result.result();

            //  associate response body size with webResource
            int size = httpResponse.body().getBytes().length;
            webResource.setBodySize(size);

            promise.complete();
            //  CASE 2: no response is received
          } else {
            promise.fail(result.cause() + ":");
            System.out.println(result.cause().getMessage());
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

  public long getFetchDurationInMilliseconds() {
    return this.fetchDurationInMilliseconds;
  }
}

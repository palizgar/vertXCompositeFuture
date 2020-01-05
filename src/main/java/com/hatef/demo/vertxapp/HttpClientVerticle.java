package com.hatef.demo.vertxapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class HttpClientVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientVerticle.class);

  private WebResource webResource;
  private HttpResponse<Buffer> httpResponse;
  private long fetchDurationInMilliseconds;
  private int size;

  public HttpClientVerticle(WebResource webResource) {
    this.webResource = webResource;
    this.fetchDurationInMilliseconds = 0;
  }

  @Override
  public void start(Promise<Void> promise) throws InterruptedException {

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
            size = httpResponse.body().getBytes().length;
            webResource.setBodySize(size);

            LOGGER.info(
                "+++ Fetch(SUCCESSFUL) for URL: "
                    + webResource.getUrl()
                    + "\n-Duration: "
                    + fetchDurationInMilliseconds
                    + " ms\n-STATUS: "
                    + httpResponse.statusMessage()
                    + "\n-BODY SIZE RECEIVED: "
                    + size);

            //  CASE 2: no response is received
          } else {

            //  fail the promise and report the cause
            LOGGER.error(
                "--- Fetch(FAILED) for URL: "
                    + webResource.getUrl()
                    + "\n"
                    + httpResponseAsyncResult.cause());
          }
        });
    //  acknowledge the caller: 'future is completed (either successfully/failed )'
    promise.complete();
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

  public int getSize() {
    return size;
  }
}

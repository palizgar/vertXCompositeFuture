package com.hatef.demo.vertxapp;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    HttpClientVerticle googleClient =
        new HttpClientVerticle(new WebResource("http://www.google.com"));
    HttpClientVerticle yahooClient =
        new HttpClientVerticle(new WebResource("http://www.yahoo.com"));
    HttpClientVerticle msnClient = new HttpClientVerticle(new WebResource("http://www.msn.com"));
    List<HttpClientVerticle> verticleList = new ArrayList<>();
    verticleList.add(msnClient);
    verticleList.add(googleClient);
    verticleList.add(yahooClient);
    Promise<HttpResponse<Buffer>> googlePromise = Promise.promise();
    Promise<HttpResponse<Buffer>> yahooPromise = Promise.promise();
    Promise<HttpResponse<Buffer>> msnPromise = Promise.promise();

    vertx.deployVerticle(
        googleClient,
        ar -> {
          if (ar.succeeded()) {
            LOGGER.info("-- Verticles Deployed Successfully");
            googlePromise.complete();

          } else {
            LOGGER.error("-- Failed to Deploy Verticle(s)");
            googlePromise.fail(ar.cause());
          }
        });

    Future<HttpResponse<Buffer>> future = googlePromise.future(); //  once googlePromise is
    // completed, the handler would be called
    future.setHandler(
        ar -> {
          if (ar.succeeded()) {
            System.out.println(
                "FETCH SUCCESS - URL: "
                    + googleClient.getWebResource().getUrl()
                    + "\n"
                    + "STATUS CODE: "
                    + googleClient.getHttpResponse().statusCode()
                    + "\n"
                    + "FETCH DURATION(ms): "
                    + googleClient.getFetchDurationInMilliseconds()
                    + "\n"
                    + "BODY SIZE(bytes): "
                    + googleClient.getWebResource().getBodySize());
          }
        });
  }
}

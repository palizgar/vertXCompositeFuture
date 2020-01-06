package com.hatef.demo.vertxapp;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Main {

  static Vertx vertx = Vertx.vertx();

  public static void main(String[] args) {
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

    //  once promises are completed, the handler would be called
    CompositeFuture.all(
            deploy(googleClient, googlePromise),
            deploy(yahooClient, yahooPromise),
            deploy(msnClient, msnPromise))
        .setHandler(
            ar -> {
              int totalSize = 0;
              if (ar.succeeded()) {
                System.out.println("\nPreparing Report...\n\n");

                for (HttpClientVerticle v : verticleList) {
                  //  print out the result to the console

                  totalSize += v.getWebResource().getBodySize();
                  System.out.println(
                      "FETCH SUCCESS - URL: "
                          + v.getWebResource().getUrl()
                          + "\n"
                          + "STATUS CODE: "
                          + v.getHttpResponse().statusCode()
                          + "\n"
                          + "FETCH DURATION(ms): "
                          + v.getFetchDurationInMilliseconds()
                          + "\n"
                          + "BODY SIZE(bytes): "
                          + v.getWebResource().getBodySize());
                }
              } else {
                System.out.println("ERROR deploying CompositeFuture");
              }
              System.out.println("\nTOTAL SIZE: " + totalSize);
            });
  }

  //  deploy a verticle and return its future object
  private static Future<HttpResponse<Buffer>> deploy(
      HttpClientVerticle v, Promise<HttpResponse<Buffer>> promise) {
    Promise promise1 = Promise.promise();

    vertx.deployVerticle(
        v,
        ar -> {
          try {
            if (ar.succeeded()) {
              System.out.println("-- Verticle(s) Deployed Successfully");
              promise.complete();

            } else {
              System.out.println("-- Failed to Deploy Verticle(s)");
              promise.fail(ar.cause().getMessage());
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
    return promise.future();
  }
}

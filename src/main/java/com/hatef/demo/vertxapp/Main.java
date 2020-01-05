package com.hatef.demo.vertxapp;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class Main {

  private static Vertx vertx;
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    HttpClientVerticle googleClient =
        new HttpClientVerticle(new WebResource("http://www.goog52fssle.com"));
    HttpClientVerticle yahooClient =
        new HttpClientVerticle(new WebResource("http://www.yahoo.com"));
    HttpClientVerticle msnClient = new HttpClientVerticle(new WebResource("http://www.msn.com"));
    List<HttpClientVerticle> verticleList = new ArrayList<>();
    verticleList.add(msnClient);
    verticleList.add(googleClient);
    verticleList.add(yahooClient);

    CompositeFuture.all(
            deploy(vertx, googleClient), deploy(vertx, yahooClient), deploy(vertx, msnClient))
        .compose(s -> deploy(vertx, new ReportVerticle(verticleList)))
        .setHandler(
            ar -> {
              if (ar.succeeded()) {
                LOGGER.info("-- Verticles Deployed Successfully");
              } else {
                LOGGER.error("-- Failed to Deploy Verticle(s)");
              }
            });
  }

  private static Future<String> deploy(Vertx vertx, Verticle verticle) {
    Promise<String> promise = Promise.promise();
    vertx.deployVerticle(verticle, promise);

    return promise.future();
  }
}

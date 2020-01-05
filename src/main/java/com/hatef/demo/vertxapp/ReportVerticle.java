package com.hatef.demo.vertxapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import java.util.ArrayList;
import java.util.List;

public class ReportVerticle extends AbstractVerticle {
  private List<HttpClientVerticle> verticleList;
  private List<Integer> sizeList = new ArrayList<>();

  @Override
  public void start(Promise<Void> promise) throws Exception {

    for (HttpClientVerticle v : verticleList) {
      sizeList.add(v.getSize());
      System.out.println(v.getSize());
    }
    promise.complete();
  }

  public ReportVerticle(List<HttpClientVerticle> verticleList) {
    this.verticleList = verticleList;
  }
}

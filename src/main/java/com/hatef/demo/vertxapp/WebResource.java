package com.hatef.demo.vertxapp;

public class WebResource {
  private String url;
  private int bodySize = 0;

  public WebResource(String url) {
    this.url = url;
  }

  public String getUrl() {
    return this.url;
  }

  public int getBodySize() {
    return this.bodySize;
  }

  public void setBodySize(int bodySize) {
    this.bodySize = bodySize;
  }
}

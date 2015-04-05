package com.buntplanet.example;

import com.buntplanet.bender.Bender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExampleWeb {
  private static final Logger logger = LoggerFactory.getLogger(ExampleWeb.class);

  public static void main(String... args) throws Exception {

    Bender bender = Bender.at(8080);

    bender.post("/cocotero", request -> {
      System.out.println("PAYLOAD:");
      for (String key : request.payload().keySet())
        logger.info(key + ": " + request.payload().get(key));
      System.out.println("");
      System.out.println("");
      System.out.println("");
      System.out.println("PARAMS:");
      for (String key : request.params().keySet())
        logger.info(key + ": " + request.params().get(key));
      System.out.println("");
      System.out.println("");
      System.out.println("");
      return request.buildResponse().content("HU HA!");
    });

    bender.get("/cocotero/:chuchu/blabla", request -> {
      System.out.println("PAYLOAD:");
      for (String key : request.payload().keySet())
        logger.info(key + ": " + request.payload().get(key));
      System.out.println("");
      System.out.println("");
      System.out.println("");
      System.out.println("PARAMS:");
      for (String key : request.params().keySet())
        logger.info(key + ": " + request.params().get(key));
      System.out.println("");
      System.out.println("");
      System.out.println("");
      return request.buildResponse().content("HU HA!");
    });

    bender.run();
  }
}

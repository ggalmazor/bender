package net.programania.example;

import net.programania.bender.Bender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenderExample {
  private static final Logger logger = LoggerFactory.getLogger(BenderExample.class);

  public static void main(String... args) throws Exception {

    Bender bender = Bender.at(8080);

    bender.post("/post/example", request -> {
      logger.info("Post request received!");
      for (String key : request.payload().keySet())
        logger.info("\t " + key + ": " + request.payload().get(key));
      return request.buildResponse().content("It worked!");
    });

    bender.get("/get/example/:param", request -> {
      logger.info("Get request received!");
      logger.info("\t param: " + request.param("param").orElse("[empty]"));
      return request.buildResponse().content("It worked!");
    });

    bender.run();
  }
}

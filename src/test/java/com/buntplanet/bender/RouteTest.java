package com.buntplanet.bender;


import net.sourceforge.urin.ParseException;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RouteTest {

  public static final Function<Request, Response> DO_NOTHING = request -> request.buildResponse().noContent();
  private static Route route = Route.of(HttpMethod.GET, "/cocotero/:chuchu/blabla", DO_NOTHING);

  @Test
  public void matches_input_path() throws ParseException {
    assertThat("Route doesn't match", route.match(HttpMethod.GET, "/cocotero/lolailo/blabla").matches(), is(true));
    assertThat("Route matches", route.match(HttpMethod.GET, "/cocotero/lolailo/blabla/chuchu").matches(), is(false));
    assertThat("Route matches", route.match(HttpMethod.GET, "/blabla/cocotero/lolailo/blabla").matches(), is(false));
    assertThat("Route matches", route.match(HttpMethod.GET, "/chuchu/lolailo/blabla").matches(), is(false));
    assertThat("Route matches", route.match(HttpMethod.GET, "/blabla/lolailo/cocotero").matches(), is(false));
  }

  @Test
  public void matches_input_path_regression() {
    assertThat("Route doesn't match", route.match(HttpMethod.GET, "/cocotero/lolailo/blabla/").matches(), is(false));
  }

  @Test
  public void parses_path_params() throws ParseException {
    RouteMatch match = route.match(HttpMethod.GET, "/cocotero/lolailo/blabla");

    assertThat("Route doesn't deserialize path param chuchu", match.params.containsKey("chuchu"), is(true));
    assertThat("Route doesn't deserialize path param chuchu", match.params.get("chuchu"), is("lolailo"));
  }

  @Test
  public void parses_query_params() throws ParseException {
    RouteMatch match = route.match(HttpMethod.GET, "/cocotero/lolailo/blabla?pepo=3&coco=chuchu");

    assertThat("Route doesn't deserialize query param pepo", match.params.containsKey("pepo"), is(true));
    assertThat("Route doesn't deserialize query param pepo", match.params.get("pepo"), is("3"));
    assertThat("Route doesn't deserialize query param coco", match.params.containsKey("coco"), is(true));
    assertThat("Route doesn't deserialize query param coco", match.params.get("coco"), is("chuchu"));
  }


}
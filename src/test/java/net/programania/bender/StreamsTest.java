package net.programania.bender;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StreamsTest {
  @Test
  public void cocoTest() {
    List<Integer> integers = Arrays.asList(1, 2, 3);

    String join = integers.stream()
        .map(Object::toString)
        .collect(joining(","));

    assertThat(join, is("1,2,3"));
    assertThat(join.isEmpty(), is(false));
  }

  @Test
  public void cocoEmptyTest() {
    String join = new ArrayList<Integer>().stream()
        .map(Object::toString)
        .collect(joining(","));

    assertThat(join, is(""));
    assertThat(join.isEmpty(), is(true));
  }

}

package scraper.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataBuilderTest {

  @Test
  void buildQueryParamsCorrectResult() {
    String queryParams = DataBuilder.buildQueryParams("key1", "value1", "key2", "value2");
    assertEquals("?key1=value1&key2=value2", queryParams);
  }

  @Test
  void buildQueryParamsOddNumberOfArgsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> DataBuilder.buildQueryParams("one", "two", "three"));
  }

  @Test
  void buildQueryParamsNoArgsThrowsException() {
    assertThrows(IllegalArgumentException.class, DataBuilder::buildQueryParams);
  }
}
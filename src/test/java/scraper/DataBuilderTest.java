package scraper;

import org.junit.jupiter.api.Test;
import scraper.util.DataBuilder;

import static org.junit.jupiter.api.Assertions.*;

class DataBuilderTest {

    @Test
    void buildQueryParamsTest() {
        assertThrows(IllegalArgumentException.class, () -> DataBuilder.buildQueryParams("one","two","three"));
        assertThrows(IllegalArgumentException.class, DataBuilder::buildQueryParams);
        String queryParams = DataBuilder.buildQueryParams("key1", "value1", "key2", "value2");
        assertEquals("?key1=value1&key2=value2", queryParams);
    }

}
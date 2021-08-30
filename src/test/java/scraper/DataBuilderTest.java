package scraper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scraper.util.DataBuilder;

import static org.junit.jupiter.api.Assertions.*;

class DataBuilderTest {

    private static DataBuilder dataBuilder;

    @BeforeAll
    static void beforeAll() {
        dataBuilder = new DataBuilder();
    }

    @Test
    void buildQueryParamsTest() {
        assertThrows(IllegalArgumentException.class, () -> dataBuilder.buildQueryParams("one","two","three"));
        assertThrows(IllegalArgumentException.class, () -> dataBuilder.buildQueryParams());
        String queryParams = dataBuilder.buildQueryParams("key1", "value1", "key2", "value2");
        assertEquals("?key1=value1&key2=value2", queryParams);
    }

}
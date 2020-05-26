package integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;
import lombok.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IntegrationTest {

    private final String CREATE_DATA_POINTS_URI = "http://localhost:8081/data-producer/data-points";
    private final String GET_OUTLIERS_URI = "http://localhost:8083/outlier-detector/outliers?publisherId={publisher}&dataSize={dataSize}&deviation={deviation}";

    private List<DataPoint> dataToTest;
    private List<Double> expectedResults;

    @BeforeClass
    public void setup() {

        // Data points ignored, being older than the last 5 data points
        final DataPoint dp01 = DataPoint.builder().publisher("p1").readings(Arrays.asList(0, 1, 3, 5, 7)).build(); // median 3
        final DataPoint dp02 = DataPoint.builder().publisher("p1").readings(Arrays.asList(1, 3, 5, 7, 9)).build(); // median 5

        // Data points taken into consideration, being the last 5 values written for publisher p1
        final DataPoint dp1 = DataPoint.builder().publisher("p1").readings(Arrays.asList(1, 2, 3, 4, 5)).build(); // median 3
        final DataPoint dp2 = DataPoint.builder().publisher("p1").readings(Arrays.asList(10, 20, 30, 40, 50)).build(); // median 30
        final DataPoint dp3 = DataPoint.builder().publisher("p1").readings(Arrays.asList(20, 30, 40, 50, 60)).build(); // median 40
        final DataPoint dp4 = DataPoint.builder().publisher("p1").readings(Arrays.asList(30, 45, 40, 75, 65, 70)).build(); // median 55
        final DataPoint dp5 = DataPoint.builder().publisher("p1").readings(Arrays.asList(100, 200, 300, 400, 500)).build(); // median 300

        // Data points ignored due to a different publisher
        final DataPoint dp001 = DataPoint.builder().publisher("p2").readings(Arrays.asList(11, 22, 33, 44, 55)).build(); // median 33
        final DataPoint dp002 = DataPoint.builder().publisher("p3").readings(Arrays.asList(101, 202, 303, 404, 505)).build(); // median 303

        dataToTest = Arrays.asList(dp01, dp02, dp1, dp2, dp3, dp4, dp5, dp001, dp002);
        expectedResults = Arrays.asList(3.0, 300.0);
    }

    @Test
    public void endToEndIntegrationTest() {
        dataToTest.stream().forEach(dp -> pushTestData(dp));
        final List<Double> results = getResults();

        assertThat("The expected outliers don't match the actual result", results, is(expectedResults));
    }

    @SneakyThrows
    private void pushTestData(DataPoint dp) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(dp), headers);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(CREATE_DATA_POINTS_URI, request, String.class);
    }

    private List<Double> getResults() {
        final RestTemplate restTemplate = new RestTemplate();
        final JsonNode outliersAsJsonNode = restTemplate.getForObject(
                GET_OUTLIERS_URI,
                JsonNode.class,
                ImmutableMap.<String, String>builder()
                        .put("publisher", "p1")
                        .put("dataSize", "5")
                        .put("deviation", "90")
                        .build());
        final ArrayNode outliersArrayNode = (ArrayNode) outliersAsJsonNode.get("outliers");
        return StreamSupport.stream(outliersArrayNode.spliterator(), false)
                .map(doubleNode -> doubleNode.asDouble())
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataPoint {
        String publisher;
        private List<Integer> readings;
    }
}

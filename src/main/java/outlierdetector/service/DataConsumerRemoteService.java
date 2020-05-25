package outlierdetector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DataConsumerRemoteService {

    @Value(value = "${DataConsumerRemoteService.dataConsumerUri}")
    private String dataConsumerUri;

    public List<Double> readDataPointsByPublisher(String publisher, Integer dataSize) {
        final JsonNode result = new RestTemplate().getForObject(
                dataConsumerUri,
                JsonNode.class,
                ImmutableMap.<String, String>builder()
                        .put("publisher", publisher)
                        .put("dataSize", dataSize.toString())
                        .build());
        return getDataPoints(result);
    }

    private List<Double> getDataPoints(JsonNode dataPoints) {
        final ArrayNode dataPointsArrayNode = (ArrayNode) dataPoints.get("dataPoints");
        return StreamSupport.stream(dataPointsArrayNode.spliterator(), false)
                .map(objNode -> objNode.get("value").asDouble())
                .collect(Collectors.toList());
    }
}

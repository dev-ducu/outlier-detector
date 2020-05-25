package outlierdetector.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import outlierdetector.exception.CustomException;

@Service
@Slf4j
public class OutlierDetectionService {

    private final DataConsumerMockService dataConsumerMockService;
    private final static Double DEFAULT_DEVIATION_PERCENTAGE = 30.0;

    public OutlierDetectionService(DataConsumerMockService dataConsumerMockService) {
        this.dataConsumerMockService = dataConsumerMockService;
    }

    public List<Integer> getOutliers(String publisherId, Integer dataSize, Optional<Double> deviation) {
        final List<Integer> data = dataConsumerMockService.readDataByPublisher(publisherId, dataSize);
        return computeOutliers(publisherId, deviation.orElse(DEFAULT_DEVIATION_PERCENTAGE), data);
    }

    private List<Integer> computeOutliers(String publisherId, Double deviation, List<Integer> data) {
        if (CollectionUtils.isEmpty(data)) {
            log.warn("No data found for publisher {}", publisherId);
            throw new CustomException(String.format("No data found for publisher %s", publisherId), HttpStatus.NOT_FOUND);
        }

        // Log the sorted data set, for troubleshooting purposes.
        Collections.sort(data);
        log.info("Reading data set: {}", data);

        // Compute the data set average value
        final Double average = data.stream().mapToDouble(dataItem -> Double.valueOf(dataItem)).average().getAsDouble();

        // Compute the lower & upper limits of the 'normal values' range
        final Double lowerLimit = subtractPercentage(average, deviation);
        final Double upperLimit = addPercentage(average, deviation);

        final List<Integer> outliers = data.stream()
                .filter(dataItem -> dataItem < lowerLimit || dataItem > upperLimit)
                .collect(Collectors.toList());

        log.info("Outliers for deviation value =  {}% : {}", deviation, outliers);
        return outliers;
    }

    private Double addPercentage(Double value, Double percentage) {
        final BigDecimal baseValue = BigDecimal.valueOf(value);
        final BigDecimal percentageInAbsoluteValue = baseValue.multiply(
                BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100)));

        return baseValue.add(percentageInAbsoluteValue).doubleValue();
    }

    private Double subtractPercentage(Double value, Double percentage) {
        final BigDecimal baseValue = BigDecimal.valueOf(value);
        final BigDecimal percentageInAbsoluteValue = baseValue.multiply(
                BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100)));

        return baseValue.subtract(percentageInAbsoluteValue).doubleValue();
    }
}

package outlierdetector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import outlierdetector.exception.CustomException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OutlierDetectionService {

    private final static Double DEFAULT_DEVIATION_PERCENTAGE = 30.0;

    private final DataConsumerRemoteService dataConsumerRemoteService;

    public OutlierDetectionService(DataConsumerRemoteService dataConsumerRemoteService) {
        this.dataConsumerRemoteService = dataConsumerRemoteService;
    }

    public List<Double> getOutliers(String publisherId, Integer dataSize, Optional<Double> deviation) {
        return computeOutliers(
                publisherId,
                deviation.orElse(DEFAULT_DEVIATION_PERCENTAGE),
                dataConsumerRemoteService.readDataPointsByPublisher(publisherId, dataSize));
    }

    private List<Double> computeOutliers(String publisherId, Double deviation, List<Double> dataPoints) {
        if (CollectionUtils.isEmpty(dataPoints)) {
            log.warn("No data found for publisher {}", publisherId);
            throw new CustomException(String.format("No data found for publisher %s", publisherId), HttpStatus.NOT_FOUND);
        }

        // Log the sorted data set, for troubleshooting purposes.
        Collections.sort(dataPoints);
        log.info("Reading data set: {}", dataPoints);

        // Compute the data set average value
        final Double average = dataPoints.stream().mapToDouble(Double::valueOf).average().getAsDouble();

        // Compute the lower & upper limits of the 'normal values' range
        final Double lowerLimit = subtractPercentage(average, deviation);
        final Double upperLimit = addPercentage(average, deviation);

        final List<Double> outliers = dataPoints.stream()
                .filter(dataPoint -> (dataPoint < lowerLimit) || (dataPoint > upperLimit))
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

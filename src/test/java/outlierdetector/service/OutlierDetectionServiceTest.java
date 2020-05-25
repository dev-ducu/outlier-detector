package outlierdetector.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import outlierdetector.exception.CustomException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutlierDetectionServiceTest {

    @Mock
    private DataConsumerRemoteService dataConsumerRemoteService;

    private OutlierDetectionService outlierDetectionService;

    @Before
    public void setup() {
        outlierDetectionService = new OutlierDetectionService(dataConsumerRemoteService);
    }

    @Test(expected = CustomException.class)
    public void getOutliers_whenNoData_shouldThrowException() {
        when(dataConsumerRemoteService.readDataPointsByPublisher("123", 5))
                .thenReturn(Collections.emptyList());

        outlierDetectionService.getOutliers("123", 5, Optional.empty());
    }

    @Test
    public void getOutliers_whenNoDeviationGiven_shouldReturnExpected() {
        when(dataConsumerRemoteService.readDataPointsByPublisher("123", 5))
                .thenReturn(Arrays.asList(11.0, 12.0, 14.0, 15.0, 15.0, 16.0, 18.0, 19.0, 22.0, 23.0));

        final List<Double> result = outlierDetectionService.getOutliers("123", 5, Optional.empty());

        assertThat(result, containsInAnyOrder(11.0, 22.0, 23.0));
    }

    @Test
    public void getOutliers_whenDeviationGiven_shouldUseDefault() {
        when(dataConsumerRemoteService.readDataPointsByPublisher("123", 5))
                .thenReturn(Arrays.asList(11.0, 12.0, 14.0, 15.0, 15.0, 16.0, 18.0, 19.0, 22.0, 23.0));

        final List<Double> result = outlierDetectionService.getOutliers("123", 5, Optional.of(35.0));

        assertThat(result, containsInAnyOrder(23));
    }
}

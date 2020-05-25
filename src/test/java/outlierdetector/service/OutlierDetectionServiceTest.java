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
    private DataConsumerMockService dataConsumerMockService;

    private OutlierDetectionService outlierDetectionService;

    @Before
    public void setup() {
        outlierDetectionService = new OutlierDetectionService(dataConsumerMockService);
    }

    @Test(expected = CustomException.class)
    public void getOutliers_whenNoData_shouldThrowException() {
        when(dataConsumerMockService.readDataByPublisher("123", 5))
                .thenReturn(Collections.emptyList());

        outlierDetectionService.getOutliers("123", 5, Optional.empty());
    }

    @Test
    public void getOutliers_whenNoDeviationGiven_shouldReturnExpected() {
        when(dataConsumerMockService.readDataByPublisher("123", 5))
                .thenReturn(Arrays.asList(11, 12, 14, 15, 15, 16, 18, 19, 22, 23));

        final List<Integer> result = outlierDetectionService.getOutliers("123", 5, Optional.empty());

        assertThat(result, containsInAnyOrder(11, 22, 23));
    }

    @Test
    public void getOutliers_whenDeviationGiven_shouldUseDefault() {
        when(dataConsumerMockService.readDataByPublisher("123", 5))
                .thenReturn(Arrays.asList(11, 12, 14, 15, 15, 16, 18, 19, 22, 23));

        final List<Integer> result = outlierDetectionService.getOutliers("123", 5, Optional.of(35.0));

        assertThat(result, containsInAnyOrder(23));
    }
}

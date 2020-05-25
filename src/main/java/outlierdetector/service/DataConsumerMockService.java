package outlierdetector.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DataConsumerMockService {

    public List<Integer> readDataByPublisher(String publisherId, Integer dataSize) {
        return Arrays.asList(11, 12, 14, 15, 15, 16, 18, 19, 22, 23);
    }
}

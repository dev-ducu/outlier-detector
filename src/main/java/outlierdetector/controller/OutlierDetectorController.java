package outlierdetector.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import outlierdetector.config.SwaggerConfig;
import outlierdetector.dto.response.OutlierResponseDTO;
import outlierdetector.service.OutlierDetectionService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping(OutlierDetectorController.OUTLIERS_PATH)
@Api(tags = SwaggerConfig.TAG_OUTLIERS)
public class OutlierDetectorController {

    public static final String OUTLIERS_PATH = "/outliers";
    private final OutlierDetectionService outlierService;

    @Autowired
    public OutlierDetectorController(final OutlierDetectionService outlierService) {
        this.outlierService = outlierService;
    }

    @GetMapping
    @ApiOperation(value = "${OutlierDetectorController.get}", response = OutlierResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Not found")
    })
    public OutlierResponseDTO get(
            @ApiParam(value = "Publisher Id", example = "publisher-1") @RequestParam String publisherId,
            @ApiParam(value = "Reading bucket size", example = "10") @RequestParam Integer dataSize,
            @ApiParam(value = "Max allowed deviation; if not given, will default to 30%", example = "0.3") @RequestParam(required = false) Double deviation) {
        log.info("Handling GET request on path => " + OUTLIERS_PATH);

        final List<Double> outliers = outlierService.getOutliers(publisherId, dataSize, Optional.ofNullable(deviation));
        final OutlierResponseDTO responseDTO = OutlierResponseDTO.builder()
                .outliers(outliers)
                .build();

        log.info("Response => {}", responseDTO);
        return responseDTO;
    }
}

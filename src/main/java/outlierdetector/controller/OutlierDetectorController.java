package outlierdetector.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import outlierdetector.config.SwaggerConfig;
import outlierdetector.dto.request.OutlierRequestDTO;
import outlierdetector.dto.response.OutlierResponseDTO;
import outlierdetector.service.OutlierDetectionService;

@RestController
@Slf4j
@RequestMapping(OutlierDetectorController.OUTLIERS_PATH)
@Api(tags = SwaggerConfig.TAG_DATA_POINTS)
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
            @ApiParam("Publisher Id") @RequestParam String publisherId,
            @ApiParam("Reading bucket size") @RequestParam Integer dataSize,
            @ApiParam("Max allowed deviation; if not given, will default to 30%") @RequestParam(required = false) Double deviation) {
        log.info("Handling GET request on path => " + OUTLIERS_PATH);

        final List<Integer> outliers = outlierService.getOutliers(publisherId, dataSize, Optional.ofNullable(deviation));

        final OutlierResponseDTO responseDTO = OutlierResponseDTO.builder()
                .outliers(outliers)
                .build();

        log.info("Response => {}", responseDTO);
        return responseDTO;
    }
}

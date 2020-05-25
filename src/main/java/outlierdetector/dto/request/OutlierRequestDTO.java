package outlierdetector.dto.request;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutlierRequestDTO {

    @NotBlank
    @ApiModelProperty(name = "publisherId", value = "Publisher ID")
    private String publisher;

    @NotBlank
    @ApiModelProperty(name = "readingSize", value = "Reading bucket size")
    private Integer readingsSize;

    @ApiModelProperty(name = "maxDeviation", value = "Max allowed deviation; if not given, will default to 30%")
    private Double maxDeviation;
}

package outlierdetector.dto.response;

import io.swagger.annotations.ApiModelProperty;
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
public class OutlierResponseDTO {

    @NotBlank
    @ApiModelProperty(name = "Outliers", value = "Computed outlier values")
    private List<Double> outliers;
}

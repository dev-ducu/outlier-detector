package outlierdetector.dto.response;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

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

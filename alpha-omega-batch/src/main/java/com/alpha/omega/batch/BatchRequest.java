package com.alpha.omega.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class BatchRequest<T> {
    @ApiModelProperty(required = true, value = "")
    protected String correlationId;
    @ApiModelProperty(required = true, value = "")
    protected Map<String,String> jobParameters;
    @ApiModelProperty(required = false, value = "")
    protected List<T> content;
    @ApiModelProperty(required = false, value = "")
    protected String jobName;
    @ApiModelProperty(required = false, value = "")
    protected String jobGroup;
    @Builder.Default
    protected BatchJobType batchJobType = BatchJobType.IN_MEMORY_JSON;
    @Builder.Default
    protected String multiReaderName = "multi.s3.reader";

    public enum BatchJobType{
        IN_MEMORY_JSON("in-memory-json"), LOCAL_CSV("local-csv"), S3_BUCKET_CSV("s3-bucket-csv");

        String valStr;

        @JsonCreator
        BatchJobType(String val) {
            valStr = val;
;        }
    }
}

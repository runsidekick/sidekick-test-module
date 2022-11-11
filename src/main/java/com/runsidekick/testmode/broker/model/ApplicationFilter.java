package com.runsidekick.testmode.broker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;

/**
 * @author yasin.kalafat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ApplicationFilter {

    private String name;
    private String version;
    private String stage;
    private Map<String, String> customTags;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationFilter that = (ApplicationFilter) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(stage, that.stage) &&
                Objects.equals(version, that.version) &&
                Objects.equals(customTags, that.customTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stage, version, customTags);
    }

}
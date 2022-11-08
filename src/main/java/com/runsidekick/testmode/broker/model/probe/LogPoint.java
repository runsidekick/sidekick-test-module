package com.runsidekick.testmode.broker.model.probe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

/**
 * @author yasin.kalafat
 */
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogPoint extends BaseProbe {

    protected String logLevel;

}
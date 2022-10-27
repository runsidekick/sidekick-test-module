package com.runsidekick.testmode.broker.model.event.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yasin.kalafat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogPointEvent extends BaseEvent {

    private String logPointId;
    private String fileName;
    private String className;
    private int lineNo;
    private String methodName;
    private String logMessage;
    private String createdAt;
    private String logLevel;

}

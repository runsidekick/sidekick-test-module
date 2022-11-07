package com.runsidekick.testmode.controller.request;

import lombok.Data;
import org.springframework.lang.NonNull;

/**
 * @author yasin.kalafat
 */
@Data
public class EventRequest {

    @NonNull
    private String appName;
    @NonNull
    private String fileName;
    @NonNull
    private int lineNo;
    private String tag;
}

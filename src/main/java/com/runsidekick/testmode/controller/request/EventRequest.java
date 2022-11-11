package com.runsidekick.testmode.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * @author yasin.kalafat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NonNull
    private String appName;
    private String fileName;
    private int lineNo;
    private String probeName;
}

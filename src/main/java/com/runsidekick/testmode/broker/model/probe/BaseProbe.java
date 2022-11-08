package com.runsidekick.testmode.broker.model.probe;

import lombok.Data;

import java.util.List;

/**
 * @author yasin.kalafat
 */
@Data
public abstract class BaseProbe {
    protected String id;
    protected String fileName;
    protected int lineNo;
    protected String client;
    protected String probeName; // for predefined probe
    protected List<String> tags;
}

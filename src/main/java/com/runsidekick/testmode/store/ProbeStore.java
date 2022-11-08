package com.runsidekick.testmode.store;

import com.runsidekick.testmode.broker.model.probe.LogPoint;
import com.runsidekick.testmode.broker.model.probe.TracePoint;

import java.util.List;

/**
 * @author yasin.kalafat
 */
public interface ProbeStore {

    void setTracePoints(List<TracePoint> tracePoints);

    void setLogPoints(List<LogPoint> logPoints);

    TracePoint getTracePointByProbeName(String probeName);

    LogPoint getLogPointByProbeName(String probeName);

    List<TracePoint> getTracePointsByTag(String tag);

    List<LogPoint> getLogPointsByTag(String tag);
}

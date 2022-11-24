package com.runsidekick.testmode.store.impl;

import com.runsidekick.testmode.broker.model.probe.LogPoint;
import com.runsidekick.testmode.broker.model.probe.TracePoint;
import com.runsidekick.testmode.store.ProbeStore;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yasin.kalafat
 */
@Component
public class ProbeStoreImpl implements ProbeStore {

    private List<TracePoint> tracePoints;

    private List<LogPoint> logPoints;

    @Override
    public void setTracePoints(List<TracePoint> tracePoints) {
        this.tracePoints = Collections.synchronizedList(tracePoints);
    }

    @Override
    public void setLogPoints(List<LogPoint> logPoints) {
        this.logPoints = Collections.synchronizedList(logPoints);
    }

    @Override
    public TracePoint getTracePointByProbeName(String probeName) {
        if (tracePoints != null) {
            List<TracePoint> filteredTracePoints = tracePoints.stream()
                    .filter(tracePoint -> tracePoint.getProbeName() != null
                            && tracePoint.getProbeName().equals(probeName))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(filteredTracePoints) ? null : filteredTracePoints.get(0);
        }
        return null;
    }

    @Override
    public LogPoint getLogPointByProbeName(String probeName) {
        if (logPoints != null) {
            List<LogPoint> filteredLogPoints = logPoints.stream()
                    .filter(logPoint -> logPoint.getProbeName() != null
                            && logPoint.getProbeName().equals(probeName))
                    .collect(Collectors.toList());
            return CollectionUtils.isEmpty(filteredLogPoints) ? null : filteredLogPoints.get(0);
        }
        return null;
    }

    @Override
    public List<TracePoint> getTracePointsByTag(String tag) {
        List<TracePoint> filteredTracePoints = tracePoints.stream()
                .filter(tracePoint -> tracePoint.getTags() != null
                        && tracePoint.getTags().contains(tag))
                .collect(Collectors.toList());
        return filteredTracePoints;
    }

    @Override
    public List<LogPoint> getLogPointsByTag(String tag) {
        List<LogPoint> filteredLogPoints = logPoints.stream()
                .filter(logPoint -> logPoint.getTags() != null
                        && logPoint.getTags().contains(tag))
                .collect(Collectors.toList());
        return filteredLogPoints;
    }
}

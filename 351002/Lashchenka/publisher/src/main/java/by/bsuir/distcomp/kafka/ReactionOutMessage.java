package by.bsuir.distcomp.kafka;

import java.util.List;

public class ReactionOutMessage {

    private String correlationId;
    private int status;
    private String errorMessage;
    private ReactionSnapshot snapshot;
    private List<ReactionSnapshot> snapshots;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ReactionSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(ReactionSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public List<ReactionSnapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<ReactionSnapshot> snapshots) {
        this.snapshots = snapshots;
    }
}

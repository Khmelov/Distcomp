package by.distcomp.app.exception;

public class AssociationNotFoundException extends RuntimeException {
    private final String associationType;
    private final Long associationId;

    public AssociationNotFoundException(String associationType, Long associationId) {
        super(String.format("%s with id %d not found", associationType, associationId));
        this.associationType = associationType;
        this.associationId = associationId;
    }

    public String getAssociationType() {
        return associationType;
    }

    public Long getAssociationId() {
        return associationId;
    }
}
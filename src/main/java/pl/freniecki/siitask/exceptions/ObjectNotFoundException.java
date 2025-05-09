package pl.freniecki.siitask.exceptions;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String boxNotFound) {
        super(boxNotFound);
    }
}

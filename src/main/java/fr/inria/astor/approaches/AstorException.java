package fr.inria.astor.approaches;

import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstorException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public AstorException() {
    }
    public AstorException(String msg) {
        super(msg);
    }
    public AstorException(Throwable e) {
        super(e);
    }
    public AstorException(String msg, Throwable e) {
        super(msg, e);
    }
}

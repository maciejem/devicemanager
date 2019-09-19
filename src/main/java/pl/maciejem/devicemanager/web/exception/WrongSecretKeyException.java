package pl.maciejem.devicemanager.web.exception;

public class WrongSecretKeyException extends RuntimeException {

    public WrongSecretKeyException() {
        super("Wrong secret key");
    }

}

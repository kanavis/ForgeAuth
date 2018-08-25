package pw.kanavis.forgelogin.auth;

public interface IAuthHandler {
    boolean getAuthorized();
    void setAuthorized(boolean authorized);
}
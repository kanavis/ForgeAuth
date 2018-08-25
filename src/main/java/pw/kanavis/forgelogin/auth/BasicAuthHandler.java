package pw.kanavis.forgelogin.auth;


public class BasicAuthHandler implements IAuthHandler {

    private boolean authorized;

    @Override
    public boolean getAuthorized() {
        return this.authorized;
    }

    @Override
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

}

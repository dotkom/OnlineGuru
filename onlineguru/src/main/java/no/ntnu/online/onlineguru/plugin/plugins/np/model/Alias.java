package no.ntnu.online.onlineguru.plugin.plugins.np.model;

/**
 * @author Håvard Slettvold
 */
public class Alias {

    private String nick;
    private String alias;
    private String apikey;

    public Alias(String nick, String alias) {
        this.nick = nick;
        this.alias = alias;
        this.apikey = "";
    }

    public Alias(String nick, String alias, String apikey) {
        this.nick = nick;
        this.alias = alias;
        this.apikey = apikey;
    }

    public String getNick() {
        return nick;
    }

    public String getAlias() {
        return alias;
    }

    public String getApikey() {
        return apikey;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Alias)) return false;
        Alias aliasObj = (Alias)o;

        if (aliasObj.getNick().equals(this.nick)) {
            return true;
        }

        return false;
    }

}

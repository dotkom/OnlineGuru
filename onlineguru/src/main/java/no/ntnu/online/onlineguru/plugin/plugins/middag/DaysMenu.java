package no.ntnu.online.onlineguru.plugin.plugins.middag;

class DaysMenu {
	
    private String menuHangaren;
    private String menuRealfag;

    public DaysMenu() {
        this.menuHangaren = "Det er ikke hentet noen meny for Hangaren enda.";
        this.menuRealfag = "Det er ikke hentet noen meny for Realfag enda.";
    }

    public void setMenu(String kantine, String Menu) {
        switch (Kantiner.valueOf(kantine)) {
            case HANGAREN: {
                menuHangaren = Menu;
                break;
            }
            case REALFAG: {
                menuRealfag = Menu;
                break;
            }
        }
    }

    public String getMenu(String kantine) {
        switch (Kantiner.valueOf(kantine)) {
            case HANGAREN: {
                return menuHangaren;
            }
            case REALFAG: {
                return menuRealfag;
            }
            default: {
                return "Det fins ingen meny for kantine "+kantine+".";
            }
        }
    }
}// end class DaysMenu
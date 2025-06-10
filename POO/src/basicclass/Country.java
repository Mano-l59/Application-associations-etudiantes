package basicclass;

/**
 * Enumération représentant les pays d'origine des étudiants.
 * Chaque pays est associé à son nom complet.
 * 
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.0
 */
public enum Country {
    FR("France"),
    IT("Italy"),
    ES("Spain"),
    GE("Germany");

    private final String fullName;

    /**
     * Constructeur privé de l'énumération Country.
     * @param fullName Le nom complet du pays.
     */
    private Country(String fullName) {
        this.fullName = fullName;
    }
    /**
     * Retourne le nom complet du pays.
     * @return Le nom complet du pays.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Cette méthode permet de récupérer un pays à partir de sa représentation en chaîne de caractères.
     * @param value La chaîne de caractères représentant le pays, soit son nom en minuscules, soit son nom complet.
     * @return Le nom du pays en minuscules.
     */
    public static Country fromString(String value) {
        String val = value.trim().toLowerCase();
        for (Country c : values()) {
            if (c.name().equalsIgnoreCase(val) || c.fullName.toLowerCase().trim().equals(val)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Pays inconnu : " + value);
    }
}
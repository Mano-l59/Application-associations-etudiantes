package manager;
import basicclass.AssociationStudent;
import basicclass.Country;
import java.io.*;
import java.util.*;

/**
 * La classe HistoryManager permet la gestion de l'historique des appariements entre les pays hôte et invité.
 * Néanmoins, il ne permet de stocker 1 seul appariement par couple de pays. Si un nouvel appariement est effectué pour ce meme couple, il sera écrasé.
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.3
 */
public class HistoryManager implements Serializable {
    /**
     * Attribut stockant l'historique des appariements via une HashMap<String, List<AssociationStudent>>.
     * Ce choix permet de représenter une liste de liste d'associations pour chaque couple de pays hôte et invité possible.
     * Dans le cas de notre application, vu que les pays sont au nombre de 4, il y aura au maximum 12 couples de pays possibles.
     *
     */
    private Map<String, List<AssociationStudent>> historique = new HashMap<>();
    
    /**
     * Ajoute ou remplace un matching pour un couple de pays.
     * @param host Pays hôte
     * @param guest Pays invité
     * @param associations Liste des associations à enregistrer
     */
    public void addOrReplaceMatching(Country host, Country guest, List<AssociationStudent> associations) {
        historique.put(buildKey(host, guest), associations);
    }

    /**
     * Récupère la liste des associations pour un couple de pays.
     * @param host Pays hôte
     * @param guest Pays invité
     * @return Liste des associations, ou null si aucune entrée
     */
    public List<AssociationStudent> getFormerMatching(Country host, Country guest) {
        return historique.get(buildKey(host, guest));
    }

    /**
     * Getters de l'attribut historique.
     * @return Retourne l'historique.
     */
    public Map<String, List<AssociationStudent>> getHistorique() {
        return historique;
    }
     /**
     * Méthode static permettant la construction de la clé de la map pour un couple de pays
     * @param host Pays hôte
     * @param guest Pays invité
     * @return La clé sous forme de chaîne de caractères
     */
    public static String buildKey(Country host, Country guest) {
        return host.name() + "-" + guest.name();
    }
    /**
     * Vide l'historique des appariements.
     */
    public void clearHistorique() {
        historique.clear();
    }

    /**
     * Sauvegarde l'historique complet dans un fichier.
     * @param filename Le nom du fichier dans lequel l'historique sera sauvegardé
     */
    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(historique);
            System.out.println("Historique sauvegardé dans " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de l'historique : " + e.getMessage());
        }
    }

    /**
     * Charge l'historique dans son entiereté depuis un fichier.
     * @param filename Le nom du fichier à partir duquel l'historique sera chargé
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.historique = (Map<String, List<AssociationStudent>>) obj;
                System.out.println("Historique chargé depuis " + filename);
            } else {
                System.err.println("Le fichier ne contient pas un historique valide.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement de l'historique : " + e.getMessage());
        }
    }

   
}
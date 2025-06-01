package basicClass;

import java.io.*;
import java.util.List;

/**
 * Classe pour gérer l'historique des associations par sérialisation binaire
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.0
 */
public class HistoryManager {
    private static final String HISTORY_FILE = "history.dat";
    
    /**
     * Sauvegarde les associations dans un fichier binaire
     * @param associations Liste des associations à sauvegarder
     */
    public void saveAssociations(List<AssociationStudent> associations) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(associations);
            System.out.println("Historique sauvegardé avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
    
    /**
     * Charge les associations depuis le fichier binaire
     * @return Liste des associations chargées ou null si erreur
     */
    @SuppressWarnings("unchecked")
    public List<AssociationStudent> loadAssociations() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
            return (List<AssociationStudent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Vérifie si le fichier d'historique existe
     * @return true si le fichier existe
     */
    public boolean historyExists() {
        File file = new File(HISTORY_FILE);
        return file.exists();
    }
    
    /**
     * Supprime le fichier d'historique
     */
    public void clearHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("Historique supprimé");
        }
    }
}
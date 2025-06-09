package utils;
import basicclass.AssociationStudent;
import basicclass.Student;
import basicclass.Constraints;
import manager.HistoryManager;
import java.util.List;

/**
 * La classe outils HistoryConstraintChecker permet de vérifier si deux étudiants ont déjà été appariés dans le passé et s'ils souhaitent être appariés à nouveau.
 * Elle utilise le gestionnaire d'historique pour accéder aux appariements passés.
 * Elle est utilisée lors de la phase de matching pour détermnier s'il faut directement les associer ou les laisser dans le gaphe pour le calcul.
 * (A noter que le reste des possibilités sur la valeur de HISTORY de chacun des 2 étudiants sont transmis alors à la méthode scoreAffinity de la classe AssociationStudent)
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.0
 */
public class HistoryConstraintChecker {

    /**
     * Permet de déterminer si deux étudiants hote et invite ont dejà été dans le passé (dans l'historique) appariés ensemble et
     * si c"est le cas, regarder si les deux étudiants veulent être appariés à nouveau ou non.
     * @param host L'étudiant hôte
     * @param guest L'étudiant invité
     * @param historyManager Le gestionnaire d'historique pour vérifier les appariements passés
     * @return true si les deux étudiants ont été appariés ensemble dans le passé et qu'ils souhaitent être appariés à nouveau, false sinon.
     */
    public static boolean checkHistoryConstraint(Student host, Student guest, HistoryManager historyManager) {
        String hostHistory = host.getConstraintsMap().getOrDefault(Constraints.HISTORY, "").toLowerCase();
        String guestHistory = guest.getConstraintsMap().getOrDefault(Constraints.HISTORY, "").toLowerCase();

        // On regarde le dernier matching entre ces deux pays
        List<AssociationStudent> lastMatching = historyManager.getFormerMatching(host.getCountry(), guest.getCountry());
        boolean werePaired = false;
        if (lastMatching != null) {
            for (AssociationStudent assoc : lastMatching) {
                if (assoc.getHost().equals(host) && assoc.getGuest().equals(guest)) {
                    werePaired = true;
                    break;
                }
            }
        }
        if (werePaired) {
            if ("same".equals(hostHistory) && "same".equals(guestHistory)) {
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
}
package ihm.tools;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import basicclass.Country;
import basicclass.Student;
import basicclass.AssociationStudent;

public class UpdateManualPreview {
    public static void update(
        ComboBox<Country> hostComboManual,
        ComboBox<Country> guestComboManual,
        ListView<Student> hostListView,
        ListView<Student> guestListView,
        Label previewLabel
    ) {
        Country h = hostComboManual.getValue();
        Country g = guestComboManual.getValue();
        if (h != null && g != null && h.equals(g)) {
            previewLabel.setText("Erreur : Les deux pays doivent être différents.");
            previewLabel.setVisible(true);
            return;
        }
        Student host = hostListView.getSelectionModel().getSelectedItem();
        Student guest = guestListView.getSelectionModel().getSelectedItem();
        if (host != null && guest != null) {
            AssociationStudent assoc = new AssociationStudent(host, guest);
            if (assoc.getScoreAssociation() == null) {
                previewLabel.setText("Association impossible : " + assoc.getInvalidReason());
                previewLabel.setVisible(true);
            } else {
                previewLabel.setVisible(false);
            }
        } else {
            previewLabel.setVisible(false);
        }
    }
}
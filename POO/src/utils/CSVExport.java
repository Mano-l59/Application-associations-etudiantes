/**
 * La classe outil CSVExport permet d'exporter une liste d'appariements"
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 1.0
 */
package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import basicclass.AssociationStudent;

public class CSVExport {

    /**
     * Exporte une liste d'appariements au format CSV.
     * @param associations La liste des AssociationStudent à exporter
     * @param filePath Le chemin du fichier CSV à créer
     * @throws IOException Si une erreur d'écriture survient
     */
    public static void exportMatchingToCsv(List<AssociationStudent> associations, String filePath) throws IOException {
        try (PrintWriter pw = new PrintWriter(filePath+".csv")) {
            pw.println("NOM1;PRENOM1;SCORE;NOM2;PRENOM2;DESCRIPTION");
            for (AssociationStudent assoc : associations) {
                if(assoc.getHost().getId()<0 || assoc.getGuest().getId()<0){
                    continue; // Ignore les étudiants fictifs
                }
                String nom1 = assoc.getHost().getName();
                String prenom1 = assoc.getHost().getForename();
                String score;
                if(assoc.getScoreAssociation() == null){
                    score = "null";
                } else {
                    score = assoc.getScoreAssociation().toString();
                }
                String nom2 = assoc.getGuest().getName();
                String prenom2 = assoc.getGuest().getForename();
                String desc = assoc.describeLevelOfAffinity();
                pw.println(nom1 + ";" + prenom1 + ";" + score + ";" + nom2 + ";" + prenom2 + ";" + desc);
            }
        }
    }
}

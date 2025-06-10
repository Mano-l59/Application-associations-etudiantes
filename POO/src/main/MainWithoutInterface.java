package main;
import basicclass.Student;
import basicclass.AssociationStudent;
import basicclass.Country;
import basicclass.MatchingEnum;
import manager.HistoryManager;
import manager.StudentManager;
import utils.CSVExport;
import algorithm.MatchingSolver;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe utilisatrice de l'application sans interface graphique.
 * Permet de charger un fichier CSV d'étudiants, de les appairer selon différents algorithmes,
 * et de gérer un historique des appariements.
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 4.0
 */
public class MainWithoutInterface {
    public static String csvFileStudent = "students.csv";
    public static String dataHistorique = "POO/data/historique.dat";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager manager = new StudentManager();
        List<Student> students = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        boolean running = true;
        while (running) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Choisir le fichier à charger");
            System.out.println("2) Voir la liste des étudiants chargés");
            System.out.println("3) Exécuter un appariement");
            System.out.println("4) Voir l'historique");
            System.out.println("5) Supprimer l'historique existant");
            System.out.println("6) Quitter");
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();
            switch (choix) {
                case "1":
                    System.out.print("Entrez le chemin du fichier CSV à charger : ");
                    csvFileStudent = scanner.nextLine();
                    try {
                        manager.loadStudentsFromCsv(csvFileStudent);
                        students = manager.getStudents();
                        failed = manager.getFailedStudents();
                        System.out.println("Fichier chargé avec succès (" + students.size() + " étudiants valides, " + failed.size() + " rejetés).");
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement du CSV : " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("\nÉtudiants chargés (" + students.size() + ") :");
                    for (Student s : students) {
                        System.out.println(s.toStringComplete());
                    }
                    System.out.println("\nÉtudiants rejetés (" + failed.size() + ") :");
                    for (String line : failed) {
                        System.out.println(line);
                    }
                    break;

                case "3":
                    if (students.isEmpty()) {
                        System.out.println("Aucun étudiant chargé. Chargez un fichier d'abord.");
                        break;
                    }
                    // Affichage des pays disponibles
                    Set<Country> countries = students.stream()
                            .map(Student::getCountry)
                            .collect(Collectors.toSet());
                    List<Country> countryList = new ArrayList<>(countries);
                    System.out.println("\nPays disponibles :");
                    for (int i = 0; i < countryList.size(); i++) {
                        System.out.println((i + 1) + ". " + countryList.get(i).getFullName());
                    }
                    // Choix des deux pays
                    System.out.print("Choisissez le numéro du pays qui reçoit : ");
                    String inputHost = scanner.nextLine().trim();
                    if (inputHost.isEmpty() || !inputHost.matches("\\d+")) {
                        System.out.println("Entrée invalide. Veuillez entrer un numéro.");
                        break;
                    }
                    int idxHost = Integer.parseInt(inputHost) - 1;

                    System.out.print("Choisissez le numéro du pays qui visite : ");
                    String inputGuest = scanner.nextLine().trim();
                    if (inputGuest.isEmpty() || !inputGuest.matches("\\d+")) {
                        System.out.println("Entrée invalide. Veuillez entrer un numéro.");
                        break;
                    }
                    int idxGuest = Integer.parseInt(inputGuest) - 1;

                    if (idxHost < 0 || idxHost >= countryList.size() || idxGuest < 0 || idxGuest >= countryList.size() || idxHost == idxGuest) {
                        System.out.println("Choix de pays invalide.");
                        break;
                    }
                    Country countryHost = countryList.get(idxHost);
                    Country countryGuest = countryList.get(idxGuest);

                    // Création des listes hosts/guests
                    List<Student> hostsList = students.stream().filter(s -> s.getCountry().equals(countryHost)).collect(Collectors.toList());
                    List<Student> guestsList = students.stream().filter(s -> s.getCountry().equals(countryGuest)).collect(Collectors.toList());

                    if (hostsList.isEmpty() || guestsList.isEmpty()) {
                        System.out.println("Aucun étudiant dans l'un des pays sélectionnés.");
                        break;
                    }
                    Set<Student> hosts = new HashSet<>(hostsList);
                    Set<Student> guests = new HashSet<>(guestsList);

                    HistoryManager historyManager = new HistoryManager();
                    historyManager.loadFromFile(dataHistorique);

                    MatchingSolver solver = new MatchingSolver(hosts, guests,historyManager);

                    // Choix de l'algo
                    System.out.println("Choisissez l'algorithme :");
                    System.out.println("a) HONGROIS_MATCHING (matching parfait à coût minimal)");
                    System.out.println("b) MAX_PAIR_MATCHING (plus grand nombre de paires)");
                    System.out.println("c) Quitter");
                    System.out.print("Votre choix : ");
                    String algoChoix;
                    MatchingEnum algoType=null;
                    boolean algoValide = false;
                    boolean quitté = false;
                    while(!quitté){
                        algoChoix = scanner.nextLine().trim().toLowerCase();
                        if (algoChoix.equals("a")) {
                            algoType = MatchingEnum.HONGROIS_MATCHING;
                            quitté = true;
                            algoValide = true;
                        } else if (algoChoix.equals("b")) {
                            algoType = MatchingEnum.MAX_PAIR_MATCHING;
                            quitté = true;
                            algoValide = true;
                        } else if (algoChoix.equals("c")) {
                            quitté = true;
                            break;
                        } else {
                            System.out.println("Choix d'algorithme invalide.");
                        }
                    }
                    if(!algoValide) {
                        System.out.println("Aucun algorithme sélectionné. Retour au menu principal.");
                        break;
                    }
                    List<AssociationStudent> associations = solver.algorithmMatching(algoType);
                    historyManager.addOrReplaceMatching(countryHost, countryGuest, associations);
                    historyManager.saveToFile(dataHistorique);
                    System.out.println("\nHOST("+ ((Student) hosts.toArray()[0]).getCountry().getFullName()+")                   | Score | GUEST("+((Student) guests.toArray()[0]).getCountry().getFullName()+")                  | Description");
                    System.out.println("------------------------------------------------------------------------------------------");
                    for (AssociationStudent association : associations) {
                        System.out.println(association); // toString tabulaire
                    }
                    
                    System.out.print("Voulez-vous exporter cet appariement au format CSV ? (o/n) : ");
                    String exportChoix = scanner.nextLine().trim().toLowerCase();
                    if (exportChoix.equals("o")) {
                        System.out.print("Entrez le nom du fichier à créer (ex: appariement.csv) : ");
                        String exportFile = scanner.nextLine().trim();
                        try {
                            CSVExport.exportMatchingToCsv(associations, exportFile);
                            System.out.println("Export CSV réussi !");
                        } catch (IOException e) {
                            System.err.println("Erreur lors de l'export : " + e.getMessage());
                        }
                    }
                    break;

                case "4":
                    HistoryManager historyManagerView = new HistoryManager();
                    historyManagerView.loadFromFile(dataHistorique);
                    Map<String, List<AssociationStudent>> historique = historyManagerView.getHistorique();
                    if (historique.isEmpty()) {
                        System.out.println("Aucun historique enregistré.");
                    } else {
                        System.out.println("\n=== Historique des appariements ===");
                        for (Map.Entry<String, List<AssociationStudent>> entry : historique.entrySet()) {
                            String key = entry.getKey();
                            List<AssociationStudent> assocList = entry.getValue();
                            System.out.println("\nAppariement : " + key);
                            System.out.println("HOST | Score | GUEST | Description");
                            System.out.println("-------------------------------------------------------------");
                            for (AssociationStudent assoc : assocList) {
                                System.out.println(assoc.toString());
                            }
                        }
                    }
                    break;

                case "5":
                    HistoryManager historyManagerClear = new HistoryManager();
                    historyManagerClear.clearHistorique();
                    historyManagerClear.saveToFile(dataHistorique);
                    System.out.println("Historique supprimé avec succès.");
                    break;
                case "6":
                    running = false;
                    System.out.println("Au revoir !");
                    break;

                default:
                    System.out.println("Choix invalide.");
            }
        }
    }
}
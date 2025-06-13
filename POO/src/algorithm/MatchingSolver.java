package algorithm;
import basicclass.*;
import manager.HistoryManager;
import utils.HistoryConstraintChecker;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.HopcroftKarpMaximumCardinalityBipartiteMatching;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import java.util.*;
import java.util.stream.Collectors;

/**
 * La classe MatchingSolver est responsable de la résolution du problème de matching entre les étudiants hôtes et invités.
 * Elle construit un graphe biparti et utilise des algorithmes de matching pour trouver les meilleures associations.
 * Cette classe repose sur les algorithmes de Kuhn-Munkres pour le matching parfait de poids minimal
 * et Hopcroft-Karp pour le matching maximum en cardinalité.
 * @author <a>Clément Roty, Mano LEMAIRE, Timothée SERGHERAERT</a>
 * @version 4.0
 */
public class MatchingSolver {

    private Set<Student> hosts;
    private Set<Student> guests;
    private Graph<Student, DefaultWeightedEdge> graph;
    private List<AssociationStudent> associations;
    private List<AssociationStudent> invalidAssociation =new ArrayList<>();
    

    /**
     * Constructeur de la classe MatchingSolver.
     * Il initialise les listes d'hôtes et d'invités, crée un graphe biparti et ajoute les associations possibles en vérifiant les contraintes d'historique.
     * Si le nombre d'hôtes et d'invités n'est pas équilibré, il ajoute des étudiants fictifs pour équilibrer les listes.
     * Utilise le gestionnaire d'historique pour vérifier les contraintes d'association basées sur l'historique des appariements.
     * @param hosts Ensemble des étudiants hôtes
     * @param guests Ensemble des étudiants invités
     * @param historyManager Gestionnaire de l'historique pour vérifier les contraintes
     */
    public MatchingSolver(Set<Student> hosts, Set<Student> guests,HistoryManager historyManager) {
        if(hosts == null || guests == null) {
            throw new IllegalArgumentException("Hosts and guests lists cannot be null.");
        }
        this.associations = new ArrayList<>();
        for (Student hote : hosts) {
            for (Student invite : guests) {
                if(HistoryConstraintChecker.checkHistoryConstraint(hote, invite, historyManager).equals(HistoryConstraintChecker.result.SAME)) {
                    this.associations.add(new AssociationStudent(hote, invite));
                    hosts.remove(hote);
                    guests.remove(invite);
                }
            }
        }
        if(hosts.size() > guests.size()) {
            int diff = hosts.size() - guests.size();
            for (int i = 0; i < diff; i++) {
                guests.add(new Student(i, ((Student) guests.toArray()[0]).getCountry()));
            }
        }
        if(guests.size() > hosts.size()) {
            int diff = guests.size() - hosts.size();
            for (int i = 0; i < diff; i++) {
                hosts.add(new Student(i,((Student) hosts.toArray()[0]).getCountry()));
            }
        }
        this.hosts = hosts;
        this.guests = guests;
        this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        for (Student hote : this.hosts) {
            this.graph.addVertex(hote);
        }
        for (Student invite : this.guests) {
            this.graph.addVertex(invite);
        }

        for (Student hote : this.hosts) {
            for (Student invite : this.guests) {
                    DefaultWeightedEdge edge=this.graph.addEdge(hote, invite);
                    if (edge != null){
                        this.graph.setEdgeWeight(edge, AssociationStudent.doubleTypeScore(hote, invite));
                    }
                
            }
        }
    }

    /**
     * Méthode pour effectuer le matching entre les étudiants hôtes et invités en utilisant l'algorithme spécifié.
     * Elle utilise soit l'algorithme de Kuhn-Munkres pour le matching parfait de poids minimal,
     * soit l'algorithme de Hopcroft-Karp pour le matching maximum en cardinalité.
     * Les associations sont stockées dans la liste d'associations.
     * @param type Le type d'algorithme de matching à utiliser (soit HONGROIS_MATCHING, soit MAX_PAIR_MATCHING).
     * @return La liste des associations d'étudiants résultant du matching
     */
    public List<AssociationStudent> algorithmMatching(MatchingEnum type){
        MatchingAlgorithm<Student, DefaultWeightedEdge> algorithm;
        if(type.equals(MatchingEnum.HONGROIS_MATCHING)){
            algorithm = new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(this.graph,this.hosts, this.guests);
        }else if(type.equals(MatchingEnum.MAX_PAIR_MATCHING)){
            algorithm = new HopcroftKarpMaximumCardinalityBipartiteMatching<>(this.graph, this.hosts, this.guests);
        }else{
            throw new IllegalArgumentException("Type de matching non supporté : " + type);
        }
        
        Set<DefaultWeightedEdge> edges = algorithm.getMatching().getEdges();
        for (DefaultWeightedEdge edge : edges) {
            Student source = graph.getEdgeSource(edge);
            Student target = graph.getEdgeTarget(edge);
            // On suppose que source est un hôte et target un invité, ou l'inverse
            AssociationStudent assoc;
            if (this.hosts.contains(source) && this.guests.contains(target)) {
                assoc = new AssociationStudent(source, target);
            } else if (this.hosts.contains(target) && this.guests.contains(source)) {
                assoc = new AssociationStudent(target, source);
            } else {
                continue;
            }
            // Ajoute uniquement si l'association est valide (scoreAssociation != null et pas fictif)
                if(assoc.getScoreAssociation() == null){
                    this.invalidAssociation.add(assoc);
                }else{
                    this.associations.add(assoc);
                }

        }
        // Après avoir rempli associations et invalidAssociation
        Set<Student> matchedHosts = associations.stream().map(AssociationStudent::getHost).collect(Collectors.toSet());
        Set<Student> matchedGuests = associations.stream().map(AssociationStudent::getGuest).collect(Collectors.toSet());

        List<Student> unmatchedHosts = hosts.stream().filter(h -> !matchedHosts.contains(h)).toList();
        List<Student> unmatchedGuests = guests.stream().filter(g -> !matchedGuests.contains(g)).toList();

        if (unmatchedHosts.size() == 1 && unmatchedGuests.size() == 1) {
            Student h = unmatchedHosts.get(0);
            Student g = unmatchedGuests.get(0);
            AssociationStudent assoc = new AssociationStudent(h, g);
            if (assoc.getScoreAssociation() != null) {
                associations.add(assoc);
            } else {
                invalidAssociation.add(assoc);
            }
        }
        return this.associations;
    }

    /**
     * Méthode pour obtenir le set des associations d'étudiants.
     * (Attention, cette liste peut contenir des étudiants fictifs si le nombre d'hôtes et d'invités n'est pas équilibré)
     * @return La liste des étudiants hotes.
     */
    public Set<Student> getHostsListe(){
        return this.hosts;
    }

    /**
     * Méthode pour obtenir le set des invités.
     * (Attention, cette liste peut contenir des étudiants fictifs si le nombre d'hôtes et d'invités n'est pas équilibré)
     * @return Le set des étudiants invités
     */
    public Set<Student> getGuestsListe(){
        return this.guests;
    }
    
    /**
     * Méthode pour obtenir la liste des associations d'étudiants invalides.
     * @return La liste des associations d'étudiants invalides
     */
    public List<AssociationStudent> getAssociationsInvalid() {
        return this.invalidAssociation;
    }
    /**
     * Méthode pour obtenir la liste des associations d'étudiants.
     * @return La liste des associations d'étudiants
     */
    public List<AssociationStudent> getAssociations() {
        return this.associations;
    }

    /**
     * Méthode pour obtenir le graphe biparti de manière textuel et en forme.
     * @return Le graphe biparti des étudiants hôtes et invités
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Vue du graphe biparti :\n");
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            Student source = graph.getEdgeSource(edge);
            Student target = graph.getEdgeTarget(edge);
            double weight = graph.getEdgeWeight(edge);
            sb.append(source)
            .append(" <---(")
            .append(weight)
            .append(")---> ")
            .append(target)
            .append("\n");
        }
        return sb.toString();
    }
    
}

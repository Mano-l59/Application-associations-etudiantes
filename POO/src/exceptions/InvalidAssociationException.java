package exceptions;

/**
 * Exception levée lorsqu'une association entre un hôte et un invité est invalide
 * en raison de contraintes rédhibitoires.
 * Cette classe sera utilisée pour signaler des erreurs lors de la création d'un appariement forcé par l'utilisateur, lors de la verion avec interface graphique.
 */
public class InvalidAssociationException extends Exception {
    public InvalidAssociationException(String message) {
        super(message);
    }

    public InvalidAssociationException(String message, Throwable cause) {
        super(message, cause);
    }
}

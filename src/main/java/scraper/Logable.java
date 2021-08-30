package scraper;

public interface Logable {
    boolean logIn(Credentials credentials);
    void logOut();
}

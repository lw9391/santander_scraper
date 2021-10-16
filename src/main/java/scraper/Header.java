package scraper;

public enum Header {

  ACCEPT("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
  ACCEPT_LANGUAGE("Accept-Language", "pl,en-US;q=0.7,en;q=0.3"),
  USER_AGENT("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0");

  public final String name;
  public final String value;

  Header(String name, String value) {
    this.name = name;
    this.value = value;
  }

}
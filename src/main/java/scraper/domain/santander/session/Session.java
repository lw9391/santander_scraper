package scraper.domain.santander.session;

import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.http.Response;
import scraper.domain.santander.Credentials;

import java.util.Date;
import java.util.List;

import static scraper.domain.santander.session.HttpResponseParser.*;

public class Session {

  private final HttpExchanges exchanges;

  public Session(HttpExchanges exchanges) {
    this.exchanges = exchanges;
  }

}
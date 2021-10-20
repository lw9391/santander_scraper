package scraper.domain;

import java.util.List;

public interface View {

  String readSmsCode();

  void display(List<AccountDetails> accountsList);

}

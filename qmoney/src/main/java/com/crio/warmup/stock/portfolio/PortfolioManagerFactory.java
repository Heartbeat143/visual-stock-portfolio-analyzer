
package com.crio.warmup.stock.portfolio;

// import com.crio.warmup.stock.dto.AnnualizedReturn;
// import com.crio.warmup.stock.dto.PortfolioTrade;
// import java.time.LocalDate;
// import java.util.List;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {

  // TODO_CRIO_TASK_MODULE_REFACTOR
  // Implement the method in such a way that it will return new Instance of
  // PortfolioManager using RestTemplate provided.
  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {

    //uper wale fuctions call krne hain
    // call kr baap ko isliy uska object bhej
    return new PortfolioManagerImpl(restTemplate);
    
    // return null;
  }





}

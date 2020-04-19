package com.crio.warmup.stock.portfolio;

// import static java.time.temporal.ChronoUnit.DAYS;
// import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;

// import com.crio.warmup.stock.dto.TiingoCandle;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
// import java.util.ArrayList;
// import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Comparator;
import java.util.List;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
// import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {



  public RestTemplate restTemplate;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility

  public PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // module 4 ka portfolios
  // TODO_CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF

  // module 3 ka catch up part isliy 'For' add kiya last main
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn (List<PortfolioTrade> portfolioTrades,
    LocalDate endDate) throws Exception {
      
      List<Candle> result1;
      Double buyPrice;
      Double sellPrice;
      Double totalReturns;
      
      AnnualizedReturn[] calculatedAnnArray = new AnnualizedReturn[portfolioTrades.size()];
      LocalDate resDate;
      LocalDate portfolioIDate;
  
      PortfolioTrade[] portfolioArray = new PortfolioTrade[portfolioTrades.size()];
      portfolioTrades.toArray(portfolioArray);
      for (int i = 0; i < portfolioTrades.size(); i++) {
        sellPrice = -1.0;
        buyPrice = -1.0;
        
        
            // "https://api.tiingo.com/tiingo/daily/" + portfolioTrades[i].getSymbol()
            // + "/prices" + "?startDate=" + portfolioTrades[i].getPurchaseDate()
            // + "&endDate=" + args[1] + "&token="
            // + "536115fa6bc01e070077d67ad6c553d9c1f1a153";
       
        
        
        result1 = getStockQuote(portfolioArray[i].getSymbol(), portfolioArray[i].getPurchaseDate(), endDate);
        Candle[] result2 = new Candle[result1.size()];
        result1.toArray(result2);
        portfolioIDate = portfolioArray[i].getPurchaseDate();
        try {
          // if (result1 != null) {
          for (int j = 0; j < result1.size(); j++) {
            resDate = result2[j].getDate();
  
            if (resDate.compareTo(portfolioIDate) == 0) {
              buyPrice = result2[j].getOpen();
              break;
            }
          }
  
          for (int j = 0; j < result1.size(); j++) {
            resDate = result2[j].getDate();
  
            if (resDate.compareTo(endDate) == 0) {
              sellPrice = result2[j].getClose();
              break;
            }
          }
          if (sellPrice == -1.0) {
            sellPrice = result2[result1.size() - 1].getClose();
          }
  
        } catch (Exception e) {
          // TODO_handle exception
          continue;
        }
      
        long totalNumDays = ChronoUnit.DAYS.between(portfolioArray[i].getPurchaseDate(), endDate);
        
        Double tot = (double) (totalNumDays / 365.24);
        totalReturns = (sellPrice - buyPrice) / buyPrice;
        Double annualizedReturns;
        annualizedReturns = Math.pow((1 + totalReturns), (1 / tot)) - 1;
        calculatedAnnArray[i] = new AnnualizedReturn(portfolioArray[i].getSymbol(), annualizedReturns, totalReturns);
      }
      List<AnnualizedReturn> calculatedAnnList = Arrays.asList(calculatedAnnArray);
      for (int i = 0; i < calculatedAnnList.size(); i++) {  
        for (int j = 1; j < (calculatedAnnList.size() - i); j++) {  
          if (calculatedAnnList.get(j - 1).getAnnualizedReturn() <= calculatedAnnList.get(j).getAnnualizedReturn()) {
            AnnualizedReturn temp;
            temp = calculatedAnnList.get(j - 1); 
            calculatedAnnList.set(j - 1, calculatedAnnList.get(j)); 
            calculatedAnnList.set(j, temp);
          }           
        }    
      }
      return calculatedAnnList;
    }

  // abhi jrurat nhi hai  
  // private Comparator<AnnualizedReturn> getComparator() {
    // return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  // }

  //CHECKSTYLE:OFF

  // module 4 ka portfolios
  // TODO_CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws Exception {
    String url = buildUri(symbol, from, to);
    try {
      Candle[] result =  restTemplate.getForObject(url, TiingoCandle[].class);
      return Arrays.asList(result); 
    } catch (NullPointerException e) {
      e.printStackTrace();
      return null;
    }
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

      // String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
      //     + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
  
      String uriTemplates = 
          "https://api.tiingo.com/tiingo/daily/" + symbol
          + "/prices" + "?startDate=" + startDate
          + "&endDate=" + endDate + "&token="
          + "536115fa6bc01e070077d67ad6c553d9c1f1a153";
    return uriTemplates;
  }
}

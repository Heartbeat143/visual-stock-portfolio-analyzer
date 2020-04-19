
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
// import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
// import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Collections;
// import java.util.Comparator;
import java.util.List;
import java.util.UUID;
// import java.util.logging.Level;
import java.util.logging.Logger;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
// import javax.sound.sampled.Port;
import org.apache.logging.log4j.ThreadContext;
// import org.springframework.web.client.RestTemplate;
// import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.*;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // module 1 ka todo iska fuction niche wala hai mainreadfile
  //  Read the json file provided in the argument[0]. The file will be avilable in the classpath.
  //  1. Use #resolveFileFromResources to get actual file from classpath.
  //  2. parse the json file using ObjectMapper provided with #getObjectMapper,
  //  and extract symbols provided in every trade.
  //  return the list of all symbols in the same order as provided in json.
  //  Test the function using gradle commands below
  //   ./gradlew run --args="trades.json"
  //  Make sure that it prints below String on the console -
  //  ["AAPL","MSFT","GOOGL"]
  //  Now, run
  //  ./gradlew build and make sure that the build passes successfully
  //  There can be few unused imports, you will need to fix them to make the build pass.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    PortfolioTrade[] user = getObjectMapper()
    .readValue(file, PortfolioTrade[].class);
    List<String> expected = new ArrayList<>();
    for (int i = 0;i < user.length;i++) {
      expected.add(user[i].getSymbol());
    }
    return expected;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  // m2
  // module two ka todo crio jo main read quotes fuction hai 
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> mainReadQuotes(String[] args) throws IOException, Exception {
    File resolveFileFromResources = resolveFileFromResources(args[0]);
    PortfolioTrade[] portfolioTrades = getObjectMapper()
    .readValue(resolveFileFromResources, PortfolioTrade[].class);
    List<String> symbols = new ArrayList<>();

    for (int i = 0; i < portfolioTrades.length; i++) {
      symbols.add(portfolioTrades[i].getSymbol());
    }

    List<LocalDate> purDate = new ArrayList<>();
    for (int i = 0; i < portfolioTrades.length; i++) {
      purDate.add(portfolioTrades[i].getPurchaseDate());
    }
    TiingoCandle[] result;
    RestTemplate restTemplate = new RestTemplate();
    List<Double> finalList = new ArrayList<>();
    System.out.print(purDate);
    for (int i = 0; i < portfolioTrades.length; i++) {
      String uri = 
          "https://api.tiingo.com/tiingo/daily/" + symbols.get(i)
          + "/prices" + "?startDate=" + purDate.get(i)
          + "&endDate=" + args[1] + "&token="
          + "536115fa6bc01e070077d67ad6c553d9c1f1a153";

      result = restTemplate.getForObject(uri, TiingoCandle[].class);
      if (result != null) {
        finalList.add(result[result.length - 1].getClose());
      }
    }
    for (int i = 0; i < symbols.size(); i++) {  
      for (int j = 1; j < (symbols.size() - i); j++) {  
        if (finalList.get(j - 1) >= finalList.get(j)) {  
          //swap elements  
          String temp;
          temp = symbols.get(j - 1);  
          symbols.set(j - 1, symbols.get(j)); 
          symbols.set(j, temp); 
          Double temp1;
          temp1 = finalList.get(j - 1);  
          finalList.set(j - 1, finalList.get(j)); 
          finalList.set(j, temp1); 
        }           
      }  
    }
    return symbols;
  }

  // TODO_CRIO_TASK_MODULE_CALCULATIONS
  // for module 3
  //  Copy the relevant code from #mainReadQuotes to parse the Json into PortfolioTrade list and
  //  Get the latest quotes from TIingo.
  //  Now That you have the list of PortfolioTrade And their data,
  //  With this data, Calculate annualized returns for the stocks provided in the Json
  //  Below are the values to be considered for calculations.
  //  buy_price = open_price on purchase_date and sell_value = close_price on end_date
  //  startDate and endDate are already calculated in module2
  //  using the function you just wrote #calculateAnnualizedReturns
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.



  // TODO_CRIO_TASK_MODULE_REFACTOR
  // for module 4
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory,
  //  Create PortfolioManager using PortfoliomanagerFactory,
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.
  //  Test the same using the same commands as you used in module 3
  //  use gralde command like below to test your code
  //  ./gradlew run --args="trades.json 2020-01-01"
  //  ./gradlew run --args="trades.json 2019-07-01"
  //  ./gradlew run --args="trades.json 2019-12-03"
  //  where trades.json is your json file

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException, Exception {
    File resolveFileFromResources = resolveFileFromResources(args[0]);
    PortfolioTrade[] portfolioTrades = getObjectMapper()
      .readValue(resolveFileFromResources, PortfolioTrade[].class);
    LocalDate enddate = LocalDate.parse(args[1]);

    //pichhla code
    List<String> symbols = new ArrayList<>();
    for (int i = 0; i < portfolioTrades.length; i++) {
      symbols.add(portfolioTrades[i].getSymbol());
    }
    
    List<LocalDate> purDate = new ArrayList<>();
    for (int i = 0; i < portfolioTrades.length; i++) {
      purDate.add(portfolioTrades[i].getPurchaseDate());
    }
    TiingoCandle[] result;
    List<Double> finall = new ArrayList<>();
    List<Double> startt = new ArrayList<>();
    System.out.print(purDate);
    for (int i = 0; i < portfolioTrades.length; i++) {
      String uri = 
          "https://api.tiingo.com/tiingo/daily/" + symbols.get(i)
          + "/prices" + "?startDate=" + purDate.get(i)
          + "&endDate=" + args[1] + "&token="
          + "536115fa6bc01e070077d67ad6c553d9c1f1a153";
         
      RestTemplate restTemplate = new RestTemplate();
     
    
      result = restTemplate.getForObject(uri, TiingoCandle[].class);
      if (result != null) {
        if (enddate.compareTo(result[result.length - 1].getDate()) != 0) {
          finall.add(result[result.length - 2].getClose());
        } else {
          finall.add(result[result.length - 1].getClose());
        }
        startt.add(result[0].getOpen());
      }
    }
    //pichla khatm
        
    AnnualizedReturn annual;
    List<AnnualizedReturn> res = new ArrayList<>();
    for (int i = 0; i < symbols.size(); i++) {
      Double buyPrice = startt.get(i);
      Double sellPrice = finall.get(i);
      annual = calculateAnnualizedReturns(enddate, portfolioTrades[i], buyPrice, sellPrice);
      res.add(annual);
    } 

    //sortkrna hai
    for (int i = 0; i < res.size(); i++) {  
      for (int j = 1; j < (res.size() - i); j++) {  
        if (res.get(j - 1).getAnnualizedReturn() <= res.get(j).getAnnualizedReturn()) {
          AnnualizedReturn temp;
          temp = res.get(j - 1); 
          res.set(j - 1, res.get(j)); 
          res.set(j, temp);
        }           
      }    
    }
        
    return res;
  }


  // TODO_CRIO_TASK_MODULE_CALCULATIONS
  // module three part 
  //  annualized returns should be calculated in two steps -
  //  1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  //  Store the same as totalReturns
  //  2. calculate extrapolated annualized returns by scaling the same in years span. The formula is
  //  annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //  Store the same as annualized_returns
  //  return the populated list of AnnualizedReturn for all stocks,
  //  Test the same using below specified command. The build should be successful
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    Double totalreturn = (sellPrice - buyPrice) / buyPrice;
    LocalDate buydate = trade.getPurchaseDate();
    Double d = 1 + totalreturn;
    Double years = ChronoUnit.DAYS.between(buydate, endDate) / 365.24;
    Double year = 1 / years;
    Double annualreturn = Math.pow(d, year) - 1;
    return new AnnualizedReturn(trade.getSymbol(),annualreturn, totalreturn);
  } 

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/"
        + "crio-user/workspace/"
        + "sukhdevsingh2192-ME_QMONEY/"
        + "qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = 
        "com.fasterxml.jackson.databind.ObjectMapper@373ebf74";
    String functionNameFromTestFileInStackTrace = 
        "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[]{
        valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }
  // modile 3 tk ka main jo unko call krta hai
  // public static void main(String[] args) throws Exception {
  //   Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
  //   ThreadContext.put("runId", UUID.randomUUID().toString());
  //   printJsonObject(mainReadFile(args));

  //   printJsonObject(mainCalculateSingleReturn(args));
  //   printJsonObject(mainReadQuotes(args));
    
  // }  
  //  Confirm that you are getting same results as in Module3.
  // module 4 se related function
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
    LocalDate endDate = LocalDate.parse(args[1]);
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);
    File resolveFileFromResources = resolveFileFromResources(args[0]);
    PortfolioTrade[] portfolioTrades = objectMapper
      .readValue(resolveFileFromResources, PortfolioTrade[].class);
           
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}


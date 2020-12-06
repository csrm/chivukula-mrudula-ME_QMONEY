package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
// import java.sql.Date;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory; 	
import java.nio.file.Files;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;




public class PortfolioManagerApplication {

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    List<String> list = new ArrayList<String>();
    PortfolioTrade[] portfolioTrade = objectMapper.readValue(file, PortfolioTrade[].class);
    for (PortfolioTrade pf: portfolioTrade) {
      list.add(pf.getSymbol());
    }
    return list;
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

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "chivukula-mrudula-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@4135c3b";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile";
    String lineNumberFromTestFileInStackTrace = "22";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }

  public static List<String> mainReadQuotes(String[] args) throws NullPointerException, 
         IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    Map<Double,List<String>> list = new TreeMap<Double,List<String>>();
    List<String> symbols = new ArrayList<>();
    if (objectMapper != null) {
      PortfolioTrade[] ptrades = objectMapper.readValue(file, PortfolioTrade[].class);
      if (ptrades != null) {
        for (PortfolioTrade ptrade : ptrades) {
          String symbol = ptrade.getSymbol();
          LocalDate purchaseDate = ptrade.getPurchaseDate();
          String token = "93c80accc7b3868d81c216fdbc53d889868fdd77";
          String uri = "https://api.tiingo.com/tiingo/daily/" + symbol 
                      + "/prices?startDate=" + purchaseDate + "&endDate=" + args[1]
                      + "&token=" + token;
          ResponseEntity<TiingoCandle[]> responses;
          try {
            responses = (new RestTemplate())
                                    .getForEntity(uri, TiingoCandle[].class);
            if (responses.getBody().length == 0) {
              throw new RuntimeException();
            }
            for (TiingoCandle response : responses.getBody()) {
              if (response.getDate().toString().equals(args[1])) {
                if (list.get(response.getClose()) == null) {
                  List<String> value = new ArrayList<>();
                  value.add(symbol);
                  list.put(response.getClose(),value);
                } else {
                  (list.get(response.getClose())).add(symbol);
                } 
                break;
              }
            }
          } catch (NullPointerException e) {
            throw new NullPointerException();
          }
        }
      }
      for (Map.Entry<Double,List<String>> entry : list.entrySet()) {
        for (String s : entry.getValue()) {
          symbols.add(s);
        }
      }
      list.clear();
    }
    return symbols;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException, ParseException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    List<AnnualizedReturn> list = new ArrayList<>();
    if (objectMapper != null) {
      PortfolioTrade[] ptrades = objectMapper.readValue(file, PortfolioTrade[].class);
      if (ptrades != null) {
        for (PortfolioTrade ptrade : ptrades) {
          String symbol = ptrade.getSymbol();
          LocalDate purchaseDate = ptrade.getPurchaseDate();
          String token = "93c80accc7b3868d81c216fdbc53d889868fdd77";
          String uri = "https://api.tiingo.com/tiingo/daily/" + symbol 
                      + "/prices?startDate=" + purchaseDate + "&endDate=" + args[1]
                      + "&token=" + token;
          ResponseEntity<TiingoCandle[]> responses;
          try {
            responses = (new RestTemplate())
                              .getForEntity(uri, TiingoCandle[].class);
            if (responses.getBody().length == 0) {
              throw new RuntimeException();
            }
            int flag = 0;
            String date = ptrade.getPurchaseDate().toString();
            double sellPrice = 0;
            double buyPrice = 0;
            int foundValue = 0;
            while (true) {
              if (flag == 2) {
                break;
              }
              for (TiingoCandle response : responses.getBody()) {
                if (response.getDate().toString().equals(date)) {
                  if (flag == 0) {
                    buyPrice = response.getOpen();
                    flag = 1;
                    foundValue = 1;
                    date = args[1];
                    break;
                  } else if (flag == 1) {
                    if (response.getClose() != 0) {
                      sellPrice = response.getClose();
                      foundValue = 1;
                      flag = 2;
                    } else {
                      foundValue = 0;
                    }
                    break;
                  }
                } else {
                  foundValue = 0;
                }
              }
              if (foundValue == 0 && flag == 1) {
                int millis = 1000 * 60 * 60 * 24;
                date = new Date(new SimpleDateFormat("yyyy-mm-dd")
                                     .parse(date).getTime() - millis).toString();
              }
            }
            list.add(calculateAnnualizedReturns(LocalDate.parse(date), 
                                      ptrade, buyPrice, sellPrice));  
          } catch (NullPointerException e) {
            throw new NullPointerException();
          }
        }
        list.sort(Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed());
      }
    }
    return list;
  }

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    double totalReturns = (sellPrice - buyPrice) / buyPrice;
    Period period = Period.between(trade.getPurchaseDate(), endDate);
    double tperiod = period.getYears() + (period.getMonths() / 12.0) 
                     + (period.getDays() / 365.0);
    double annualizedReturns = Math.pow((1 + totalReturns), 
                                         (1 / tperiod)) - 1;
    return new AnnualizedReturn(trade.getSymbol(), 
                                annualizedReturns, totalReturns);
  }
// TODO: CRIO_TASK_MODULE_REFACTOR
//  Once you are done with the implementation inside PortfolioManagerImpl and
//  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
//  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
//  call the newly implemented method in PortfolioManager to calculate the annualized returns.

// Note:
// Remember to confirm that you are getting same results for annualized returns as in Module 3.
public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
throws Exception {
 LocalDate endDate = LocalDate.parse(args[1]);
 PortfolioManager portfolioManager = PortfolioManagerFactory.
                                     getPortfolioManager(new RestTemplate());
 ObjectMapper objectMapper = getObjectMapper();
 PortfolioTrade[] portfolioTrades = objectMapper.readValue(resolveFileFromResources(args[0]), PortfolioTrade[].class);
 return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
}

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());    
    ThreadContext.put("runId", UUID.randomUUID().toString());
    String arg[] = args;
    if(args.length != 2) {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    Date date = new Date();
	    System.out.println();
      arg[1] = formatter.format(date);
    }
  
    printJsonObject(mainReadFile(args));
    
    printJsonObject(mainReadQuotes(args));
    
    printJsonObject(mainCalculateSingleReturn(args));
    
    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}


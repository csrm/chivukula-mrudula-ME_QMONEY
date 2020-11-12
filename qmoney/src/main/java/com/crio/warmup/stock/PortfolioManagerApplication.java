package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;




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

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadFile(args));


    printJsonObject(mainReadQuotes(args));


  }
}


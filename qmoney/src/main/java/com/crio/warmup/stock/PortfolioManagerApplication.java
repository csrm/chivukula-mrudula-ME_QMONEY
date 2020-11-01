
package com.crio.warmup.stock;

// import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
// import java.nio.file.Files;
import java.nio.file.Paths;
// import java.time.LocalDate;
// import java.time.temporal.ChronoUnit;
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
import org.apache.logging.log4j.ThreadContext;
// import org.springframework.core.io.DefaultResourceLoader;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.web.client.RestTemplate;


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


  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.





  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



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


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.



  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadFile(args));



  }
}


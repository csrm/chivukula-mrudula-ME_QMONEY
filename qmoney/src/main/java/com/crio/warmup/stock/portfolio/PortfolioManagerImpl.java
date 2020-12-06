
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    List<AnnualizedReturn> list = new ArrayList<>();
    for (PortfolioTrade ptrade : portfolioTrades) {
      String symbol = ptrade.getSymbol();
      LocalDate startDate = ptrade.getPurchaseDate();
      try {
        List<Candle> responses = getStockQuote(symbol, startDate, endDate);
        int flag = 0;
        String date = ptrade.getPurchaseDate().toString();
        double sellPrice = 0;
        double buyPrice = 0;
        int foundValue = 0;
        while (true) {
          if (flag == 2) {
            break;
          }
          for (Candle response : responses) {
            if (response.getDate().toString().equals(date)) {
              if (flag == 0) {
                buyPrice = response.getOpen();
                flag = 1;
                foundValue = 1;
                date =  endDate.toString();
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
            try {
              date = new Date(new SimpleDateFormat("yyyy-mm-dd")
                                 .parse(date).getTime() - millis).toString();
            } catch (ParseException pe) {
              pe.printStackTrace();
            }
            
          }
        }
        
        double totalReturns = (sellPrice - buyPrice) / buyPrice;
        Period period = Period.between(startDate, endDate);
        double tperiod = period.getYears() + (period.getMonths() / 12.0) 
                                           + (period.getDays() / 365.0);
        double annualizedReturns = Math.pow((1 + totalReturns), 
                                            (1 / tperiod)) - 1; 
        list.add(new AnnualizedReturn(symbol, annualizedReturns, totalReturns));
        
      } catch (NullPointerException e) {
        throw new NullPointerException();
      } catch (JsonProcessingException je) {
        throw new RuntimeException();
      }
      
    }
    list.sort(getComparator());
    return list;
  }



  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
     String uri = buildUri(symbol, from, to);
     ResponseEntity<TiingoCandle[]> responseEntity= restTemplate.getForEntity(uri, 
                                   TiingoCandle[].class);
     return Arrays.asList(responseEntity.getBody());
     //return null;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String token = "93c80accc7b3868d81c216fdbc53d889868fdd77";
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+ symbol + "/prices?"
            + "startDate=" + startDate + "&endDate=" + endDate + "&token="+ token;
       return uriTemplate;
  }
}

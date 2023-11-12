package com.example.newMock.Controller;


import com.example.newMock.Model.RequestDTO;
import com.example.newMock.Model.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Random;

@RestController
public class MainController {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    ObjectMapper mapper = new ObjectMapper();


    @PostMapping(
            value = "/info/postBalances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Object postBalances(@RequestBody RequestDTO requestDTO) {
        {




            try {
                String clientId = requestDTO.getClientId();
                char firstDigit = clientId.charAt(0);
                BigDecimal maxLimit;
                String currency;

                Random random = new Random();
                BigDecimal randomBalance;


                if (firstDigit == '8') {
                    maxLimit = new BigDecimal(2000.00);
                   currency = "US";
                    randomBalance = BigDecimal.valueOf(random.nextInt(2000));
                } else if (firstDigit == '9') {
                    maxLimit = new BigDecimal(1000.00);
                    currency = "EU";
                   randomBalance = BigDecimal.valueOf(random.nextInt(1000));
                } else {
                    maxLimit = new BigDecimal(10000);
                    currency = "RU";
                   randomBalance = BigDecimal.valueOf(random.nextInt(10000));
                }



                String RqUID = requestDTO.getRqUID();
                ResponseDTO responseDTO = new ResponseDTO();

                responseDTO.setRqUID(RqUID);
                responseDTO.setClientId(clientId);
                responseDTO.setAccount(requestDTO.getAccount());
                responseDTO.setCurrency(currency);
                responseDTO.setBalance(randomBalance);
                responseDTO.setMaxLimit(maxLimit);

                log.error("**** Запрос ****" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));
                log.error("**** Ответ ****" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));

                return responseDTO;
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }
}

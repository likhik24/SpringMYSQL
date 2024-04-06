package com.example.javadb.controller;

import com.example.javadb.model.Quote;
import com.example.javadb.repository.QuoteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class QuoteController {
    @Autowired
    private static final Logger LOG = LoggerFactory.getLogger(QuoteController.class);

    private final String PHONE_NUMBER = System.getenv("PHONE_NUMBER");

    // constructor
    public QuoteController(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
        
    }
    private final QuoteRepository quoteRepository;

    @GetMapping("/quotes")
    public List<Quote> getQuotes(@RequestParam("search") Optional<String> searchParam){
        return searchParam
                .map(quoteRepository::getContainingQuote)
                .orElse(quoteRepository.findAll());
    }

    @GetMapping("/quotes/{quoteId}" )
    public ResponseEntity<String> readQuote(@PathVariable("quoteId") Long id) {
        return ResponseEntity.of(quoteRepository.findById(id).map( Quote::getQuote ));
    }

    @PostMapping("/quotes")
    public Quote addQuote(@RequestBody String quote) {
        Quote q = new Quote();
        q.setQuote(quote);
        return quoteRepository.save(q);
    }

    @RequestMapping(value="/quotes/{quoteId}", method=RequestMethod.DELETE)
    public void deleteQuote(@PathVariable(value = "quoteId") Long id) {
        quoteRepository.deleteById(id);
    }
 
    @PutMapping("/quotes/{quoteId}")
    public Quote updateQuote(@RequestBody String quote, @PathVariable(value = "quoteId") Long id) {
        Quote q = new Quote();
        q.setId(id);
        q.setQuote(quote);
        return quoteRepository.save(q);
    }


    @GetMapping(value = "/sms")
    @ResponseBody
    public String sendSMS() {
        long min = 6;
        long max = 12;
        long random = (long)(Math.random()*(max-min+1)+min);

        Quote quote = quoteRepository.findById(random).get();
        String returnString = quote.getQuote();
        
        return " sent successfully";
    }
}

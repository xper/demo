
package com.stk.demo.bts;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/bts")
@RequiredArgsConstructor
public class BtsController {
    
    private final BtsService btsService;

    @GetMapping
    public String getBts() {
        log.info("getBts");
        return btsService.retrieveBts();
    }
    
}

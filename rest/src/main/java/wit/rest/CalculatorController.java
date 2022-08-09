package wit.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wit.calculator.Calculator;
import wit.config.MessageConfig;

/**
 * API Controller
 */

@RestController
@RequestMapping("/")
public class CalculatorController {
    private double result;
    private long id;
    private int SLEEP = 150;

    @Autowired
    private RabbitTemplate template;

    public CalculatorController() {
        this.result = 0;
        this.id = 0;
    }

    Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    // Sum operation
    @RequestMapping(value = "/sum", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> getSum(@RequestBody Calculator calculator) throws InterruptedException {
        logger.trace("Add method access");
        calculator.setOperation("sum");
        sendMessage(calculator);;
        HttpHeaders responseHeaders = setHeaders();

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("result " + getResult());
    }

    // Sub operation
    @RequestMapping(value = "/sub", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> getSub(@RequestBody Calculator calculator) throws InterruptedException {
        logger.trace("Sub method access");
        calculator.setOperation("sub");
        sendMessage(calculator);
        HttpHeaders responseHeaders = setHeaders();

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("result " + getResult());
    }

    // Mul operation
    @RequestMapping(value = "/mul", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> getMul(@RequestBody Calculator calculator) throws InterruptedException {
        logger.trace("Mul method access");
        calculator.setOperation("mul");
        sendMessage(calculator);;
        HttpHeaders responseHeaders = setHeaders();

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("result " + getResult());
    }

    // Div operation
    @RequestMapping(value = "/div", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> getDiv(@RequestBody Calculator calculator) throws InterruptedException {
        logger.trace("Div method access");
        calculator.setOperation("div");
        sendMessage(calculator);
        HttpHeaders responseHeaders = setHeaders();

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("result " + getResult());
    }



    // function that sends message to the queue
    public void sendMessage(Calculator calculator) throws InterruptedException {
        template.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.QUEUE, calculator);
        Thread.sleep(SLEEP);
    }

    public HttpHeaders setHeaders(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("ID-request",
                String.valueOf(getId()));

        return responseHeaders;
    }


    // Get the result from the queue
    @RabbitListener(queues = MessageConfig.QUEUE_FINAL)
    public void consumeMessageFromQueue(Calculator result) {
        setResult(result.getResult());
        setId(result.getId());
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

package wit.persistence;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import wit.calculator.Calculator;
import wit.config.MessageConfig;

@Component
public class DbSeeder implements CommandLineRunner {
    private CalculatorRepository calculatorRepository;

    @Autowired
    private RabbitTemplate template;

    public DbSeeder(CalculatorRepository calculatorRepository) {
        this.calculatorRepository = calculatorRepository;
    }

    @RabbitListener(queues = MessageConfig.QUEUE)
    public void consumeMessageFromQueue(Calculator result) {

        switch(result.getOperation()) {
            case "sum":
                double sum = result.getA() + result.getB();
                result.setResult(sum);
                break;
            case "sub":
                double sub = result.getA() - result.getB();
                result.setResult(sub);
                break;
            case "mul":
                double mul = result.getA() * result.getB();
                result.setResult(mul);
                break;
            case "div":
                double div = result.getA() / result.getB();
                result.setResult(div);
                break;
            default:
                System.out.println("Invalid op");
        }

        // Store info in the database
        this.calculatorRepository.save(result);

        // send the message to the final queue
        template.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.QUEUE_FINAL, result);
    }


    public void run(String... strings) throws Exception {
        //Calculator first = new Calculator(2,3);
        //this.calculatorRepository.save(first);
    }
}

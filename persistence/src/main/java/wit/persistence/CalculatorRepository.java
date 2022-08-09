package wit.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wit.calculator.Calculator;

@Repository
public interface CalculatorRepository extends JpaRepository<Calculator, Long> {
}

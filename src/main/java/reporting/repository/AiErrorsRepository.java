package reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import reporting.model.ReportingAiError;

public interface AiErrorsRepository extends JpaRepository<ReportingAiError, Long> {

}

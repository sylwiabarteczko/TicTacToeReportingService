package reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import reporting.model.ReportingUser;

import java.util.List;

public interface UsersRepository extends JpaRepository<ReportingUser, Long> {

    @Query("SELECT MONTH(u.registeredAt), COUNT(u) FROM ReportingUser u GROUP BY MONTH(u.registeredAt)")
    List<Object[]> countByMonth();
}

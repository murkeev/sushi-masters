package murkeev.repository;

import murkeev.model.Sushi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SushiRepository extends JpaRepository<Sushi, Long> {
}

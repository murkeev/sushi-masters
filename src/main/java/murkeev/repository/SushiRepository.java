package murkeev.repository;

import murkeev.enums.SushiCategory;
import murkeev.model.Sushi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SushiRepository extends JpaRepository<Sushi, Long> {
    List<Sushi> findByCategory(SushiCategory category);
}
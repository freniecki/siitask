package pl.freniecki.siitask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.freniecki.siitask.model.EventAccount;

@Repository
public interface EventAccountRepository extends JpaRepository<EventAccount, Long> {
}

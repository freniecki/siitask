package pl.freniecki.siitask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.freniecki.siitask.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}

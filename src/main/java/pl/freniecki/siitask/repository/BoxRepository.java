package pl.freniecki.siitask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.freniecki.siitask.model.Box;

import java.util.UUID;

@Repository
public interface BoxRepository extends JpaRepository<Box, UUID> {

}

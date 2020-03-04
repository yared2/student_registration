package cs544_2020_01_light_attendanceproject.dao;

import cs544_2020_01_light_attendanceproject.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}

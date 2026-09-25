package eu.iu3cyv.status.monitor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    List<Monitor> findByEnabledTrueOrderByDisplayOrderAscNameAsc();

    List<Monitor> findAllByOrderByDisplayOrderAscNameAsc();
}

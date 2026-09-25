package eu.iu3cyv.status.monitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

    List<CheckResult> findByMonitorIdInAndCheckedAtAfterOrderByCheckedAt(List<Long> monitorIds, Instant after);

    @Modifying
    @Query("delete from CheckResult c where c.checkedAt < :before")
    int deleteOlderThan(Instant before);
}

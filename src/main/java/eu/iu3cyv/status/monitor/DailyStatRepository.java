package eu.iu3cyv.status.monitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

    Optional<DailyStat> findByMonitorIdAndDay(Long monitorId, LocalDate day);

    List<DailyStat> findByMonitorIdInAndDayGreaterThanEqual(List<Long> monitorIds, LocalDate from);

    @Modifying
    @Query("delete from DailyStat d where d.day < :before")
    int deleteOlderThan(LocalDate before);
}

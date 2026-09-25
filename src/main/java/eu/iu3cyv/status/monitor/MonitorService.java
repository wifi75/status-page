package eu.iu3cyv.status.monitor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MonitorService {

    private final MonitorRepository repository;

    public MonitorService(MonitorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MonitorView> publicMonitors() {
        return repository.findByEnabledTrueOrderByDisplayOrderAscNameAsc()
                .stream()
                .map(MonitorView::of)
                .toList();
    }
}

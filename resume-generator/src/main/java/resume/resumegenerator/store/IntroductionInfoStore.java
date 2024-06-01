package resume.resumegenerator.store;

import org.springframework.stereotype.Component;
import resume.resumegenerator.domain.entity.IntroductionInfo;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class IntroductionInfoStore {

    private final ConcurrentHashMap<Long, IntroductionInfo> store = new ConcurrentHashMap<>();

    public IntroductionInfo save(Long userId, IntroductionInfo introductionInfo) {
        store.put(userId, introductionInfo);
        return store.get(userId);
    }

    public IntroductionInfo findById(Long userId) {
        return store.get(userId);
    }

    public IntroductionInfo update(Long userId, IntroductionInfo introductionInfo) {
        store.put(userId, introductionInfo);
        return introductionInfo;
    }

    public boolean existsById(Long userId) {
        return store.containsKey(userId);
    }
}

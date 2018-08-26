package dan.langford.chult;

import dan.langford.chult.service.DiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.IntFunction;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

@Service
@Slf4j
public class SomeThreadTest {

    @Autowired
    DiceService dice;


    public void run(){
        ForkJoinPool pool = ForkJoinPool.commonPool();

        List<Callable<Integer>> tasks = rangeClosed(1, 10000)
                .mapToObj(this::buildTask)
                .collect(toList());

        List<Integer> results = pool.invokeAll(tasks).stream()
                .map(this::getNotStupid)
                .filter(Objects::nonNull)
                .collect(toList());


        log.info("results={}", results);


    }

    Callable<Integer> buildTask(int i) {
        return () -> dice.roll(i+"d"+i);
    }

    <T> T getNotStupid(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
           throw new RuntimeException(e);
        }
    }




}

package dan.langford.chult.repo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.NumberUtils;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Repository
@Slf4j
public class FileRepo {

    @Value("${app.campaign}")
    String store;

    private FileSystemResource fsr;
    private Properties props;

    @SneakyThrows
    private Properties load(){
        props = new Properties();
        fsr = new FileSystemResource("chult-" + store + ".properties");
        if (!fsr.exists()) {
            fsr.getFile().createNewFile();
        }
        log.info("fileuri={}", fsr.getURI());
        props.load(fsr.getInputStream());
        return props;
    }

    public Properties get() {
        if (this.props==null) {
            this.props=load();
        }
        return this.props;
    }

    @SneakyThrows
    private void store(){
        this.props.store(fsr.getOutputStream(), null);
    }

    private void store(Properties props) {
        this.props = props;
        this.store();
    }

    public Set<Integer> loadRollCache(String memoryId) {
        this.props = this.load();
        return Arrays.stream(this.props.getProperty("rollcache."+memoryId,"-1").split(" "))
                .map(x-> NumberUtils.parseNumber(x, Integer.class))
                .collect(toSet());
    }

    public void storeRollCache(String memoryId, Set<Integer> rollCache) {
        this.props.setProperty("rollcache."+memoryId, rollCache.stream().sorted().filter(x->x>=0).map(Object::toString).collect(joining(" ")));
        this.store(this.props);
    }

    public Set<String> loadGroupCache(String memoryId) {
        this.props = this.load();
        return Arrays.stream(this.props.getProperty("groupcache."+memoryId,"x").split(" "))
                .collect(toSet());
    }

    public void storeGroupCache(String memoryId, Set<String> rollCache) {
        this.props.setProperty("groupcache."+memoryId, rollCache.stream().sorted().filter(x->!"x".equals(x)).collect(joining(" ")));
        this.store(this.props);
    }

    public Integer getDay() {
        return Integer.valueOf(this.get().getProperty("time.day","0"));
    }

    public void storeDay(Integer day){
        this.get().setProperty("time.day", day.toString());
        this.store();
    }

    public Integer getHour() {
        return Integer.valueOf(this.get().getProperty("time.hour","0900"));
    }

    public void storeHour(Integer hour){
        this.get().setProperty("time.hour", String.format("%04d", hour));
        this.store();
    }

    public Integer getHoursLeft() {
        return Integer.valueOf(this.get().getProperty("time.hoursleft","10"));
    }

    public void storeHoursLeft(Integer hoursleft){
        this.get().setProperty("time.hoursleft", hoursleft.toString());
        this.store();
    }
}

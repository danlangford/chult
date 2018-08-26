package dan.langford.chult.service;

import dan.langford.chult.repo.FileRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TimeService {

    @Autowired
    FileRepo fileRepo;

    public int getDay(){
        return fileRepo.getDay();
    }

    public void newDay(){
        fileRepo.storeDay(fileRepo.getDay()+1);
        fileRepo.storeHour(fileRepo.getHour());
        fileRepo.storeHoursLeft(fileRepo.getHoursLeft());
    }
}

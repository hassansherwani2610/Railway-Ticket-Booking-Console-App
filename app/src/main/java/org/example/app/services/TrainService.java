package org.example.app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrainService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Train> trainList;
    private static final String TRAIN_FINAL_PATH = "app/src/main/java/org/example/app/localDb/trains.json";

    public TrainService() throws IOException{
        File trains = new File(TRAIN_FINAL_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>(){});
    }

}

package org.example.app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrainService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Train> trainList;
    private static final String TRAIN_FINAL_PATH = "app/src/main/java/org/example/app/localDb/trains.json";

    public TrainService() throws IOException{
        File trains = new File(TRAIN_FINAL_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>(){});
    }

    // To save the changes in file (Local DB)
    private void saveTrainListToFile(){
        try{
            objectMapper.writeValue(new File(TRAIN_FINAL_PATH) , trainList); // To save date of "trainList" in file (Local DB)
        }
        catch (IOException exception){
            System.out.println("Unable to save train data. Please try again later.");
        }
    }

    // This checks whether the TRAIN's go the source and destination input by USER
    private boolean validTrain(Train train , String source , String destination){
        List<String> stations = train.getStations().stream().map(String::toLowerCase).toList();

        int sourceIndex = stations.indexOf(source.toLowerCase());
        int destinationIndex = stations.indexOf(destination.toLowerCase());

        return sourceIndex >= 0 && destinationIndex >= 0 && sourceIndex < destinationIndex;
    }

    // This tells us that the train is valid to go the desired source and destination
    public List<Train> searchTrains(String source , String destination){
        return trainList.stream().filter(train -> validTrain(train, source, destination)).collect(Collectors.toList());
    }

    // To add a New Train in our Data in our file (Local DB)
    public void addTrain(Train newTrain){
        Optional<Train> trainExists = trainList.stream().filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId())).findFirst();

        if (trainExists.isPresent()){
            // If a train with the same "trainId" exists, update it instead of adding a new one
            updateTrain(newTrain);
        }
        else {
            // Otherwise, add the new train to the list
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    // To update the Data of already existing Train
    public void updateTrain(Train updatedTrain){

    }
}

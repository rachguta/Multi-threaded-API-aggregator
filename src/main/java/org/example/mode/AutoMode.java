package org.example.mode;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.format.CsvFileManager;
import org.example.format.FileManager;
import org.example.format.JsonFileManager;
import java.util.NoSuchElementException;

public class AutoMode implements Mode {

    @Override
    public void start() throws NoSuchElementException, NoDataException, FileProcessingException {
        System.out.println("Auto Mode");
        API[] apis;
        String formatName;
        FileManager fileManager;
        apis = CliManager.readApis();
        formatName = CliManager.readFormat();

        if(formatName.equals("json")){
            fileManager = new JsonFileManager();
        }
        else{
            fileManager = new CsvFileManager();
        }

        for(API api : apis){
            Aggregator.aggregateData(api);
        }

        if(Aggregator.getData().isEmpty()){
            throw new NoDataException("No data was aggregated from the APIs");
        }

        String fileName = CliManager.readFileName() + "."  + formatName;
        fileManager.writeToNewFile(fileName, Aggregator.getData());
    }
}

package org.example.mode;

import org.example.Aggregator;
import org.example.API;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.filemanager.CsvFileManager;
import org.example.filemanager.FileManager;
import org.example.filemanager.JsonFileManager;

import java.util.NoSuchElementException;

public class AutoMode implements Mode{
    String[] args;

    public AutoMode(String[] args) {
        this.args = args;
    }

    @Override
    public void start() throws IllegalArgumentException,NoSuchElementException, NoDataException, FileProcessingException {
        System.out.println("Auto mode");
        API[] apis = CliManager.parseApisFromArgs(args);
        String formatName = CliManager.parseFormatFromArgs(args);
        FileManager fileManager;

        if(formatName.equals("json")){
            fileManager = new JsonFileManager();
        }
        else{
            fileManager = new CsvFileManager();
        }

        for(API api : apis){
            String url = api.createUrlWithDefaultParameter();
            Aggregator.aggregateData(api, url);
        }

        if(Aggregator.getData().isEmpty()){
            throw new NoDataException("No data was aggregated from the APIs");
        }

        String fileName = "res."  + formatName;

        fileManager.writeToNewFile(fileName, Aggregator.getData());
    }
}

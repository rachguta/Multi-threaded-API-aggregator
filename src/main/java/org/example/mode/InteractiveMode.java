package org.example.mode;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.filemanager.CsvFileManager;
import org.example.filemanager.FileManager;
import org.example.filemanager.JsonFileManager;

import java.util.NoSuchElementException;

public class InteractiveMode implements Mode {

    @Override
    public void start() throws NoSuchElementException, NoDataException, FileProcessingException {
        System.out.println("Interactive Mode");
        API[] apis;
        String formatName;
        FileManager fileManager;
        String fileMode;
        String outputMode;
        apis = CliManager.readApis();
        formatName = CliManager.readFormat();

        if(formatName.equals("json")){
            fileManager = new JsonFileManager();
        }
        else{
            fileManager = new CsvFileManager();
        }

        for(API api : apis){
            if(!api.getParameter().isEmpty()){
                String parameter = CliManager.readParameter(api);
                Aggregator.aggregateData(api, api.createUrlWithParameter(parameter));
            }
            else{
                Aggregator.aggregateData(api, api.getUrl());
            }
        }

        if(Aggregator.getData().isEmpty()){
            throw new NoDataException("No data was aggregated from the APIs");
        }

        fileMode = CliManager.readFileMode();

        String fileName = CliManager.readFileName() + "."  + formatName;

        if(fileMode.equals("create")){
            fileManager.writeToNewFile(fileName, Aggregator.getData());
        }
        else{
            fileManager.writeToExistingFile(fileName, Aggregator.getData());
        }

        outputMode = CliManager.readOutputMode();

        if(outputMode.equals("fully")){
            fileManager.printAll(fileName);
        }
        else{
            API outputApi = CliManager.readApi();
            fileManager.printByApi(fileName, outputApi);
        }

    }
}

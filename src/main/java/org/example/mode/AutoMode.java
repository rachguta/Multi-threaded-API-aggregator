package org.example.mode;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.format.CsvFormat;
import org.example.format.FileFormat;
import org.example.format.JsonFormat;

import java.util.NoSuchElementException;

public class AutoMode implements Mode {

    @Override
    public void start() throws NoSuchElementException {
        System.out.println("Auto Mode");
        API[] apis;
        String formatName;
        FileFormat fileFormat;
        apis = CliManager.readApis();
        formatName = CliManager.readFormat();
        if(formatName.equals("json")){
            fileFormat = new JsonFormat();
        }
        else{
            fileFormat = new CsvFormat();
        }
        for(API api : apis){
            Aggregator.aggregateData(api);
        }
        String fileName = CliManager.readFileName() + "."  + formatName;
        fileFormat.writeToNewFile(fileName, Aggregator.getData());
    }
}

package com.developer.alexandru.orarusv.data;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by alexandru on 8/14/16.
 */
public abstract class CSVParser {

    private BufferedReader br;
    private boolean wasSuccessful;

    public CSVParser(BufferedReader bufferedReader)
    {
        this.br = bufferedReader;
    }

    public void parse()
    {
        String line;
        try {
            while ((line = br.readLine()) != null) {

                int len = line.length();

                char[] array = line.toCharArray();

                // The char within the string is part of data, not part of a HTML tag
                boolean isData = false;
                int start = 0, stop;
                for (int j = 1; j < len - 1; j++) {

                    String buffer;
                    try {
                        // HTML tag end, data begin
                        if (array[j] == '>' && array[j + 1] != '<') {
                            isData = true;
                            start = j + 1;
                            continue;
                        }
                        // data end, HTML tag begin
                        if (array[j] == '<') {
                            if (isData) {
                                stop = j - 1;

                                buffer = String.copyValueOf(array, start, stop - start);

                                String[] data = buffer.split(";");

                                handleData(data);
                            }

                            isData = false;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        wasSuccessful = false;
                    }
                    
                    wasSuccessful = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            wasSuccessful = false;
        }
    }

    public boolean wasSuccessful()
    {
        return wasSuccessful;
    }

    public abstract boolean handleData(String[] data);
}

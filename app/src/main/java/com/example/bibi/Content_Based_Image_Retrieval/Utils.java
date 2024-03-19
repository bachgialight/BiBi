package com.example.bibi.Content_Based_Image_Retrieval;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static List<String> loadLabelList(Context context) {
        List<String> labelList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("labels.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return labelList;
    }

    public static Map<String, Float> getTopKLabels(Map<String, Float> labeledProb, int k) {
        Map<String, Float> topK = new HashMap<>();
        int count = 0;
        for (Map.Entry<String, Float> entry : labeledProb.entrySet()) {
            if (count < k) {
                topK.put(entry.getKey(), entry.getValue());
                count++;
            } else {
                break;
            }
        }
        return topK;
    }
}

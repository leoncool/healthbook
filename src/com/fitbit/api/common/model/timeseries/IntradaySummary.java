package com.fitbit.api.common.model.timeseries;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;

public class IntradaySummary {

    private Data summary;
    private IntradayDataset intradayDataset;

    public IntradaySummary(JSONObject json, TimeSeriesResourceType resourceType) throws JSONException {
        String timeSeriesJsonName = resourceType.getResourcePath().substring(1).replace('/', '-');
        String intradayDataJsonName = timeSeriesJsonName + "-intraday";
        List<Data> dataList=Data.jsonArrayToDataList(json.getJSONArray(timeSeriesJsonName));
        if(dataList.size()>0)
        {
        	  summary = Data.jsonArrayToDataList(json.getJSONArray(timeSeriesJsonName)).get(0);
        }
         if (json.has(intradayDataJsonName)) {
            intradayDataset = new IntradayDataset(json.getJSONObject(intradayDataJsonName));
        }
    }

    public IntradaySummary(Data summary, IntradayDataset intradayDataset) {
        this.summary = summary;
        this.intradayDataset = intradayDataset;
    }

    public Data getSummary() {
        return summary;
    }

    public IntradayDataset getIntradayDataset() {
        return intradayDataset;
    }
}

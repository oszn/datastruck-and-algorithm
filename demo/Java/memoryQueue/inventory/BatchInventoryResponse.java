package com.example.demo.component.concurrent.inventory;

import com.example.demo.dao.entry.commodity.InventoryListTest;

import java.util.ArrayList;
import java.util.List;

public class BatchInventoryResponse {
    List<InventoryResponse> responseList;
    int num;
    public List<InventoryListTest> convert(){
        List<InventoryListTest> list=new ArrayList<>();
        for(InventoryResponse response:responseList){
            InventoryListTest test=new InventoryListTest();
            test.setCommodityId(response.getId());
            test.setTestIdx(response.getIdx());
            list.add(test);
        }
        return list;
    }
    public BatchInventoryResponse(){
        num=0;
        responseList=new ArrayList<>();
    }
    public void add(InventoryResponse response){
        responseList.add(response);
        num++;
    }
    public int getNum() {
        return num;
    }
    public int size(){
        return responseList.size();
    }
    public InventoryResponse get(int idx){
        return responseList.get(idx);
    }
    public void setNum(int num) {
        this.num = num;
    }

    public List<InventoryResponse> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<InventoryResponse> responseList) {
        this.responseList = responseList;
    }

    @Override
    public String toString() {
        return "BatchInventoryResponse{" +
                "responseList=" + responseList +
                ", num=" + num +
                '}';
    }
}

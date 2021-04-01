import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class StoreRecords {
    public AtomicIntegerArray[] storeArrays;

    public StoreRecords(){
        storeArrays = new AtomicIntegerArray[512];
        for (int i = 0; i < 512; i++){
            storeArrays[i] = new AtomicIntegerArray(100000);
        }
        System.out.println("StoreRecords constructor finished");
    }

    public void increase(int storeID, int itemID, int delta){
//        System.out.println("increase method called");
//        if (storeArrays[storeID] == null) storeArrays[storeID] = new AtomicIntegerArray(100000);
        AtomicIntegerArray atomicIntegerArray = storeArrays[storeID];
        int res = atomicIntegerArray.addAndGet(itemID, delta);
//        System.out.println(res);
    }

    public int getNumber(int storeID, int itemID){
        AtomicIntegerArray array = storeArrays[storeID];
        if (array == null) return 0;
        return array.get(itemID);
    }

    public String getStoreTop10Items(int storeID){
        PriorityQueue<int[]> pq = new PriorityQueue<>(10, (a1, a2) -> a1[1] == a2[1]? a2[0]-a1[0]: a1[1]-a2[1]);
        AtomicIntegerArray array = storeArrays[storeID];
        if (array == null) return "";
        for (int i = 0; i < 100000; i++) {
            pq.offer(new int[]{i, array.get(i)});
            if (pq.size() > 10) pq.poll();
        }

        List<int[]> list = new ArrayList<>();

        for (int[] arr:pq){
            list.add(arr);
        }

        Collections.sort(list,(a, b)->b[1] == a[1]? a[0]-b[0]: b[1]-a[1]);

        JSONObject responesJson = new JSONObject();
        JSONArray stores = new JSONArray();

        for (int[] arr:list){
            JSONObject store = new JSONObject().put("ItemID", arr[0]).put("numberOfItems", arr[1]);
            stores.put(store);
        }

        responesJson.put("stores",stores);
        return responesJson.toString();
    }

    public String getItemTop5Store(int itemID){
        PriorityQueue<int[]> pq = new PriorityQueue<>(5, (a1, a2) -> a1[1] == a2[1]? a2[0]-a1[0]: a1[1]-a2[1]);

        for (int i = 0; i < 512; i++){
            AtomicIntegerArray currentStore = storeArrays[i];
            if (currentStore == null) pq.offer(new int[]{i+1, 0});
            else pq.offer(new int[]{i, storeArrays[i].get(itemID)});
            if (pq.size() > 5) pq.poll();
        }

        List<int[]> list = new ArrayList<>();

        for (int[] arr:pq){
            list.add(arr);
        }

        Collections.sort(list,(a, b)->b[1] == a[1]? a[0]-b[0]: b[1]-a[1]);

        JSONObject responesJson = new JSONObject();
        JSONArray stores = new JSONArray();

        for (int[] arr:list){
            JSONObject store = new JSONObject().put("storeID", arr[0]).put("numberOfItems", arr[1]);
            stores.put(store);
        }
        responesJson.put("stores",stores);
        return responesJson.toString();
    }

}

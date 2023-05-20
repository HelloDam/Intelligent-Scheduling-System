import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dam.model.entity.enterprise.ProvinceCityRegionEntity;
import com.dam.service.ProvinceCityRegionService;
import com.dam.service.impl.ProvinceCityRegionServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveCityDataTest {
    @Autowired
    private ProvinceCityRegionService provinceCityRegionService;

    @Test
    public void saveDataToDataBase() throws IOException {

        String path = "D:\\Projects\\damFile\\SmartSchedulingSystem\\smart-scheduling-system\\Code\\serve\\v1\\smart-scheduling-system-server\\file\\data\\省市区.txt";

        StringBuilder stringBuilder = new StringBuilder();

//        利用io流读取数据
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fileInputStream);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        HashMap map = JSONObject.parseObject(stringBuilder.toString(), HashMap.class);


        for (Object o : map.entrySet()) {
            this.saveData(o, 0L);
        }
        int temp = 0;
    }


    public void saveData(Object o, Long parentId) {

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String json = entry.getValue().toString();
            if (json.contains(":")) {
                JSONObject value = JSON.parseObject(json);
                String name = (String) value.get("name");
                Integer type = -1;
                if (name.contains("省")) {
                    type = 0;
                } else if (name.contains("市")) {
                    type = 1;
                } else if (name.contains("区") || name.contains("县")) {
                    type = 2;
                }
                if (type != -1) {
                    ProvinceCityRegionEntity provinceCityRegionEntity = new ProvinceCityRegionEntity();
                    provinceCityRegionEntity.setName(name);
                    provinceCityRegionEntity.setType(type);
                    provinceCityRegionEntity.setParentId(parentId);
                    ProvinceCityRegionServiceImpl provinceCityRegionService = new ProvinceCityRegionServiceImpl();
                    provinceCityRegionService.save(provinceCityRegionEntity);
                    JSONObject child = JSON.parseObject(JSON.toJSONString(value.get("child")));
                    this.saveData(child, provinceCityRegionEntity.getId());
                }
            } else {

                String name = json;
                Integer type = -1;
                if (name.contains("省")) {
                    type = 0;
                } else if (name.contains("市")) {
                    type = 1;
                } else if (name.contains("区")) {
                    type = 2;
                }
                ProvinceCityRegionEntity provinceCityRegionEntity = new ProvinceCityRegionEntity();
                provinceCityRegionEntity.setName(name);
                provinceCityRegionEntity.setType(type);
                provinceCityRegionEntity.setParentId(parentId);
                ProvinceCityRegionServiceImpl provinceCityRegionService = new ProvinceCityRegionServiceImpl();
                provinceCityRegionService.save(provinceCityRegionEntity);
            }

            int temp = 0;
        }
    }

}

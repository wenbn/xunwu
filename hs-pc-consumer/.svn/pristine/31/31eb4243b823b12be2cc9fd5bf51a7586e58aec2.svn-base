package www.ucforward.com.controller.base;

import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import www.ucforward.com.utils.FastDFSClient;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.PropertiesUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 文件上传类
 */
@Controller
@RequestMapping("/file")
public class InteriorUploadController {


    public static final String IMG_URL = PropertiesUtils.loadProps("/fastdfs/fastdfs.properties").getProperty("fastdfs_host");

    //上传图片  @RequestParam(required = false 防止为null 抛异常
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam(required = false) MultipartFile submitFile , HttpServletResponse response) throws Exception{
        Map<String,String> metaList = new HashMap<String, String>();
        String originalFilename = submitFile.getOriginalFilename();
        metaList.put("name",originalFilename);
        //metaList.put("author","wenbn");
//        String fid = FastDFSClient.uploadFile(file,submitFile.getOriginalFilename(),metaList);
        String fid = FastDFSClient.uploadFile(submitFile.getBytes(),submitFile.getOriginalFilename(),metaList);
        JSONObject  jo = new JSONObject();
        jo.put("fileName",originalFilename);
        if( fid == null || "".equals(fid) ){
            jo.put("state",1);
            jo.put("message","文件上传失败");
        }else{
            String url = IMG_URL + fid;
            jo.put("fid",fid);
            jo.put("url", url);
            jo.put("state",0);
            jo.put("message","文件上传成功");
        }
        return JsonUtil.toJson(jo);
    }

    //文件下载
    @RequestMapping(value = "/download")
    @ResponseBody
    public String download(@RequestParam(required = false) String fid , HttpServletResponse response) throws IOException {
        String _filename = "";
        Map<Object,Object> jo = new HashMap<Object,Object>();
        try{
            Map<String,String> metaList = FastDFSClient.getFileMetadata(fid);
            if(metaList==null || metaList.size()<1){
                jo.put("state",1);
                jo.put("message","文件下载失败");
                return JsonUtil.toJson(jo);
            }
            for (Iterator<Map.Entry<String,String>> iterator = metaList.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String, String> entry = iterator.next();
                String name = entry.getKey();
                String value = entry.getValue();
                if ("name".equals(name)) {
                    _filename = value;
                    break;
                }
            }
            jo.put("state",0);
            jo.put("message","文件上传成功");
            jo.put("download_url",IMG_URL+fid+"?filename="+_filename);
        }finally {
            return JsonUtil.toJson(jo);
        }
    }

    //文件删除
    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete_file(@RequestParam(required = false) String fid , HttpServletResponse response) throws IOException {
        String message = "";
        Map<Object,Object> jo = new HashMap<Object,Object>();
        if( fid == null || "".equals(fid) ){
            message = "文件ID不能为空";
            jo.put("message",message);
            return JsonUtil.toJson(jo);
        }
        int delete_state = FastDFSClient.deleteFile(fid);
        if(delete_state == 0){
            jo.put("message","文件删除成功");
            jo.put("state",0);
        }else{
            jo.put("message","文件删除失败");
            jo.put("state",1);
        }
        return JsonUtil.toJson(jo);
    }

    //上传多张图片
    @RequestMapping(value = "/uploads")
    public @ResponseBody
    List<JSONObject> uploads(@RequestParam(required = false) MultipartFile[] files , HttpServletResponse response) throws Exception {
        List<JSONObject> result = new ArrayList<>();
        JSONObject jo = null;
        Map<String,String> metaList = null;
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            metaList = Maps.newHashMap();
            metaList.put("name",originalFilename);
            String fid = FastDFSClient.uploadFile(file.getBytes(),file.getOriginalFilename(),metaList);
            jo = new JSONObject();
            jo.put("fileName",originalFilename);
            if( fid == null || "".equals(fid) ){
                jo.put("state",1);
                jo.put("message","文件上传失败");
            }else{
                String url = IMG_URL + fid;
                jo.put("fid",fid);
                jo.put("url", url);
                jo.put("state",0);
                jo.put("message","文件上传成功");
            }
            result.add(jo);
        }
        return result;
    }

}

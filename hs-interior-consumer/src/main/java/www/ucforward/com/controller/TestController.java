package www.ucforward.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.vo.ResultVo;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Controller
public class TestController {

    @Autowired
    private CommonService commonService;

    /**
     *
     * @return
     */
    @RequestMapping("test")
    @ResponseBody
    public String testTx(){
        ResultVo vo = new ResultVo();
        int state = 0;
        try {
            state = commonService.testTx();
        } catch (Exception e) {
            state =-1;
            e.printStackTrace();
        }
        vo.setResult(state);
        return JsonUtil.toJson(vo);
    }
}

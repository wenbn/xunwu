package www.ucforward.com.manager;

import www.ucforward.com.entity.HsMsgSetting;
import www.ucforward.com.vo.ResultVo;

import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/26 17:02
 * @Description:
 */
public interface MsgManager {

    /**
     * 获取消息列表
     * @param condition
     * @return
     */
    ResultVo getMsgList(Map<Object,Object> condition);

    /**
     * 获取消息历史
     * @param condition
     * @return
     */
    ResultVo getMsgHistory(Map<Object,Object> condition);

    /**
     * 获取消息类型
     * @param type 类型 1外部 2外获 3外看 4区域长
     * @return
     */
    ResultVo getMsgType(String type);

    /**
     * 获取人员消息设置
     * @param condition
     * @return
     */
    ResultVo getMemMsgSetting(Map<Object,Object> condition);

    /**
     * 用户设置消息
     * @param hsMsgSetting
     * @return
     */
    ResultVo setMemMsgSetting(HsMsgSetting hsMsgSetting);

    /**
     * 推送系统消息
     * @param condition
     * @return
     */
    ResultVo pushSysMsg(Map<Object,Object> condition);
}

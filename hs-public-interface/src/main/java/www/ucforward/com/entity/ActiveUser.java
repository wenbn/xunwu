package www.ucforward.com.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ActiveUser implements Serializable{

    private Integer userid;//用户id（主键）
    private String usercode;// 用户账号
    private String username;// 用户名称
    private String mobile;

    private List<Map<Object,Object>> roleList;//权限列表

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Map<Object,Object>> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Map<Object,Object>> roleList) {
        this.roleList = roleList;
    }
}

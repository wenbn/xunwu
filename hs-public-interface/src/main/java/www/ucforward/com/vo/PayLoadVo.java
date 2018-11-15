package www.ucforward.com.vo;

import java.util.Date;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
public class PayLoadVo {
    private String iss;//jwt签发者
    private Date exp;  //jwt的过期时间，这个过期时间必须要大于签发时间
    private Date iat;  //jwt的签发时间
    private String userId;
    private String areaCode;//电话地区号
    private String moble;//手机号码

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    public Date getIat() {
        return iat;
    }

    public void setIat(Date iat) {
        this.iat = iat;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getMoble() {
        return moble;
    }

    public void setMoble(String moble) {
        this.moble = moble;
    }


    //iss: 该JWT的签发者，是否使用是可选的；
    //sub: 该JWT所面向的用户，是否使用是可选的；
    //aud: 接收该JWT的一方，是否使用是可选的；
    //exp(expires): 什么时候过期，这里是一个Unix时间戳，是否使用是可选的；
    //iat(issued at): 在什么时候签发的(UNIX时间)，是否使用是可选的；
    //其他还有：
    //nbf (Not Before)：如果当前时间在nbf里的时间之前，则Token不被接受；一般都会留一些余地，比如几分钟；，是否使用是可选的；
    //https://www.cnblogs.com/xiekeli/p/5607107.html
}

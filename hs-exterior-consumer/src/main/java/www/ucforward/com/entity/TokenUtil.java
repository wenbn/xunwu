package www.ucforward.com.entity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import www.ucforward.com.vo.PayLoadVo;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/23
 */
public class TokenUtil {

    private static final String typ = "JWT";
    private static final String alg = "SHA-256";
    private static final String secret = "www.ucforword.com";

    public static String createToken(PayLoadVo payLoadVo) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alg", typ);
        map.put("typ", alg);
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long _nowMillis = 30*60*1000+nowMillis;
        Date expDate = new Date(_nowMillis);
        String token = JWT.create()
                .withHeader(map)//header
                .withClaim("iss", payLoadVo.getIss())//jwt签发者
                .withClaim("exp", expDate)//jwt的过期时间
                .withClaim("iat", now)//jwt的签发时间
                .withClaim("userId", payLoadVo.getUserId())//payload
                .sign(Algorithm.HMAC256(secret));//加密
        return token;
    }

    public static Map<String,Object> verifyToken(String token) throws Exception{
        Map<String,Object> result = new HashMap<String,Object>();
        PayLoadVo vo = new PayLoadVo();
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT jwt = verifier.verify(token);
        Map<String, Claim> claims = jwt.getClaims();
        if(claims ==null){
            result.put("state",1);
            result.put("message","校验失败");
            return result;
        }
        Date nowDate = new Date();
        Date expDate = claims.get("exp").asDate();
        vo.setIss(claims.get("iss").asString());//jwt签发者
        vo.setExp(expDate);//jwt的过期时间
        vo.setIat(claims.get("iat").asDate());//jwt的签发时间
        vo.setUserId(claims.get("userId").asString());
        return result;
    }

    public static void main(String[] args) throws Exception {
        PayLoadVo vo = new PayLoadVo();
        vo.setIss("123456");
        vo.setUserId("145");

        String token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IlNIQS0yNTYifQ.eyJpc3MiOiIxMjM0NTYiLCJleHAiOjE1MjcwNTkyODAsImlhdCI6MTUyNzA1NzQ4MCwidXNlcklkIjoiMTQ1In0.QPlbGDUWvZzADVSR__RYzlLF6LSsbLT_63idJgCSaPI";

        // 生成token
        String _token = TokenUtil.createToken(vo);
        // 打印token
        System.out.println("token: " + _token);

        // 解密token
         verifyToken(token);

    }

}

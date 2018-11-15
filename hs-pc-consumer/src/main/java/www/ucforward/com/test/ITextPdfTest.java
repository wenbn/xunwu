package www.ucforward.com.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.utils.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *https://blog.csdn.net/dreamer_8399/article/details/78541038
 * https://blog.csdn.net/jidetashuo/article/details/51691262
 * @author wenbn
 * @version 1.0
 * @date 2018/10/13
 */
public class ITextPdfTest {

    public static void fillTemplate(Map<Object,Object> data) throws IOException, DocumentException {
        String templatePath ="D:/converted1.pdf";
        // 生成的新文件路径
        String newPDFPath = "D:/converted2.pdf";
        PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            out = new FileOutputStream(newPDFPath);// 输出流
            reader = new PdfReader(templatePath);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields fields = stamper.getAcroFields();

            insertText(stamper,fields,data);

            Document doc = new Document();
            insertImage(stamper,fields,null);

            stamper.setFormFlattening(true);// 如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.close();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();
        } catch (IOException e) {
            e.getStackTrace();
        } catch (DocumentException e) {
            e.getStackTrace();
        }
    }

    /**
     * 插入图片
     *
     * @param ps
     * @param s
     * @author WangMeng
     * @date 2016年6月16日
     */
    public static void insertImage(PdfStamper ps, AcroFields s,String file_url){
        try {
            System.out.println("kkeke");
            List<AcroFields.FieldPosition> list = s.getFieldPositions("logo");
            Rectangle signRect = list.get(0).position;
            int page = list.get(0).page;
            Image image = Image.getInstance("D:/feiji.jpg");
            PdfContentByte under = ps.getOverContent(page);
            float x = signRect.getLeft();
            float y = signRect.getBottom();
            System.out.println(x);
            System.out.println(y);
            image.setAbsolutePosition(x, y);
            image.scaleToFit(signRect.getWidth(), signRect.getHeight());
            under.addImage(image);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 插入文本
     * @return
     * @author WangMeng`
     * @date 2016年6月16日
     */
    public static void insertText(PdfStamper stamper, AcroFields fields,Map<Object,Object> data) throws IOException, DocumentException {
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            fields.setField(StringUtil.trim(entry.getKey()), StringUtil.trim(entry.getValue()));
        }
    }


    public static void main(String[] args) throws IOException, DocumentException {
        Map<Object,Object> data = Maps.newHashMap();
        data.put("name","文彬");
        data.put("sex","男");
        data.put("huji","湖南省益阳市");
        data.put("age","23");
        data.put("Text5","深圳");
        data.put("Text6","15012940205");
        data.put("Text7","汉");
        data.put("Text8","qq.com");
        fillTemplate(data);

    }
}

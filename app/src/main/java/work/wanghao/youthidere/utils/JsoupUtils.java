package work.wanghao.youthidere.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import work.wanghao.youthidere.model.ImgItem;

/**
 * Created by wangh on 2015-12-6-0006.
 */
public class JsoupUtils {

    public static String parseContent2VideoUrl(String content) {
        Document doc = Jsoup.parse(content);
        if (doc == null) {
            return null;
        }
        return doc.getElementsByTag("video").attr("src");
    }

    public static List<ImgItem> parseContent2ImgUrl(String content) {
        Document doc = Jsoup.parse(content);
        if (doc == null) {
            Log.e("doc", "doc为空");
            return null;
        }
        Elements elements = doc.getElementsByTag("p"); 
//        elements.select("p:containsOwn(\u00a0)").remove();
////        Elements elements=new Elements();
////        for(Element elements1:elementsdoc){
////            if(!elements1.text().equals("")){
////                elements.add(elements1);
////            }
////        }
        Log.e("doc长度", "长度为" + String.valueOf(elements.size()));
//        Elements elementsTitle=elements.select("^img[data-src]");
//        Elements elementsImgUrl=elements.select("img[data-src]");
//        elements.removeAll(elementsImgUrl);
//        Elements elementsTitle=elements;
        Elements elementsTitle = new Elements();
        Elements elementsImgUrl = new Elements();
        List<ImgItem> data = new ArrayList<ImgItem>();
       /* for (Element element : elements) {
            ImgItem temp = new ImgItem();
            do {
                if (element.select("img[data-src]") == null) {
                    temp.setTitle(element.text());
                } else {
                    temp.setImgUrl(element.select("img[data-src]").attr("img[data-src]"));
                }
            } while (element.select("img[data-src]") == null);
            }*/
        ImgItem temp = null;
        for (int i=0;i<elements.size();i++)
        {
           if (elements.get(i).select("img[data-src]").isEmpty()) 
           {
               Log.e("if元素值:",elements.get(i).text());
               if (i!=0)
               {
                   data.add(temp);
                   Log.e("添加数据:","");
               }
              temp=new ImgItem();
               temp.setTitle(elements.get(i).text());
            }
            else
            {
                Log.e("else元素值:",elements.get(i).text());
                temp.setImgUrl(elements.get(i).select("img[data-src]").attr("data-src"));
            }
            if(i==elements.size()-1)
            {
                data.add(temp);
            }
        }
//            if(element.select("img[data-src]")==null){
//                temp.setTitle(element.text());
//            }

//           if(element.select("img[data-src]")!=null){
//               elementsImgUrl.add(element);
//               
//           }else {
//               elementsTitle.add(element);
//              
//           }
        
        Log.e("elementsTitle长度", "长度为" + String.valueOf(elementsTitle.size()));
        Log.e("elementsImgUrl长度", "长度为" + String.valueOf(elementsImgUrl.size()));
//        List<ImgItem> items = new ArrayList<ImgItem>();
//        for (int i = 0; i < elementsTitle.size(); i++) {
//            ImgItem imgItem = new ImgItem();
//            imgItem.setTitle(elementsTitle.get(i).text());
//            imgItem.setImgUrl(elementsImgUrl.get(i).getElementsByTag("img").attr("data-src"));
//            items.add(imgItem);
//        }
//        Log.e("item长度", "长度为:" + String.valueOf(items.size()));
//        return items;
        return data;
    }


}

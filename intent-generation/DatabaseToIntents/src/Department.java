public class Department implements TableRep{

    private String name;
    private String contactInformation;
    private String pageLink;

    Department(String n, String ci, String pl){
        name = n;
        contactInformation = ci;
        pageLink = pl;
    }

    @Override
    public Object getValue(int attrNum){
        switch(attrNum){
            case 0:
                return getName();
            case 1:
                return getContactInformation();
            case 2:
                return getPageLink();
            default:
                return "No such value";
        }

    }

    String getName(){
        return name;
    }

    String getContactInformation(){
        return contactInformation;
    }

    String getPageLink(){
        return pageLink;
    }
}

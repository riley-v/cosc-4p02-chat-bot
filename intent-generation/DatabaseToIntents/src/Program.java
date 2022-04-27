public class Program implements TableRep{

    private String name;
    private String requirements;
    private String description;
    private String pageLink;
    private String departmentName;

    Program(String n, String r, String d, String pl, String dn){
        name = n;
        requirements = r;
        description = d;
        pageLink = pl;
        departmentName = dn;
    }

    @Override
    public Object getValue(int attrNum){
        switch(attrNum){
            case 0:
                return getName();
            case 1:
                return getRequirements();
            case 2:
                return getDescription();
            case 3:
                return getPageLink();
            case 4:
                return getDepartmentName();
            default:
                return "No such value";
        }

    }

    String getName(){
        return name;
    }

    String getRequirements(){
        return requirements;
    }

    String getDescription() {
        return description;
    }

    String getPageLink(){
        return pageLink;
    }

    String getDepartmentName() {
        return departmentName;
    }
}

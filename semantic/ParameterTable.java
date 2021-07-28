package semantic;

/**
 *
 * @author Savio
 */
public class ParameterTable {
    
     private String function;
    private String name;
    private int type;
    private int order;
    
    public ParameterTable(String function, String name, int type, int order) {
        //super();
        this.function = function;
        this.name = name;
        this.type = type;
        this.order = order;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
}

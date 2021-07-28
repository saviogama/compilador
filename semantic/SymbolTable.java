package semantic;

/**
 *
 * @author Savio
 */
public class SymbolTable {
    
    private String name;
    private int type;
    private int value;
    private int scope;
    private int function;
    
    public SymbolTable(String name, int type, int value, int scope, int function) {
        //super();
        this.name = name;
        this.type = type;
        this.value = value;
        this.scope = scope;
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
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }
    
    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }
    
}
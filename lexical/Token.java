package lexical;

/**
 *
 * @author Savio
 */
public class Token {
    
    public static final int TK_MAIN = 0;
    public static final int TK_ACHAVES = 1;
    public static final int TK_FCHAVES = 2;
    public static final int TK_APARENTESES = 3;
    public static final int TK_FPARENTESES = 4;
    public static final int TK_ASPAS = 5;
    public static final int TK_IMPRIMIR = 6;
    public static final int TK_ATRIBUICAO = 7;
    public static final int TK_IGUALDADE = 8;
    public static final int TK_DIFERENCA = 9;
    public static final int TK_MAIOR = 10;
    public static final int TK_MENOR = 11;
    public static final int TK_MAIORIGUAL = 12;
    public static final int TK_MENORIGUAL = 13;
    public static final int TK_MAIS = 14;
    public static final int TK_MENOS = 15;
    public static final int TK_MULTIPLICADOR = 16;
    public static final int TK_DIVISOR = 17;
    public static final int TK_VAZIO = 18;
    public static final int TK_INTEIRO = 19;
    public static final int TK_BOOLEANO = 20;
    public static final int TK_ENQUANTO = 21;
    public static final int TK_SE = 22;
    public static final int TK_SENAO = 23;
    public static final int TK_CONTINUE = 24;
    public static final int TK_PAUSA = 25;
    public static final int TK_RETORNO = 26;
    public static final int TK_IDENTIFICADOR = 27;
    public static final int TK_NUMERO = 28;
    public static final int TK_VERDADEIRO = 29;
    public static final int TK_FALSO = 30;
    public static final int TK_FUNCAO = 31;
    public static final int TK_VIRGULA = 32;
    public static final int TK_FIM = 33;

    public static final String TK_TEXT[] = {
        "MAIN",
        "ACHAVES",
        "FCHAVES",
        "APARENTESES",
        "FPARENTESES",
        "ASPAS",
        "IMPRIMIR",
        "ATRIBUICAO",
        "IGUALDADE",
        "DIFERENCA",
        "MAIOR",
        "MENOR",
        "MAIORIGUAL",
        "MENORIGUAL",
        "MAIS",
        "MENOS",
        "MULTIPLICADOR",
        "DIVISOR",
        "VAZIO",
        "INTEIRO",
        "BOOLEANO",
        "ENQUANTO",
        "SE",
        "SENAO",
        "CONTINUE",
        "PAUSA",
        "RETORNO",
        "IDENTIFICADOR",
        "NUMERO",
        "VERDADEIRO",
        "FALSO",
        "FUNCAO",
        "VIRGULA",
        "FIM"
    };
    
    private int tipo;
    private String texto;
    private int line;
    private int column;

    public Token(int tipo, String texto) {
        super();
        this.tipo = tipo;
        this.texto = texto;
    }

    public Token() {
        super();
    }

    public String getTexto() {
        return texto;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Token [tipo = " + tipo + ", texto = " + texto + "]";
    }
    
    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

}
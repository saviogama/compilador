package parser;

import exception.SemanticException;
import exception.SyntaxException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import lexical.Scanner;
import lexical.Token;
import semantic.ParameterTable;
import semantic.SymbolTable;

/**
 *
 * @author Savio
 */
public class Parser {

    private final Scanner scanner;
    private Token token;
    ArrayList<ParameterTable> parameter;
    ArrayList<SymbolTable> symbols;
    //As variaveis abaixo são auxiliares para detectar erro de declaração e chamada de variaveis fora do escopo e retorno de tipos errados de uma função
    private int currentScope;
    private int currentType;
    private int hasReturn;
    //Variavel auxiliar para verificar se um break ou continue esta fora de um laço de repetição
    private int hasLoop;
    //Variavel auxiliar para verificar se uma expressao logica esta errada
    private int wrongExpression;
    //Variaveis auxiliares para o gerador de código de 3 endereços
    private int auxId;
    private int currentId;
    private int currentTemp;
    private int currentWhile;
    private int currentIf;
    StringBuilder cod3 = new StringBuilder();
    File file = new File("C:\\Users\\Savio\\Documents\\output.txt");

    //O Parser recebe o analisador léxico como parâmetro no construtor pois a cada procedimento ele é consultado
    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.parameter = new ArrayList<>();
        this.symbols = new ArrayList<>();
        this.currentScope = 0;
        this.currentType = 0;
        this.hasReturn = 0;
        this.hasLoop = 0;
        this.wrongExpression = 0;
        this.auxId = 0;
        this.currentId = 0;
        this.currentTemp = 0;
        this.currentWhile = 0;
        this.currentIf = 0;
    }

    public void Main() throws IOException {
        DeclaracaoExterna();
        //Se o programa compilar, escreve o codigo de 3 endereços
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(cod3.toString());
        }
    }
    
    public void DeclaracaoExterna(){
        token = scanner.nextToken();
        if(token != null){
            DeclaracaoFuncao();
            DeclaracaoExterna();
        }
    }
    
    public void DeclaracaoFuncao(){
        currentScope++;
        Tipo();
        currentType = token.getTipo();
        token = scanner.nextToken();
        Identificador();
        String functionName = token.getTexto();
        //Verifica se a função já foi declarada
        int aux = 0;
        int isFunction = 0;
        for (int i = 0; i < symbols.size(); i++) {
            if(token.getTexto().equals(symbols.get(i).getName())){
                aux = 1;
                isFunction = symbols.get(i).getFunction();
                break;
            }
        }
        if(aux == 1 && isFunction == 1){
            throw new SemanticException("FUNCTION as already declarated!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        cod3.append(functionName).append(":").append("\n");
        cod3.append("BeginFunc").append("\n");
        //Adiciona a função a tabela de simbolos
        symbols.add(new SymbolTable(token.getTexto(), currentType, 0, currentScope, 1));
        token = scanner.nextToken();
        if (token.getTipo() != Token.TK_APARENTESES) {
            throw new SyntaxException("( Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        ListaParametros(functionName, 0);
        if (token.getTipo() != Token.TK_FPARENTESES) {
            throw new SyntaxException(") Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        Declaracao();
        if (hasReturn == 0) {
            throw new SemanticException("FUNCTION must be RETURN statement!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        cod3.append("EndFunc").append("\n");
        hasReturn = 0;
    }
    
    public void Tipo(){
        if (token.getTipo() != Token.TK_INTEIRO && token.getTipo() != Token.TK_BOOLEANO) {
            throw new SyntaxException("TYPE Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
    }
    
    public void Identificador(){
        if (token.getTipo() != Token.TK_IDENTIFICADOR) {
            throw new SyntaxException("IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
    }
    
    public void Variavel(){
        if (token.getTipo() != Token.TK_INTEIRO && token.getTipo() != Token.TK_BOOLEANO) {
            throw new SyntaxException("TYPE Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        int tipoAux = token.getTipo();
        token = scanner.nextToken();
        Identificador();
        //Verifica se a variavel já foi declarada
        int aux = 0;
        int isFunction = 0;
        for (int i = 0; i < symbols.size(); i++) {
            if(token.getTexto().equals(symbols.get(i).getName()) && currentScope == symbols.get(i).getScope()){
                aux = 1;
                isFunction = symbols.get(i).getFunction();
                break;
            }
        }
        if(aux == 1 && isFunction == 0){
            throw new SemanticException("VARIABLE as already declarated!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        //Adiciona a variavel a tabela de simbolos
        symbols.add(new SymbolTable(token.getTexto(), tipoAux, 0, currentScope, 0));
        if(tipoAux == 19){
            cod3.append("id").append(currentId).append(" = ").append("0").append("\n");
        } else {
            cod3.append("id").append(currentId).append(" = ").append("false").append("\n");
        }
        currentId++;
    }
    
    public void ListaParametros(String functionName, int order){
        token = scanner.nextToken();
        if(token != null && token.getTipo() != Token.TK_FPARENTESES){
            if (token.getTipo() != Token.TK_INTEIRO && token.getTipo() != Token.TK_BOOLEANO) {
                throw new SyntaxException("TYPE Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            int tipo = token.getTipo();
            token = scanner.nextToken();
            if (token.getTipo() != Token.TK_IDENTIFICADOR) {
                throw new SyntaxException("IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            String parameterName = token.getTexto();
            //Verifica se a variavel já foi declarada
            int aux = 0;
            for (int i = 0; i < parameter.size(); i++) {
                if(token.getTexto().equals(parameter.get(i).getName()) && functionName.equals(parameter.get(i).getFunction())){
                    aux = 1;
                    break;
                }
            }
            if(aux == 1){
                throw new SemanticException("PARAMETER as already declarated!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            parameter.add(new ParameterTable(functionName, parameterName, tipo, order));
            ListaParametros(functionName, (order + 1));
        }
    }
    
    public void Declaracao() {
        if (token.getTipo() != Token.TK_ACHAVES) {
            throw new SyntaxException("{ Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        //Garante todas as declarações dentro de uma mesma declaração pertencente a uma função (está confuso, mas é isso ai)
        while(token.getTipo() != Token.TK_FCHAVES){
            if(token.getTipo() == Token.TK_IMPRIMIR){
                Imprimir();
            }
            if(token.getTipo() == Token.TK_SE){
                DeclaracaoSelecao();
            }
            if(token.getTipo() == Token.TK_ENQUANTO){
                DeclaracaoIteracao();
            }
            if(token.getTipo() == Token.TK_CONTINUE || token.getTipo() == Token.TK_PAUSA || token.getTipo() == Token.TK_RETORNO){
                DeclaracaoSalto();
            }
            if(token.getTipo() != Token.TK_FCHAVES){
                Expressao();
            }
        }
        if (token.getTipo() != Token.TK_FCHAVES) {
            throw new SyntaxException("} Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
    }
    
    public void Imprimir(){
        if (token.getTipo() != Token.TK_IMPRIMIR) {
            throw new SyntaxException("PRINT Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        if (token.getTipo() != Token.TK_APARENTESES) {
            throw new SyntaxException("( Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        Expressao();
        cod3.append("print temp").append(currentTemp-1).append("\n");
        if (token.getTipo() != Token.TK_FPARENTESES) {
            throw new SyntaxException(") Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
    }
    
    public void DeclaracaoSelecao(){
        if (token.getTipo() != Token.TK_SE) {
            throw new SyntaxException("IF Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        if (token.getTipo() != Token.TK_APARENTESES) {
            throw new SyntaxException("( Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        wrongExpression = 1;
        Expressao();
        cod3.append("ifFalse ").append("temp").append(currentTemp-1).append(" goto ").append("fimIf").append(currentIf).append("\n");
        if(wrongExpression == 1){
            throw new SemanticException("Wrong EXPRESSION inside IF declaration!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        if (token.getTipo() != Token.TK_FPARENTESES) {
            throw new SyntaxException(") Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        Declaracao();
        token = scanner.nextToken();
        if (token.getTipo() == Token.TK_SENAO) {
            cod3.append("goto L_fimIf").append(currentIf).append("\n");
            cod3.append("fimIf").append(currentIf).append(":").append("\n");
            token = scanner.nextToken();
            Declaracao();
            token = scanner.nextToken();
            cod3.append("L_fimIf").append(currentIf).append(":").append("\n");
        } else {
            cod3.append("fimIf").append(currentIf).append(":").append("\n");
        }
        currentIf++;
    }
    
    public void DeclaracaoIteracao(){
        hasLoop = hasLoop + 1;
        if (token.getTipo() != Token.TK_ENQUANTO) {
            throw new SyntaxException("WHILE Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        if (token.getTipo() != Token.TK_APARENTESES) {
            throw new SyntaxException("( Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        cod3.append("W").append(currentWhile).append(":").append("\n");
        wrongExpression = 1;
        Expressao();
        cod3.append("ifFalse ").append("temp").append(currentTemp-1).append(" goto ").append("fimW").append(currentWhile).append("\n");
        if(wrongExpression == 1){
            throw new SemanticException("Wrong EXPRESSION inside WHILE declaration!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        if (token.getTipo() != Token.TK_FPARENTESES) {
            throw new SyntaxException(") Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        Declaracao();
        token = scanner.nextToken();
        cod3.append("fimW").append(currentWhile).append(":").append("\n");
        currentWhile++;
        hasLoop = hasLoop - 1;
    }
    
    public void DeclaracaoSalto(){
        if(token.getTipo() == Token.TK_RETORNO){
            int contador = 0;
            hasReturn = 1;
            token = scanner.nextToken();
            if(currentType == 20){
                if (token.getTipo() != Token.TK_FALSO && token.getTipo() != Token.TK_VERDADEIRO && token.getTipo() != Token.TK_IDENTIFICADOR) {
                    throw new SemanticException("BOOLEAN RETURN Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                }
                else if(token.getTipo() == Token.TK_IDENTIFICADOR){
                    int aux = 0;
                    for (int i = 0; i < symbols.size(); i++) {
                        //O if abaixo é gambiarra, mas funciona
                        if(symbols.get(i).getFunction() == 1){
                            contador--;
                        }
                        if(token.getTexto().equals(symbols.get(i).getName()) && currentScope == symbols.get(i).getScope()){
                            aux = 1;
                            if(symbols.get(i).getFunction() == 1){
                                continue;
                            }
                            if(currentType == symbols.get(i).getType()){
                                aux = 2;
                                break;
                            }
                        }
                        contador++;
                    }
                    if(aux == 0){
                        throw new SemanticException("VARIABLE not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    if(aux == 1){
                        throw new SemanticException("FUNCTION and RETURN must be same types!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    cod3.append("return ").append("id").append(contador).append("\n");
                } else {
                    cod3.append("return ").append(token.getTexto()).append("\n");
                }
            }
            else if (currentType == 19) {
                if (token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR) {
                    throw new SemanticException("INTEGER RETURN Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                }
                else if(token.getTipo() == Token.TK_IDENTIFICADOR){
                    int aux = 0;
                    for (int i = 0; i < symbols.size(); i++) {
                        //O if abaixo é gambiarra, mas funciona
                        if(symbols.get(i).getFunction() == 1){
                            contador--;
                        }
                        if(token.getTexto().equals(symbols.get(i).getName()) && currentScope == symbols.get(i).getScope()){
                            aux = 1;
                            if(symbols.get(i).getFunction() == 1){
                                continue;
                            }
                            if(currentType == symbols.get(i).getType()){
                                aux = 2;
                                break;
                            }
                        }
                        contador++;
                    }
                    if(aux == 0){
                        throw new SemanticException("VARIABLE not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    if(aux == 1){
                        throw new SemanticException("FUNCTION and RETURN must be same types!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    cod3.append("return ").append("id").append(contador).append("\n");
                } else {
                    cod3.append("return ").append(token.getTexto()).append("\n");
                }
            }
            token = scanner.nextToken();
        }
        else if(token.getTipo() == Token.TK_CONTINUE){
            if(hasLoop <= 0){
                throw new SemanticException("CONTINUE must be in a loop!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            token = scanner.nextToken();
            //Expressao();
            cod3.append("goto W").append(currentWhile).append("\n");
        }
        else if(token.getTipo() == Token.TK_PAUSA){
            if(hasLoop <= 0){
                throw new SemanticException("BREAK must be in a loop!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            token = scanner.nextToken();
            //Expressao();
            cod3.append("goto ").append("fimW").append(currentWhile).append("\n");
        }
    }
    
    public void Expressao(){
        if(token != null){
            if(token.getTipo() == Token.TK_NUMERO){
                cod3.append("temp").append(currentTemp).append(" = ").append(token.getTexto());
                token = scanner.nextToken();
                if(token.getTipo() == Token.TK_MAIS || token.getTipo() == Token.TK_MENOS || token.getTipo() == Token.TK_MULTIPLICADOR || token.getTipo() == Token.TK_DIVISOR){
                    ExpressaoAritmetica(); //+, -, *, /
                }
                if(token.getTipo() == Token.TK_MAIOR || token.getTipo() == Token.TK_MENOR || token.getTipo() == Token.TK_MAIORIGUAL || token.getTipo() == Token.TK_MENORIGUAL){
                    ExpressaoRelacional(); //>, <, >=, <=
                    wrongExpression = 0;
                    currentTemp++;
                }
                if(token.getTipo() == Token.TK_IGUALDADE || token.getTipo() == Token.TK_DIFERENCA){
                    ExpressaoIgualdadeAritmetica(); //==, !=
                    wrongExpression = 0;
                    currentTemp++;
                }
            }
            if(token.getTipo() == Token.TK_IDENTIFICADOR){
                //Verifica se a variavel ou função foi inicializada antes de ser chamada
                int aux = 0;
                int isFunction = 0;
                int tipo = 0;
                for (int i = 0; i < symbols.size(); i++) {
                    if(token.getTexto().equals(symbols.get(i).getName())){
                        isFunction = symbols.get(i).getFunction();
                        if(isFunction == 1){
                            aux = 1;
                            tipo = symbols.get(i).getType();
                            break;
                        }
                        else if(currentScope == symbols.get(i).getScope()){
                            aux = 1;
                            tipo = symbols.get(i).getType();
                            break;
                        }
                    }
                }
                if(aux == 0){
                    throw new SemanticException("VARIABLE or FUNCTION not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                }
                String functionName = token.getTexto();
                int contador1 = 0;
                for (int j = 0; j < symbols.size(); j++) {
                    //O if abaixo é gambiarra, mas funciona
                    if(symbols.get(j).getFunction() == 1){
                        contador1--;
                    }
                    if(token.getTexto().equals(symbols.get(j).getName()) && currentScope == symbols.get(j).getScope()){
                        if(symbols.get(j).getFunction() == 1){
                            continue;
                        } else {
                            break;
                        }
                    }
                    contador1++;
                }
                token = scanner.nextToken();
                if(token.getTipo() == Token.TK_APARENTESES){
                    if(isFunction != 1){
                        throw new SemanticException("FUNCTION expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    token = scanner.nextToken();
                    //Contando quantos parametros existem nessa função
                    int contador = 0;
                    for (int i = 0; i < parameter.size(); i++) {
                        if(functionName.equals(parameter.get(i).getFunction())){
                            contador++;
                        }
                    }
                    int contadorAux = contador;
                    while(contador > 0){
                        if(token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR && token.getTipo() != Token.TK_VERDADEIRO && token.getTipo() != Token.TK_FALSO){
                            throw new SyntaxException("NUMBER or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                        }
                        contador--;
                        cod3.append("param ").append(token.getTexto()).append("\n");
                        token = scanner.nextToken();
                    }
                    if (token.getTipo() != Token.TK_FPARENTESES) {
                        throw new SyntaxException(") Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    cod3.append("temp").append(currentTemp).append(" = call ").append(functionName).append(", ").append(contadorAux).append("\n");
                    currentTemp++;
                    token = scanner.nextToken();
                }
                if(token.getTipo() == Token.TK_ATRIBUICAO){
                    auxId = contador1;
                    if(isFunction == 1){
                        throw new SemanticException("FUNCTION assigment is not permited!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    ExpressaoAtribuicao(tipo); //=
                    cod3.append("id").append(auxId).append(" = temp").append(currentTemp).append("\n");
                    //auxId = 0;
                    currentTemp++;
                }
                if(token.getTipo() == Token.TK_MAIS || token.getTipo() == Token.TK_MENOS || token.getTipo() == Token.TK_MULTIPLICADOR || token.getTipo() == Token.TK_DIVISOR){
                    if(isFunction == 1){
                        if(tipo != 19){
                            throw new SemanticException("FUNCTION must be INTEGER!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                        }
                    }
                    ExpressaoAritmetica(); //+, -, *, /
                    cod3.append("id").append(auxId).append(" = temp").append(currentTemp-1).append("\n");
                    //auxId = 0;
                }
                if(token.getTipo() == Token.TK_MAIOR || token.getTipo() == Token.TK_MENOR || token.getTipo() == Token.TK_MAIORIGUAL || token.getTipo() == Token.TK_MENORIGUAL){
                    if(isFunction == 1){
                        if(tipo != 19){
                            throw new SemanticException("FUNCTION must be INTEGER!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                        }
                    }
                    cod3.append("temp").append(currentTemp).append(" = ").append("id").append(contador1);
                    ExpressaoRelacional(); //>, <, >=, <=
                    wrongExpression = 0;
                    currentTemp++;
                }
                if(token.getTipo() == Token.TK_IGUALDADE || token.getTipo() == Token.TK_DIFERENCA){
                    cod3.append("temp").append(currentTemp).append(" = ").append("id").append(contador1);
                    if(tipo == 20){
                        ExpressaoIgualdadeBooleana(); //==, !=
                        wrongExpression = 0;
                        currentTemp++;
                    } else {
                        ExpressaoIgualdadeAritmetica(); //==, !=
                        wrongExpression = 0;
                        currentTemp++;
                    }
                }
                auxId = 0;
            }
            if(token.getTipo() == Token.TK_VERDADEIRO || token.getTipo() == Token.TK_FALSO){
                cod3.append("temp").append(currentTemp).append(" = ").append(token.getTexto());
                token = scanner.nextToken();
                ExpressaoIgualdadeBooleana(); //==, !=
                wrongExpression = 0;
                currentTemp++;
            }
            if(token.getTipo() == Token.TK_INTEIRO || token.getTipo() == Token.TK_BOOLEANO){
                Variavel();
                //Isso abaixo evita o 'Error!', ler um token antes de chamar o fim do programa com }
                token = scanner.nextToken();
            }
        }
    }
    
    public void ExpressaoAtribuicao(int type){
        if(token.getTipo() != Token.TK_ATRIBUICAO) {
            throw new SyntaxException("ASSIGMENT OPERATOR Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        token = scanner.nextToken();
        if(type == 19){
            if(token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR) {
                throw new SemanticException("NUMBER or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            if(token.getTipo() == Token.TK_IDENTIFICADOR){
                //Verifica se a variavel ou função foi inicializada antes de ser chamada
                int aux = 0;
                int isFunction = 0;
                int tipo = 0;
                for (int i = 0; i < symbols.size(); i++) {
                    if(token.getTexto().equals(symbols.get(i).getName())){
                        if(symbols.get(i).getFunction() == 1){
                            aux = 1;
                            isFunction = symbols.get(i).getFunction();
                            tipo = symbols.get(i).getType();
                            break;
                        }
                        if(currentScope == symbols.get(i).getScope()){
                            aux = 1;
                            isFunction = symbols.get(i).getFunction();
                            tipo = symbols.get(i).getType();
                            break;
                        }
                    }
                }
                if(aux == 0){
                    throw new SemanticException("VARIABLE or FUNCTION not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                }
                if(type != tipo){
                    throw new SemanticException("Assignment of INTEGER expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                }
                String functionName = token.getTexto();
                token = scanner.nextToken();
                if(token.getTipo() == Token.TK_APARENTESES){
                    if(isFunction != 1){
                        throw new SemanticException("FUNCTION expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    token = scanner.nextToken();
                    int contador = 0;
                    for (int i = 0; i < parameter.size(); i++) {
                        if(functionName.equals(parameter.get(i).getFunction())){
                            contador++;
                        }
                    }
                    int contadorAux = contador;
                    while(contador > 0){
                        if(token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR && token.getTipo() != Token.TK_VERDADEIRO && token.getTipo() != Token.TK_FALSO){
                            throw new SyntaxException("NUMBER or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                        }
                        contador--;
                        cod3.append("param ").append(token.getTexto()).append("\n");
                        token = scanner.nextToken();
                    }
                    if (token.getTipo() != Token.TK_FPARENTESES) {
                        throw new SyntaxException(") Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    cod3.append("temp").append(currentTemp).append(" = ").append("call ").append(functionName).append(", ").append(contadorAux).append("\n");
                    token = scanner.nextToken();
                } else {
                    cod3.append("temp").append(currentTemp).append(" = ").append(functionName).append("\n");
                }
            } else {
                cod3.append("temp").append(currentTemp).append(" = ").append(token.getTexto()).append("\n");
                token = scanner.nextToken();
            }
        } else{
            if(token.getTipo() != Token.TK_VERDADEIRO && token.getTipo() != Token.TK_FALSO && token.getTipo() != Token.TK_IDENTIFICADOR) {
                throw new SemanticException("BOOLEAN or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            if(token.getTipo() == Token.TK_IDENTIFICADOR){
                //Verifica se a variavel ou função foi inicializada antes de ser chamada
                int aux = 0;
                int isFunction = 0;
                int tipo = 0;
                for (int i = 0; i < symbols.size(); i++) {
                    if(token.getTexto().equals(symbols.get(i).getName())){
                        if(symbols.get(i).getFunction() == 1){
                            aux = 1;
                            isFunction = symbols.get(i).getFunction();
                            tipo = symbols.get(i).getType();
                            break;
                        }
                        if(currentScope == symbols.get(i).getScope()){
                            aux = 1;
                            isFunction = symbols.get(i).getFunction();
                            tipo = symbols.get(i).getType();
                            break;
                        }
                    }
                }
                if(aux == 0){
                    throw new SemanticException("VARIABLE or FUNCTION not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                }
                if(type != tipo){
                    throw new SemanticException("Assignment of BOOLEAN expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                }
                String functionName = token.getTexto();
                token = scanner.nextToken();
                if(token.getTipo() == Token.TK_APARENTESES){
                    if(isFunction != 1){
                        throw new SemanticException("FUNCTION expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    token = scanner.nextToken();
                    int contador = 0;
                    for (int i = 0; i < parameter.size(); i++) {
                        if(functionName.equals(parameter.get(i).getFunction())){
                            contador++;
                        }
                    }
                    int contadorAux = contador;
                    while(contador > 0){
                        if(token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR && token.getTipo() != Token.TK_VERDADEIRO && token.getTipo() != Token.TK_FALSO){
                            throw new SyntaxException("NUMBER or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                        }
                        contador--;
                        cod3.append("param ").append(token.getTexto()).append("\n");
                        token = scanner.nextToken();
                    }
                    if (token.getTipo() != Token.TK_FPARENTESES) {
                        throw new SyntaxException(") Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
                    }
                    cod3.append("temp").append(currentTemp).append(" = ").append("call ").append(functionName).append(", ").append(contadorAux).append("\n");
                    token = scanner.nextToken();
                } else {
                    cod3.append("temp").append(currentTemp).append(" = ").append(functionName).append("\n");
                }
            } else {
                cod3.append("temp").append(currentTemp).append(" = ").append(token.getTexto()).append("\n");
                token = scanner.nextToken();
            }
        }
    }
    
    public void ExpressaoAritmetica(){
        if(token.getTipo() != Token.TK_MAIS && token.getTipo() != Token.TK_MENOS && token.getTipo() != Token.TK_MULTIPLICADOR && token.getTipo() != Token.TK_DIVISOR) {
            throw new SyntaxException("ARITHMETIC OPERATOR Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        String op = token.getTexto();
        token = scanner.nextToken();
        if(token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR) {
            throw new SyntaxException("NUMBER or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        if(token.getTipo() == Token.TK_IDENTIFICADOR){
            int contador = 0;
            int aux = 0;
            int tipo = 0;
            for (int i = 0; i < symbols.size(); i++) {
                if(symbols.get(i).getFunction() == 1){
                    contador--;
                }
                if(token.getTexto().equals(symbols.get(i).getName()) && currentScope == symbols.get(i).getScope()){
                    if(symbols.get(i).getFunction() == 1){
                        continue;
                    } else {
                        aux = 1;
                        tipo = symbols.get(i).getType();
                        break;
                    }
                }
                contador++;
            }
            if(aux == 0){
                throw new SemanticException("VARIABLE not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            if(tipo != 19){
                throw new SemanticException("INTEGER expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            cod3.append("temp").append(currentTemp).append(" = temp").append(currentTemp-1).append(" ").append(op).append(" ").append(token.getTexto()).append("\n");
            currentTemp++;
        } else {
            cod3.append("temp").append(currentTemp).append(" = temp").append(currentTemp-1).append(" ").append(op).append(" ").append(token.getTexto()).append("\n");
            currentTemp++;
        }
        token = scanner.nextToken();
        if(token.getTipo() == Token.TK_MAIS || token.getTipo() == Token.TK_MENOS || token.getTipo() == Token.TK_MULTIPLICADOR || token.getTipo() == Token.TK_DIVISOR){
            ExpressaoAritmetica();
        }
    }
    
    public void ExpressaoRelacional(){
        if(token.getTipo() != Token.TK_MAIOR && token.getTipo() != Token.TK_MENOR && token.getTipo() != Token.TK_MAIORIGUAL && token.getTipo() != Token.TK_MENORIGUAL) {
            throw new SyntaxException("RELATIONAL OPERATOR Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        cod3.append(" ").append(token.getTexto());
        token = scanner.nextToken();
        if(token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR) {
            throw new SyntaxException("NUMBER or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        if(token.getTipo() == Token.TK_IDENTIFICADOR){
            int contador = 0;
            int aux = 0;
            int tipo = 0;
            for (int i = 0; i < symbols.size(); i++) {
                if(symbols.get(i).getFunction() == 1){
                    contador--;
                }
                if(token.getTexto().equals(symbols.get(i).getName()) && currentScope == symbols.get(i).getScope()){
                    if(symbols.get(i).getFunction() == 1){
                        continue;
                    } else {
                        aux = 1;
                        tipo = symbols.get(i).getType();
                        break;
                    }
                }
                contador++;
            }
            if(aux == 0){
                throw new SemanticException("VARIABLE not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            if(tipo != 19){
                throw new SemanticException("INTEGER expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            cod3.append(" id").append(contador).append("\n");
        } else {
            cod3.append(" ").append(token.getTexto()).append("\n");
        }
        token = scanner.nextToken();
    }
    
    public void ExpressaoIgualdadeAritmetica(){
        if(token.getTipo() != Token.TK_IGUALDADE && token.getTipo() != Token.TK_DIFERENCA) {
            throw new SyntaxException("EQUALITY OPERATOR Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        cod3.append(" ").append(token.getTexto());
        token = scanner.nextToken();
        if(token.getTipo() != Token.TK_NUMERO && token.getTipo() != Token.TK_IDENTIFICADOR) {
            throw new SyntaxException("NUMBER or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        if(token.getTipo() == Token.TK_IDENTIFICADOR){
            int contador = 0;
            int aux = 0;
            int tipo = 0;
            for (int i = 0; i < symbols.size(); i++) {
                if(symbols.get(i).getFunction() == 1){
                    contador--;
                }
                if(token.getTexto().equals(symbols.get(i).getName()) && currentScope == symbols.get(i).getScope()){
                    if(symbols.get(i).getFunction() == 1){
                        continue;
                    } else {
                        aux = 1;
                        tipo = symbols.get(i).getType();
                        break;
                    }
                }
                contador++;
            }
            if(aux == 0){
                throw new SemanticException("VARIABLE not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            if(tipo != 19){
                throw new SemanticException("INTEGER expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            cod3.append(" id").append(contador).append("\n");
        } else {
            cod3.append(" ").append(token.getTexto()).append("\n");
        }
        token = scanner.nextToken();
    }
    
    public void ExpressaoIgualdadeBooleana(){
        if(token.getTipo() != Token.TK_IGUALDADE && token.getTipo() != Token.TK_DIFERENCA) {
            throw new SyntaxException("EQUALITY OPERATOR Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        cod3.append(" ").append(token.getTexto());
        token = scanner.nextToken();
        if(token.getTipo() != Token.TK_VERDADEIRO && token.getTipo() != Token.TK_FALSO && token.getTipo() != Token.TK_IDENTIFICADOR) {
            throw new SyntaxException("BOOLEAN or IDENTIFIER Expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
        }
        if(token.getTipo() == Token.TK_IDENTIFICADOR){
            int contador = 0;
            int aux = 0;
            int tipo = 0;
            for (int i = 0; i < symbols.size(); i++) {
                if(symbols.get(i).getFunction() == 1){
                    contador--;
                }
                if(token.getTexto().equals(symbols.get(i).getName()) && currentScope == symbols.get(i).getScope()){
                    if(symbols.get(i).getFunction() == 1){
                        continue;
                    } else {
                        aux = 1;
                        tipo = symbols.get(i).getType();
                        break;
                    }
                }
                contador++;
            }
            if(aux == 0){
                throw new SemanticException("VARIABLE not initialized!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            if(tipo != 20){
                throw new SemanticException("BOOLEAN expected!, found " + Token.TK_TEXT[token.getTipo()] + " (" + token.getTexto() + ") at LINE " + token.getLine() + " and COLUMN " + token.getColumn());
            }
            cod3.append(" id").append(contador).append("\n");
        } else {
            cod3.append(" ").append(token.getTexto()).append("\n");
        }
        token = scanner.nextToken();
    }
    
}
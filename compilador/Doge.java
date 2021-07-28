package compilador;

import exception.LexicalException;
import exception.SemanticException;
import exception.SyntaxException;
import lexical.Scanner;
import parser.Parser;

/**
 *
 * @author Savio
 */
public class Doge {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner("C:\\Users\\Savio\\Documents\\input.doge");
            Parser parser = new Parser(scanner);
            parser.Main();
            System.out.println("Compilation Sucessfull");
        } catch (LexicalException ex) {
            System.out.println("Lexical error: " + ex.getMessage());
        } catch (SemanticException ex) {
            System.out.println("Semantic error: " + ex.getMessage());
        } catch (SyntaxException ex) {
            System.out.println("Syntax error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error!");
            System.out.println(ex.getClass().getName());
        }
    }

}
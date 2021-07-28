package lexical;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import exception.LexicalException;

/**
 *
 * @author Savio
 */
public class Scanner {

    private char[] content;
    private int estado;
    private int posicao;
    private int line;
    private int column;

    public Scanner(String filename) {
        try {
            line = 1;
            column = 0;
            String txtconteudo;
            txtconteudo = new String(Files.readAllBytes(Paths.get(filename).toAbsolutePath()), StandardCharsets.UTF_8);
            content = txtconteudo.toCharArray();
            posicao = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Token nextToken() {
        char currentChar;
        Token token;
        String termo = "";
        if (isEOF()) {
            return null;
        }
        estado = 0;
        while (true) {
            currentChar = nextChar();
            column++;

            switch (estado) {
                case 0: {
                    if (isSpace(currentChar)) {
                        estado = 0;
                    } else if (isChar(currentChar)) {
                        /**
                        if (currentChar == 'm') {
                            estado = 7;
                            termo += currentChar;
                        } */ if (currentChar == 'p') {
                            estado = 8;
                            termo += currentChar;
                        } else if (currentChar == 'v') {
                            estado = 9;
                            termo += currentChar;
                        } else if (currentChar == 'i') {
                            estado = 10;
                            termo += currentChar;
                        } else if (currentChar == 'b') {
                            estado = 11;
                            termo += currentChar;
                        } else if (currentChar == 'w') {
                            estado = 12;
                            termo += currentChar;
                        } else if (currentChar == 'e') {
                            estado = 13;
                            termo += currentChar;
                        } else if (currentChar == 'c') {
                            estado = 14;
                            termo += currentChar;
                        } else if (currentChar == 'r') {
                            estado = 15;
                            termo += currentChar;
                        } else if (currentChar == 't') {
                            estado = 16;
                            termo += currentChar;
                        } else if (currentChar == 'f') {
                            estado = 17;
                            termo += currentChar;
                        } else {
                            estado = 1;
                            termo += currentChar;
                        }
                    } else if (isDigit(currentChar)) {
                        estado = 2;
                        termo += currentChar;
                    } else if (isEqualSignal(currentChar)) {
                        estado = 3;
                        termo += currentChar;
                    } else if (isDiferenceSignal(currentChar)) {
                        estado = 4;
                        termo += currentChar;
                    } else if (isLogicOperator(currentChar)) {
                        if (currentChar == '<') {
                            termo += currentChar;
                            estado = 5;
                        } else {
                            termo += currentChar;
                            estado = 6;
                        }
                    } else if (isNumOperator(currentChar)) {
                        if (currentChar == '+') {
                            termo += currentChar;
                            token = new Token();
                            token.setTipo(Token.TK_MAIS);
                            token.setTexto(termo);
                            token.setLine(line);
                            token.setColumn(column - termo.length());
                            return token;
                        } else if (currentChar == '-') {
                            termo += currentChar;
                            token = new Token();
                            token.setTipo(Token.TK_MENOS);
                            token.setTexto(termo);
                            token.setLine(line);
                            token.setColumn(column - termo.length());
                            return token;
                        } else if (currentChar == '*') {
                            termo += currentChar;
                            token = new Token();
                            token.setTipo(Token.TK_MULTIPLICADOR);
                            token.setTexto(termo);
                            token.setLine(line);
                            token.setColumn(column - termo.length());
                            return token;
                        } else {
                            termo += currentChar;
                            token = new Token();
                            token.setTipo(Token.TK_DIVISOR);
                            token.setTexto(termo);
                            token.setLine(line);
                            token.setColumn(column - termo.length());
                            return token;
                        }
                    } else if (isOpenChaves(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_ACHAVES);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else if (isCloseChaves(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_FCHAVES);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else if (isOpenPar(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_APARENTESES);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else if (isClosePar(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_FPARENTESES);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else if(isEndLine(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_FIM);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else if(isVirgula(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_VIRGULA);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else {
                        throw new LexicalException("Unrecognized char at line " + line + " column " + column);
                    }
                    break;
                }
                case 1: {
                    if (isChar(currentChar) || isDigit(currentChar)) {
                        estado = 1;
                        termo += currentChar;
                    } else if (isSpace(currentChar)
                            || isEqualSignal(currentChar)
                            || isDiferenceSignal(currentChar)
                            || isLogicOperator(currentChar)
                            || isNumOperator(currentChar)
                            || isOpenChaves(currentChar)
                            || isCloseChaves(currentChar)
                            || isOpenPar(currentChar)
                            || isClosePar(currentChar)
                            || isEndLine(currentChar)
                            || isVirgula(currentChar)) {
                        
                        if(!isEOF(currentChar)){
                            back();
                        }
                        token = new Token();
                        token.setTipo(Token.TK_IDENTIFICADOR);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else {
                        throw new LexicalException("Malformed identifier");
                    }
                    break;
                }
                case 2: {
                    if (isDigit(currentChar)) {
                        estado = 2;
                        termo += currentChar;
                    } else if (!isDigit(currentChar)) {
                        if(!isEOF(currentChar)){
                            back();
                        }
                        token = new Token();
                        token.setTipo(Token.TK_NUMERO);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else {
                        throw new LexicalException("Unrecognized number");
                    }
                    break;
                }
                case 3: {
                    if (isEqualSignal(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_IGUALDADE);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else if (!isEqualSignal(currentChar)) {
                        back();
                        token = new Token();
                        token.setTipo(Token.TK_ATRIBUICAO);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else {
                        throw new LexicalException("Unrecognized char at line " + line + " column " + column);
                    }
                }
                case 4: {
                    if (isEqualSignal(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_DIFERENCA);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else {
                        throw new LexicalException("Unrecognized char at line " + line + " column " + column);
                    }
                }
                case 5: {
                    if (isEqualSignal(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_MENORIGUAL);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else {
                        back();
                        token = new Token();
                        token.setTipo(Token.TK_MENOR);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    }
                }
                case 6: {
                    if (isEqualSignal(currentChar)) {
                        termo += currentChar;
                        token = new Token();
                        token.setTipo(Token.TK_MAIORIGUAL);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    } else {
                        back();
                        token = new Token();
                        token.setTipo(Token.TK_MAIOR);
                        token.setTexto(termo);
                        token.setLine(line);
                        token.setColumn(column - termo.length());
                        return token;
                    }
                }
                /**
                case 7: {
                    String termoAux = "";
                    if (currentChar == 'a') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'i') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'n') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (isChar(currentChar) || isDigit(currentChar)) {
                                    termoAux += currentChar;
                                    termo += termoAux;
                                    estado = 1;
                                } else {
                                    back();
                                    termo += termoAux;
                                    token = new Token();
                                    token.setTipo(Token.TK_MAIN);
                                    token.setTexto(termo);
                                    token.setLine(line);
                                    token.setColumn(column - termo.length());
                                    return token;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                } */
                case 8: {
                    String termoAux = "";
                    if (currentChar == 'r') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'i') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'n') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (currentChar == 't') {
                                    termoAux += currentChar;
                                    currentChar = nextChar();
                                    column++;
                                    if (isChar(currentChar) || isDigit(currentChar)) {
                                        termoAux += currentChar;
                                        termo += termoAux;
                                        estado = 1;
                                    } else {
                                        back();
                                        termo += termoAux;
                                        token = new Token();
                                        token.setTipo(Token.TK_IMPRIMIR);
                                        token.setTexto(termo);
                                        token.setLine(line);
                                        token.setColumn(column - termo.length());
                                        return token;
                                    }
                                } else {
                                    back();
                                    back();
                                    back();
                                    back();
                                    estado = 1;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 9: {
                    String termoAux = "";
                    if (currentChar == 'o') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'i') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'd') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (isChar(currentChar) || isDigit(currentChar)) {
                                    termoAux += currentChar;
                                    termo += termoAux;
                                    estado = 1;
                                } else {
                                    back();
                                    termo += termoAux;
                                    token = new Token();
                                    token.setTipo(Token.TK_VAZIO);
                                    token.setTexto(termo);
                                    token.setLine(line);
                                    token.setColumn(column - termo.length());
                                    return token;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 10: {
                    String termoAux = "";
                    if (currentChar == 'f') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (isChar(currentChar) || isDigit(currentChar)) {
                            termoAux += currentChar;
                            termo += termoAux;
                            estado = 1;
                        } else {
                            back();
                            termo += termoAux;
                            token = new Token();
                            token.setTipo(Token.TK_SE);
                            token.setTexto(termo);
                            token.setLine(line);
                            token.setColumn(column - termo.length());
                            return token;
                        }
                    } else if (currentChar == 'n') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 't') {
                            termoAux += currentChar;
                            termo += termoAux;
                            token = new Token();
                            token.setTipo(Token.TK_INTEIRO);
                            token.setTexto(termo);
                            token.setLine(line);
                            token.setColumn(column - termo.length());
                            return token;
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 11: {
                    String termoAux = "";
                    if (currentChar == 'o') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'o') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'l') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (isChar(currentChar) || isDigit(currentChar)) {
                                    termoAux += currentChar;
                                    termo += termoAux;
                                    estado = 1;
                                } else {
                                    back();
                                    termo += termoAux;
                                    token = new Token();
                                    token.setTipo(Token.TK_BOOLEANO);
                                    token.setTexto(termo);
                                    token.setLine(line);
                                    token.setColumn(column - termo.length());
                                    return token;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    }
                    if (currentChar == 'r') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'e') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'a') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (currentChar == 'k') {
                                    termoAux += currentChar;
                                    currentChar = nextChar();
                                    column++;
                                    if (isChar(currentChar) || isDigit(currentChar)) {
                                        termoAux += currentChar;
                                        termo += termoAux;
                                        estado = 1;
                                    } else {
                                        back();
                                        termo += termoAux;
                                        token = new Token();
                                        token.setTipo(Token.TK_PAUSA);
                                        token.setTexto(termo);
                                        token.setLine(line);
                                        token.setColumn(column - termo.length());
                                        return token;
                                    }
                                } else {
                                    back();
                                    back();
                                    back();
                                    back();
                                    estado = 1;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 12: {
                    String termoAux = "";
                    if (currentChar == 'h') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'i') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'l') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (currentChar == 'e') {
                                    termoAux += currentChar;
                                    currentChar = nextChar();
                                    column++;
                                    if (isChar(currentChar) || isDigit(currentChar)) {
                                        termoAux += currentChar;
                                        termo += termoAux;
                                        estado = 1;
                                    } else {
                                        back();
                                        termo += termoAux;
                                        token = new Token();
                                        token.setTipo(Token.TK_ENQUANTO);
                                        token.setTexto(termo);
                                        token.setLine(line);
                                        token.setColumn(column - termo.length());
                                        return token;
                                    }
                                } else {
                                    back();
                                    back();
                                    back();
                                    back();
                                    estado = 1;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 13: {
                    String termoAux = "";
                    if (currentChar == 'l') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 's') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'e') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (isChar(currentChar) || isDigit(currentChar)) {
                                    termoAux += currentChar;
                                    termo += termoAux;
                                    estado = 1;
                                } else {
                                    back();
                                    termo += termoAux;
                                    token = new Token();
                                    token.setTipo(Token.TK_SENAO);
                                    token.setTexto(termo);
                                    token.setLine(line);
                                    token.setColumn(column - termo.length());
                                    return token;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 14: {
                    String termoAux = "";
                    if (currentChar == 'o') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'n') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 't') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (currentChar == 'i') {
                                    termoAux += currentChar;
                                    currentChar = nextChar();
                                    column++;
                                    if (currentChar == 'n') {
                                        termoAux += currentChar;
                                        currentChar = nextChar();
                                        column++;
                                        if (currentChar == 'u') {
                                            termoAux += currentChar;
                                            currentChar = nextChar();
                                            column++;
                                            if (currentChar == 'e') {
                                                termoAux += currentChar;
                                                currentChar = nextChar();
                                                column++;
                                                if (isChar(currentChar) || isDigit(currentChar)) {
                                                    termoAux += currentChar;
                                                    termo += termoAux;
                                                    estado = 1;
                                                } else {
                                                    back();
                                                    termo += termoAux;
                                                    token = new Token();
                                                    token.setTipo(Token.TK_CONTINUE);
                                                    token.setTexto(termo);
                                                    token.setLine(line);
                                                    token.setColumn(column - termo.length());
                                                    return token;
                                                }
                                            } else {
                                                back();
                                                back();
                                                back();
                                                back();
                                                back();
                                                back();
                                                back();
                                                estado = 1;
                                            }
                                        } else {
                                            back();
                                            back();
                                            back();
                                            back();
                                            back();
                                            back();
                                            estado = 1;
                                        }
                                    } else {
                                        back();
                                        back();
                                        back();
                                        back();
                                        back();
                                        estado = 1;
                                    }
                                } else {
                                    back();
                                    back();
                                    back();
                                    back();
                                    estado = 1;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 15: {
                    String termoAux = "";
                    if (currentChar == 'e') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 't') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'u') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (currentChar == 'r') {
                                    termoAux += currentChar;
                                    currentChar = nextChar();
                                    column++;
                                    if (currentChar == 'n') {
                                        termoAux += currentChar;
                                        currentChar = nextChar();
                                        column++;
                                        if (isChar(currentChar) || isDigit(currentChar)) {
                                            termoAux += currentChar;
                                            termo += termoAux;
                                            estado = 1;
                                        } else {
                                            back();
                                            termo += termoAux;
                                            token = new Token();
                                            token.setTipo(Token.TK_RETORNO);
                                            token.setTexto(termo);
                                            token.setLine(line);
                                            token.setColumn(column - termo.length());
                                            return token;
                                        }
                                    } else {
                                        back();
                                        back();
                                        back();
                                        back();
                                        back();
                                        estado = 1;
                                    }
                                } else {
                                    back();
                                    back();
                                    back();
                                    back();
                                    estado = 1;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 16: {
                    String termoAux = "";
                    if (currentChar == 'r') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'u') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'e') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (isChar(currentChar) || isDigit(currentChar)) {
                                    termoAux += currentChar;
                                    termo += termoAux;
                                    estado = 1;
                                } else {
                                    back();
                                    termo += termoAux;
                                    token = new Token();
                                    token.setTipo(Token.TK_VERDADEIRO);
                                    token.setTexto(termo);
                                    token.setLine(line);
                                    token.setColumn(column - termo.length());
                                    return token;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
                case 17: {
                    String termoAux = "";
                    if (currentChar == 'u') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'n') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 'c') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (currentChar == 't') {
                                    termoAux += currentChar;
                                    currentChar = nextChar();
                                    column++;
                                    if (currentChar == 'i') {
                                        termoAux += currentChar;
                                        currentChar = nextChar();
                                        column++;
                                        if (currentChar == 'o') {
                                            termoAux += currentChar;
                                            currentChar = nextChar();
                                            column++;
                                            if (currentChar == 'n') {
                                                termoAux += currentChar;
                                                currentChar = nextChar();
                                                column++;
                                                if (isChar(currentChar) || isDigit(currentChar)) {
                                                    termoAux += currentChar;
                                                    termo += termoAux;
                                                    estado = 1;
                                                } else {
                                                    back();
                                                    termo += termoAux;
                                                    token = new Token();
                                                    token.setTipo(Token.TK_FUNCAO);
                                                    token.setTexto(termo);
                                                    token.setLine(line);
                                                    token.setColumn(column - termo.length());
                                                    return token;
                                                }
                                            } else {
                                                back();
                                                back();
                                                back();
                                                back();
                                                back();
                                                back();
                                                back();
                                                estado = 1;
                                            }
                                        } else {
                                            back();
                                            back();
                                            back();
                                            back();
                                            back();
                                            back();
                                            estado = 1;
                                        }
                                    } else {
                                        back();
                                        back();
                                        back();
                                        back();
                                        back();
                                        estado = 1;
                                    }
                                } else {
                                    back();
                                    back();
                                    back();
                                    back();
                                    estado = 1;
                                }
                            } else {
                                back();
                                back();
                                back();
                                estado = 1;
                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    }
                    if (currentChar == 'a') {
                        termoAux += currentChar;
                        currentChar = nextChar();
                        column++;
                        if (currentChar == 'l') {
                            termoAux += currentChar;
                            currentChar = nextChar();
                            column++;
                            if (currentChar == 's') {
                                termoAux += currentChar;
                                currentChar = nextChar();
                                column++;
                                if (currentChar == 'e') {
                                    termoAux += currentChar;
                                    currentChar = nextChar();
                                    column++;
                                    if (isChar(currentChar) || isDigit(currentChar)) {
                                        termoAux += currentChar;
                                        termo += termoAux;
                                        estado = 1;
                                    } else {
                                        back();
                                        termo += termoAux;
                                        token = new Token();
                                        token.setTipo(Token.TK_FALSO);
                                        token.setTexto(termo);
                                        token.setLine(line);
                                        token.setColumn(column - termo.length());
                                        return token;
                                    }
                                } else {
                                    back();
                                    back();
                                    back();
                                    estado = 1;
                                }
                            } else {

                            }
                        } else {
                            back();
                            back();
                            estado = 1;
                        }
                    } else {
                        back();
                        estado = 1;
                    }
                    break;
                }
            }
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isEqualSignal(char c) {
        return c == '=';
    }

    private boolean isDiferenceSignal(char c) {
        return c == '!';
    }

    private boolean isLogicOperator(char c) {
        return c == '>' || c == '<';
    }

    private boolean isNumOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isOpenChaves(char c) {
        return c == '{';
    }

    private boolean isCloseChaves(char c) {
        return c == '}';
    }

    private boolean isOpenPar(char c) {
        return c == '(';
    }

    private boolean isClosePar(char c) {
        return c == ')';
    }
    
    private boolean isEndLine(char c) {
        return c == ';';
    }
    
    private boolean isVirgula(char c) {
        return c == ',';
    }
    
    private boolean isSpace(char c) {
        if (c == '\n') {
            line++;
            column = 0;
        }
	return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private char nextChar() {
        if (isEOF()) {
            return '\0';
	}
        return content[posicao++];
    }

    private boolean isEOF() {
        return posicao >= content.length;
    }
    
    private boolean isEOF(char c) {
        return c == '\0';
    }

    private void back() {
        posicao--;
        column--;
    }
}
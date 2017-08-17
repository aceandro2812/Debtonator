package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 8/16/2017.
 */

import java.util.Stack;
import java.util.StringTokenizer;

public class Infix {
    public static double infix(String expression) {
        // remove white space and add evaluation operator
        if (expression.equals("")) {
            return 0;
        } else {
            expression = expression.replaceAll("[\t\n ]", "") + "=";
            String operator = "*/+-=";
            // split up the operators from the values
            StringTokenizer tokenizer = new StringTokenizer(expression,
                    operator, true);
            Stack<String> operatorStack = new Stack<String>();
            Stack<String> valueStack = new Stack<String>();
            while (tokenizer.hasMoreTokens()) {
                // add the next token to the proper stack
                String token = tokenizer.nextToken();
                if (!operator.contains(token))
                    valueStack.push(token);
                else
                    operatorStack.push(token);
                // perform any pending operations
                resolve(valueStack, operatorStack);
            }
            // return the top of the value stack
            String lastOne = (String) valueStack.pop();
            try {
                return Double.parseDouble(lastOne);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public static int getPriority(String op) {
        if (op.equals("*") || op.equals("/"))
            return 1;
        else if (op.equals("+") || op.equals("-"))
            return 2;
        else if (op.equals("="))
            return 3;
        else
            return Integer.MIN_VALUE;
    }

    public static void resolve(Stack<String> values, Stack<String> operators) {
        while (operators.size() >= 2) {
            String first = (String) operators.pop();
            String second = (String) operators.pop();
            if (getPriority(first) < getPriority(second)) {
                operators.push(second);
                operators.push(first);
                return;
            } else {
                String firstValue = (String) values.pop();
                String secondValue;
                secondValue = (String) values.pop();
                values.push(getResults(secondValue, second, firstValue));
                operators.push(first);
            }
        }
    }

    public static String getResults(String operand1, String operator, String operand2) {
        // System.out.println("Performing " + operand1 + operator + operand2);
        double op1;
        double op2;
        try {
            op1 = Double.parseDouble(operand1);

        } catch (Exception e) {
            throw e;
        }
        try {
            op2 = Double.parseDouble(operand2);

        } catch (Exception e) {
            throw e;
        }
        if (operator.equals("*"))
            return "" + (op1 * op2);
        else if (operator.equals("/"))
            return "" + (op1 / op2);
        else if (operator.equals("+"))
            return "" + (op1 + op2);
        else if (operator.equals("-"))
            return "" + (op1 - op2);
        else
            return "0";
    }

    public static boolean checkSemantics(String str) {
        if (str.length() != 0) {
            //check for * or / at the beginning
            switch (str.charAt(0)) {
                case '*':
                case '/':
                    return false;
            }
            //check for the consecutive operators for strings with length>1
            if (str.length() > 1) {
                int index = 0;
                index = findFirstOp(str, 1);
                while (index < str.length() - 1 && index != -1) {
                    if (belongsToSign(str, index - 1) || belongsToSign(str, index + 1))
                        return false;
                    index = findFirstOp(str, index + 1);
                }
            }
            //check for invalid decimal points
            int firstPos = 0, operPos = 0, secPos = 0;
            firstPos = str.indexOf('.', firstPos);
            while (firstPos < str.length() - 1 && firstPos != -1) {
                secPos = str.indexOf('.', firstPos + 1);
                if (secPos == -1) {
                    firstPos = -1;
                } else {
                    operPos = str.indexOf('+', firstPos);
                    if (operPos == -1) {
                        operPos = str.indexOf('-', firstPos);
                    }
                    if (operPos == -1) {
                        operPos = str.indexOf('*', firstPos);
                    }
                    if (operPos == -1) {
                        operPos = str.indexOf('/', firstPos);
                    }
                    if (operPos > secPos || operPos == -1) {
                        return false;
                    }
                    firstPos = secPos + 1;
                }

            }
        }
        return true;
    }

    public static int findFirstOp(String str, int index1) {
        int result = -1;

        int var = str.indexOf('*', index1);
        if (var >= index1)
            if (result == -1 || var < result)
                result = var;

        var = str.indexOf('+', index1);
        if (var >= index1)
            if (result == -1 || var < result)
                result = var;

        var = str.indexOf('-', index1);
        if (var >= index1)
            if (result == -1 || var < result)
                result = var;

        var = str.indexOf('/', index1);
        if (var >= index1)
            if (result == -1 || var < result)
                result = var;

        return result;
    }

    public static boolean belongsToSign(String str, int pos) {
        if (pos >= 0)//do if cursor is not at the starting
        {
            if (str.charAt(pos) == '+'
                    || str.charAt(pos) == '-'
                    || str.charAt(pos) == '*'
                    || str.charAt(pos) == '/') {
                return true;
            } else {
                return false;
            }
        } else
            return false;//default is false
    }

}
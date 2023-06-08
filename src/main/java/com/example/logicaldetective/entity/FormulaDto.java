package com.example.logicaldetective.entity;

import com.example.logicaldetective.logical.Operator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SortNatural;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@NoArgsConstructor
@AllArgsConstructor
public class FormulaDto {
    private static Map<String, Operator> LOperators = Map.of(
            "!", new Operator("!", "НЕ*", "!*"),
            "&", new Operator("&", "* И *", "*&*"),
            "|", new Operator("|", "* ИЛИ *", "*|*"),
            "=>", new Operator("=>", "ЕСЛИ *, ТО *", "*=>*"),
            "<=>", new Operator("<=>", "* ТОГДА И ТОЛЬКО ТОГДА, КОГДА *", "*<=>*")
    );
    private Long id;
    private String description;
    @JsonManagedReference
    @SortNatural
    private List<LiteralDto> literals;
    private List<String> operators;
    @JsonIgnore
    @JsonBackReference
    private PlotDto plot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LiteralDto> getLiterals() {
        return literals;
    }

    public void setLiterals(List<LiteralDto> literals) {
        this.literals = literals;
    }

    public List<String> getOperators() {
        return operators;
    }

    public void setOperators(List<String> operators) {
        this.operators = operators;
    }

    private boolean isAlpha(String s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
    /*
        literals: [.getName()=A, .getName()=B, .getName()=C]
        operators: ['=>','&','*','*','*']

        return: "(A&B)=>C"
     */
    public String getLogform() {
        Stack<String> s = new Stack<String>();
        int k = 0;
        for (String key : this.operators) {
            if (this.LOperators.containsKey(key)) {
                s.push(key);
            } else {
                s.push(this.literals.get(k++).getName());
            }
        }
        /*
            s = ["C", "B", "A", "&", "=>"]
         */
        Stack<String> l = new Stack<String>();
        while (s.size() != 0) {
            if (s.peek().equals("!")) {
                Operator operator = this.LOperators.get(s.pop());
                if ((this.isAlpha(l.peek())) | (l.peek().charAt(0) == '(')) {
                    l.push(operator.toString(l.pop()));
                }
                else {
                    l.push(operator.toString("(" + l.pop() + ")"));
                }

            } else if (this.LOperators.containsKey(s.peek())) {
                Operator operator = this.LOperators.get(s.pop());
                if ((s.size() == 0) && (l.size() == 2)) {
                    l.push(operator.toString(l.pop(), l.pop()));
                } else {
                    l.push("(" + operator.toString(l.pop(), l.pop()) + ")");
                }
            } else {
                l.push(s.pop());
            }
        }
        return l.pop();
    }
    /*
        literals: [.getName()=A, .getName()=B, .getName()=C]
        operators: ['=>','&','*','*','*']

        (A&B)=>C

        A - яблоко красное
        B - яблоко ароматное
        С - яблоко вкусное

        return: "ЕСЛИ(яблоко красное И яблоко ароматное), ТО(яблоко вкусное)"
     */

    public String getDesform() {
        Stack<String> s = new Stack<String>();
        int k = 0;
        for (String key : this.operators) {
            if (this.LOperators.containsKey(key)) {
                s.push(key);
            } else {
                s.push(this.literals.get(k++).getDescription());
            }
        }
        Stack<String> l = new Stack<String>();
        while (s.size() != 0) {
            String key = s.pop();
            if (key.equals("!")) {
                Operator operator = this.LOperators.get(key);
                if (l.peek().charAt(0) == '(') {
                    l.push(operator.getDescription(l.pop()));
                }
                else {
                    l.push(operator.getDescription("(" + l.pop() + ")") );
                }
            } else if (this.LOperators.containsKey(key)) {
                Operator operator = this.LOperators.get(key);
                if ((s.size() == 0) & (l.size() == 2)) {
                    l.push(operator.getDescription( l.pop() ,  l.pop() ));
                } else {
                    l.push("(" + operator.getDescription( l.pop() ,  l.pop() ) + ")");
                }
            } else {
                l.push(key);
            }
        }
        return l.pop();
    }
}

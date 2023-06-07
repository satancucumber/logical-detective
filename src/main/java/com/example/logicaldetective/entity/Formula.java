package com.example.logicaldetective.entity;

import com.example.logicaldetective.logical.Operator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(name = "formula_entity-graph", attributeNodes = @NamedAttributeNode("plot"))
@Table(name = "formula")
public class Formula {

    private static Map<String, Operator> LOperators = Map.of(
            "!", new Operator("!", "НЕ*", "!*"),
            "&", new Operator("&", "* И *", "*&*"),
            "|", new Operator("|", "* ИЛИ *", "*|*"),
            "=>", new Operator("=>", "ЕСЛИ *, ТО *", "*=>*"),
            "<=>", new Operator("<=>", "* ТОГДА И ТОЛЬКО ТОГДА, КОГДА *", "*<=>*")
    );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "description")
    private String description;
    @ManyToMany
    @JoinTable (name="formula_literal",
            joinColumns=@JoinColumn (name="formula_id"),
            inverseJoinColumns=@JoinColumn(name="literal_id"))
    private List<Literal> literals;
    @Column(name = "operators")
    private List<String> operators;
    @JsonIgnore
    @JsonBackReference
    @ManyToOne(optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="plot_id", nullable = false)
    private Plot plot;

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

    public List<Literal> getLiterals() {
        return literals;
    }

    public void setLiterals(List<Literal> literals) {
        this.literals = literals;
    }

    public List<String> getOperators() {
        return operators;
    }

    public void setOperators(List<String> operators) {
        this.operators = operators;
    }

    public Plot getPlot() {
        return plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
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
    public static boolean isDescription(String s) {
        if (s == null) {
            return false;
        }
        List<String> operators = Arrays.asList("НЕ", "ИЛИ", "И", "ЕСЛИ");
        for (String operator : operators)
        {
            if (s.contains(operator)) {
                return false;
            }
        }
        return true;
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

    /*
        (A&B)|C
        operators: [(, *, &, *, ), |, *]

        operators: [ '*', '=>', '(', '(', '!', '*', '<=>', '*', ')', '&', '!', '(', '*', '|', '(', '*', '&', '*', ')', ')', ')' ]
        tree:
                                           =>
                                       *        &
                                            <=>     !
                                           !   *    |
                                           *      *   &
                                                     * *

        operators: [ =>, *, &, <=>, !, *, *, !, |, &, *, * ]
     */
    public List<String> toPolish() {
        List<String> l = new ArrayList<String>();
        Stack<String> r = new Stack<String>();
        for (String operator : this.operators) {
            r.push(operator);
        }
        Stack<String> s = new Stack<String>();
        String item;
        while (r.size() != 0) {
            item = r.pop();
            if (item.equals("(")) {
                String operator = s.pop();
                while (!operator.equals(")")) {
                    l.add(operator);
                    operator = s.pop();
                }
            } else if (item.equals("*") | item.equals("!")) {
                l.add(item);
            } else {
                s.push(item);
            }
        }
        while (s.size() != 0) {
            l.add(s.pop());
        }
        Collections.reverse(l);  // Если убрать получится обратная польская запись
        return l;
    }
    public void negative(){
        Stack<String> r = new Stack<>();
        List<String> l = new ArrayList<>();
        List<String> s;

        s = this.operators;
        Collections.reverse(s);
        for(String operator: s){
            r.push(operator);
        }

        r.push("!");
        Collections.reverse(r);

        while (r.size() != 0){
            l.add(r.pop());
        }
        this.operators = l;
        deMorgan();

    }
    public void simplificationCF() {
        List<String> l;
        Stack<String> p = new Stack<>();
        Stack<String> r = new Stack<>();
        String item;

        l = this.operators;
//        Collections.reverse(l); //Реверсируем, что записать нормально в стек

        for (String operator: l) { // [=>, &, *, <=>, *, !, *, *]
            p.push(operator);
        }

        while (p.size() != 0){
            item = p.pop();
            switch (item) {
                case ("=>") -> simImplication();
                case ("<=>") -> simEquivalence();
            }
        }
        deMorgan();

    }

    // Избавление от импликаций
    public void simImplication(){
        Stack<List<String>> r = new Stack<>();
        Stack<String> p = new Stack<>();

        List<String> l = this.operators;

        for (String operator: l) { // [=>, &, *, <=>, *, !, *, *]
            p.push(operator);
        }

        while (p.size() != 0) {
            switch (p.peek()) {
                case ("*") -> {
                    r.push(List.of(p.pop()));
                }
                case ("!") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("&"), ("|"), ("<=>") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("=>") -> {
                    p.pop();
                    List<String> list = new ArrayList<>(List.of("|"));
                    if (r.peek().get(r.peek().size()-1).equals("!")){
                        List<String> a = r.pop();
                        a.remove(0);
                        list.addAll(a);
                    }else {
                        list.add("!");
                        list.addAll(r.pop());
                    }

                    list.addAll(r.pop());
                    r.push(list);
                }
            }
        }

        Collections.reverse(r);

        this.operators = r.pop();

        this.setDescription("Избавимся от импликаций: ");
    }

    public void simEquivalence(){
        Stack<List<String>> r = new Stack<>();
        Stack<String> p = new Stack<>();

        List<String> l = this.operators;

        for (String operator: l) { // [=>, &, *, <=>, *, !, *, *]
            p.push(operator);
        }

        while (p.size() != 0) {
            switch (p.peek()) {
                case ("*") -> {
                    r.push(List.of(p.pop()));
                }
                case ("!") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("&"), ("|"), ("=>") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("<=>") -> {
                    p.pop();
                    List<String> list = new ArrayList<>(List.of("|"));
                    List<String> a = r.pop();
                    List<String> b = r.pop();
                    list.add("&");
                    list.addAll(a);
                    list.addAll(b);
                    list.add("&");
                    simDoubleNegativeExtra(list, a, b);

                    r.push(list);
                }
            }
        }

        Collections.reverse(r);
        this.operators = r.pop();
    }

    // Преобразование по законам де Моргана
    public void deMorgan(){
        Stack<List<String>> r = new Stack<List<String>>();
        Stack<String> p = new Stack<String>();

        List<String> l = this.operators;

        for (String operator: l) { // [=>, &, *, <=>, *, !, *, *]
            p.push(operator);
        }
        while (p.size() != 0) {
            switch (p.peek()) {
                case ("*") -> {
                    r.push(List.of(p.pop()));
                }
                case ("!") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("&"), ("|") -> {
                    List<String> list = new ArrayList<>();
                    String a = p.pop();
                    if (p.size() != 0) {
                        if (Objects.equals(p.peek(), "!")) {
                            p.pop();
                            switch (a) {
                                case ("&") -> {
                                    list.add("|");
                                    simDoubleNegative(r, list);
                                }
                                case ("|") -> {
                                    list.add("&");
                                    simDoubleNegative(r, list);
                                }
                            }
                        } else {
                            list.add(a);
                            list.addAll(r.pop());
                            list.addAll(r.pop());
                        }
                    } else {
                        list.add(a);
                        list.addAll(r.pop());
                        list.addAll(r.pop());
                    }
                    r.push(list);
                }
            }
        }
        Collections.reverse(r);
        this.operators = r.pop();
        while (valDeMorgan()) deMorgan();

        this.setDescription("Внесём отрицания внутрь скобок по законам де Мограна: ");
    }

    private void simDoubleNegative(Stack<List<String>> r, List<String> list) {
        List<String> d = r.pop();
        List<String> b = r.pop();
        simDoubleNegativeExtra(list, d, b);
    }

    private void simDoubleNegativeExtra(List<String> list, List<String> d, List<String> b) {
        if (Objects.equals(d.get(0), "!")){
            d.remove(0);
            list.addAll(d);
        }else {
            list.add("!");
            list.addAll(d);
        }
        if (Objects.equals(b.get(0), "!")){
            b.remove(0);
            list.addAll(b);
        }else {
            list.add("!");
            list.addAll(b);
        }
    }

    private Boolean valDeMorgan() {
        Stack<String> p = new Stack<>();
        List<String> l = this.operators;
        Collections.reverse(l);
        for (String operator: l) { // [=>, &, *, <=>, *, !, *, *]
            p.push(operator);
        }
        Collections.reverse(l);

        while (p.size() != 0){
            if ("!".equals(p.peek())) {
                p.pop();
                if ((Objects.equals(p.peek(), "&")) | (Objects.equals(p.peek(), "|"))) return true;
            } else {
                p.pop();
            }
        }

        return false;
    }

    // Преобразование по свойству дистрибутивности
    public void simDistributivity(){
        Stack<List<String>> r = new Stack<>();
        Stack<String> p = new Stack<>();
        List<Literal> lit = new ArrayList<>(this.literals);
        List<String> l = this.operators;
        int count = 0;

        for (String operator: l) {
            p.push(operator);
        }

        while (p.size() != 0) {
            switch (p.peek()) {
                case ("*") -> {
                    r.push(List.of(p.pop()));
                    count++;
                }
                case ("!") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("&") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("|")  -> {
                    p.pop();
                    List<String> list = new ArrayList<>();
                    List<String> a = r.pop();
                    List<String> b = r.pop();
                    if (Objects.equals(a.get(0), "&")){
                        int count2 = 0, count3 = 0, count4 = 0;
                        Stack<List<String>> k = distSeparate(list, a);

                        for (String i: b) if (Objects.equals(i, "*")) count3++;
                        for (String m: k.peek()) if (Objects.equals(m, "*")) count2++;

                        list.addAll(k.pop());

                        for (String m: k.peek()) if (Objects.equals(m, "*")) count4++;

                        list.addAll(b);
                        list.add("|");
                        list.addAll(k.pop());
                        list.addAll(b);
//                        System.out.println(count4);
//                        System.out.println(count3);
//                        System.out.println(count2);
//                        System.out.println(count);
//                        System.out.println(lit.size());
//                        for(Literal literal: lit) System.out.println(literal.getName());
                        for (int j = 0; j < count3; j++) {
                            lit.add(lit.size() - count + count2 + j, lit.get(lit.size() - count + count2 + count4 + j ));
                            count++;
                            count4++;
                        }

                    } else {
                        if (Objects.equals(b.get(0), "&")) {
                            int /*count2 = 0.*/ count3 = 0;
                            Stack<List<String>> k = distSeparate(list, b);

                            for (String i: a) if (Objects.equals(i, "*")) count3++;
//                            for (String m: k.peek()) if (Objects.equals(m, "*")) count2++;

                            list.addAll(a);
                            list.addAll(k.pop());
                            list.add("|");
                            list.addAll(a);
                            list.addAll(k.pop());

//                            System.out.println(count3);
//                            System.out.println(count2);
//                            System.out.println(count);
//                            System.out.println(lit.size());

                            for (int j = 0; j < count3; j++) {
                                lit.add(lit.size() - count + count3 + j + 1, lit.get(lit.size() - count + j));
                                count++;
                            }
//                            for(Literal literal: lit) System.out.println(literal.getName());
//                            System.out.println("fskfsfko");
                        } else {
                            list.add("|");
                            list.addAll(a);
                            list.addAll(b);
                        }
                    }
                    r.push(list);
                }
            }
        }
        Collections.reverse(r);
        this.operators = r.pop();
        this.literals = lit;
        while (valDistributivity()) simDistributivity();

        this.setDescription("По свойству дистрибутивности получим: ");
    }

    public Boolean valDistributivity() {
        Stack<List<String>> r = new Stack<>();
        Stack<String> p = new Stack<>();
        List<String> l = this.operators;

        for (String operator: l) {
            p.push(operator);
        }

        while (p.size() != 0) {
            switch (p.peek()) {
                case ("*") -> {
                    r.push(List.of(p.pop()));
                }
                case ("!") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("&") -> {
                    List<String> list = new ArrayList<>(List.of(p.pop()));
                    list.addAll(r.pop());
                    list.addAll(r.pop());
                    r.push(list);
                }
                case ("|")  -> {
                    p.pop();
                    List<String> list = new ArrayList<>();
                    List<String> a = r.pop();
                    List<String> b = r.pop();
                    if (Objects.equals(a.get(0), "&")){
                        return true;
                    } else {
                        if (Objects.equals(b.get(0), "&")) {
                            return true;
                        } else {
                            list.add("|");
                            list.addAll(a);
                            list.addAll(b);
                        }
                    }
                    r.push(list);
                }
            }
        }
        return false;
    }
    public Stack<List<String>> distSeparate(List<String> list, List<String> b) {
        list.add("&");
        list.add("|");
        Stack<List<String>> k = new Stack<>();
        Stack<String> g = new Stack<>();
        for (String operator1: b){
            g.push(operator1);
        }
        while (g.size() != 0){
            switch (g.peek()){
                case ("*") -> k.push(List.of(g.pop()));
                case ("!") -> {
                    List<String> list1 = new ArrayList<>(List.of(g.pop()));
                    list1.addAll(k.pop());
                    k.push(list1);
                }
                case ("|") -> {
                    List<String> list1 = new ArrayList<>(List.of(g.pop()));
                    list1.addAll(k.pop());
                    list1.addAll(k.pop());
                    k.push(list1);
                }
                case ("&") -> {
                    if (g.size() <= 1){
                        g.pop();
                    } else {
                        List<String> list1 = new ArrayList<>(List.of(g.pop()));
                        list1.addAll(k.pop());
                        list1.addAll(k.pop());
                        k.push(list1);
                    }
                }
            }
        }
        return k;
    }

    // Перевод формулы в КНФ
    public List<List<Literal>> toKNF() {
        Stack<List<Literal>> dis = new Stack<List<Literal>>();
        Stack<Literal> literals = new Stack<Literal>();

        for (Literal literal : this.getLiterals()) {
            literals.push(literal);
        }
        Stack<String> s = new Stack<String>();
        for (String key : this.getOperators()) {
            s.push(key);
        }
        while (s.size() != 0) {
            switch (s.peek()) {
                case ("*") -> {
                    s.pop();
                    Literal lit = literals.pop();
                    Literal l = new Literal();
                    l.setId(lit.getId());
                    l.setName(lit.getName());
                    l.setDescription(lit.getDescription());
                    l.setSuspect(lit.getSuspect());
                    l.setFormulas(lit.getFormulas());
                    l.setNegative(false);
                    dis.add(Arrays.asList(l));
                }
                case ("!") -> {
                    Literal lit = dis.pop().get(0);
                    Literal l = new Literal();
                    l.setId(lit.getId());
                    l.setName(lit.getName());
                    l.setDescription(lit.getDescription());
                    l.setSuspect(lit.getSuspect());
                    l.setFormulas(lit.getFormulas());
                    l.setNegative(true);
                    dis.push(Arrays.asList(l));
                    s.pop();
                }
                case ("|") -> {
                    List<Literal> list = new ArrayList<Literal>();
                    list.addAll(dis.pop());
                    list.addAll(dis.pop());
                    dis.push(list);
                    s.pop();
                }
                case ("&") -> {
                    s.pop();
                }
            }
        }
        List<List<Literal>> res = new ArrayList<List<Literal>>();
        while (dis.size() != 0) {
            res.add(dis.pop());
        }
        return res;
    }
}

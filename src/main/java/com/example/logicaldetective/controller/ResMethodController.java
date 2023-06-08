package com.example.logicaldetective.controller;

import com.example.logicaldetective.entity.Formula;
import com.example.logicaldetective.entity.Literal;
import com.example.logicaldetective.logical.ResMethod;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@CrossOrigin
@RequestMapping("api/v1")
public class ResMethodController {
    private static List<String> before_first = new ArrayList<String>(Arrays.asList("Знаем, что", "Известно, что", "Есть информация, что"));
    private static List<String> before_second = new ArrayList<String>(Arrays.asList("В то же время известно, что", "Также мы знаем, что", "Также нам известно, что"));
    private static List<String> before_result = new ArrayList<String>(Arrays.asList("Можно сделать вывод, что", "Очевидно, что", "Следовательно,","Можем сказать, что", "Делаем вывод, что", "Заключаем, что","Приходим к выводу, что"));
    private List<List<Formula>> transform;
    private List<List<List<String>>> strKnf;
    private  List<List<List<Literal>>> steps;
    private List<List<Literal>> selected;
    private List<List<Literal>> knf;

    @GetMapping("/resmethod/transform")
    public List<List<Formula>> listTransform() {
        return this.transform;
    }
    @GetMapping("/resmethod/str")
    public List<List<List<String>>> listStr() {
        return this.strKnf;
    }
    @GetMapping("/resmethod/steps")
    public List<List<String>> listSteps() {
        List<List<String>> textSteps = new ArrayList<List<String>>();
        if (this.steps.size() != 0) {
            return this.toText(this.steps);
        }
        return textSteps;
    }
    @GetMapping("resmethod/selected")
    public List<String> listSelected() {
        return this.selectedToText(this.selected);
    }
    @PostMapping("/resmethod")
    public List<List<Formula>> create(@RequestBody List<Formula> formulas) {
        List<List<Formula>> resFormulas = new ArrayList<List<Formula>>();
        List<List<List<String>>> resStrKNF = new ArrayList<List<List<String>>>();
        List<List<Literal>> resKnf = new ArrayList<List<Literal>>();
        for (Formula formula : formulas) {
            List<Formula> list = this.transformation(formula);
            resFormulas.add(list);

            List<List<Literal>> knf = list.get(list.size() - 1).toKNF();

            resKnf.addAll(knf);

            List<List<String>> strknf = new ArrayList<List<String>>();

            for (List<Literal> literals : knf) {
                List<String> kon = new ArrayList<String>();
                for (Literal literal : literals) {
                    if (literal.getNegative()) {
                        kon.add("НЕ" + "(" + literal.getDescription() + ")");
                    }
                    else {
                        kon.add(literal.getDescription());
                    }
                }
                strknf.add(kon);
            }
            resStrKNF.add(strknf);
        }
        this.transform = resFormulas;
        this.knf = resKnf;
        this.strKnf = resStrKNF;

        this.makeResolution();

        return resFormulas;
    }

    public void makeResolution() {
        ResMethod resMethod = new ResMethod();
        List<List<List<Literal>>> steps = new ArrayList<List<List<Literal>>>();
        List<List<Literal>> selected;
        if (this.valError()) {
            resMethod.setRes(this.knf);
            resMethod.makeRes(0);
            this.steps = resMethod.getSolution();
            this.selected = resMethod.getCondition();
        }
        else {
            this.steps = steps;
        }
    }

    public List<String> selectedToText(List<List<Literal>> selected) {
        List<String> text = new ArrayList<String>();
        for (List<Literal> literals : selected) {
            String des = "";
                if (literals.size() == 1) {
                    if (literals.get(0).getNegative()) {
                        des += "Не " + Character.toLowerCase(literals.get(0).getDescription().charAt(0)) + literals.get(0).getDescription().substring(1) + ".";
                    }
                    else {
                        des += literals.get(0).getDescription() + ".";
                    }
                }
                else {
                    for (Literal lit : literals) {
                        String str = Character.toLowerCase(lit.getDescription().charAt(0)) + lit.getDescription().substring(1);
                        if (literals.indexOf(lit) == 0) {
                            if (lit.getNegative()) des += "Если " + str + ", то";
                            else des += "Если не " + str + ", то";
                        }
                        else {
                            if (lit.getNegative()) des += (" не " + str);
                            else des += (" " + str);

                            if (literals.indexOf(lit) != literals.size()-1) des += " или";
                            else des += ".";
                        }
                    }
                }
            text.add(des);
        }
        return text;
    }

    public List<List<String>> toText(List<List<List<Literal>>> resResult) {
        List<List<String>> text = new ArrayList<List<String>>();
        for (List<List<Literal>> steps : resResult) {
            String first = this.before_first.get(ThreadLocalRandom.current().nextInt(0, this.before_first.size()));
            String second = this.before_second.get(ThreadLocalRandom.current().nextInt(0, this.before_second.size()));
            String result = this.before_result.get(ThreadLocalRandom.current().nextInt(0, this.before_result.size()));
            List<String> part = new ArrayList<String>(Arrays.asList(first, second, result));
            int i = 0;
            for (List<Literal> step : steps) {
                if (step.size() == 1) {
                    if (step.get(0).getNegative()) {
                        part.set(i, part.get(i)+ " не " + Character.toLowerCase(step.get(0).getDescription().charAt(0)) + step.get(0).getDescription().substring(1) + ".");
                    }
                    else {
                        part.set(i, part.get(i)+ " " + Character.toLowerCase(step.get(0).getDescription().charAt(0)) + step.get(0).getDescription().substring(1) + ".");
                    }
                }
                else {
                    for (Literal lit : step) {
                        String str = Character.toLowerCase(lit.getDescription().charAt(0)) + lit.getDescription().substring(1);
                        if (step.indexOf(lit) == 0) {
                            if (lit.getNegative()) part.set(i, part.get(i) + (" если " + str + ", то"));
                            else part.set(i, part.get(i) + (" если не " + str + ", то"));
                        }
                        else {
                            if (lit.getNegative()) part.set(i, part.get(i) + (" не " + str));
                            else part.set(i, part.get(i) + (" " + str));

                            if (step.indexOf(lit) != step.size()-1) part.set(i, part.get(i) + " или");
                            else part.set(i, part.get(i) + ".");
                        }
                    }
                }
                i++;
            }
            text.add(part);
        }
        return text;
    }

    public List<Formula> transformation(Formula formula) {
        List<Formula> list = new ArrayList<Formula>();
        Formula newFormula = new Formula();
        newFormula.setId(formula.getId());
        newFormula.setDescription(formula.getDescription());
        newFormula.setOperators(formula.getOperators());
        newFormula.setLiterals(formula.getLiterals());
        newFormula.setPlot(formula.getPlot());
        newFormula.simImplication();
        if (!formula.getOperators().equals(newFormula.getOperators())) {
            Formula f = new Formula();
            f.setId(newFormula.getId());
            f.setDescription(newFormula.getDescription());
            f.setOperators(newFormula.getOperators());
            f.setLiterals(newFormula.getLiterals());
            f.setPlot(newFormula.getPlot());
            list.add(f);
        }
        newFormula.deMorgan();
        if (list.size() == 0) {
            if (!formula.getOperators().equals(newFormula.getOperators())) {
                Formula f = new Formula();
                f.setId(newFormula.getId());
                f.setDescription(newFormula.getDescription());
                f.setOperators(newFormula.getOperators());
                f.setLiterals(newFormula.getLiterals());
                f.setPlot(newFormula.getPlot());
                list.add(f);
            }
        }
        else {
            if (!list.get(list.size()-1).getOperators().equals(newFormula.getOperators())) {
                Formula f = new Formula();
                f.setId(newFormula.getId());
                f.setDescription(newFormula.getDescription());
                f.setOperators(newFormula.getOperators());
                f.setLiterals(newFormula.getLiterals());
                f.setPlot(newFormula.getPlot());
                list.add(f);
            }
        }
        newFormula.simDistributivity();
        if (list.size() == 0) {
            if (!formula.getOperators().equals(newFormula.getOperators())) {
                Formula f = new Formula();
                f.setId(newFormula.getId());
                f.setDescription(newFormula.getDescription());
                f.setOperators(newFormula.getOperators());
                f.setLiterals(newFormula.getLiterals());
                f.setPlot(newFormula.getPlot());
                list.add(f);
            }
        }
        else {
            if (!list.get(list.size()-1).getOperators().equals(newFormula.getOperators())) {
                Formula f = new Formula();
                f.setId(newFormula.getId());
                f.setDescription(newFormula.getDescription());
                f.setOperators(newFormula.getOperators());
                f.setLiterals(newFormula.getLiterals());
                f.setPlot(newFormula.getPlot());
                list.add(f);
            }
        }
        if (list.size() == 0) {
            formula.setDescription("Оставляем без изменений: ");
            list.add(formula);
        }
        return list;
    }
    public List<Literal> findSuspect() {
        List<Literal> suspect = new ArrayList<Literal>();

        boolean flag = true;
        List<String> names = new ArrayList<>();
        for (List<Literal> i: this.knf) {
            for (Literal lit: i) {
                if (lit.getSuspect()) {
                    for (String name: names){
                        if (Objects.equals(lit.getName(), name)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        lit.setNegative(true);
                        suspect.add(lit);
                        names.add(lit.getName());
                    }
                    flag = true;
                }
            }
        }
        return suspect;
    }
    public boolean valError() {
        int count = 0;
        boolean flag = true;
        List<String> names = new ArrayList<>();
        for (List<Literal> i: this.knf) {
            for (Literal j: i) {
                if (j.getSuspect()) {
                    for (String n: names){
                        if (Objects.equals(j.getName(), n)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        count++;
                        names.add(j.getName());
                    }
                    flag = true;
                }
            }
        }
        if (count != 1) return false;
        return true;
    }
}

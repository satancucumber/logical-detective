package com.example.logicaldetective.logical;



import com.example.logicaldetective.entity.Literal;

import java.util.*;

public class ResMethod {
    private List<List<Literal>> res;
    private Literal suspect;
    private HashMap<List<Literal>, List<List<Literal>>> path;
    private List<List<List<Literal>>> solution;
    private List<List<Literal>> condition;
    private HashMap<List<List<Literal>>, List<List<Literal>>> moving;

    public Literal getSuspect() {
        return suspect;
    }

    public void setSuspect(Literal suspect) {
        this.suspect = suspect;
    }

    public List<List<Literal>> getRes() {
        return res;
    }

    public void setRes(List<List<Literal>> res) {
        this.res = res;
    }

    public void setPath(HashMap<List<Literal>, List<List<Literal>>> path) {this.path = path;}

    public HashMap<List<Literal>, List<List<Literal>>> getPath() {return path;}

    public void setSolution(List<List<List<Literal>>> solution) {this.solution = solution;}

    public List<List<List<Literal>>> getSolution() {return this.solution;}

    public void setCondition(List<List<Literal>> condition) {this.condition = condition;}

    public List<List<Literal>> getCondition() {return this.condition;}

    public HashMap<List<List<Literal>>, List<List<Literal>>> getMoving() {
        return moving;
    }

    public void setMoving(HashMap<List<List<Literal>>, List<List<Literal>>> moving) {
        this.moving = moving;
    }

    public void makeRes(int k) {
        this.condition = new ArrayList<>();
        this.solution = new ArrayList<>();
        List<List<Literal>> resNew = new ArrayList<>(new ArrayList<>());
        List<Literal> begin_literal1 = new ArrayList<>();
        List<Literal> begin_literal2 = new ArrayList<>();
        if (k == 0) {
            for (List<Literal> i : this.res) {
                for (Literal j : i) {
                    if (j.getSuspect()) {
                        this.suspect = j;
                        break;
                    }
                }
            }
            this.moving = new HashMap<>();
            Literal literal = new Literal();
            literal.setNegative(true);
            literal.setSuspect(true);
            literal.setName(this.suspect.getName());
            literal.setDescription(this.suspect.getDescription());
            literal.setId(this.suspect.getId());
            literal.setFormulas(this.suspect.getFormulas());
            this.res.add(List.of(literal));
            this.path = new HashMap<>();
            for (List<Literal> w: this.getRes()) {
                this.path.put(w, null);
            }
        }
        for (int i = 0; i < this.getRes().size() - 1; i++) {
            for (int j = i + 1;j < this.getRes().size(); j++) {
                List<Literal> a = new ArrayList<>(new ArrayList<>(this.getRes().get(i)));
                List<Literal> b = new ArrayList<>(new ArrayList<>(this.getRes().get(j)));
                List<Integer> flag_dublicate = new ArrayList<>();
                List<Integer> remove_a = new ArrayList<>();
                List<Integer> remove_b = new ArrayList<>();
                boolean resolution = false;
                int resa_id = -1;
                int resb_id = -1;
                for (int m = 0; m < a.size(); m++) {
                    for (int n = 0; n < b.size(); n++) {
                        if (Objects.equals(a.get(m).getName(), b.get(n).getName())) {
                            if (Objects.equals(a.get(m).getNegative(), b.get(n).getNegative())) {
                                boolean flag_dublicate_bool = false;
                                for (Integer l : flag_dublicate) {
                                    if (Objects.equals(a.get(l).getName(), a.get(m).getName())) {
                                        flag_dublicate_bool = true;
                                        break;
                                    }
                                }
                                remove_b.add(n);
                                if (!flag_dublicate_bool) {
                                    flag_dublicate.add(m);
                                } else {
                                    remove_a.add(m);
                                }
                            } else {
                                List<Integer> quadruplicates = new ArrayList<>(new ArrayList<>(flag_dublicate));
                                boolean b1 = false;
                                resa_id = m;
                                resb_id = n;
                                remove_a.add(m);
                                remove_b.add(n);
                                resolution = true;
                                for (int p = n + 1; p < b.size(); p++) {
                                    if (Objects.equals(a.get(m).getName(), b.get(p).getName())) {
                                        if (Objects.equals(a.get(m).getNegative(), b.get(p).getNegative())) {
                                            for (Integer l : quadruplicates) {
                                                if (Objects.equals(a.get(l).getName(), a.get(p).getName())) {
                                                    b1 = true;
                                                    break;
                                                }
                                            }
                                            remove_b.add(p);
                                            if (!b1) {
                                                quadruplicates.add(m);
                                            } else {
                                                remove_a.add(m);
                                            }
                                            b1 = false;

                                        }
                                    }
                                }
                                if (m+1 <= a.size()) {
                                    for (int u = m + 1; u < a.size(); u++) {
                                        for (int v = 0; v < b.size(); v++) {
                                            if (Objects.equals(a.get(u).getName(), b.get(v).getName())) {
                                                if (Objects.equals(a.get(u).getNegative(), b.get(v).getNegative())) {
                                                    for (Integer l : quadruplicates) {
                                                        if (Objects.equals(a.get(l).getName(), a.get(u).getName())) {
                                                            b1 = true;
                                                            break;
                                                        }
                                                    }
                                                    remove_b.add(v);
                                                    if (!b1) {
                                                        quadruplicates.add(u);
                                                    } else {
                                                        remove_a.add(u);
                                                    }
                                                    b1 = false;

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if ((resolution) & (resa_id != -1) & (resb_id != -1)) {

                    List<Literal> c = new ArrayList<>(collecting(a, remove_a));
                    c.addAll(collecting(b, remove_b));

                    this.moving.put(Arrays.asList(a, b), Arrays.asList(moving_literal(a, resa_id), moving_literal(b, resb_id)));
                    if (!valRepeatRes(resNew, c)) {
                        this.path.put(c, Arrays.asList(a, b));
                        resNew.add(c);
                        if (c.size() == 0) {
                            begin_literal1.addAll(a);
                            begin_literal2.addAll(b);
                        }
                    }
                }
            }
        }
        this.res.addAll(resNew);



        if (!valMakeRes()) {
            if (k < 10) {
                k++;
                makeRes(k);
            } else {
                this.res.clear();
                this.solution.clear();
                this.condition.clear();
            }
        } else {
            List<List<List<Literal>>> result = new ArrayList<>();
            List<List<Literal>> condition = new ArrayList<>();
            Stack<List<Literal>> s = new Stack<>();
            s.push(begin_literal1);
            s.push(begin_literal2);
            result.add(Arrays.asList(begin_literal1, begin_literal2));
            while (s.size() != 0) {
                if (!Objects.equals(this.path.get(s.peek()), null)) {
                    List<Literal> a = s.pop();
                    List<List<Literal>> iterator = new ArrayList<>(this.moving.get(this.path.get(a)));
                    iterator.add(a);
                    s.push(this.path.get(a).get(0));
                    s.push(this.path.get(a).get(1));
                    result.add(iterator);
                } else {
                    condition.add(s.peek());
                    s.pop();
                }
            }
            Collections.reverse(result);
            this.solution = result;
            this.condition = condition;
        }
    }

    private List<Literal> moving_literal(List<Literal> list, int index) {
        List<Literal> result = new ArrayList<>(list);
        Literal literal = result.remove(index);
        result.add(0, literal);
        return result;
    }

    private List<Literal> collecting(List<Literal> list, List<Integer> remove) {
        List<Literal> result = new ArrayList<>();
        boolean flag_remove = false;
        for (int iterator = 0; iterator < list.size(); iterator++) {
            for (int iterator1 : remove){
                if (iterator == iterator1) {
                    flag_remove = true;
                    break;
                }
            }
            if (!flag_remove) {
                result.add(list.get(iterator));
            }
            flag_remove = false;
        }
        return result;
    }

    private boolean valMakeRes() {
        for (List<Literal> i: this.res) {
            if (i.size() == 0) return true;
        }
        return false;
    }

    private boolean valRepeatRes(List<List<Literal>> newRes, List<Literal> list) {
        for (List<Literal> i: newRes) {
            if (Objects.equals(i, list)) {
                return true;
            }
        }

        for (List<Literal> j: this.getRes()) {
            if (Objects.equals(j, list)) {
                return true;
            }
        }

        return false;
    }


}

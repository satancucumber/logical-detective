<template>
    <div>
        <h5>Формулы</h5>
        <p></p>
        <vue-good-table
            :columns="columns"
            :rows="formulas"
            class="table"
        >
            <template slot="table-row" slot-scope="props">
                  <span v-if="props.column.field == 'before'">
                    <b-button variant="primary" @click="edit(props.row.id)">Изменить</b-button>
                    <b-button variant="primary" @click="del_row(props.row.id)">Удалить</b-button>
                  </span>
                  <span v-else>
                    {{props.formattedRow[props.column.field]}}
                  </span>
            </template>
        </vue-good-table>
        <p></p>
        <b-button variant="primary" @click="add">Добавить</b-button>
        <p></p>
        <div id="form2">
            <b-row>
                <b-col sm="3">
                    <b-form-input readonly
                        v-model="logform"
                    ></b-form-input>
                    <p></p>
                </b-col>
                <b-col>
                    <b-button @click="del_symbol"> X </b-button>
                </b-col>
            </b-row>
            <b-row>
                <b-col sm="1">
                    <p></p>
                    <b-button-group v-for="operator in const_operators" v-bind:key="operator">
                        <b-button @click="add_operator(operator)"> {{ operator }} </b-button>
                    </b-button-group>
                </b-col>
                <b-col sm="2">
                    <p></p>
                    <b-button-group v-for="literal in const_literals" v-bind:key="literal.id">
                        <b-button @click="add_literal(literal)"> {{ literal.name }} </b-button>
                    </b-button-group>
                </b-col>
            </b-row>
            <p></p>
            <b-button variant="primary" @click="save"> Сохранить </b-button>
            <p></p>
        </div>
    </div>
</template>
<script>
    export default {
        props: ['mliterals', 'mformulas'],
        data() {
            return {
                mid: 1,
                id: null,
                literals: [],
                operators: [],
                formulas: [],
                logform: '',
                last_symbol: '',
                const_literals: [],
                const_operators: [
                    '(', ')',
                    '!',
                    '&', '|',
                    '=>'
                ],
                columns: [
                    {
                      label: 'Before',
                      field: 'before'
                    },
                    {
                      label: 'Формула',
                      field: 'logform'
                    }
                ],
            }
        },
        created() {
            this.const_literals = this.mliterals;
            this.formulas = this.mformulas;
        },
        methods: {
            add_operator(operator) {
                this.logform = this.logform + operator;
                this.operators.push(operator);
                this.last_symbol = operator;
            },
            add_literal(literal) {
                this.logform = this.logform + literal.name;
                this.literals.push(literal);
                this.operators.push('*');
                this.last_symbol = literal.name;
            },
            update_last_symbol() {
                if(this.logform != '') {
                    if(this.operators[this.operators.length - 1] == '*') {
                        this.last_symbol = this.literals[this.literals.length - 1].name;
                    } else {
                        this.last_symbol = this.operators[this.operators.length - 1];
                    }
                } else {
                    this.last_symbol = '';
                }
            },
            del_symbol() {
                if(this.last_symbol != '') {
                    var index = this.logform.lastIndexOf(this.last_symbol);
                    if(this.last_symbol == this.operators[this.operators.length - 1]) {
                        this.operators.pop();
                    } else {
                        this.logform = this.logform.substring(0, index);
                        this.literals.pop();
                        this.operators.pop();
                    }
                    this.logform = this.logform.substring(0, index);
                    this.update_last_symbol()
                }

            },
            add() {
                document.getElementById("form2").style.display = "block";
            },
            getIndex(list, id) {
                for (var i = 0; i < list.length; i++ ) {
                    if (list[i].id == id) {
                        return i;
                    }
                }
            },
            clean() {
                document.getElementById("form2").style.display = "none";
                this.id = null;
                this.logform = '';
                this.operators = [];
                this.literals = [];
            },
            save() {
                var formula = {
                    id: this.id,
                    description: '',
                    operators: this.operators,
                    literals: this.literals,
                  logform: this.logform
                };
                if (this.id) {
                    var index = this.getIndex(this.formulas, this.id);
                    this.formulas.splice(index, 1, formula);
                } else {
                    formula.id = this.mid++;
                    this.formulas.push(formula);
                }
                this.clean()
            },
            edit(id) {
                var index = this.getIndex(this.formulas, id);
                document.getElementById("form2").style.display = "block";
                this.id = this.formulas[index].id;
                this.logform = this.formulas[index].logform;
                this.operators = this.formulas[index].operators;
                this.literals = this.formulas[index].literals;
            },
            del_row(id) {
                var index = this.getIndex(this.formulas, id);
                this.formulas.splice(index, 1);
            }
        }
    };
</script>
<style scoped>
    #form2 {
        display: block
    }
</style>
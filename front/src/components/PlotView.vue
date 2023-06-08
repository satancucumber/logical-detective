<template>
    <div style="margin: 20px 50px;">
      <div v-show="!isResMethod">
        <h4> {{ plot.name }} </h4>
        <p></p>
        <span
            v-for="str in plot.text"
            v-bind:key="str"
        >
            <span
                v-show="isDescription(str)"
            >
                <span
                    style="background-color:#ddd;"
                    @mouseleave="desLeave(str)"

                    v-show="isHold(str)"
                > {{ str }}
                </span>
                <span
                    @mouseover="desOver(str)"
                    v-show="!isHold(str)"
                > {{ str }}
                </span>
            </span>
            <span
                v-show="!isDescription(str)"
            > {{ str }}
            </span>
        </span>
        <p></p>
        <b-row>
          <b-col style="border: solid dimgrey; padding: 15px 80px">
            <b-icon icon="file-earmark-text"></b-icon>
              Обозначения
          </b-col>
          <b-col style="border: solid dimgrey; padding: 15px 80px">
            <b-icon icon="book"></b-icon>
              Логические выражения
          </b-col>
          <b-col style="border: solid dimgrey; padding: 10px 80px">
            <b-icon icon="display"></b-icon>
            Окно доказательств
            <b-button @click="showRes" > Расследовать </b-button>
          </b-col>
        </b-row>
        <b-row>
            <b-col style="border: solid dimgrey">
                <div v-for="literal in literals" v-bind:key="literal.id">
                    <div style="border: solid lightgrey">
                        {{ literal.name }} -- {{ literal.description }}
                    </div>
                </div>
            </b-col>
            <b-col style="border: solid dimgrey">
                <div v-for="formula in plot.formulas" v-bind:key="formula.id" @click="toComputer(formula.id)">
                  <div style="border: solid lightgrey" @mouseleave="desLeave(formula.description)" @mouseover="desOver(formula.description)">
                    <span
                        style="background-color:#ddd;"
                        v-show="isHold(formula.description)"
                    > {{ formula.logform }}
                    </span>
                    <span
                        v-show="!isHold(formula.description)"
                    > {{ formula.logform }}
                    </span>
                  </div>
                </div>
            </b-col>
            <b-col style="border: solid dimgrey">
              <div v-for="formula in formulas" v-bind:key="formula.id" @click="toFormulas(formula.id)">
                <div style="border: solid lightgrey" @mouseleave="desLeave(formula.description)" @mouseover="desOver(formula.description)">
                    <span
                        style="background-color:#ddd;"
                        v-show="isHold(formula.description)"
                    > {{ formula.logform }}
                    </span>
                  <span
                      v-show="!isHold(formula.description)"
                  > {{ formula.logform }}
                    </span>
                </div>
              </div>
            </b-col>
        </b-row>
      </div>
      <res-method v-show="isResMethod" :key="keyResMethod" :mformulas="formulas" ></res-method>

      <b-button v-show="isResMethod" @click="back">Назад</b-button>
    </div>
</template>
<script>
    import ResMethod from "./ResMethodView.vue"
    export default {
        props: ['mplot'],
        components : {
          ResMethod
        },
        data() {
            return {
              isResMethod: false,
              keyResMethod: 0,
              formulas: [],
              descriptions: [],
              literals: [],
              plot: null
            }
        },
        created() {
          this.mplot.formulas.forEach(f=> {
            this.descriptions.push(Object.assign({}, {name: f.description, val: false}));
            f.literals.forEach(l => {
              var b = true;
              for (var i = 0; i < this.literals.length; i++){
                if (l.id == this.literals[i].id){
                  b = false;
                }
              }
              if (b == true) {
                this.literals.push(l)
              }
              })
            })
          this.plot = this.mplot;
        },
        methods: {
            isDescription(str) {
                var formulas = this.plot.formulas;
                for (var i = 0; i < formulas.length; i++ ) {
                    if (str == formulas[i].description) {
                        return true;
                    }
                }
                return false;
            },
            val_by_name(name) {
                for (var i = 0; i < this.descriptions.length; i++) {
                    if (this.descriptions[i].name == name) {
                      return this.descriptions[i].val
                    }
                }
            },
            getIndex(list, id) {
              for (var i = 0; i < list.length; i++ ) {
                if (list[i].id == id) {
                  return i;
                }
              }
            },
            desOver(str) {
              for (var i = 0; i < this.descriptions.length; i++) {
                if (this.descriptions[i].name == str) {
                  this.descriptions[i].val = true;
                }
              }
            },
            desLeave(str) {
              for (var i = 0; i < this.descriptions.length; i++) {
                if (this.descriptions[i].name == str) {
                  this.descriptions[i].val = false;
                }
              }
            },
            isHold(str) {
                if (this.val_by_name(str)) {
                    return true;
                }
                return false;
            },
            toComputer(id) {
                var index = this.getIndex(this.plot.formulas, id);
                this.formulas.push(this.plot.formulas[index]).then(
                    this.plot.formulas.splice(index, 1)
                );
            },
            toFormulas(id) {
              var index = this.getIndex(this.formulas, id);
              this.plot.formulas.push(this.formulas[index]).then(
                  this.formulas.splice(index, 1)
              );
            },
            showRes() {
              if (this.formulas.length != 0) {
                this.isResMethod = true;
                this.keyResMethod += 1;
              }

            },
            back() {
              this.isResMethod = false;
            }
        }

    }
</script>
<style scoped>

</style>
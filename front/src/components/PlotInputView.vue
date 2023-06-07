<template>
  <div>
    <main-menu></main-menu>
    <div style="margin: 20px 50px;">
      <h5 v-show="isName">Название</h5>
      <p></p>
      <b-form-input inline
                    v-show="isName"
                    v-model="name"
                    id="inline-form-input-name"
                    class="mb-2 mr-sm-2 mb-sm-0"
                    placeholder="Название сюжета"
      ></b-form-input>
      <p></p>
      <b-button v-show="isName" variant="primary" @click="isName = false; isLiterals = true">Далее</b-button>

      <literal-form v-show="isLiterals" id="literalform" :literals="literals"></literal-form>
      <b-button v-show="isLiterals" variant="primary" @click="isLiterals = false; isFormulas = true">Далее</b-button>
      <formula-form v-show="isFormulas" :mliterals="literals" :mformulas="formulas"></formula-form>
      <b-button v-show="isFormulas" variant="primary" @click="isFormulas = false; isText = true; textKey += 1;">Далее</b-button>

      <text-form v-show="isText" :mformulas="formulas" :mtext="text" :key="textKey"></text-form>
      <b-button v-show="isText" variant="primary" @click="savePlot">Сохранить и перейти к решению</b-button>

      <Plot v-show="isPlot" :mplot="plot" :key="plotKey"></Plot>
      <b-button v-show="isPlot" variant="primary" @click="createPlot">Опубликовать</b-button>
    </div>
  </div>
</template>
<script>
import MainMenu from "./MainView.vue"
import LiteralForm from "./LiteralFormView.vue"
import FormulaForm from "./FormulaFormView.vue"
import TextForm from "./TextFormView.vue"
import Plot from "@/components/PlotView.vue";
import {url} from "@/main";
export default {
  name: 'PlotInputView',
  components: {
    Plot,
    MainMenu,
    LiteralForm,
    FormulaForm,
    TextForm
  },
  data() {
    return {
      textKey: 0,
      plotKey: 0,
      isPlot: false,
      isName: true,
      isLiterals: false,
      isFormulas: false,
      isText: false,
      id: null,
      index: null,
      name: '',
      literals: [],
      formulas: [],
      text: [],
      plots: []
    }
  },
  methods: {
    getIndex(list, id) {
      for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
          return i;
        }
      }
    },
    getLiterals(formula) {
      var literals = [];
      formula.literals.forEach(l => {
        var literal = l;
        delete literal.id;
        literals.push(literal);
      })
      return literals
    },
    getFormulas() {
      var formulas = [];
      this.formulas.forEach(f => {
        var formula = f;
        delete formula.id;
        formula.literals = this.getLiterals(f);
      })
      return formulas;
    },
    createFormulas() {
      var formulas = this.getFormulas()
      this.$http.post(url + "/formulas/list", formulas).then(
          this.$http.get(url + "/formulas/" + this.plot.id, formulas)
      )
    },
    createPlot() {
      var plot = {
        name: this.name,
        text: this.text,
        formulas: this.getFormulas()
      };
      this.$http.post(url + "/plot", plot).then()
      )
    },
    savePlot() {
      var plot = {
        name: this.name,
        text: this.text,
        formulas: this.getFormulas()
      }
      this.plot = plot;
      this.isText = false;
      this.isPlot = true;
      this.plotKey += 1;
    },
  }
}
</script>
<style scoped>
#literalform {
  display: block
}
</style>
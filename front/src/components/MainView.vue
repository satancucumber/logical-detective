<template>
  <div>
    <b-navbar toggleable="lg" type="dark" variant="info">
      <b-navbar-brand>
        <b-icon icon="justify" v-b-toggle.sidebar-1></b-icon>
      </b-navbar-brand>
      <b-navbar-brand href="/" @click="isPlot = false">
        Логический детектив
      </b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

    </b-navbar>
    <b-sidebar id="sidebar-1" title="Сюжеты" shadow>
      <div class="px-3 py-2" v-for="plot in plots" v-bind:key="plot.id">
        <button type="button" class="btn btn-link" @click="showPlot(plot.id)"> {{ plot.name }} </button>
      </div>
    </b-sidebar>
    <div v-show="!isPlot" style="margin: 20px 500px;">
      <p></p>
      <h5> Добро пожаловать! </h5>
      <p>
        Вам будет представлена некоторая история, состоящая из высказываний. <br/>
        Высказывания записаны в виде математических выражений в окне выражений. <br/>
        При наведении мыши на высказывание или его выражение, подсвечиваются оба элемента пары.<br/>
        Окно доказательств - это те данные, которые вы предоставляете компьютерному детективу,<br/>
        который, используя данные выражения, сможет разгадать это дело. <br/>
        Чтобы просто переместить выражение в окно доказательств, кликните по нему. <br/>
        Для возврата выражения из окна доказательств, также необходиму кликнуть по нему. <br/>
        Когда закончите, нажмите кнопку "расследовать" и детектив проведет расследование. <br/>
      </p>
      <p>
        Для того, чтобы начать выберите сюжет в боковом меню.
      </p>
    </div>

    <Plot v-show="isPlot" :mplot="plot" :key="plotKey"></Plot>

  </div>
</template>

<script>
import Plot from "./PlotView.vue"
import {url} from "@/main";
export default {
  components: {
    Plot
  },
  name: 'MainView',
  data() {
    return{
      plotKey: 0,
      isPlot: false,
      plot: null,
      id: null,
      plots: []
    }
  },
  created() {
    this.getData();
  },
  methods: {
    getData() {
      this.$http.get(url + "/plots").then(response => {
        const plots = response && response.data ? response.data : []
        this.plots = plots.map(r => {
          return {
            id: r.id,
            name: r.name,
            text: r.text,
            formulas: r.formulas
          }
        })
      })
    },
    getIndex(list, id) {
      for (var i = 0; i < list.length; i++ ) {
        if (list[i].id == id) {
          return i;
        }
      }
    },
    showPlot(id) {
      var index = this.getIndex(this.plots, id);
      this.plot = this.plots[index];
      this.isPlot = true;
      this.plotKey += 1;
    },
  }
}
</script>
<style>

</style>